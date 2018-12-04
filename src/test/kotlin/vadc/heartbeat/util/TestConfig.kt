package vadc.heartbeat.util

import org.mockito.Mockito
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.jms.core.JmsTemplate

@Configuration
@Profile("test")
class TestConfig {

    @Bean
    fun jmsTemplate(): JmsTemplate {
        return Mockito.mock(JmsTemplate::class.java)
    }
}