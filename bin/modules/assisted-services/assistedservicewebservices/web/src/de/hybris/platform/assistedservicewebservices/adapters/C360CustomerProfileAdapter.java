/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.adapters;

import de.hybris.platform.assistedservicefacades.customer360.CustomerProfileData;
import de.hybris.platform.assistedservicewebservices.dto.C360CustomerProfile;
import de.hybris.platform.assistedservicewebservices.dto.C360CustomerProfileDataWsDTO;
import de.hybris.platform.assistedservicewebservices.dto.Customer360DataWsDTO;


public class C360CustomerProfileAdapter extends C360FragmentDataAdapter<CustomerProfileData>
{
    @Override
    public Customer360DataWsDTO adapt(CustomerProfileData data)
    {
        C360CustomerProfile c360CustomerProfile = new C360CustomerProfile();
        C360CustomerProfileDataWsDTO c360CustomerProfileData = dataMapper.map(data, C360CustomerProfileDataWsDTO.class);
        c360CustomerProfile.setProfile(c360CustomerProfileData);
        return c360CustomerProfile;
    }
}
