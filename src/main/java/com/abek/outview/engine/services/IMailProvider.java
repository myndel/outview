package com.abek.outview.engine.services;

import java.util.List;

import com.abek.outview.exception.MailConnectException;
import com.abek.outview.exception.OutputException;
import com.abek.outview.model.Email;

public interface IMailProvider {

	void init() throws MailConnectException;
	void writeEmail(Email email) throws OutputException;
	List<Email> listEmails() throws MailConnectException;
}
