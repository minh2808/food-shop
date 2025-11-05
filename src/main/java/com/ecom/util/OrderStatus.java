package com.ecom.util;

public enum OrderStatus {

    IN_PROGRESS(1, "Đang tiến hành"),
    ORDER_RECEIVED(2, "Đã nhận đơn hàng"),
    PRODUCT_PACKED(3, "Đã đóng gói sản phẩm"),
    OUT_FOR_DELIVERY(4, "Đang giao hàng"),
    DELIVERED(5, "Đã giao hàng"),
    CANCEL(6, "Đã hủy đơn hàng");


    private final Integer id;
    private final String name;

    OrderStatus(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}