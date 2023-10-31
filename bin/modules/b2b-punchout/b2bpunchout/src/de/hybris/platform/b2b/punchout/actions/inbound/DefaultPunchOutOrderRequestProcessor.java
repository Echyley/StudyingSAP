/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.actions.inbound;

import de.hybris.platform.b2b.punchout.PunchOutException;
import de.hybris.platform.b2b.punchout.PunchOutResponseMessage;
import de.hybris.platform.b2b.punchout.services.CXMLBuilder;
import de.hybris.platform.b2b.punchout.services.CXMLElementBrowser;
import de.hybris.platform.b2b.punchout.services.PunchOutConfigurationService;
import de.hybris.platform.b2b.punchout.services.PunchOutService;
import de.hybris.platform.b2b.punchout.services.PunchOutSessionService;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commerceservices.order.CommerceCartRestorationException;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.user.UserService;

import org.cxml.CXML;
import org.cxml.ItemOut;
import org.cxml.OrderRequest;
import org.cxml.SupplierPartAuxiliaryID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.collections.CollectionUtils;
import org.cxml.ItemDetail;
import org.cxml.OrderRequestHeader;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * Default implementation of {@link PunchOutService}.
 */
public class DefaultPunchOutOrderRequestProcessor implements PunchOutInboundProcessor
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultPunchOutOrderRequestProcessor.class);
	private PrepareCartPurchaseOrderProcessing prepareCartPurchaseOrderProcessing;
	private PopulateCartPurchaseOrderProcessing populateCartPurchaseOrderProcessing;
	private PlacePurchaseOrderProcessing placePurchaseOrderProcessing;
	private CartService cartService;
	private CommerceCartService commerceCartService;
	private UserService userService;
	private PunchOutSessionService punchOutSessionService;
	private PunchOutConfigurationService punchOutConfigurationService;

	@Override
	public CXML generatecXML(final CXML requestBody)
	{
		final String cartCode = getRequestCartCode(requestBody);
		CartModel cartModel = null;

		if (cartCode != null)
		{
			cartModel = restoreSessionCartByCode(cartCode);
			if (LOG.isDebugEnabled())
			{
				LOG.debug(String.format("restored punchOut cart to session. cartId=%s", cartCode));
			}
		}

		if(cartModel == null || !validateCart(requestBody, cartModel))
		{
			getCartService().removeSessionCart();
			cartModel = getCartService().getSessionCart();
			getPunchOutSessionService().setPunchOutSessionCartIsValid(Boolean.FALSE);
			if (LOG.isDebugEnabled())
			{
				LOG.debug("create a new punchOut cart according to the request cxml");
			}
		}

		cartModel.setStatus(OrderStatus.CREATED);
		cartModel.setPunchOutOrder(Boolean.TRUE);
		getPopulateCartPurchaseOrderProcessing().process(requestBody, cartModel);
		getPrepareCartPurchaseOrderProcessing().process();
		final OrderData orderData = getPlacePurchaseOrderProcessing().process();

		return CXMLBuilder.newInstance().withResponseCode(HttpStatus.OK)
				.withResponseMessage(PunchOutResponseMessage.OK).withResponseValue(orderData.getCode()).create();
	}

	/**
	 * Finds an {@link OrderRequest} from the input.
	 *
	 * @param input
	 *           the {@link CXML} input
	 * @return the {@link OrderRequest} from the input
	 */
	protected OrderRequest getOrderRequest(final CXML input)
	{
		final OrderRequest result = new CXMLElementBrowser(input).findRequestByType(OrderRequest.class);
		if (result == null)
		{
			throw new PunchOutException(HttpStatus.CONFLICT, "No OrderRequest in the CXML request data");
		}
		return result;
	}

	/**
	 * restore session cart by code and the cart will be re-calculated
	 * @param cartCode
	 * @return cartModel
	 */
	protected CartModel restoreSessionCartByCode(final String cartCode)
	{
		final UserModel user = getUserService().getCurrentUser();
		final CartModel cartModel = getCommerceCartService().getCartForCodeAndUser(cartCode, user);
		if (cartModel == null)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug(String.format("get cart failed. cartId=%s, userId=%s", cartCode, user.getUid()));
			}
			return null;
		}

		try {
			final CommerceCartParameter parameter = new CommerceCartParameter();
			parameter.setEnableHooks(true);
			parameter.setCart(cartModel);
			parameter.setIgnoreRecalculation(false);
			getCommerceCartService().restoreCart(parameter);
			getCartService().setSessionCart(cartModel);
			getPunchOutSessionService().setPunchOutSessionCartIsValid(Boolean.TRUE);
		} catch (CommerceCartRestorationException e) {
			throw new PunchOutException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("restore cart failed. cartId=%s", cartModel.getCode()), e);
		}

		return cartModel;
	}

	/**
	 * @return get the cart Code
	 */
	protected String getRequestCartCode(final CXML requestBody)
	{
		final OrderRequest orderRequest = getOrderRequest(requestBody);
		String cartCode = null;
		for (final ItemOut itemOut : orderRequest.getItemOut())
		{
			SupplierPartAuxiliaryID supplierPartAuxiliaryID = itemOut.getItemID().getSupplierPartAuxiliaryID();
			if (supplierPartAuxiliaryID != null && !supplierPartAuxiliaryID.getContent().isEmpty())
			{
				String curItemCartCode = (String) supplierPartAuxiliaryID.getContent().get(0);
				if (cartCode == null && curItemCartCode != null && curItemCartCode.length() > 0)
				{
					cartCode = curItemCartCode;
				}
				else if (curItemCartCode == null || !curItemCartCode.equals(cartCode))
				{
					throw new PunchOutException(HttpStatus.BAD_REQUEST,
							"supplierPartAuxiliaryID of ItemID in request cxml should be consistent.");
				}
			}
		}

		return cartCode;
	}


	protected boolean validateCart(final CXML requestBody, final CartModel cartModel)
	{
		if (!getPunchOutConfigurationService().isOrderVerificationEnabled())
		{
			return false;
		}

		final OrderRequest orderRequest = getOrderRequest(requestBody);
		final List<ItemOut> itemOuts = orderRequest.getItemOut();
		if (CollectionUtils.isEmpty(itemOuts))
		{
			throw new PunchOutException(HttpStatus.BAD_REQUEST, "Miss ItemOut in cxml request.");
		}

		final List<AbstractOrderEntryModel> cartEntries = cartModel.getEntries();
		if (CollectionUtils.isEmpty(cartEntries))
		{
			return false;
		}
		final Map<String, AbstractOrderEntryModel> cartEntryMap = cartEntries.stream().collect(
				Collectors.toMap((cartEntryModel -> cartEntryModel.getProduct().getCode()),
						Function.identity()));

		if (cartEntryMap.size() != itemOuts.size())
		{
			return false;
		}
		for (final ItemOut itemOut : itemOuts)
		{
			final String supplierPartID = itemOut.getItemID().getSupplierPartID().getvalue();
			final AbstractOrderEntryModel cartEntryModel = cartEntryMap.get(supplierPartID);
			if (cartEntryModel == null)
			{
				return false;
			}
			if (!Objects.equals(cartEntryModel.getQuantity(), Long.valueOf(itemOut.getQuantity())))
			{
				return false;
			}
			final ItemDetail itemDetail = (ItemDetail) itemOut.getItemDetailOrBlanketItemDetail().iterator().next();
			if (!Objects.equals(cartEntryModel.getBasePrice(), Double.valueOf(itemDetail.getUnitPrice().getMoney().getvalue())))
			{
				return false;
			}
		}
		return true;
	}

	protected PunchOutConfigurationService getPunchOutConfigurationService()
	{
		return punchOutConfigurationService;
	}

	public void setPunchOutConfigurationService(final PunchOutConfigurationService punchOutConfigurationService)
	{
		this.punchOutConfigurationService = punchOutConfigurationService;
	}

	/**
	 * @return the prepareCartPurchaseOrderProcessing
	 */
	protected PrepareCartPurchaseOrderProcessing getPrepareCartPurchaseOrderProcessing()
	{
		return prepareCartPurchaseOrderProcessing;
	}

	/**
	 * @param prepareCartPurchaseOrderProcessing the prepareCartPurchaseOrderProcessing to set
	 */
	public void setPrepareCartPurchaseOrderProcessing(final PrepareCartPurchaseOrderProcessing prepareCartPurchaseOrderProcessing)
	{
		this.prepareCartPurchaseOrderProcessing = prepareCartPurchaseOrderProcessing;
	}

	/**
	 * @return the populateCartPurchaseOrderProcessing
	 */
	protected PopulateCartPurchaseOrderProcessing getPopulateCartPurchaseOrderProcessing()
	{
		return populateCartPurchaseOrderProcessing;
	}

	/**
	 * @param populateCartPurchaseOrderProcessing the populateCartPurchaseOrderProcessing to set
	 */
	public void setPopulateCartPurchaseOrderProcessing(
			final PopulateCartPurchaseOrderProcessing populateCartPurchaseOrderProcessing)
	{
		this.populateCartPurchaseOrderProcessing = populateCartPurchaseOrderProcessing;
	}

	/**
	 * @return the placePurchaseOrderProcessing
	 */
	protected PlacePurchaseOrderProcessing getPlacePurchaseOrderProcessing()
	{
		return placePurchaseOrderProcessing;
	}

	/**
	 * @param placePurchaseOrderProcessing the placePurchaseOrderProcessing to set
	 */
	public void setPlacePurchaseOrderProcessing(final PlacePurchaseOrderProcessing placePurchaseOrderProcessing)
	{
		this.placePurchaseOrderProcessing = placePurchaseOrderProcessing;
	}

	/**
	 * @return the cartService
	 */
	protected CartService getCartService()
	{
		return cartService;
	}

	/**
	 * @param cartService the cartService to set
	 */
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}

	/**
	 * @param commerceCartService
	 */
	public void setCommerceCartService(final CommerceCartService commerceCartService)
	{
		this.commerceCartService = commerceCartService;
	}

	protected CommerceCartService getCommerceCartService()
	{
		return commerceCartService;
	}

	/**
	 * @param userService
	 */
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	protected UserService getUserService()
	{
		return userService;
	}

	protected PunchOutSessionService getPunchOutSessionService()
	{
		return punchOutSessionService;
	}

	public void setPunchOutSessionService(final PunchOutSessionService punchOutSessionService)
	{
		this.punchOutSessionService = punchOutSessionService;
	}
}
