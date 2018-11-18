package vadc.heartbeat

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class HeartBeatFunnelApplication

fun main(args: Array<String>) {
    runApplication<HeartBeatFunnelApplication>(*args)
}
