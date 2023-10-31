/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicefacades.customer360.providers;

import static de.hybris.platform.assistedservicefacades.constants.AssistedservicefacadesConstants.CART_DESCRIPTION;
import static de.hybris.platform.assistedservicefacades.constants.AssistedservicefacadesConstants.CART_TEXT;
import static de.hybris.platform.assistedservicefacades.constants.AssistedservicefacadesConstants.ORDER_DESCRIPTION;
import static de.hybris.platform.assistedservicefacades.constants.AssistedservicefacadesConstants.ORDER_TEXT;
import static de.hybris.platform.assistedservicefacades.constants.AssistedservicefacadesConstants.SAVED_CART_TEXT;

import de.hybris.platform.assistedservicefacades.util.AssistedServiceUtils;
import de.hybris.platform.assistedservicefacades.customer360.GeneralActivityData;
import de.hybris.platform.assistedservicefacades.customer360.GeneralActivityDataList;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.order.CommerceSaveCartService;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;


/**
 * Model provider implementation for General Activity fragment.
 */
public class GeneralActivityModelProvider extends TicketsModelProvider
{
	private static final int DEFAULT_EVENT_LIMIT = 20;
	private static final String LIST_SIZE = "listSize";
	private static final String SORT = "byDate";
	private CustomerAccountService customerAccountService;
	private BaseStoreService baseStoreService;
	private BaseSiteService baseSiteService;
	private CommerceCartService commerceCartService;
	private CommerceSaveCartService commerceSaveCartService;
	private EnumerationService enumerationService;

	@Override
	public GeneralActivityDataList getModel(final Map<String, String> parameters)
	{
		final int listSize = Integer.parseInt(parameters.get(LIST_SIZE));
		final GeneralActivityDataList generalActivityList = super.getModel(parameters);
		final CustomerModel user = (CustomerModel) getUserService().getCurrentUser();

		populateCartDataWithCustomizeListSize(generalActivityList, user, listSize);
		populateOrderDataWithCustomizeListSize(generalActivityList, user, listSize);

		// Sort by Update Date desc as default sorting
		Collections.sort(generalActivityList.getGeneralActivities(), Comparator.comparing(GeneralActivityData::getUpdated).reversed());

		// return regarding to limit
		if(generalActivityList.getGeneralActivities().size() > listSize) {
			final List<GeneralActivityData> activities = generalActivityList.getGeneralActivities().subList(0, listSize);
			generalActivityList.setGeneralActivities(activities);
		}
		return generalActivityList;
	}



	/**
	 * OrderModel -> GeneralActivityData converter
	 *
	 * @param generalActivityList
	 * @param user
	 */
	protected void populateOrderData(final GeneralActivityDataList generalActivityList, final CustomerModel user)
	{
		final PageableData latestOrderPageableData = genPageableData(getEventNumberLimit(),SORT);
		populateOrders(generalActivityList,user,latestOrderPageableData);
	}

	protected void populateOrderDataWithCustomizeListSize(final GeneralActivityDataList generalActivityList, final CustomerModel user, final int listSize)
	{
		final PageableData latestOrderPageableData = genPageableData(listSize, SORT);
		populateOrders(generalActivityList, user, latestOrderPageableData);
	}

	protected void populateOrders(final GeneralActivityDataList generalActivityList, final CustomerModel user, final PageableData latestOrderPageableData)
	{
		final List<OrderModel> latestOrders = getCustomerAccountService()
				.getOrderList(user, getBaseStoreService().getCurrentBaseStore(), null, latestOrderPageableData).getResults();

		for (final OrderModel order : latestOrders)
		{
			final GeneralActivityData activityData = new GeneralActivityData();
			activityData.setType(ORDER_TEXT);
			activityData.setId(order.getCode());
			activityData.setStatus(order.getStatus() == null ? null : getEnumerationService().getEnumerationName(order.getStatus()));
			activityData.setCreated(order.getCreationtime());
			activityData.setUpdated(order.getModifiedtime());
			activityData.setUrl(AssistedServiceUtils.populateCartorOrderUrl(order, getBaseSiteService().getCurrentBaseSite()));
			activityData.setDescription(ORDER_DESCRIPTION);
			activityData.setDescriptionArgs(new Object[]
					{Integer.valueOf(order.getEntries().size()), order.getCurrency().getSymbol(), order.getTotalPrice()});
			generalActivityList.getGeneralActivities().add(activityData);
		}
	}


