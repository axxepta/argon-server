package de.axxepta.dao.implementations;

import static jetbrains.exodus.env.StoreConfig.WITHOUT_DUPLICATES;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.core.Context;

import org.apache.log4j.Logger;
import org.basex.core.BaseXException;
import org.glassfish.jersey.server.ResourceConfig;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import de.axxepta.basex.RunDirectCommands;
import de.axxepta.dao.interfaces.IDocumentCacheDAO;
import de.axxepta.dao.interfaces.IDocumentDAO;
import jetbrains.exodus.ByteIterable;
import jetbrains.exodus.bindings.CompressedUnsignedLongArrayByteIterable;
import jetbrains.exodus.bindings.StringBinding;
import jetbrains.exodus.env.Cursor;
import jetbrains.exodus.env.Environment;
import jetbrains.exodus.env.Environments;
import jetbrains.exodus.env.Store;
import jetbrains.exodus.env.Transaction;
import jetbrains.exodus.env.TransactionalExecutable;

@Service(name = "DocumentDatabaseCacheDAO")
@Singleton
public class DocumentDBCacheDAOImpl implements IDocumentCacheDAO {

	private static final Logger LOG = Logger.getLogger(DocumentDBCacheDAOImpl.class);

	public static final String STORE_DOCUMENTS_PATH_DB = System.getProperty("user.dir") + File.separator + "shiro-res"
			+ File.separator + ".store-documents";

	private final Environment env;

	private final Store storeDocuments;

	private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

	@Context
	private ResourceConfig resourceConfig;

	private String commonDatabaseName;

	@Inject
	@Named("BaseXDao")
	private IDocumentDAO documentDAO;

	private RunDirectCommands runDirectCommands;

	private Runnable transferRunnable = new Runnable() {

		@Override
		public void run() {
			transferFiles();
		}
	};

	private void transferFiles() {
		List<String> fileNameList = getSavedFilesName();
		BufferedWriter out = null;
		for (String fileName : fileNameList) {
			String content = getContentFile(fileName);
			File temp = null;
			try {
				temp = File.createTempFile("tempFile", ".suffix");
				out = new BufferedWriter(new FileWriter(temp));
				if (content != null)
					out.write(content);
				else {
					temp.delete();
					LOG.error("Content of the file " + fileName + " is null or emty");
					continue;
				}

				saveInCommonDatabase(temp);

				temp.delete();

			} catch (IOException e) {
				LOG.error(e.getMessage());
				continue;
			}

			if (delete(fileName))
				LOG.info(fileName + " cannot be deleted from Xodus database");
		}
	}

	private void saveInCommonDatabase(File file) {
		documentDAO.uploadXMLDocument(file, false, commonDatabaseName);
	}

	public DocumentDBCacheDAOImpl() {
		env = Environments.newInstance(STORE_DOCUMENTS_PATH_DB);
		storeDocuments = env.computeInTransaction(txn -> env.openStore("Sessions", WITHOUT_DUPLICATES, txn));
		LOG.info("Create DAO object for documents caching");

		runDirectCommands = new RunDirectCommands();
	}

	@PostConstruct
	private void initTransfer() {
		scheduledExecutorService.scheduleAtFixedRate(transferRunnable, 20, 30, TimeUnit.MINUTES);

		commonDatabaseName = (String) resourceConfig.getProperty("common_dabase_name");

		if (commonDatabaseName == null || commonDatabaseName.isEmpty())
			commonDatabaseName = "common_database";

		if (!runDirectCommands.existDatabase(commonDatabaseName))
			try {
				runDirectCommands.createDatabase(commonDatabaseName, null);
			} catch (BaseXException e) {
				LOG.error(e.getMessage());
			}
	}

	@Override
	public List<String> getSavedFilesName() {
		List<String> listFileNames = new ArrayList<>();
		env.executeInTransaction(new TransactionalExecutable() {
			@Override
			public void execute(@NotNull final Transaction txn) {
				try (Cursor cursor = storeDocuments.openCursor(txn)) {
					while (cursor.getNext()) {
						final ByteIterable key = cursor.getKey();
						String fileName = StringBinding.entryToString(key);
						listFileNames.add(fileName);
					}
				}
			}
		});

		LOG.info("Saved " + listFileNames.size() + " files");
		return listFileNames;
	}

	@Override
	public String getContentFile(String fileName) {

		StringBuilder response = new StringBuilder();

		@NotNull
		final ByteIterable keyFileName = StringBinding.stringToEntry(fileName);
		env.executeInTransaction(new TransactionalExecutable() {
			@Override
			public void execute(@NotNull final Transaction txn) {
				final ByteIterable entry = storeDocuments.get(txn, keyFileName);

				if (entry == null) {
					LOG.error("entry for " + fileName + " not exist");
					response.append("");
				}

				byte[] array = CompressedUnsignedLongArrayByteIterable.readIterator(entry.iterator(),
						entry.getLength());

				response.append(array);
			}
		});

		return response.toString();
	}

	@Override
	public boolean save(String fileName, String content) {
		if (fileName == null || fileName.isEmpty())
			return false;
		if (content == null || content.isEmpty())
			return false;

		@NotNull
		final ByteIterable keyFileName = StringBinding.stringToEntry(fileName);

		@NotNull
		final ByteIterable valueContent = StringBinding.stringToEntry(content);
		env.executeInTransaction((txn) -> {
			storeDocuments.put(txn, keyFileName, valueContent);
		});
		LOG.info("Save content of the file with name " + fileName);

		return true;
	}

	@Override
	public boolean update(String fileName, String content) {
		if (fileName == null || fileName.isEmpty())
			return false;
		if (content == null || content.isEmpty())
			return false;

		final ByteIterable keyFileName = StringBinding.stringToEntry(fileName);
		@NotNull
		final ByteIterable valueContent = StringBinding.stringToEntry(content);
		env.executeInTransaction((txn) -> {
			if (storeDocuments.get(txn, valueContent) != null) {
				storeDocuments.put(txn, keyFileName, valueContent);
				LOG.info("Update file with name" + fileName);
			}

		});

		return true;
	}

	@Override
	public boolean delete(String fileName) {
		@NotNull
		final ByteIterable key = StringBinding.stringToEntry(fileName);
		AtomicBoolean success = new AtomicBoolean();
		success.set(true);
		env.executeInTransaction((txn) -> {
			boolean isDel = storeDocuments.delete(txn, key);
			if (!isDel) {
				LOG.error("Document with nane " + fileName + " wasn't deleted");
				success.set(false);
			}
		});
		LOG.info("Delete document with name " + fileName);
		return success.get();
	}

	@PreDestroy
	private void stopChacheDAO() {
		transferFiles();
		runDirectCommands.close();
		scheduledExecutorService.shutdown();
		env.close();
	}

}
