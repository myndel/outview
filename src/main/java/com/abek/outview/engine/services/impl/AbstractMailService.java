package com.abek.outview.engine.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.abek.outview.engine.services.IMailProvider;
import com.abek.outview.exception.MailConnectException;
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
	
}
