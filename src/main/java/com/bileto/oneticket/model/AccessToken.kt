package com.bileto.oneticket.model

import com.fasterxml.jackson.annotation.JsonProperty

data class AccessToken (

	val status: String,

	@get:JsonProperty("token_type")
	val tokenType: String,

	@get:JsonProperty("access_token")
	val accessToken: String

)