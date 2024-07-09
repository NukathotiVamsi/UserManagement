package com.example.springboot.ValidationService;

import com.example.springboot.exception.InvalidPasswordException;

public interface ValidationService {
	public boolean isValidEmailId(String emailId);

	public boolean isValidPhoneNo(String phoneNo);

	public boolean isValidPassword(String password) throws InvalidPasswordException;
}