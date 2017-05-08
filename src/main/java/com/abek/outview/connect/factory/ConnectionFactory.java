package com.abek.outview.connect.factory;

import com.abek.outview.engine.services.IMailProvider;
import com.abek.outview.engine.services.impl.MailServiceStandard;
import com.abek.outview.manager.ConfigManager;

public class ConnectionFactory {

	private static ConnectionFactory _instance;
	
	private ConnectionFactory(){
		super();
	}
	
	public static ConnectionFactory getInstance(){
		if(_instance == null){
			_instance = new ConnectionFactory();
		}
		
		return _instance;
	}
	
	public IMailProvider getMailProvider(ConfigManager config) {
		MailServiceStandard mail = new MailServiceStandard(config);
		
		return mail;
	}
	
}
