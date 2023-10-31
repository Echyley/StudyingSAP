/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.velocity.eval.key;

import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Preconditions;



public class CutOffEdgesKeyEvaluator extends SplitKeyEvaluator
{


	@Override
	public String[] eval(final String key)
	{
		Preconditions.checkArgument(StringUtils.isNotBlank(key), "Given key should be not blank");

		final StringTokenizer tokenizerFromStart = new StringTokenizer(key, getSeparator(), true);

		String firstBunch = null;
		String lastBunch = null;
		if (tokenizerFromStart.countTokens() > 1 && tokenizerFromStart.hasMoreTokens()) //first
		{
			firstBunch = tokenizerFromStart.nextToken();
		}
		else
		{
			return new String[] { key };
		}

		final StringTokenizer tokenizerFromEnd = new StringTokenizer(StringUtils.reverse(key), getSeparator(), true);

		if (tokenizerFromEnd.countTokens() > 1 && tokenizerFromEnd.hasMoreTokens()) //first
		{
			lastBunch = StringUtils.reverse(tokenizerFromEnd.nextToken());
		}
		else
		{
			return new String[] { firstBunch, StringUtils.stripStart(key, firstBunch) };
		}

		final String middle = StringUtils.stripEnd(StringUtils.stripStart(key, firstBunch), lastBunch);

		final String middleStripped = StringUtils.stripEnd(StringUtils.stripStart(middle, getSeparator()), getSeparator());

		if (StringUtils.isNotBlank(middleStripped))
		{
			return new String[] { firstBunch, middleStripped, lastBunch };
		}
		else
		{
			return new String[] { firstBunch, lastBunch };
		}
	}
}
