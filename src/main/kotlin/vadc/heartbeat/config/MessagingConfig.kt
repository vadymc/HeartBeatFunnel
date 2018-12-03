package vadc.heartbeat.config

import com.amazon.sqs.javamessaging.ProviderConfiguration
import com.amazon.sqs.javamessaging.SQSConnectionFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.jms.annotation.EnableJms
import org.springframework.jms.config.DefaultJmsListenerContainerFactory
import org.springframework.jms.core.JmsTemplate
import org.springframework.jms.support.destination.DynamicDestinationResolver
import javax.jms.Session

@Configuration
@EnableJms
@Profile("!test")
class MessagingConfig {

    @Bean
    fun sqsConnectionFactory(): SQSConnectionFactory {
        return SQSConnectionFactory(ProviderConfiguration())
    }

    @Bean
    fun jmsListenerContainerFactory(sqsConnectionFactory: SQSConnectionFactory): DefaultJmsListenerContainerFactory {
        return DefaultJmsListenerContainerFactory().apply {
            setConnectionFactory(sqsConnectionFactory)
            setDestinationResolver(DynamicDestinationResolver())
            setConcurrency("1-3")
            setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE)
        }
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