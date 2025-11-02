package com.ecom.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.ecom.model.Product;
import com.ecom.model.UserDtls;
import com.ecom.repository.UserRepository;
import com.ecom.service.UserService;



@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public UserDtls saveUser(UserDtls user) {
		 
		user.setRole("ROLE_USER");
		
		
		String encodePassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodePassword);
		UserDtls saveUser=userRepository.save(user);
		return saveUser;
	}

	@Override
	public UserDtls getUserByEmail(String email) {
		
		return userRepository.findByEmail(email);
	}


	@Override
	public List<UserDtls> getUsers(String role) {
		return userRepository.findByRole(role);
	}

	@Override
	public Boolean deleteUserDtls(Integer id) {
		UserDtls user = userRepository.findById(id).orElse(null);
		if (!ObjectUtils.isEmpty(user)) {
			userRepository.delete(user);
			return true;
		}
		return false;
	}

	@Override
	public UserDtls saveAdmin(UserDtls user) {
         user.setRole("ROLE_ADMIN");
		
		
		String encodePassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodePassword);
		UserDtls saveUser=userRepository.save(user);
		return saveUser;
	}

	@Override
	public Boolean existsEmail(String email) {
		
		return userRepository.existsByEmail(email);
	}

}
