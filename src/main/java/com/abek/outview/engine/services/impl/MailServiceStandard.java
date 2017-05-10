package com.abek.outview.engine.services.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.FetchProfile;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.Logger;

import com.abek.outview.exception.FilesystemException;
import com.abek.outview.exception.MailConnectException;
import com.abek.outview.exception.OutputException;
import com.abek.outview.manager.ConfigManager;
import com.abek.outview.manager.FilesystemManager;
import com.abek.outview.manager.FilterManager;
import com.abek.outview.model.Email;

public class MailServiceStandard extends AbstractMailService {
	private static Logger LOGGER = Logger.getLogger(MailServiceStandard.class);

	public MailServiceStandard(ConfigManager config) {
		super();
		this.config = config;
	}

	public List<Email> listEmails() throws MailConnectException {
		return emails;
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
		super.init();
		Properties properties = config.getProperties();
		FilterManager filter = FilterManager.getInstance(config);
		FilesystemManager fileSystem = FilesystemManager.getInstance(config);
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
			LOGGER.debug(String.format("[IMAP/POP3] Found %d emails", messages.length));

			// Optimisation de la lecture des emails, utiliser le fetch
			FetchProfile fetchProfile = new FetchProfile();
//			fetchProfile.add(FetchProfile.Item.ENVELOPE);
//			fetchProfile.add(FetchProfile.Item.FLAGS);
			fetchProfile.add(FetchProfile.Item.CONTENT_INFO);
			
			LOGGER.debug("[IMAP/POP3] Fetching email list");
			emailFolder.fetch(messages, fetchProfile);
			LOGGER.debug("[IMAP/POP3] Finished fetching email list");
			
			int index = 0;
			for (Message message: messages) {
				Email email = new Email();
				email.setSubject(message.getSubject());
				if(message.getFrom() != null && message.getFrom().length > 0){
					Address from = message.getFrom()[0];
					email.setFrom(from.toString());
				}
				email.setBody(getTextFromMessage(message));
				
				if(!filter.select(email)){
					email = null;
					continue;
				}
				
				index++;
				email.setIndex(index);
				
				fileSystem.prepareDirectory(email);
				writeAttachment(email, message);
				writeToEml(message, email);
				writeToTxt(email);
				
//				emails.add(email);
				LOGGER.debug(String.format("[IMAP/POP3] got %d/%d emails", index, messages.length));
			}
			
			LOGGER.debug(String.format("[IMAP/POP3] Found %d emails", emails.size()));
			
			emailFolder.close(false);
			store.close();

		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		LOGGER.debug("[IMAP/POP3] Finished Initializing connection");
	}
	
	/**
	 * Sauvegarde les PJ dans le répertoire
	 * @param email 
	 * @param message
	 * @throws MessagingException 
	 * @throws IOException 
	 * @throws FilesystemException 
	 */
	protected void writeAttachment(Email email, Message message) throws FilesystemException, MessagingException, IOException{
		Object content = message.getContent();
		
		if (content instanceof Multipart) {
			Multipart multipart = (Multipart) content;
			
			for (int i = 0; i < multipart.getCount(); i++) {
				BodyPart bodyPart = multipart.getBodyPart(i);
				if(!Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())){
					continue; // dealing with attachments only
				} 
				fileSystem.writeAttachment(bodyPart.getInputStream(), bodyPart.getFileName(), email);
			}
		}
	}
	
	private String getTextFromMessage(Message message) throws Exception {
	    String result = "";
	    if (message.isMimeType("text/plain")) {
	        result = message.getContent().toString();
	    } else if (message.isMimeType("multipart/*")) {
	        MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
	        result = getTextFromMimeMultipart(mimeMultipart);
	    }
	    else{
	    	result = message.getContent().toString();
	    }
	    return result;
	}

	private String getTextFromMimeMultipart(
	        MimeMultipart mimeMultipart) throws Exception{
	    String result = "";
	    int count = mimeMultipart.getCount();
	    for (int i = 0; i < count; i++) {
	        BodyPart bodyPart = mimeMultipart.getBodyPart(i);
	        if (bodyPart.isMimeType("text/plain")) {
	            result = result + "\n" + bodyPart.getContent();
	            break; // without break same text appears twice in my tests
	        } else if (bodyPart.isMimeType("text/html")) {
	            String html = (String) bodyPart.getContent();
	            result = result + "\n" + org.jsoup.Jsoup.parse(html).text();
	        } else if (bodyPart.getContent() instanceof MimeMultipart){
	            result = result + getTextFromMimeMultipart((MimeMultipart)bodyPart.getContent());
	        }
	    }
	    return result;
	}
	
	@Override
	protected void writeToEml(Object messageObject, Email email) {
		if (messageObject instanceof Message) {
			Message emailMessage = (Message) messageObject;
			try {
				String mailFilename = fileSystem.getEmailFolder(email) + File.separator + email.getIndex() + ".eml";
				emailMessage.writeTo(new FileOutputStream(new File(mailFilename)));
			} catch (IOException | MessagingException e) {
				e.printStackTrace();
			}
		}
	}
}
