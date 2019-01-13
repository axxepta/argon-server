package de.axxepta.services.implementations;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Singleton;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.RemoteAddCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.jvnet.hk2.annotations.Service;

import com.google.common.io.Files;

import de.axxepta.models.UserAuthModel;
import de.axxepta.services.interfaces.IDocumentGitService;

@Service(name = "DocumentGitServiceImplementation")
@Singleton
public class DocumentGitServiceImpl implements IDocumentGitService {

	private static final Logger LOG = Logger.getLogger(DocumentGitServiceImpl.class);

	private Map<String, Git> gitCloneMap;

	private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

	private Runnable clearRunnable = new Runnable() {
		public void run() {
			long now = Instant.now().toEpochMilli();

			int numberDeletedDir = 0;
			for (Entry<String, Git> entry : gitCloneMap.entrySet()) {
				File dirGit = entry.getValue().getRepository().getDirectory();
				if (now - 1000000 > dirGit.lastModified()) {
					try {
						FileUtils.forceDelete(dirGit);
						gitCloneMap.remove(entry.getKey());
						numberDeletedDir++;
					} catch (IOException e) {
						LOG.error("Directory " + dirGit.getAbsolutePath() + " cannot be deleted, with exception "
								+ e.getMessage());
					}
				}
			}

			LOG.info("Number of directory deleted is " + numberDeletedDir);
		}
	};

	@PostConstruct
	private void initService() {
		gitCloneMap = new HashMap <>();
		scheduledExecutorService.scheduleAtFixedRate(clearRunnable, 3, 8, TimeUnit.MINUTES);
	}

	@Override
	public List<String> getRemoteNames(String gitURL, UserAuthModel userAuth) {

		LOG.info("Obtain head names");

		Collection<Ref> refs = getRefsFromURL(gitURL, userAuth);
		if (refs == null)
			return null;
		List<String> branchesNameList = new ArrayList<>();
		for (Ref ref : refs) {
			branchesNameList.add(ref.getName().substring(ref.getName().lastIndexOf("/") + 1, ref.getName().length()));
		}

		return branchesNameList;
	}

	@Override
	public Pair<List<String>, List<String>> getFileNamesFromCommit(String gitURL, UserAuthModel userAuth) {
		File temp = null;
		try {
			temp = File.createTempFile(gitURL.substring(gitURL.lastIndexOf('/'), gitURL.length()), ".suffix");
		} catch (IOException e1) {
			LOG.error("Temp file " + temp + " not exist");
			return null;
		}
		Git git = getGitFromURL(gitURL, userAuth);

		Repository repository = git.getRepository();

		if (repository == null)
			return null;
		
		List<String> listDirs = new ArrayList<>();
		List<String> listFiles = new ArrayList<>();
		try {
			ObjectId objectId = repository.resolve(Constants.HEAD);
			RevWalk walk = new RevWalk(repository);
			RevCommit commit = walk.parseCommit(objectId);
			RevTree tree = commit.getTree();
			TreeWalk treeWalk = new TreeWalk(repository);
			treeWalk.addTree(tree);
			treeWalk.setRecursive(false);
			
			while (treeWalk.next()) {
				if (treeWalk.isSubtree()) {
					listDirs.add(treeWalk.getPathString());
					treeWalk.enterSubtree();
				} else {
					listFiles.add(treeWalk.getPathString());
				}
			}
			
			git.close();
			walk.close();
			treeWalk.close();
			
		} catch (IOException e) {
			LOG.error(e.getMessage());
			return null;
		}

		return Pair.of(listDirs, listFiles);
	}

	@Override
	public byte[] getDocumentFromRepository(String gitURL, String fileName, UserAuthModel userAuth) {
		
		Git git = getGitFromURL(gitURL, userAuth);
		if (git == null)
			return null;
		Repository repository = git.getRepository();

		try {
			ObjectId objectId = repository.resolve(Constants.HEAD);
			RevWalk walk = new RevWalk(repository);

			RevCommit commit = walk.parseCommit(objectId);
			RevTree tree = commit.getTree();
			TreeWalk treewalk = TreeWalk.forPath(repository, fileName, tree);
			byte[] content = repository.open(treewalk.getObjectId(0)).getBytes();
			repository.close();
			walk.close();
			return content;
		} catch (IOException e) {
			LOG.error(e.getMessage());
			return null;
		}
	}

