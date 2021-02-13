package com.shop.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.shop.entities.User;

public interface UserRepo extends JpaRepository<User, Integer> {

	@Query("select u from User u where u.username = :username")
	public User getUserByUserName(@Param("username") String username);

	@Query("select u from User u where u.email = :email")
	public User getUserByEmail(@Param("email") String email);

}
