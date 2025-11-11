package com.ecom.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class OrderAddress {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;


	private String fullName;

	private String email;

	private String mobileNo;

	private String address;

	private String cityOrProvince;
}





