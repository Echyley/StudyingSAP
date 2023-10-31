/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.audit;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.user.UserModel;

import org.junit.Before;


@IntegrationTest
public class DBAuditGDPREnabledImplTest extends AbstractDBAuditHandlerTest
{
	@Before
	@Override
	public void setUp()
	{
		super.setUp();
		enableGDPRAudit();
		assertThat(auditEnablementService.isAuditEnabledForType(UserModel._TYPECODE)).isTrue();
	}
}
