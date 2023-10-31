/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapcentralorderservices.services.impl;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.sap.sapcentralorderservices.pojo.v1.CentralOrderDetailsResponse;
import de.hybris.platform.sap.sapcentralorderservices.pojo.v1.CentralOrderListResponse;
import de.hybris.platform.store.BaseStoreModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.sap.sapcentralorderservices.clients.CentralOrderApiClient;
import com.sap.sapcentralorderservices.constants.SapcentralorderservicesConstants;
import com.sap.sapcentralorderservices.exception.SapCentralOrderException;
import com.sap.sapcentralorderservices.services.CentralOrderService;


/**
 * DefaultCentralOrderService
 */
public class DefaultCentralOrderService implements CentralOrderService
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultCentralOrderService.class);

	private CentralOrderApiClient centralOrderApiClient;
	private AtomicReference<CentralOrderApiClient> atomicRefCentralOrderApiClient = new AtomicReference<>(centralOrderApiClient);

	/**
	 * @return the centralOrderApiClient
	 */
	public CentralOrderApiClient getCentralOrderApiClient()
	{
		return this.atomicRefCentralOrderApiClient.get();
	}



	/**
	 * @param centralOrderApiClient
	 *           the centralOrderApiClient to set
	 */
	public void setCentralOrderApiClient(final CentralOrderApiClient centralOrderApiClient)
	{
		this.atomicRefCentralOrderApiClient.set(centralOrderApiClient);
	}

	@Override
	public ResponseEntity<CentralOrderListResponse[]> getCentalOrderList(final CustomerModel customerModel,
			final BaseStoreModel store, final OrderStatus[] status, final PageableData pageableData, final String sourceSystemId)
	{


		final int currentPage = pageableData.getCurrentPage();
		final int pageSize = pageableData.getPageSize();
		final String customerId = customerModel.getCustomerID();
		final String uriString = "";

		// Since the new central order API only supports order number sort.
		final UriComponents uriComponents = UriComponentsBuilder.fromUriString(uriString)
				.queryParam(SapcentralorderservicesConstants.CUSTOMER_ID, customerId)
				.queryParam(SapcentralorderservicesConstants.SORT, new StringBuilder().append(SapcentralorderservicesConstants.DEFAULT_SORT_TYPE).append(SapcentralorderservicesConstants.COMMA).append(SapcentralorderservicesConstants.SORT_DIRETION).toString())
				.queryParam(SapcentralorderservicesConstants.PAGE, currentPage)
				.queryParam(SapcentralorderservicesConstants.SOURCE_SYSTEM_ID, sourceSystemId)
				.queryParam(SapcentralorderservicesConstants.SIZE, pageSize)
				.build();
		ResponseEntity<CentralOrderListResponse[]> response = null;
		try
		{
			response = getCentralOrderApiClient().getEntity(uriComponents, CentralOrderListResponse[].class);

		}
		catch (final SapCentralOrderException e)
		{
			LOG.warn(e.toString());
		}
		return response;

	}

	@Override
	public ResponseEntity<CentralOrderDetailsResponse> getCentalOrderDetailsForCode(final CustomerModel currentCustomer,
			final String orderCode, final String sourceSystemId)
	{
		ResponseEntity<CentralOrderDetailsResponse[]> response = null;
		ResponseEntity<CentralOrderDetailsResponse> responseWithGuid = null;
		final String uriString = SapcentralorderservicesConstants.EMPTY_STRING;
		final UriComponents uriComponents;

		try
		{
				uriComponents = UriComponentsBuilder.fromUriString(uriString)
					.queryParam(SapcentralorderservicesConstants.DOCUMENT_NUMBER, orderCode)
					.queryParam(SapcentralorderservicesConstants.SOURCE_SYSTEM_ID, sourceSystemId).build();
				response = getCentralOrderApiClient().getEntity(uriComponents, CentralOrderDetailsResponse[].class);
				final Optional<ResponseEntity<CentralOrderDetailsResponse>> responseguid = Optional.ofNullable(response)
						.map(v -> Optional.ofNullable(v.getBody()).filter(responseBody -> responseBody.length > 0).map(centralOrderDetails ->
						getCentalOrderDetailsForGuid(currentCustomer, centralOrderDetails[0].getId(),
								sourceSystemId)
						)).orElseThrow(RuntimeException::new);

				if (responseguid.isPresent())
				{
					responseWithGuid = responseguid.get();
				}
		}
		catch (final SapCentralOrderException e)
		{
			LOG.warn(e.toString());
		}
		return responseWithGuid;
	}



	@Override
	public ResponseEntity<CentralOrderDetailsResponse> getCentalOrderDetailsForGuid(final CustomerModel currentCustomer,
			final String guid, final String sourceSystemId)
	{

		ResponseEntity<CentralOrderDetailsResponse> response = null;
		final String uriString = SapcentralorderservicesConstants.EMPTY_STRING;
		final UriComponents uriComponents;

		try
		{
			uriComponents = UriComponentsBuilder.fromUriString(uriString).path(SapcentralorderservicesConstants.SLASH + "" + guid)
					.queryParam(SapcentralorderservicesConstants.SOURCE_SYSTEM_ID, sourceSystemId)
					.build();
			response = getCentralOrderApiClient().getEntity(uriComponents, CentralOrderDetailsResponse.class);

		}
		catch (final SapCentralOrderException e)
		{
			LOG.warn(e.toString());
		}
		return response;



	}


}
