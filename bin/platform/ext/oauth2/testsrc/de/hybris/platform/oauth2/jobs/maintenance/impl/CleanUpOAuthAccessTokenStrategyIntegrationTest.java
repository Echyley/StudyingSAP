/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.oauth2.jobs.maintenance.impl;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.task.utils.NeedsTaskEngine;
import de.hybris.platform.testframework.PropertyConfigSwitcher;
import de.hybris.platform.webservicescommons.model.OAuthAccessTokenModel;
import de.hybris.platform.webservicescommons.model.OAuthClientDetailsModel;

import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.util.SerializationUtils;

/**
 * Tests the logic for service {@link CleanUpOAuthAccessTokenStrategy} which is responsible for cleaning up OAuth access token.
 */
@IntegrationTest
@NeedsTaskEngine
public class CleanUpOAuthAccessTokenStrategyIntegrationTest extends ServicelayerBaseTest
{
	private static final String ACCESS_TOKEN_VALUE = "accessTokenValue";

	private static final int DAYS = 2;

	@Resource
	private ModelService modelService;

	@Resource
	private ConfigurationService configurationService;

	@Resource
	private FlexibleSearchService flexibleSearchService;

	private CleanUpOAuthAccessTokenStrategy cleanupStrategy;

	private OAuthAccessTokenModel accessToken;

	private final PropertyConfigSwitcher cleanUpTimeSwitch = new PropertyConfigSwitcher("cleanup.cronjob.oauth.access.token.expiry.time.seconds");

	private final PropertyConfigSwitcher validityTimeSwitch = new PropertyConfigSwitcher("oauth2.accessTokenValiditySeconds");

	private DefaultOAuth2AccessToken oauthAccessToken;

	@Before
	public void setUp(){
		cleanupStrategy = new CleanUpOAuthAccessTokenStrategy(modelService, configurationService);
		oauthAccessToken = new DefaultOAuth2AccessToken(ACCESS_TOKEN_VALUE);

		final OAuthClientDetailsModel client = modelService.create(OAuthClientDetailsModel.class);
		client.setClientId(uniqueString());
		accessToken = modelService.create(OAuthAccessTokenModel.class);
		accessToken.setTokenId(uniqueString());
		accessToken.setToken(SerializationUtils.serialize(oauthAccessToken));
		accessToken.setAuthenticationId(uniqueString());
		accessToken.setAuthentication(uniqueString());
		accessToken.setClient(client);
	}

	@After
	public void cleanUp()
	{
		cleanUpTimeSwitch.switchBackToDefault();
	}

	public void createExpiredToken()
	{
		// Making sure that the token is expired
		oauthAccessToken.setExpiration(DateTime.now().minusDays(DAYS).toDate());
		accessToken.setToken(SerializationUtils.serialize(oauthAccessToken));
		modelService.save(accessToken);
	}

	public void createValidToken()
	{
		// Making sure that the token is valid
		oauthAccessToken.setExpiration(DateTime.now().plusDays(DAYS).toDate());
		accessToken.setToken(SerializationUtils.serialize(oauthAccessToken));
		modelService.save(accessToken);
	}

	@Test
	public void shouldRemoveExpiredToken()
	{
		createExpiredToken();
		cleanUpTimeSwitch.switchToValue("0");
		validityTimeSwitch.switchToValue("0");
		List<OAuthAccessTokenModel> token = useCleanUpOAuthAccessTokenStrategy();
		assertThat(token).hasSize(1);
		assertThat(token.get(0).getPk()).isEqualTo(accessToken.getPk());

		cleanupStrategy.process(token);
		token = useCleanUpOAuthAccessTokenStrategy();
		assertThat(token).isEmpty();
	}

	@Test
	public void shouldNotRemoveValidToken()
	{
		createValidToken();
		cleanUpTimeSwitch.switchToValue("0");
		validityTimeSwitch.switchToValue("0");
		List<OAuthAccessTokenModel> token = useCleanUpOAuthAccessTokenStrategy();
		assertThat(token).hasSize(1);
		assertThat(token.get(0).getPk()).isEqualTo(accessToken.getPk());

		cleanupStrategy.process(token);
		token = useCleanUpOAuthAccessTokenStrategy();
		assertThat(token).hasSize(1);
	}

	@Test
	public void shouldNotReturnToken()
	{
		cleanUpTimeSwitch.switchToValue("300");
		validityTimeSwitch.switchToValue("300");
		final List<OAuthAccessTokenModel> token = useCleanUpOAuthAccessTokenStrategy();
		assertThat(token).isEmpty();
	}

	private List<OAuthAccessTokenModel> useCleanUpOAuthAccessTokenStrategy()
	{
			final CronJobModel cronJob = modelService
					.create(CronJobModel.class);
			final FlexibleSearchQuery query = cleanupStrategy.createFetchQuery(cronJob);
			return flexibleSearchService.<OAuthAccessTokenModel>search(query).getResult();
	}

	private String uniqueString()
	{
		return UUID.randomUUID().toString();
	}
}
