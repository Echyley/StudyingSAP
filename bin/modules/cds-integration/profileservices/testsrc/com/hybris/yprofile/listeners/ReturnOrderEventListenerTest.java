package com.hybris.yprofile.listeners;

import com.hybris.yprofile.services.ProfileTransactionService;
import de.hybris.platform.commerceservices.consent.CommerceConsentService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.commerceservices.event.CreateReturnEvent;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.cluster.ClusterService;
import de.hybris.platform.servicelayer.event.impl.EventScope;
import de.hybris.platform.servicelayer.tenant.TenantService;
import de.hybris.platform.site.BaseSiteService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
public class ReturnOrderEventListenerTest {

    private static final int CLUSTER_ID = 1;
    private static final long CLUSTER_ISLAND_ID = 1L;
    private static final List<String> CLUSTER_GROUPS = Arrays.asList("test");
    private static final String TENANT_ID = "TENANT";
    private static final String CONSENT_REFERENCE = "consent-ref";
    private static final String SITE_ID = "testsite";

    @Mock
    private ProfileTransactionService profileTransactionService;

    @Mock
    private BaseSiteService baseSiteService;

    @Mock
    private CreateReturnEvent event;

    @Mock
    private TenantService tenantService;

    @Mock
    private ClusterService clusterService;

    @Mock
    private CommerceConsentService commerceConsentService;

    @InjectMocks
    private ReturnOrderEventListener listener = new ReturnOrderEventListener();

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
    public void shouldNotHandleEventWithNullCustomer() {
        BaseSiteModel baseSiteModel = mock(BaseSiteModel.class);
        when(baseSiteModel.getUid()).thenReturn(SITE_ID);
        OrderModel orderModel = mock(OrderModel.class);
        when(orderModel.getSite()).thenReturn(baseSiteModel);

        ReturnRequestModel returnRequestModel = mock(ReturnRequestModel.class);
        when(returnRequestModel.getOrder()).thenReturn(orderModel);
        
        when(event.getReturnRequest()).thenReturn(returnRequestModel);
        listener.onApplicationEvent(event);

        verify(profileTransactionService, never()).sendReturnOrderEvent(any(ReturnRequestModel.class));
    }

    @Test
    public void shouldNotHandleEventWithNullCustomerConsentReference() {

        BaseSiteModel baseSiteModel = mock(BaseSiteModel.class);
        when(baseSiteModel.getUid()).thenReturn(SITE_ID);
        OrderModel orderModel = mock(OrderModel.class);
        when(orderModel.getSite()).thenReturn(baseSiteModel);
        UserModel userModel = mock(UserModel.class);
        when(orderModel.getUser()).thenReturn(userModel);
        ReturnRequestModel returnRequestModel = mock(ReturnRequestModel.class);
        when(returnRequestModel.getOrder()).thenReturn(orderModel);

        when(event.getReturnRequest()).thenReturn(returnRequestModel);


        listener.onApplicationEvent(event);

        verify(profileTransactionService, never()).sendReturnOrderEvent(any(ReturnRequestModel.class));
    }

    @Test
    public void shouldHandleEvent() {

        BaseSiteModel baseSiteModel = mock(BaseSiteModel.class);
        when(baseSiteModel.getUid()).thenReturn(SITE_ID);
        when(baseSiteModel.getChannel()).thenReturn(SiteChannel.B2C);
        OrderModel orderModel = mock(OrderModel.class);
        when(orderModel.getSite()).thenReturn(baseSiteModel);
        CustomerModel customerModel = mock(CustomerModel.class);
        when(orderModel.getUser()).thenReturn(customerModel);
        when(orderModel.getConsentReference()).thenReturn(CONSENT_REFERENCE);
        ReturnRequestModel returnRequestModel = mock(ReturnRequestModel.class);
        when(returnRequestModel.getOrder()).thenReturn(orderModel);

        when(event.getReturnRequest()).thenReturn(returnRequestModel);

        doNothing().when(profileTransactionService).sendReturnOrderEvent(any(ReturnRequestModel.class));
        doNothing().when(baseSiteService).setCurrentBaseSite(any(BaseSiteModel.class), anyBoolean());

        listener.onApplicationEvent(event);

        verify(profileTransactionService, times(1)).sendReturnOrderEvent(returnRequestModel);

    }

}
