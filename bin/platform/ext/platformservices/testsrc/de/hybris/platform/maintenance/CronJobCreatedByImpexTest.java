/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.maintenance;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.type.SearchRestrictionModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.testframework.PropertyConfigSwitcher;

import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@IntegrationTest
public class CronJobCreatedByImpexTest extends ServicelayerTest
{
	private static final String TEST = "test";
	private static final String EMPLOYEE = "employee";
	private static final String TEST_GROUP = "testGroup";
	private static final String SEARCH_RESTRICTION_QUERY = String.format("{name} not in ('%s')", TEST);
	private static final String REMOVE_TEST_USER_CRON_JOB = "removeTestUserCronJob";
	private static final String FIND_TEST_USER_QUERY = String.format("SELECT {%s} FROM {%s} WHERE {%s}='%s'", UserModel.PK,
			UserModel._TYPECODE, UserModel.NAME, TEST);

	private final PropertyConfigSwitcher filteredCtxAttributesInImportMode = new PropertyConfigSwitcher(
			"cronjob.ctx.filtered.attributes.in.impex.import.mode");

	@Resource
	private ModelService modelService;

	@Resource
	private TypeService typeService;

	@Resource
	private CronJobService cronJobService;

	@Resource
	private FlexibleSearchService flexibleSearchService;

	private UserModel employeeUserWithRestrictions;

	@Before
	public void setUp() throws ImpExException
	{
		createTestUserToBeRemovedByCronJob();
		employeeUserWithRestrictions = createEmployeeUserToRunCronJob();
	}

	@After
	public void tearDown()
	{
		filteredCtxAttributesInImportMode.switchBackToDefault();
	}

	@Test
	public void shouldNotIgnoreRestrictionAndNotRemoveUserIfThereIsFiltrationOfRedundantSessionCtxAttributes()
			throws ImpExException
	{
		/*
		When importing CronJob via Impex, there are added attributes (needed for import purposes) to SessionContext.
		For instance DefaultImportProcessor#adjustSessionSettings adds attribute which disables search restrictions :
		"disableRestrictions:true". Afterwards all SessionContext attributes are added to CronJob as a sessionContextValues (in
		CronJob#adjustAttributes) which can cause issues when running this CronJob, for instance disabled restrictions.
		To prevent situation where sessionContextValues holds redundant attributes (added in import mode), there has been
		introduced method to filter SessionContext attributes if "import.mode=true" : CronJob#filterSessionCtxAttributesAddedDuringImportCronJobByImpex
		 */

		//given
		filteredCtxAttributesInImportMode.switchToValue(getRedundantSessionContextAttributeAddedDuringImport());
		importCsv("/platformservices/test/remove-test-user-cronjob.impex", "UTF-8");
		final CronJobModel removeTestUserCronJob = getImportedCronJobWithSetSessionUser(employeeUserWithRestrictions);

		assertThat(testUserExists()).isTrue();

		//when
		cronJobService.performCronJob(removeTestUserCronJob, true);

		//then
		assertThat(testUserExists()).isTrue();
	}

	@Test
	public void shouldIgnoreRestrictionAndRemoveUserIfThereIsNoFiltrationRedundantSessionCtxAttributes() throws ImpExException
	{
		//given
		filteredCtxAttributesInImportMode.switchToValue(StringUtils.EMPTY);
		importCsv("/platformservices/test/remove-test-user-cronjob.impex", "UTF-8");
		final CronJobModel removeTestUserCronJob = getImportedCronJobWithSetSessionUser(employeeUserWithRestrictions);

		assertThat(testUserExists()).isTrue();

		//when
		cronJobService.performCronJob(removeTestUserCronJob, true);

		//then
		assertThat(testUserExists()).isFalse();
	}

	private CronJobModel getImportedCronJobWithSetSessionUser(final UserModel sessionUser)
	{
		final CronJobModel cronJob = cronJobService.getCronJob(REMOVE_TEST_USER_CRON_JOB);
		cronJob.setSessionUser(sessionUser);
		modelService.save(cronJob);

		return cronJob;
	}

	private UserModel createEmployeeUserToRunCronJob()
	{
		final UserModel employeeUser = createUser(EMPLOYEE);
		employeeUser.setGroups(Set.of(createGroupWithTestUserRestriction()));
		modelService.save(employeeUser);

		return employeeUser;
	}

	private void createTestUserToBeRemovedByCronJob()
	{
		createUser(TEST);
	}

	private UserModel createUser(final String uid)
	{
		final UserModel user = modelService.create(UserModel.class);
		user.setUid(uid);
		user.setName(uid);
		modelService.save(user);

		return user;
	}

	private UserGroupModel createGroupWithTestUserRestriction()
	{
		final UserGroupModel userGroup = modelService.create(UserGroupModel.class);
		userGroup.setUid(TEST_GROUP);
		userGroup.setName(TEST_GROUP);
		modelService.save(userGroup);

		final SearchRestrictionModel searchRestriction = modelService.create(SearchRestrictionModel.class);
		searchRestriction.setCode(TEST);
		searchRestriction.setPrincipal(userGroup);
		searchRestriction.setQuery(SEARCH_RESTRICTION_QUERY);
		searchRestriction.setRestrictedType(typeService.getComposedTypeForClass(UserModel.class));
		searchRestriction.setActive(true);
		searchRestriction.setGenerate(true);

		modelService.save(searchRestriction);

		return userGroup;
	}

	private boolean testUserExists()
	{
		return flexibleSearchService.search(FIND_TEST_USER_QUERY).getResult().size() != 0;
	}

	private String getRedundantSessionContextAttributeAddedDuringImport()
	{
		return "disableRestrictions,disableRestrictionGroupInheritance,use.fast.algorithms,import.mode,disable.attribute.check," +
				"disable.interceptor.beans,disable.interceptor.types,disable.UniqueAttributesValidator.for.types,currentCronJob," +
				"currentJob,core.types.creation.initial,save.from.service.layer,ctx.enable.fs.on.read-replica,impex.creation";
	}
}
