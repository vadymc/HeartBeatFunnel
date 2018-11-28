package vadc.heartbeat

import io.restassured.RestAssured.given
import io.restassured.filter.log.LogDetail
import org.hamcrest.Matchers.equalTo
import org.junit.Test
import org.awaitility.Awaitility.*
import org.awaitility.Duration.*
import org.hamcrest.Matchers.`is`
import org.springframework.beans.factory.annotation.Autowired
import vadc.heartbeat.domain.IncomingEvent
import vadc.heartbeat.domain.IncomingEvent.State.*
import vadc.heartbeat.repository.IncomingEventRepository
import java.util.concurrent.Callable

class EventProcessingTest: AbstractIntTest() {

    @Autowired
    private lateinit var incomingEventRepository: IncomingEventRepository

    @Test
    fun testEventProcessingLifecycle() {
        val body = """{"test":"value"}"""

        // submit new event
        val incomingEvent = given(requestSpecification)
                .given()
                    .body(body)
                .`when`()
                    .post("/v1/events/")
                .then()
                    .statusCode(200)
                    .body("payload", equalTo(body))
                .log().all()
                .extract().body().`as`(IncomingEvent::class.java)
        val eventId = incomingEvent.id

        // get by id submitted event
        given(requestSpecification)
                .`when`()
                    .get("/v1/events/{id}/", eventId)
                .then()
                    .statusCode(200)
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

    private fun eventProcessingState(id: String): Callable<IncomingEvent.State> {
        return Callable { incomingEventRepository.findById(id).get().state }
    }

    private fun doesExist(id: String): Callable<Boolean> {
        return Callable { incomingEventRepository.existsById(id) }
    }
}