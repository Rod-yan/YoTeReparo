package com.yotereparo.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yotereparo.dao.UserDao;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	@Autowired
	UserDao dao;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) {
		return UserDetailsImpl.build(dao.getUserById(username));
	}

}