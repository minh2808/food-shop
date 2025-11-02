package com.ecom.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ecom.model.Category;
import com.ecom.model.Product;
import com.ecom.model.UserDtls;
import com.ecom.service.CartService;
import com.ecom.service.CategoryService;
import com.ecom.service.ProductService;
import com.ecom.service.UserService;
//import com.ecom.util.CommonUtil;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;



@Controller
public class HomeController {
	
	@Autowired
	private CategoryService categoryService;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private CartService cartService;
	
//	@Autowired
//	private CommonUtil commonUtil;
//	
	
	@ModelAttribute
	public void getUserDetails(Principal p, Model m) {
		if (p != null) {
			String email = p.getName();
			UserDtls userDtls = userService.getUserByEmail(email);
			m.addAttribute("user", userDtls);
			Integer countCart = cartService.getCountCart(userDtls.getId());
			m.addAttribute("countCart", countCart);
		}
		List<Category> allCategory = categoryService.getAllCategory();
		m.addAttribute("categorys", allCategory);
	}
   
	@GetMapping("/")
	public String index(Model m) {
		List<Category> allCategory = categoryService.getAllCategory().stream()
			.sorted((c1,c2)->c2.getId().compareTo(c1.getId()))
				.limit(4).toList();
	
		List<Product> allProducts = productService.getAllProducts("").stream()
				.sorted((p1,p2)->p2.getId().compareTo(p1.getId()))
				.limit(8).toList();
		m.addAttribute("category", allCategory);
		m.addAttribute("products", allProducts);
		
		return "index";
	}
	
	@GetMapping("/signin")
	public String login() {
		return "login";
	}

	@GetMapping("/register")
	public String register() {
		return "register";
	}
	
	
	@GetMapping("/products")
	public String products(Model m, @RequestParam(value = "category", defaultValue = "") String category) {
		// System.out.println("category="+category);
		List<Category> categories = categoryService.getAllCategory();
		List<Product> products = productService.getAllProducts(category);
		m.addAttribute("categories", categories);
		m.addAttribute("products", products);
		m.addAttribute("paramValue", category);
		
		return "product";
	}
	
	@GetMapping("/search")
	public String searchProduct(@RequestParam String ch, Model m) {
		List<Product> searchProducts = productService.searchProduct(ch);
		m.addAttribute("products", searchProducts);
     	List<Category> categories = categoryService.getAllCategory();
        m.addAttribute("categories", categories);
		return "product";

	}
	    
	
	
	@GetMapping("/viewProduct/{id}")
	public String product(@PathVariable int id, Model m) {
		Product productById = productService.getProductById(id);
		m.addAttribute("product", productById);
		System.out.println(productById.getImage());
		return "view_product";
	}
	
	
	
	
	@PostMapping("/saveUser")
	public String saveUser(@ModelAttribute UserDtls user, HttpSession session)
			throws IOException {
		
		Boolean existsEmail = userService.existsEmail(user.getEmail());
		
		if (existsEmail) {
			session.setAttribute("errorMsg", "Email already exist");
		} else {
			UserDtls saveUser = userService.saveUser(user);

			if (!ObjectUtils.isEmpty(saveUser)) {
				
				session.setAttribute("succMsg", "Register successfully");
			} else {
				session.setAttribute("errorMsg", "something wrong on server");
			}
		}
		

		return "redirect:/register";
	}

	
	@GetMapping("/admin")
	public String admin() {
		return "admin";
	}
	
	
	
	
	
} 
