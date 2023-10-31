/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.configuration.utility;

import de.hybris.platform.integrationservices.model.IntegrationObjectModel;

import javax.validation.constraints.NotNull;

import org.zkoss.zul.Listitem;

/**
 * Utility class for export configuration editor.
 */
public final class ExportConfigurationUtils
{
	private static final String FRACTION_REGEX = "[0-9][0-9]*/[0-9][0-9]*";

	private ExportConfigurationUtils()
	{
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Creates a listitem
	 *
	 * @param io IntegrationObject that the listitem will be created for
	 * @return Listitem
	 */
	public static Listitem createListitem(final IntegrationObjectModel io)
	{
		return new Listitem(io.getRootItem().getType().getCode(), io);
	}

	/**
	 * Adds the currently selected instance count to the label of the listitem. Removes the count if the amount is zero.
	 *
	 * @param listitem      Listitem who's label will be modified.
	 * @param countFraction Count representing number of selected instances over the total number of instances
	 * @return Listitem
	 */
	public static Listitem appendCountToLabel(final Listitem listitem, final String countFraction)
	{
		String label = clearLabelCount(listitem.getLabel());


		if (countFraction.matches(FRACTION_REGEX))
		{
			if (isNonZeroFraction(countFraction))
			{
				label += " [" + countFraction + "]";
			}
		}
		else
		{
			throw new IllegalArgumentException();
		}

		listitem.setLabel(label);

		return listitem;

	}

	private static boolean isNonZeroFraction(final String countFraction)
	{
		final String[] fractionParts = countFraction.split("/");
		final int numerator = Integer.parseInt(fractionParts[0]);
		final int denominator = Integer.parseInt(fractionParts[1]);

		return numerator > 0 && denominator > 0;
	}

	private static String clearLabelCount(@NotNull final String oldLabel)
	{
		final boolean hasCount = oldLabel.indexOf(' ') != -1;

		return hasCount ? oldLabel.substring(0, oldLabel.indexOf(' ')) : oldLabel;
	}
}
