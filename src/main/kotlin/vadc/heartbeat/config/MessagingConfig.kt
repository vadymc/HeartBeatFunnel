package vadc.heartbeat.config

import com.amazon.sqs.javamessaging.ProviderConfiguration
import com.amazon.sqs.javamessaging.SQSConnectionFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.jms.annotation.EnableJms
import org.springframework.jms.core.JmsTemplate

@Configuration
@EnableJms
@Profile("!test")
class MessagingConfig {

    @Bean
    fun sqsConnectionFactory(): SQSConnectionFactory {
        return SQSConnectionFactory(ProviderConfiguration())
    }

    @Bean
    fun jmsTemplate(sqsConnectionFactory: SQSConnectionFactory): JmsTemplate {
        return JmsTemplate().apply {
            connectionFactory = sqsConnectionFactory
        }
    }

    companion object {
        val NOTIFICATION_QUEUE = "notification_events"
    }

}