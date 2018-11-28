package vadc.heartbeat

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.redis.core.RedisKeyValueAdapter.EnableKeyspaceEvents.ON_STARTUP
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

@SpringBootApplication
@EnableRedisRepositories(enableKeyspaceEvents = ON_STARTUP)
@EnableTransactionManagement
class HeartBeatFunnelApplication

fun main(args: Array<String>) {
    runApplication<HeartBeatFunnelApplication>(*args)
}
