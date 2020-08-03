package com.example.demo.controllers;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

@RestController
@RequestMapping("/api/user")
public class UserController {

	private Logger logger = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		return ResponseEntity.of(userRepository.findById(id));
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {

		logger.error("Method : Find by username");

		User user = userRepository.findByUsername(username);
		if (Objects.isNull(user)) {
			logger.error("User was not found");
			return ResponseEntity.notFound().build();
		}
		logger.info("User saved successfully");
		return ResponseEntity.ok(user);
	}
	
	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {

		logger.error("Method : Create new user");

		User user = new User();
		user.setUsername(createUserRequest.getUsername());

		Cart cart = new Cart();
		cartRepository.save(cart);
		user.setCart(cart);
		if (createUserRequest.getPassword().length() < 7 || !Objects.equals(createUserRequest.getPassword(),
				createUserRequest.getPasswordConfirm())){
			logger.error("Password and passwordConfirm are either is inferior to 7 characters or are not equals");
			return ResponseEntity.badRequest().build();
		}


		logger.info("Password and asswordConfirm before hashing : \n"
				+ "Password : " + createUserRequest.getPassword() + "\n" + "PasswordConfirm : " + createUserRequest.getPasswordConfirm());

		user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));

		logger.info("Before saving credentials (password Hashed)  : \n"
				+ "Username : " + user.getUsername() + "\n" + "Password : " + user.getPassword());

		userRepository.save(user);

		logger.info("User saved successfully");
		return ResponseEntity.ok(user);
	}
	
}
