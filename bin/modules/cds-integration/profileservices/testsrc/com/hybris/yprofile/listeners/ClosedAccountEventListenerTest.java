package com.hybris.yprofile.listeners;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.consent.CommerceConsentService;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.commerceservices.event.ClosedAccountEvent;
import de.hybris.platform.commerceservices.model.consent.ConsentModel;
import de.hybris.platform.commerceservices.model.consent.ConsentTemplateModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.cluster.ClusterService;
import de.hybris.platform.servicelayer.event.impl.EventScope;
import de.hybris.platform.servicelayer.tenant.TenantService;
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
public class ClosedAccountEventListenerTest {

    private static final int CLUSTER_ID = 1;
    private static final long CLUSTER_ISLAND_ID = 1L;
    private static final List<String> CLUSTER_GROUPS = Arrays.asList("test");
    private static final String TENANT_ID = "TENANT";
    private static final String CONSENT_REFERENCE = "consent-ref";
    private static final String SITE_ID = "testsite";

    @Mock
    private CommerceConsentService commerceConsentService;

    @Mock
    private ClosedAccountEvent event;

    @Mock
    private TenantService tenantService;

    @Mock
    private ClusterService clusterService;

    @InjectMocks
    private ClosedAccountEventListener listener = new ClosedAccountEventListener();

    private AutoCloseable closeable;

    @Before
    public void setUp() throws Exception {

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
    public void shouldNotHandleEventWithNullSite() {

        listener.onApplicationEvent(event);

        verify(commerceConsentService, never()).withdrawConsent(any(ConsentModel.class));
    }

    @Test
    public void shouldNotHandleEventWithNullCustomer() {

        BaseSiteModel baseSiteModel = mock(BaseSiteModel.class);
        when(baseSiteModel.getUid()).thenReturn(SITE_ID);
        when(event.getSite()).thenReturn(baseSiteModel);
        listener.onApplicationEvent(event);

        verify(commerceConsentService, never()).withdrawConsent(any(ConsentModel.class));
    }

    @Test
    public void shouldNotHandleEventWithNullCustomerConsentReference() {

        BaseSiteModel baseSiteModel = mock(BaseSiteModel.class);
        when(baseSiteModel.getUid()).thenReturn(SITE_ID);
        when(event.getSite()).thenReturn(baseSiteModel);
        CustomerModel customer = mock(CustomerModel.class);
        when(event.getCustomer()).thenReturn(customer);

        listener.onApplicationEvent(event);

        verify(commerceConsentService, never()).withdrawConsent(any(ConsentModel.class));
    }

    @Test
    public void shouldHandleEvent() {

        BaseSiteModel baseSiteModel = mock(BaseSiteModel.class);
        when(baseSiteModel.getUid()).thenReturn(SITE_ID);
        when(baseSiteModel.getChannel()).thenReturn(SiteChannel.B2C);
        when(event.getSite()).thenReturn(baseSiteModel);
        CustomerModel customer = mock(CustomerModel.class);
        when(event.getCustomer()).thenReturn(customer);

        ConsentModel consentModel = mock(ConsentModel.class);
        when(consentModel.getConsentReference()).thenReturn(CONSENT_REFERENCE);
        when(commerceConsentService.getLatestConsentTemplate(anyString(), any())).thenReturn(mock(ConsentTemplateModel.class));
        when(commerceConsentService.getActiveConsent(any(), any())).thenReturn(consentModel);
        doNothing().when(commerceConsentService).withdrawConsent(any(ConsentModel.class));

        listener.onApplicationEvent(event);

        verify(commerceConsentService, times(1)).withdrawConsent(any(ConsentModel.class));
        //verify(commerceConsentService, times(1)).withdrawConsent(any());

    }

}
