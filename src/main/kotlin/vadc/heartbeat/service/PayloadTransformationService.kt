package vadc.heartbeat.service

import org.springframework.stereotype.Service
import vadc.heartbeat.util.Transformation

@Service
class PayloadTransformationService {

    fun transform(payload: String): String {
        return Transformation.find(payload)
                .convertFunction
                .apply(payload)
    }
}