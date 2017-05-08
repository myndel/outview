package com.abek.outview.engine.services.impl;

import java.util.List;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

import org.apache.log4j.Logger;

import com.abek.outview.engine.services.IMailProvider;
import com.abek.outview.exception.MailConnectException;
import com.abek.outview.exception.OutputException;
import com.abek.outview.manager.ConfigManager;
import com.abek.outview.model.Email;

public class MailServiceStandard implements IMailProvider {
	private static Logger LOGGER = Logger.getLogger(MailServiceStandard.class);

	/**
	 * Instance de configuration à fournir pour amorcer la connexion
	 */
	private ConfigManager config;

	public MailServiceStandard(ConfigManager config) {
		super();
		this.config = config;
	}

	@Override
	public List<Email> listEmails() throws MailConnectException {
		return null;
	}

	@Override
	public void writeEmail(Email email) throws OutputException {

	}

	/**
	 * Initialise la connexion à l'email en fonction de la configuration
	 */
	@Override
	public void init() throws MailConnectException {
		LOGGER.debug("[IMAP/POP3] Initializing connection");
		if (config == null) {
			LOGGER.error("[IMAP/POP3] No configuration provided, ConfigManager must not be null");
			throw new MailConnectException("[IMAP/POP3] No configuration provided, ConfigManager must not be null");
		}

		Properties properties = config.getProperties();
		try {
			Session emailSession = Session.getDefaultInstance(properties);

			// create the POP3 store object and connect with the pop server

			String host = config.getHost();
			String user = config.getUsername();
			String password = config.getPassword();
			String protocol = config.getProtocol();
			
			Store store = emailSession.getStore(protocol);
			store.connect(host, user, password);

			// create the folder object and open it
			Folder emailFolder = store.getFolder("INBOX");
			emailFolder.open(Folder.READ_ONLY);

			// retrieve the messages from the folder in an array and print it
			Message[] messages = emailFolder.getMessages();
			System.out.println("messages.length---" + messages.length);

			for (int i = 0, n = messages.length; i < n; i++) {
				Message message = messages[i];
				System.out.println("---------------------------------");
				System.out.println("Email Number " + (i + 1));
				System.out.println("Subject: " + message.getSubject());
				System.out.println("From: " + message.getFrom()[0]);
				System.out.println("Text: " + message.getContent().toString());
				
				Email email = new Email();
			}

			// close the store and folder objects
			emailFolder.close(false);
			store.close();

		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		LOGGER.debug("[IMAP/POP3] Finished Initializing connection");
	}

}
