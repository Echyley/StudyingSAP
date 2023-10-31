package de.hybris.platform.hac.facade;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.hac.data.dto.SqlSearchResultData;
import de.hybris.platform.jalo.flexiblesearch.hints.impl.SQLServerQueryHints;
import de.hybris.platform.jalo.flexiblesearch.internal.ReadOnlyConditionsHelper;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.testframework.PropertyConfigSwitcher;
import de.hybris.platform.util.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.annotation.Resource;

import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;

@IntegrationTest
public class HacFlexibleSearchFacadeTest extends ServicelayerBaseTest
{
	private static final String EXAMPLE_FS_QUERY = "SELECT {PK} FROM {Product}";
	private static final String READ_ONLY_DATASOURCE = "F";
	private static final String MASTER_DATASOURCE = "master";

	private final String tablePrefix = Registry.getCurrentTenantNoFallback().getDataSource().getTablePrefix();
	private final String exampleSqlQuery = "SELECT item_t0.PK FROM " + tablePrefix + "products item_t0";

	private final PropertyConfigSwitcher flexibleSearchReadOnlyDataSource = new PropertyConfigSwitcher(
			ReadOnlyConditionsHelper.PARAM_FS_READ_ONLY_DATASOURCE);
	private final PropertyConfigSwitcher maxDOPParameterSwitcher = new PropertyConfigSwitcher(
			SQLServerQueryHints.FLEXIBLESEARCH_SQL_SERVER_MAXDOP_HINT);

	@Resource
	private FlexibleSearchService flexibleSearchService;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private SessionService sessionService;

	@Resource
	private I18NService i18nService;

	@Resource(name = "defaultUserService")
	private UserService userService;

	@Resource
	private ModelService modelService;

	@Resource
	private HacDatabaseFacade hacDatabaseFacade;

	private HacFlexibleSearchFacade hacFlexibleSearchFacade;

	private UserModel employee;

	@Before
	public void setUp()
	{
		flexibleSearchReadOnlyDataSource.switchToValue(READ_ONLY_DATASOURCE);
		maxDOPParameterSwitcher.switchToValue("1");
		hacFlexibleSearchFacade = new HacFlexibleSearchFacade();
		hacFlexibleSearchFacade.setFlexibleSearchService(flexibleSearchService);
		hacFlexibleSearchFacade.setCatalogVersionService(catalogVersionService);
		hacFlexibleSearchFacade.setSessionService(sessionService);
		hacFlexibleSearchFacade.setUserService(userService);
		hacFlexibleSearchFacade.setI18nService(i18nService);
		hacFlexibleSearchFacade.setDatabaseFacade(hacDatabaseFacade);

		final PrincipalGroupModel adminGroup = userService.getAdminUserGroup();

		employee = modelService.create(EmployeeModel.class);
		final String value = UUID.randomUUID().toString();
		employee.setUid(value);
		employee.setName(value);
		employee.setGroups(Sets.newHashSet(adminGroup));
		modelService.saveAll();
	}

	@After
	public void tearDown()
	{
		flexibleSearchReadOnlyDataSource.switchBackToDefault();
		maxDOPParameterSwitcher.switchBackToDefault();
	}

	@Test
	public void shouldExecuteFlexibleSearchQueryOnReadOnlyDataSource()
	{
		// when
		final SqlSearchResultData actual = hacFlexibleSearchFacade.executeFlexibleSearchQuery(EXAMPLE_FS_QUERY,
				employee, new Locale("en"), 200, false, READ_ONLY_DATASOURCE);

		// then
		assertThat(actual.getDataSourceId()).isEqualToIgnoringCase(READ_ONLY_DATASOURCE);
	}

	@Test
	public void shouldExecuteSQLQueryOnReadOnlyDataSource()
	{
		// when
		final SqlSearchResultData actual = hacFlexibleSearchFacade.executeRawSql(exampleSqlQuery, 200, false,
				READ_ONLY_DATASOURCE);

		// then
		assertThat(actual.getDataSourceId()).isEqualToIgnoringCase(READ_ONLY_DATASOURCE);
	}

