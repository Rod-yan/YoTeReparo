package com.yotereparo.util;

public class CustomRequestError {
	private String field;
	private String defaultMessage;
	private String objectName;

	public CustomRequestError() { }

	public CustomRequestError(String objectName, String field, String defaultMessage) {
	    this.objectName = objectName;
		this.field = field;
	    this.defaultMessage = defaultMessage;
	}
	
	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getDefaultMessage() {
		return defaultMessage;
	}

	public void setDefaultMessage(String defaultMessage) {
		this.defaultMessage = defaultMessage;
	}
}
