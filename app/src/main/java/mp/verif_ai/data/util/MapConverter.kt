package mp.verif_ai.data.util

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MapConverter {
    private val gson = Gson()
    private val mapType = object : TypeToken<Map<String, Any>>() {}.type

    @TypeConverter
    fun fromMap(map: Map<String, Any>?): String? {
        return map?.let { gson.toJson(it, mapType) }
    }

    @TypeConverter
    fun toMap(json: String?): Map<String, Any>? {
        return json?.let { gson.fromJson(it, mapType) }
    }
}