package vadc.heartbeat.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import vadc.heartbeat.domain.IncomingEvent

@Repository
interface IncomingEventRepository : CrudRepository<IncomingEvent, String>