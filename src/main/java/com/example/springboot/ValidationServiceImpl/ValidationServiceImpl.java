package com.example.springboot.ValidationServiceImpl;

import com.example.springboot.ValidationService.ValidationService;
import com.example.springboot.exception.InvalidPasswordException;
import org.springframework.stereotype.Service;

@Service
public class ValidationServiceImpl implements ValidationService {

    @Override
    public boolean isValidEmailId(String emailId) {
        return emailId != null && emailId.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }

    @Override
    public boolean isValidPhoneNo(String phoneNo) {
        return phoneNo != null && phoneNo.matches("\\d{10}");
    }

    @Override
    public boolean isValidPassword(String password) throws InvalidPasswordException {
        if (password == null) {
            throw new InvalidPasswordException("Password cannot be null.");
        }

        // Check password length
        if (password.length() < 8 || password.length() > 15) {
            throw new InvalidPasswordException("Password must be between 8 and 15 characters long.");
        }

        // Check password pattern
        String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()\\-\\[{}\\]:;',?/*~$%^+=<>\\[\\]]).{8,15}$";
        if (!password.matches(regex)) {
            throw new InvalidPasswordException(
                    "Password must contain at least one lowercase letter, one uppercase letter, one digit, and one special character.");
        }

        return true;
    }
}
