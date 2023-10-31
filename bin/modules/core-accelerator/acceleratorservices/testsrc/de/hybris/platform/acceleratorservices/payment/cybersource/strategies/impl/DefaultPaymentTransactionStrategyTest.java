/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.acceleratorservices.payment.cybersource.strategies.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.payment.data.OrderInfoData;
import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.payment.PaymentService;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPaymentTransactionStrategyTest
{
	@Mock
	FlexibleSearchService searchService;
	@Mock
	private OrderModel mockOrder;
	@Mock
	private CustomerModel mockCustomer;
	@Mock
	private OrderInfoData mockOrderInfo;
	@Mock
	private PaymentService paymentService;
	@Mock
	private ModelService modelService;
	@Mock
	private CommerceCheckoutService commerceCheckoutService;
	@Mock
	private PaymentTransactionModel mockTransaction;
	@Mock
	private PaymentTransactionEntryModel mockPaymentTransactionEntry;

	@InjectMocks
	private DefaultPaymentTransactionStrategy defaultPaymentTransactionStrategy;

	@Before
	public void setUp() throws Exception
	{
		when(modelService.create(PaymentTransactionModel.class)).thenReturn(mockTransaction);
		when(modelService.create(PaymentTransactionEntryModel.class)).thenReturn(mockPaymentTransactionEntry);

		when(searchService.getModelByExample(any())).thenReturn(mockOrder);
		when(paymentService.getNewPaymentTransactionEntryCode(mockTransaction, PaymentTransactionType.CREATE_SUBSCRIPTION))
				.thenReturn("entryCode123");
		when(mockCustomer.getUid()).thenReturn("mock.customer@id.com");

		when(mockOrderInfo.getOrderPageRequestToken()).thenReturn("token-1234");
	}

	@Test
	public void savePaymentTransactionEntry()
	{
		final String mockPaymentProvider = "MOCK_PAYMENT_PROVIDER";
		when(commerceCheckoutService.getPaymentProvider()).thenReturn(mockPaymentProvider);

		final String requestId = "random_id-1234567890";
		final PaymentTransactionEntryModel entry = defaultPaymentTransactionStrategy.savePaymentTransactionEntry(mockCustomer,
				requestId, mockOrderInfo);
		verify(mockTransaction).setCode(anyString());
		verify(mockTransaction).setRequestId(requestId);
		verify(mockTransaction).setRequestToken("token-1234");
		verify(mockTransaction).setPaymentProvider(mockPaymentProvider);
		verify(modelService).save(mockTransaction);

		verify(mockPaymentTransactionEntry).setType(PaymentTransactionType.CREATE_SUBSCRIPTION);
		verify(mockPaymentTransactionEntry).setRequestId(requestId);
		verify(mockPaymentTransactionEntry).setRequestToken("token-1234");
		verify(mockPaymentTransactionEntry).setTime(any(Date.class));
		verify(mockPaymentTransactionEntry).setPaymentTransaction(mockTransaction);
		verify(mockPaymentTransactionEntry).setTransactionStatusDetails("SUCCESFULL");
		verify(mockPaymentTransactionEntry).setCode("entryCode123");
		verify(modelService).save(mockPaymentTransactionEntry);

		assertThat(entry).isEqualToComparingFieldByField(mockPaymentTransactionEntry);
	}

	@Test
	public void testNullParametersSetPaymentTransactionReviewResult()
	{
		defaultPaymentTransactionStrategy.setPaymentTransactionReviewResult(null, null);
		defaultPaymentTransactionStrategy.setPaymentTransactionReviewResult(mockPaymentTransactionEntry, null);
		defaultPaymentTransactionStrategy.setPaymentTransactionReviewResult(null, "599b141c2dc0a5d2ecf6735d2c9fb7a7416d83fa");

		verifyNoInteractions(searchService);
	}

	@Test
	public void testEmptyPaymentTransactionsSetPaymentTransactionReviewResult()
	{
		when(mockOrder.getPaymentTransactions()).thenReturn(List.of());
		defaultPaymentTransactionStrategy.setPaymentTransactionReviewResult(mockPaymentTransactionEntry, "599b141c2dc0a5d2ecf6735d2c9fb7a7416d83fa-1");
		verify(searchService).getModelByExample(any(OrderModel.class));
		verify(mockOrder, atMostOnce()).getPaymentTransactions();
	}

	@Test
	public void testPaymentTransactionsExistSetPaymentTransactionReviewResult()
	{
		when(mockTransaction.getEntries()).thenReturn(List.of());
		when(mockOrder.getPaymentTransactions()).thenReturn(List.of(mockTransaction));
		defaultPaymentTransactionStrategy.setPaymentTransactionReviewResult(mockPaymentTransactionEntry, "599b141c2dc0a5d2ecf6735d2c9fb7a7416d83fa-1");
		verify(searchService).getModelByExample(any(OrderModel.class));
		verify(mockOrder, times(2)).getPaymentTransactions();
	}
}
