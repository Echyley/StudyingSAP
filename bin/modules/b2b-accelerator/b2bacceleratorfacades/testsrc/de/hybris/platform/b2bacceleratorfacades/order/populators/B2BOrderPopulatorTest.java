/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bacceleratorfacades.order.populators;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCommentModel;
import de.hybris.platform.b2b.model.B2BCostCenterModel;
import de.hybris.platform.b2b.model.B2BPermissionResultModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BCommentData;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BPaymentTypeData;
import de.hybris.platform.b2bacceleratorfacades.order.data.TriggerData;
import de.hybris.platform.b2b.enums.CheckoutPaymentType;
import de.hybris.platform.b2bapprovalprocessfacades.company.data.B2BPermissionResultData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BCostCenterData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.cronjob.model.TriggerModel;
import de.hybris.platform.orderscheduling.model.CartToOrderCronJobModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

@UnitTest
public class B2BOrderPopulatorTest
{
    private static final String PURCHASE_ORDER_NUMBER = "purchaseOrderNumber";
    private static final String JOB_CODE = "jobCode";
    private AutoCloseable openMocks;
    @Mock
    private Converter<B2BCostCenterModel, B2BCostCenterData> b2BCostCenterConverter;
    @Mock
    private Converter<CheckoutPaymentType, B2BPaymentTypeData> b2bPaymentTypeConverter;
    @Mock
    private Converter<B2BCommentModel, B2BCommentData> b2BCommentConverter;
    @Mock
    private Converter<B2BPermissionResultModel, B2BPermissionResultData> b2BPermissionResultConverter;
    @Mock
    private Converter<UserModel, CustomerData> b2bCustomerConverter;
    @Mock
    private Converter<TriggerModel, TriggerData> triggerConverter;
    @Mock
    private Converter<B2BUnitModel, B2BUnitData> b2bUnitConverter;
    @InjectMocks
    private final B2BOrderPopulator b2BOrderPopulator = new B2BOrderPopulator();

    @Before
    public void setUp()
    {
        openMocks = MockitoAnnotations.openMocks(this);
    }
    @After
    public void tearDown() throws Exception
    {
        openMocks.close();
    }

