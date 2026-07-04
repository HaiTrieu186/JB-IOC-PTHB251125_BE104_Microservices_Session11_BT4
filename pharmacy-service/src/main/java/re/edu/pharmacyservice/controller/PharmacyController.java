package re.edu.pharmacyservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import re.edu.pharmacyservice.dto.event.ImportEvent;
import re.edu.pharmacyservice.dto.request.MedicineUpdateRequest;
import re.edu.pharmacyservice.dto.response.ApiResponse;
import re.edu.pharmacyservice.entity.Medicine;
import re.edu.pharmacyservice.service.PharmacyServiceImpl;
import tools.jackson.databind.ObjectMapper;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/medicines")
public class PharmacyController {
    private final PharmacyServiceImpl pharmacyService;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    @GetMapping("/{id}")
    private ResponseEntity<?> getMedicineById(
            @PathVariable Long id
    ) {
        Medicine m = pharmacyService.getMedicineById(id);

        ApiResponse<Medicine> response = ApiResponse.success(
                "Lấy thông tin thuốc id: "+id+" thành công!",
                m
        );

        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMedicine(
            @PathVariable Long id,
            @Valid @RequestBody MedicineUpdateRequest request
    ) {
        Medicine updatedMedicine = pharmacyService.updateMedicine(id, request);

        ApiResponse<Medicine> response = ApiResponse.success(
                "Cập nhật thông tin thuốc thành công!",
                updatedMedicine
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/import")
    public ResponseEntity<String> importMedicine() {
            System.out.println("--> [Publisher] Đang xử lý nhập hàng vào kho...");

            // Tạo đối tượng Event
            ImportEvent event = ImportEvent.builder()
                    .type("IMPORT")
                    .message("Đã nhập 100 hộp Panadol")
                    .build();

            // Chuyển Object thành JSON
            String jsonMessage = objectMapper.writeValueAsString(event);

            // Bắn vào Redis Channel tên là "pharmacy-alerts"
            stringRedisTemplate.convertAndSend("pharmacy-alerts", jsonMessage);

            return ResponseEntity.ok("Nhập hàng và gửi thông báo thành công!");
    }

    @PutMapping("/sell/{id}")
    public ResponseEntity<String> sellMedicine(@PathVariable Long id) {
        // Tạo thread 1 đóng vai nhân viên A
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                String rsBuy = pharmacyService.sellMedicine(id);
                System.out.println("Người dùng 1 : " + rsBuy);
            }
        });

        // Tạo thread 2 đóng vai nhân viên B
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                String rsBuy = pharmacyService.sellMedicine(id);
                System.out.println("Người dùng 2 : " + rsBuy);
            }
        });

        // Bấm nút Start cho 2 đứa chạy đua cùng lúc
        thread2.start();
        thread1.start();

        return ResponseEntity.ok("Đã gửi request mua hàng, hãy check log Console!");
    }
}
