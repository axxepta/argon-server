package de.axxepta.dao.implementations;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.core.Context;

import org.apache.log4j.Logger;
import org.glassfish.jersey.server.ResourceConfig;
import org.jvnet.hk2.annotations.Service;

import de.axxepta.dao.interfaces.IDocumentCacheDAO;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;

@Service(name = "DocumentMemoryCacheDAO")
@Singleton
public class DocumentMemoryCacheDAO implements IDocumentCacheDAO {

	private Cache cache;
	
	private static final Logger LOG = Logger.getLogger(DocumentMemoryCacheDAO.class);
	
	@Inject
	@Named("DocumentDatabaseCacheDAO")
	IDocumentCacheDAO documentDatabaseDAO;
	
	@Context
	private ResourceConfig resourceConfig;
	
	@PostConstruct
	private void initCacheDAO() {
		cache = CacheManager.getInstance().getCache("documents");
		configureCache();
	}
	
	private void configureCache() {
		CacheConfiguration config = cache.getCacheConfiguration();
		config.setEternal(false);
		int maxElements = 1000;
		int timeLive = 100;
		try {
			maxElements = Integer.parseUnsignedInt((String) resourceConfig.getProperty("cachemax-elements-in-memory"));
		} catch (NumberFormatException e) {
			LOG.error((String) resourceConfig.getProperty("cache-max-elements-in-memory") + "is not a number");
		}

		try {
			timeLive = Integer.parseUnsignedInt((String) resourceConfig.getProperty("cache-seconds-time-to-live"));
		} catch (NumberFormatException e) {
			LOG.error((String) resourceConfig.getProperty("cache-seconds-time-to-live") + "is not a number");
		}

		config.setMaxElementsInMemory(maxElements);
		config.setTimeToLiveSeconds(timeLive);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<String> getSavedFilesName() {
		return cache.getKeys();
	}

	@Override
	public String getContentFile(String fileName) {
		if(cache.isKeyInCache(fileName))
			return (String) cache.get(fileName).getValue();
		else
			return documentDatabaseDAO.getContentFile(fileName);
	}

	@Override
	public boolean save(String fileName, String content) {
		Element element = new Element(fileName, content);
		cache.put(element);
		return documentDatabaseDAO.save(fileName, content);	
	}

	@Override
	public boolean update(String fileName, String content) {
		
		LOG.info("Update " + fileName);
		
		if( cache.isKeyInCache(fileName) ) {
			Element element = new Element(fileName, content);
			cache.put(element);
			return documentDatabaseDAO.update(fileName, content);
		}
		else return false;
			
	}

	@Override
	public boolean delete(String fileName) {
		
		LOG.info("Delete " + fileName);
		
		boolean response;
		if( cache.isKeyInCache(fileName) ) {
			response = cache.remove(fileName);
			response = documentDatabaseDAO.delete(fileName);
		}
		else 
			response = false;
		
		return response;
	}
	
	@PreDestroy
	private void shutdownService() {
		CacheManager.getInstance().shutdown();
	}
}
