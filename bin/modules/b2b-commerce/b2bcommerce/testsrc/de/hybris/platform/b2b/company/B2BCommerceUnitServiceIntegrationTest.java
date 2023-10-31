/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.company;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.catalog.jalo.CatalogManager;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.I18NService;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;

@IntegrationTest
public class B2BCommerceUnitServiceIntegrationTest extends ServicelayerTransactionalTest
{
	private static final String CUSTOM_RETAIL = "Test Custom Retail";
	private static final String RUSTIC_CUSTOMER_USER = "customer.k@rustic-hw.com";
	private static final String PRONTO_CUSTOMER_USER = "customer.p@pronto-hw.com";
	private static final String RUSTIC_ADMIN_USER = "admin.z@rustic-hw.com";
	private static final String PRONTO_ADMIN_USER = "admin.p@pronto-hw.com";

	@Resource
	private B2BCommerceUnitService b2bCommerceUnitService;

	@Resource
	private I18NService i18NService;

	@Resource
	private CommonI18NService commonI18NService;

	private User rusticAdminUser;
	private User rusticCustomerUser;
	private User prontoAdminUser;
	private User prontoCustomerUser;

	@Before
	public void setup() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
		createDefaultUsers();
		CatalogManager.getInstance().createEssentialData(Collections.emptyMap(), null);

		i18NService.setCurrentLocale(Locale.ENGLISH);
		commonI18NService.setCurrentLanguage(commonI18NService.getLanguage("en"));
		commonI18NService.setCurrentCurrency(commonI18NService.getCurrency("USD"));

		importCsv("/impex/essentialdata_1_usergroups.impex", "UTF-8");
		importCsv("/impex/essentialdata_2_b2bcommerce.impex", "UTF-8");
		importCsv("/b2bcommerce/test/usergroups.impex", "UTF-8");

