package io.dhruv.weatherwise.utils

import androidx.datastore.core.Serializer
import io.dhruv.weatherwise.data.model.ResponseModal
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream


enum class ConnectionState {
    Available, Unavailable
}

object ResponseModalSerializer : Serializer<ResponseModal>{
    override val defaultValue: ResponseModal
        get() = ResponseModal()

    override suspend fun readFrom(input: InputStream): ResponseModal {
          return try {
              Json.decodeFromString(
                  deserializer = ResponseModal.serializer(),
                  string = input.readBytes().decodeToString()
              )
       }catch (e : Exception){
           e.printStackTrace()
              defaultValue
       }

    }

    override suspend fun writeTo(t: ResponseModal, output: OutputStream) {
       output.write(Json.encodeToString(serializer = ResponseModal.serializer(), value = t).encodeToByteArray())
    }

}