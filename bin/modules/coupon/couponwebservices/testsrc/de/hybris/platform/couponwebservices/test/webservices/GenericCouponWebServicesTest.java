/*
 * [y] hybris Platform
 *
 * Copyright (c) 2021 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.couponwebservices.test.webservices;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.couponservices.model.AbstractCouponModel;
import de.hybris.platform.couponservices.model.MultiCodeCouponModel;
import de.hybris.platform.couponservices.services.CouponCodeGenerationService;
import de.hybris.platform.couponwebservices.constants.CouponwebservicesConstants;
import de.hybris.platform.couponwebservices.dto.CouponValidationResponseWsDTO;
import de.hybris.platform.couponwebservices.util.CouponWsUtils;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert;
import de.hybris.platform.webservicescommons.testsupport.client.WsRequestBuilder;
import de.hybris.platform.webservicescommons.testsupport.client.WsSecuredRequestBuilder;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import java.util.Optional;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * <p>the tests are for occ api of
 * "/couponservices/v2/coupon/validate/{couponCode}"
 */
@NeedsEmbeddedServer(webExtensions = { CouponwebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class GenericCouponWebServicesTest extends ServicelayerTest
{
	public static final String OAUTH_CLIENT_ID = "coupon_user";
	public static final String OAUTH_CLIENT_PASS = "secret";

	private static final String BASE_URI = "couponservices/v2";
	private static final String URI = BASE_URI + "/coupon/validate";

	@Resource
	protected CouponWsUtils couponWsUtils;
	@Resource
	protected CouponCodeGenerationService couponCodeGenerationService;

	private WsRequestBuilder wsRequestBuilder;
	private WsSecuredRequestBuilder wsSecuredRequestBuilder;

	@Before
	public void setUp() throws Exception
	{
		wsRequestBuilder = new WsRequestBuilder()//
				.extensionName(CouponwebservicesConstants.EXTENSIONNAME);

		wsSecuredRequestBuilder = new WsSecuredRequestBuilder()//
				.extensionName(CouponwebservicesConstants.EXTENSIONNAME)//
				.client(OAUTH_CLIENT_ID, OAUTH_CLIENT_PASS)//
				.grantClientCredentials();

		createCoreData();
		createDefaultUsers();
		importCsv("/couponwebservices/test/ws-user.impex", "utf-8");
		importCsv("/couponwebservices/test/coupon-test-data.impex", "utf-8");
		importCsv("/couponwebservices/test/coupon-redemption-test-data.impex", "utf-8");
		importCsv("/couponwebservices/test/coupon-promotion-test-data.impex", "utf-8");
	}

	@Test
	public void testValidateCouponWithoutAuthorization()
	{
		final Response result = wsRequestBuilder//
				.path(URI).path("TEST_COUPON1").build().accept(MediaType.APPLICATION_JSON).get();
		result.bufferEntity();
		WebservicesAssert.assertResponse(Status.UNAUTHORIZED, Optional.empty(), result);
	}

	@Test
	public void testValidateCouponUsingClientCredentials()
	{
		final Response result = wsSecuredRequestBuilder//
				.path(URI).path("TEST_COUPON1").queryParam("customerId", "ppetersonson")//
				.build().accept(MediaType.APPLICATION_JSON).get();
		result.bufferEntity();
		WebservicesAssert.assertResponse(Status.OK, Optional.empty(), result);
	}

	@Test
	public void testValidateCouponNotExistCouponCodeError()
	{
		final Response response = wsSecuredRequestBuilder//
				.path(URI).path("INVALID_TEST_COUPON1").build().accept(MediaType.APPLICATION_JSON).get();
		response.bufferEntity();
		WebservicesAssert.assertResponse(Status.NOT_FOUND, Optional.empty(), response);
	}

	@Test
	public void testValidateCouponInvalidCustomerIdError()
	{
		final Response response = wsSecuredRequestBuilder//
				.path(URI)//
				.path("TEST_COUPON1").queryParam("customerId", "invalid_user")//
				.build()//
				.accept(MediaType.APPLICATION_JSON)//
				.get();
		response.bufferEntity();
		WebservicesAssert.assertResponse(Status.BAD_REQUEST, Optional.empty(), response);
	}


	@Test
	public void testValidateSingleCodeCouponUsingClientCredentials()
	{
		final Response result = wsSecuredRequestBuilder//
				.path(URI).path("TEST_COUPON1").build().accept(MediaType.APPLICATION_JSON).get();
		result.bufferEntity();
		WebservicesAssert.assertResponse(Status.OK, Optional.empty(), result);
		final CouponValidationResponseWsDTO validateCouponResponseWsDTO = result.readEntity(CouponValidationResponseWsDTO.class);
		assertNotNull(validateCouponResponseWsDTO);
		assertTrue(validateCouponResponseWsDTO.getValid());
	}

	@Test
	public void testValidateSingleCodeCouponWhenCouponIsInvalid()
	{
		final Response result = wsSecuredRequestBuilder//
				.path(URI).path("TEST_COUPON4").build().accept(MediaType.APPLICATION_JSON).get();
		result.bufferEntity();
		WebservicesAssert.assertResponse(Status.OK, Optional.empty(), result);
		final CouponValidationResponseWsDTO validateCouponResponseWsDTO = result.readEntity(CouponValidationResponseWsDTO.class);
		assertNotNull(validateCouponResponseWsDTO);
		assertFalse(validateCouponResponseWsDTO.getValid());
	}

	@Test
	public void testValidateSingleCodeCouponWithCustomerId()
	{
		final Response response = wsSecuredRequestBuilder//
				.path(URI)//
				.path("TEST_COUPON1").queryParam("customerId", "ppetersonson")//
				.build()//
				.accept(MediaType.APPLICATION_JSON)//
				.get();
		response.bufferEntity();
		WebservicesAssert.assertResponse(Status.OK, Optional.empty(), response);
		final CouponValidationResponseWsDTO validateCouponResponseWsDTO = response.readEntity(CouponValidationResponseWsDTO.class);
		assertNotNull(validateCouponResponseWsDTO);
		assertTrue(validateCouponResponseWsDTO.getValid());
	}

	@Test
	public void testValidateSingleCodeCouponWithCustomerIdWhenCouponIsNotValid()
	{
		final Response response = wsSecuredRequestBuilder//
				.path(URI)//
				.path("TEST_COUPON2").queryParam("customerId", "demo")//
				.build()//
				.accept(MediaType.APPLICATION_JSON)//
				.get();
		response.bufferEntity();
		WebservicesAssert.assertResponse(Status.OK, Optional.empty(), response);
		final CouponValidationResponseWsDTO validateCouponResponseWsDTO = response.readEntity(CouponValidationResponseWsDTO.class);
		assertNotNull(validateCouponResponseWsDTO);
		assertFalse(validateCouponResponseWsDTO.getValid());
	}

	@Test
	public void testValidateMultiCodeCouponWhenCouponIsNotActive()
	{
		final String couponCode = generateMultiCodeCoupon("COUPON1");
		final Response response = wsSecuredRequestBuilder//
				.path(URI)//
				.path(couponCode).build()//
				.accept(MediaType.APPLICATION_JSON)//
				.get();
		response.bufferEntity();

		WebservicesAssert.assertResponse(Status.OK, Optional.empty(), response);
		final CouponValidationResponseWsDTO validateCouponResponseWsDTO = response.readEntity(CouponValidationResponseWsDTO.class);
		assertNotNull(validateCouponResponseWsDTO);
		assertFalse(validateCouponResponseWsDTO.getValid());
	}

	@Test
	public void testValidateMultiCodeCouponWhenCouponIsActive()
	{
		final String couponCode = generateMultiCodeCoupon("COUPON3");
		final Response response = wsSecuredRequestBuilder//
				.path(URI)//
				.path(couponCode).build()//
				.accept(MediaType.APPLICATION_JSON)//
				.get();
		response.bufferEntity();

		WebservicesAssert.assertResponse(Status.OK, Optional.empty(), response);
		final CouponValidationResponseWsDTO validateCouponResponseWsDTO = response.readEntity(CouponValidationResponseWsDTO.class);
		assertNotNull(validateCouponResponseWsDTO);
		assertTrue(validateCouponResponseWsDTO.getValid());
	}

	@Test
	public void testValidateMultiCodeCouponWhenCouponIsNotValid()
	{
		final Response response = wsSecuredRequestBuilder//
				.path(URI)//
				.path("invalid-couponCode-1234").build()//
				.accept(MediaType.APPLICATION_JSON)//
				.get();
		response.bufferEntity();
		WebservicesAssert.assertResponse(Status.NOT_FOUND, Optional.empty(), response);
	}

	protected String generateMultiCodeCoupon(final String couponId)
	{
		final AbstractCouponModel couponModel = getCouponWsUtils().getCouponById(couponId);
		getCouponWsUtils().assertValidMultiCodeCoupon(couponModel, couponId);
		return getCouponCodeGenerationService().generateCouponCode((MultiCodeCouponModel) couponModel);
	}

	protected CouponWsUtils getCouponWsUtils()
	{
		return couponWsUtils;
	}

	protected CouponCodeGenerationService getCouponCodeGenerationService()
	{
		return couponCodeGenerationService;
	}
}
