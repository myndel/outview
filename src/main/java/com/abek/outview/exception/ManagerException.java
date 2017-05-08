package com.abek.outview.exception;

public class ManagerException extends Exception {

	private static final long serialVersionUID = 6509920397038186755L;

	public ManagerException(Throwable e) {
		super(e);
	}

	public ManagerException(String message) {
		super(message);
	}
}
