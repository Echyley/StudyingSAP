/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.adapters;

import de.hybris.platform.assistedservicewebservices.dto.C360CustomerProductInterestList;
import de.hybris.platform.assistedservicewebservices.dto.Customer360DataWsDTO;
import de.hybris.platform.assistedservicewebservices.dto.C360CustomerProductInterestWsDTO;
import de.hybris.platform.customerinterestsfacades.data.ProductInterestRelationData;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class C360CustomerInterestsListAdapter extends C360FragmentDataAdapter<List<ProductInterestRelationData>> {

	@Override
	public Customer360DataWsDTO adapt(final List<ProductInterestRelationData> source)
	{
		final C360CustomerProductInterestList c360CustomerProductInterestList = new C360CustomerProductInterestList();
		List<C360CustomerProductInterestWsDTO> c360CustomerProductInterests = Collections.emptyList();
		if (source != null)
		{
			c360CustomerProductInterests = source.stream().map(interest -> getDataMapper().map(interest, C360CustomerProductInterestWsDTO.class))
					.collect(Collectors.toList());
		}
		c360CustomerProductInterestList.setCustomerProductInterests(c360CustomerProductInterests);
		return c360CustomerProductInterestList;
	}
}
