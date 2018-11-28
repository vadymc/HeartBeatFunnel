package vadc.heartbeat

import io.restassured.RestAssured.given
import io.restassured.filter.log.LogDetail
import org.junit.Test


class HeartBeatFunnelApplicationTests: AbstractIntTest() {

	@Test
	fun testHealth() {
		given(requestSpecification)
				.`when`()
					.get("/actuator/health")
				.then()
					.statusCode(200)
				.log().ifValidationFails(LogDetail.ALL)
	}

}
