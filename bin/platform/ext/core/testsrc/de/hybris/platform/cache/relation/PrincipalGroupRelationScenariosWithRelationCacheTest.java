/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.cache.relation;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.testframework.PropertyConfigSwitcher;

@IntegrationTest
public class PrincipalGroupRelationScenariosWithRelationCacheTest extends PrincipalGroupRelationScenariosTest
{
	private static final String PG_REL_NAME = "PrincipalGroupRelation";
	private final PropertyConfigSwitcher cacheEnabled = new PropertyConfigSwitcher("relation.cache.enabled");
	private final PropertyConfigSwitcher cachePgRelEnabled = new PropertyConfigSwitcher(
			"relation.cache." + PG_REL_NAME + ".enabled");

	@Override
	public void setUp()
	{
		cacheEnabled.switchToValue("true");
		cachePgRelEnabled.switchToValue("true");
	}

	@Override
	public void tearDown()
	{
		cacheEnabled.switchBackToDefault();
		cachePgRelEnabled.switchBackToDefault();
	}
}
