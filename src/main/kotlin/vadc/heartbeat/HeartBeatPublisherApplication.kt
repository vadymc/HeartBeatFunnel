package vadc.heartbeat

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class HeartBeatPublisherApplication

fun main(args: Array<String>) {
    runApplication<HeartBeatPublisherApplication>(*args)
}
