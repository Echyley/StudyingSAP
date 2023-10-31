/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.adapters;

import de.hybris.platform.assistedservicefacades.customer360.CustomerViewHeadingData;
import de.hybris.platform.assistedservicewebservices.dto.C360Overview;
import de.hybris.platform.assistedservicewebservices.dto.C360OverviewDataWsDTO;
import de.hybris.platform.assistedservicewebservices.dto.Customer360DataWsDTO;


public class C360OverviewAdapter extends C360FragmentDataAdapter<CustomerViewHeadingData>
{

    @Override
    public Customer360DataWsDTO adapt(CustomerViewHeadingData data)
    {
        C360Overview c360Overview = new C360Overview();
        C360OverviewDataWsDTO c360OverviewData = getDataMapper().map(data, C360OverviewDataWsDTO.class);
        c360Overview.setOverview(c360OverviewData);

        return c360Overview;
    }
}
