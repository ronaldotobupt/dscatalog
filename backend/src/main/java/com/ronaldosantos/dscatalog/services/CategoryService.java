package com.ronaldosantos.dscatalog.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import com.ronaldosantos.dscatalog.dto.CategoryDTO;
import com.ronaldosantos.dscatalog.entities.Category;
import com.ronaldosantos.dscatalog.repositories.CategoryRepository;
import com.ronaldosantos.dscatalog.services.exceptions.EntityNotFoundException;



@Service
public class CategoryService {
	
	@Autowired
	CategoryRepository repository;
	
	@Transactional(readOnly = true)
	public List<CategoryDTO> findAll(){
		List<Category> list = repository.findAll();
		return list.stream().map(x -> new CategoryDTO(x)).toList();
	}
	
	public CategoryDTO findById(@PathVariable Long id) {
		Category dto = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada"));
		return new CategoryDTO(dto);
		
	}

}
