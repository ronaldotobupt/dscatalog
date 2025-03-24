package com.ronaldosantos.dscatalog.services;



import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import com.ronaldosantos.dscatalog.dto.CategoryDTO;
import com.ronaldosantos.dscatalog.dto.ProductDTO;
import com.ronaldosantos.dscatalog.entities.Category;
import com.ronaldosantos.dscatalog.entities.Product;
import com.ronaldosantos.dscatalog.projections.ProductProjection;
import com.ronaldosantos.dscatalog.repositories.CategoryRepository;
import com.ronaldosantos.dscatalog.repositories.ProductRepository;
import com.ronaldosantos.dscatalog.services.exceptions.DatabaseException;
import com.ronaldosantos.dscatalog.services.exceptions.ResourceNotFoundException;
import com.ronaldosantos.dscatalog.util.Utils;

import jakarta.persistence.EntityNotFoundException;



@Service
public class ProductService {
	
	@Autowired
	ProductRepository repository;
	
	@Autowired
	CategoryRepository categoryRepository;
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public Page<ProductDTO> findAllPaged(String name, String categoryId, Pageable pageable){
		
		//Criando um vetor de string com as categorias informadas na requisição
//		String [] vet = categoryId.split(",");
		
		//Transformando o vetor em uma coleção do tipo List de string
		//List<String> list = Arrays.asList(vet);
		
		//Transformando uma coleção do Tipo list de string para long
		//List<Long> categoryIds1 = list.stream().map(Long::parseLong).toList();
		
		//Fazendo tudo dentro de uma mesma função
		//List<Long>categoryIds = Arrays.asList(categoryId.split(",")).stream().map(Long::parseLong).toList();
		
		List<Long> categoryIds = Arrays.asList();
		if(!"0".equals(categoryId)) {
			categoryIds = Arrays.asList(categoryId.split(",")).stream().map(Long::parseLong).toList();
		}
		
		//Recebendo uma página com os produtos informados na requisição
		Page<ProductProjection>page = repository.searchProducts(categoryIds, name, pageable); 
		
		//Seperando os Ids recebidos na requisição e transformando eu uma coleção do tipo list de Long
		List<Long>productIds = page.map(x -> x.getId()).toList();
		
		//Buscando no banco os produtos e suas categorias, passando a lista de ids recebida na requisição
		List<Product> entities = repository.searchProductWithCategories(productIds);
		
		entities = (List<Product>) Utils.replace(page.getContent(),entities);
		
		List<ProductDTO> dtos = entities.stream().map(p -> new ProductDTO(p, p.getCategories())).toList();
		
		//Transformando o DTO em página
		Page<ProductDTO> pageDto = new PageImpl<>(dtos, page.getPageable(), page.getTotalElements());
		
		
		return pageDto;
	}
	
	@Transactional(readOnly = true)
	public ProductDTO findById(@PathVariable Long id) {
		Product dto = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));
		return new ProductDTO(dto, dto.getCategories());
		
	}

	@Transactional
	public ProductDTO insert(ProductDTO dto) {
		Product entity = new Product();
		copyDtoToEntity(dto, entity);
		entity = repository.save(entity);
		return new ProductDTO(entity);
	}

	@Transactional
	public ProductDTO update(Long id,ProductDTO dto) {
		try {
		Product entity = repository.getReferenceById(id);
		copyDtoToEntity(dto, entity);
		entity = repository.save(entity);
		return new ProductDTO(entity);
		}
		catch(EntityNotFoundException e) {
			throw new ResourceNotFoundException("Id não encontrado");
		}
	}

	@Transactional(propagation = Propagation.SUPPORTS)
	public void delete(Long id) {
		if (!repository.existsById(id)) {
			throw new ResourceNotFoundException("Database error");
		}
		try {
	        	repository.deleteById(id);    		
		}
	    	catch (DataIntegrityViolationException e) {
	        	throw new DatabaseException("Falha de integridade referencial");
	   	}
	}
	
	
	@Transactional(readOnly = true)
	public Page<ProductDTO> findAllPaged(Pageable pagealbe ) {
		Page<Product> result = repository.findAll(pagealbe);
		return result.map(x -> new ProductDTO(x));
	}
	
	private void copyDtoToEntity(ProductDTO dto, Product entity) {
		entity.setDate(dto.getDate());
		entity.setDescription(dto.getDescription());
		entity.setName(dto.getName());
		entity.setImgUrl(dto.getImgUrl());
		entity.setPrice(dto.getPrice());
		
		entity.getCategories().clear();
		for(CategoryDTO catDto : dto.getCategories()) {
			Category category = categoryRepository.getReferenceById(catDto.getId());
			entity.getCategories().add(category);
		}
		
	}

	

}
