package com.bileto.oneticket

import com.bileto.oneticket.model.AccessToken
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.converter.FormHttpMessageConverter
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import java.net.URI
import java.net.URL
import java.nio.charset.StandardCharsets

class OneTicket {

	private val email: String
	private val password: String
	private val restTemplate: RestTemplate
	private val jsonMapper: ObjectMapper

	constructor(email: String, password: String) {
		this.email = email
		this.password = password
		this.restTemplate = RestTemplate(mutableListOf<HttpMessageConverter<*>>(
			FormHttpMessageConverter(),
			StringHttpMessageConverter(StandardCharsets.UTF_8)
		))
		this.jsonMapper = jacksonObjectMapper()
	}

	fun getAccessToken(): AccessToken {
		val url = urlFor("oauth/v2/token")
		val headers = HttpHeaders()
		headers.contentType = MediaType.APPLICATION_FORM_URLENCODED

		val keys: MultiValueMap<String, String> = with (LinkedMultiValueMap<String, String>()) {
			setAll(mapOf(
				"email" to email,
				"password" to password,
				"grant_type" to "password"
			))

			this
		}

		val requestEntity = HttpEntity(keys, headers)
		val responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String::class.java)

		return when (responseEntity.statusCode) {
			HttpStatus.OK -> {
				jsonMapper.readValue(responseEntity.body, TYPE_ACCESS_TOKEN)
			}
			else -> {
				throw RuntimeException("Could not retrieve access token to access OneTicket API")
			}
		}
	}

	private fun urlFor(localPath: String, module: String? = null): URI = if (module == null) URL("$API_BASE/$localPath").toURI() else URL("$API_BASE/$module/$localPath").toURI()

	companion object {
		const val API_BASE = "https://api.oneticket.cz"

		@JvmStatic
		private val LOG: Logger = LoggerFactory.getLogger(OneTicket::class.java)

		@JvmStatic private val TYPE_ACCESS_TOKEN = object: TypeReference<AccessToken>() {}
	}

}
