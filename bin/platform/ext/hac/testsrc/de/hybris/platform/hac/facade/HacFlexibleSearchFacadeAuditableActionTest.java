/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.hac.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.audit.AuditableActions;
import de.hybris.platform.audit.AuditableActionsUtil;
import de.hybris.platform.audit.actions.AuditableActionHandler;
import de.hybris.platform.hac.data.dto.SqlSearchResultData;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.search.exceptions.FlexibleSearchException;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.Config;

import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

@IntegrationTest
public class HacFlexibleSearchFacadeAuditableActionTest extends ServicelayerBaseTest
{
	@Resource
	private HacFlexibleSearchFacade hacFlexibleSearchFacade;
	@Resource
	private UserService userService;

	private AuditableActionHandler testAuditableActionHandler;
	private Supplier<AuditableActionHandler> originalActionHandler;

	private static final String FLEXIBLE_SEARCH_SELECT = "SELECT {pk} FROM {User!}";
	private static final String AUDIT_FS_NAME = "Executed search from console";
	private static final String AUDIT_SQL_NAME = "Attempted search from console";
	private static final String AUDIT_FAILED_NAME_SUFFIX = " failed";
	private static final String AUDIT_MAX_COUNT = "maxCount";
	private static final String AUDIT_DATA_SOURCE_ID = "dataSourceId";
	private static final String AUDIT_COMMIT = "commit";
	private static final String AUDIT_QUERY_TYPE = "queryType";
	private static final String AUDIT_RESULT_COUNT = "resultCount";
	private static final String AUDIT_IS_DATA_SOURCE_READ_ONLY = "isDataSourceReadOnly";
	private static final String AUDIT_LOCALE = "locale";
	private static final String AUDIT_USER = "user";
	private static final String AUDIT_MASTER_DATASOURCE = "master";
	private static final String AUDIT_FLEXIBLE_QUERY_VALUE = "Flexible Query";
	private static final String AUDIT_NULL_VALUE = "null";
	private static final String AUDIT_RESULT_EXCEPTION = "exception";
	private static final String AUDIT_RESULT_EXCEPTION_MSG = "exception message";

	@Before
	public void setUp()
	{
		testAuditableActionHandler = mock(AuditableActionHandler.class);
		originalActionHandler = AuditableActionsUtil.getAuditableActionHandlerFactory();
		AuditableActionsUtil.setAuditableActionHandlerFactory(() -> testAuditableActionHandler);
	}

	@After
	public void tearDown()
	{
		AuditableActionsUtil.setAuditableActionHandlerFactory(originalActionHandler);
	}

	@Test
	public void shouldAuditFlexibleSearchExecution()
	{
		//when
		final SqlSearchResultData executeFlexibleSearchQuery = hacFlexibleSearchFacade
				.executeFlexibleSearchQuery(FLEXIBLE_SEARCH_SELECT, userService.getAnonymousUser(),
						Locale.ENGLISH, 100, false, AUDIT_MASTER_DATASOURCE);

		//then
		final Optional<AuditableActions.Action> optionalAction = getAuditableAction(AUDIT_FS_NAME);
		assertThat(optionalAction).isNotEmpty();
		assertThat(optionalAction.get().getActionAttributes()).containsOnly(entry(AUDIT_COMMIT, false),
				entry(AUDIT_MAX_COUNT, 100), entry(AUDIT_DATA_SOURCE_ID, AUDIT_MASTER_DATASOURCE),
				entry(AUDIT_QUERY_TYPE, AUDIT_FLEXIBLE_QUERY_VALUE),
				entry(AUDIT_RESULT_COUNT, executeFlexibleSearchQuery.getResultCount()),
				entry(AUDIT_IS_DATA_SOURCE_READ_ONLY, false), entry(AUDIT_LOCALE, Locale.ENGLISH),
				entry(AUDIT_USER, userService.getAnonymousUser().getPk()));
	}

	@Test
	public void shouldAuditFailedFlexibleSearchExecution()
	{
		//when
		hacFlexibleSearchFacade.executeFlexibleSearchQuery("dummy",
				userService.getAnonymousUser(), Locale.ENGLISH, 100, false, AUDIT_MASTER_DATASOURCE);

		//then
		final Optional<AuditableActions.Action> optionalAction = getAuditableAction(AUDIT_FS_NAME + AUDIT_FAILED_NAME_SUFFIX);
		assertThat(optionalAction).isNotEmpty();
		assertThat(optionalAction.get().getActionAttributes()).containsOnly(entry(AUDIT_COMMIT, false),
				entry(AUDIT_MAX_COUNT, 100), entry(AUDIT_DATA_SOURCE_ID, AUDIT_MASTER_DATASOURCE),
				entry(AUDIT_QUERY_TYPE, AUDIT_FLEXIBLE_QUERY_VALUE),
				entry(AUDIT_IS_DATA_SOURCE_READ_ONLY, false), entry(AUDIT_LOCALE, Locale.ENGLISH),
				entry(AUDIT_USER, userService.getAnonymousUser().getPk()),
				entry(AUDIT_RESULT_EXCEPTION, FlexibleSearchException.class.getName()),
				entry(AUDIT_RESULT_EXCEPTION_MSG, "Missing SELECT clause in 'dummy'"));
	}

