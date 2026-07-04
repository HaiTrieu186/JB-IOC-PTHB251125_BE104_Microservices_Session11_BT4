package re.edu.pharmacyservice.entity;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Medicine implements Serializable {
    private Long id;
    private String code;
    private String name;
    private String unit;
    private BigDecimal price;
    private int quantity;
}