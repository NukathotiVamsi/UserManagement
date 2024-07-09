package com.example.springboot.response;

public class Response {
	private Integer statusCode;
	private Object isError;
	private Object result;

	public Response() {

	}

	public Integer getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(Integer statusCode) {
		this.statusCode = statusCode;
	}

	public Object getIsError() {
		return isError;
	}

	public void setIsError(Object isError) {
		this.isError = isError;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

}
