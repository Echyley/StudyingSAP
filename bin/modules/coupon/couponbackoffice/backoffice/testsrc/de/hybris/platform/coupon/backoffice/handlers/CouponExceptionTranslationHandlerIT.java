/*
 * [y] hybris Platform
 *
 * Copyright (c) 2020 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */

package de.hybris.platform.coupon.backoffice.handlers;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.couponservices.model.CodeGenerationConfigurationModel;
import de.hybris.platform.couponservices.model.MultiCodeCouponModel;
import de.hybris.platform.couponservices.model.SingleCodeCouponModel;
import de.hybris.platform.ruleenginebackoffice.handlers.RuleEngineExceptionTranslationHandler;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Arrays;

import java.util.UUID;


@IntegrationTest
public class CouponExceptionTranslationHandlerIT extends ServicelayerTest {
    @Resource
    private ModelService modelService;

    private RuleEngineExceptionTranslationHandler handler;

    private final static String UUID_STRING = UUID.randomUUID().toString();
    @Before
    public void setUp() throws Exception
    {
        handler = new RuleEngineExceptionTranslationHandler();
        handler.setSupportedInterceptors(Arrays.asList(
                "CodeGenerationConfigurationUsageValidateInterceptor",
                "CodeGenerationConfigurationValidateInterceptor",
                "MultiCodeCouponValidateInterceptor",
                "SingleCodeCouponValidateInterceptor"));
    }

    @Test
    public void shouldWorkForCodeGenerationConfigurationValidateInterceptor() throws Exception
    {
        //given
        final CodeGenerationConfigurationModel model = new CodeGenerationConfigurationModel();
        model.setName(UUID_STRING);
        model.setCouponPartCount(1);
        model.setCouponPartLength(1);
        model.setCodeSeparator("-");
        //then
        final Throwable throwable = ThrowableAssert.catchThrowable( () -> modelService.save(model) );
        //then
        Assertions.assertThat(handler.canHandle(throwable)).isTrue();
        Assertions.assertThat(handler.toString(throwable)).contains("The product of 'coupon part length' and 'coupon part count' must be at least 4!");
    }
    @Test
    public void shouldWorkForSingleCodeCouponModel() throws Exception
    {
        //given
        final SingleCodeCouponModel model = new SingleCodeCouponModel();
        //then
        final Throwable throwable = ThrowableAssert.catchThrowable( () -> modelService.save(model) );
        //then
        Assertions.assertThat(handler.canHandle(throwable)).isTrue();
        Assertions.assertThat(handler.toString(throwable)).contains("CouponId must be specified");
    }
    @Test
    public void shouldWorkForMultiCodeCouponValidateInterceptor() throws Exception
    {
        //given
        final MultiCodeCouponModel model = new MultiCodeCouponModel();
        //then
        final Throwable throwable = ThrowableAssert.catchThrowable( () -> modelService.save(model) );
        //then
        Assertions.assertThat(handler.canHandle(throwable)).isTrue();
        Assertions.assertThat(handler.toString(throwable)).contains("CouponId must be specified");
    }

}
