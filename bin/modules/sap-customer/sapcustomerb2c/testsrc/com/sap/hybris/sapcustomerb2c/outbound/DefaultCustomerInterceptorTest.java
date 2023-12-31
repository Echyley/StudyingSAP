/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcustomerb2c.outbound;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.commercefacades.storesession.impl.DefaultStoreSessionFacade;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static com.sap.hybris.sapcustomerb2c.CustomerConstantsUtils.CONTACT_ID;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;


/**
 * JUnit Test for class DefaultCustomerInterceptor check if the CustomerExportService will only be called in a specific
 * situation.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCustomerInterceptorTest {

  @InjectMocks
  private DefaultCustomerInterceptor defaultCustomerInterceptor;
  @Mock
  private CustomerModel customerModel;
  @Mock
  private AddressModel defaultShipmentAddress;
  @Mock
  private InterceptorContext ctx;
  @Mock
  private BaseStoreService baseStoreService;
  @Mock
  private DefaultStoreSessionFacade storeSessionFacade;
  @Mock
  private CustomerExportService customerExportService;
  @Mock
  private LanguageData languageData;
  @Mock
  private BaseStoreModel baseStore;
  @Mock
  private CustomerAddressReplicationUtilityService customerAddressReplicationUtilityService;

  @Test
  public void testSuccessfulReplication() throws InterceptorException {

    given(customerExportService.isCustomerReplicationEnabled()).willReturn(Boolean.TRUE);
    given(customerExportService.isClassCustomerModel(customerModel)).willReturn(Boolean.TRUE);
    given(customerModel.getSapContactID()).willReturn(CONTACT_ID);
    given(customerModel.getDefaultShipmentAddress()).willReturn(defaultShipmentAddress);
    given(customerAddressReplicationUtilityService.isCustomerReplicationRequired(any(), any(), any())).willReturn(true);
    given(customerAddressReplicationUtilityService.findDefaultAddress(any(), any())).willReturn(defaultShipmentAddress);

    // when
    defaultCustomerInterceptor.onValidate(customerModel, ctx);

    // then
    verify(customerExportService, times(1)).isCustomerReplicationEnabled();
    verify(customerExportService, times(1)).isClassCustomerModel(customerModel);
    verify(customerModel, times(1)).getSapContactID();
    verify(baseStoreService, times(1)).getCurrentBaseStore();
    verify(storeSessionFacade, times(1)).getCurrentLanguage();
    verify(customerExportService, times(1)).sendCustomerData(customerModel, null, null, defaultShipmentAddress);
  }

  @Test
  public void testCustomerReplicationDisabled() throws InterceptorException {

    given(customerExportService.isCustomerReplicationEnabled()).willReturn(Boolean.FALSE);
    Mockito.lenient().when(customerExportService.isClassCustomerModel(customerModel)).thenReturn(Boolean.TRUE);
    Mockito.lenient().when(customerModel.getCustomerID()).thenReturn(CONTACT_ID);
    Mockito.lenient().when(ctx.isModified(customerModel, CustomerModel.DEFAULTSHIPMENTADDRESS)).thenReturn(true);
    Mockito.lenient().when(customerModel.getDefaultShipmentAddress()).thenReturn(defaultShipmentAddress);

    // when
    defaultCustomerInterceptor.onValidate(customerModel, ctx);

    // then
    verify(customerExportService, times(1)).isCustomerReplicationEnabled();
    verify(customerExportService, never()).isClassCustomerModel(customerModel);
    verify(customerModel, never()).getSapContactID();
    verify(baseStoreService, never()).getCurrentBaseStore();
    verify(storeSessionFacade, never()).getCurrentLanguage();
    verify(customerExportService, never()).sendCustomerData(customerModel, null, null, defaultShipmentAddress);

  }

  @Test
  public void testNotCustomerModel() throws InterceptorException {

    given(customerExportService.isCustomerReplicationEnabled()).willReturn(Boolean.TRUE);
    given(customerExportService.isClassCustomerModel(customerModel)).willReturn(Boolean.FALSE);
    Mockito.lenient().when(customerModel.getCustomerID()).thenReturn(CONTACT_ID);
    Mockito.lenient().when(ctx.isModified(customerModel, CustomerModel.DEFAULTSHIPMENTADDRESS)).thenReturn(true);
    Mockito.lenient().when(customerModel.getDefaultShipmentAddress()).thenReturn(defaultShipmentAddress);

    // when
    defaultCustomerInterceptor.onValidate(customerModel, ctx);

    // then
    verify(customerExportService, times(1)).isCustomerReplicationEnabled();
    verify(customerExportService, times(1)).isClassCustomerModel(customerModel);
    verify(customerModel, never()).getSapContactID();
    verify(baseStoreService, never()).getCurrentBaseStore();
    verify(storeSessionFacade, never()).getCurrentLanguage();
    verify(customerExportService, never()).sendCustomerData(customerModel, null, null, defaultShipmentAddress);

  }

  @Test
  public void testSapContactIDNull() throws InterceptorException {

    given(customerExportService.isCustomerReplicationEnabled()).willReturn(Boolean.TRUE);
    given(customerExportService.isClassCustomerModel(customerModel)).willReturn(Boolean.TRUE);
    given(customerModel.getSapContactID()).willReturn(null);
    Mockito.lenient().when(ctx.isModified(customerModel, CustomerModel.DEFAULTSHIPMENTADDRESS)).thenReturn(true);
    Mockito.lenient().when(customerModel.getDefaultShipmentAddress()).thenReturn(defaultShipmentAddress);

    // when
    defaultCustomerInterceptor.onValidate(customerModel, ctx);

    // then
    verify(customerExportService, times(1)).isCustomerReplicationEnabled();
    verify(customerExportService, times(1)).isClassCustomerModel(customerModel);
    verify(customerModel, times(1)).getSapContactID();
    verify(baseStoreService, never()).getCurrentBaseStore();
    verify(storeSessionFacade, never()).getCurrentLanguage();
    verify(customerExportService, never()).sendCustomerData(customerModel, null, null, defaultShipmentAddress);

  }

  @Test
  public void testUnsupportedFieldModified() throws InterceptorException {

    given(customerExportService.isCustomerReplicationEnabled()).willReturn(Boolean.TRUE);
    given(customerExportService.isClassCustomerModel(customerModel)).willReturn(Boolean.TRUE);
    given(customerModel.getSapContactID()).willReturn(CONTACT_ID);
    Mockito.lenient().when(ctx.isModified(customerModel, CustomerModel.DEFAULTPAYMENTINFO)).thenReturn(true);
    Mockito.lenient().when(customerModel.getDefaultShipmentAddress()).thenReturn(defaultShipmentAddress);

    // when
    defaultCustomerInterceptor.onValidate(customerModel, ctx);

    // then
    verify(customerExportService, times(1)).isCustomerReplicationEnabled();
    verify(customerExportService, times(1)).isClassCustomerModel(customerModel);
    verify(customerModel, times(1)).getSapContactID();
    verify(baseStoreService, never()).getCurrentBaseStore();
    verify(storeSessionFacade, never()).getCurrentLanguage();
    verify(customerExportService, never()).sendCustomerData(customerModel, null, null, defaultShipmentAddress);
  }

  @Test
  public void testExportSessionLanguage() throws InterceptorException {

    given(customerExportService.isCustomerReplicationEnabled()).willReturn(Boolean.TRUE);
    given(customerExportService.isClassCustomerModel(customerModel)).willReturn(Boolean.TRUE);
    given(customerModel.getSapContactID()).willReturn(CONTACT_ID);
    given(customerModel.getDefaultShipmentAddress()).willReturn(defaultShipmentAddress);
    given(storeSessionFacade.getCurrentLanguage()).willReturn(languageData);
    given(storeSessionFacade.getCurrentLanguage().getIsocode()).willReturn("DE");
    given(customerAddressReplicationUtilityService.isCustomerReplicationRequired(any(), any(), any())).willReturn(true);
    given(customerAddressReplicationUtilityService.findDefaultAddress(any(), any())).willReturn(defaultShipmentAddress);

    // when
    defaultCustomerInterceptor.onValidate(customerModel, ctx);

    // then
    verify(customerExportService, times(1)).isCustomerReplicationEnabled();
    verify(customerExportService, times(1)).isClassCustomerModel(customerModel);
    verify(customerModel, times(1)).getSapContactID();
    verify(baseStoreService, times(1)).getCurrentBaseStore();
    verify(languageData, times(1)).getIsocode();
    verify(customerExportService, times(1)).sendCustomerData(customerModel, null, "DE", defaultShipmentAddress);

  }

  @Test
  public void testExportBaseStoreUid() throws InterceptorException {

    given(customerExportService.isCustomerReplicationEnabled()).willReturn(Boolean.TRUE);
    given(customerExportService.isClassCustomerModel(customerModel)).willReturn(Boolean.TRUE);
    given(customerModel.getSapContactID()).willReturn(CONTACT_ID);
    given(customerModel.getDefaultShipmentAddress()).willReturn(defaultShipmentAddress);
    given(baseStoreService.getCurrentBaseStore()).willReturn(baseStore);
    given(baseStore.getUid()).willReturn("ELECTRONICS");
    given(customerAddressReplicationUtilityService.isCustomerReplicationRequired(any(), any(), any())).willReturn(true);
    given(customerAddressReplicationUtilityService.findDefaultAddress(any(), any())).willReturn(defaultShipmentAddress);

    // when
    defaultCustomerInterceptor.onValidate(customerModel, ctx);

    // then
    verify(customerExportService, times(1)).isCustomerReplicationEnabled();
    verify(customerExportService, times(1)).isClassCustomerModel(customerModel);
    verify(customerModel, times(1)).getSapContactID();
    verify(baseStoreService, times(2)).getCurrentBaseStore();
    verify(baseStore, times(1)).getUid();
    verify(customerExportService, times(1)).sendCustomerData(customerModel, "ELECTRONICS", null, defaultShipmentAddress);

  }

}