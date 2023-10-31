/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.oauth2;

import static com.google.common.collect.ImmutableSet.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.oauth2.model.OAuthAuthorizationCodeModel;
import de.hybris.platform.oauth2.util.SHAGenerator;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.testframework.PropertyConfigSwitcher;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;

@IntegrationTest
public class AuthorizationCodeServiceTest extends ServicelayerTransactionalTest
{
	private static final PropertyConfigSwitcher enableSignatureForAuthorizationCodeSwitcher = new PropertyConfigSwitcher(
			"oauth2.authorizationcode.stored.as.sha.signature");
	private static final PropertyConfigSwitcher authorizationCodeLength = new PropertyConfigSwitcher(
			"oauth2.authorizationcode.length");

	@Resource(name = "oauthAuthorizationCode")
	private AuthorizationCodeService service;
	@Resource
	private FlexibleSearchService flexibleSearchService;

	@After
	public void tearDown()
	{
		enableSignatureForAuthorizationCodeSwitcher.switchBackToDefault();
		authorizationCodeLength.switchBackToDefault();
		service.resetRandomValueStringGenerator();
	}

	@Test
	public void testCorrectCodeUsage() throws Exception
	{
		OAuth2Authentication auth = new OAuth2Authentication(
				new OAuth2Request(null, "clientId", null, true, of("read"), null, "http://localhost:8080", null, null),
				new UsernamePasswordAuthenticationToken("admin", "nimda"));
		service.store("code", auth);
	}

	@Test(expected = RuntimeException.class)
	public void testCodeUsedTwice() throws Exception
	{
		testCorrectCodeUsage();
		testCorrectCodeUsage();
	}

	@Test
	public void testCodeStoredTwice() throws Exception
	{
		testCorrectCodeUsage();
		final OAuth2Authentication auth = service.remove("code");
		final OAuth2Request request = auth.getOAuth2Request();
		assertThat(request.getClientId()).isEqualTo("clientId");
		assertThat(request.getRedirectUri()).isEqualTo("http://localhost:8080");
		final Authentication authentication = auth.getUserAuthentication();
		assertThat(authentication.getPrincipal()).isEqualTo("admin");
		assertThat(authentication.getCredentials()).isEqualTo("nimda");
	}

	@Test(expected = ModelNotFoundException.class)
	public void testRemovedTwice() throws Exception
	{
		testCorrectCodeUsage();
		service.remove("code");
		service.remove("code");
	}

	@Test(expected = ModelNotFoundException.class)
	public void testUseNonExistingCode() throws Exception
	{
		service.remove("doesNotExists");
	}


	private OAuth2Authentication createAuthentication(){
		return new OAuth2Authentication(
				new OAuth2Request(null, "clientId", null, true, of("read"), null, "http://localhost:8080", null, null),
				new UsernamePasswordAuthenticationToken("admin", "nimda"));
	}

	@Test
	public void shouldCreateAndRemoveIfCreatingSignatureForAuthorizationCodeIsEnabled()
	{
		enableSignatureForAuthorizationCodeSwitcher.switchToValue("true");
		service.store("codeToStore", createAuthentication());
		assertThat(findStoredModel(SHAGenerator.generateSHA256Signature("codeToStore"))).isNotNull();
		service.remove("codeToStore");
		assertThatThrownBy(() -> findStoredModel(SHAGenerator.generateSHA256Signature("codeToStore"))).isInstanceOf(
				ModelNotFoundException.class);
	}

	@Test
	public void shouldCreateAndRemoveIfCreatingSignatureForAuthorizationCodeIsDisabled()
	{
		enableSignatureForAuthorizationCodeSwitcher.switchToValue("false");
		service.store("codeToStore", createAuthentication());
		assertThat(findStoredModel("codeToStore")).isNotNull();
		service.remove("codeToStore");
		assertThatThrownBy(() -> findStoredModel("codeToStore")).isInstanceOf(
				ModelNotFoundException.class);
	}

	@Test
	public void shouldCreateAuthorizationCodeWithLengthOfConfiguredValue()
	{
		final int configuredAuthCodeLength = 20;
		authorizationCodeLength.switchToValue(String.valueOf(configuredAuthCodeLength));
		service.resetRandomValueStringGenerator();
		final String authCode = service.createAuthorizationCode(createAuthentication());
		assertThat(authCode).hasSize(configuredAuthCodeLength);
	}

	@Test
	public void shouldCreateAuthorizationCodeWithDefaultValue()
	{
		authorizationCodeLength.switchToValue("4");
		service.resetRandomValueStringGenerator();
		final String authCode = service.createAuthorizationCode(createAuthentication());
		final int defaultAuthCodeLength = 30;
		assertThat(authCode).hasSize(defaultAuthCodeLength);
	}

	private OAuthAuthorizationCodeModel findStoredModel(final String codeToSearch)
	{
		final OAuthAuthorizationCodeModel authCode = new OAuthAuthorizationCodeModel();
		authCode.setCode(codeToSearch);
		return flexibleSearchService.getModelByExample(authCode);
	}

}
