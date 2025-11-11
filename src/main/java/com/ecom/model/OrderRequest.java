package com.ecom.model;

import lombok.Data;

@Data
public class OrderRequest {

	private String fullName;

	private String email;

	private String mobileNo;

	private String address;

	private String cityOrProvince;

	private String paymentType;
}
