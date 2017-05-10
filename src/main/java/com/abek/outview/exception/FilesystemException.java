package com.abek.outview.exception;

public class FilesystemException extends ManagerException {
	private static final long serialVersionUID = -4899937682754125200L;

	public FilesystemException(Throwable e) {
		super(e);
	}

	public FilesystemException(String message) {
		super(message);
	}
}
