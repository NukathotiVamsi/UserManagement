package com.example.springboot.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.example.springboot.dto.UserDTO;
import com.example.springboot.dto.UserDao;

public interface UserService {
	ResponseEntity<?> saveUserWithRandomPassword(UserDao userDao);

	ResponseEntity<?> findUserById(Long userId);

	ResponseEntity<?> getAllUser();

	public ResponseEntity<?> updateUser(Long id, UserDao userDao);

	ResponseEntity<?> deleteUserId(long id);

	public List<UserDTO> findUsersWithSorting(String field);

	public ResponseEntity<?> findUsersWithPagination(Integer offSet, Integer pageSize);

	public ResponseEntity<?> searchUsers(String searchKey);

}
