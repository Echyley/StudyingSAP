/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.cache.relation;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.testframework.PropertyConfigSwitcher;

@IntegrationTest
public class PrincipalGroupRelationScenariosWithoutRelationCacheTest extends PrincipalGroupRelationScenariosTest
{
	private static final String PG_REL_NAME = "PrincipalGroupRelation";
	private final PropertyConfigSwitcher cachePgRelEnabled = new PropertyConfigSwitcher(
			"relation.cache." + PG_REL_NAME + ".enabled");

	@Override
	public void setUp()
	{
		cachePgRelEnabled.switchToValue("false");
	}

	@Override
	public void tearDown()
	{
		cachePgRelEnabled.switchBackToDefault();
	}
}
