package de.simpletactics.wiki.lib.services

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.springframework.stereotype.Component

@Component
class JsonParser(
    private var gson: Gson,
) {

    init {
        val gsonBuilder = GsonBuilder()
        gsonBuilder.setPrettyPrinting()
        gson = gsonBuilder.create()
    }

    fun <T> parsetoJson(objects: T): String {
        return gson.toJson(objects)
    }

    fun <T> parseToClass(json: String, classType: T): T {
        return gson.fromJson(json, classType!!::class.java)
    }

}
