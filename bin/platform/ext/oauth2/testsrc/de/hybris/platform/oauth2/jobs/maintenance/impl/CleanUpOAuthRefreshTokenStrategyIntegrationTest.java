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
import de.hybris.platform.webservicescommons.model.OAuthClientDetailsModel;
import de.hybris.platform.webservicescommons.model.OAuthRefreshTokenModel;

import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.oauth2.common.DefaultExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.util.SerializationUtils;


/**
 * Tests the logic for service {@link CleanUpOAuthRefreshTokenStrategy} which is responsible for cleaning up OAuth refresh token.
 */
@IntegrationTest
@NeedsTaskEngine
public class CleanUpOAuthRefreshTokenStrategyIntegrationTest extends ServicelayerBaseTest
{
	private static final String REFRESH_TOKEN_VALUE = "refreshTokenValue";

	private static final int DAYS = 2;

	@Resource
	private ModelService modelService;

	@Resource
	private ConfigurationService configurationService;

	@Resource
	private FlexibleSearchService flexibleSearchService;

	private CleanUpOAuthRefreshTokenStrategy cleanupStrategy;

	private OAuthRefreshTokenModel refreshToken;

	private final PropertyConfigSwitcher cleanUpTimeSwitch = new PropertyConfigSwitcher("cleanup.cronjob.oauth.refresh.token.expiry.time.seconds");

	private final PropertyConfigSwitcher validityTimeSwitch = new PropertyConfigSwitcher("oauth2.refreshTokenValiditySeconds");

	private DefaultExpiringOAuth2RefreshToken oauthRefreshToken;

	@Before
	public void setUp(){
		cleanupStrategy = new CleanUpOAuthRefreshTokenStrategy(modelService, configurationService);
		final OAuthClientDetailsModel client = modelService.create(OAuthClientDetailsModel.class);
		client.setClientId(uniqueString());

		refreshToken = modelService.create(OAuthRefreshTokenModel.class);
		refreshToken.setTokenId(uniqueString());
		refreshToken.setToken(SerializationUtils.serialize(oauthRefreshToken));
		refreshToken.setAuthentication(uniqueString());
	}

	@After
	public void cleanUp()
	{
		cleanUpTimeSwitch.switchBackToDefault();
	}

	public void createExpiredToken()
	{
		// Making sure that the token is expired
		oauthRefreshToken = new DefaultExpiringOAuth2RefreshToken(REFRESH_TOKEN_VALUE, DateTime.now().minusDays(DAYS).toDate());
		refreshToken.setToken(SerializationUtils.serialize(oauthRefreshToken));
		modelService.save(refreshToken);
	}

	public void createValidToken()
	{
		// Making sure that the token is valid
		oauthRefreshToken = new DefaultExpiringOAuth2RefreshToken(REFRESH_TOKEN_VALUE, DateTime.now().plusDays(DAYS).toDate());
		refreshToken.setToken(SerializationUtils.serialize(oauthRefreshToken));
		modelService.save(refreshToken);
	}

	@Test
	public void shouldRemoveExpiredToken()
	{
		createExpiredToken();
		cleanUpTimeSwitch.switchToValue("0");
		validityTimeSwitch.switchToValue("0");
		List<OAuthRefreshTokenModel> token = useCleanUpOAuthRefreshTokenStrategy();
		assertThat(token).hasSize(1);
		assertThat(token.get(0).getPk()).isEqualTo(refreshToken.getPk());

		cleanupStrategy.process(token);
		token = useCleanUpOAuthRefreshTokenStrategy();
		assertThat(token).isEmpty();
	}

	@Test
	public void shouldNotRemoveValidToken()
	{
		createValidToken();
		cleanUpTimeSwitch.switchToValue("0");
		validityTimeSwitch.switchToValue("0");
		List<OAuthRefreshTokenModel> token = useCleanUpOAuthRefreshTokenStrategy();
		assertThat(token).hasSize(1);
		assertThat(token.get(0).getPk()).isEqualTo(refreshToken.getPk());

		cleanupStrategy.process(token);
		token = useCleanUpOAuthRefreshTokenStrategy();
		assertThat(token).hasSize(1);
	}

	@Test
	public void shouldNotReturnToken()
	{
		cleanUpTimeSwitch.switchToValue("300");
		validityTimeSwitch.switchToValue("300");
		final List<OAuthRefreshTokenModel> token = useCleanUpOAuthRefreshTokenStrategy();
		assertThat(token).isEmpty();
	}

	private List<OAuthRefreshTokenModel> useCleanUpOAuthRefreshTokenStrategy()
	{
			final CronJobModel cronJob = modelService
					.create(CronJobModel.class);
			final FlexibleSearchQuery query = cleanupStrategy.createFetchQuery(cronJob);
			return flexibleSearchService.<OAuthRefreshTokenModel>search(query).getResult();
	}

	private String uniqueString()
	{
		return UUID.randomUUID().toString();
	}
}
