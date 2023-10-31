/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.services.impl;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BCustomerService;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.cpq.productconfig.services.BusinessContextService;
import de.hybris.platform.cpq.productconfig.services.EngineDeterminationService;
import de.hybris.platform.cpq.productconfig.services.client.CpqClient;
import de.hybris.platform.cpq.productconfig.services.client.CpqClientUtil;
import de.hybris.platform.cpq.productconfig.services.data.BusinessContext;
import de.hybris.platform.cpq.productconfig.services.data.BusinessContextRequest;
import de.hybris.platform.cpq.productconfig.services.data.InvolvedParty;
import de.hybris.platform.cpq.productconfig.services.data.SalesArea;
import de.hybris.platform.cpq.productconfig.services.strategies.CPQInteractionStrategy;
import de.hybris.platform.sap.sapmodel.services.SalesAreaService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.hybris.charon.RawResponse;
import com.hybris.charon.exp.HttpException;


/**
 * Default implementation of the {@link BusinessContextService}
 */
public class DefaultBusinessContextService implements BusinessContextService
{
	protected static final int HTTP_STATUS_CREATED = 201;
	protected static final String BILL_TO = "SP";
	private final CPQInteractionStrategy cpqInteractionStrategy;
	private final SalesAreaService commonSalesAreaService;
	private final B2BCustomerService<B2BCustomerModel, B2BUnitModel> b2bCustomerService;
	private final B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;
	private final CommonI18NService i18NService;
	private final EngineDeterminationService engineDeterminationService;
	private final CpqClientUtil clientUtil;

	/**
	 * Injection of mandatory beans
	 *
	 * @param cpqInteractionStrategy
	 *           interaction with cpq
	 * @param commonSalesAreaService
	 *           sales area service
	 * @param b2bCustomerService
	 *           b2b customer service
	 * @param b2bUnitService
	 *           b2b unit service
	 * @param i18nService
	 *           common i18n service
	 * @param engineDeterminationService
	 *           engine determination service
	 * @param cpqClientUtil
	 *           cpq client util
	 */
	public DefaultBusinessContextService(final CPQInteractionStrategy cpqInteractionStrategy,
			final SalesAreaService commonSalesAreaService,
			final B2BCustomerService<B2BCustomerModel, B2BUnitModel> b2bCustomerService,
			final B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService, final CommonI18NService i18nService,
			final EngineDeterminationService engineDeterminationService, final CpqClientUtil cpqClientUtil)
	{
		this.engineDeterminationService = engineDeterminationService;
		this.cpqInteractionStrategy = cpqInteractionStrategy;
		this.commonSalesAreaService = commonSalesAreaService;
		this.b2bCustomerService = b2bCustomerService;
		this.b2bUnitService = b2bUnitService;
		this.i18NService = i18nService;
		this.clientUtil = cpqClientUtil;
	}


	@Override
	public BusinessContext getBusinessContext()
	{
		final BusinessContext businessContext = new BusinessContext();
		businessContext.setSalesArea(getSalesArea());
		businessContext.setCurrencyCode(getCurrencyCode());
		businessContext.setLanguage(getLanguageCode());
		businessContext.setInvolvedParties(getInvolvedParties());
		return businessContext;
	}

	protected SalesArea getSalesArea()
	{
		final SalesArea salesArea = new SalesArea();
		salesArea.setSalesOrganization(getCommonSalesAreaService().getSalesOrganization());
		salesArea.setDistributionChannel(getCommonSalesAreaService().getDistributionChannel());
		salesArea.setDivision(getCommonSalesAreaService().getDivision());
		return salesArea;
	}

	protected String getCurrencyCode()
	{
		return getI18NService().getCurrentCurrency().getIsocode();
	}

	protected String getLanguageCode()
	{
		return getI18NService().getCurrentLanguage().getIsocode();
	}

	protected List<InvolvedParty> getInvolvedParties()
	{
		final List<InvolvedParty> involvedParties = new ArrayList<>();
		involvedParties.add(getBillTo());
		return involvedParties;
	}

	protected InvolvedParty getBillTo()
	{
		final InvolvedParty billTo = new InvolvedParty();
		billTo.setKey(BILL_TO);
		billTo.setExternalId(getBusinessPartnerId());
		return billTo;
	}

	protected String getBusinessPartnerId()
	{
		String businessPartnerId = null;
		B2BUnitModel b2bUnit = null;
		final B2BCustomerModel b2bCustomer = getB2bCustomerService().getCurrentB2BCustomer();
		b2bUnit = getB2bUnitService().getParent(b2bCustomer);
		if (b2bUnit != null)
		{
			businessPartnerId = getB2bUnitService().getRootUnit(b2bUnit).getUid();
		}

		return businessPartnerId != null ? businessPartnerId.toUpperCase(Locale.ENGLISH) : businessPartnerId;
	}

	@Override
	public String getOwnerId()
	{
		return getBusinessPartnerId();
	}

	@Override
	public void sendBusinessContextToCPQ(final String ownerId, final BusinessContext businessContext)
	{
		if (ownerId == null)
		{
			throw new IllegalStateException("Supplied owner id is null.");
		}
		if (!getEngineDeterminationService().isMockEngineActive())
		{
			performCPQInteraction(ownerId, businessContext);
		}
	}


	protected void performCPQInteraction(final String ownerId, final BusinessContext businessContext)
	{
		final String authorization = getCpqInteractionStrategy().getAuthorizationString();
		final BusinessContextRequest businessContextRequest = new BusinessContextRequest();
		businessContextRequest.setConfigurationContext(businessContext);
		businessContextRequest.setOwnerId(ownerId);
		try
		{
			final CpqClient client = getCpqInteractionStrategy().getClient();
			final RawResponse<?> rawResponse = clientUtil
					.toResponse(client.createOrUpdateConfigurationContext(businessContextRequest, authorization));
			clientUtil.checkHTTPStatusCode("SEND_CONTEXT", HTTP_STATUS_CREATED, rawResponse);
		}
		catch (final HttpException e)
		{
			throw new IllegalStateException(String.format("Business context could not be sent. Error http code %s", e.getCode()), e);
		}
	}

	protected SalesAreaService getCommonSalesAreaService()
	{
		return commonSalesAreaService;
	}

	protected B2BCustomerService<B2BCustomerModel, B2BUnitModel> getB2bCustomerService()
	{
		return b2bCustomerService;
	}

	protected B2BUnitService<B2BUnitModel, B2BCustomerModel> getB2bUnitService()
	{
		return b2bUnitService;
	}

	protected CommonI18NService getI18NService()
	{
		return i18NService;
	}

	protected CPQInteractionStrategy getCpqInteractionStrategy()
	{
		return cpqInteractionStrategy;
	}

	protected EngineDeterminationService getEngineDeterminationService()
	{
		return engineDeterminationService;
	}

}
