package vadc.heartbeat.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import vadc.heartbeat.domain.ShadowIncomingEvent

@Repository
interface ShadowIncomingEventRepository : CrudRepository<ShadowIncomingEvent, String>