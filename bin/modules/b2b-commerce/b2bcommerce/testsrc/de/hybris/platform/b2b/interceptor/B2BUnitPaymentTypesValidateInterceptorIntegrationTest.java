/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.interceptor;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.enums.CheckoutPaymentType;
import de.hybris.platform.basecommerce.util.BaseCommerceBaseTest;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import javax.annotation.Resource;

import java.util.Collections;
import java.util.Locale;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;


@ContextConfiguration(locations =
		{ "classpath:b2bcommerce/test/b2bcommerce-test-spring.xml" })
@IntegrationTest
public class B2BUnitPaymentTypesValidateInterceptorIntegrationTest extends BaseCommerceBaseTest
{

	private static final String B2B_CUSTOMER_UID = "DCAdmin";
	private static final String B2B_ROOT_UNIT_UID = "DC";
	private static final String B2B_CHILD_UNIT_UID = "DC Sales US";

	@Resource
	private UserService userService;

	@Resource
	private B2BCommerceUnitService b2bCommerceUnitService;

	@Resource
	protected ModelService modelService;

	@Resource
	private EnumerationService enumerationService;

	@Resource(name = "i18NService")
	private I18NService i18NService;

	@Resource(name = "l10nService")
	private L10NService l10NService;

	@Before
	public void beforeTest() throws Exception
	{
		createCoreData();
		importCsv("/b2bcommerce/test/b2bUnitExcludePaymentTypes.impex", "utf-8");
		userService.setCurrentUser(userService.getUserForUID(B2B_CUSTOMER_UID));
		i18NService.setCurrentLocale(Locale.ENGLISH);
	}

	@Test
	public void shouldSaveB2BUnitWithNoExcludedPaymentTypes() {
		final B2BUnitModel b2bUnitModel = b2bCommerceUnitService.getUnitForUid(B2B_ROOT_UNIT_UID);
		b2bUnitModel.setB2bExcludedPaymentTypes(Collections.EMPTY_SET);
		modelService.save(b2bUnitModel);

		final B2BUnitModel b2bUnitModel2 = b2bCommerceUnitService.getUnitForUid(B2B_ROOT_UNIT_UID);
		assertThat(b2bUnitModel2.getB2bExcludedPaymentTypes().isEmpty()).isTrue();
	}

	@Test
	public void shouldSaveB2BUnitWithOnlyOneExcludedPaymentType() {
		final B2BUnitModel b2bUnitModel = b2bCommerceUnitService.getUnitForUid(B2B_ROOT_UNIT_UID);
		b2bUnitModel.setB2bExcludedPaymentTypes(Set.of(CheckoutPaymentType.CARD));
		modelService.save(b2bUnitModel);

		final B2BUnitModel b2bUnitModel2 = b2bCommerceUnitService.getUnitForUid(B2B_ROOT_UNIT_UID);
		assertThat(b2bUnitModel2.getB2bExcludedPaymentTypes()).isNotEmpty().hasSize(1).contains(CheckoutPaymentType.CARD);
	}

	@Test
	public void shouldNotSaveB2BUnitWithAllPaymentTypesExcluded() {
		final B2BUnitModel b2bUnitModel = b2bCommerceUnitService.getUnitForUid(B2B_ROOT_UNIT_UID);
		b2bUnitModel.setB2bExcludedPaymentTypes(Set.copyOf(enumerationService.getEnumerationValues(CheckoutPaymentType._TYPECODE)));
		Exception exception = assertThrows(ModelSavingException.class, () -> modelService.save(b2bUnitModel));
		assertThat(exception).hasMessageEndingWith(l10NService.getLocalizedString("error.b2bunit.excludedallpaymenttypes"));
	}

	@Test
	public void shouldNotSaveChildB2BUnitWithExcludedPaymentType() {
		final B2BUnitModel b2bUnitModel = b2bCommerceUnitService.getUnitForUid(B2B_CHILD_UNIT_UID);
		b2bUnitModel.setB2bExcludedPaymentTypes(Set.of(CheckoutPaymentType.CARD));
		Exception exception = assertThrows(ModelSavingException.class, () -> modelService.save(b2bUnitModel));
		assertThat(exception).hasMessageEndingWith(l10NService.getLocalizedString("error.b2bunit.nonroot.excludedpaymenttypes"));
	}

}

