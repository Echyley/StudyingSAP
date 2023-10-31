/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.velocity.eval.key;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Preconditions;


public class SplitKeyEvaluator implements KeyEvaluator<String>
{
	private static final String DEFAULT_KEY_SEPARATOR = ".";

	private String separator = DEFAULT_KEY_SEPARATOR;

	@Override
	public String[] eval(final String key)
	{
		Preconditions.checkArgument(StringUtils.isNotBlank(key), "Given key should be not blank");
		return key.split(separator);
	}

	protected String getSeparator()
	{
		return separator;
	}

	@Resource
	@Override
	public void setKeySeparator(final String separator)
	{
		this.separator = separator;
	}
}
