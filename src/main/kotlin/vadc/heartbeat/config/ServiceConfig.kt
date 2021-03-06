package vadc.heartbeat.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.MessageListener
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.listener.PatternTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import vadc.heartbeat.controller.filter.ApiKeyFilter
import vadc.heartbeat.service.EventOutProcessor
import vadc.heartbeat.util.EnvironmentContainer

@Configuration
class ServiceConfig {

    @Value("\${hbf.redis.host}")
    private var redisHost: String = ""

    @Value("\${hbf.redis.port}")
    private var redisPort: Int = -1

    @Value("\${hbf.api.key}")
    private lateinit var apiKey: String

    @Value("\${hbf.auth.value}")
    private lateinit var authValue: String

    @Autowired(required = false)
    private var environmentContainer: EnvironmentContainer? = null

    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory {
        if (environmentContainer != null) {
            return JedisConnectionFactory(RedisStandaloneConfiguration(environmentContainer!!.redisHost(), environmentContainer!!.redisPort()))
        }
        return JedisConnectionFactory(RedisStandaloneConfiguration(redisHost, redisPort))
    }

    @Bean
    fun redisTemplate(redisConnectionFactory: RedisConnectionFactory): RedisTemplate<String, Any> {
        return RedisTemplate<String, Any>().apply {
            setConnectionFactory(redisConnectionFactory)
        }
    }

    @Bean
    fun redisMessageListenerContainer(
            redisConnectionFactory: RedisConnectionFactory,
            expiredMessageListener: MessageListener,
            eventOutProcessor: EventOutProcessor
    )
            : RedisMessageListenerContainer {
        return RedisMessageListenerContainer().apply {
            setConnectionFactory(redisConnectionFactory)
            addMessageListener(expiredMessageListener, PatternTopic(expiredEventTopic))
            addMessageListener(eventOutProcessor, ChannelTopic(incomingEventTopic))
        }
    }

    @Bean
    fun filterRegistrationBean(): FilterRegistrationBean<ApiKeyFilter> {
        return FilterRegistrationBean<ApiKeyFilter>().apply {
            filter = ApiKeyFilter(apiKey, authValue)
            addUrlPatterns("/v1/*")
        }
    }

    companion object {
        val incomingEventList = "incoming_event_list"
        val incomingEventTopic = "incoming_event_topic"
        val expiredEventTopic = "__keyevent@*__:expired"
    }
}