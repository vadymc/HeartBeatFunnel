package vadc.heartbeat

import config.ApiKeyFilter
import io.restassured.RestAssured.given
import org.awaitility.Awaitility.await
import org.awaitility.Duration.FIVE_SECONDS
import org.awaitility.Duration.TEN_SECONDS
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.equalTo
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import vadc.heartbeat.domain.IncomingEvent
import vadc.heartbeat.domain.IncomingEvent.State.PROCESSED
import vadc.heartbeat.repository.IncomingEventRepository
import java.util.concurrent.Callable

class EventProcessingTest: AbstractIntTest() {

    @Autowired
    private lateinit var incomingEventRepository: IncomingEventRepository

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

    private fun eventProcessingState(id: String): Callable<IncomingEvent.State> {
        return Callable { incomingEventRepository.findById(id).get().state }
    }

    private fun doesExist(id: String): Callable<Boolean> {
        return Callable { incomingEventRepository.existsById(id) }
    }
}