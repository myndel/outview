package com.abek.outview.engine.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.abek.outview.engine.services.IMailProvider;
import com.abek.outview.exception.MailConnectException;
import com.abek.outview.exception.ManagerException;
import com.abek.outview.manager.ConfigManager;
import com.abek.outview.manager.FilterManager;
import com.abek.outview.model.Email;

public abstract class AbstractMailService implements IMailProvider {
	protected static Logger LOGGER = Logger.getLogger(AbstractMailService.class);
	protected List<Email>	emails;

	/**
	 * Instance de configuration à fournir pour amorcer la connexion
	 */
	protected ConfigManager	config;
	protected FilterManager	filter;

	public void init() throws MailConnectException{
		if (config == null) {
			LOGGER.error("[MAIL PROVIDER] No configuration provided, ConfigManager must not be null");
			throw new MailConnectException("[IMAP/POP3] No configuration provided, ConfigManager must not be null");
		}
		if(filter == null){
			LOGGER.error("[MAIL PROVIDER] creating new filter");
			filter = FilterManager.getInstance(config);
			try {
				filter.init();
			} catch (ManagerException e) {
				LOGGER.error("[MAIL PROVIDER] Filed initializing filter: "+e.getMessage());
				throw new MailConnectException(e);
			}
		}
		emails = new ArrayList<>();
	}
	
	/**
	 * Traite les messages et les filtre
	 * 
	 * @param bufferEmails
	 */
	protected void initEmailLists(List<Email> bufferEmails) {
		// Ce n'est pas la façon la plus optimisée pour filtrer, ie, récupérer
		// tous les emails en mémoire... ensuite la filtrer
		// ici un showcase pour montrer comment filtrer les message en utilisant
		// le stream de java 8
		emails = bufferEmails.stream().filter(email -> {
			return filter.select(email);
		}).collect(Collectors.toList());

	}
}
