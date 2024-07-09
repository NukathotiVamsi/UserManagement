package com.example.springboot.response;

public class CustomResponse {
	private String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;

	}

	public CustomResponse(String message) {
		super();
		this.message = message;
	}

	

}
