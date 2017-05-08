package com.abek.outview.exception;

public class ConfigException extends ManagerException {
	private static final long serialVersionUID = -4899937682754125200L;

	public ConfigException(Throwable e) {
		super(e);
	}

	public ConfigException(String message) {
		super(message);
	}
}
