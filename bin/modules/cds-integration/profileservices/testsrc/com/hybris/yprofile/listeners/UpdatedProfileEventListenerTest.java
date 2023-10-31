package com.hybris.yprofile.listeners;

import com.hybris.yprofile.services.ProfileTransactionService;
import de.hybris.platform.commerceservices.consent.CommerceConsentService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.event.UpdatedProfileEvent;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.commerceservices.model.consent.ConsentModel;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
public class UpdatedProfileEventListenerTest {

    private static final int CLUSTER_ID = 1;
    private static final long CLUSTER_ISLAND_ID = 1L;
    private static final List<String> CLUSTER_GROUPS = Arrays.asList("test");
    private static final String TENANT_ID = "TENANT";
    private static final String CONSENT_REFERENCE = "consent-ref";
    private static final String SITE_ID = "testsite";

    @Mock
    private ProfileTransactionService profileTransactionService;

    @Mock
    private UpdatedProfileEvent event;

    @Mock
    private TenantService tenantService;

    @Mock
    private ClusterService clusterService;

    @Mock
    private BaseStoreModel baseStoreModel;

    @Mock
    private CommerceConsentService commerceConsentService;

    @InjectMocks
    private UpdatedProfileEventListener listener = new UpdatedProfileEventListener();

    private AutoCloseable closeable;

    @Before
    public void setUp() throws Exception {

        closeable = MockitoAnnotations.openMocks(this);
        listener.setCommerceConsentService(this.commerceConsentService);

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
    public void shouldNotHandleEventWithNullCustomer() {

        listener.onApplicationEvent(event);

        verify(profileTransactionService, never()).sendPersonalDetailsChangedEvent(any(CustomerModel.class), anyString(), anyString());
    }

    @Test
    public void shouldNotHandleEventWithNullCustomerConsentReference() {

        CustomerModel customer = mock(CustomerModel.class);
        when(event.getCustomer()).thenReturn(customer);

        listener.onApplicationEvent(event);

        verify(profileTransactionService, never()).sendPersonalDetailsChangedEvent(any(CustomerModel.class), anyString(), anyString());
    }

    @Test
    public void shouldHandleEvent() {

        CustomerModel customer = mock(CustomerModel.class);

        ConsentModel consentModel = mock(ConsentModel.class);
        when(consentModel.getConsentReference()).thenReturn(CONSENT_REFERENCE);
        when(commerceConsentService.getActiveConsent(any(), any())).thenReturn(consentModel);

        when(event.getCustomer()).thenReturn(customer);
        when(event.getSite()).thenReturn(mock(BaseSiteModel.class));
        when(baseStoreModel.getUid()).thenReturn(SITE_ID);
        when(event.getBaseStore()).thenReturn(baseStoreModel);

        doNothing().when(profileTransactionService).sendPersonalDetailsChangedEvent(any(CustomerModel.class), anyString(), anyString());

        listener.onApplicationEvent(event);

        verify(profileTransactionService, times(1)).sendPersonalDetailsChangedEvent(customer, SITE_ID, CONSENT_REFERENCE);

    }

}