	@Test
	public void shouldAuditFlexibleSearchExecutionNullUserLocaleAndCount()
	{
		//when
		final SqlSearchResultData executeFlexibleSearchQuery = hacFlexibleSearchFacade.executeFlexibleSearchQuery(
				FLEXIBLE_SEARCH_SELECT, null, null, null, false, AUDIT_MASTER_DATASOURCE);

		//then
		final Optional<AuditableActions.Action> optionalAction = getAuditableAction(AUDIT_FS_NAME);
		assertThat(optionalAction).isNotEmpty();
		assertThat(optionalAction.get().getActionAttributes()).containsOnly(entry(AUDIT_COMMIT, false),
				entry(AUDIT_MAX_COUNT, AUDIT_NULL_VALUE), entry(AUDIT_DATA_SOURCE_ID, AUDIT_MASTER_DATASOURCE),
				entry(AUDIT_QUERY_TYPE, AUDIT_FLEXIBLE_QUERY_VALUE), entry(AUDIT_RESULT_COUNT, executeFlexibleSearchQuery.getResultCount()),
				entry(AUDIT_IS_DATA_SOURCE_READ_ONLY, false), entry(AUDIT_LOCALE, AUDIT_NULL_VALUE), entry(AUDIT_USER, AUDIT_NULL_VALUE));
	}

	@Test
	public void shouldAuditFlexibleSearchExecutionNullDatasource()
	{
		//when
		final SqlSearchResultData executeFlexibleSearchQuery = hacFlexibleSearchFacade.executeFlexibleSearchQuery(
				FLEXIBLE_SEARCH_SELECT, userService.getAnonymousUser(), Locale.ENGLISH, 100, false, (String) null);

		//then
		final Optional<AuditableActions.Action> optionalAction = getAuditableAction(AUDIT_FS_NAME);
		assertThat(optionalAction).isNotEmpty();
		assertThat(optionalAction.get().getActionAttributes()).containsOnly(entry(AUDIT_COMMIT, false),
				entry(AUDIT_MAX_COUNT, 100), entry(AUDIT_DATA_SOURCE_ID, AUDIT_MASTER_DATASOURCE),
				entry(AUDIT_QUERY_TYPE, AUDIT_FLEXIBLE_QUERY_VALUE), entry(AUDIT_RESULT_COUNT, executeFlexibleSearchQuery.getResultCount()),
				entry(AUDIT_IS_DATA_SOURCE_READ_ONLY, false), entry(AUDIT_LOCALE, Locale.ENGLISH),
				entry(AUDIT_USER, userService.getAnonymousUser().getPk()));
	}

	@Test
	public void shouldAuditRawQuerySearchExecution()
	{
		//when
		hacFlexibleSearchFacade.executeRawSql("SELECT * FROM " + Config.getString("db.tableprefix", "") + "users", 100, false,
				AUDIT_MASTER_DATASOURCE);

		//then
		final Optional<AuditableActions.Action> optionalAction = getAuditableAction(AUDIT_SQL_NAME);
		assertThat(optionalAction).isNotEmpty();
		assertThat(optionalAction.get().getActionAttributes()).containsOnly(entry(AUDIT_COMMIT, false),
				entry(AUDIT_MAX_COUNT, 100), entry(AUDIT_DATA_SOURCE_ID, AUDIT_MASTER_DATASOURCE),
				entry(AUDIT_QUERY_TYPE, "Direct SQL query"));
	}

	@Test
	public void shouldAuditRawQueryFailedSearchExecution()
	{
		//when
		hacFlexibleSearchFacade.executeRawSql("update dummy",
				100, false, AUDIT_MASTER_DATASOURCE);

		//then
		final Optional<AuditableActions.Action> optionalAction = getAuditableAction(AUDIT_SQL_NAME + AUDIT_FAILED_NAME_SUFFIX);
		assertThat(optionalAction).isNotEmpty();
		assertThat(optionalAction.get().getActionAttributes())
			.hasSize(6)
			.contains(entry(AUDIT_COMMIT, false),
				entry(AUDIT_MAX_COUNT, 100), entry(AUDIT_DATA_SOURCE_ID, AUDIT_MASTER_DATASOURCE),
				entry(AUDIT_QUERY_TYPE, "Direct SQL query"))
			.containsKeys(AUDIT_RESULT_EXCEPTION, AUDIT_RESULT_EXCEPTION_MSG);
	}

	private Optional<AuditableActions.Action> getAuditableAction(final String actionName)
	{
		final ArgumentCaptor<AuditableActions.Action> argCaptor = ArgumentCaptor.forClass(AuditableActions.Action.class);
		verify(testAuditableActionHandler, atLeastOnce()).auditAction(argCaptor.capture());

		return argCaptor.getAllValues().stream().filter(action -> actionName.equals(action.getActionName())).findFirst();
	}
}
