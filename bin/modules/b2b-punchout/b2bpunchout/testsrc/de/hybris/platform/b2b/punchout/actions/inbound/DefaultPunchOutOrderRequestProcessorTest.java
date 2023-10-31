/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.actions.inbound;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.punchout.PunchOutException;
import de.hybris.platform.b2b.punchout.PunchOutResponseMessage;
import de.hybris.platform.b2b.punchout.PunchOutUtils;
import de.hybris.platform.b2b.punchout.services.PunchOutSessionService;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commerceservices.order.CommerceCartRestorationException;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.b2b.punchout.services.PunchOutConfigurationService;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.user.UserService;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.cxml.CXML;
import org.cxml.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPunchOutOrderRequestProcessorTest
{
	@InjectMocks
	private DefaultPunchOutOrderRequestProcessor punchOutOrderRequestProcessor;

	private CXML requestXML;
	private CXML requestXMLWithCartId;
	private CXML requestXMLWithDiffCartIds;
	private CartModel cartModel;
	private CXML punchOutOrderRequestXml;
	private CXML punchOutOrderRequestXmlWithoutItemOut;

	@Mock
	private CartService cartService;
	@Mock
	private CommerceCartService commerceCartService;
	@Mock
	private UserService userService;
	@Mock
	private PrepareCartPurchaseOrderProcessing prepareCartPurchaseOrderProcessing;
	@Mock
	private PopulateCartPurchaseOrderProcessing populateCartPurchaseOrderProcessing;
	@Mock
	private PlacePurchaseOrderProcessing placePurchaseOrderProcessing;
	@Mock
	private PunchOutSessionService punchOutSessionService;
	@Mock
	private PunchOutConfigurationService punchOutConfigurationService;


	@Before
	public void setUp() throws FileNotFoundException
	{
		requestXML = PunchOutUtils.unmarshallCXMLFromFile("b2bpunchout/test/sampleOrderRequest.xml");
		requestXMLWithCartId = PunchOutUtils.unmarshallCXMLFromFile("b2bpunchout/test/sampleOrderRequestWithCartId.xml");
		requestXMLWithDiffCartIds = PunchOutUtils.unmarshallCXMLFromFile("b2bpunchout/test/sampleOrderRequestWithDiffCartIds.xml");
		punchOutOrderRequestXml = PunchOutUtils.unmarshallCXMLFromFile("b2bpunchout/test/punchOutOrderRequest.xml");
		punchOutOrderRequestXmlWithoutItemOut = PunchOutUtils.unmarshallCXMLFromFile("b2bpunchout/test/punchOutOrderRequestWithoutItemOut.xml");

		cartModel = new CartModel();
		when(cartService.getSessionCart()).thenReturn(cartModel);
	}

	@Test
	public void testGenerateCxmlParamException()
	{
		assertThatThrownBy(() -> punchOutOrderRequestProcessor.generatecXML(requestXMLWithDiffCartIds))
				.isInstanceOf(PunchOutException.class);
	}


	@Test
	public void testGenerateCxmlRestoreCartException() throws CommerceCartRestorationException
	{
		CartModel cartMock = new CartModel();
		cartMock.setCode("00004018");
		when(userService.getCurrentUser()).thenReturn(new CustomerModel());
		when(commerceCartService.getCartForCodeAndUser(Mockito.eq("00004018"), Mockito.any(CustomerModel.class))).thenReturn(cartMock);
		when(commerceCartService.restoreCart(Mockito.any(CommerceCartParameter.class))).thenThrow(new CommerceCartRestorationException("restore cart fail"));

		assertThatThrownBy(() -> punchOutOrderRequestProcessor.generatecXML(requestXMLWithCartId))
				.isInstanceOf(PunchOutException.class);
	}

	@Test
	public void testGenerateCxmlResponse()
	{
		final OrderData orderData = new OrderData();
		orderData.setCode("123");
		when(placePurchaseOrderProcessing.process()).thenReturn(orderData);
		final CXML responseXML = punchOutOrderRequestProcessor.generatecXML(requestXML);
		assertThat(responseXML).isNotNull();

		verify(cartService).removeSessionCart();
		verify(cartService).getSessionCart();

		verify(populateCartPurchaseOrderProcessing).process(requestXML, cartModel);
		verify(prepareCartPurchaseOrderProcessing).process();
		verify(placePurchaseOrderProcessing).process();

		final Response response = (Response) responseXML.getHeaderOrMessageOrRequestOrResponse().get(0);
		assertThat(response).isNotNull()
							.hasFieldOrPropertyWithValue("status.code", Integer.toString(HttpStatus.OK.value()))
							.hasFieldOrPropertyWithValue("status.text", PunchOutResponseMessage.OK)
				         .hasFieldOrPropertyWithValue("status.value", "123");
	}

	@Test
	public void testGenerateCxmlResponseWithCartId() throws CommerceCartRestorationException
	{
		CartModel cartMock = new CartModel();
		cartMock.setCode("00004018");
		when(userService.getCurrentUser()).thenReturn(new CustomerModel());
		when(commerceCartService.getCartForCodeAndUser(Mockito.eq("00004018"), Mockito.any(CustomerModel.class))).thenReturn(cartMock);
		final OrderData orderData = new OrderData();
		orderData.setCode("456");
		when(placePurchaseOrderProcessing.process()).thenReturn(orderData);
		final CXML responseXML = punchOutOrderRequestProcessor.generatecXML(requestXMLWithCartId);
		assertThat(responseXML).isNotNull();

		verify(commerceCartService).restoreCart(Mockito.any(CommerceCartParameter.class));
		verify(prepareCartPurchaseOrderProcessing).process();
		verify(placePurchaseOrderProcessing).process();

		final Response response = (Response) responseXML.getHeaderOrMessageOrRequestOrResponse().get(0);
		assertThat(response).isNotNull()
				.hasFieldOrPropertyWithValue("status.code", Integer.toString(HttpStatus.OK.value()))
				.hasFieldOrPropertyWithValue("status.text", PunchOutResponseMessage.OK)
				.hasFieldOrPropertyWithValue("status.value", "456");
	}


	@Test
	public void testGenerateCxmlResponseWithAllValidatedOK()
	{
		cartModel.setTotalPrice(96.00);
		List<AbstractOrderEntryModel> entries = new ArrayList<>();
		AbstractOrderEntryModel abstractOrderEntryModel = new AbstractOrderEntryModel();
		abstractOrderEntryModel.setBasePrice(48.00);
		abstractOrderEntryModel.setQuantity(2L);
		ProductModel productModel = new ProductModel();
		productModel.setCode("3755219");
		abstractOrderEntryModel.setProduct(productModel);
		entries.add(abstractOrderEntryModel);
		cartModel.setEntries(entries);

		when(punchOutConfigurationService.isOrderVerificationEnabled()).thenReturn(Boolean.TRUE);

		final boolean validateResult = punchOutOrderRequestProcessor.validateCart(punchOutOrderRequestXml, cartModel);
		assertThat(validateResult).isTrue();
	}

	@Test
	public void testGenerateCxmlResponseWithProductValidatedNotOK()
	{
		cartModel.setTotalPrice(96.00);
		List<AbstractOrderEntryModel> entries = new ArrayList<>();
		AbstractOrderEntryModel abstractOrderEntryModel = new AbstractOrderEntryModel();
		abstractOrderEntryModel.setBasePrice(48.00);
		abstractOrderEntryModel.setQuantity(2L);
		ProductModel productModel = new ProductModel();
		productModel.setCode("0000000");
		abstractOrderEntryModel.setProduct(productModel);
		entries.add(abstractOrderEntryModel);
		cartModel.setEntries(entries);

		when(punchOutConfigurationService.isOrderVerificationEnabled()).thenReturn(Boolean.TRUE);

		final boolean validateResult = punchOutOrderRequestProcessor.validateCart(punchOutOrderRequestXml, cartModel);
		assertThat(validateResult).isFalse();
	}

	@Test
	public void testGenerateCxmlResponseWithBasePriceValidatedNotOK()
	{
		cartModel.setTotalPrice(96.00);
		List<AbstractOrderEntryModel> entries = new ArrayList<>();
		AbstractOrderEntryModel abstractOrderEntryModel = new AbstractOrderEntryModel();
		abstractOrderEntryModel.setBasePrice(0.1);
		abstractOrderEntryModel.setQuantity(2L);
		ProductModel productModel = new ProductModel();
		productModel.setCode("3755219");
		abstractOrderEntryModel.setProduct(productModel);
		entries.add(abstractOrderEntryModel);
		cartModel.setEntries(entries);

		when(punchOutConfigurationService.isOrderVerificationEnabled()).thenReturn(Boolean.TRUE);

		final boolean validateResult = punchOutOrderRequestProcessor.validateCart(punchOutOrderRequestXml, cartModel);
		assertThat(validateResult).isFalse();
	}

	@Test
	public void testGenerateCxmlResponseWithQuantityValidatedNotOK()
	{
		cartModel.setTotalPrice(96.00);
		List<AbstractOrderEntryModel> entries = new ArrayList<>();
		AbstractOrderEntryModel abstractOrderEntryModel = new AbstractOrderEntryModel();
		abstractOrderEntryModel.setBasePrice(48.00);
		abstractOrderEntryModel.setQuantity(1L);
		ProductModel productModel = new ProductModel();
		productModel.setCode("3755219");
		abstractOrderEntryModel.setProduct(productModel);
		entries.add(abstractOrderEntryModel);
		cartModel.setEntries(entries);

		when(punchOutConfigurationService.isOrderVerificationEnabled()).thenReturn(Boolean.TRUE);

		final boolean validateResult = punchOutOrderRequestProcessor.validateCart(punchOutOrderRequestXml, cartModel);
		assertThat(validateResult).isFalse();
	}

	@Test
	public void testGenerateCxmlResponseWithVerificationNotEnabled()
	{
		when(punchOutConfigurationService.isOrderVerificationEnabled()).thenReturn(Boolean.FALSE);

		final boolean validateResult = punchOutOrderRequestProcessor.validateCart(punchOutOrderRequestXml, cartModel);
		assertThat(validateResult).isFalse();
	}

	@Test
	public void testGenerateCxmlResponseWithEmptyCart()
	{
		when(punchOutConfigurationService.isOrderVerificationEnabled()).thenReturn(Boolean.TRUE);

		final boolean validateResult = punchOutOrderRequestProcessor.validateCart(punchOutOrderRequestXml, cartModel);
		assertThat(validateResult).isFalse();
	}

	@Test
	public void testGenerateCxmlResponseWithNotItemOut()
	{
		when(punchOutConfigurationService.isOrderVerificationEnabled()).thenReturn(Boolean.TRUE);

		assertThatThrownBy(() -> punchOutOrderRequestProcessor.validateCart(punchOutOrderRequestXmlWithoutItemOut, cartModel))
				.isInstanceOf(PunchOutException.class);
	}
}
