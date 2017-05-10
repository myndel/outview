package com.abek.outview.manager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.abek.outview.AppConstants;
import com.abek.outview.exception.ConfigException;
import com.abek.outview.manager.interfaces.IManager;

public class ConfigManager implements IManager{
	private static Logger LOGGER = Logger.getLogger(ConfigManager.class);
	private static ConfigManager _instance;

	private Properties properties;
	
	private ConfigManager(){
		super();
	}
	
	public static ConfigManager getInstance(){
		if(_instance == null){
			_instance = new ConfigManager();
		}
		
		return _instance;
	}
	
	/**
	 * Initialise la configuration à partir du fichier properties}
	 * @throws ConfigException 
	 */
	public void init() throws ConfigException{
		LOGGER.debug("[PROPERTIES] Starting init");
		try {
			properties = new Properties();
            properties.load(getConfigInputStream());
            
            //FIXME security issue: obfusc properties output
            LOGGER.debug("[PROPERTIES] Properties loaded: "+properties.toString());
        } catch (IOException e) {
            LOGGER.error("[PROPERTIES] Coult not load properties");
            throw new ConfigException(e);
        }
	}
	
	/**
	 * Retourne le stream sur le fichier properties
	 * @return
	 * @throws ConfigException 
	 */
	private InputStream getConfigInputStream() throws ConfigException{
		LOGGER.debug("[PROPERTIES] Getting properties stream");
		
        ClassLoader loader = getClass().getClassLoader();
        // FICHIER TEMPORAIRE DE DEV, NO CREDENTIALS ON GIT
        InputStream configFileStream = loader.getResourceAsStream(AppConstants.PROPERTIES_DEV_FILE_NAME);
        if(configFileStream == null){
        	configFileStream = loader.getResourceAsStream(AppConstants.PROPERTIES_FILE_NAME);
        }
        
        
        LOGGER.debug("[PROPERTIES] Properties stream: "+AppConstants.PROPERTIES_FILE_NAME);
        if(configFileStream == null){
        	LOGGER.error("[PROPERTIES] ConfigFile not found");
        	throw new ConfigException("Config file not found");
        }
        
        LOGGER.debug("[PROPERTIES] Config stream found, returning it");
        return configFileStream;
	}

	public String getHost(){
		return properties.getProperty(AppConstants.PROPERTY_MAIL_HOST_NAME);
	}
	
	public String getProtocol(){
		return properties.getProperty(AppConstants.PROPERTY_MAIL_PROTOCOL);
	}
	
	public String getUsername(){
		return properties.getProperty(AppConstants.PROPERTY_MAIL_USERNAME);
	}
	
	public String getPassword(){
		return properties.getProperty(AppConstants.PROPERTY_MAIL_PASSWORD);
	}
	
	/**
	 * retourne le répertoire utilisé pour sauvegarder les emails
	 * @return
	 */
	public String getEmailRepository(){
		String repository = properties.getProperty(AppConstants.PROPERTY_MAIL_REPOSITORY, "");
		repository = repository.trim();
		if(!repository.endsWith(File.separator)){
			repository += File.separator;
		}
		return repository;
	}
	
	public Properties getProperties() {
		return properties;
	}
}
