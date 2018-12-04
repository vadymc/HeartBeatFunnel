package vadc.heartbeat.listener

import org.slf4j.LoggerFactory
import org.springframework.jms.annotation.JmsListener
import org.springframework.stereotype.Component

/**
 * Temporary class for messaging test. To be moved to HeartBeatPublisher service.
 */
@Component
class NotificationEventJmsListener {
    private val log = LoggerFactory.getLogger(NotificationEventJmsListener::class.java)

    @JmsListener(destination = "notification_events")
    fun processEvent(payload: String) {
        log.info("Received event $payload")
    }
}