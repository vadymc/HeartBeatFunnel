package vadc.heartbeat

import config.ApiKeyFilter
import io.restassured.RestAssured.given
import org.awaitility.Awaitility.await
import org.awaitility.Duration
import org.awaitility.Duration.FIVE_SECONDS
import org.awaitility.Duration.TEN_SECONDS
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.equalTo
import org.junit.Test
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.jms.core.JmsTemplate
import vadc.heartbeat.config.MessagingConfig
import vadc.heartbeat.domain.IncomingEvent
import vadc.heartbeat.domain.IncomingEvent.State.PROCESSED
import vadc.heartbeat.repository.IncomingEventRepository
import java.util.concurrent.Callable
import java.util.concurrent.TimeUnit

class EventProcessingTest: AbstractIntTest() {

    @Autowired
    private lateinit var incomingEventRepository: IncomingEventRepository

    @Autowired
    private lateinit var jmsTemplate: JmsTemplate

    @Value("\${hbf.api.key}")
    private lateinit var apiKey: String

    @Test
    fun testEventProcessingLifecycle() {
        val body = """{"test":"value"}"""

        // submit new event
        val incomingEvent = given(requestSpecification)
                .given()
                    .header(ApiKeyFilter.apiKey, apiKey)
                    .body(body)
                .`when`()
                    .post("/v1/events/")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("payload", equalTo(body))
                .log().all()
                .extract().body().`as`(IncomingEvent::class.java)
        val eventId = incomingEvent.id

        // get by id submitted event
        given(requestSpecification)
                .`when`()
                    .header(ApiKeyFilter.apiKey, apiKey)
                    .get("/v1/events/{id}/", eventId)
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("payload", equalTo(body))
                .log().all()

        // await event is processed
        await()
                .atMost(FIVE_SECONDS)
                .until(eventProcessingState(eventId), `is`(PROCESSED))

        // await event is expired after successful processing
        await()
                .atMost(TEN_SECONDS)
                .until(doesExist(eventId), `is`(false))

        // verify message was sent out
        verify(jmsTemplate, atLeastOnce())
                .convertAndSend(MessagingConfig.NOTIFICATION_QUEUE, body)
    }

    @Test
    fun testRequestWithoutApiKeyFails() {
        given(requestSpecification)
                .given()
                    .body("testbody")
                .`when`()
                    .post("/v1/events")
                .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value())
    }

    @Test
    fun testEventReprocessing() {
        val body = """{"test":"value2"}"""

        // fail first try of event processing
        `when`(jmsTemplate.convertAndSend(MessagingConfig.NOTIFICATION_QUEUE, body))
                .thenThrow(RuntimeException("Unable to process event"))
                .thenAnswer {  }

        val incomingEvent = given(requestSpecification)
                .given()
                    .header(ApiKeyFilter.apiKey, apiKey)
                    .body(body)
                .`when`()
                    .post("/v1/events/")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("payload", equalTo(body))
                .log().all()
                .extract().body().`as`(IncomingEvent::class.java)
        val eventId = incomingEvent.id

        // await event is processed
        await()
                .atMost(Duration(30, TimeUnit.SECONDS))
                .until(eventProcessingState(eventId), `is`(PROCESSED))

        // await event is expired after successful processing
        await()
                .atMost(Duration(20, TimeUnit.SECONDS))
                .until(doesExist(eventId), `is`(false))

        // verify event was tried to be processed two times
        verify(jmsTemplate, times(2))
                .convertAndSend(MessagingConfig.NOTIFICATION_QUEUE, body)
    }

    private fun eventProcessingState(id: String): Callable<IncomingEvent.State> {
        return Callable { incomingEventRepository.findById(id).get().state }
    }

    private fun doesExist(id: String): Callable<Boolean> {
        return Callable { incomingEventRepository.existsById(id) }
    }
}