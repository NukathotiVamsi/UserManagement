package com.example.springboot.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.springboot.dto.UserDTO;
import com.example.springboot.dto.UserDao;

import com.example.springboot.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class UserController {

	@Autowired
	private UserService userService;

	@PostMapping("/users")
	public ResponseEntity<?> saveUser(@Valid @RequestBody UserDao userDao) {
		ResponseEntity<?> createUserResponse = userService.saveUserWithRandomPassword(userDao);

		return createUserResponse;
	}

	@GetMapping("/users/{id}")
	public ResponseEntity<?> findUserById(@PathVariable Long id) {
		return userService.findUserById(id);
	}

	@GetMapping("/users/all")
	public ResponseEntity<?> getAllUsers() {
		return userService.getAllUser();
	}

	@PutMapping("/users/{id}")
	public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserDao userDao) {
		return userService.updateUser(id, userDao);
	}

	@DeleteMapping("/users/{id}")
	public ResponseEntity<?> deleteUser(@PathVariable long id) {
		return userService.deleteUserId(id);
	}

	// http://localhost:8084/api/users/0/10

	@GetMapping("/users/{offSet}/{pageSize}")
	public ResponseEntity<?> getUsersWithPagination(@PathVariable(name = "offSet") Integer offSet,
			@PathVariable(name = "pageSize") Integer pageSize) {

		return userService.findUsersWithPagination(offSet, pageSize);

	}

	// http://localhost:8082/api/users/sorted/firstName
	@GetMapping("/users/sorted/{field}")
	public ResponseEntity<List<UserDTO>> getUsersSorted(@PathVariable String field) {
		List<UserDTO> sortedUsers = userService.findUsersWithSorting(field);
		return ResponseEntity.ok(sortedUsers);
	}

//  http://localhost:8082/api/users/search?searchKey=6
	@GetMapping("/users/search")
	public ResponseEntity<?> searchUsers(@RequestParam String searchKey) {
		ResponseEntity<?> users = userService.searchUsers(searchKey);
		return ResponseEntity.ok(users);
	}
}
