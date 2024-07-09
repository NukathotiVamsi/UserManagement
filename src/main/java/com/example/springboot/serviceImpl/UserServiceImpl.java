package com.example.springboot.serviceImpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.springboot.ValidationService.ValidationService;
import com.example.springboot.dto.UserDTO;
import com.example.springboot.dto.UserDao;
import com.example.springboot.entity.User;
import com.example.springboot.exception.InvalidInputException;
import com.example.springboot.exception.InvalidPasswordException;
import com.example.springboot.repository.UserRepository;
import com.example.springboot.response.Response;
import com.example.springboot.response.ResponseMessage;
import com.example.springboot.service.EmailService;
import com.example.springboot.service.UserService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private EmailService emailService;

	@Autowired
	private ValidationService validationService;

	public ResponseEntity<?> saveUserWithRandomPassword(UserDao userDao) {
		Response response = new Response();

		try {
			// Check if email is already present
			Optional<User> findByEmailId = userRepository.findByEmailId(userDao.getEmailId());
			if (findByEmailId.isPresent()) {
				throw new InvalidInputException("EmailId Already Present");
			}

			// Check if phone number is already present
			Optional<User> findByPhoneNo = userRepository.findByPhoneNo(userDao.getPhoneNo());
			if (findByPhoneNo.isPresent()) {
				throw new InvalidInputException("PhoneNo Already Present");
			}

			// Validate email format
			if (!validationService.isValidEmailId(userDao.getEmailId())) {
				throw new InvalidInputException("Invalid EmailId");
			}

			// Validate phone number format
			if (!validationService.isValidPhoneNo(userDao.getPhoneNo())) {
				throw new InvalidInputException("Invalid PhoneNo");
			}

			// Validate password
			validationService.isValidPassword(userDao.getPassword());

			// Check if passwords match
			if (!userDao.getPassword().equals(userDao.getConfirmPassword())) {
				throw new InvalidInputException("ConfirmPassword does not match Original Password");
			}

			// Convert UserDao to User
			User user = new User();
			user.setFirstName(userDao.getFirstName());
			user.setLastName(userDao.getLastName());
			user.setEmailId(userDao.getEmailId());
			user.setPhoneNo(userDao.getPhoneNo());

			// Encode the password
			String encodedPassword = passwordEncoder.encode(userDao.getPassword());
			user.setPassword(encodedPassword);
			user.setConfirmPassword(encodedPassword);

			// Save user to the database
			userRepository.save(user);

			// Send user details email after successful save
			emailService.sendUserDetailsEmail(user);

			// Success response
			response.setStatusCode(HttpStatus.CREATED.value());
			response.setIsError(false);
			response.setResult(new ResponseMessage("User created successfully"));
			return new ResponseEntity<>(response, HttpStatus.CREATED);

		} catch (InvalidInputException | InvalidPasswordException e) {
			// Handle known validation exceptions
			response.setStatusCode(HttpStatus.BAD_REQUEST.value());
			response.setIsError(true);
			response.setResult(new ResponseMessage(e.getMessage()));
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

		} catch (Exception e) {
			// Handle unexpected exceptions
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setIsError(true);
			response.setResult(new ResponseMessage("An unexpected error occurred. Please try again later."));
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<?> getAllUser() {
		Response response = new Response();
		List<User> users = userRepository.findAll();
		List<UserDTO> userDTOs = users.stream().map(user -> new UserDTO(user.getId(), user.getFirstName(),
				user.getLastName(), user.getPhoneNo(), user.getEmailId())).toList();
		response.setStatusCode(200);
		response.setIsError(false);
		response.setResult(userDTOs);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<?> findUserById(Long id) {
		Response response = new Response();

		try {
			Optional<User> user = userRepository.findById(id);
			if (user.isPresent()) {
				UserDTO userDTO = convertToDTO2(user.get());
				response.setStatusCode(200);
				response.setIsError(false);
				response.setResult(userDTO);
				return new ResponseEntity<>(response, HttpStatus.OK);
			} else {
				response.setStatusCode(404);
				response.setIsError(true);
				response.setResult(new ResponseMessage("User not found with id: " + id));
				return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			response.setStatusCode(500);
			response.setIsError(true);
			response.setResult(new ResponseMessage("An unexpected error occurred. Please try again later."));
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private UserDTO convertToDTO2(User user) {
		UserDTO userDTO = new UserDTO();
		userDTO.setId(user.getId());
		userDTO.setFirstName(user.getFirstName());
		userDTO.setLastName(user.getLastName());
		userDTO.setPhoneNo(user.getPhoneNo());
		userDTO.setEmailId(user.getEmailId());

		return userDTO;
	}

	@Override
	public ResponseEntity<?> updateUser(Long id, UserDao userDao) {
		Response response = new Response();
		try {
			Optional<User> existingUser = userRepository.findById(id);
			if (existingUser.isPresent()) {
				User userToUpdate = existingUser.get();

				if (userRepository.findByEmailId(userDao.getEmailId()).isPresent()
						&& !userToUpdate.getEmailId().equals(userDao.getEmailId())) {
					response.setStatusCode(400);
					response.setIsError(true);
					response.setResult(new ResponseMessage("EmailId already exists"));
					return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
				}

				if (userRepository.findByPhoneNo(userDao.getPhoneNo()).isPresent()
						&& !userToUpdate.getPhoneNo().equals(userDao.getPhoneNo())) {
					response.setStatusCode(400);
					response.setIsError(true);
					response.setResult(new ResponseMessage("PhoneNo already exists"));
					return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
				}

				if (!userDao.getPassword().equals(userDao.getConfirmPassword())) {
					response.setStatusCode(400);
					response.setIsError(true);
					response.setResult(new ResponseMessage("Passwords do not match"));
					return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
				}

				// Convert UserDao to User
				// BeanUtils.copyProperties(userDao, userToUpdate, "id", "password",
				// "confirmPassword");

				String encodedPassword = passwordEncoder.encode(userDao.getPassword());
				userToUpdate.setPassword(encodedPassword);
				userToUpdate.setConfirmPassword(encodedPassword);

				userRepository.save(userToUpdate);
				response.setStatusCode(200);
				response.setIsError(false);
				response.setResult(new ResponseMessage("User updated successfully"));
				return new ResponseEntity<>(response, HttpStatus.OK);
			} else {
				response.setStatusCode(404);
				response.setIsError(true);
				response.setResult(new ResponseMessage("User not found with id: " + id));
				return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			response.setStatusCode(500);
			response.setIsError(true);
			response.setResult(new ResponseMessage("An unexpected error occurred. Please try again later."));
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<Response> deleteUserId(long userId) {
		Response response = new Response();
		Optional<User> user = userRepository.findById(userId);
		if (user.isPresent()) {
			userRepository.deleteById(userId);
			response.setStatusCode(200);
			response.setIsError(false);
			response.setResult(new ResponseMessage("User deleted successfully"));
			return new ResponseEntity<>(response, HttpStatus.OK);
		} else {
			response.setStatusCode(404);
			response.setIsError(true);
			response.setResult(new ResponseMessage("User not found with id: " + userId));
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		}
	}

	public List<UserDTO> findUsersWithSorting(String field) {
		Sort sort = Sort.by(field);
		List<User> users = userRepository.findAll(sort);
		return users.stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	private UserDTO convertToDTO(User user) {
		UserDTO userDTO = new UserDTO();
		userDTO.setId(user.getId());
		userDTO.setFirstName(user.getFirstName());
		userDTO.setLastName(user.getLastName());
		userDTO.setPhoneNo(user.getPhoneNo());
		userDTO.setEmailId(user.getEmailId());
		// Add other fields as necessary
		return userDTO;
	}

	@Override
    public ResponseEntity<?> findUsersWithPagination(Integer offSet, Integer pageSize) {
        Response response = new Response();
        try {
            Pageable pageable = PageRequest.of(offSet, pageSize);
            Page<User> paginatedUsers = userRepository.findAll(pageable);

            if (!paginatedUsers.isEmpty()) {
                List<UserDTO> userDTOList = paginatedUsers.stream().map(this::convertToDTO1).collect(Collectors.toList());
                response.setStatusCode(HttpStatus.OK.value());
                response.setIsError(false);
                response.setResult(userDTOList);
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                response.setIsError(true);
                response.setResult(new ResponseMessage("No users found for the given offSet and pageSize"));
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setIsError(true);
            response.setResult(new ResponseMessage("An internal server error occurred while retrieving users with pagination"));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

	private UserDTO convertToDTO1(User user) {
		UserDTO userDTO = new UserDTO();
		userDTO.setId(user.getId());
		userDTO.setFirstName(user.getFirstName());
		userDTO.setLastName(user.getLastName());
		userDTO.setPhoneNo(user.getPhoneNo());
		userDTO.setEmailId(user.getEmailId());
		// Add other fields as necessary
		return userDTO;
	}

	@Override
	public ResponseEntity<?> searchUsers(String searchKey) {
	    Response response = new Response();
	    try {
	        List<UserDTO> users = userRepository.searchUsers(searchKey).stream().map(user -> {
	            UserDTO userDTO = new UserDTO();
	            try {
	                userDTO.setId(user.getId());
	                userDTO.setFirstName(user.getFirstName());
	                userDTO.setLastName(user.getLastName());
	                userDTO.setPhoneNo(user.getPhoneNo());
	                userDTO.setEmailId(user.getEmailId());
	            } catch (Exception e) {
	                // Log the exception and continue with the remaining fields
	                System.err.println("Error mapping user: " + e.getMessage());
	            }
	            return userDTO;
	        }).collect(Collectors.toList());

	        if (users.isEmpty()) {
	            response.setStatusCode(HttpStatus.NOT_FOUND.value());
	            response.setIsError(true);
	            response.setResult(new ResponseMessage("No users found with the search key: " + searchKey));
	            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	        } else {
	            response.setStatusCode(HttpStatus.OK.value());
	            response.setIsError(false);
	            response.setResult(users);
	            return new ResponseEntity<>(response, HttpStatus.OK);
	        }
	    } catch (Exception e) {
	        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
	        response.setIsError(true);
	        response.setResult(new ResponseMessage("An internal server error occurred while searching for users"));
	        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}


}
