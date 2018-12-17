package vadc.heartbeat.util

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.testcontainers.containers.GenericContainer

@Component
@Profile("!travis")
class TestContainer: EnvironmentContainer {

    private var redis: GenericContainer<*> = init()

    override fun redisHost(): String {
        return redis.getContainerIpAddress()
    }

    override fun redisPort(): Int {
        return redis.getFirstMappedPort()
    }

    private fun init(): GenericContainer<*> {
        val redis: GenericContainer<*> = GenericContainer<Nothing>("redis:5.0.1")
                .withExposedPorts(6379)
        redis.start()
        return redis
    }
}