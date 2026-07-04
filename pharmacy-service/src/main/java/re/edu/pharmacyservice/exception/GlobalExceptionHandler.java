package re.edu.pharmacyservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import re.edu.pharmacyservice.dto.response.ApiResponseError;


@RestControllerAdvice
public class GlobalExceptionHandler {

    // LỖI KHÔNG TÌM THẤY DỮ LIỆU
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponseError> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ApiResponseError response = ApiResponseError.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .error("Not Found")
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // LỖI FALLBACK
    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ApiResponseError> handleServiceUnavailable(ServiceUnavailableException ex) {

        ApiResponseError errorResponse = ApiResponseError.builder()
                .status(HttpStatus.SERVICE_UNAVAILABLE.value())          // Set mã 503
                .error(HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase()) //  lấy chữ "Service Unavailable"
                .message(ex.getMessage())                                // Lấy câu thông báo từ lúc bạn throw ở Service
                .build();

        // Trả về JSON lỗi kèm theo HTTP Status 503
        return new ResponseEntity<>(errorResponse, HttpStatus.SERVICE_UNAVAILABLE);
    }

    // 3. LỖI HỆ THỐNG
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseError> handleGeneralException(Exception ex) {
        ApiResponseError response = ApiResponseError.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message("Đã xảy ra sự cố trên máy chủ: " + ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
