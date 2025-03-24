package com.ronaldosantos.dscatalog.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ronaldosantos.dscatalog.entities.Product;
import com.ronaldosantos.dscatalog.projections.ProductProjection;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
	
	@Query(nativeQuery = true, value = 
			"""
				SELECT * FROM(
				SELECT DISTINCT TB_PRODUCT.ID, TB_PRODUCT.NAME
				FROM TB_PRODUCT
				INNER JOIN TB_PRODUCT_CATEGORY ON TB_PRODUCT.ID = TB_PRODUCT_CATEGORY.PRODUCT_ID
				WHERE(:categoryIds IS NULL OR TB_PRODUCT_CATEGORY.CATEGORY_ID IN :categoryIds)
				AND
				LOWER (TB_PRODUCT.NAME) LIKE LOWER(CONCAT('%',:name,'%'))
				)AS tb_result
			
			""",
			countQuery = 
			"""
				SELECT COUNT (*)
				FROM
				(
				SELECT DISTINCT TB_PRODUCT.ID, TB_PRODUCT.NAME
				FROM TB_PRODUCT
				INNER JOIN TB_PRODUCT_CATEGORY ON TB_PRODUCT.ID = TB_PRODUCT_CATEGORY.PRODUCT_ID
				WHERE(:categoryIds IS NULL OR TB_PRODUCT_CATEGORY.CATEGORY_ID IN :categoryIds)
				AND
				LOWER (TB_PRODUCT.NAME) LIKE LOWER(CONCAT('%',:name,'%'))
				)AS tb_result	
					
				""")
	Page<ProductProjection> searchProducts(List<Long>categoryIds, String name, Pageable pageable);
	
	
	
	@Query("SELECT obj FROM Product obj JOIN FETCH obj.categories WHERE obj.id IN :productIds")
	List<Product> searchProductWithCategories(List<Long> productIds);

}
