/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commercewebservicescommons.services.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercewebservicescommons.dto.captcha.CaptchaProviderWsDtoType;
import de.hybris.platform.commercewebservicescommons.dto.captcha.CaptchaValidationContext;
import de.hybris.platform.commercewebservicescommons.dto.captcha.CaptchaValidationResult;
import de.hybris.platform.commercewebservicescommons.dto.captcha.CaptchaVersionWsDtoType;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.CaptchaTokenMissingException;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.CaptchaValidationException;
import de.hybris.platform.commercewebservicescommons.strategies.CaptchaValidationStrategy;
import de.hybris.platform.commercewebservicescommons.strategies.impl.GoogleRecaptchaV2ValidationStrategy;
import de.hybris.platform.webservicescommons.errors.exceptions.NotFoundException;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.any;


/**
 * Test suite for {@link de.hybris.platform.commercewebservicescommons.services.CaptchaValidationService}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCaptchaValidationServiceTest
{
	@InjectMocks
	private DefaultCaptchaValidationService captchaValidationService;

	@Mock
	private GoogleRecaptchaV2ValidationStrategy captchaValidationStrategy;

	private List<CaptchaValidationStrategy> strategies;

	@Mock
	private CaptchaValidationContext context;

	private final static String CAPTCHA_TOKEN = "03ANYolqv1YE-eCxznv6cWNaBf9VgzFn3mCGWXCQqoroWGk6fdjCj3OYt57wLZ5Wzo9frd6KQ4A2R6amEtYzCjOfDaqLg52M0eDx9eIQmSDRjfnW6nCQqFPv_MYVM5ZgA_08W7RoVFJ38r2kELfGw5p3-X7qs8AYKUH7F28DEtl_ytfeg-leklb7bfKgRfMhQtcuVRxIsp9DShbgWf_FzU809CVowLnbSKe3JhIBf0MQdhsO5Wgsl4_itmVFtnmtMd3ZfDBllc_3yYczsEMrmeewMsYHJ-IKPkK4YEiS0jXZ_Ubc3Cla4sQzrnbrF6GSRbh46G2pAEhmk8gqyUQXmkbdnR7iMLdZ5dZfZVjNQxQqusvzC7H7X66r3Lh3mCliZE2M1ChQejOMMaqGuCMnWX1CYE7JsBT0LZIhFezJsuoCQvQNrK396QdqhiymaQ4uFMlifv5GRa8aYF3XUfZsT6_yQgvsCm3C5edy7gc7KikhQ5HWOgQhq18VY";

	@Before
	public void setUp()
	{
		strategies = List.of(captchaValidationStrategy);
		ReflectionTestUtils.setField(captchaValidationService, "strategies", strategies);
		Mockito.when(context.getBaseSiteId()).thenReturn("electronics");
		Mockito.when(context.getBaseStoreId()).thenReturn("electronics");
		Mockito.when(context.isCaptchaCheckEnabled()).thenReturn(true);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValidateShouldThrowCaptchaTokenMissingExceptionWhenContextIsNull()
	{
		//when
		captchaValidationService.validate(null);

		//verify
		Mockito.verify(context, Mockito.times(0)).getBaseSiteId();
		Mockito.verify(context, Mockito.times(0)).getBaseStoreId();
		Mockito.verify(context, Mockito.times(0)).getCaptchaToken();
		Mockito.verify(captchaValidationStrategy, Mockito.times(0)).getProviderType();
		Mockito.verify(captchaValidationStrategy, Mockito.times(0)).getVersion();
		Mockito.verify(captchaValidationStrategy, Mockito.times(0)).validate(any());
	}

	@Test
	public void testValidateShouldDoNothingWhenCaptchaCheckEnabledIsFalse()
	{
		//when
		Mockito.when(context.isCaptchaCheckEnabled()).thenReturn(false);
		captchaValidationService.validate(context);

		//verify
		Mockito.verify(context, Mockito.times(0)).getBaseSiteId();
		Mockito.verify(context, Mockito.times(0)).getBaseStoreId();
		Mockito.verify(context, Mockito.times(0)).getCaptchaToken();
		Mockito.verify(captchaValidationStrategy, Mockito.times(0)).getProviderType();
		Mockito.verify(captchaValidationStrategy, Mockito.times(0)).getVersion();
		Mockito.verify(captchaValidationStrategy, Mockito.times(0)).validate(any());
	}

	@Test(expected = CaptchaTokenMissingException.class)
	public void testValidateShouldThrowCaptchaTokenMissingExceptionWhenCaptchaTokenIsNull()
	{
		//given
		Mockito.when(context.getCaptchaToken()).thenReturn(null);

		//when
		captchaValidationService.validate(context);

		//verify
		Mockito.verify(context, Mockito.times(1)).getBaseSiteId();
		Mockito.verify(context, Mockito.times(1)).getBaseStoreId();
		Mockito.verify(context, Mockito.times(1)).getCaptchaToken();
		Mockito.verify(captchaValidationStrategy, Mockito.times(0)).getProviderType();
		Mockito.verify(captchaValidationStrategy, Mockito.times(0)).getVersion();
		Mockito.verify(captchaValidationStrategy, Mockito.times(0)).validate(any());
	}

	@Test(expected = CaptchaTokenMissingException.class)
	public void testValidateShouldThrowCaptchaTokenMissingExceptionWhenCaptchaTokenIsEmpty()
	{
		//given
		Mockito.when(context.getCaptchaToken()).thenReturn("");

		//when
		captchaValidationService.validate(context);

		//verify
		Mockito.verify(context, Mockito.times(1)).getBaseSiteId();
		Mockito.verify(context, Mockito.times(1)).getBaseStoreId();
		Mockito.verify(context, Mockito.times(1)).getCaptchaToken();
		Mockito.verify(captchaValidationStrategy, Mockito.times(0)).getProviderType();
		Mockito.verify(captchaValidationStrategy, Mockito.times(0)).getVersion();
		Mockito.verify(captchaValidationStrategy, Mockito.times(0)).validate(any());
	}

	@Test(expected = CaptchaTokenMissingException.class)
	public void testValidateShouldThrowCaptchaTokenMissingExceptionWhenCaptchaTokenIsBlank()
	{
		//given
		Mockito.when(context.getCaptchaToken()).thenReturn("   ");

		//when
		captchaValidationService.validate(context);

		//verify
		Mockito.verify(context, Mockito.times(1)).getBaseSiteId();
		Mockito.verify(context, Mockito.times(1)).getBaseStoreId();
		Mockito.verify(context, Mockito.times(1)).getCaptchaToken();
		Mockito.verify(captchaValidationStrategy, Mockito.times(0)).getProviderType();
		Mockito.verify(captchaValidationStrategy, Mockito.times(0)).getVersion();
		Mockito.verify(captchaValidationStrategy, Mockito.times(0)).validate(any());
	}

	@Test(expected = NotFoundException.class)
	public void testValidateShouldThrowNotFoundExceptionWhenNoProperStrategyFound()
	{
		//given
		Mockito.when(context.getCaptchaToken()).thenReturn(CAPTCHA_TOKEN);
		Mockito.when(captchaValidationStrategy.getProviderType()).thenReturn(null);

		//when
		captchaValidationService.validate(context);

		//verify
		Mockito.verify(context, Mockito.times(0)).getBaseSiteId();
		Mockito.verify(context, Mockito.times(0)).getBaseStoreId();
		Mockito.verify(context, Mockito.times(1)).getCaptchaToken();
		Mockito.verify(captchaValidationStrategy, Mockito.times(1)).getProviderType();
		Mockito.verify(captchaValidationStrategy, Mockito.times(0)).getVersion();
		Mockito.verify(captchaValidationStrategy, Mockito.times(0)).validate(any());
	}

	@Test(expected = CaptchaValidationException.class)
	public void testValidateShouldThrowCaptchaValidationExceptionWhenPreCheckFailed()
	{
		//given
		Mockito.when(context.getCaptchaToken()).thenReturn(CAPTCHA_TOKEN);
		Mockito.when(captchaValidationStrategy.getProviderType()).thenReturn(CaptchaProviderWsDtoType.GOOGLE);
		Mockito.when(captchaValidationStrategy.getVersion()).thenReturn(CaptchaVersionWsDtoType.V2);
		Mockito.when(captchaValidationStrategy.preCheckToken(context)).thenReturn(false);

		//when
		captchaValidationService.validate(context);

		//verify
		Mockito.verify(context, Mockito.times(1)).getBaseSiteId();
		Mockito.verify(context, Mockito.times(1)).getBaseStoreId();
		Mockito.verify(context, Mockito.times(1)).getCaptchaToken();
		Mockito.verify(captchaValidationStrategy, Mockito.times(1)).getProviderType();
		Mockito.verify(captchaValidationStrategy, Mockito.times(1)).getVersion();
		Mockito.verify(captchaValidationStrategy, Mockito.times(1)).preCheckToken(context);
		Mockito.verify(captchaValidationStrategy, Mockito.times(1)).validate(any());
	}

	@Test(expected = CaptchaValidationException.class)
	public void testValidateShouldThrowCaptchaValidationExceptionWhenTokenIsInvalid()
	{
		//given
		Mockito.when(context.getCaptchaToken()).thenReturn(CAPTCHA_TOKEN);
		Mockito.when(captchaValidationStrategy.getProviderType()).thenReturn(CaptchaProviderWsDtoType.GOOGLE);
		Mockito.when(captchaValidationStrategy.getVersion()).thenReturn(CaptchaVersionWsDtoType.V2);
		Mockito.when(captchaValidationStrategy.preCheckToken(context)).thenReturn(true);

		CaptchaValidationResult result = new CaptchaValidationResult();
		result.setSuccess(false);
		result.setReason("invalid-input-response");
		Mockito.when(captchaValidationStrategy.validate(context)).thenReturn(result);

		//when
		captchaValidationService.validate(context);

		//verify
		Mockito.verify(context, Mockito.times(1)).getBaseSiteId();
		Mockito.verify(context, Mockito.times(1)).getBaseStoreId();
		Mockito.verify(context, Mockito.times(1)).getCaptchaToken();
		Mockito.verify(captchaValidationStrategy, Mockito.times(1)).getProviderType();
		Mockito.verify(captchaValidationStrategy, Mockito.times(1)).getVersion();
		Mockito.verify(captchaValidationStrategy, Mockito.times(1)).preCheckToken(context);
		Mockito.verify(captchaValidationStrategy, Mockito.times(1)).validate(any());
	}

	@Test
	public void testValidateShouldSuccessWhenTokenIsValid()
	{
		//given
		Mockito.when(context.getCaptchaToken()).thenReturn(CAPTCHA_TOKEN);
		Mockito.when(captchaValidationStrategy.getProviderType()).thenReturn(CaptchaProviderWsDtoType.GOOGLE);
		Mockito.when(captchaValidationStrategy.getVersion()).thenReturn(CaptchaVersionWsDtoType.V2);
		Mockito.when(captchaValidationStrategy.preCheckToken(context)).thenReturn(true);

		CaptchaValidationResult result = new CaptchaValidationResult();
		result.setSuccess(true);
		Mockito.when(captchaValidationStrategy.validate(context)).thenReturn(result);

		//when
		captchaValidationService.validate(context);

		//verify
		Mockito.verify(context, Mockito.times(0)).getBaseSiteId();
		Mockito.verify(context, Mockito.times(0)).getBaseStoreId();
		Mockito.verify(context, Mockito.times(1)).getCaptchaToken();
		Mockito.verify(captchaValidationStrategy, Mockito.times(1)).getProviderType();
		Mockito.verify(captchaValidationStrategy, Mockito.times(1)).getVersion();
		Mockito.verify(captchaValidationStrategy, Mockito.times(1)).preCheckToken(context);
		Mockito.verify(captchaValidationStrategy, Mockito.times(1)).validate(any());
	}
}
