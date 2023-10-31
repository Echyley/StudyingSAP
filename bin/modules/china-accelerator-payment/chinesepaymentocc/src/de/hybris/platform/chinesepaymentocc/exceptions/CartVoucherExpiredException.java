/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.chinesepaymentocc.exceptions;

import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceException;

import java.util.ArrayList;
import java.util.List;


/**
 * Thrown when coupons applied on cart expires
 */
public class CartVoucherExpiredException extends WebserviceException
{
	private static final String TYPE = "cartVoucherError";
	private static final String SUBJECT_TYPE = "voucher";
	private static final String REASON_INVALID = "expired";
	private final List<String> invalidVouchers = new ArrayList();
	private static final String VOUCHER_EXPIRED_MESSAGE = "The applied coupon [%s] is expired.";


	public CartVoucherExpiredException(final String coupon)
	{

		super(generateVoucherExpiredMessage(coupon), REASON_INVALID, coupon);

	}


	@Override
	public String getSubjectType()
	{
		return SUBJECT_TYPE;
	}

	@Override
	public String getType()
	{
		return TYPE;
	}



	private static String generateVoucherExpiredMessage(final String voucherCode) {
		return String.format(VOUCHER_EXPIRED_MESSAGE, voucherCode);
	}

	/**
	 * @deprecated (deprecated since 2105, throw invalid voucher one ny one, no need to get all invalid vouthers)
	 */
	@Deprecated(since = "2105", forRemoval = true)
	public List<String> getInvalidVouchers()
	{
		return invalidVouchers;
	}

	/**
	 * @deprecated (deprecated since 2105, throw invalid voucher one ny one, no need to get all invalid vouthers)
	 */
	@Deprecated(since = "2105", forRemoval = true)
	public void addInvalidVoucher(final String voucheCode)
	{
		invalidVouchers.add(voucheCode);

	}
}
