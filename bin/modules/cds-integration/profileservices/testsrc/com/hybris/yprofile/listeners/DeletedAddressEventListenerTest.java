/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.yprofile.listeners;

import com.hybris.yprofile.services.ProfileTransactionService;
import de.hybris.platform.commerceservices.consent.CommerceConsentService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.event.DeletedAddressEvent;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.commerceservices.model.consent.ConsentModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.servicelayer.cluster.ClusterService;
import de.hybris.platform.servicelayer.event.impl.EventScope;
import de.hybris.platform.servicelayer.tenant.TenantService;
import de.hybris.platform.store.BaseStoreModel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;

import static org.mockito.Mockito.*;

@UnitTest
public class DeletedAddressEventListenerTest {

    private static final int CLUSTER_ID = 1;
    private static final long CLUSTER_ISLAND_ID = 1L;
    private static final List<String> CLUSTER_GROUPS = Arrays.asList("test");
    private static final String TENANT_ID = "TENANT";

    private static final String CONSENT_REFERENCE = "myConsentReference";
    private static final String BASE_SITE_ID = "1234abc";

    @InjectMocks
    private DeletedAddressEventListener listener = new DeletedAddressEventListener();

    @Mock
    private ProfileTransactionService profileTransactionService;

    @Mock
    private BaseStoreModel baseStoreModel;

    @Mock
    private CustomerModel customerModel;

    @Mock
    private DeletedAddressEvent event;

    @Mock
    private TenantService tenantService;

    @Mock
    private ClusterService clusterService;

    @Mock
    private CommerceConsentService commerceConsentService;

    private AutoCloseable closeable;

    @Before
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        
        when(clusterService.getClusterId()).thenReturn(CLUSTER_ID);
        when(clusterService.getClusterIslandId()).thenReturn(CLUSTER_ISLAND_ID);
        when(clusterService.getClusterGroups()).thenReturn(CLUSTER_GROUPS);

        when(tenantService.getCurrentTenantId()).thenReturn(TENANT_ID);

        EventScope eventScope = new EventScope();
        eventScope.setClusterId(CLUSTER_ID);
        eventScope.setClusterIslandId(CLUSTER_ISLAND_ID);
        eventScope.setTenantId(TENANT_ID);

        when(event.getScope()).thenReturn(eventScope);
    }

    @After
    public void releaseMocks() throws Exception {
        closeable.close();
    }

    @Test
    public void verifyEmptyAddressSent() {
        ConsentModel consentModel = mock(ConsentModel.class);
        when(consentModel.getConsentReference()).thenReturn(CONSENT_REFERENCE);
        when(commerceConsentService.getActiveConsent(any(), any())).thenReturn(consentModel);

        when(baseStoreModel.getUid()).thenReturn(BASE_SITE_ID);
        when(event.getSite()).thenReturn(mock(BaseSiteModel.class));
        when(event.getCustomer()).thenReturn(customerModel);
        when(event.getBaseStore()).thenReturn(baseStoreModel);
        when(customerModel.getAddresses()).thenReturn(Collections.emptyList());
        listener.onApplicationEvent(event);
        verify(profileTransactionService, times(1)).sendAddressDeletedEvent(customerModel,
                BASE_SITE_ID, CONSENT_REFERENCE);
    }

    @Test
    public void shouldNotHandleEventWithNullCustomer() {

        listener.onApplicationEvent(event);

        verify(profileTransactionService, never()).sendAddressDeletedEvent(any(CustomerModel.class), anyString(), anyString());
    }

    @Test
    public void shouldNotHandleEventWithNullCustomerConsentReference() {

        CustomerModel customer = mock(CustomerModel.class);
        when(event.getCustomer()).thenReturn(customer);

        listener.onApplicationEvent(event);

        verify(profileTransactionService, never()).sendAddressDeletedEvent(any(CustomerModel.class), anyString(), anyString());
    }
}
