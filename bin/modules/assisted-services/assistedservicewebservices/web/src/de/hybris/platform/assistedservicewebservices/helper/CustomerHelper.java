/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.helper;

import de.hybris.platform.assistedservicefacades.user.data.AutoSuggestionCustomerData;
import de.hybris.platform.assistedservicewebservices.dto.CustomerSearchPageWsDTO;
import de.hybris.platform.assistedservicewebservices.dto.CustomerSuggestionWsDTO;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.commercewebservicescommons.dto.user.UserWsDTO;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;

import javax.annotation.Resource;

import java.util.List;

import org.springframework.stereotype.Component;


@Component
public class CustomerHelper
{

	@Resource(name = "dataMapper")
	private DataMapper dataMapper;

	public CustomerSearchPageWsDTO getCustomerSearchPageDto(final SearchPageData<CustomerData> customers)
	{
		return dataMapper.map(customers, CustomerSearchPageWsDTO.class);
	}

	public List<CustomerSuggestionWsDTO> getCustomerSuggestions(final List<AutoSuggestionCustomerData> customerData)
	{
		return dataMapper.mapAsList(customerData, CustomerSuggestionWsDTO.class, FieldSetLevelHelper.DEFAULT_LEVEL);
	}

	public UserWsDTO getUserWsDTO(final CustomerData customerData)
	{
		return dataMapper.map(customerData, UserWsDTO.class);
	}
}
