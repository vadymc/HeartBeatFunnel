package vadc.heartbeat.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import vadc.heartbeat.domain.IncomingEvent
import vadc.heartbeat.repository.IncomingEventRepository
import vadc.heartbeat.service.EventInProcessor

@RestController
@RequestMapping("/v1/events")
class EventController {

    @Autowired
    lateinit var eventInProcessor: EventInProcessor

    @Autowired
    lateinit var incomingEventRepository: IncomingEventRepository

    @PostMapping("/")
    fun submitEvent(@RequestBody event: String): IncomingEvent {
        return eventInProcessor.submit(event)
    }

    @GetMapping("/{id}/")
    fun getEvent(@PathVariable("id") id: String): IncomingEvent {
        return incomingEventRepository.findById(id).orElse(null)
    }
}