/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorfacades.cart.action.populator;

import de.hybris.platform.commercefacades.order.converters.populator.AbstractOrderPopulator;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.i18n.L10NService;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Converter implementation for {@link de.hybris.platform.core.model.order.CartModel} as source and
 * {@link de.hybris.platform.commercefacades.order.data.CartData} as target type.
 */
public class SavedCartDescriptionPopulator<T extends CartData> extends AbstractOrderPopulator<CartModel, T>
{

	private static final Integer ARG_SIZE_IN_CSV_IMPORTED_SAVE_CART_DESCRIPTION = 4;
	private static final Integer CART_NAME = 0;
	private static final Integer CSV_IMPORT_SUCCESS_ACCOUNT = 1;
	private static final Integer CSV_PARTIAL_IMPORT_ACCOUNT = 2;
	private static final Integer CSV_IMPORT_FAILURE_ACCOUNT = 3;
	private static final Pattern REGEX_TO_FILTER_OUT_NUMBERS = Pattern.compile("\\d+");
	private static final String DESCRIPTION_PREFIX_OF_CSV_IMPORTED_SAVE_CART = "This cart was created by CSV import";
	private L10NService l10nService;
	private String summaryMessageKey;

	@Override
	public void populate(final CartModel source, final T target)
	{
		String sourceDescription = source.getDescription();
		if (StringUtils.isNotEmpty(sourceDescription))
		{
			List<String> values = parseNumbersInDescription(sourceDescription);
			if (sourceDescription.startsWith(DESCRIPTION_PREFIX_OF_CSV_IMPORTED_SAVE_CART)
					&& values.size() == ARG_SIZE_IN_CSV_IMPORTED_SAVE_CART_DESCRIPTION)
			{
				final Object[] localizationArguments =
				{ values.get(CART_NAME), values.get(CSV_IMPORT_SUCCESS_ACCOUNT), values.get(CSV_PARTIAL_IMPORT_ACCOUNT),
						values.get(CSV_IMPORT_FAILURE_ACCOUNT) };
				final String localizedDescription = getL10nService().getLocalizedString(getSummaryMessageKey(),
						localizationArguments);
				target.setDescription(localizedDescription);
			}
			else
			{
				target.setDescription(sourceDescription);
			}

		}
	}

	public L10NService getL10nService()
	{
		return this.l10nService;
	}

	public void setL10nService(L10NService l10nService)
	{
		this.l10nService = l10nService;
	}

	protected String getSummaryMessageKey()
	{
		return summaryMessageKey;
	}

	public void setSummaryMessageKey(final String summaryMessageKey)
	{
		this.summaryMessageKey = summaryMessageKey;
	}

	private List<String> parseNumbersInDescription(final String description)
	{
		List<String> numbersInDescription = new ArrayList<>();
		Matcher m = REGEX_TO_FILTER_OUT_NUMBERS.matcher(description);
		while (m.find())
		{
			numbersInDescription.add(m.group());
		}
		return numbersInDescription;
	}

}
