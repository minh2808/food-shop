package com.ecom.service;

import java.util.List;

import com.ecom.model.OrderRequest;
import com.ecom.model.ProductOrder;

public interface OrderService {

	
	
	public void saveOrder(Integer userid,OrderRequest orderRequest);
	
	public List<ProductOrder> getOrdersByUser(Integer userId);
	
	public ProductOrder updateOrderStatus(Integer id,String status);
	
	/**
	 * Cancel order: restore product stock and remove the order record.
	 */
	public void cancelOrder(Integer id);
	
	public List<ProductOrder> getAllOrders();

}
