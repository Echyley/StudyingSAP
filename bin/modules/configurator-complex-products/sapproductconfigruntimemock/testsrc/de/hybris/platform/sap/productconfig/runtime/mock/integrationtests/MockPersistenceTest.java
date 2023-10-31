/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.mock.integrationtests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.jalo.CoreBasicDataCreator;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.mock.ConfigMock;
import de.hybris.platform.sap.productconfig.runtime.mock.impl.HomeTheaterMockImpl;
import de.hybris.platform.sap.productconfig.runtime.mock.model.ProductConfigurationMockModel;
import de.hybris.platform.sap.productconfig.runtime.mock.persistence.ConfigurationMockPersistenceService;
import de.hybris.platform.sap.productconfig.runtime.mock.util.ConfigurationMockIdGenarator;
import de.hybris.platform.sap.productconfig.runtime.mock.util.impl.DefaultConfigurationMockIdGenarator;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.Collections;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class MockPersistenceTest extends ServicelayerTest
{
	protected static final String TEST_CONFIGURE_SITE = "testConfigureSite";
	private static final String UTF_8 = "utf-8";
	private static final Logger LOG = Logger.getLogger(MockPersistenceTest.class);

	@Resource(name = "flexibleSearchService")
	private FlexibleSearchService flexibleSearchService;

	@Resource(name = "sapProductConfigConfigurationMockPersistenceService")
	private ConfigurationMockPersistenceService persistenceService;;

	private int numberBeforeTest;
	private ConfigModel configModel;
	private ProductConfigurationMockModel persistenceModel;
	private final ConfigMock mock = new HomeTheaterMockImpl();
	private final ConfigurationMockIdGenarator mockIdGenrator = new DefaultConfigurationMockIdGenarator();

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		numberBeforeTest = getNumberOfPersistedMockConfigs();
		// bypassing provider and create mock directly to have a test independent from provider
		configModel = mock.createDefaultConfiguration();
		configModel.setId(mockIdGenrator.generateConfigId("pCode", null));
	}

	@Test
	public void testMockPersistenceWriteSimple() throws ConfigurationEngineException
	{
		persistenceService.writeConfigModel(configModel);

		final int numberAfterTest = getNumberOfPersistedMockConfigs();
		assertTrue("Persisting a mock configuration did not increase number of persisted configs",
				numberAfterTest > numberBeforeTest);
	}

	@Test
	public void testMockPersistenceWriteReadCurrentAndExternalAreIndependent() throws ConfigurationEngineException
	{
		updateRoomSize(configModel, "ROOM_SIZE_SMALL");
		persistenceService.writeConfigModel(configModel);

		updateRoomSize(configModel, "ROOM_SIZE_MEDIUM");
		persistenceService.writeExtConfigModel(configModel);

		final ConfigModel restoredCurrentConfigModel = persistenceService.readConfigModel(configModel.getId());
		final ConfigModel restoredExternalConfigModel = persistenceService.readExtConfigModel(configModel.getId());

		// we should have 3 distinct objects: original config, current sate, external state
		assertNotSame(configModel, restoredCurrentConfigModel);
		assertNotSame(restoredCurrentConfigModel, restoredExternalConfigModel);
		assertNotSame(restoredCurrentConfigModel, configModel);

		// id should remain same
		assertEquals(configModel.getId(), restoredCurrentConfigModel.getId());
		assertEquals(configModel.getId(), restoredExternalConfigModel.getId());

		// room size should remain as persisted
		assertEquals("ROOM_SIZE_SMALL", getRoomSize(restoredCurrentConfigModel));
		assertEquals("ROOM_SIZE_MEDIUM", getRoomSize(restoredExternalConfigModel));
	}

	@Test
	public void testMockPersistenceWriteReadExisting() throws ConfigurationEngineException
	{
		// default config => version 1
		configModel.setVersion("1");
		configModel = persistConfigModel(configModel);
		final ConfigModel defaultConfigModel = persistenceService.readConfigModel(configModel.getId());

		// first Update => version 2
		updateRoomSize(configModel, "ROOM_SIZE_SMALL");
		configModel.setVersion("2");
		configModel = persistConfigModel(configModel);
		final ConfigModel configModelAfterFirstUpdate = persistenceService.readConfigModel(configModel.getId());

		// second Update => version 3
		updateRoomSize(configModel, "ROOM_SIZE_MEDIUM");
		configModel.setVersion("3");
		configModel = persistConfigModel(configModel);
		final ConfigModel configModelAfterSecondUpdate = persistenceService.readConfigModel(configModel.getId());

		// we should have 3 distinct objects: as each read shall return an independent object
		assertNotSame(defaultConfigModel, configModelAfterSecondUpdate);
		assertNotSame(configModelAfterFirstUpdate, configModelAfterSecondUpdate);
		assertNotSame(configModelAfterFirstUpdate, defaultConfigModel);

		// id should remain same
		assertEquals(defaultConfigModel.getId(), configModelAfterFirstUpdate.getId());
		assertEquals(defaultConfigModel.getId(), configModelAfterSecondUpdate.getId());

		// room size and version should remain as persisted
		assertEquals("1", defaultConfigModel.getVersion());
		assertEquals("2", configModelAfterFirstUpdate.getVersion());
		assertEquals("3", configModelAfterSecondUpdate.getVersion());
		assertEquals("ROOM_SIZE_SMALL", getRoomSize(configModelAfterFirstUpdate));
		assertEquals("ROOM_SIZE_MEDIUM", getRoomSize(configModelAfterSecondUpdate));

	}

	protected ConfigModel persistConfigModel(final ConfigModel configModel)
	{
		persistenceService.writeConfigModel(configModel);
		return persistenceService.readConfigModel(configModel.getId());
	}

	protected void updateRoomSize(final ConfigModel configModel, final String size) throws ConfigurationEngineException
	{
		configModel.getRootInstance().getCstic("ROOM_SIZE").setSingleValue(size);
	}

	protected String getRoomSize(final ConfigModel configModel) throws ConfigurationEngineException
	{
		return configModel.getRootInstance().getCstic("ROOM_SIZE").getSingleValue();
	}

	protected int getNumberOfPersistedMockConfigs()
	{
		final String query = "Select {pk} from {ProductConfigurationMock}";
		return flexibleSearchService.search(query).getTotalCount();
	}

	public static void createCoreData() throws Exception
	{
		// copied from ServicelayerTestLogic.createCoredata()
		// we only need this, but do not want to import the impex file (to save testruntime)
		JaloSession.getCurrentSession().setUser(UserManager.getInstance().getAdminEmployee());
		new CoreBasicDataCreator().createEssentialData(Collections.EMPTY_MAP, null);
		//ServicelayerTestLogic.createCoreData();
	}

}
