package com.example.springboot.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.springboot.entity.User;


public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmailId(String emailId);

	Optional<User> findByPhoneNo(String phoneNo);

	@Query("SELECT u FROM User u WHERE " + "u.firstName LIKE CONCAT('%',:query, '%')"
			+ "OR u.lastName LIKE CONCAT('%', :query, '%')" + "OR u.phoneNo LIKE CONCAT('%', :query, '%')"
			+ "OR u.emailId LIKE CONCAT('%', :query, '%')")
	List<User> searchUsers(@Param("query") String query);

	

}
