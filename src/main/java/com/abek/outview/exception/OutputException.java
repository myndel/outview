package com.abek.outview.exception;

public class OutputException extends Exception {

	private static final long serialVersionUID = 983870732845806478L;

	public OutputException(Throwable e) {
		super(e);
	}

	public OutputException(String message) {
		super(message);
	}
}
