package vadc.heartbeat.util

import vadc.heartbeat.util.TransformationCheckFunctionHolder.Companion.isDefault
import vadc.heartbeat.util.TransformationCheckFunctionHolder.Companion.isRadarr
import vadc.heartbeat.util.TransformationCheckFunctionHolder.Companion.isSonarr
import vadc.heartbeat.util.TransformationConvertFunctionHolder.Companion.convertDefault
import vadc.heartbeat.util.TransformationConvertFunctionHolder.Companion.convertRadarr
import vadc.heartbeat.util.TransformationConvertFunctionHolder.Companion.convertSonarr
import java.util.function.Function

enum class Transformation(val checkFunction: Function<String, Boolean>, val convertFunction: Function<String, String>) {
    SONARR(isSonarr(), convertSonarr()),
    RADARR(isRadarr(), convertRadarr()),
    DEFAULT(isDefault(), convertDefault())
    ;

    companion object {
        fun find(payload: String): Transformation {
            return values().first { it.checkFunction.apply(payload) }
        }
    }
}