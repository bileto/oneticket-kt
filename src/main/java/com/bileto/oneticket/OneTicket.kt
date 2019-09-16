package com.bileto.oneticket

import com.bileto.oneticket.model.AccessToken
import com.bileto.oneticket.model.Carrier
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy.SNAKE_CASE
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpMethod.POST
import org.springframework.http.HttpStatus.OK
import org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.converter.FormHttpMessageConverter
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.util.Assert
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
		this.jsonMapper.propertyNamingStrategy = SNAKE_CASE
		this.jsonMapper.registerModule(JavaTimeModule())
	}

	fun getAccessToken(): AccessToken {
		val url = urlFor("oauth/v2/token")
		val headers = HttpHeaders()
		headers.contentType = APPLICATION_FORM_URLENCODED

		val keys: MultiValueMap<String, String> = with (LinkedMultiValueMap<String, String>()) {
			setAll(mapOf(
				"email" to email,
				"password" to password,
				"grant_type" to "password"
			))

			this
		}

		val requestEntity = HttpEntity(keys, headers)
		val responseEntity = restTemplate.exchange(url, POST, requestEntity, String::class.java)

		 when (responseEntity.statusCode) {
			OK -> {
				val accessToken: AccessToken = jsonMapper.readValue(responseEntity.body, TYPE_ACCESS_TOKEN)
				Assert.isTrue(accessToken.tokenType == "Bearer", "Access Token type ${accessToken.tokenType} is not supported by client")
				return accessToken
			}
			else -> {
				throw RuntimeException("Could not retrieve access token to access OneTicket API")
			}
		}
	}

	fun getCarriers(): List<Carrier> {
		val token = getAccessToken()
		val url = urlFor("carrier", "core")

		val headers = HttpHeaders()
		headers.accept = listOf(APPLICATION_JSON)
		headers.setBearerAuth(token.accessToken)

		val requestEntity = HttpEntity<String>(headers)
		val responseEntity = restTemplate.exchange(url, GET, requestEntity, String::class.java)

		when (responseEntity.statusCode) {
			OK -> {
				val carriers: List<Carrier> = jsonMapper.readValue<List<Carrier>>(responseEntity.body, TYPE_CARRIERS)
				LOG.trace("${carriers.size} read from API")
				return carriers
			}
			else -> {
				throw RuntimeException("Error loading carriers from OneTicket API${responseEntity.headers.get(HTTP_HEADER_ONETICKET_ERROR)?.joinToString(prefix = "; ")}")
			}
		}
	}

	private fun urlFor(localPath: String, module: String? = null): URI = if (module == null) URL("$API_BASE/$localPath").toURI() else URL("$API_BASE/$module/$localPath").toURI()

	companion object {
		const val API_BASE = "https://api.oneticket.cz"

		const val HTTP_HEADER_ONETICKET_ERROR = "X-SJT-Error"

		@JvmStatic
		private val LOG: Logger = LoggerFactory.getLogger(OneTicket::class.java)

		@JvmStatic private val TYPE_ACCESS_TOKEN = object: TypeReference<AccessToken>() {}
		@JvmStatic private val TYPE_CARRIER = object: TypeReference<Carrier>() {}
		@JvmStatic private val TYPE_CARRIERS = object: TypeReference<List<Carrier>>() {}
	}

}
