package com.abek.outview.engine.services;

import java.util.List;

import com.abek.outview.exception.MailConnectException;
import com.abek.outview.model.Email;

public interface IMailProvider {

	void init() throws MailConnectException;
	List<Email> listEmails() throws MailConnectException;
}
