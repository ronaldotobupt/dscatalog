package com.ronaldosantos.dscatalog.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ronaldosantos.dscatalog.entities.Category;
import com.ronaldosantos.dscatalog.repositories.CategoryRepository;

@Service
public class CategoryService {
	
	@Autowired
	CategoryRepository repository;
	
	public List<Category> findAll(){
		return repository.findAll();
	}

}
