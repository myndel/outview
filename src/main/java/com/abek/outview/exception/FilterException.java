package com.abek.outview.exception;

public class FilterException extends ManagerException {
	private static final long serialVersionUID = -4899937682754125200L;

	public FilterException(Throwable e) {
		super(e);
	}

	public FilterException(String message) {
		super(message);
	}
}
