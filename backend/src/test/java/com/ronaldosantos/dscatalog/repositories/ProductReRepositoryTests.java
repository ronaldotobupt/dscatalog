package com.ronaldosantos.dscatalog.repositories;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.ronaldosantos.dscatalog.entities.Product;
import com.ronaldosantos.dscatalog.tests.Factory;

@DataJpaTest
public class ProductReRepositoryTests {
	
	@Autowired
	private ProductRepository repository;
	
	private Long existingId;
	private Long nonExistingId;
	private Long countTotalProducts;
	
	@BeforeEach
	void inciarVariaveis() throws Exception{
		existingId = 1L;
		nonExistingId = 100L;
		countTotalProducts = 25L;
	}
	
	
	@Test
	public void deleteShouldDeleteObjectWhenIdexists() {
		
		repository.deleteById(existingId);
		
		Optional<Product> result = repository.findById(existingId);
		
		Assertions.assertFalse(result.isPresent());
		
	}
	
	@Test
	public void saveShouldPersistWhithAutoincrementWhenIdIsNull() {
		
		Product product = Factory.createProduct();
		product.setId(null);
		
		product = repository.save(product);
		
		Assertions.assertNotNull(product.getId());
		Assertions.assertEquals(countTotalProducts + 1, product.getId());
		
	}
	
	@Test
	public void findByIdShouldSearchObjectWhenIdexists() {
		
		Optional<Product> result = repository.findById(existingId);
		
		Assertions.assertTrue(result.isPresent());
		
	}
	
	@Test
	public void findByIdShouldNotSearchObjectWhenNotIdexists() {
		
		Optional<Product> result = repository.findById(nonExistingId);
		
		Assertions.assertFalse(result.isPresent());
		
	}

}
