package com.qxs.generator.web.model;

import java.io.ByteArrayOutputStream;

import javax.persistence.Transient;

public class GenerateResult {

	@Transient
	private ByteArrayOutputStream outputStream;

	private Status status;

	private String message;

	
	public GenerateResult() {
		super();
	}
	public GenerateResult(Status status, ByteArrayOutputStream outputStream) {
		super();
		this.status = status;
		this.outputStream = outputStream;
	}
	public GenerateResult(Status status, String message) {
		super();
		this.status = status;
		this.message = message;
	}

	public ByteArrayOutputStream getOutputStream() {
		return outputStream;
	}

	public void setOutputStream(ByteArrayOutputStream outputStream) {
		this.outputStream = outputStream;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public static enum Status {
		SUCCESS, FAIL
	}
}