    @Test
    public void shouldPopulate()
    {
        final OrderModel orderModel = mock(OrderModel.class);
        final List<AbstractOrderEntryModel> entries = new ArrayList<>();
        final B2BCostCenterModel b2BCostCenter = mock(B2BCostCenterModel.class);
        final AbstractOrderEntryModel abstractOrderEntryModel = mock(AbstractOrderEntryModel.class);
        entries.add(abstractOrderEntryModel);
        final B2BCostCenterData b2BCostCenterData = mock(B2BCostCenterData.class);

        final B2BUnitModel b2BUnitModel = mock(B2BUnitModel.class);
        final B2BUnitData b2BUnitData = mock(B2BUnitData.class);
        final CheckoutPaymentType checkoutPaymentType = mock(CheckoutPaymentType.class);
        final B2BPaymentTypeData b2BPaymentTypeData = mock(B2BPaymentTypeData.class);

        final List<B2BCommentModel> b2BComments = new ArrayList<>();
        final B2BCommentModel comment1 = mock(B2BCommentModel.class);
        final B2BCommentModel comment2 = mock(B2BCommentModel.class);
        final Date commentDate1 = mock(Date.class);
        final Date commentDate2 = mock(Date.class);
        b2BComments.add(comment1);
        b2BComments.add(comment2);
        final B2BCommentData b2BCommentData1 = mock(B2BCommentData.class);
        final B2BCommentData b2BCommentData2 = mock(B2BCommentData.class);
        final List<B2BCommentData> comments = new ArrayList<>();
        comments.add(b2BCommentData1);
        comments.add(b2BCommentData2);

        final Collection<B2BPermissionResultModel> permissions = new ArrayList<>();
        final B2BPermissionResultModel b2BPermissionResultModel = mock(B2BPermissionResultModel.class);
        permissions.add(b2BPermissionResultModel);
        final List<B2BPermissionResultData> permissionsResults = new ArrayList<>();
        final B2BPermissionResultData b2BPermissionResultData = mock(B2BPermissionResultData.class);
        permissionsResults.add(b2BPermissionResultData);

        final UserModel userModel = mock(UserModel.class);
        final CustomerData customerData = mock(CustomerData.class);
        final CartToOrderCronJobModel cartToOrderCronJobModel = mock(CartToOrderCronJobModel.class);

        final List<TriggerModel> triggers = new ArrayList<>();
        final TriggerModel triggerModel = mock(TriggerModel.class);
        triggers.add(triggerModel);
        final TriggerData triggerData = mock(TriggerData.class);
        final Date triggerDate = mock(Date.class);

        given(orderModel.getEntries()).willReturn(entries);
        given(abstractOrderEntryModel.getCostCenter()).willReturn(b2BCostCenter);
        given(b2BCostCenterConverter.convert(b2BCostCenter)).willReturn(b2BCostCenterData);

        given(orderModel.getUnit()).willReturn(b2BUnitModel);
        given(b2bUnitConverter.convert(b2BUnitModel)).willReturn(b2BUnitData);

        given(orderModel.getPurchaseOrderNumber()).willReturn(PURCHASE_ORDER_NUMBER);
        given(orderModel.getPaymentType()).willReturn(checkoutPaymentType);
        given(b2bPaymentTypeConverter.convert(checkoutPaymentType)).willReturn(b2BPaymentTypeData);

        given(orderModel.getB2bcomments()).willReturn(b2BComments);
        given(comment1.getCreationtime()).willReturn(commentDate1);
        given(comment2.getCreationtime()).willReturn(commentDate2);
        given(comment1.getCreationtime().compareTo(comment2.getCreationtime())).willReturn(1);
        given(b2BCommentConverter.convert(comment2)).willReturn(b2BCommentData2);

        given(orderModel.getPermissionResults()).willReturn(permissions);
        given(b2BPermissionResultConverter.convert(b2BPermissionResultModel)).willReturn(b2BPermissionResultData);

        given(orderModel.getUser()).willReturn(userModel);
        given(b2bCustomerConverter.convert(userModel)).willReturn(customerData);

        given(orderModel.getSchedulingCronJob()).willReturn(cartToOrderCronJobModel);
        given(orderModel.getSchedulingCronJob().getCode()).willReturn(JOB_CODE);
        given(orderModel.getSchedulingCronJob().getTriggers()).willReturn(triggers);
        given(triggerConverter.convert(triggerModel)).willReturn(triggerData);
        given(orderModel.getQuoteExpirationDate()).willReturn(triggerDate);

        given(b2BCommentConverter.convert(comment1)).willReturn(b2BCommentData1);
        given(b2BCommentConverter.convert(comment2)).willReturn(b2BCommentData2);

        final OrderData orderData = new OrderData();
        b2BOrderPopulator.populate(orderModel, orderData);
        Assert.assertEquals(b2BCostCenterData, orderData.getCostCenter());
        Assert.assertEquals(b2BUnitData, orderData.getOrgUnit());
        Assert.assertEquals(PURCHASE_ORDER_NUMBER, orderData.getPurchaseOrderNumber());
        Assert.assertEquals(b2BPaymentTypeData, orderData.getPaymentType());
        Assert.assertEquals(b2BCommentData2, orderData.getB2BComment());
        Assert.assertEquals(1, orderData.getB2bPermissionResult().size());
        Assert.assertEquals(permissionsResults, orderData.getB2bPermissionResult());
        Assert.assertEquals(customerData, orderData.getB2bCustomerData());
        Assert.assertEquals(JOB_CODE, orderData.getJobCode());
        Assert.assertEquals(triggerData, orderData.getTriggerData());
        Assert.assertEquals(triggerDate, orderData.getQuoteExpirationDate());
        Assert.assertEquals(2, orderData.getB2bCommentData().size());
        Assert.assertEquals(comments, orderData.getB2bCommentData());
    }
}
