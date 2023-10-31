/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.services.hooks;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.cpq.productconfig.services.ConfigurationService;
import de.hybris.platform.cpq.productconfig.services.ConfigurationServiceLayerHelper;
import de.hybris.platform.cpq.productconfig.services.model.CloudCPQOrderEntryProductInfoModel;
import de.hybris.platform.order.strategies.ordercloning.CloneAbstractOrderHook;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;


/**
 * Default CPQ implementation of the {@link CloneAbstractOrderHook}.<br>
 * It will ensure that any CPQ configuration is proper handled whenever a hybris document is cloned. In principal it
 * will ensure that the configuration is deep copied, when both, source and traget documemnt continue to exist. So that
 * both configurations are independent from each other. For example this is the case when an order is tranformed into a
 * cart.<br>
 * In case the source document is discarded, no deep copy will take place. Instead the existing configuration is used
 * for the target document, to save the overhead of deep copying a configuration. For example this is the case for the
 * cart to order transformation.
 */
public class DefaultProductConfigCloneAbstractOrderHook implements CloneAbstractOrderHook
{

	private static final String PERFORM_DEEP_COPY = "perform deep copy of cpq configuration for entry";
	private static final String PERFORM_SHALLOW_COPY = "perform shallow copy of cpq configuration id for entry";
	private static final String AFTER_SHALLOW_COPY_IS = " after shallow copy is ";
	private static final String AFTER_DEEP_COPY_IS = " after deep copy is ";
	private static final String IS = " is ";
	private static final String CONFIGURATION_ID_IN_SOURCE_ENTRY = "configuration id in source entry ";
	private static final String CONFIGURATION_ID_IN_TARGET_ENTRY = "configuration id in target entry ";
	private static final Logger LOG = Logger.getLogger(DefaultProductConfigCloneAbstractOrderHook.class.getName());


	private final ConfigurationService cpqService;
	private final ConfigurationServiceLayerHelper serviceLayerHelper;
	private final int itemsStart;
	private final int itemsIncrement;

	/**
	 * Constructor that gets the needed beans. The default clone strategy is injected via setter injection as we might
	 * get circular dependency issues otherwise.
	 *
	 * @param cpqService
	 *           Configuration service
	 * @param serviceLayerHelper
	 * @param itemsStart
	 * @param itemsIncrement
	 */
	public DefaultProductConfigCloneAbstractOrderHook(final ConfigurationService cpqService,
			final ConfigurationServiceLayerHelper serviceLayerHelper, final int itemsStart, final int itemsIncrement)
	{
		super();
		this.cpqService = cpqService;
		this.serviceLayerHelper = serviceLayerHelper;
		this.itemsStart = itemsStart;
		this.itemsIncrement = itemsIncrement;
	}

	@Override
	public void beforeClone(final AbstractOrderModel original, final Class abstractOrderClassResult)
	{
		// not implemented
	}

	@Override
	public <T extends AbstractOrderModel> void afterClone(final AbstractOrderModel original, final T clone,
			final Class abstractOrderClassResult)
	{
		final List<AbstractOrderEntryModel> targetEntries = clone.getEntries();
		final int targetSize = targetEntries != null ? targetEntries.size() : 0;
		if (targetSize > 0)
		{
			final List<AbstractOrderEntryModel> sourceEntries = original.getEntries();
			final int sourceSize = sourceEntries != null ? sourceEntries.size() : 0;
			if (targetSize != sourceSize)
			{
				throw new IllegalStateException(String.format(
						"After cloning target entry list has size '%d', but source entry list has a different size '%d', which is not expected at this time.",
						targetSize, sourceSize));
			}
			final boolean isCloneNeeded = isCloneConfigNeeded(original, abstractOrderClassResult);
			for (int ii = 0; ii < targetEntries.size() && ii < sourceEntries.size(); ii++)
			{
				if (isCloneNeeded)
				{
					// if source document continues to exists, we need to deep copy the configuration, so that source and target are independent
					cloneConfig(original, sourceEntries.get(ii), targetEntries.get(ii));
				}
				else
				{
					// otherwise we can just reuse the existing configuration for the target document
					copyConfig(sourceEntries.get(ii), targetEntries.get(ii));
				}
			}
		}
	}

	@Override
	public void beforeCloneEntries(final AbstractOrderModel original)
	{
		// not implemented
	}

	@Override
	public <T extends AbstractOrderEntryModel> void afterCloneEntries(final AbstractOrderModel original,
			final List<T> clonedEntries)
	{
		// not implemented
	}

	@Override
	public void adjustEntryNumbers(final Map<AbstractOrderEntryModel, Integer> entryNumberMappings)
	{
		if (checkIfEntryNumbersIncrementedByOne(entryNumberMappings.values()))
		{
			updateEntryNumbers(entryNumberMappings);
		}
	}


	protected void updateEntryNumbers(final Map<AbstractOrderEntryModel, Integer> entryNumberMappings)
	{
		entryNumberMappings.replaceAll((entry, entryNumber) -> entry.getEntryNumber() * itemsIncrement + itemsStart);
	}


