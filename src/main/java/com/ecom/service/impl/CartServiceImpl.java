package com.ecom.service.impl;

import java.util.ArrayList;
import java.util.List;


import com.ecom.exception.InsufficientStockException;
import com.ecom.exception.ResourceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.ecom.model.Cart;
import com.ecom.model.Product;
import com.ecom.model.UserDtls;
import com.ecom.repository.CartRepository;
import com.ecom.repository.ProductRepository;
import com.ecom.repository.UserRepository;
import com.ecom.service.CartService;

@Service
public class CartServiceImpl implements CartService {

	@Autowired
	private CartRepository cartRepository;


	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProductRepository productRepository;


	//Thêm/Cập nhật với Kiểm tra Tồn kho
	@Override
	public Cart saveCart(Integer productId, Integer userId) {
		// Tìm người hoặc sản phẩm, nếu không có trả về Exception
		UserDtls userDtls = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người có ID là: " + userId));
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm có ID là: " + productId));

		// Lấy số lượng tồn kho
		Integer stockAvailable = product.getStockQuantity();
		Cart existingCart = cartRepository.findByProductIdAndUserId(productId, userId);
		Cart cartToSave = null;

		// Tạo mới hoặc cập nhật
		if (ObjectUtils.isEmpty(existingCart)) {
			// Thêm mới nếu không tồn tại
			int requestedQuantity = 1;
			checkStockAvailability(stockAvailable, requestedQuantity); // Kiểm tra 1 sản phẩm còn trong kho không
			cartToSave = createNewCart(userDtls, product);
		} else {
			// Nếu tồn tại tăng số lượng
			int newQuantity = existingCart.getQuantity() + 1;
			checkStockAvailability(stockAvailable, newQuantity); // Kiểm tra số lượng sau khi tăng
			cartToSave = updateExistingCart(existingCart, product); // tăng số lượng 
		}

		// Lưu DATABASE
		return cartRepository.save(cartToSave);
	}

	// Lấy tất cả các mục giỏ hàng của một người dùng
	@Override
	public List<Cart> getCartsByUser(Integer userId) {
		// Tìm người dùng
		userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng có ID là: " + userId));

		List<Cart> carts = cartRepository.findByUserId(userId);
		Double totalOrderPrice = 0.0;
		for (Cart c : carts) {
			Double productPrice;
			if (c.getProduct() != null) {
				productPrice = c.getProduct().getDiscountPrice();
			} else {
				productPrice = 0.0;
			}

			Double totalPriceItem = productPrice * c.getQuantity();
			c.setTotalPrice(totalPriceItem);                        // Giá trị của 1 đơn hàng
			totalOrderPrice += totalPriceItem;
		}
		// CẬP NHẬT TỔNG GIÁ ĐƠN HÀNG VÀO TỪNG MỤC (Cho mục đích hiển thị)
		for (Cart c : carts) {
			c.setTotalOrderPrice(totalOrderPrice);
		}
		return carts;
	}

	// Đếm số mục
	@Override
	public Integer getCountCart(Integer userId) {
		userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID là: " + userId));
		return cartRepository.countByUserId(userId);
	}

	// Tăng/Giảm số lượng với Kiểm tra tồn kho --
	@Override
	public void updateQuantity(String sy, Integer cartId) {
		Cart cart = cartRepository.findById(cartId)
				.orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giỏ hàng với ID là: " + cartId));

		int currentQuantity = cart.getQuantity();
		int newQuantity;

		if (sy.equalsIgnoreCase("de")) {     // Giảm số lượng
			newQuantity = currentQuantity - 1;
			if (newQuantity <= 0) {
				cartRepository.delete(cart);            // Xóa
				return;
			}
			else {
				cart.setQuantity(newQuantity);
			}
		}
		else if (sy.equalsIgnoreCase("in")) { // Tăng số lượng
			newQuantity = currentQuantity + 1;
			// Kiểm tra tồn kho trước khi tăng
			Integer stockAvailable = cart.getProduct().getStockQuantity();
			checkStockAvailability(stockAvailable, newQuantity);
			cart.setQuantity(newQuantity);
		}
		else {            // Không phải "in" hoặc "de" thì không cần xử lí
			return;
		}

		// Cập nhật tổng giá và lưu
		double price = cart.getProduct().getDiscountPrice();
		cart.setTotalPrice(newQuantity * price);
		cartRepository.save(cart);
	}

	@Override
    public void updateQuantityByInput(Integer cartId, Integer newQuantity) {
        if (newQuantity == null || newQuantity < 0) {
            return;  				// nhập sai
        }
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giỏ hàng với ID là: " + cartId));

        if (newQuantity == 0) {
            cartRepository.delete(cart);
            return;
        }
        Integer stockAvailable = cart.getProduct().getStockQuantity();
        checkStockAvailability(stockAvailable, newQuantity);
        cart.setQuantity(newQuantity);
        double price = cart.getProduct().getDiscountPrice();
        cart.setTotalPrice(newQuantity * price);
        
        cartRepository.save(cart);
    }
	
	@Override
    public void removeCartItem(Integer cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy mục giỏ hàng với ID là: " + cartId));
        // Thực hiện xóa
        cartRepository.delete(cart);
    }



	
	// HÀM HỖ TRỢ
	// Tạo một mục Cart mới với số lượng mặc định là 1
	private Cart createNewCart(UserDtls user, Product product) {
		Cart cart = new Cart();
		cart.setProduct(product);
		cart.setUser(user);
		cart.setQuantity(1);
		cart.setTotalPrice(1 * product.getDiscountPrice());
		return cart;
	}

	// Cập nhật số lượng và tổng giá cho mục Cart đã tồn tại (Tăng lên 1)
	private Cart updateExistingCart(Cart existingCart, Product product) {
		int newQuantity = existingCart.getQuantity() + 1;
		existingCart.setQuantity(newQuantity);
		existingCart.setTotalPrice(newQuantity * product.getDiscountPrice());
		return existingCart;
	}

	// Kiểm tra xem còn hàng không
	private void checkStockAvailability(Integer stockAvailable, int requestedQuantity) {
		if (stockAvailable == null) {
			throw new InsufficientStockException("Dữ liệu về kho hàng của sản phẩm bị mất hoặc không tồn tại.");
		}
		// Kiểm tra số lượng yêu cầu có vượt quá hàng tồn kho không
		if (requestedQuantity > stockAvailable) {
			throw new InsufficientStockException("Không thể đặt, chỉ còn " + stockAvailable + " trong kho.");
		}
	}

}
