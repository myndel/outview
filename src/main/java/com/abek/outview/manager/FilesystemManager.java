package com.abek.outview.manager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;

import com.abek.outview.AppConstants;
import com.abek.outview.exception.FilesystemException;
import com.abek.outview.exception.FilterException;
import com.abek.outview.exception.ManagerException;
import com.abek.outview.manager.interfaces.IManager;
import com.abek.outview.model.Email;

public class FilesystemManager implements IManager {
	private static Logger LOGGER = Logger.getLogger(FilesystemManager.class);

	private static FilesystemManager _instance;
	private ConfigManager config;
	
	private FilesystemManager(){
		super();
	}

	private FilesystemManager(ConfigManager config){
		this();
		this.config = config;
	}
	
	public static FilesystemManager getInstance(ConfigManager config){
		if(_instance == null){
			_instance = new FilesystemManager(config);
		}
		
		return _instance;
	}
	@Override
	public void init() throws ManagerException {
		LOGGER.debug("[FS Manager] Initializing filter configuration");
		if(config == null){
			LOGGER.error("[FS Manager] config manager must not be null");
			throw new FilterException("config manager must not be null");
		}
		
		String repositoryDirName = config.getEmailRepository();
		LOGGER.debug(String.format("[FS Manager] Repository location: %s", repositoryDirName));
		if(repositoryDirName == null || repositoryDirName.trim().isEmpty()){
			LOGGER.error("[FS Manager] Repository name must not be empty: property name: "+AppConstants.PROPERTY_MAIL_REPOSITORY);
			throw new FilterException("Repository name must not be empty: property name: "+AppConstants.PROPERTY_MAIL_REPOSITORY);
		}
		
		//On crée le répertoire s'il n'existe pas.
		File repository = new File(repositoryDirName);
		if(!repository.exists()){
			LOGGER.debug("[FS Manager] creating new repository");
			repository.mkdirs();
		}
		else{
			LOGGER.debug("[FS Manager] repository exists, clean it");
			clearDir(repository);
			LOGGER.debug("[FS Manager] repository exists, clean!");
		}
	}

	/**
	 * Crée un répertoire pour l'expéditeur avec son adresse d'envoi comme nom
	 * @param email
	 */
	public void prepareDirectory(Email email){
		String repository = config.getEmailRepository();
		
		String cleanFrom = getFolderFor(email);
		File emailFolder = new File(repository+File.separator+cleanFrom);
		
		if(!emailFolder.exists()){
			LOGGER.debug("[FS Manager] Preparing folder for "+email.getFrom());
			emailFolder.mkdirs();
			LOGGER.debug(String.format("[FS Manager] folder for \"%s\" ready", email.getFrom()));
		}
		
	}
	
	/**
	 * Nettoie tout le contenu du répertoire
	 * @param repository
	 */
	private void clearDir(File folder) {
	    File[] files = folder.listFiles();
	    if(files!=null) {
	        for(File f: files) {
	            if(f.isDirectory()) {
	            	clearDir(f);
	            } else {
	                f.delete();
	            }
	        }
	    }
	    folder.delete();
	}

	/**
	 * Retourne le nom du dossier à utiliser pour cet email
	 * @param email
	 * @return
	 */
	private String getFolderFor(Email email) {
		String cleanName = email.getFrom().replaceAll("[<>]+", "");
		cleanName = cleanName.replaceAll("[\\W+]", "_");
		return cleanName;
	}

	/**
	 * Ecrit sur le FS la PJ
	 * @param bodyPart
	 * @param email
	 * @throws FilesystemException 
	 */
	public void writeAttachment(InputStream inputStream, String filename, Email email) throws FilesystemException {
			//Pièce jointe si présente (<numéro>_<nom pièce  jointe>.<extension pièce jointe>)
			String attachmentFilname = getEmailFolder(email);
			attachmentFilname += File.separator + email.getIndex() + "_" + filename;
			
			LOGGER.debug("[FS Manager] writing PJ: "+attachmentFilname);
		try {
			FileOutputStream fios = new FileOutputStream(attachmentFilname);
			byte[] buffer = new byte[1024 * 32];
	        int len = inputStream.read(buffer);
	        
	        while (len != -1) {
	        	fios.write(buffer, 0, len);
	            len = inputStream.read(buffer);
	        }
	        
	        inputStream.close();
	        fios.flush();
	        fios.close();
		} catch (IOException  e) {
			LOGGER.error("[FS Manager] Failed writing attachment: "+e.getMessage());
			throw new FilesystemException(e);
		}
		LOGGER.debug("[FS Manager] Finished writing attachment");
	}

	public String getEmailFolder(Email email) {
		return  config.getEmailRepository() + getFolderFor(email);
	}

	/**
	 * Ecrit le fichier dans un fichier text
	 * @param email
	 * @throws FileNotFoundException 
	 */
	public void writeEmail(Email email) throws FileNotFoundException {
		String filename = getEmailFolder(email);
		filename += File.separator + email.getIndex()+".txt";
		
		StringBuilder mailContent = new StringBuilder();
		
		mailContent.append(email.getSubject());
		mailContent.append("\n");
		mailContent.append("\n");
		mailContent.append("\n");
		String rawBody = email.getBody();
		String escapedBody = StringEscapeUtils.escapeHtml4(rawBody);
		mailContent.append(escapedBody);
		
		
		PrintWriter out = new PrintWriter(filename);
		out.print(mailContent.toString());
		out.close();
	}
}
