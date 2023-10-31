/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.oauth2.jobs.maintenance.impl;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.oauth2.model.OAuthAuthorizationCodeModel;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.task.utils.NeedsTaskEngine;
import de.hybris.platform.testframework.PropertyConfigSwitcher;

import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the logic for service {@link CleanUpOAuthAuthorizationCodeStrategy} which is responsible for cleaning up OAuth authorization code.
 */
@IntegrationTest
@NeedsTaskEngine
public class CleanUpOAuthAuthorizationCodeStrategyIntegrationTest extends ServicelayerBaseTest
{
	@Resource
	private ModelService modelService;

	@Resource
	private ConfigurationService configurationService;

	@Resource
	private FlexibleSearchService flexibleSearchService;

	private CleanUpOAuthAuthorizationCodeStrategy cleanupStrategy;

	private OAuthAuthorizationCodeModel authorizationCode;

	private final PropertyConfigSwitcher cleanUpTimeSwitch = new PropertyConfigSwitcher("cleanup.cronjob.oauth.code.expiry.time.minutes");

	@Before
	public void setUp(){
		cleanupStrategy = new CleanUpOAuthAuthorizationCodeStrategy(modelService, configurationService);
		prepareTestData();
	}

	@After
	public void cleanUp()
	{
		cleanUpTimeSwitch.switchBackToDefault();
	}

	public void prepareTestData()
	{
		authorizationCode = modelService.create(OAuthAuthorizationCodeModel.class);
		authorizationCode.setCode(uniqueString());
		authorizationCode.setAuthentication(uniqueString());
		modelService.save(authorizationCode);
	}

	@Test
	public void shouldRemoveCode()
	{
		cleanUpTimeSwitch.switchToValue("0");
		List<OAuthAuthorizationCodeModel> code = useCleanUpOAuthAuthorizationCodeStrategy();
		assertThat(code).hasSize(1);
		assertThat(code.get(0).getPk()).isEqualTo(authorizationCode.getPk());

		cleanupStrategy.process(code);
		code = useCleanUpOAuthAuthorizationCodeStrategy();
		assertThat(code).isEmpty();
	}

	@Test
	public void shouldNotReturnCode()
	{
		cleanUpTimeSwitch.switchToValue("5");
		final List<OAuthAuthorizationCodeModel> code = useCleanUpOAuthAuthorizationCodeStrategy();
		assertThat(code).isEmpty();
	}

	private List<OAuthAuthorizationCodeModel> useCleanUpOAuthAuthorizationCodeStrategy()
	{
			final CronJobModel cronJob = modelService
					.create(CronJobModel.class);
			final FlexibleSearchQuery query = cleanupStrategy.createFetchQuery(cronJob);
			return flexibleSearchService.<OAuthAuthorizationCodeModel>search(query).getResult();
	}

	private String uniqueString()
	{
		return UUID.randomUUID().toString();
	}
}
