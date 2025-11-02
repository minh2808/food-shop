package com.ecom.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException{

    private static final long serialVersionUID = 1L;

    /**
     * Constructor đơn giản nhất, nhận vào một thông báo lỗi (message).
     * Ví dụ: "User not found with ID: 123"
     * * @param message Thông báo chi tiết về lỗi
     */
    public ResourceNotFoundException(String message) {
        // Truyền thông báo lỗi lên lớp cha (RuntimeException)
        super(message);
    }

    // Bạn có thể thêm các constructor khác nếu cần:
    /*
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    */
}
