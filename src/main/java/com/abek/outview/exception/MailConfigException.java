package com.abek.outview.exception;

public class MailConfigException extends ManagerException {
	private static final long serialVersionUID = -4899937682754125200L;

	public MailConfigException(Throwable e) {
		super(e);
	}

	public MailConfigException(String message) {
		super(message);
	}
}
