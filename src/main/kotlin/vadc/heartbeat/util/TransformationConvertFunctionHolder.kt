package vadc.heartbeat.util

import org.json.JSONObject
import java.util.function.Function

class TransformationConvertFunctionHolder {

    companion object {

        fun convertSonarr(): Function<String, String> {
            return Function { payload ->
                val json = JSONObject(payload)
                val action = json["eventType"]
                val title = json.getJSONObject("series")["title"]
                val season: Any
                val episode: Any
                var note = ""

                val episodeArray = json.getJSONArray("episodes")
                season = (episodeArray.get(0) as JSONObject)["seasonNumber"]
                episode = (episodeArray.get(0) as JSONObject)["episodeNumber"]
                if (episodeArray.length() > 1) {
                    note = " (first in list)"
                }
                return@Function "[$action]$title S${season}E${episode}${note}"
            }
        }

        fun convertRadarr(): Function<String, String> {
            return Function { payload ->
                val json = JSONObject(payload)
                val action = json["eventType"]
                val title = json.getJSONObject("movie")["title"]

                return@Function "[$action]$title"
            }
        }

        fun convertDefault(): Function<String, String> {
            return Function { it }
        }
    }

}