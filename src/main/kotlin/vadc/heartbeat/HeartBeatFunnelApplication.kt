package vadc.heartbeat

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.redis.core.RedisKeyValueAdapter.EnableKeyspaceEvents.ON_STARTUP
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories

@SpringBootApplication
@EnableRedisRepositories(enableKeyspaceEvents = ON_STARTUP)
@EnableEncryptableProperties
class HeartBeatFunnelApplication

fun main(args: Array<String>) {
    runApplication<HeartBeatFunnelApplication>(*args)
}
