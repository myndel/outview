package com.abek.outview.manager;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.abek.outview.connect.factory.ConnectionFactory;
import com.abek.outview.engine.services.IMailProvider;
import com.abek.outview.exception.ConfigException;
import com.abek.outview.exception.MailConfigException;
import com.abek.outview.exception.MailConnectException;
import com.abek.outview.manager.interfaces.IManager;
import com.abek.outview.model.Email;

public class MailManager implements IManager{
	
	private static Logger LOGGER = Logger.getLogger(MailManager.class);
	public static MailManager _instance;
	
	private MailManager(){
		super();
	}
	
	public static MailManager getInstance(){
		if(_instance == null){
			_instance = new MailManager();
		}
		return _instance;
	}
	
	/**
	 * Initialise les properties, et les connexions
	 */
	public void init() throws MailConfigException{
		LOGGER.debug("[MAIL] initializing properties");
		try {
			ConfigManager.getInstance().init();
		} catch (ConfigException e) {
			LOGGER.error("[MAIL] Initialization failed: "+e.getMessage());
			throw new MailConfigException(e);
		}
	}
	
	public List<Email> getEmailList(){
		LOGGER.debug("[MAIL] start reading emails");
		ArrayList<Email> listEmails = new ArrayList<>();
		
		IMailProvider mailProvider = ConnectionFactory.getInstance().getMailProvider(ConfigManager.getInstance());
		try {
			mailProvider.init();
			listEmails.addAll(mailProvider.listEmails());
		} catch (MailConnectException e) {
			e.printStackTrace();
		}
		
		LOGGER.debug(String.format("[MAIL] end reading emails; %d e-mail(s) found", listEmails.size()));
		return listEmails;
	}
}
