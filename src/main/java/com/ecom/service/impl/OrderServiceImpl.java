package com.ecom.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import com.ecom.model.Cart;
import com.ecom.model.OrderAddress;
import com.ecom.model.OrderRequest;
import com.ecom.model.ProductOrder;
import com.ecom.repository.CartRepository;
import com.ecom.repository.ProductOrderRepository;
import com.ecom.service.OrderService;
import com.ecom.util.OrderStatus;
import com.ecom.model.Product;

@Service
public class OrderServiceImpl implements OrderService {
	
	@Autowired
	private ProductOrderRepository orderRepository;

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private com.ecom.repository.ProductRepository productRepository;

	@Override
	@Transactional
	public void saveOrder(Integer userid, OrderRequest orderRequest) {

		List<Cart> carts = cartRepository.findByUserId(userid);

		if (carts == null || carts.isEmpty()) {
			return;
		}

		for (Cart cart : carts) {
			// kiểm tra Product có còn trong kho không
			if (cart.getProduct() == null) {
				throw new com.ecom.exception.ResourceNotFoundException("Sản phẩm không tồn tại trong giỏ hàng.");
			}
			Product prod = cart.getProduct();
			Integer stock = prod.getStockQuantity() != null ? prod.getStockQuantity() : 0;
			Integer needed = cart.getQuantity() != null ? cart.getQuantity() : 0;

			// nếu không đủ hàng trong kho thì báo lỗi
			if (stock < needed) {
				throw new com.ecom.exception.InsufficientStockException("Trong kho không đủ sản phẩm: " + prod.getTitle());
			}
			prod.setStockQuantity(stock - needed);
			productRepository.save(prod);

			// Tạo đơn hàng mới
			ProductOrder order = new ProductOrder();
			order.setOrderId(UUID.randomUUID().toString());
			order.setOrderDate(LocalDate.now());
			order.setProduct(cart.getProduct());
			try {
				order.setPrice(cart.getProduct().getDiscountPrice());
			} catch (Exception ex) {
				order.setPrice(0.0);
			}

			order.setQuantity(cart.getQuantity());
			order.setUser(cart.getUser());

			order.setStatus( OrderStatus.IN_PROGRESS.getName());
			order.setPaymentType(orderRequest.getPaymentType());

			try {
				double p = order.getPrice() != null ? order.getPrice() : 0.0;
				int q = order.getQuantity() != null ? order.getQuantity() : 0;
				order.setTotalAmount(p * q);
			} catch (Exception ex) {
				order.setTotalAmount(0.0);
			}

			OrderAddress address = new OrderAddress();

			address.setFullName(orderRequest.getFullName());
			address.setEmail(orderRequest.getEmail());
			address.setMobileNo(orderRequest.getMobileNo());
			address.setAddress(orderRequest.getAddress());
			address.setCityOrProvince(orderRequest.getCityOrProvince());


			order.setOrderAddress(address);

			orderRepository.save(order);

		}

		cartRepository.deleteAll(carts);

	}

	@Override
	public List<ProductOrder> getOrdersByUser(Integer userId) {
		
		List<ProductOrder> orders = orderRepository.findByUserId(userId);
		return orders;
	}

	@Override
	public ProductOrder updateOrderStatus(Integer id, String status) {
		
		Optional<ProductOrder> findById = orderRepository.findById(id);
		if (findById.isPresent()) {
			ProductOrder productOrder = findById.get();
			productOrder.setStatus(status);
			ProductOrder updateOrder=orderRepository.save(productOrder);
			return updateOrder;
		}

		return null;
	}

	@Override
	@Transactional
	public void cancelOrder(Integer id) {
		Optional<ProductOrder> optional = orderRepository.findById(id);
		if (!optional.isPresent()) {
			throw new com.ecom.exception.ResourceNotFoundException("Không tìm thấy đơn hàng để hủy: " + id);
		}

		ProductOrder order = optional.get();
		if (order.getProduct() != null) {
			com.ecom.model.Product prod = order.getProduct();
			Integer stock = prod.getStockQuantity() != null ? prod.getStockQuantity() : 0;
			Integer qty = order.getQuantity() != null ? order.getQuantity() : 0;
			prod.setStockQuantity(stock + qty);
			productRepository.save(prod);
		}

		orderRepository.delete(order);
	}

	@Override
	public List<ProductOrder> getAllOrders() {
		return orderRepository.findAll();
	}

}