		rusticAdminUser = UserManager.getInstance().getUserByLogin(RUSTIC_ADMIN_USER);
		rusticCustomerUser = UserManager.getInstance().getUserByLogin(RUSTIC_CUSTOMER_USER);
		prontoAdminUser = UserManager.getInstance().getUserByLogin(PRONTO_ADMIN_USER);
		prontoCustomerUser = UserManager.getInstance().getUserByLogin(PRONTO_CUSTOMER_USER);
	}

	@Test
	public void shouldGetUnitAsCustomer()
	{
		JaloSession.getCurrentSession().setUser(rusticCustomerUser);

		final B2BUnitModel unitModel = b2bCommerceUnitService.getUnitForUid("Test Custom Retail");
		assertThat(unitModel).isNotNull().hasFieldOrPropertyWithValue("name", "Test Custom Retail");
	}

	@Test
	public void shouldNotGetUnitWhenNotAssignedAsCustomer()
	{
		JaloSession.getCurrentSession().setUser(rusticCustomerUser);

		final B2BUnitModel unitModel = b2bCommerceUnitService.getUnitForUid("Test Pronto");
		assertThat(unitModel).isNull();
	}

	@Test
	public void shouldGetUnitAsB2BAdmin()
	{
		JaloSession.getCurrentSession().setUser(rusticAdminUser);

		final B2BUnitModel unitModel = b2bCommerceUnitService.getUnitForUid("Test Custom Retail");
		assertThat(unitModel).isNotNull().hasFieldOrPropertyWithValue("name", "Test Custom Retail");
	}

	@Test
	public void shouldNotGetUnitAsB2BAdminWhenNotAssignedToUnit()
	{
		JaloSession.getCurrentSession().setUser(rusticAdminUser);

		final B2BUnitModel unitModel = b2bCommerceUnitService.getUnitForUid("Test Pronto");
		assertThat(unitModel).isNull();
	}

	@Test
	public void shouldGetOrganizationAsCustomer()
	{
		JaloSession.getCurrentSession().setUser(rusticCustomerUser);

		final Collection<? extends B2BUnitModel> organizations = b2bCommerceUnitService.getOrganization();
		assertThat(organizations).isNotNull()
				.hasSize(1)
				.extracting("uid")
				.contains("Test Rustic");
	}

	@Test
	public void shouldGetOrganizationAsB2BAdmin()
	{
		JaloSession.getCurrentSession().setUser(rusticAdminUser);

		final Collection<? extends B2BUnitModel> organizations = b2bCommerceUnitService.getOrganization();
		assertThat(organizations).isNotNull()
								 .hasSize(4)
								 .extracting("uid")
								 .contains("Test Rustic", "Test Rustic Retail", CUSTOM_RETAIL, "Test Custom-B Retail");
	}

	@Test
	public void shouldGetBranchAsCustomer()
	{
		JaloSession.getCurrentSession().setUser(rusticCustomerUser);

		final Collection<? extends B2BUnitModel> branches = b2bCommerceUnitService.getBranch();
		assertThat(branches).isNotNull()
				.hasSize(1)
				.extracting("uid")
				.contains("Test Custom Retail");
	}

	@Test
	public void shouldGetBranchAsB2BAdmin()
	{
		JaloSession.getCurrentSession().setUser(rusticAdminUser);

		final Collection<? extends B2BUnitModel> branches = b2bCommerceUnitService.getBranch();
		assertThat(branches).isNotNull()
							.hasSize(4)
							.extracting("uid")
							.containsExactlyInAnyOrder("Test Rustic", "Test Rustic Retail", CUSTOM_RETAIL, "Test Custom-B Retail");
	}

	@Test
	public void shouldGetRootUnitAsCustomer()
	{
		JaloSession.getCurrentSession().setUser(rusticCustomerUser);

		final B2BUnitModel rootUnit = b2bCommerceUnitService.getRootUnit();
		assertThat(rootUnit).isNotNull()
				.hasFieldOrPropertyWithValue("uid", "Test Rustic");
	}

	@Test
	public void shouldGetRootUnitAsB2BAdmin()
	{
		JaloSession.getCurrentSession().setUser(rusticAdminUser);

		final B2BUnitModel rootUnit = b2bCommerceUnitService.getRootUnit();
		assertThat(rootUnit).isNotNull()
							.hasFieldOrPropertyWithValue("uid", "Test Rustic");
	}

	@Test
	public void shouldGetParentUnitAsCustomer()
	{
		JaloSession.getCurrentSession().setUser(rusticCustomerUser);

		final B2BUnitModel parentUnit = b2bCommerceUnitService.getParentUnit();
		assertThat(parentUnit).isNotNull()
				.hasFieldOrPropertyWithValue("uid", "Test Custom Retail");
	}

	@Test
	public void shouldGetParentUnitAsB2BAdmin()
	{
		JaloSession.getCurrentSession().setUser(rusticAdminUser);

		final B2BUnitModel parentUnit = b2bCommerceUnitService.getParentUnit();
		assertThat(parentUnit).isNotNull()
							  .hasFieldOrPropertyWithValue("uid", "Test Rustic");
	}

	@Test
	public void shouldGetAllUnitsOfOrganizationAsCustomer()
	{
		JaloSession.getCurrentSession().setUser(rusticCustomerUser);

		final Collection<? extends B2BUnitModel> allUnits = b2bCommerceUnitService.getAllUnitsOfOrganization();
		assertThat(allUnits).isNotNull()
				.hasSize(1)
				.extracting("uid")
				.contains("Test Custom Retail");
	}

	@Test
	public void shouldGetAllUnitsOfOrganizationAsB2BAdmin()
	{
		JaloSession.getCurrentSession().setUser(rusticAdminUser);

		final Collection<? extends B2BUnitModel> allUnits = b2bCommerceUnitService.getAllUnitsOfOrganization();
		assertThat(allUnits).isNotNull()
							.hasSize(4)
							.extracting("uid")
							.containsExactlyInAnyOrder("Test Rustic", "Test Rustic Retail", CUSTOM_RETAIL, "Test Custom-B Retail");
	}

	@Test
	public void shouldGetAllowedParentUnitsAsCustomer()
	{
		JaloSession.getCurrentSession().setUser(rusticCustomerUser);

		final B2BUnitModel unitModel = b2bCommerceUnitService.getUnitForUid(CUSTOM_RETAIL);
		final Collection<? extends B2BUnitModel> allUnits = b2bCommerceUnitService.getAllowedParentUnits(unitModel);

		assertThat(allUnits).isNotNull()
				.hasSize(1)
				.extracting("uid")
				.contains("Test Rustic Retail");
	}

	@Test
	public void shouldGetAllowedParentUnitsAsB2BAdmin()
	{
		JaloSession.getCurrentSession().setUser(rusticAdminUser);

		final B2BUnitModel unitModel = b2bCommerceUnitService.getUnitForUid(CUSTOM_RETAIL);
		final Collection<? extends B2BUnitModel> allUnits = b2bCommerceUnitService.getAllowedParentUnits(unitModel);

		assertThat(allUnits).isNotNull()
							.hasSize(3)
							.extracting("uid")
							.containsExactlyInAnyOrder("Test Rustic", "Test Rustic Retail", "Test Custom-B Retail");
	}

	@Test
	public void shouldSetParentUnitAsCustomer()
	{
		JaloSession.getCurrentSession().setUser(rusticAdminUser);
		final B2BUnitModel newParentUnitModel = b2bCommerceUnitService.getUnitForUid("Test Rustic Retail");
		final B2BUnitModel unitModel = b2bCommerceUnitService.getUnitForUid("Test Custom-B Retail");

		assertThat(newParentUnitModel).isNotNull();
		assertThat(unitModel).isNotNull();

		B2BUnitModel oldParentUnitModel = b2bCommerceUnitService.getParentUnit(unitModel);
		assertThat(oldParentUnitModel).isNotNull().hasFieldOrPropertyWithValue("uid", "Test Rustic");

		JaloSession.getCurrentSession().setUser(rusticCustomerUser);
		b2bCommerceUnitService.setParentUnit(unitModel, newParentUnitModel);

		JaloSession.getCurrentSession().setUser(rusticAdminUser);
		oldParentUnitModel = b2bCommerceUnitService.getParentUnit(unitModel);
		assertThat(oldParentUnitModel).isNotNull().hasFieldOrPropertyWithValue("uid", "Test Rustic Retail");
	}

	@Test
	public void shouldSetParentUnitAsB2BAdmin()
	{
		JaloSession.getCurrentSession().setUser(rusticAdminUser);
		final B2BUnitModel newParentUnitModel = b2bCommerceUnitService.getUnitForUid("Test Rustic Retail");
		final B2BUnitModel unitModel = b2bCommerceUnitService.getUnitForUid("Test Custom-B Retail");

		assertThat(newParentUnitModel).isNotNull();
		assertThat(unitModel).isNotNull();

		B2BUnitModel oldParentUnitModel = b2bCommerceUnitService.getParentUnit(unitModel);
		assertThat(oldParentUnitModel).isNotNull().hasFieldOrPropertyWithValue("uid", "Test Rustic");

		b2bCommerceUnitService.setParentUnit(unitModel, newParentUnitModel);

		oldParentUnitModel = b2bCommerceUnitService.getParentUnit(unitModel);
		assertThat(oldParentUnitModel).isNotNull().hasFieldOrPropertyWithValue("uid", "Test Rustic Retail");
	}

	@Test
	public void shouldNotGetAllowedParentUnitsWithNullAsUnit()
	{
		assertThatThrownBy(() -> b2bCommerceUnitService.getAllowedParentUnits(null))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Unit can not be null!");
	}

	@Test
	public void shouldNotEnableUnitAsCustomer()
	{
		JaloSession.getCurrentSession().setUser(rusticAdminUser);

		final B2BUnitModel unit = b2bCommerceUnitService.getUnitForUid(CUSTOM_RETAIL);
		assertThat(unit).isNotNull().hasFieldOrPropertyWithValue("active", true);

		b2bCommerceUnitService.disableUnit(CUSTOM_RETAIL);
		assertThat(unit).isNotNull().hasFieldOrPropertyWithValue("active", false);

		JaloSession.getCurrentSession().setUser(rusticCustomerUser);
		// enable
		assertThatThrownBy(() -> b2bCommerceUnitService.enableUnit(CUSTOM_RETAIL))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Parameter unit can not be null");

		assertThat(unit).isNotNull().hasFieldOrPropertyWithValue("active", false);
	}

	@Test
	public void shouldDisableOrEnableUnitAsB2BAdmin()
	{
		JaloSession.getCurrentSession().setUser(rusticAdminUser);

		final B2BUnitModel unit = b2bCommerceUnitService.getUnitForUid(CUSTOM_RETAIL);
		assertThat(unit).isNotNull().hasFieldOrPropertyWithValue("active", true);

		// disable
		b2bCommerceUnitService.disableUnit(CUSTOM_RETAIL);
		assertThat(unit).hasFieldOrPropertyWithValue("active", false);

		// enable
		b2bCommerceUnitService.enableUnit(CUSTOM_RETAIL);
		assertThat(unit).hasFieldOrPropertyWithValue("active", true);
	}

	@Test
	public void shouldNotDisableOrEnableUnitAsCustomerForDifferentUnit()
	{
		JaloSession.getCurrentSession().setUser(rusticCustomerUser);
		final B2BUnitModel unit = b2bCommerceUnitService.getUnitForUid(CUSTOM_RETAIL);
		assertThat(unit).isNotNull().hasFieldOrPropertyWithValue("active", true);

		JaloSession.getCurrentSession().setUser(prontoCustomerUser);
		// disable
		assertThatThrownBy(() -> b2bCommerceUnitService.disableUnit(CUSTOM_RETAIL))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Parameter unit can not be null");

		// enable
		assertThatThrownBy(() -> b2bCommerceUnitService.enableUnit(CUSTOM_RETAIL))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Parameter unit can not be null");
	}

	@Test
	public void shouldNotDisableOrEnableUnitAsB2BAdminForDifferentUnit()
	{
		JaloSession.getCurrentSession().setUser(rusticAdminUser);
		final B2BUnitModel unit = b2bCommerceUnitService.getUnitForUid(CUSTOM_RETAIL);
		assertThat(unit).isNotNull().hasFieldOrPropertyWithValue("active", true);

		JaloSession.getCurrentSession().setUser(prontoAdminUser);
		// disable
		assertThatThrownBy(() -> b2bCommerceUnitService.disableUnit(CUSTOM_RETAIL))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Parameter unit can not be null");

		// enable
		assertThatThrownBy(() -> b2bCommerceUnitService.enableUnit(CUSTOM_RETAIL))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Parameter unit can not be null");
	}
}
