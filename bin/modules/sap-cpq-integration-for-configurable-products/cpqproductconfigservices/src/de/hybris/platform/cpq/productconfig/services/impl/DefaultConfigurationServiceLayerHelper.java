/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.services.impl;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntFunction;

import org.apache.log4j.Logger;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.cpq.productconfig.services.ConfigurationServiceLayerHelper;
import de.hybris.platform.cpq.productconfig.services.model.CloudCPQOrderEntryProductInfoModel;
import de.hybris.platform.order.model.AbstractOrderEntryProductInfoModel;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;


/**
 * default implementation of the {@link ConfigurationServiceLayerHelper}
 */
public class DefaultConfigurationServiceLayerHelper implements ConfigurationServiceLayerHelper
{
	private static final Logger LOG = Logger.getLogger(DefaultConfigurationServiceLayerHelper.class);
	private static final String ANONYMOUS_USER = "anonymous";
	/**
	 * max pages to process
	 */
	public static final int MAXIMUM_PAGES = 10000;
	/**
	 * items per page
	 */
	public static final int PAGE_SIZE = 100;
	private final BaseSiteService baseSiteService;
	private final UserService userService;

	/**
	 * @deprecated: use constructor
	 *              {@link DefaultConfigurationServiceLayerHelper#DefaultConfigurationServiceLayerHelper(BaseSiteService, UserService)}
	 *              instead
	 */
	@Deprecated(since = "2211", forRemoval = true)
	public DefaultConfigurationServiceLayerHelper(final BaseSiteService baseSiteService)
	{
		super();
		this.baseSiteService = baseSiteService;
		this.userService = null;
	}

	/**
	 * @param baseSiteService
	 *           base site service
	 * @param userService
	 *           user service
	 */
	public DefaultConfigurationServiceLayerHelper(final BaseSiteService baseSiteService, final UserService userService)
	{
		super();
		this.baseSiteService = baseSiteService;
		this.userService = userService;
	}



	@Override
	public CloudCPQOrderEntryProductInfoModel getCPQInfo(final AbstractOrderEntryModel targetEntry)
	{
		if (null != targetEntry && null != targetEntry.getProductInfos())
		{
			final AbstractOrderEntryProductInfoModel productInfo = targetEntry.getProductInfos().stream()
					.filter(pInfo -> ConfiguratorType.CLOUDCPQCONFIGURATOR.equals(pInfo.getConfiguratorType())).findFirst()
					.orElse(null);
			return (CloudCPQOrderEntryProductInfoModel) productInfo;
		}
		return null;
	}

	@Override
	public void ensureBaseSiteSetAndExecuteConfigurationAction(final AbstractOrderModel order,
			final Consumer<BaseSiteModel> action)
	{
		final BaseSiteModel previousBaseSite = getBaseSiteService().getCurrentBaseSite();
		boolean baseSiteChanged = false;
		if (order != null)
		{
			final BaseSiteModel baseSiteFromOrder = order.getSite();
			if (baseSiteFromOrder != null)
			{
				getBaseSiteService().setCurrentBaseSite(baseSiteFromOrder, false);
				baseSiteChanged = true;
			}
		}

		try
		{
			action.accept(getBaseSiteService().getCurrentBaseSite());
		}
		finally
		{
			if (baseSiteChanged)
			{
				baseSiteService.setCurrentBaseSite(previousBaseSite, false);
			}
		}
	}

	@Override
	public <T> void processPageWise(final IntFunction<SearchPageData<T>> searchFunction,
			final Consumer<List<T>> searchResultConsumer)
	{
		int currentPage = 0;
		boolean hasTotalResultSizeBeenReached;
		do
		{
			hasTotalResultSizeBeenReached = processPage(searchFunction, currentPage, searchResultConsumer);
			currentPage++;
		}
		while (!hasTotalResultSizeBeenReached && currentPage < MAXIMUM_PAGES);

	}

	protected <T> boolean processPage(final IntFunction<SearchPageData<T>> searchFunction, final int currentPage,
			final Consumer<List<T>> searchResultConsumer)
	{
		final SearchPageData<T> searchPageData = searchFunction.apply(currentPage);
		LOG.info(String.format("Processing search result page %s of %s", searchPageData.getPagination().getCurrentPage() + 1,
				searchPageData.getPagination().getNumberOfPages()));
		searchResultConsumer.accept(searchPageData.getResults());
		return searchPageData.getResults().size() < PAGE_SIZE;
	}

	protected UserModel retrieveUserForAbstractOrderEntry(final AbstractOrderEntryModel entry)
	{
		return entry.getOrder().getUser();
	}

	protected boolean isRelevantForImpersonation(final AbstractOrderEntryModel entry)
	{
		boolean relevant = false;
		final String currenUserUid = getUserService().getCurrentUser().getUid();
		if (ANONYMOUS_USER.equals(currenUserUid) && !(entry instanceof CartEntryModel))
		{
			relevant = true;
		}
		return relevant;
	}

	@Override
	public UserModel retrieveUserForAbstractOrderEntryIfRelevant(final AbstractOrderEntryModel entry)
	{
		if (isRelevantForImpersonation(entry))
		{
			return retrieveUserForAbstractOrderEntry(entry);
		}
		else
		{
			return null;
		}
	}

	protected UserService getUserService()
	{
		return userService;
	}

	public BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}
}
