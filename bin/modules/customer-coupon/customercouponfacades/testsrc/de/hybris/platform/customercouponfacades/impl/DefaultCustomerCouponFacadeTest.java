/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.customercouponfacades.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commercefacades.voucher.VoucherFacade;
import de.hybris.platform.commercefacades.voucher.exceptions.VoucherOperationException;
import de.hybris.platform.commerceservices.search.ProductSearchService;
import de.hybris.platform.commerceservices.search.facetdata.ProductCategorySearchPageData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.PaginationData;
import de.hybris.platform.commerceservices.search.pagedata.SortData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.customercouponfacades.customercoupon.data.CustomerCouponData;
import de.hybris.platform.customercouponfacades.customercoupon.data.CustomerCouponNotificationData;
import de.hybris.platform.customercouponfacades.customercoupon.data.CustomerCouponSearchPageData;
import de.hybris.platform.customercouponfacades.emums.AssignCouponResult;
import de.hybris.platform.customercouponfacades.strategies.CustomerCouponRemovableStrategy;
import de.hybris.platform.customercouponservices.CustomerCouponService;
import de.hybris.platform.customercouponservices.model.CouponNotificationModel;
import de.hybris.platform.customercouponservices.model.CustomerCouponModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

/**
 * Unit test for {@link DefaultCustomerCouponFacade}
 */
@UnitTest
public class DefaultCustomerCouponFacadeTest
{
	private static final String COUPON_ID = "testid";
	private static final String SEARCH_TEXT = "search-text";
	private static final String ASM_ERROR_MESSAGE ="Coupon is not assignable in ASM";
	@Spy
	private final DefaultCustomerCouponFacade facade = new DefaultCustomerCouponFacade();

	private Collection<CustomerModel> customers;

