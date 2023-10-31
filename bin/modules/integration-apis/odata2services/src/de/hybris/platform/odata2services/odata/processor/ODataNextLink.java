/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.odata2services.odata.processor;

/**
 * @deprecated Please make use of {@link ODataNextLinkBuilder} directly.
 */
@Deprecated (since = "21.05", forRemoval = true)
public class ODataNextLink
{
	private ODataNextLink()
	{
	}

	/**
	 * @deprecated Please use {@link ODataNextLinkBuilder} instead.
	 */
	@Deprecated(since = "21.05", forRemoval = true)
	public static class Builder extends ODataNextLinkBuilder
	{
		Builder()
		{
			super();
		}
	}
}
