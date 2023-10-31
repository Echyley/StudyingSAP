/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.adapters;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.assistedservicefacades.customer360.CustomerProfileData;
import de.hybris.platform.assistedservicewebservices.dto.C360CustomerProfile;
import de.hybris.platform.assistedservicewebservices.dto.C360CustomerProfileDataWsDTO;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class C360ProfileAdapterTest {
    @Mock
    private DataMapper dataMapper;

    private C360CustomerProfileAdapter c360CustomerProfileAdapter;

    @Before
    public void setUp()
    {
        c360CustomerProfileAdapter = new C360CustomerProfileAdapter();
        c360CustomerProfileAdapter.setDataMapper(dataMapper);
    }

    @Test
    public void testC360CustomerProfileAdapter()
    {
        final CustomerProfileData customerProfileData = new CustomerProfileData();

        final C360CustomerProfileDataWsDTO c360CustomerProfileDataWsDTO = new C360CustomerProfileDataWsDTO();

        when(dataMapper.map(customerProfileData, C360CustomerProfileDataWsDTO.class))
                .thenReturn(c360CustomerProfileDataWsDTO);

        final C360CustomerProfile c360CustomerProfile = (C360CustomerProfile) c360CustomerProfileAdapter.adapt(customerProfileData);
        verify(dataMapper).map(customerProfileData, C360CustomerProfileDataWsDTO.class);
        assertThat(c360CustomerProfile).isNotNull();
        assertThat(c360CustomerProfile.getProfile()).isSameAs(c360CustomerProfileDataWsDTO);
    }

    @Test
    public void testC360CustomerProfileAdapterWithNull()
    {
        final C360CustomerProfile c360CustomerProfile = (C360CustomerProfile) c360CustomerProfileAdapter.adapt(null);
        assertThat(c360CustomerProfile).isNotNull();
        assertThat(c360CustomerProfile.getProfile()).isNull();
    }
}
