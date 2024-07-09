package com.example.springboot.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.example.springboot.response.AuthRequest;
import com.example.springboot.response.AuthResponse;
import com.example.springboot.service.AuthService;
import com.example.springboot.util.JwtUtil;

@Service
public class AuthServiceImpl implements AuthService {
	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtUtil jwtUtil;

	public AuthResponse generateToken(AuthRequest authRequest) {
		try {
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(authRequest.getEmailId(), authRequest.getPassword()));
			String username = authentication.getName();
			String token = jwtUtil.generateToken(username);
			return new AuthResponse("Authentication successful. Token generated.", token);
		} catch (BadCredentialsException e) {
			return new AuthResponse("Invalid EmailId or password", null);
		}
	}

}