	@Test
	public void shouldExecuteFlexibleSearchQueryOnMasterDataSource()
	{
		// when
		final SqlSearchResultData actual = hacFlexibleSearchFacade.executeFlexibleSearchQuery(EXAMPLE_FS_QUERY,
				employee, new Locale("en"), 200, false, MASTER_DATASOURCE);

		// then
		assertThat(actual.getDataSourceId()).isEqualToIgnoringCase(MASTER_DATASOURCE);
	}

	@Test
	public void shouldExecuteSQLQueryOnMasterDataSource()
	{
		// when
		final SqlSearchResultData actual = hacFlexibleSearchFacade.executeRawSql(exampleSqlQuery, 200, false, MASTER_DATASOURCE);

		// then
		assertThat(actual.getDataSourceId()).isEqualToIgnoringCase(MASTER_DATASOURCE);
	}

	@Test
	public void shouldReturnFDataSourceAsDefault()
	{
		final String expected = Config.getParameter(ReadOnlyConditionsHelper.PARAM_FS_READ_ONLY_DATASOURCE);

		// when
		final String actual = hacFlexibleSearchFacade.getDefaultDataSource();

		// then
		assertThat(actual).isEqualToIgnoringCase(expected);
	}

	@Test
	public void shouldReturnAllDataSources()
	{
		// given
		final List<String> expected = new ArrayList<>(hacDatabaseFacade.databaseInfo().keySet());

		// when
		final List<String> actual = hacFlexibleSearchFacade.getAllDataSources();

		// then
		Assertions.assertThat(actual).hasSameElementsAs(expected);
	}

	@Test
	public void shouldNotAddMaxdopHintAndExecuteQueryOnReadOnlyDataSource()
	{
		Assume.assumeTrue("SQL server only", Config.isSQLServerUsed());

		// when
		final SqlSearchResultData actual = hacFlexibleSearchFacade.executeFlexibleSearchQuery(EXAMPLE_FS_QUERY,
				employee, new Locale("en"), 200, false, READ_ONLY_DATASOURCE);

		// then
		assertThat(actual.getDataSourceId()).isEqualToIgnoringCase(READ_ONLY_DATASOURCE);
		assertThat(actual.getQuery()).doesNotContain("MAXDOP");
	}

	@Test
	public void shouldAddMaxdopHintAndExecuteQueryOnMasterDataSource()
	{
		Assume.assumeTrue("SQL server only", Config.isSQLServerUsed());

		// when
		final SqlSearchResultData actual = hacFlexibleSearchFacade.executeFlexibleSearchQuery(EXAMPLE_FS_QUERY,
				employee, new Locale("en"), 200, false, MASTER_DATASOURCE);

		// then
		assertThat(actual.getDataSourceId()).isEqualToIgnoringCase(MASTER_DATASOURCE);
		assertThat(actual.getQuery()).contains("MAXDOP");
	}

	@Test
	public void shouldChangeMaxdopHintAndExecuteQueryOnMasterDataSource()
	{
		Assume.assumeTrue("SQL server only", Config.isSQLServerUsed());

		// when
		SqlSearchResultData actual = hacFlexibleSearchFacade.executeFlexibleSearchQuery(EXAMPLE_FS_QUERY,
				employee, new Locale("en"), 200, false, MASTER_DATASOURCE);

		// then
		assertThat(actual.getDataSourceId()).isEqualToIgnoringCase(MASTER_DATASOURCE);
		assertThat(actual.getQuery()).contains("MAXDOP 1");

		// when
		maxDOPParameterSwitcher.switchToValue("2");
		actual = hacFlexibleSearchFacade.executeFlexibleSearchQuery(EXAMPLE_FS_QUERY,
				employee, new Locale("en"), 200, false, MASTER_DATASOURCE);

		// then
		assertThat(actual.getDataSourceId()).isEqualToIgnoringCase(MASTER_DATASOURCE);
		assertThat(actual.getQuery()).contains("MAXDOP 2");
	}
}
