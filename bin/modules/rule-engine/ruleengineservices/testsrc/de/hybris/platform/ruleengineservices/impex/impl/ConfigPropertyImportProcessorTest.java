/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.ruleengineservices.impex.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.user.daos.impl.DefaultTitleDao;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class ConfigPropertyImportProcessorTest extends ServicelayerTest
{
	private DefaultTitleDao defaultTitleDao;

	private String DB_USERNAME_KEY = "db.username";
	private String DB_USERNAME_VALUE;

	@Resource
	private FlexibleSearchService flexibleSearchService;

	@Resource
	private ConfigurationService configurationService;

	@Before
	public void prepare()
	{
		defaultTitleDao = new DefaultTitleDao();
		defaultTitleDao.setFlexibleSearchService(flexibleSearchService);
		DB_USERNAME_VALUE = configurationService.getConfiguration().getString(DB_USERNAME_KEY);
	}

	@Test
	public void testSensitivePropertiesBeNotLoadedWhenInBlacklist() throws Exception
	{
		importData("/ruleengineservices/test/impex/testConfigImportProcessor.impex", "utf-8");
		final TitleModel oneWithUserName = defaultTitleDao.findTitleByCode("test1");
		final TitleModel oneWithStrVal = defaultTitleDao.findTitleByCode("test2");
		final TitleModel oneWithPassWdIsSensitive = defaultTitleDao.findTitleByCode("test3");
		final TitleModel oneWithTestNotExistProperty = defaultTitleDao.findTitleByCode("test4");
		Assert.assertEquals(DB_USERNAME_VALUE, oneWithUserName.getName());
		Assert.assertEquals("testone", oneWithStrVal.getName());
		Assert.assertEquals("$config-db.password", oneWithPassWdIsSensitive.getName());
		Assert.assertEquals("$config-property.that.is.not.exist", oneWithTestNotExistProperty.getName());
	}
}
