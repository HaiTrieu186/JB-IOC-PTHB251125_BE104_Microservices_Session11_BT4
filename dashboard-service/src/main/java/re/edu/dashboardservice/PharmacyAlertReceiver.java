package re.edu.dashboardservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@Slf4j
@RequiredArgsConstructor
public class PharmacyAlertReceiver {
    private final ObjectMapper objectMapper;

    public void handleImportAlert(String message) {


        try {
            System.out.println(" ======= THÔNG BÁO DASHBOARD QUẢN LÝ =======");
            System.out.println("TYPE : IMPORT");
            System.out.println("Nội dung (JSON): "+ message);


            // Dùng ObjectMapper
            ImportEvent event = objectMapper.readValue(message, ImportEvent.class);
            System.out.println("Objec nhận được: "+ event);
            System.out.println("Loại sự kiện: " + event.getType());
            System.out.println("Nội dung chi tiết: " + event.getMessage());

        } catch (Exception e) {
            System.out.println("Lỗi khi parse JSON: " + e.getMessage());
        }
    }
}
