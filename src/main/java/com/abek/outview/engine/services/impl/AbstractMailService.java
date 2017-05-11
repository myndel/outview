package com.abek.outview.engine.services.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.abek.outview.engine.services.IMailProvider;
import com.abek.outview.exception.MailConnectException;
import com.abek.outview.exception.OutputException;
import com.abek.outview.manager.ConfigManager;
import com.abek.outview.manager.FilesystemManager;
import com.abek.outview.model.Email;
import com.abek.outview.model.EmailCollection;
import com.abek.outview.model.EmailSummary;
import com.abek.outview.model.Sender;
import com.abek.outview.util.MailUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class AbstractMailService implements IMailProvider {
	protected static Logger					LOGGER	= Logger.getLogger(AbstractMailService.class);
	protected EmailCollection emailCollector;

	/**
	 * Instance de configuration à fournir pour amorcer la connexion
	 */
	protected ConfigManager					config;

	/**
	 * Instance du gestionnaire FS
	 */
	protected FilesystemManager				fileSystem;

	public void init() throws MailConnectException {
		if (config == null) {
			LOGGER.error("[MAIL PROVIDER] No configuration provided, ConfigManager must not be null");
			throw new MailConnectException("[IMAP/POP3] No configuration provided, ConfigManager must not be null");
		}
		fileSystem = FilesystemManager.getInstance(config);
		emailCollector = new EmailCollection();
	}
	
	/**
	 * Ecrit en sortie l'email
	 * 
	 * @param email
	 */
	protected abstract void writeToEml(Object messageObject, Email email);

	protected abstract void writeAttachment(Object messageObject, Email email);

	/**
	 * Ajoute l'email à la collection tirée par expéditeur
	 * @param email
	 */
	protected void 	addEmailToSendersCollection(Email email) {
		String senderId = MailUtils.getSenderCleanName(email);
		Sender sender = MailUtils.findSender(senderId, emailCollector);
		if(sender == null){
			sender = new Sender();
			emailCollector.getSenders().add(sender);
		}
		
		sender.setId(senderId);
		sender.setMailAddress(email.getFrom());
		sender.setRepository(FilesystemManager.getInstance(config).getEmailFolder(email));
		
		sender.getEmails().add(email);
		
		//Descripteur
		EmailSummary summary = new EmailSummary();
		summary.setId(email.getIndex());
		summary.setSubject(email.getSubject());
		sender.getEmailSummaries().add(summary);
		
		emailCollector.getEmails().add(email);
	}
	
	/**
	 * Imprime dans chaque dossier d'email le résumé des emails en json
	 * @param emailCollector
	 * @throws FileNotFoundException 
	 */
	protected void initializeDescriptors(EmailCollection emailCollector) throws FileNotFoundException {
		LOGGER.debug("[MAIL PROVIDER] Writing email descriptors");
		if(emailCollector != null){
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			for(Sender sender: emailCollector.getSenders()){
				String json = gson.toJson(sender.getEmailSummaries());
				
				//Créer le fichier
				PrintWriter out = new PrintWriter(sender.getRepository()+File.separator+"summary.json");
				out.print(json);
				out.close();
			}
		}
		LOGGER.debug("[MAIL PROVIDER] Finished writing email descriptors");
	}
	
	/**
	 * Ecrit dans un TXT le contenu du mail
	 * 
	 * @param email
	 * @throws OutputException
	 */
	protected void writeToTxt(Email email) throws OutputException {
		LOGGER.debug("[MAIL PROVIDER] Writing body txt mail ");
		try {
			fileSystem.writeEmail(email);
		} catch (FileNotFoundException e) {
			LOGGER.error("[MAIL PROVIDER] Failed writing body txt mail");
			throw new OutputException("Failed writing body txt mail: " + e.getMessage());
		}
		LOGGER.debug("[MAIL PROVIDER] Finished writing body txt mail ");
	}
}
