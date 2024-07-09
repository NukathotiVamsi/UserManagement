package com.example.springboot.jwt.filter;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.springboot.response.CustomResponse;
import com.example.springboot.serviceImpl.UserInfoUserDetailsService;
import com.example.springboot.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private UserInfoUserDetailsService userDetailsService;

	@Autowired
	private ObjectMapper objectMapper;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String header = request.getHeader("Authorization");
		if (header != null && header.startsWith("Bearer ")) {
			String token = header.substring(7);
			String username = null;
			try {
				username = jwtUtil.extractUsername(token);
			} catch (ExpiredJwtException e) {
				// Handle expired token exception
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				CustomResponse apiResponse = new CustomResponse("Token expired. Please log in again.");
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
				return;
			} catch (SignatureException | MalformedJwtException e) {
				// Handle invalid token exception
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				CustomResponse apiResponse = new CustomResponse("Invalid token. Please log in again.");
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
				return;
			}

			if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				// If username is extracted and no authentication is present in the security
				// context
				UserDetails userDetails = userDetailsService.loadUserByUsername(username); // Load user details
				if (jwtUtil.validateToken(token, userDetails)) { // Validate the JWT token
					UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
							null, userDetails.getAuthorities()); // Create authentication token
					authToken.setDetails(new WebAuthenticationDetails(request)); // Set authentication details
					SecurityContextHolder.getContext().setAuthentication(authToken); // Set authentication in security
																						// context
				}
			}
		}

		filterChain.doFilter(request, response); // Continue with the filter chain
	}
}
