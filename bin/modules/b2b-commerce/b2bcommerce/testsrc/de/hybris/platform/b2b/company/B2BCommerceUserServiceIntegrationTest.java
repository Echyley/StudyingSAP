/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.company;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.model.B2BUserGroupModel;
import de.hybris.platform.b2b.util.B2BCommerceTestUtils;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Collections;
import java.util.Locale;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class B2BCommerceUserServiceIntegrationTest extends ServicelayerTransactionalTest
{
	private static final String PRONTO_ADMIN_USER = "linda.wolf@pronto-hw.com";
	private static final String RUSTIC_ADMIN_USER = "linda.wolf@rustic-hw.com";
	private static final String PRONTO_CUSTOMER_USER = "mark.rivers@pronto-hw.com";
	private static final String RUSTIC_CUSTOMER_USER = "mark.rivers@rustic-hw.com";

	private static final String PRONTO_ENHANCED_GROUP = "enhancedGroup";
	private static final String PRONTO_COMMON_GROUP = "commonGroup";
	private static final String RUSTIC_PREMIUM_GROUP = "premiumGroup";
	private static final String RUSTIC_STANDARD_GROUP = "standardGroup";
	private static final String RUSTIC_LIMITED_GROUP = "limitedGroup";

	private static final PageableData PAGEABLE_DATA = B2BCommerceTestUtils.createPageableData();

	@Resource
	private B2BCommerceUserService b2bCommerceUserService;

	@Resource
	private B2BCommerceB2BUserGroupService b2bCommerceB2BUserGroupService;

	@Resource
	private I18NService i18NService;

	@Resource
	private CommonI18NService commonI18NService;

	@Resource
	private UserService userService;

	private User prontoAdminUser;
	private User rusticAdminUser;
	private User rusticCustomerUser;

	@Before
	public void setUp() throws Exception
	{
		de.hybris.platform.servicelayer.ServicelayerTest.createCoreData();
		de.hybris.platform.servicelayer.ServicelayerTest.createDefaultCatalog();
		de.hybris.platform.catalog.jalo.CatalogManager.getInstance().createEssentialData(Collections.emptyMap(), null);
		importCsv("/impex/essentialdata_1_usergroups.impex", "UTF-8");
		importCsv("/impex/essentialdata_2_b2bcommerce.impex", "UTF-8");

		importCsv("/b2bcommerce/test/usergroups.impex", "UTF-8");

		i18NService.setCurrentLocale(Locale.ENGLISH);
		commonI18NService.setCurrentLanguage(commonI18NService.getLanguage("en"));
		commonI18NService.setCurrentCurrency(commonI18NService.getCurrency("USD"));

		// this to avoid search restrictions being applied to Users based on the current logged in user.
		rusticAdminUser = UserManager.getInstance().getUserByLogin(RUSTIC_ADMIN_USER);
		prontoAdminUser = UserManager.getInstance().getUserByLogin(PRONTO_ADMIN_USER);
		rusticCustomerUser = UserManager.getInstance().getUserByLogin(RUSTIC_CUSTOMER_USER);
	}


	@Test
	public void shouldGetParentUnitAsCustomer()
	{
		JaloSession.getCurrentSession().setUser(rusticCustomerUser);

		final B2BUnitModel unitModel = b2bCommerceUserService.getParentUnitForCustomer(RUSTIC_CUSTOMER_USER);
		assertThat(unitModel).isNotNull().hasFieldOrPropertyWithValue("uid", "Rustic Retail");
	}

	@Test
	public void shouldNotGetParentUnitWhenNotAssignedAsCustomer()
	{
		JaloSession.getCurrentSession().setUser(rusticCustomerUser);

		assertThatThrownBy(() -> b2bCommerceUserService.getParentUnitForCustomer(PRONTO_CUSTOMER_USER))
			.isInstanceOf(UnknownIdentifierException.class)
			.hasMessage("Cannot find user with uid 'mark.rivers@pronto-hw.com'");
	}

	@Test
	public void shouldNotGetParentUnitForB2BAdminAsCustomer()
	{
		JaloSession.getCurrentSession().setUser(rusticCustomerUser);

		assertThatThrownBy(() -> b2bCommerceUserService.getParentUnitForCustomer(RUSTIC_ADMIN_USER))
			.isInstanceOf(UnknownIdentifierException.class)
			.hasMessage("Cannot find user with uid 'linda.wolf@rustic-hw.com'");
	}

	@Test
	public void shouldGetParentUnitAsB2BAdmin()
	{
		JaloSession.getCurrentSession().setUser(rusticAdminUser);

		final B2BUnitModel unitModel = b2bCommerceUserService.getParentUnitForCustomer(RUSTIC_ADMIN_USER);
		assertThat(unitModel).isNotNull().hasFieldOrPropertyWithValue("uid", "Rustic");
	}

	@Test
	public void shouldNotGetParentUnitWhenNotAssignedAsB2BAdmin()
	{
		JaloSession.getCurrentSession().setUser(rusticAdminUser);

		assertThatThrownBy(() -> b2bCommerceUserService.getParentUnitForCustomer(PRONTO_ADMIN_USER))
			.isInstanceOf(UnknownIdentifierException.class)
			.hasMessage("Cannot find user with uid 'linda.wolf@pronto-hw.com'");
	}

	@Test
	public void shouldGetParentUnitForB2BCustomerAsB2BAdmin()
	{
		JaloSession.getCurrentSession().setUser(rusticAdminUser);

		final B2BUnitModel unitModel = b2bCommerceUserService.getParentUnitForCustomer(RUSTIC_CUSTOMER_USER);
		assertThat(unitModel).isNotNull().hasFieldOrPropertyWithValue("uid", "Rustic Retail");
	}

	@Test
	public void shouldAddUserGroupToUserAsB2BAdmin()
	{
		JaloSession.getCurrentSession().setUser(rusticAdminUser);

		final B2BUserGroupModel userGroup = b2bCommerceUserService.addB2BUserGroupToCustomer(RUSTIC_CUSTOMER_USER, RUSTIC_LIMITED_GROUP);
		assertThat(userGroup).isNotNull()
							 .hasFieldOrPropertyWithValue("uid", "limitedGroup");

		assertThat(userGroup.getMembers()).extracting("uid")
										  .contains(RUSTIC_CUSTOMER_USER);
	}

	@Test
	public void shouldNotAddUserGroupFromDifferentUnitToUserAsB2BAdmin()
	{
		JaloSession.getCurrentSession().setUser(rusticAdminUser);

		assertThatThrownBy(() -> b2bCommerceUserService.addB2BUserGroupToCustomer(RUSTIC_CUSTOMER_USER, PRONTO_COMMON_GROUP))
			.isInstanceOf(UnknownIdentifierException.class)
			.hasMessage("UserGroupModel with uid 'commonGroup' not found!");
	}

	@Test
	public void shouldNotAddUserGroupToUserAsB2BCustomer()
	{
		JaloSession.getCurrentSession().setUser(rusticCustomerUser);

		assertThatThrownBy(() -> b2bCommerceUserService.addB2BUserGroupToCustomer("marie.dubois@rustic-hw.com", RUSTIC_PREMIUM_GROUP))
			.isInstanceOf(UnknownIdentifierException.class)
			.hasMessage("UserGroupModel with uid 'premiumGroup' not found!");
	}

	@Test
	public void shouldNotAddUserGroupFromDifferentUnitToUserAsB2BCustomer()
	{
		JaloSession.getCurrentSession().setUser(rusticCustomerUser);

		assertThatThrownBy(() -> b2bCommerceUserService.addB2BUserGroupToCustomer(RUSTIC_CUSTOMER_USER, PRONTO_COMMON_GROUP))
			.isInstanceOf(UnknownIdentifierException.class)
			.hasMessage("UserGroupModel with uid 'commonGroup' not found!");
	}

	@Test
	public void shouldRemoveUserGroupFromUserAsB2BAdmin()
	{
		JaloSession.getCurrentSession().setUser(rusticAdminUser);

		B2BCustomerModel customer = userService.getUserForUID(RUSTIC_CUSTOMER_USER, B2BCustomerModel.class);
		assertThat(customer).isNotNull();
		assertThat(customer.getGroups()).extracting("uid").contains(RUSTIC_PREMIUM_GROUP);

		B2BUserGroupModel userGroup =  b2bCommerceB2BUserGroupService.getUserGroupForUID(RUSTIC_PREMIUM_GROUP, B2BUserGroupModel.class);
		assertThat(userGroup).isNotNull();
		assertThat(userGroup.getMembers()).extracting("uid").contains(RUSTIC_CUSTOMER_USER);

		b2bCommerceUserService.removeB2BUserGroupFromCustomerGroups(RUSTIC_CUSTOMER_USER, RUSTIC_PREMIUM_GROUP);
		customer = userService.getUserForUID(RUSTIC_CUSTOMER_USER, B2BCustomerModel.class);
		assertThat(customer).isNotNull();
		assertThat(customer.getGroups()).extracting("uid").doesNotContain(RUSTIC_PREMIUM_GROUP);
	}

	@Test
	public void shouldNotRemoveUserGroupFromDifferentUnitFromUserAsB2BAdmin()
	{
		JaloSession.getCurrentSession().setUser(prontoAdminUser);
		B2BCustomerModel customer = userService.getUserForUID(PRONTO_CUSTOMER_USER, B2BCustomerModel.class);
		assertThat(customer).isNotNull();
		assertThat(customer.getGroups()).extracting("uid").contains(PRONTO_ENHANCED_GROUP);

		JaloSession.getCurrentSession().setUser(rusticAdminUser);
		assertThatThrownBy(() -> b2bCommerceUserService.removeB2BUserGroupFromCustomerGroups(PRONTO_CUSTOMER_USER, PRONTO_ENHANCED_GROUP))
			.isInstanceOf(UnknownIdentifierException.class)
			.hasMessage("Cannot find user with uid 'mark.rivers@pronto-hw.com'");

		JaloSession.getCurrentSession().setUser(prontoAdminUser);
		final B2BUserGroupModel userGroup =  b2bCommerceB2BUserGroupService.getUserGroupForUID(PRONTO_ENHANCED_GROUP, B2BUserGroupModel.class);
		customer = userService.getUserForUID(PRONTO_CUSTOMER_USER, B2BCustomerModel.class);
		assertThat(customer).isNotNull();
		assertThat(customer.getGroups()).extracting("uid").contains(PRONTO_ENHANCED_GROUP);
	}

	@Test
	public void shouldNotRemoveUserGroupFromDifferentUnitFromUserAsB2BCustomer()
	{
		JaloSession.getCurrentSession().setUser(rusticCustomerUser);

		assertThatThrownBy(() -> b2bCommerceUserService.removeB2BUserGroupFromCustomerGroups(PRONTO_CUSTOMER_USER, PRONTO_COMMON_GROUP))
			.isInstanceOf(UnknownIdentifierException.class)
			.hasMessage("Cannot find user with uid 'mark.rivers@pronto-hw.com'");
	}
}
