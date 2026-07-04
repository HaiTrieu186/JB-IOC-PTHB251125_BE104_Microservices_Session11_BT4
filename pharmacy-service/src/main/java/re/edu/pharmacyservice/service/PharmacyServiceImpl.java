package re.edu.pharmacyservice.service;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import re.edu.pharmacyservice.dto.request.MedicineUpdateRequest;
import re.edu.pharmacyservice.entity.Medicine;
import re.edu.pharmacyservice.exception.ResourceNotFoundException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class PharmacyServiceImpl {
    private static final List<Medicine> medicines;
    private final RedissonClient redissonClient;

    static {
        medicines = new ArrayList<>();
        // Thuốc giảm đau hạ sốt
        medicines.add(new Medicine(
                1L, "MED001", "Paracetamol 500mg", "Vỉ",
                new BigDecimal("15000"),1
        ));

        // Kháng sinh
        medicines.add(new Medicine(
                2L, "MED002", "Amoxicillin 500mg", "Hộp",
                new BigDecimal("55000"),2
        ));

        // Trị dạ dày
        medicines.add(new Medicine(
                3L, "MED003", "Omeprazole 20mg", "Lọ",
                new BigDecimal("120000"),2
        ));

        // Tăng sức đề kháng
        medicines.add(new Medicine(
                4L, "MED004", "Vitamin C 1000mg", "Hộp",
                new BigDecimal("85000"),1
        ));

        // Kháng viêm
        medicines.add(new Medicine(
                5L, "MED005", "Ibuprofen 400mg", "Vỉ",
                new BigDecimal("25000"),1
        ));

        // Chống dị ứng
        medicines.add(new Medicine(
                6L, "MED006", "Cetirizine 10mg", "Vỉ",
                new BigDecimal("18000"),1
        ));
    }

    @Cacheable(value = "medicines", key = "#id")
    public Medicine getMedicineById(Long id) {
        System.out.println("-->  Redis chưa có thuốc với ID: " + id);
        System.out.println("-->  Đang truy vấn database để lấy chi tiết thuốc có ID: " + id);

        return medicines.stream()
                .filter(medicine -> medicine.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Không tìm thấy loại thuốc với ID: " + id));
    }

    @CacheEvict(value = "medicines", key = "#id")
    public Medicine updateMedicine(Long id, MedicineUpdateRequest request) {
        System.out.println("--> [Action] Đang cập nhật thông tin thuốc có ID: " + id);

        for (Medicine m : medicines) {
            if (m.getId().equals(id)) {
                // Cập nhật giá từ DTO
                m.setPrice(request.getPrice());
                System.out.println("--> [Action] Đã cập nhật giá mới thành công!");
                System.out.println("--> [Redis Log] Spring Boot chuẩn bị kích hoạt @CacheEvict để XÓA key 'medicines::" + id + "' khỏi Redis...");
                return m;
            }
        }
        throw new ResourceNotFoundException("Không tìm thấy loại thuốc với ID: " + id);
    }

    public String sellMedicine(Long id) {
        // 1. Khai báo cái ổ khóa cho riêng loại thuốc này
        RLock lock = redissonClient.getLock("lock:medicine:" + id);

        try {
            // 2. Cố gắng lấy khóa (Wait time = 3s, Lease time = 5s)
            boolean isLocked = lock.tryLock(3, 5, TimeUnit.SECONDS);

            if (isLocked) {
                try {
                    // Nếu lấy được khóa, bắt đầu xử lý mua hàng
                    Medicine m = getMedicineById(id);

                    if (m.getQuantity() > 0) {
                        // Giả lập hệ thống xử lý mất 0.5s để dễ thấy sự tranh giành
                        Thread.sleep(500);

                        m.setQuantity(m.getQuantity() - 1);
                        return "Mua thành công! Số lượng còn lại: " + m.getQuantity();
                    } else {
                        return "Hết hàng!";
                    }
                } finally {
                    // Xử lý xong BẮT BUỘC phải trả lại khóa để người khác còn dùng
                    lock.unlock();
                }
            } else {
                return "Hệ thống đang bận, vui lòng thử lại sau!";
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Lỗi gián đoạn hệ thống!";
        }
    }
}
