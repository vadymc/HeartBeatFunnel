package vadc.heartbeat.util

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.testcontainers.containers.GenericContainer

@Component
@Profile("!test")
class TestContainer {

    companion object {
        init {
            val redis: GenericContainer<*> = GenericContainer<Nothing>("redis:5.0.1")
                    .withExposedPorts(6379)
            redis.start()
            System.setProperty("hbf.redis.host", redis.getContainerIpAddress())
            System.setProperty("hbf.redis.port", redis.getFirstMappedPort().toString())
        }
    }
}