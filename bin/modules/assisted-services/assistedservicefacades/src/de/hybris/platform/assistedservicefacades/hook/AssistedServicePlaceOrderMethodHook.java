/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicefacades.hook;

import de.hybris.platform.assistedservicefacades.AssistedServiceFacade;
import de.hybris.platform.assistedservicefacades.constants.AssistedservicefacadesConstants;
import de.hybris.platform.commerceservices.order.hook.CommercePlaceOrderMethodHook;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.service.data.CommerceOrderResult;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;


/**
 * Assisted service place order hook for setting up placedBy attribute for an order.
 */
public class AssistedServicePlaceOrderMethodHook implements CommercePlaceOrderMethodHook
{
	private AssistedServiceFacade assistedServiceFacade;
	private ModelService modelService;
	private UserService userService;
	private SessionService sessionService;

	@Override
	public void afterPlaceOrder(final CommerceCheckoutParameter parameter, final CommerceOrderResult orderModel)
			throws InvalidCartException
	{
		UserModel agent;
		if (assistedServiceFacade.isAssistedServiceAgentLoggedIn())
		{
			agent = assistedServiceFacade.getAsmSession().getAgent();
		}
		// check if acting is different from user
		else
		{
			agent = getActingUserFromSessionContext();
		}
		if (agent != null)
		{
			orderModel.getOrder().setPlacedBy(agent);
			modelService.save(orderModel.getOrder());
			modelService.refresh(orderModel.getOrder());
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.hybris.platform.commerceservices.order.hook.CommercePlaceOrderMethodHook#beforePlaceOrder(de.hybris.platform
	 * .commerceservices.service.data.CommerceCheckoutParameter)
	 */
	@Override
	public void beforePlaceOrder(final CommerceCheckoutParameter parameter) throws InvalidCartException
	{
		// Nothing done here

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.hybris.platform.commerceservices.order.hook.CommercePlaceOrderMethodHook#beforeSubmitOrder(de.hybris.platform
	 * .commerceservices.service.data.CommerceCheckoutParameter,
	 * de.hybris.platform.commerceservices.service.data.CommerceOrderResult)
	 */
	@Override
	public void beforeSubmitOrder(final CommerceCheckoutParameter parameter, final CommerceOrderResult result)
			throws InvalidCartException
	{
		// Nothing done here
	}

	/**
	 * get that acting user, and if acting user is there and different from user.
	 * we return it.
	 *
	 * @return userModel if it's agent, or return null
	 */
	protected UserModel getActingUserFromSessionContext()
	{
		final String actingUserUid = getSessionService().getCurrentSession()
				.getAttribute(AssistedservicefacadesConstants.ACTING_USER_UID);
		final String userId = getUserService().getCurrentUser().getUid();
		if (actingUserUid != null && !userId.equals(actingUserUid))
		{
			return getUserService().getUserForUID(actingUserUid);
		}
		return null;
	}

	/**
	 * @return the assistedServiceFacade
	 */
	public AssistedServiceFacade getAssistedServiceFacade()
	{
		return assistedServiceFacade;
	}

	/**
	 * @param assistedServiceFacade
	 *           the assistedServiceFacade to set
	 */
	public void setAssistedServiceFacade(final AssistedServiceFacade assistedServiceFacade)
	{
		this.assistedServiceFacade = assistedServiceFacade;
	}

	/**
	 * @return the modelService
	 */
	public ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * @param modelService
	 *           the modelService to set
	 */
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * @return the UserService
	 */
	public UserService getUserService()
	{
		return userService;
	}

	/**
	 * @param userService the modelService to set
	 */
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	/**
	 * @return the SessionService
	 */
	public SessionService getSessionService()
	{
		return sessionService;
	}

	/**
	 * @param sessionService the modelService to set
	 */
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

}
