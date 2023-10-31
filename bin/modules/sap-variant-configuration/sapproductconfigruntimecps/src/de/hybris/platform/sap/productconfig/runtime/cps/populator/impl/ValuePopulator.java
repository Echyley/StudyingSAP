/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.populator.impl;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.Registry;
import de.hybris.platform.sap.productconfig.runtime.cps.constants.SapproductconfigruntimecpsConstants;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSValue;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.util.Config;

import org.apache.commons.lang.StringUtils;




/**
 * Responsible to populate characteristics
 */
public class ValuePopulator implements Populator<CPSValue, CsticValueModel>
{

	@Override
	public void populate(final CPSValue source, final CsticValueModel target)
	{
		populateCoreAttributes(source, target);

	}

	protected String getEmptyValueConstant()
	{
		if (Registry.hasCurrentTenant())
		{
			// productive mode
			return Config.getString("sapproductconfigruntimecps.emptyValueReplacement",
					SapproductconfigruntimecpsConstants.REPLACE_EMPTY_DOMAIN_VALUE);
		}
		else
		{
			// unit test mode
			return SapproductconfigruntimecpsConstants.REPLACE_EMPTY_DOMAIN_VALUE;
		}
	}

	protected void populateCoreAttributes(final CPSValue source, final CsticValueModel target)
	{
		final String value = source.getValue();

		if (StringUtils.isEmpty(value))
		{
			target.setName(getEmptyValueConstant());
		}
		else
		{
			target.setName(value);

		}
		populateAuthor(source, target);
	}

	protected void populateAuthor(final CPSValue source, final CsticValueModel target)
	{

		final String author = source.getAuthor();
		if (!StringUtils.isEmpty(author))
		{
			// we use first char of Author, User -> 'U', System -> 'S', Default -> 'D'
			final char authorFlag = author.charAt(0);
			switch (authorFlag)
			{
				case 'U':
					target.setAuthor(CsticValueModel.AUTHOR_USER);
					target.setAuthorExternal(CsticValueModel.AUTHOR_EXTERNAL_USER);
					break;
				case 'D':
					target.setAuthor(CsticValueModel.AUTHOR_USER);
					target.setAuthorExternal(CsticValueModel.AUTHOR_EXTERNAL_DEFAULT);
					break;
				case 'S':
					target.setAuthor(CsticValueModel.AUTHOR_SYSTEM);
					target.setAuthorExternal(CsticValueModel.AUTHOR_SYSTEM);
					break;
				default:
					final String authorString = String.valueOf(authorFlag);
					target.setAuthor(authorString);
					target.setAuthorExternal(authorString);
			}
		}
	}
}
