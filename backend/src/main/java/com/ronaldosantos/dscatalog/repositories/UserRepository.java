package com.ronaldosantos.dscatalog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ronaldosantos.dscatalog.entities.User;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	
	

}
