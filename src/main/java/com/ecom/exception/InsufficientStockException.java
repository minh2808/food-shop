package com.ecom.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST) // Đặt mã trạng thái HTTP là 400
public class InsufficientStockException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    /**
     * Constructor nhận vào thông báo lỗi chi tiết.
     * @param message Thông báo chi tiết về lỗi thiếu hàng
     */

    public InsufficientStockException(String message) {
        // Truyền thông báo lỗi lên lớp cha (RuntimeException)
        super(message);
    }
}