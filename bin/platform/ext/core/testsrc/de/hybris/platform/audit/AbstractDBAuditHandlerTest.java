/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.audit;

import static org.junit.Assert.fail;

import de.hybris.platform.audit.actions.AuditableActionHandler;
import de.hybris.platform.audit.demo.AuditTestConfigManager;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.directpersistence.cache.SLDDataContainer;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.persistence.audit.AuditType;
import de.hybris.platform.persistence.audit.AuditableChange;
import de.hybris.platform.persistence.audit.AuditableOperations;
import de.hybris.platform.persistence.audit.DBAuditHandler;
import de.hybris.platform.persistence.audit.internal.AuditEnablementService;
import de.hybris.platform.persistence.audit.internal.DBAuditEnablementService;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.testframework.PropertyConfigSwitcher;
import de.hybris.platform.testframework.assertions.assertj.TestLogListenerAssert;
import de.hybris.platform.testframework.log.TestLogListener;
import de.hybris.platform.testframework.seed.TestDataCreator;
import de.hybris.platform.tx.Transaction;

import java.util.function.Supplier;

import javax.annotation.Resource;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

public abstract class AbstractDBAuditHandlerTest extends ServicelayerBaseTest implements AuditableTest
{
	private final PropertyConfigSwitcher auditAllTypesEnabledProperty = new PropertyConfigSwitcher("auditing.alltypes.enabled");
	private final PropertyConfigSwitcher disabledDbAuditForUser = new PropertyConfigSwitcher("dbaudit.user.disabled");
	private final PropertyConfigSwitcher disabledDbAuditForEmployee = new PropertyConfigSwitcher("dbaudit.employee.disabled");

	@Resource(name = "auditingEnablementService")
	protected AuditEnablementService auditEnablementService;

	@Resource
	private DBAuditHandler dbAuditHandler;
	@Resource
	private SessionService sessionService;
	@Resource
	private ModelService modelService;
	@Resource
	private DBAuditEnablementService dbAuditEnablementService;
	@Resource
	private TypeService typeService;

	private Supplier<AuditableActionHandler> originalActionHandler;

	private final TestLogListener testLogListener = new TestLogListener();
	private AuditTestConfigManager auditTestConfigManager;
	private TestDataCreator creator;
	private AuditTestHelper auditTestHelper;

	@Before
	public void setUp()
	{
		final AuditableActionHandler testAuditableActionHandler = new TestSlf4jAuditableActionHandler();
		originalActionHandler = AuditableActions.getAuditableActionHandlerFactory();
		AuditableActions.setAuditableActionHandlerFactory(() -> testAuditableActionHandler);

		auditTestConfigManager = new AuditTestConfigManager(auditEnablementService);
		auditTestHelper = new AuditTestHelper();
		auditTestHelper.clearAuditDataForTypes(UserModel._TYPECODE, AddressModel._TYPECODE, TitleModel._TYPECODE);
		createTestData();
		assumeAuditEnabled();

		testLogListener.attach();
	}

	@After
	public void cleanup()
	{
		testLogListener.detach();
		if (originalActionHandler != null)
		{
			AuditableActions.setAuditableActionHandlerFactory(originalActionHandler);
		}
		auditTestConfigManager.resetAuditConfiguration();
		auditTestHelper.clearAuditDataForTypes("User", "Address", "Title");
		auditTestHelper.removeCreatedItems();
		auditAllTypesEnabledProperty.switchBackToDefault();
		disabledDbAuditForUser.switchBackToDefault();
		disabledDbAuditForEmployee.switchBackToDefault();
		dbAuditEnablementService.refreshConfiguredAuditTypes();
	}

	protected void enableGDPRAudit()
	{
		auditAllTypesEnabledProperty.switchToValue("true");
		auditTestConfigManager.enableAuditingForTypes(UserModel._TYPECODE, AddressModel._TYPECODE, TitleModel._TYPECODE);
	}

	protected void disableGDPRAudit()
	{
		auditAllTypesEnabledProperty.switchToValue("false");
		auditTestConfigManager.disableAuditingForTypes(UserModel._TYPECODE, AddressModel._TYPECODE, TitleModel._TYPECODE);
	}

	@Test
	public void testDirectAuditSingle()
	{
		//given
		final Triple<AuditableChange, PK, PK> mockAuditableChange = mockAuditableChange(AuditType.CREATION);

		//when
		dbAuditHandler.auditOperation(mockAuditableChange.getLeft());

		//then
		logAssert("changeType=CREATION").occurrences(1);
		logAssert("PK=" + mockAuditableChange.getMiddle().toString()).occurrences(1);
		logAssert("typePK=" + mockAuditableChange.getRight().toString()).occurrences(1);
		logAssert("message=dummyMessage").occurrences(0);
	}

