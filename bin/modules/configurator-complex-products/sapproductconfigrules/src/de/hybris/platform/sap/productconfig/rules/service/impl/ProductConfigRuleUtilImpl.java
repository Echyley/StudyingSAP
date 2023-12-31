/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.rules.service.impl;

import de.hybris.platform.sap.productconfig.rules.service.ProductConfigRuleUtil;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;




/**
 * Default implementation of the {@link ProductConfigRuleUtil}.
 */
public class ProductConfigRuleUtilImpl implements ProductConfigRuleUtil
{
	private static final int LOW = 16;
	public static final double LIST_INITIAL_CAPACITY_CALCULATE_DIVISOR = 0.75;
	public static final int LIST_INITIAL_CAPACITY_CALCULATOR_SCALE = 2;

	protected void getPlainCstic(final InstanceModel instance, final List<CsticModel> cstics,
			final Map<String, CsticModel> csticMap)
	{
		if (CollectionUtils.isNotEmpty(instance.getCstics()))
		{
			for (final CsticModel cstic : instance.getCstics())
			{
				if (!csticMap.containsKey(cstic.getName()))
				{
					cstics.add(cstic);
					csticMap.put(cstic.getName(), cstic);
				}
			}
		}
	}

	protected void getPlainCsticFromSubInstance(final List<InstanceModel> subInstances, final List<CsticModel> cstics,
			final Map<String, CsticModel> csticMap)
	{
		if (CollectionUtils.isNotEmpty(subInstances))
		{
			for (final InstanceModel subInstance : subInstances)
			{
				getPlainCstics(subInstance, cstics, csticMap);
			}
		}
	}

	protected void getPlainCstics(final InstanceModel instance, final List<CsticModel> cstics,
			final Map<String, CsticModel> csticMap)
	{
		if (instance != null)
		{
			getPlainCstic(instance, cstics, csticMap);
			getPlainCsticFromSubInstance(instance.getSubInstances(), cstics, csticMap);
		}
	}

	@Override
	public List<CsticModel> getCstics(final ConfigModel source)
	{
		final int size = calculateInitialSize(source);
		final List<CsticModel> cstics = new ArrayList<>(size);
		if (source != null && source.getRootInstance() != null)
		{
			final Map<String, CsticModel> csticMap = new HashMap<>(size);
			getPlainCstics(source.getRootInstance(), cstics, csticMap);
		}
		return cstics;
	}

	@Override
	public Map<String, CsticModel> getCsticMap(final ConfigModel source)
	{
		final int size = calculateInitialSize(source);
		final Map<String, CsticModel> csticMap = new HashMap<>(size);
		if (source != null && source.getRootInstance() != null)
		{
			final List<CsticModel> cstics = new ArrayList<>(size);
			getPlainCstics(source.getRootInstance(), cstics, csticMap);
		}
		return csticMap;
	}

	protected int calculateInitialSize(final ConfigModel source)
	{
		int size = 0;
		if (source != null && source.getRootInstance() != null)
		{
			final List<CsticModel> cstics = source.getRootInstance().getCstics();
			if (cstics != null)
			{
				size = BigDecimal.valueOf(cstics.size()).divide(BigDecimal.valueOf(LIST_INITIAL_CAPACITY_CALCULATE_DIVISOR), LIST_INITIAL_CAPACITY_CALCULATOR_SCALE, RoundingMode.HALF_EVEN)
						.add(BigDecimal.valueOf(1)).intValue();
			}
			if (!source.isSingleLevel() && size < LOW)
			{
				size = LOW;
			}
		}
		return size;
	}

	@Override
	public List<CsticModel> getCsticsForCsticName(final ConfigModel source, final String csticName)
	{
		final List<CsticModel> cstics = new ArrayList<>();
		if (source != null && source.getRootInstance() != null && StringUtils.isNotEmpty(csticName))
		{
			getCsticForInstance(source.getRootInstance(), cstics, csticName);
		}
		return cstics;
	}

	protected void getCsticForInstance(final InstanceModel instance, final List<CsticModel> cstics, final String csticName)
	{
		final CsticModel cstic = instance.getCstic(csticName);
		if (cstic != null)
		{
			cstics.add(cstic);
		}

		if (!instance.getSubInstances().isEmpty())
		{
			for (final InstanceModel subInstance : instance.getSubInstances())
			{
				getCsticForInstance(subInstance, cstics, csticName);
			}
		}
	}

}
