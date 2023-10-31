/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.b2bunit;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.basecommerce.util.BaseCommerceBaseTest;
import de.hybris.platform.servicelayer.user.UserService;

import javax.annotation.Resource;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@ContextConfiguration(locations =
		{ "classpath:b2bcommerce/test/b2bcommerce-test-spring.xml" })
@IntegrationTest
public class DefaultB2BUnitIsRootDynamicAttributeHandlerIntegrationTest extends BaseCommerceBaseTest
{
	private static final String B2B_CUSTOMER_UID = "DCAdmin";
	private static final String B2B_ROOT_UNIT_UID = "DC";
	private static final String B2B_NON_ROOT_UNIT_UID = "DC Sales";
	private static final int EXPECTED_B2B_ROOT_UNITS_COUNT = 1;
	private static final int EXPECTED_B2B_NON_ROOT_UNITS_COUNT = 3;

	@Resource
	private B2BCommerceUnitService b2bCommerceUnitService;

	@Resource
	private UserService userService;

	@Resource
	private DefaultB2BUnitIsRootDynamicAttributeHandler defaultB2BUnitIsRootDynamicAttributeHandler;

	@Before
	public void beforeTest() throws Exception
	{
		createCoreData();
		importCsv("/b2bcommerce/test/b2bUnitExcludePaymentTypes.impex", "utf-8");
		userService.setCurrentUser(userService.getUserForUID(B2B_CUSTOMER_UID));
	}

	@Test
	public void shouldDetectUnitIsRoot()
	{
		final B2BUnitModel b2bUnitModel = b2bCommerceUnitService.getUnitForUid(B2B_ROOT_UNIT_UID);
		assertThat(defaultB2BUnitIsRootDynamicAttributeHandler.get(b2bUnitModel)).isTrue();
	}

	@Test
	public void shouldDetectUnitIsNotRoot()
	{
		final B2BUnitModel b2bUnitModel = b2bCommerceUnitService.getUnitForUid(B2B_NON_ROOT_UNIT_UID);
		assertThat(defaultB2BUnitIsRootDynamicAttributeHandler.get(b2bUnitModel)).isFalse();
	}

	@Test
	public void shouldDetectIfUnitsOfOrgAreRootUnitOrNot()
	{
		final Set<B2BUnitModel> b2BUnitModelSet = (Set<B2BUnitModel>) b2bCommerceUnitService.getOrganization();

		int rootCount = 0;
		int nonRootCount = 0;

		for (B2BUnitModel b2BUnitModel : b2BUnitModelSet) {
			if (b2BUnitModel.getUid().equals(B2B_ROOT_UNIT_UID)) {
				rootCount++;
				assertThat(defaultB2BUnitIsRootDynamicAttributeHandler.get(b2BUnitModel)).isTrue();
			} else {
				nonRootCount++;
				assertThat(defaultB2BUnitIsRootDynamicAttributeHandler.get(b2BUnitModel)).isFalse();
			}
		}
		assertThat(rootCount).isSameAs(EXPECTED_B2B_ROOT_UNITS_COUNT);
		assertThat(nonRootCount).isSameAs(EXPECTED_B2B_NON_ROOT_UNITS_COUNT);
	}

	@Test
	public void shouldNotSetDynamicAttributeIsRoot()
	{
		final B2BUnitModel b2bUnitModel = b2bCommerceUnitService.getUnitForUid(B2B_ROOT_UNIT_UID);
		assertThatThrownBy(() -> {
			defaultB2BUnitIsRootDynamicAttributeHandler.set(b2bUnitModel, Boolean.FALSE);
		}).isInstanceOf(UnsupportedOperationException.class);
	}
}
