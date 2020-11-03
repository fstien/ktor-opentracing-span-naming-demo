package com.github.fstien

import com.zopa.ktor.opentracing.OpenTracingClient
import com.zopa.ktor.opentracing.span
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.apache.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

class EarthquakeClient {
    private val client = HttpClient(Apache) {
        install(JsonFeature) {
            serializer = JacksonSerializer {}
        }

        install(OpenTracingClient)
    }

    private suspend fun getEarthquakes(): List<Earthquake> = span {
        val call: HttpStatement = client.get("http://localhost:8081/earthquakes")

        val earthquakeResponse: List<Earthquake> = call.execute {
            when(it.status) {
                HttpStatusCode.OK -> it.receive()
                else -> throw Exception("Error response received from earquakes.usgs ${it.status}")
            }
        }

        return earthquakeResponse
    }

    suspend fun getLatest(): Earthquake = span {
        val earthquakes = getEarthquakes()
        val latest = earthquakes.first()
        return latest
    }

    suspend fun getBiggest(): Earthquake = span {
        val earthquakes = getEarthquakes()
        val biggest = earthquakes.sortedBy { it.magnitude }.last()
        return biggest
    }

    suspend fun getBiggerThan(threshold: Double): List<Earthquake> = span {
        val earthquakes = getEarthquakes()
        val biggerThan = earthquakes.filter { it.magnitude > threshold }
        return biggerThan
    }
}

data class Earthquake(
    val location: String,
    val magnitude: Double,
    val timeGMT: String
)