	protected boolean checkIfEntryNumbersIncrementedByOne(final Collection<Integer> values)
	{
		if (CollectionUtils.isNotEmpty(values))
		{
			final List<Integer> entryNumbers = new ArrayList<>(values);
			entryNumbers.sort(null);
			final int size = entryNumbers.size();
			return entryNumbers.get(size - 1) == (size - 1);
		}
		return false;
	}

	/**
	 * Checks whether a deep copy of the configuration is needed
	 *
	 * @param source
	 *           source abstract order document
	 * @param targetClass
	 *           target abstract order document class
	 * @return is deep copy of configuration needed
	 */
	public boolean isCloneConfigNeeded(final AbstractOrderModel source, final Class<?> targetClass)
	{
		return isCartToCart(source, targetClass) || isQuoteToCart(source, targetClass) || isOrderToCart(source, targetClass)
				|| isQuoteToQuote(source, targetClass);

	}

	protected boolean isCartToCart(final AbstractOrderModel source, final Class<?> targetClass)
	{
		return isSubClass(CartModel.class, targetClass) && source instanceof CartModel;
	}

	protected boolean isQuoteToCart(final AbstractOrderModel source, final Class<?> targetClass)
	{
		return isSubClass(CartModel.class, targetClass) && source instanceof QuoteModel;
	}

	protected boolean isOrderToCart(final AbstractOrderModel source, final Class<?> targetClass)
	{
		return isSubClass(CartModel.class, targetClass) && source instanceof OrderModel;
	}

	protected boolean isQuoteToQuote(final AbstractOrderModel source, final Class<?> targetClass)
	{
		return isSubClass(QuoteModel.class, targetClass) && source instanceof QuoteModel;
	}

	protected boolean isSubClass(final Class<?> baseClass, final Class<?> subClass)
	{
		return baseClass.isAssignableFrom(subClass);
	}

	protected void cloneConfig(final AbstractOrderModel source, final AbstractOrderEntryModel sourceEntry,
			final AbstractOrderEntryModel targetEntry)
	{
		LOG.debug(PERFORM_DEEP_COPY);
		final CloudCPQOrderEntryProductInfoModel cpqInfoTarget = serviceLayerHelper.getCPQInfo(targetEntry); // can handle null input
		final CloudCPQOrderEntryProductInfoModel cpqInfoSource = serviceLayerHelper.getCPQInfo(sourceEntry); // can handle null input
		// if target or source entries are null, the the corresponding info objects are guaranteed to be null as well.
		if (cpqInfoTarget != null && cpqInfoSource != null)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug(CONFIGURATION_ID_IN_TARGET_ENTRY + targetEntry.getEntryNumber() + IS + cpqInfoTarget.getConfigurationId());
				LOG.debug(CONFIGURATION_ID_IN_SOURCE_ENTRY + sourceEntry.getEntryNumber() + IS + cpqInfoSource.getConfigurationId());
			}
			serviceLayerHelper.ensureBaseSiteSetAndExecuteConfigurationAction(source, baseSiteModel -> cpqInfoTarget
					.setConfigurationId(cpqService.cloneConfiguration(cpqInfoSource.getConfigurationId(), true)));
			if (LOG.isDebugEnabled())
			{
				LOG.debug(CONFIGURATION_ID_IN_TARGET_ENTRY + targetEntry.getEntryNumber() + AFTER_DEEP_COPY_IS
						+ cpqInfoTarget.getConfigurationId());
			}
		}
	}



	protected void copyConfig(final AbstractOrderEntryModel sourceEntry, final AbstractOrderEntryModel targetEntry)
	{
		LOG.debug(PERFORM_SHALLOW_COPY);
		final CloudCPQOrderEntryProductInfoModel cpqInfoTarget = serviceLayerHelper.getCPQInfo(targetEntry); // can handle null input
		final CloudCPQOrderEntryProductInfoModel cpqInfoSource = serviceLayerHelper.getCPQInfo(sourceEntry); // can handle null input
		// if target or source entries are null, the the corresponding info objects are guaranteed to be null as well.
		if (cpqInfoTarget != null && cpqInfoSource != null)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug(CONFIGURATION_ID_IN_TARGET_ENTRY + targetEntry.getEntryNumber() + IS + cpqInfoTarget.getConfigurationId());
				LOG.debug(CONFIGURATION_ID_IN_SOURCE_ENTRY + sourceEntry.getEntryNumber() + IS + cpqInfoSource.getConfigurationId());
			}
			cpqInfoTarget.setConfigurationId(cpqInfoSource.getConfigurationId());
			if (LOG.isDebugEnabled())
			{
				LOG.debug(CONFIGURATION_ID_IN_TARGET_ENTRY + targetEntry.getEntryNumber() + AFTER_SHALLOW_COPY_IS
						+ cpqInfoTarget.getConfigurationId());
			}

		}
	}

}
