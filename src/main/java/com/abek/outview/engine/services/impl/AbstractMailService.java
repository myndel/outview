package com.abek.outview.engine.services.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.abek.outview.engine.services.IMailProvider;
import com.abek.outview.exception.MailConnectException;
import com.abek.outview.exception.OutputException;
import com.abek.outview.manager.ConfigManager;
import com.abek.outview.manager.FilesystemManager;
import com.abek.outview.model.Email;

public abstract class AbstractMailService implements IMailProvider {
	protected static Logger LOGGER = Logger.getLogger(AbstractMailService.class);
	protected List<Email>	emails;

	/**
	 * Instance de configuration à fournir pour amorcer la connexion
	 */
	protected ConfigManager	config;
	
	/**
	 * Instance du gestionnaire FS
	 */
	protected FilesystemManager fileSystem;

	public void init() throws MailConnectException{
		if (config == null) {
			LOGGER.error("[MAIL PROVIDER] No configuration provided, ConfigManager must not be null");
			throw new MailConnectException("[IMAP/POP3] No configuration provided, ConfigManager must not be null");
		}
		fileSystem = FilesystemManager.getInstance(config);
		emails = new ArrayList<>();
	}
	
	/**
	 * Ecrit en sortie l'email
	 * @param email
	 */
	protected abstract void writeToEml(Object messageObject, Email email);
	
	/**
	 * Ecrit dans un TXT le contenu du mail
	 * @param email
	 * @throws OutputException 
	 */
	protected void writeToTxt(Email email) throws OutputException{
		LOGGER.debug("[MAIL PROVIDER] Writing body txt mail ");
		try {
			fileSystem.writeEmail(email);
		} catch (FileNotFoundException e) {
			LOGGER.error("[MAIL PROVIDER] Failed writing body txt mail");
			throw new OutputException("Failed writing body txt mail: "+e.getMessage());
		}
		LOGGER.debug("[MAIL PROVIDER] Finished writing body txt mail ");
	}
}