	@Test
	public void testDirectAuditSingleWithMessage()
	{
		//given
		final Triple<AuditableChange, PK, PK> mockAuditableChange = mockAuditableChange(AuditType.CREATION);

		//when
		dbAuditHandler.auditOperation(mockAuditableChange.getLeft(), "dummyMessage");

		//then
		logAssert("changeType=CREATION").occurrences(1);
		logAssert("PK=" + mockAuditableChange.getMiddle().toString()).occurrences(1);
		logAssert("typePK=" + mockAuditableChange.getRight().toString()).occurrences(1);
		logAssert("message=dummyMessage").occurrences(1);
	}

	@Test
	public void testDirectAuditMultiple()
	{
		//given
		final Triple<AuditableChange, PK, PK> mockAuditableChange1 = mockAuditableChange(AuditType.CREATION);
		final Triple<AuditableChange, PK, PK> mockAuditableChange2 = mockAuditableChange(AuditType.MODIFICATION);

		//when
		dbAuditHandler.auditOperation(mockAuditableChange1.getLeft());
		dbAuditHandler.auditOperation(mockAuditableChange2.getLeft());

		//then
		logAssert().occurrences(2);
		logAssert("message=dummyMessage").occurrences(0);

		logAssert("changeType=CREATION").occurrences(1);
		logAssert("PK=" + mockAuditableChange1.getMiddle().toString()).occurrences(1);
		logAssert("typePK=" + mockAuditableChange1.getRight().toString()).occurrences(1);

		logAssert("changeType=MODIFICATION").occurrences(1);
		logAssert("PK=" + mockAuditableChange2.getMiddle().toString()).occurrences(1);
		logAssert("typePK=" + mockAuditableChange2.getRight().toString()).occurrences(1);
	}

	@Test
	public void testDirectAuditMultipleWithMessage()
	{
		//given
		final Triple<AuditableChange, PK, PK> mockAuditableChange1 = mockAuditableChange(AuditType.CREATION);
		final Triple<AuditableChange, PK, PK> mockAuditableChange2 = mockAuditableChange(AuditType.MODIFICATION);

		//when
		dbAuditHandler.auditOperation(mockAuditableChange1.getLeft(), "dummyMessage");
		dbAuditHandler.auditOperation(mockAuditableChange2.getLeft(), "dummyMessage");

		//then
		logAssert().occurrences(2);
		logAssert("message=dummyMessage").occurrences(2);

		logAssert("changeType=CREATION").occurrences(1);
		logAssert("PK=" + mockAuditableChange1.getMiddle().toString()).occurrences(1);
		logAssert("typePK=" + mockAuditableChange1.getRight().toString()).occurrences(1);

		logAssert("changeType=MODIFICATION").occurrences(1);
		logAssert("PK=" + mockAuditableChange2.getMiddle().toString()).occurrences(1);
		logAssert("typePK=" + mockAuditableChange2.getRight().toString()).occurrences(1);
	}

	private Triple<AuditableChange, PK, PK> mockAuditableChange(final AuditType auditType)
	{
		final AuditableChange auditableChange = Mockito.mock(AuditableChange.class);
		final SLDDataContainer sldDataContainer = Mockito.mock(SLDDataContainer.class);
		final PK pk = PK.createCounterPK(20);
		final PK typePk = PK.createCounterPK(30);

		Mockito.doReturn(sldDataContainer).when(auditableChange).getBefore();
		Mockito.doReturn(Boolean.TRUE).when(auditableChange).isMeaningful();
		Mockito.doReturn(auditType).when(auditableChange).calculateAuditType();

		Mockito.doReturn(pk).when(sldDataContainer).getPk();
		Mockito.doReturn(typePk).when(sldDataContainer).getTypePk();

		return Triple.of(auditableChange, pk, typePk);
	}

	@Test
	public void testMultiple()
	{
		//when
		final UserModel user = creator.createUser(RandomStringUtils.randomAlphabetic(15), "user");
		final UserGroupModel group1 = creator.createUserGroup(RandomStringUtils.randomAlphabetic(15), "group1");
		final UserGroupModel group2 = creator.createUserGroup(RandomStringUtils.randomAlphabetic(15), "group2");
		user.setGroups(Sets.newHashSet(group1, group2));

		modelService.saveAll(user, group1, group2);
		//then
		logAssert("PK=" + user.getPk()).occurrences(2);
		logAssert("PK=" + group1.getPk()).occurrences(2);
		logAssert("PK=" + group2.getPk()).occurrences(2);
	}

	@Test
	public void testMultipleMixed()
	{
		//given
		disabledDbAuditForUser.switchToValue("true");
		dbAuditEnablementService.refreshConfiguredAuditTypes();

		//when
		final UserModel user = creator.createUser(RandomStringUtils.randomAlphabetic(15), "user");
		final UserGroupModel group1 = creator.createUserGroup(RandomStringUtils.randomAlphabetic(15), "group1");
		final UserGroupModel group2 = creator.createUserGroup(RandomStringUtils.randomAlphabetic(15), "group2");
		user.setGroups(Sets.newHashSet(group1, group2));

		modelService.saveAll(user, group1, group2);
		//then
		logAssert("PK=" + user.getPk()).occurrences(0);
		logAssert("PK=" + group1.getPk()).occurrences(2);
		logAssert("PK=" + group2.getPk()).occurrences(2);
	}

