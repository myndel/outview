package com.abek.outview;

import com.abek.outview.exception.MailConfigException;
import com.abek.outview.manager.MailManager;

/**
 * Appliaction de filtre d'emails
 *
 */
public class OutViewApp {
    public static void main( String[] args ) {
    	try {
			MailManager.getInstance().init();
			MailManager.getInstance().getEmailList();
		} catch (MailConfigException e) {
			e.printStackTrace();
		}
    }
}
