package re.edu.pharmacyservice.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class MedicineUpdateRequest {

    @NotNull(message = "Giá thuốc không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá thuốc phải lớn hơn 0")
    private BigDecimal price;
;
}