	@Test
	public void testTransactionRollback()
	{
		//given
		UserModel user;

		//when
		final Transaction tx = Transaction.current();
		tx.begin();
		try
		{
			user = creator.createUser(RandomStringUtils.randomAlphabetic(15), "user");
		}
		finally
		{
			tx.rollback();
		}

		//then
		logAssert("PK=" + user.getPk()).occurrences(0);
	}

	@Test
	public void testNoTransaction()
	{
		//given
		final UserModel userModel = creator.createUser(RandomStringUtils.randomAlphabetic(15), "user");

		//when
		final User user = modelService.getSource(userModel);
		user.setDescription("test");

		//then
		logAssert("PK=" + userModel.getPk()).occurrences(2);
	}

	@Test
	public void testDisabledDataAudit()
	{
		//given
		disabledDbAuditForUser.switchToValue("true");
		dbAuditEnablementService.refreshConfiguredAuditTypes();

		//when
		final UserModel user = creator.createUser(RandomStringUtils.randomAlphabetic(15), "user");

		//then
		logAssert("PK=" + user.getPk()).occurrences(0);
	}

	@Test
	public void testDisabledDataAuditSuperType()
	{
		//given
		disabledDbAuditForUser.switchToValue("true");
		dbAuditEnablementService.refreshConfiguredAuditTypes();

		//when
		final UserModel employee = creator.createEmployee(RandomStringUtils.randomAlphabetic(15), "employee");

		//then
		logAssert("PK=" + employee.getPk()).occurrences(0);
	}

	@Test
	public void testDisabledDataAuditSubType()
	{
		//given
		disabledDbAuditForEmployee.switchToValue("true");
		dbAuditEnablementService.refreshConfiguredAuditTypes();

		//when
		final UserModel user = creator.createUser(RandomStringUtils.randomAlphabetic(15), "user");

		//then
		logAssert("PK=" + user.getPk()).occurrences(1);
	}

	@Test
	public void shouldNotAuditActionsWhenDisabledInSession()
	{
		final UserModel user = sessionService.executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public UserModel execute()
			{
				dbAuditEnablementService.disableAuditInSession();
				return creator.createUser(RandomStringUtils.randomAlphabetic(15), "user");
			}
		});

		logAssert("PK=" + user.getPk()).occurrences(0);
	}

	@Test
	public void shouldReEnableActionAuditingWhenOnceDisabled()
	{
		final UserModel user = sessionService.executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public UserModel execute()
			{
				dbAuditEnablementService.disableAuditInSession();
				creator.createUser(RandomStringUtils.randomAlphabetic(15), "user");

				dbAuditEnablementService.enableAuditInSession();
				return creator.createUser(RandomStringUtils.randomAlphabetic(15), "user");
			}
		});

		logAssert("PK=" + user.getPk()).occurrences(1);
	}

	@Test
	public void shouldNotFailOnClearAuditAndItemCreationWithTx() throws Exception
	{
		Transaction.current().execute(() -> {
			final UserModel user = creator.createUser(RandomStringUtils.randomAlphabetic(15), "user");
			try
			{
				AuditableOperations.clearCurrentAuditOperationsFor(user.getItemtype());
			}
			catch (final NullPointerException e)
			{
				fail("Null pointer exception not expected");
			}
			return null;
		});
	}

	private TestLogListenerAssert logAssert()
	{
		return logAssert("DB_AUDIT");
	}

	private TestLogListenerAssert logAssert(final String message)
	{
		return TestLogListenerAssert.assertThat(testLogListener).hasLog().withMessageContaining(message)
				.loggedFrom(TestSlf4jAuditableActionHandler.class);
	}

	private void createTestData()
	{
		creator = new TestDataCreator(modelService);

		final UserModel user = auditTestHelper.createItem(() -> creator.createUser("adam", "Adam"));
		final TitleModel title = auditTestHelper.createItem(() -> creator.createTitle("Mr", "Mister"));
		final AddressModel address = auditTestHelper.createItem(() -> creator.createAddress("Somewhere", "Else", user));

		address.setTitle(title);
		modelService.save(address);

		user.setDefaultPaymentAddress(address);
		modelService.save(user);
	}

	public static class TestSlf4jAuditableActionHandler implements AuditableActionHandler
	{
		private static final Logger LOG = LoggerFactory.getLogger(TestSlf4jAuditableActionHandler.class);

		@Override
		public void auditAction(final AuditableActions.Action action)
		{
			LOG.warn("Action {} {}", action.getActionName(), action.getActionAttributes());
		}
	}
}
