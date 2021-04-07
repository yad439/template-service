package com.vedblade.templateservice

import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class SendingService(clientBuilder: WebClient.Builder) {
	private val webClient = clientBuilder.build()

	fun send(endpoint: String, text: String) {
		webClient.post().uri(endpoint)
			.contentType(MediaType.APPLICATION_JSON).bodyValue(MessageDto(text))
			.retrieve().toBodilessEntity().subscribe()
	}

	private class MessageDto(val message: String)
}