package vadc.heartbeat.util

interface EnvironmentContainer {

    fun redisHost(): String

    fun redisPort(): Int
}