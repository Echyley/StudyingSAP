/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.labelproviders;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Test;
import org.springframework.util.Assert;


@UnitTest
public class AddressLabelProviderTest
{
	@Test
	public void blankTest()
	{
		final AddressLabelProvider provider = new AddressLabelProvider();
		Assert.isNull(provider.getIconPath(null));
	}
}
