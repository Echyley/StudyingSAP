/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.customercouponocc.controllers.customercoupon;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.voucher.exceptions.VoucherOperationException;
import de.hybris.platform.customercouponfacades.CustomerCouponFacade;
import de.hybris.platform.customercouponocc.errors.exceptions.CustomerCouponClaimException;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.validation.Validator;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;


@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class CustomerCouponsControllerTest
{

	private static final String DEFAULT_TEST_COUPON_CODE = "DefaultTestCouponCode";

	@Mock
	private CustomerCouponFacade customerCouponFacadeMock;

	private ValidatorMock customerCouponCodeValidatorMock;

	private CustomerCouponsController controller;

	@Before
	public void setUp()
	{
		customerCouponCodeValidatorMock = new ValidatorMock();

		controller = new CustomerCouponsController();

		controller.setCustomerCouponFacade(customerCouponFacadeMock);
		controller.setCustomerCouponCodeValidator(customerCouponCodeValidatorMock);
	}

	@Test(expected = WebserviceValidationException.class)
	public void testunassignCustomerCouponShouldValidCouponCode()
	{
		BeanPropertyBindingResult errors = new BeanPropertyBindingResult(new Object(), CustomerCouponsController.COUPON_CODE);
		errors.addError(new ObjectError(CustomerCouponsController.COUPON_CODE, "message"));

		customerCouponCodeValidatorMock.setErrors(errors);

		controller.unassignCustomerCoupon(null);
	}

	@Test(expected = CustomerCouponClaimException.class)
	public void testunassignCustomerCouponShouldHandleVoucherOperationException() throws VoucherOperationException
	{
		doThrow(new VoucherOperationException("Test Exception")).when(customerCouponFacadeMock)
				.releaseCoupon(DEFAULT_TEST_COUPON_CODE);

		controller.unassignCustomerCoupon(DEFAULT_TEST_COUPON_CODE);
	}

	@Test
	public void testunassignCustomerCouponShouldInvokeFacade() throws VoucherOperationException
	{
		controller.unassignCustomerCoupon(DEFAULT_TEST_COUPON_CODE);

		verify(customerCouponFacadeMock).releaseCoupon(DEFAULT_TEST_COUPON_CODE);
	}

	static class ValidatorMock implements Validator
	{
		private Errors errors;

		protected ValidatorMock()
		{
			errors = null;
		}

		protected ValidatorMock(final Errors errors)
		{
			this.errors = errors;
		}

		@Override
		public boolean supports(final Class<?> clazz)
		{
			return true;
		}

		@Override
		public void validate(final Object target, final Errors errors)
		{
			if (this.errors != null)
			{
				errors.addAllErrors(this.errors);
			}
		}

		protected Errors getErrors()
		{
			return errors;
		}

		public void setErrors(final Errors errors)
		{
			this.errors = errors;
		}
	}
}
