package vadc.heartbeat.util

import java.util.function.Function

class TransformationCheckFunctionHolder {

    companion object {
        fun isSonarr(): Function<String, Boolean> {
            return Function { payload ->
                return@Function isPotentialJson(payload) && payload.contains("series")
            }
        }

        fun isRadarr(): Function<String, Boolean> {
            return Function { payload ->
                return@Function isPotentialJson(payload) && payload.contains("movie")
            }
        }

        fun isDefault(): Function<String, Boolean> {
            return Function { true }
        }

        private fun isPotentialJson(string: String): Boolean {
            return string.startsWith('{') && string.endsWith('}')
        }
    }
}