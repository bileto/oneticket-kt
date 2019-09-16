package com.bileto.oneticket;

import com.bileto.oneticket.model.AccessToken;
import com.bileto.oneticket.util.ApiCredentialsCallback;
import com.bileto.oneticket.util.ApiCredentialsResolver;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.util.StringUtils.hasText;

@DisplayName("OneTicket API Client tests")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(ApiCredentialsCallback.class)
@ExtendWith(ApiCredentialsResolver.class)
public class OneTicketLiveApiTests {

	private OneTicket client;

	@BeforeAll
	public void setupClient(String email, String password) {
		this.client = new OneTicket(email, password);
	}

	@Test
	@DisplayName("Test for obtaining access token into API")
	public void testGetAccessToken() {
		AccessToken token = this.client.getAccessToken();

		assertNotNull(token, "Token not received");
		assertTrue(hasText(token.getAccessToken()), "Access token not read");
		assertEquals("Bearer", token.getTokenType(), "Access token has different type than Bearer");
	}

}
