package com.ecom.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Product;

public interface ProductService {

	public Product saveProduct(Product product);
	
	public List<Product> getAllProducts();
	
	public Boolean deleteProduct(Integer id);
	
	public Product getProductById(Integer id);
    
	// Simple rating endpoint: record a rating (1-5) for a product
	public Product rateProduct(Integer productId, Integer rating);
	
	public Product updateProduct(Product product, MultipartFile file);
	
	public List<Product> getAllProducts(String category);
	
	public List<Product> searchProduct(String ch);

}
