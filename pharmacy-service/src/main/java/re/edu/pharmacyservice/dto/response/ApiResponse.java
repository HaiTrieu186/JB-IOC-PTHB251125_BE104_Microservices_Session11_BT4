package re.edu.pharmacyservice.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Biến nào null thì tự động tàng hình
public class ApiResponse<T> {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    private int status;          // Mã trạng thái (200, 400, 404, 500...)
    //private String error;        // Tên lỗi (vd: "Bad Request", "Not Found" - dùng khi có lỗi)
    private String message;      // Thông báo chi tiết
    private T data;              // Dữ liệu trả về khi thành công
    //private Object errors; // Dùng riêng cho lỗi validation DTO (chứa key-value)



    public static <T> ApiResponse<T> success(String message) {
        return ApiResponse.<T>builder()
                .status(200)
                .message(message)
                .build();
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .status(200)
                .message(message)
                .data(data)
                .build();
    }

//    public static <T> ApiResponse<T> error(int status, String error, String message) {
//        return ApiResponse.<T>builder()
//                .status(status)
//                .error(error)
//                .message(message)
//                .build();
//    }
//
//    public static <T> ApiResponse<T> error(int status, String error, String message, Map<String, String> errors) {
//        return ApiResponse.<T>builder()
//                .status(status)
//                .error(error)
//                .message(message)
//                .errors(errors)
//                .build();
//    }
}
