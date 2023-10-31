/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicefacades.customer360.providers;

import de.hybris.platform.assistedservicefacades.customer360.CustomerViewHeadingData;
import de.hybris.platform.assistedservicefacades.customer360.FragmentModelProvider;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.OrderFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.ticket.model.CsTicketModel;
import de.hybris.platform.ticket.service.TicketBusinessService;
import de.hybris.platform.ticket.service.TicketService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;


/**
 * Model provider implementation for Customer 360 Heading fragment.
 */
public class HeadingDataProvider implements FragmentModelProvider<CustomerViewHeadingData>
{
	private static final int DEFAULT_PAGE_SIZE = 10;
	private OrderFacade orderFacade;
	private TicketService ticketService;
	private TicketBusinessService ticketBusinessService;
	private BaseSiteService baseSiteService;
	private UserService userService;
	private Converter<MediaModel, ImageData> imageConverter;
	private Converter<AddressModel, AddressData> addressConverter;
	private CartFacade cartFacade;

	@Override
	public CustomerViewHeadingData getModel(final Map<String, String> parameters)
	{
		final CustomerViewHeadingData customerViewHeadingData = new CustomerViewHeadingData();
		final CustomerModel currentCustomer = (CustomerModel) getUserService().getCurrentUser();
		customerViewHeadingData.setName(currentCustomer.getName());
		customerViewHeadingData.setEmail(Optional.ofNullable(currentCustomer.getUndecoratedUid()).orElse(currentCustomer.getUid()));
		customerViewHeadingData.setSignedUp(currentCustomer.getCreationtime());

		final AddressModel defaultShipmentAddress = currentCustomer.getDefaultShipmentAddress();
		if (defaultShipmentAddress != null)
		{
			customerViewHeadingData.setAddress(getAddressConverter().convert(defaultShipmentAddress));
		}

		if (null != currentCustomer.getProfilePicture())
		{
			customerViewHeadingData.setProfilePicture(getImageConverter().convert(currentCustomer.getProfilePicture()));
		}

		final CartData cartData = getCartFacade().getMiniCart();
		if (cartData != null && cartData.getTotalUnitCount().intValue() > 0)
		{
			customerViewHeadingData.setCartCode(cartData.getCode());
			customerViewHeadingData.setCartSize(cartData.getTotalUnitCount());
		}

		final OrderHistoryData latestOrder = getLatestOrderForCustomer();
		if (latestOrder != null)
		{
			customerViewHeadingData.setLatestOrderCode(latestOrder.getCode());
			customerViewHeadingData.setLatestOrderTotal(latestOrder.getTotal().getFormattedValue());
			customerViewHeadingData.setLatestOrderTime(latestOrder.getPlaced());
		}
		final CsTicketModel latestUpdatedOpenTicket = getLatestUpdatedOpenTicketForCustomer(currentCustomer);
		if (latestUpdatedOpenTicket != null)
		{
			customerViewHeadingData.setLatestOpenedTicketId(latestUpdatedOpenTicket.getTicketID());
			customerViewHeadingData.setLatestOpenedTicketCreated(latestUpdatedOpenTicket.getCreationtime());
		}
		return customerViewHeadingData;
	}

	protected OrderHistoryData getLatestOrderForCustomer()
	{
		final PageableData pageableData = new PageableData();
		pageableData.setPageSize(1);

		final SearchPageData<OrderHistoryData> orders = getOrderFacade().getPagedOrderHistoryForStatuses(pageableData);

		return CollectionUtils.isEmpty(orders.getResults()) ? null : orders.getResults().iterator().next();
	}

	protected CsTicketModel getLatestUpdatedOpenTicketForCustomer(final UserModel customer)
	{
		final PageableData pageableData = new PageableData();
		pageableData.setPageSize(DEFAULT_PAGE_SIZE);
		final List<CsTicketModel> tickets = getTicketService()
				.getTicketsForCustomerOrderByModifiedTime(customer, getBaseSiteService().getCurrentBaseSite(), pageableData)
				.getResults();
		for (final CsTicketModel ticket : tickets)
		{
			if (!getTicketBusinessService().isTicketClosed(ticket))
			{
				return ticket;
			}
		}
		return null;
	}

	protected UserService getUserService()
	{
		return userService;
	}

	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	protected BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

	protected TicketService getTicketService()
	{
		return ticketService;
	}

	public void setTicketService(final TicketService ticketService)
	{
		this.ticketService = ticketService;
	}

	protected TicketBusinessService getTicketBusinessService()
	{
		return ticketBusinessService;
	}

	public void setTicketBusinessService(final TicketBusinessService ticketBusinessService)
	{
		this.ticketBusinessService = ticketBusinessService;
	}

	protected Converter<AddressModel, AddressData> getAddressConverter()
	{
		return addressConverter;
	}

	public void setAddressConverter(final Converter<AddressModel, AddressData> addressConverter)
	{
		this.addressConverter = addressConverter;
	}

	protected Converter<MediaModel, ImageData> getImageConverter()
	{
		return imageConverter;
	}

	public void setImageConverter(final Converter<MediaModel, ImageData> imageConverter)
	{
		this.imageConverter = imageConverter;
	}

	protected CartFacade getCartFacade()
	{
		return cartFacade;
	}

	public void setCartFacade(final CartFacade cartFacade)
	{
		this.cartFacade = cartFacade;
	}

	protected OrderFacade getOrderFacade()
	{
		return orderFacade;
	}

	public void setOrderFacade(final OrderFacade orderFacade)
	{
		this.orderFacade = orderFacade;
	}
}
