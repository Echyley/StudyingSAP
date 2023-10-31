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
import static org.mockito.Mockito.*;


/**
 * JUnit Test for class defaultAddressInterceptor check if the CustomerExportService will only be called in a specific
 * situation.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultAddressInterceptorTest {

  @InjectMocks
  private DefaultAddressInterceptor defaultAddressInterceptor;
  @Mock
  private AddressModel addressModel;
  @Mock
  private CustomerModel customerModel;
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

    // given
    given(addressModel.getOwner()).willReturn(customerModel);
    given(customerExportService.isCustomerReplicationEnabled()).willReturn(Boolean.TRUE);
    given(customerExportService.isClassCustomerModel(addressModel.getOwner())).willReturn(Boolean.TRUE);
    Mockito.lenient().when(customerModel.getSapContactID()).thenReturn(CONTACT_ID);
    Mockito.lenient().when(customerModel.getDefaultShipmentAddress()).thenReturn(addressModel);
    given(customerAddressReplicationUtilityService.isAddressReplicationRequired(any(), any(), any())).willReturn(true);
    given(customerAddressReplicationUtilityService.findDefaultAddress(any(), any())).willReturn(addressModel);

    // when
    defaultAddressInterceptor.onValidate(addressModel, ctx);

    // then
    verify(customerExportService, times(1)).isCustomerReplicationEnabled();
    verify(customerExportService, times(1)).isClassCustomerModel(addressModel.getOwner());
    verify(customerModel, times(1)).getSapContactID();
    verify(baseStoreService, times(1)).getCurrentBaseStore();
    verify(storeSessionFacade, times(1)).getCurrentLanguage();
    verify(customerExportService, times(1)).sendCustomerData(customerModel, null, null, addressModel);

  }

  @Test
  public void testCustomerReplicationDisabled() throws InterceptorException {

    // given
    given(addressModel.getOwner()).willReturn(customerModel);
    Mockito.lenient().when(customerExportService.isCustomerReplicationEnabled()).thenReturn(Boolean.FALSE);
    Mockito.lenient().when(customerExportService.isClassCustomerModel(addressModel.getOwner())).thenReturn(Boolean.TRUE);
    Mockito.lenient().when(customerModel.getSapContactID()).thenReturn(CONTACT_ID);
    Mockito.lenient().when(customerModel.getDefaultShipmentAddress()).thenReturn(addressModel);
    Mockito.lenient().when(customerAddressReplicationUtilityService.isAddressReplicationRequired(any(), any(), any())).thenReturn(true);
    Mockito.lenient().when(customerAddressReplicationUtilityService.findDefaultAddress(any(), any())).thenReturn(addressModel);

    // when
    defaultAddressInterceptor.onValidate(addressModel, ctx);

    // then
    verify(customerExportService, times(1)).isCustomerReplicationEnabled();
    verify(customerExportService, never()).isClassCustomerModel(addressModel.getOwner());
    verify(customerModel, never()).getSapContactID();
    verify(baseStoreService, never()).getCurrentBaseStore();
    verify(storeSessionFacade, never()).getCurrentLanguage();
    verify(customerExportService, never()).sendCustomerData(customerModel, null, null, addressModel);

  }

  @Test
  public void testAddressOwnerNotCustomerModel() throws InterceptorException {

    // given
    given(addressModel.getOwner()).willReturn(customerModel);
    given(customerExportService.isCustomerReplicationEnabled()).willReturn(Boolean.TRUE);
    Mockito.lenient().when(customerExportService.isClassCustomerModel(addressModel.getOwner())).thenReturn(Boolean.FALSE);
    Mockito.lenient().when(customerModel.getSapContactID()).thenReturn(CONTACT_ID);
    Mockito.lenient().when(customerModel.getDefaultShipmentAddress()).thenReturn(addressModel);
    Mockito.lenient().when(customerAddressReplicationUtilityService.isAddressReplicationRequired(any(), any(), any())).thenReturn(true);
    Mockito.lenient().when(customerAddressReplicationUtilityService.findDefaultAddress(any(), any())).thenReturn(addressModel);

    // when
    defaultAddressInterceptor.onValidate(addressModel, ctx);

    // then
    verify(customerExportService, times(1)).isCustomerReplicationEnabled();
    verify(customerExportService, times(1)).isClassCustomerModel(addressModel.getOwner());
    verify(customerModel, never()).getSapContactID();
    verify(baseStoreService, never()).getCurrentBaseStore();
    verify(storeSessionFacade, never()).getCurrentLanguage();
    verify(customerExportService, never()).sendCustomerData(customerModel, null, null, addressModel);

  }

  @Test
  public void testSapContactIDNull() throws InterceptorException {

    // given
    given(addressModel.getOwner()).willReturn(customerModel);
    given(customerExportService.isCustomerReplicationEnabled()).willReturn(Boolean.TRUE);
    given(customerExportService.isClassCustomerModel(addressModel.getOwner())).willReturn(Boolean.TRUE);
    Mockito.lenient().when(customerModel.getSapContactID()).thenReturn(null);
    Mockito.lenient().when(customerModel.getDefaultShipmentAddress()).thenReturn(addressModel);
    Mockito.lenient().when(customerAddressReplicationUtilityService.isAddressReplicationRequired(any(), any(), any())).thenReturn(true);
    Mockito.lenient().when(customerAddressReplicationUtilityService.findDefaultAddress(any(), any())).thenReturn(addressModel);

    // when
    defaultAddressInterceptor.onValidate(addressModel, ctx);

    // then
    verify(customerExportService, times(1)).isCustomerReplicationEnabled();
    verify(customerExportService, times(1)).isClassCustomerModel(addressModel.getOwner());
    verify(customerModel, times(1)).getSapContactID();
    verify(baseStoreService, never()).getCurrentBaseStore();
    verify(storeSessionFacade, never()).getCurrentLanguage();
    verify(customerExportService, never()).sendCustomerData(customerModel, null, null, addressModel);

  }

  @Test
  public void testUnsupportedFieldModified() throws InterceptorException {

    // given
    given(addressModel.getOwner()).willReturn(customerModel);
    given(customerExportService.isCustomerReplicationEnabled()).willReturn(Boolean.TRUE);
    given(customerExportService.isClassCustomerModel(addressModel.getOwner())).willReturn(Boolean.TRUE);
    Mockito.lenient().when(customerModel.getSapContactID()).thenReturn(CONTACT_ID);
    Mockito.lenient().when(customerModel.getDefaultShipmentAddress()).thenReturn(addressModel);
    Mockito.lenient().when(customerAddressReplicationUtilityService.isAddressReplicationRequired(any(), any(), any())).thenReturn(false);
    Mockito.lenient().when(customerAddressReplicationUtilityService.findDefaultAddress(any(), any())).thenReturn(addressModel);

    // when
    defaultAddressInterceptor.onValidate(addressModel, ctx);

    // then
    verify(customerExportService, times(1)).isCustomerReplicationEnabled();
    verify(customerExportService, times(1)).isClassCustomerModel(addressModel.getOwner());
    verify(customerModel, times(1)).getSapContactID();
    verify(baseStoreService, never()).getCurrentBaseStore();
    verify(storeSessionFacade, never()).getCurrentLanguage();
    verify(customerExportService, never()).sendCustomerData(customerModel, null, null, addressModel);

  }

  @Test
  public void testExportSessionLanguage() throws InterceptorException {

    // given
    given(addressModel.getOwner()).willReturn(customerModel);
    given(customerExportService.isCustomerReplicationEnabled()).willReturn(Boolean.TRUE);
    given(customerExportService.isClassCustomerModel(addressModel.getOwner())).willReturn(Boolean.TRUE);
    Mockito.lenient().when(customerModel.getSapContactID()).thenReturn(CONTACT_ID);
    Mockito.lenient().when(customerModel.getDefaultShipmentAddress()).thenReturn(addressModel);
    given(storeSessionFacade.getCurrentLanguage()).willReturn(languageData);
    given(storeSessionFacade.getCurrentLanguage().getIsocode()).willReturn("DE");
    given(customerAddressReplicationUtilityService.isAddressReplicationRequired(any(), any(), any())).willReturn(true);
    given(customerAddressReplicationUtilityService.findDefaultAddress(any(), any())).willReturn(addressModel);

    // when
    defaultAddressInterceptor.onValidate(addressModel, ctx);

    // then
    verify(customerExportService, times(1)).isCustomerReplicationEnabled();
    verify(customerExportService, times(1)).isClassCustomerModel(addressModel.getOwner());
    verify(customerModel, times(1)).getSapContactID();
    verify(baseStoreService, times(1)).getCurrentBaseStore();
    verify(languageData, times(1)).getIsocode();
    verify(customerExportService, times(1)).sendCustomerData(customerModel, null, "DE", addressModel);

  }

  @Test
  public void testExportBaseStoreUid() throws InterceptorException {

    // given
    given(addressModel.getOwner()).willReturn(customerModel);
    given(customerExportService.isCustomerReplicationEnabled()).willReturn(Boolean.TRUE);
    given(customerExportService.isClassCustomerModel(addressModel.getOwner())).willReturn(Boolean.TRUE);
    given(customerModel.getSapContactID()).willReturn(CONTACT_ID);
    Mockito.lenient().when(customerModel.getDefaultShipmentAddress()).thenReturn(addressModel);
    given(baseStoreService.getCurrentBaseStore()).willReturn(baseStore);
    given(baseStore.getUid()).willReturn("ELECTRONICS");
    given(customerAddressReplicationUtilityService.isAddressReplicationRequired(any(), any(), any())).willReturn(true);
    given(customerAddressReplicationUtilityService.findDefaultAddress(any(), any())).willReturn(addressModel);

    // when
    defaultAddressInterceptor.onValidate(addressModel, ctx);

    // then
    verify(customerExportService, times(1)).isCustomerReplicationEnabled();
    verify(customerExportService, times(1)).isClassCustomerModel(addressModel.getOwner());
    verify(customerModel, times(1)).getSapContactID();
    verify(baseStoreService, times(2)).getCurrentBaseStore();
    verify(baseStore, times(1)).getUid();
    verify(customerExportService, times(1)).sendCustomerData(customerModel, "ELECTRONICS", null, addressModel);

  }

  @Test
  public void testNotShipmentAddress() throws InterceptorException {

    // given
    given(addressModel.getOwner()).willReturn(customerModel);
    given(customerExportService.isCustomerReplicationEnabled()).willReturn(Boolean.TRUE);
    given(customerExportService.isClassCustomerModel(addressModel.getOwner())).willReturn(Boolean.TRUE);
    Mockito.lenient().when(customerModel.getSapContactID()).thenReturn(CONTACT_ID);
    Mockito.lenient().when(customerModel.getDefaultShipmentAddress()).thenReturn(addressModel);

    // when
    defaultAddressInterceptor.onValidate(addressModel, ctx);

    // then
    verify(customerExportService, times(1)).isCustomerReplicationEnabled();
    verify(customerExportService, times(1)).isClassCustomerModel(addressModel.getOwner());
    verify(customerModel, times(1)).getSapContactID();
    verify(baseStoreService, never()).getCurrentBaseStore();
    verify(storeSessionFacade, never()).getCurrentLanguage();
    verify(customerExportService, never()).sendCustomerData(customerModel, null, null, addressModel);

  }

}