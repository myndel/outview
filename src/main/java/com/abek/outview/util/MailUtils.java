package com.abek.outview.util;

import javax.mail.BodyPart;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.Logger;

import com.abek.outview.model.Email;
import com.abek.outview.model.EmailCollection;
import com.abek.outview.model.Sender;

public class MailUtils {
	
	private static Logger LOGGER = Logger.getLogger(MailUtils.class);
	
	/**
	 * Retourne le contenu text à partir d'un corps d'email
	 * @param mimeMultipart
	 * @return
	 * @throws Exception
	 */
	public static String getTextFromMimeMultipart(MimeMultipart mimeMultipart) throws Exception {
		String result = "";
		int count = mimeMultipart.getCount();
		for (int i = 0; i < count; i++) {
			try{
				BodyPart bodyPart = mimeMultipart.getBodyPart(i);
				if (bodyPart.isMimeType("text/plain")) {
					result = result + "\n" + bodyPart.getContent();
					break; // without break same text appears twice in my tests
				} else if (bodyPart.isMimeType("text/html")) {
					String html = (String) bodyPart.getContent();
					result = result + "\n" + org.jsoup.Jsoup.parse(html).text();
				} else if (bodyPart.getContent() instanceof MimeMultipart) {
					result = result + getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent());
				}
			}catch (Exception e) {
				LOGGER.warn("[MailUtils] Failed reading mail body texte: "+e.getMessage());
			}
		}
		return result;
	}
	
	/**
	 * Retourne l'objet qui contient les emails, triée par expéditeur (identifiant par son email formatté)
	 * @param senderId
	 * @return
	 */
	public static Sender findSender(String senderId, EmailCollection emailCollector){
		if(emailCollector == null || senderId == null || senderId.trim().isEmpty()){
			return null;
		}
		
		Sender criteria = new Sender();
		criteria.setId(senderId);
		if(!emailCollector.getSenders().contains(criteria)){
			return null;
		}

		for(Sender sender: emailCollector.getSenders()){
			if(senderId.equalsIgnoreCase(sender.getId())){
				return sender;
			}
		}
		
		return null;
	}
	
	/**
	 * Compte le nombre d'emals
	 * @param emailCollector
	 * @return
	 */
	public static int getEmailCount(EmailCollection emailCollector){
		int total = 0;
		
		for(Sender sender: emailCollector.getSenders()){
			total += sender.getEmails().size();
		}
		
		return total;
	}
	
	/**
	 * Retourne l'identifiant 
	 * @param email
	 * @return
	 */
	public static String getSenderCleanName(Email email) {
		String cleanName = email.getFrom().replaceAll("[<>]+", "");
		cleanName = cleanName.replaceAll("[\\W+]", "_");
		return cleanName;
	}
}
