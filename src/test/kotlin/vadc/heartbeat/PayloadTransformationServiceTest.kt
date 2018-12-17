package vadc.heartbeat

import org.hamcrest.Matchers.`is`
import org.junit.Assert.assertThat
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import vadc.heartbeat.service.PayloadTransformationService

class PayloadTransformationServiceTest: AbstractIntTest() {

    @Autowired
    private lateinit var payloadTransformationService: PayloadTransformationService

    @Test
    fun testSonarrTransformationSingleEpisode() {
        val result = payloadTransformationService.transform(load("sonarr_payload_one_episode.json"))

        assertThat(result, `is`("[Download]Gravity Falls S2E14"))
    }

    @Test
    fun testSonarrTransformationTwoEpisodes() {
        val result = payloadTransformationService.transform(load("sonarr_payload_two_episodes.json"))

        assertThat(result, `is`("[Download]Gravity Falls S2E14 (first in list)"))
    }

    @Test
    fun testRadarrTransformation() {
        val result = payloadTransformationService.transform(load("radarr_payload.json"))

        assertThat(result, `is`("[Download]Finding Nemo"))
    }

    @Test
    fun testDefaultTransformation() {
        val payload = "not json event"
        val result = payloadTransformationService.transform(payload)

        assertThat(result, `is`(payload))
    }
}