package com.shop.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.shop.dao.UserRepo;
import com.shop.entities.User;

public class UserDetailsServiceImple implements UserDetailsService {

	@Autowired
	private UserRepo userRepo;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	
	User user = this.userRepo.getUserByUserName(username);
	
	if(user == null) {
		throw new UsernameNotFoundException("Couldn't find any user with this name");
	}
	
	CustomUserDetails customUserDetails = new CustomUserDetails(user);
	
	return customUserDetails;
	}
	
	

}
