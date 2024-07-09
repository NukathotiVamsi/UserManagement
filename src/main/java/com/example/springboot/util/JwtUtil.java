package com.example.springboot.util;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

// utilize JSON Web Tokens (JWT) for authentication and authorization
@Component
public class JwtUtil {

	// Generate a JWT token for the given username
	public String generateToken(String username) {
		Map<String, Object> claims = new HashMap<>();
		return createToken(claims, username);
	}

	// Create a JWT token with the specified claims and username
	private String createToken(Map<String, Object> claims, String username) {
		return Jwts.builder().setClaims(claims).setSubject(username).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // Token expires in 1 hour
				.signWith(getSecKey(), SignatureAlgorithm.HS256).compact();
	}

	// Get the secret key for signing the token
	private Key getSecKey() {
		byte[] keyBytes = Decoders.BASE64.decode("3273357638792F423F4528482B4D6251655368566D597133743677397A244326");
		return Keys.hmacShaKeyFor(keyBytes);
	}

	// Extract username from JWT token
	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	// Extract a specific claim from the token using a resolver function
	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	// Extract all claims from the token
	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(getSecKey()).build().parseClaimsJws(token).getBody();
	}

	// Validate the JWT token
	public Boolean validateToken(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}

	// Check if the token is expired
	private Boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	// Extract expiration date from the token
	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}
}