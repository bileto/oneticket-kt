package com.bileto.oneticket.util

import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ExtensionContext.Namespace
import org.junit.jupiter.api.extension.ExtensionContext.Store
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.Properties

class ApiCredentialsCallback : BeforeAllCallback {

	override fun beforeAll(context: ExtensionContext) {
		LOG.trace("Loading API credentials")

		val properties = Properties()
		properties.load(javaClass.classLoader.getResourceAsStream("private/api-credentials.properties"))

		LOG.trace("Loaded properties: ${properties.entries.joinToString(";") { "${it.key},${it.value}" }}")

		val store: Store = context.getStore(NAMESPACE)
		store.put(EMAIL, properties[EMAIL])
		store.put(PASSWORD, properties[PASSWORD])
	}

	companion object {
		const val EMAIL = "email"
		const val PASSWORD = "password"

		@JvmStatic val NAMESPACE: Namespace
			get() = Namespace.create(ApiCredentialsCallback::class.java)

		@JvmStatic private val LOG: Logger = LoggerFactory.getLogger(ApiCredentialsCallback::class.java)
	}

}
