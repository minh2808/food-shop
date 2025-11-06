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
import org.springframework.data.domain.Page;

import com.ecom.model.Category;
import com.ecom.model.Product;
import com.ecom.model.UserDtls;
import com.ecom.model.Comment;
import com.ecom.service.CartService;
import com.ecom.service.CommentService;
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

	@Autowired
	private CommentService commentService;
	
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
		public String product(@PathVariable int id,@RequestParam(defaultValue="0") Integer pageNo , Model m) {
			Product productById = productService.getProductById(id);
			m.addAttribute("product", productById);
	
			Page<Comment> comments = commentService.getCommentsByProduct(id, pageNo, 5);
			m.addAttribute("comments",comments.getContent());
			m.addAttribute("currentPage", pageNo);
			m.addAttribute("totalPages",comments.getTotalPages());
			m.addAttribute("totalComment", comments.getTotalElements());
			return "view_product";
		}
		
		@PostMapping("user/addComment")
		public String addComment(@RequestParam Integer productId,
								@RequestParam String content,
								Principal p,
								HttpSession session){
			if( p == null){
				session.setAttribute("errorMsg", "Please login to comment");
				return "redirect:/signin";
			}
			UserDtls user = userService.getUserByEmail(p.getName());
			Product product = productService.getProductById(productId);
	
			if(product == null){
				session.setAttribute("errorMsg", "Product not found");
				return "redirect:/viewProduct/"+productId;
			}
			Comment comment = new Comment();
			comment.setProduct(product);
			comment.setUser(user);
			comment.setContent(content);
	
			Comment savedComment = commentService.saveComment(comment);
			if(savedComment != null){
				session.setAttribute("succMsg", "Comment added successfully");
			}
			else{
				session.setAttribute("errorMsg", "Failed to add comment");
			}
	
	
			return "redirect:/viewProduct/"+productId;
		}
	
		
		@PostMapping("/user/deleteComment/{id}")
		public String deleteComment(@PathVariable Integer id,
									@RequestParam Integer productId,
									Principal p,
									HttpSession session){
			if(p == null){
				session.setAttribute("errorMsg", "Please login");
				return "redirect:/signin";
			}
			UserDtls user = userService.getUserByEmail(p.getName());
			Boolean deleted = commentService.deleteComment(id, user.getId());
	
			if(deleted){
				session.setAttribute("succMsg", "Comment deleted successfully");
			}
			else{
				session.setAttribute("errorMsg", "Cannot delete message");
			}
			return "redirect:/viewProduct/"+productId;
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
