/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.configurablebundlebackoffice.assertions;

import org.assertj.core.api.Assertions;

import com.hybris.backoffice.widgets.advancedsearch.impl.SearchConditionData;


public class BackofficeAssertions extends Assertions
{
	public static SearchConditionDataAssert assertThat(final SearchConditionData actual)
	{
		return SearchConditionDataAssert.assertThat(actual);
	}

}
