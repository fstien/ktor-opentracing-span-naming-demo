package com.github.fstien

import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.response.*
import io.ktor.routing.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    val usGeologicalSurveyClient = USGeologicalSurveyClient()

    routing {
        get("/earthquakes") {
            val earthquakes = usGeologicalSurveyClient.getAll()
            call.respond(HttpStatusCode.OK, earthquakes)
        }
    }
}

