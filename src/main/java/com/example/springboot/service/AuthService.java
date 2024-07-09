package com.example.springboot.service;

import com.example.springboot.response.AuthRequest;
import com.example.springboot.response.AuthResponse;

public interface AuthService {

    AuthResponse generateToken(AuthRequest authRequest);
}
