package mp.verif_ai.domain.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

data object JsonUtils {
    val gson = Gson()

    fun <T> toJson(data: T): String {
        return gson.toJson(data)
    }

    inline fun <reified T> fromJson(json: String): T {
        return gson.fromJson(json, object : TypeToken<T>() {}.type)
    }

    fun <T> fromJson(json: String, type: Type): T {
        return gson.fromJson(json, type)
    }

    fun mapToJson(map: Map<String, String>): String {
        return gson.toJson(map)
    }

    fun jsonToMap(json: String): Map<String, String> {
        val mapType = object : TypeToken<Map<String, String>>() {}.type
        return gson.fromJson(json, mapType)
    }

    fun listToJson(list: List<String>): String {
        return gson.toJson(list)
    }

    fun jsonToList(json: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(json, listType)
    }
}