package re.edu.dashboardservice;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import tools.jackson.databind.ObjectMapper;

@Configuration
public class RedisPubSubConfig {

    @Bean
    public RedisMessageListenerContainer container(
            RedisConnectionFactory redisConnectionFactory,
            MessageListenerAdapter alertListenerAdapter
    ) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);

        // Đăng ký nghe channel "pharmacy-alerts"
        container.addMessageListener(alertListenerAdapter, new PatternTopic("pharmacy-alerts"));
        return container;
    }

    @Bean
    public MessageListenerAdapter alertListenerAdapter(PharmacyAlertReceiver receiver) {
        return new MessageListenerAdapter(receiver, "handleImportAlert");
    }
}
