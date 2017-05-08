package com.abek.outview.exception;

public class MailConnectException extends Exception {
	private static final long serialVersionUID = -8389215871679944459L;

	public MailConnectException(Throwable e) {
		super(e);
	}

	public MailConnectException(String message) {
		super(message);
	}
}
