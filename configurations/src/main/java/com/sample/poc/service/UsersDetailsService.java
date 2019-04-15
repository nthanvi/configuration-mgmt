package com.sample.poc.service;

import com.sample.poc.repository.UserRepository;
import com.sample.poc.model.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static java.util.Collections.emptyList;

@Service("userDetailsService")
public class UsersDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		AppUser user = Optional.ofNullable(userRepository.findByUsername(username)).orElseThrow(
				() -> new UsernameNotFoundException(String.format("No user found with username '%s'.", username)));
		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
				emptyList());
	}
}