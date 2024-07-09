package com.example.springboot.serviceImpl;

import com.example.springboot.entity.User;
import com.example.springboot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserInfoUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String emailId) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByEmailId(emailId);
        User user = userOptional.orElseThrow(() -> new UsernameNotFoundException("User not found with emailId: " + emailId));

        // Return a UserDetails object which implements Spring Security's UserDetails interface
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmailId())
                .password(user.getPassword())
                .roles("USER ") // You can set roles or authorities here as needed
                .build();
    }
}
