/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.assistedservicefacades.customer360.AdditionalInformationFrameworkFacade;
import de.hybris.platform.assistedservicefacades.customer360.Fragment;
import de.hybris.platform.assistedservicefacades.customer360.StoreLocationData;
import de.hybris.platform.assistedservicepromotionfacades.customer360.CSACouponData;
import de.hybris.platform.assistedservicewebservices.adapters.C360CouponListAdapter;
import de.hybris.platform.assistedservicewebservices.adapters.C360FragmentDataAdapter;
import de.hybris.platform.assistedservicewebservices.dto.C360CustomerProductInterestList;
import de.hybris.platform.assistedservicewebservices.dto.C360CustomerProductInterestWsDTO;
import de.hybris.platform.assistedservicewebservices.dto.C360ProductWsDTO;
import de.hybris.platform.assistedservicewebservices.dto.C360CouponWsDTO;
import de.hybris.platform.assistedservicewebservices.dto.C360CouponList;
import de.hybris.platform.assistedservicewebservices.dto.Customer360QueryListWsDTO;
import de.hybris.platform.assistedservicewebservices.dto.Customer360QueryWsDTO;
import de.hybris.platform.assistedservicewebservices.dto.C360StoreLocation;
import de.hybris.platform.assistedservicewebservices.dto.Customer360DataWsDTO;
import de.hybris.platform.assistedservicewebservices.dto.Customer360ListWsDTO;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.RequestParameterException;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.customerinterestsfacades.data.ProductInterestEntryData;
import de.hybris.platform.customerinterestsfacades.data.ProductInterestRelationData;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Test suite for {@link AssistedServiceCustomer360Controller}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AssistedServiceCustomer360ControllerTest {
    private static final String QUERY_TYPE = "C360Type";
    private static final String BASE_SITE = "baseSiteID";
    private static final String USER_ID = "userID";
    private static final String FRAGMENT_ID = "fragmentID";
    private static final String SECTION_ID = "sectionID";
    private static final String STORE_LOCATION_ADDRESS = "address";
    private static final String STORE_LOCATION_TYPE = "c360StoreLocation";
    private static final String PRODUCT_CODE = "productCode";
    private static final String CUSTOMER_PRODUCT_INTEREST_TYPE = "c360CustomerProductInterestList";

    @Mock
    private AdditionalInformationFrameworkFacade additionalInformationFrameworkFacade;
    @Mock
    private Validator customer360QueryListValidator;
    @Mock
    private DataMapper dataMapper;
    @Mock
    private C360FragmentDataAdapter c360FragmentDataAdapter;
    @Mock
    private UserService userService;

    @Mock
    private Fragment csaCustomerCouponsFragment;

    @Mock
    private List<Fragment> promotionSectionFragments;

    private Map<String, String> dataTypeSectionMap;
    private Map<String, String> dataTypeFragmentMap;

    private Map<String, C360FragmentDataAdapter> fragmentDataAdaptersMap;

    private AssistedServiceCustomer360Controller assistedServiceCustomer360Controller;

    @Mock
    private C360CouponListAdapter couponListAdapter;

    @Before
    public void setUp() {
        assistedServiceCustomer360Controller = new AssistedServiceCustomer360Controller();
        assistedServiceCustomer360Controller.setDataMapper(dataMapper);
        assistedServiceCustomer360Controller
                .setCustomer360QueryListValidator(customer360QueryListValidator);
        assistedServiceCustomer360Controller
                .setAdditionalInformationFrameworkFacade(additionalInformationFrameworkFacade);
        assistedServiceCustomer360Controller
                .setUserService(userService);

        dataTypeFragmentMap = new HashMap<>();
        dataTypeFragmentMap.put(QUERY_TYPE, FRAGMENT_ID);
        assistedServiceCustomer360Controller.setDataTypeFragmentMap(dataTypeFragmentMap);

        dataTypeSectionMap = new HashMap<>();
        dataTypeSectionMap.put(QUERY_TYPE, SECTION_ID);
        assistedServiceCustomer360Controller.setDataTypeSectionMap(dataTypeSectionMap);

        fragmentDataAdaptersMap = new HashMap<>();
        fragmentDataAdaptersMap.put(QUERY_TYPE, c360FragmentDataAdapter);
        assistedServiceCustomer360Controller.setFragmentDataAdaptersMap(fragmentDataAdaptersMap);

        assistedServiceCustomer360Controller.setPromotionSectionFragments(promotionSectionFragments);
    }

    @Test
    public void testSearchCustomer360WithAdapter() {
        final String QUERY_TYPE = "c360CouponList";
        dataTypeFragmentMap.put(QUERY_TYPE, FRAGMENT_ID);
        dataTypeSectionMap.put(QUERY_TYPE, SECTION_ID);
        final Customer360QueryWsDTO customer360QueryWsDTO = new Customer360QueryWsDTO();
        customer360QueryWsDTO.setType(QUERY_TYPE);
        final Customer360QueryListWsDTO customer360QueryListWsDTO = new Customer360QueryListWsDTO();
        customer360QueryListWsDTO.setCustomer360Queries(Arrays.asList(customer360QueryWsDTO));

        CustomerModel customer = new CustomerModel();
        when(userService.getCurrentUser()).thenReturn(customer);

        final Fragment fragment = new Fragment();
        final CSACouponData csaCouponData = new CSACouponData();
        csaCouponData.setCode("test");
        fragment.setData(Arrays.asList(csaCouponData));

        final C360CouponWsDTO c360CouponWsDTO = new C360CouponWsDTO();
        c360CouponWsDTO.setCode("csa_coupon_test");
        final C360CouponList c360CouponList = new C360CouponList();
        c360CouponList.setCoupons(Arrays.asList(c360CouponWsDTO));

        fragmentDataAdaptersMap.put(QUERY_TYPE, couponListAdapter);

        when(additionalInformationFrameworkFacade.getFragment(eq(SECTION_ID), eq(FRAGMENT_ID), any()))
                .thenReturn(fragment);
        when(couponListAdapter.adapt(Arrays.asList(csaCouponData))).thenReturn(c360CouponList);

        final Customer360ListWsDTO response =  assistedServiceCustomer360Controller
                .searchCustomer360(BASE_SITE, USER_ID, customer360QueryListWsDTO);

        verify(additionalInformationFrameworkFacade).getFragment(eq(SECTION_ID), eq(FRAGMENT_ID), any());
        verify(couponListAdapter).adapt(Arrays.asList(csaCouponData));
        assertThat(response).isNotNull();
        assertThat(response.getValue()
                .get(0)).isSameAs(c360CouponList);
        verify(promotionSectionFragments, times(1)).contains(any());
    }

    @Test
    public void testSearchStoreLocationByCustomer360() {
        final Customer360QueryWsDTO customer360QueryWsDTO = new Customer360QueryWsDTO();
        customer360QueryWsDTO.setType(QUERY_TYPE);
        final Customer360QueryListWsDTO customer360QueryListWsDTO = new Customer360QueryListWsDTO();
        customer360QueryListWsDTO
                .setCustomer360Queries(Arrays.asList(customer360QueryWsDTO));

        CustomerModel customer = new CustomerModel();
        when(userService.getCurrentUser()).thenReturn(customer);

        final Fragment fragment = new Fragment();
        final StoreLocationData storeLocationData = new StoreLocationData();
        storeLocationData.setAddress(STORE_LOCATION_ADDRESS);
        fragment.setData(storeLocationData);

        final C360StoreLocation c360StoreLocation = new C360StoreLocation();
        c360StoreLocation.setAddress(STORE_LOCATION_ADDRESS);
        c360StoreLocation.setType(STORE_LOCATION_TYPE);

        fragmentDataAdaptersMap = new HashMap<>();
        fragmentDataAdaptersMap.put(QUERY_TYPE, null);
        assistedServiceCustomer360Controller.setFragmentDataAdaptersMap(fragmentDataAdaptersMap);
        when(additionalInformationFrameworkFacade.getFragment(eq(SECTION_ID), eq(FRAGMENT_ID), any()))
                .thenReturn(fragment);
        when(dataMapper.map(storeLocationData, Customer360DataWsDTO.class))
                .thenReturn(c360StoreLocation);

        final Customer360ListWsDTO response =  assistedServiceCustomer360Controller
                .searchCustomer360(BASE_SITE, USER_ID, customer360QueryListWsDTO);

        verify(additionalInformationFrameworkFacade).getFragment(eq(SECTION_ID), eq(FRAGMENT_ID), any());
        verify(dataMapper).map(storeLocationData, Customer360DataWsDTO.class);
        assertThat(response).isNotNull();
        assertThat(response.getValue()
                .get(0)).isSameAs(c360StoreLocation);
        verify(promotionSectionFragments, times(1)).contains(any());
    }

    @Test
    public void testSearchCustomerInterestByCustomer360() {
        final Customer360QueryWsDTO customer360QueryWsDTO = new Customer360QueryWsDTO();
        customer360QueryWsDTO.setType(QUERY_TYPE);
        final Customer360QueryListWsDTO customer360QueryListWsDTO = new Customer360QueryListWsDTO();
        customer360QueryListWsDTO
                .setCustomer360Queries(Arrays.asList(customer360QueryWsDTO));

        CustomerModel customer = new CustomerModel();
        when(userService.getCurrentUser()).thenReturn(customer);

        final Fragment fragment = new Fragment();
        final ProductData productData = new ProductData();
        productData.setCode(PRODUCT_CODE);
        final ProductInterestEntryData productInterestEntryData = new ProductInterestEntryData();
        final ProductInterestRelationData productInterestRelationData = new ProductInterestRelationData();
        productInterestRelationData.setProduct(productData);
        productInterestRelationData.setProductInterestEntry(Arrays.asList(productInterestEntryData));
        fragment.setData(Arrays.asList(productInterestEntryData));

        final C360ProductWsDTO c360ProductWsDTO = new C360ProductWsDTO();
        c360ProductWsDTO.setCode(PRODUCT_CODE);
        final C360CustomerProductInterestWsDTO c360CustomerProductInterestWsDTO= new C360CustomerProductInterestWsDTO();
        c360CustomerProductInterestWsDTO.setProduct(c360ProductWsDTO);
        final C360CustomerProductInterestList c360CustomerProductInterestList = new C360CustomerProductInterestList();
        c360CustomerProductInterestList.setCustomerProductInterests(Arrays.asList(c360CustomerProductInterestWsDTO));
        c360CustomerProductInterestList.setType(CUSTOMER_PRODUCT_INTEREST_TYPE);

        when(additionalInformationFrameworkFacade.getFragment(eq(SECTION_ID), eq(FRAGMENT_ID), any()))
                .thenReturn(fragment);
        when(c360FragmentDataAdapter.adapt(fragment.getData())).thenReturn(c360CustomerProductInterestList);

        final Customer360ListWsDTO response =  assistedServiceCustomer360Controller
                .searchCustomer360(BASE_SITE, USER_ID, customer360QueryListWsDTO);

        verify(additionalInformationFrameworkFacade).getFragment(eq(SECTION_ID), eq(FRAGMENT_ID), any());
        verify(c360FragmentDataAdapter).adapt(fragment.getData());
        assertThat(response).isNotNull();
        assertThat(response.getValue()
                .get(0)).isSameAs(c360CustomerProductInterestList);
        verify(promotionSectionFragments, times(1)).contains(any());
    }

    @Test
    public void testSearchCustomer360WhenValidationFailed() {
        final Customer360QueryWsDTO customer360QueryWsDTO = new Customer360QueryWsDTO();
        customer360QueryWsDTO.setType("invalidType");
        final Customer360QueryListWsDTO customer360QueryListWsDTO = new Customer360QueryListWsDTO();
        customer360QueryListWsDTO
                .setCustomer360Queries(Arrays.asList(customer360QueryWsDTO));

        doAnswer(invocationOnMock -> {
            final Errors errors = invocationOnMock.getArgument(1);
            errors.rejectValue("customer360Queries[0].type", "field.invalid");
            return null;
        }).when(customer360QueryListValidator)
                .validate(eq(customer360QueryListWsDTO), any());

        assertThatThrownBy(() -> assistedServiceCustomer360Controller
                .searchCustomer360(BASE_SITE, USER_ID, customer360QueryListWsDTO)
        ).isInstanceOf(WebserviceValidationException.class);
    }


    @Test
    public void testSearchCustomer360WhenUserIdNotCustomer() {
        final Customer360QueryWsDTO customer360QueryWsDTO = new Customer360QueryWsDTO();
        customer360QueryWsDTO.setType("QUERY_TYPE");
        final Customer360QueryListWsDTO customer360QueryListWsDTO = new Customer360QueryListWsDTO();
        customer360QueryListWsDTO
                .setCustomer360Queries(Arrays.asList(customer360QueryWsDTO));

        assertThatThrownBy(() -> assistedServiceCustomer360Controller
                .searchCustomer360(BASE_SITE, USER_ID, customer360QueryListWsDTO)
        ).isInstanceOf(RequestParameterException.class);
    }
}
