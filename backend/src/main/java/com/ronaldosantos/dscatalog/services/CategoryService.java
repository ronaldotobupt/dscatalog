package com.ronaldosantos.dscatalog.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import com.ronaldosantos.dscatalog.dto.CategoryDTO;
import com.ronaldosantos.dscatalog.entities.Category;
import com.ronaldosantos.dscatalog.repositories.CategoryRepository;
import com.ronaldosantos.dscatalog.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;



@Service
public class CategoryService {
	
	@Autowired
	CategoryRepository repository;
	
	@Transactional(readOnly = true)
	public List<CategoryDTO> findAll(){
		List<Category> list = repository.findAll();
		return list.stream().map(x -> new CategoryDTO(x)).toList();
	}
	
	@Transactional(readOnly = true)
	public CategoryDTO findById(@PathVariable Long id) {
		Category dto = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));
		return new CategoryDTO(dto);
		
	}

	@Transactional
	public CategoryDTO insert(CategoryDTO dto) {
		Category entity = new Category();
		entity.setName(dto.getName());
		entity = repository.save(entity);
		return new CategoryDTO(entity);
	}

	@Transactional
	public CategoryDTO update(Long id,CategoryDTO dto) {
		try {
		Category entity = repository.getReferenceById(id);
		entity.setName(dto.getName());
		entity = repository.save(entity);
		return new CategoryDTO(entity);
		}
		catch(EntityNotFoundException e) {
			throw new ResourceNotFoundException("Id não encontrado");
		}
	}
	
	

}
