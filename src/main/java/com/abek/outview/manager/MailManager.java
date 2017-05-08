package com.abek.outview.manager;

import org.apache.log4j.Logger;

import com.abek.outview.exception.ConfigException;
import com.abek.outview.exception.MailConfigException;
import com.abek.outview.manager.interfaces.IManager;

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
}