	/**
	 * CartModel -> GeneralActivityData converter
	 *
	 * @param generalActivityList
	 * @param user
	 */
	protected void populateCartData(final GeneralActivityDataList generalActivityList, final CustomerModel user)
	{
		final PageableData pageableData = genPageableData(getEventNumberLimit(), null);
		populateCarts(generalActivityList,user,pageableData);

	}

	protected void populateCartDataWithCustomizeListSize(final GeneralActivityDataList generalActivityList, final CustomerModel user, final int listSize)
	{
		final PageableData pageableData = genPageableData(listSize, null);
		populateCarts(generalActivityList, user, pageableData);

	}

	protected void populateCarts(final GeneralActivityDataList generalActivityList, final CustomerModel user, final PageableData pageableData)
	{
		final List<CartModel> carts = new ArrayList<>();

		// populate Session Carts
		final List<CartModel> sessionCarts = getCommerceCartService().getCartsForSiteAndUser(getBaseSiteService().getCurrentBaseSite(), user);
		if (CollectionUtils.isNotEmpty(sessionCarts))
		{
			carts.addAll(sessionCarts);
		}
		//populate Saved Carts
		final List<CartModel> savedCarts = getCommerceSaveCartService().getSavedCartsForSiteAndUser(pageableData,
				getBaseSiteService().getCurrentBaseSite(), user, null).getResults();
		if (CollectionUtils.isNotEmpty(savedCarts))
		{
			carts.addAll(savedCarts);
		}

		for (final CartModel cart : carts)
		{
			final GeneralActivityData activityData = new GeneralActivityData();
			activityData.setId(cart.getCode());
			activityData.setStatus(null);
			activityData.setCreated(cart.getCreationtime());
			activityData.setUpdated(cart.getModifiedtime());
			activityData.setUrl(AssistedServiceUtils.populateCartorOrderUrl(cart, getBaseSiteService().getCurrentBaseSite()));

			if (cart.getSaveTime() != null)
			{
				activityData.setType(SAVED_CART_TEXT);
				activityData.setDescription(cart.getName());
			}
			else
			{
				activityData.setType(CART_TEXT);
				activityData.setDescription(CART_DESCRIPTION);
				activityData.setDescriptionArgs(new Object[]
						{Integer.valueOf(cart.getEntries().size()), cart.getCurrency().getSymbol(), cart.getTotalPrice()});
			}
			generalActivityList.getGeneralActivities().add(activityData);
		}
	}

	protected PageableData genPageableData(final int listSize, final String sort)
	{
		final PageableData pageableData = new PageableData();
		pageableData.setCurrentPage(0);
		pageableData.setPageSize(listSize);
		pageableData.setSort(sort);
		return pageableData;
	}
	@Override
	protected int getEventNumberLimit()
	{
		return DEFAULT_EVENT_LIMIT; // Default value is 20 for General Activity
	}

	protected CustomerAccountService getCustomerAccountService()
	{
		return customerAccountService;
	}

	public void setCustomerAccountService(final CustomerAccountService customerAccountService)
	{
		this.customerAccountService = customerAccountService;
	}

	protected BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}

	protected BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

	protected CommerceCartService getCommerceCartService()
	{
		return commerceCartService;
	}

	public void setCommerceCartService(final CommerceCartService commerceCartService)
	{
		this.commerceCartService = commerceCartService;
	}

	protected CommerceSaveCartService getCommerceSaveCartService()
	{
		return commerceSaveCartService;
	}

	public void setCommerceSaveCartService(final CommerceSaveCartService commerceSaveCartService)
	{
		this.commerceSaveCartService = commerceSaveCartService;
	}

	protected EnumerationService getEnumerationService()
	{
		return enumerationService;
	}

	public void setEnumerationService(final EnumerationService enumerationService)
	{
		this.enumerationService = enumerationService;
	}
}