	@Override
	public boolean commitDocumentLocalStored(String gitURL, File localFile, String directoryOnGit, String commitMessage,
			UserAuthModel authUser) {

		Git git = getGitFromURL(gitURL, authUser);

		File root = git.getRepository().getDirectory();
		File dirCommit = new File(root.getAbsolutePath() + File.separator + directoryOnGit);
		if (!dirCommit.exists()) {
			LOG.error("Directory " + directoryOnGit + " not exists");
			return false;
		}
		
		try {
			FileUtils.copyFileToDirectory(localFile, dirCommit);
		} catch (IOException e) {
			LOG.error("The file could not be copied " + e.getMessage());
			return false;
		}
		try {
			git.add().addFilepattern(localFile.getName()).call();
		} catch (GitAPIException e) {
			LOG.error("The file cannot be added to local repository " + e.getMessage());
			return false;
		}
		
		//commit
		try {
			git.commit().setOnly(localFile.getName()).setMessage(commitMessage).call();
		} catch (GitAPIException e) {
			LOG.error("The file cannot be commited " + e.getMessage());
			return false;
		}

		// push on local git
		
		RemoteAddCommand remoteAddCommand = git.remoteAdd();
		String branchName;
		try {
			branchName = git.getRepository().getBranch();
		} catch (IOException e1) {
			LOG.error("Cannot be obtain branch name");
			return false;

		}

		LOG.info("Branch name is " + branchName);

		remoteAddCommand.setName(branchName);
		try {
			remoteAddCommand.setUri(new URIish(gitURL));
		} catch (URISyntaxException e) {
			LOG.error(e.getMessage());
			return false;
		}

		try {
			remoteAddCommand.call();
		} catch (GitAPIException e) {
			LOG.error(e.getMessage());
			return false;
		}

		PushCommand pushCommand = git.push();
		pushCommand.setCredentialsProvider(
				new UsernamePasswordCredentialsProvider(authUser.getUsername(), authUser.getPassword()));

		try {
			pushCommand.call();
		} catch (GitAPIException e) {
			LOG.error(e.getMessage());
			return false;
		}
		return false;
	}

	private Collection<Ref> getRefsFromURL(String url, UserAuthModel authUser) {
		UsernamePasswordCredentialsProvider credentials = null;
		String username = authUser.getUsername();
		String password = authUser.getPassword();
		if (username != null && !username.isEmpty() && password != null && !password.isEmpty())
			credentials = new UsernamePasswordCredentialsProvider(username, password);

		long start = System.nanoTime();

		Collection<Ref> refsRepository = null;
		try {
			refsRepository = Git.lsRemoteRepository().setHeads(true).setTags(true).setRemote(url)
					.setCredentialsProvider(credentials).call();
		} catch (GitAPIException e) {
			LOG.error(e.getMessage());
			return null;
		}

		long end = System.nanoTime();

		LOG.info("Duration to obtain Ref from URL " + url + "  is " + (end - start) + " nano seconds");

		return refsRepository;
	}

	private Git getGitFromURL(String url, UserAuthModel authUser) {

		UsernamePasswordCredentialsProvider credentials = null;

		String username = authUser.getUsername();
		String password = authUser.getPassword();
		if (username != null && !username.isEmpty() && password != null && !password.isEmpty())
			credentials = new UsernamePasswordCredentialsProvider(username, password);

		Git git = null;
		if (!gitCloneMap.containsKey(url)) {
			File tempDirClone = null;
			
			tempDirClone =  Files.createTempDir();		
			
			long start = System.nanoTime();
			try {
				git = Git.cloneRepository().setURI(url).setCredentialsProvider(credentials).setDirectory(tempDirClone)
						.call();
			} catch (GitAPIException e) {
				LOG.error(e.getMessage());
				return null;
			}

			long end = System.nanoTime();

			gitCloneMap.put(url, git);
			LOG.info("Duration to obtain Ref from URL " + url + "  is " + (end - start) + " nano seconds");
		} else {
			LOG.info(url + " is contained in hash map");
			git = gitCloneMap.get(url);
		}

		return git;
	}

	@PreDestroy
	private void shutdowsService() {
		for (Entry<String, Git> entry : gitCloneMap.entrySet()) {
			File dirGit = entry.getValue().getRepository().getDirectory();
			try {
				FileUtils.forceDelete(dirGit);
				LOG.info(dirGit + " was succesfuly deleted");
			} catch (IOException e) {
				LOG.error("Directory " + dirGit.getAbsolutePath() + " cannot be deleted, with exception "
						+ e.getMessage() + " in shutdown method");
			}
		}
	}
}