	@Mock
	private UserService userService;
	@Mock
	private CustomerCouponService customerCouponService;
	@Mock
	private Converter<CustomerCouponModel, CustomerCouponData> customerCouponConverter;
	@Mock
	private Converter<CouponNotificationModel, CustomerCouponNotificationData> customerCouponNotificationConverter;
	@Mock
	private Converter<SearchPageData<CustomerCouponModel>, CustomerCouponSearchPageData> customerCouponSearchPageDataConverter;
	@Mock
	private CustomerModel customer;
	@Mock
	private PageableData pageableData;
	@Mock
	private de.hybris.platform.commerceservices.search.pagedata.SearchPageData<CustomerCouponModel> pagedCouponModels;
	@Mock
	private SearchPageData searchPageData;
	@Mock
	private SearchPageData<CustomerCouponModel> couponSearchPageData;
	@Mock
	private CustomerCouponModel couponModel;
	@Mock
	private List<CustomerCouponModel> couponModels;
	@Mock
	private List<CustomerCouponData> coupons;
	@Mock
	private CustomerCouponData coupon;
	@Mock
	private PaginationData pagination;
	@Mock
	private List<SortData> sorts;
	@Mock
	private Converter<ProductCategorySearchPageData<SolrSearchQueryData, SearchResultValueData, CategoryModel>, ProductCategorySearchPageData<SearchStateData, ProductData, CategoryData>> productCategorySearchPageConverter;
	@Mock
	private ProductSearchService<SolrSearchQueryData, SearchResultValueData, ProductCategorySearchPageData<SolrSearchQueryData, SearchResultValueData, CategoryModel>> productSearchService;
	@Mock
	private CustomerCouponSearchPageData customerCouponSearchPageData;
	@Mock
	private CustomerCouponRemovableStrategy customerCouponRemovableStrategy;
	@Mock
	private CartData cart;
	@Mock
	private CartFacade cartFacade;
	@Mock
	private VoucherFacade voucherFacade;
	@Mock
	private CouponNotificationModel notificationModel;
	@Mock
	private CustomerCouponNotificationData notificationData;


	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);
		facade.setUserService(userService);
		facade.setCustomerCouponService(customerCouponService);
		facade.setCustomerCouponConverter(customerCouponConverter);
		facade.setCustomerCouponSearchPageDataConverter(customerCouponSearchPageDataConverter);
		facade.setCustomerCouponRemovableStrategy(customerCouponRemovableStrategy);
		facade.setCartFacade(cartFacade);
		facade.setVoucherFacade(voucherFacade);
		facade.setCustomerCouponNotificationConverter(customerCouponNotificationConverter);


		customers = new ArrayList<>(0);
		customers.add(customer);

		Mockito.when(customerCouponService.isAsm()).thenReturn(false);
		Mockito.lenient().when(userService.getCurrentUser()).thenReturn(customer);
		Mockito.lenient().when(customerCouponService.getCustomerCouponsForCustomer(customer, pageableData)).thenReturn(pagedCouponModels);
		Mockito.lenient().when(customerCouponService.getEffectiveCustomerCouponsForCustomer(customer)).thenReturn(couponModels);
		Mockito.lenient().when(pagedCouponModels.getResults()).thenReturn(couponModels);
		Mockito.doReturn(coupons).when(facade).convertCustomerCoupons(couponModels);
		Mockito.lenient().when(pagedCouponModels.getPagination()).thenReturn(pagination);
		Mockito.lenient().when(pagedCouponModels.getSorts()).thenReturn(sorts);
		Mockito.lenient().when(couponModel.getCustomers()).thenReturn(customers);
		Mockito.lenient().when(customerCouponService.getPaginatedCouponsForCustomer(customer, searchPageData)).thenReturn(
				couponSearchPageData);

	}

	@Test
	public void testGetPagedCouponsData()
	{
		final de.hybris.platform.commerceservices.search.pagedata.SearchPageData<CustomerCouponData> result = facade
				.getPagedCouponsData(pageableData);

		Assert.assertEquals(coupons, result.getResults());
		Assert.assertEquals(pagination, result.getPagination());
		Assert.assertEquals(sorts, result.getSorts());
	}

	@Test
	public void testGetPagedCouponsData_PagedCouponModelNull()
	{
		Mockito.lenient().when(customerCouponService.getCustomerCouponsForCustomer(customer, pageableData)).thenReturn(null);
		final de.hybris.platform.commerceservices.search.pagedata.SearchPageData<CustomerCouponData> result = facade
				.getPagedCouponsData(pageableData);
		Assert.assertEquals(0, result.getResults().size());
		Assert.assertNull(result.getSorts());
		Assert.assertNull(result.getPagination());
	}

	@Test
	public void testGetCouponsData()
	{
		final List<CustomerCouponData> result = facade.getCouponsData();

		Assert.assertEquals(coupons, result);
	}

	@Test
	public void testGetCouponsData_ResultEmpty()
	{
		Mockito.lenient().when(customerCouponService.getEffectiveCustomerCouponsForCustomer(customer)).thenReturn(Collections.emptyList());
		final List<CustomerCouponData> result = facade.getCouponsData();

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testGrantCouponAccessForCurrentUserSuccess()
	{
		couponModel = new CustomerCouponModel();
		couponModel.setCouponId(COUPON_ID);
		final Optional<CustomerCouponModel> optional = Optional.of(couponModel);
		Mockito.when(customerCouponService.getValidCustomerCouponByCode(COUPON_ID)).thenReturn(optional);
		final AssignCouponResult result = facade.grantCouponAccessForCurrentUser(COUPON_ID);
		Assert.assertEquals(AssignCouponResult.SUCCESS, result);
	}

	@Test
	public void testGrantCouponAccessForCurrentUserInAsmAllowed()
	{
		couponModel = new CustomerCouponModel();
		couponModel.setCouponId(COUPON_ID);
		couponModel.setAssignable(true);
		final Optional<CustomerCouponModel> optional = Optional.of(couponModel);
		Mockito.when(customerCouponService.isAsm()).thenReturn(true);
		Mockito.when(customerCouponService.getValidCustomerCouponByCode(COUPON_ID)).thenReturn(optional);
		final AssignCouponResult result = facade.grantCouponAccessForCurrentUser(COUPON_ID);
		Assert.assertEquals(AssignCouponResult.SUCCESS, result);
	}

	@Test
	public void testGrantCouponAccessForCurrentUserInAsmNotAllowed()
	{
		couponModel = new CustomerCouponModel();
		couponModel.setCouponId(COUPON_ID);
		couponModel.setAssignable(false);
		final Optional<CustomerCouponModel> optional = Optional.of(couponModel);
		Mockito.when(customerCouponService.isAsm()).thenReturn(true);
		Mockito.when(customerCouponService.getValidCustomerCouponByCode(COUPON_ID)).thenReturn(optional);
		final AssignCouponResult result = facade.grantCouponAccessForCurrentUser(COUPON_ID);
		Assert.assertEquals(AssignCouponResult.NOTALLOWED, result);
	}

	@Test
	public void testGrantCouponAccessForCurrentUserExist()
	{
		couponModel = new CustomerCouponModel();
		couponModel.setCouponId(COUPON_ID);
		couponModel.setCustomers(Collections.singleton(customer));
		final Optional<CustomerCouponModel> optional = Optional.of(couponModel);
		Mockito.when(customerCouponService.getValidCustomerCouponByCode(COUPON_ID)).thenReturn(optional);
		final AssignCouponResult result = facade.grantCouponAccessForCurrentUser(COUPON_ID);
		Assert.assertEquals(AssignCouponResult.ASSIGNED, result);
	}

	@Test
	public void testGrantCouponAccessForCurrentUserError()
	{
		final Optional<CustomerCouponModel> optional = Optional.empty();
		Mockito.lenient().when(customerCouponService.getValidCustomerCouponByCode(anyString())).thenReturn(optional);
		final AssignCouponResult result = facade.grantCouponAccessForCurrentUser(COUPON_ID);
		Assert.assertEquals(AssignCouponResult.INEXISTENCE, result);
	}

	@Test
	public void testGetAssignableCustomerCoupons()
	{
		Mockito.lenient().when(customerCouponService.getAssignableCustomerCoupons(customer, SEARCH_TEXT)).thenReturn(couponModels);
		final List<CustomerCouponData> result = facade.getAssignableCustomerCoupons(SEARCH_TEXT);

		Assert.assertEquals(coupons, result);
	}

	@Test
	public void testGetAssignedCustomerCoupons()
	{
		Mockito.lenient().when(customerCouponService.getAssignedCustomerCouponsForCustomer(customer, SEARCH_TEXT)).thenReturn(couponModels);
		final List<CustomerCouponData> result = facade.getAssignedCustomerCoupons(SEARCH_TEXT);

		Assert.assertEquals(coupons, result);
	}

	@Test
	public void testGetCustomerCouponForCode()
	{
		Mockito.lenient().when(customerCouponService.getCustomerCouponForCode(COUPON_ID)).thenReturn(Optional.of(couponModel));
		Mockito.lenient().when(customerCouponConverter.convert(couponModel)).thenReturn(coupon);

		final CustomerCouponData result = facade.getCustomerCouponForCode(COUPON_ID);
		Assert.assertEquals(coupon, result);
	}

	@Test
	public void testIsCouponOwnedByCurrentUser()
	{
		Mockito.lenient().when(customerCouponService.getCustomerCouponForCode(COUPON_ID)).thenReturn(Optional.of(couponModel));

		final boolean result = facade.isCouponOwnedByCurrentUser(COUPON_ID);
		Assert.assertTrue(result);
	}

	@Test
	public void testIsCouponOwnedByCurrentUser_otherwise()
	{
		Mockito.lenient().when(customerCouponService.getCustomerCouponForCode(COUPON_ID)).thenReturn(Optional.of(couponModel));
		customers.remove(customer);

		final boolean result = facade.isCouponOwnedByCurrentUser(COUPON_ID);
		Assert.assertFalse(result);
	}

	@Test
	public void testGetPaginatedCoupons()
	{
		Mockito.lenient().when(customerCouponSearchPageDataConverter.convert(couponSearchPageData)).thenReturn(
				customerCouponSearchPageData);
		final CustomerCouponSearchPageData result = facade.getPaginatedCoupons(searchPageData);
		Assert.assertEquals(customerCouponSearchPageData, result);
	}

	@Test
	public void testGetValidCouponForCode()
	{
		final CustomerCouponModel model = new CustomerCouponModel();
		model.setCouponId(COUPON_ID);
		model.setCustomers(Collections.singleton(customer));
		final Optional<CustomerCouponModel> optional = Optional.of(model);
		Mockito.lenient().when(customerCouponService.getValidCustomerCouponByCode(anyString())).thenReturn(optional);
		Mockito.lenient().when(customerCouponConverter.convert(model)).thenReturn(coupon);
		final CustomerCouponData result = facade.getValidCouponForCode(COUPON_ID);
		Assert.assertEquals(coupon, result);

	}

	@Test
	public void testNotification(){

		Mockito.lenient().when(customerCouponService.saveCouponNotification(COUPON_ID)).thenReturn(Optional.of(notificationModel));
		Mockito.lenient().when(customerCouponNotificationConverter.convert(notificationModel)).thenReturn(notificationData);
		doNothing().when(customerCouponService).removeCouponNotificationByCode(COUPON_ID);

		final CustomerCouponNotificationData notification = facade.saveCouponNotification(COUPON_ID);
		Assert.assertEquals(notificationData, notification);

		facade.removeCouponNotificationByCode(COUPON_ID);
		verify(customerCouponService, times(1)).removeCouponNotificationByCode(COUPON_ID);

	}

	@Test
	public void testReleaseCoupon() throws VoucherOperationException
	{
		Mockito.lenient().when(customerCouponRemovableStrategy.checkRemovable(anyString())).thenReturn(true);
		doNothing().when(customerCouponService).removeCouponForCustomer(COUPON_ID, customer);
		doNothing().when(customerCouponService).removeCouponNotificationByCode(COUPON_ID);
		Mockito.lenient().when(cartFacade.getSessionCart()).thenReturn(cart);
		final List appliedVouchers = Collections.singletonList(cart);
		Mockito.lenient().when(cart.getAppliedVouchers()).thenReturn(appliedVouchers);
		doNothing().when(voucherFacade).releaseVoucher(COUPON_ID);
		
		facade.releaseCoupon(COUPON_ID);
		
		Mockito.lenient().when(customerCouponRemovableStrategy.checkRemovable(anyString())).thenReturn(true);
		facade.releaseCoupon(COUPON_ID);
	}
	@Test
	public void testReleaseCouponInAsmAllowed() throws VoucherOperationException
	{
		Mockito.lenient().when(customerCouponRemovableStrategy.checkRemovable(anyString())).thenReturn(true);

		couponModel = new CustomerCouponModel();
		couponModel.setCouponId(COUPON_ID);
		couponModel.setAssignable(true);
		final Optional<CustomerCouponModel> optional = Optional.of(couponModel);
		Mockito.when(customerCouponService.isAsm()).thenReturn(true);
		Mockito.when(customerCouponService.getValidCustomerCouponByCode(COUPON_ID)).thenReturn(optional);

		doNothing().when(customerCouponService).removeCouponForCustomer(COUPON_ID, customer);
		doNothing().when(customerCouponService).removeCouponNotificationByCode(COUPON_ID);
		Mockito.lenient().when(cartFacade.getSessionCart()).thenReturn(cart);
		final List<String> appliedVouchers = Collections.singletonList(COUPON_ID);
		Mockito.lenient().when(cart.getAppliedVouchers()).thenReturn(appliedVouchers);
		doNothing().when(voucherFacade).releaseVoucher(COUPON_ID);

		facade.releaseCoupon(COUPON_ID);
		verify(voucherFacade, times(1)).releaseVoucher(COUPON_ID);

	}

	@Test
	public void testReleaseCouponInAsmNotAllowed() throws VoucherOperationException
	{
		Mockito.lenient().when(customerCouponRemovableStrategy.checkRemovable(anyString())).thenReturn(true);

		couponModel = new CustomerCouponModel();
		couponModel.setCouponId(COUPON_ID);
		couponModel.setAssignable(false);
		final Optional<CustomerCouponModel> optional = Optional.of(couponModel);
		Mockito.when(customerCouponService.isAsm()).thenReturn(true);
		Mockito.when(customerCouponService.getValidCustomerCouponByCode(COUPON_ID)).thenReturn(optional);

		final Exception exception = Assert.assertThrows(VoucherOperationException.class,
				() -> facade.releaseCoupon(COUPON_ID));
		assertThat(exception.getMessage()).contains(ASM_ERROR_MESSAGE);
	}

}
