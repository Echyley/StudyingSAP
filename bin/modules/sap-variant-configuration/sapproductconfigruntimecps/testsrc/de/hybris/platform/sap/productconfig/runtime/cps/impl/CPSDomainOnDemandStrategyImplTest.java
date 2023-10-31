/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.cps.CharonFacade;
import de.hybris.platform.sap.productconfig.runtime.cps.masterdata.service.ConfigurationMasterDataService;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSItem;
import de.hybris.platform.sap.productconfig.runtime.cps.populator.impl.MasterDataContext;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.ContextualConverter;
import de.hybris.platform.sap.productconfig.runtime.interf.CsticGroup;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.CsticGroupImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticGroupModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticGroupModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticValueModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.GenericTestConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.InstanceModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.services.UniqueKeyGenerator;
import de.hybris.platform.sap.productconfig.runtime.interf.services.impl.UniqueKeyGeneratorImpl;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 * Unit test for {@link CPSConfigurationProvider}
 */
@UnitTest
public class CPSDomainOnDemandStrategyImplTest
{
	private static final String UNIQUE_GROUP_KEY = "1-$1-ROOT@Group";
	protected static final String UNKNOWN = "UNKNOWN";
	private static final String EXTERNAL_CONFIG = "external Config";
	private static final String CONFIG_ID = "configId";
	private static final String GROUP_NAME = "DIMENSIONS";
	private static final String GROUP_NAME_2 = "ELECTRICITY";
	private static final String INST_ID = "3";
	private static final String INST_ID_2 = "2";
	private static final String PRODUCT_CODE = "Product Code";
	private static final Date NOW = new Date();
	private static final Integer KBID = Integer.valueOf(124);
	private final static String KB_NAME = "kb";
	private final static String KB_LOGSYS = "RR5CLNT910";
	private final static String KB_VERSION = "1.0";
	private static final String ROOT_ID = "1";
	private static final String CSTIC_NAME = "Cstic Name";
	private static final String CSTIC_NAME_INVISIBLE = "Cstic Name Invisible";
	private static final String ROOT_GROUP_NAME = "Root Group Name";
	private static final String BASE_PRODUCT_CODE = "BaseProduct";

	private final CPSDomainOnDemandStrategyImpl classUnderTest = new CPSDomainOnDemandStrategyImpl();
	private final ConfigModel enrichedConfiguration = new ConfigModelImpl();
	private final KBKey kbKey = new KBKeyImpl(PRODUCT_CODE, KB_NAME, KB_LOGSYS, KB_VERSION);
	private ConfigModel genericTestModel;
	private final ConfigModel configModelMl = new ConfigModelImpl();
	private final CsticGroupModel firstGroup = new CsticGroupModelImpl();
	private final UniqueKeyGenerator keyGenerator = new UniqueKeyGeneratorImpl();
	private final CPSConfiguration configuration = new CPSConfiguration();
	private final CPSConflictGroupHandlerImpl conflictGroupHandler = new CPSConflictGroupHandlerImpl();

	@Mock
	private Converter<CPSConfiguration, ConfigModel> configModelConverter;
	@Mock
	private CharonFacade charonFacade;
	@Mock
	private ConfigurationMasterDataService configurationMasterDataService;
	@Mock
	private ContextualConverter<CPSItem, InstanceModel, MasterDataContext> itemModelConverter;


	private final InstanceModel rootInstance = new InstanceModelImpl();
	private final InstanceModel subInstance = new InstanceModelImpl();
	private final CsticGroupModel csticGroup = new CsticGroupModelImpl();
	private final InstanceModel rootInstanceEnriched = new InstanceModelImpl();
	private final InstanceModel subInstanceEnriched = new InstanceModelImpl();
	private final CsticGroupModel csticGroupEnriched = new CsticGroupModelImpl();

	private final CsticModel csticEnriched = new CsticModelImpl();
	private final CsticValueModel csticValueEnriched = new CsticValueModelImpl();
	private final CsticModel cstic = new CsticModelImpl();
	private final CsticModel invisibleCstic = new CsticModelImpl();
	private final CsticGroupModel rootGroup = new CsticGroupModelImpl();
	private final CPSExternalConfiguration externalConfiguration = new CPSExternalConfiguration();

	@Before
	public void initialize()
	{
		MockitoAnnotations.openMocks(this);
		classUnderTest.setCharonFacade(charonFacade);
		classUnderTest.setKeyGenerator(keyGenerator);
		classUnderTest.setMasterDataService(configurationMasterDataService);
		classUnderTest.setItemModelConverter(itemModelConverter);
		classUnderTest.setConflictGroupHandler(conflictGroupHandler);

		conflictGroupHandler.setKeyGenerator(keyGenerator);

		genericTestModel = new GenericTestConfigModelImpl(new Properties()).createDefaultConfiguration();
		genericTestModel.getRootInstance().setCsticGroups(Arrays.asList(firstGroup));
		firstGroup.setName("Group");
		firstGroup.setCsticNames(Arrays.asList(CSTIC_NAME));
		genericTestModel.getRootInstance().setCstics(Arrays.asList(cstic));
		Mockito.when(itemModelConverter.convertWithContext(Mockito.any(), Mockito.any()))
				.thenReturn(genericTestModel.getRootInstance());
		configModelMl.setRootInstance(rootInstance);

		rootInstance.setCsticGroups(Arrays.asList(rootGroup, rootGroup));
		rootInstance.setSubInstances(Arrays.asList(subInstance));
		subInstance.setCsticGroups(Arrays.asList(csticGroup));
		subInstance.setCstics(Arrays.asList(cstic, invisibleCstic));
		csticGroup.setName(GROUP_NAME);
		csticGroup.setCsticNames(Arrays.asList(CSTIC_NAME, CSTIC_NAME_INVISIBLE));
		rootGroup.setName(ROOT_GROUP_NAME);
		cstic.setName(CSTIC_NAME);
		cstic.setVisible(true);
		invisibleCstic.setName(CSTIC_NAME_INVISIBLE);
		invisibleCstic.setVisible(false);
		subInstance.setId(INST_ID);
		rootInstance.setId(ROOT_ID);

		enrichedConfiguration.setRootInstance(rootInstanceEnriched);
		rootInstanceEnriched.setSubInstances(Arrays.asList(subInstanceEnriched));
		subInstanceEnriched.setCsticGroups(Arrays.asList(csticGroupEnriched));
		subInstanceEnriched.setCstics(Arrays.asList(csticEnriched));
		csticGroupEnriched.setName(GROUP_NAME);
		csticGroupEnriched.setCsticNames(Arrays.asList(CSTIC_NAME));
		csticEnriched.setName(CSTIC_NAME);
		csticEnriched.setAssignableValues(Arrays.asList(csticValueEnriched));

		subInstanceEnriched.setId(INST_ID);
		rootInstanceEnriched.setId(ROOT_ID);

		Mockito.when(charonFacade.createDefaultConfiguration(kbKey)).thenReturn(configuration);
		Mockito.when(charonFacade.isReadDomainValuesOnDemand()).thenReturn(false);
		Mockito.when(configModelConverter.convert(configuration)).thenReturn(genericTestModel);

		Mockito.when(charonFacade.createConfigurationFromExternal(externalConfiguration, PRODUCT_CODE)).thenReturn(configuration);

	}


	@Test
	public void testDetermineFirstGroup()
	{
		final String firstGroupId = classUnderTest.determineFirstGroup(configModelMl);
		assertNotNull(firstGroupId);
		assertTrue(firstGroupId.contains(GROUP_NAME));
	}

	@Test(expected = NullPointerException.class)
	public void testDetermineFirstGroupNullInput()
	{
		classUnderTest.determineFirstGroup(null);
	}

	@Test(expected = NullPointerException.class)
	public void testDetermineFirstGroupNoRootInstance()
	{
		classUnderTest.determineFirstGroup(new ConfigModelImpl());
	}

	@Test(expected = IllegalStateException.class)
	public void testDetermineFirstGroupNoGroups()
	{
		final InstanceModel rootInstance = new InstanceModelImpl();
		final ConfigModel configModel = new ConfigModelImpl();
		configModel.setRootInstance(rootInstance);
		classUnderTest.determineFirstGroup(configModel);
	}

	@Test(expected = IllegalStateException.class)
	public void testDetermineFirstGroupEmptyGroupList()
	{
		final InstanceModel rootInstance = new InstanceModelImpl();
		rootInstance.setCsticGroups(new ArrayList<>());
		final ConfigModel configModel = new ConfigModelImpl();
		configModel.setRootInstance(rootInstance);
		classUnderTest.determineFirstGroup(configModel);
	}

	@Test
	public void testMergeGroupDetails()
	{
		assertTrue(CollectionUtils.isEmpty(configModelMl.getRootInstance().getSubInstances().get(0)//
				.getCstics()//
				.get(0)//
				.getAssignableValues()));

		classUnderTest.mergeGroupDetails(configModelMl, enrichedConfiguration.getRootInstance(), INST_ID, GROUP_NAME);
		//check that after model has been enriched, domain values are present
		assertEquals(csticValueEnriched, configModelMl.getRootInstance().getSubInstances().get(0)//
				.getCstics()//
				.get(0)//
				.getAssignableValues()//
				.get(0));
	}

	@Test(expected = IllegalStateException.class)
	public void testMergeGroupDetailsGroupNotFound()
	{
		classUnderTest.mergeGroupDetails(configModelMl, enrichedConfiguration.getRootInstance(), INST_ID, UNKNOWN);

	}

	@Test
	public void testMergeDetailsForCstics()
	{
		classUnderTest.mergeDetailsForCstics(configModelMl, enrichedConfiguration.getRootInstance(), INST_ID,
				Arrays.asList(CSTIC_NAME));
		assertEquals(csticValueEnriched, configModelMl.getRootInstance().getSubInstances().get(0)//
				.getCstics()//
				.get(0)//
				.getAssignableValues()//
				.get(0));
	}

	@Test(expected = NullPointerException.class)
	public void testMergeDetailsForCsticsNoCstics()
	{
		classUnderTest.mergeDetailsForCstics(configModelMl, enrichedConfiguration.getRootInstance(), INST_ID, null);
	}



	@Test(expected = IllegalStateException.class)
	public void testMergeDetailsForCsticsInstanceNotFound()
	{
		classUnderTest.mergeDetailsForCstics(configModelMl, enrichedConfiguration.getRootInstance(), UNKNOWN,
				Arrays.asList(CSTIC_NAME));
	}

	@Test
	public void testFindGroup()
	{
		final Optional<CsticGroupModel> group = classUnderTest.findGroup(rootInstance, INST_ID, GROUP_NAME);
		assertTrue(group.isPresent());
	}

	@Test
	public void testFindGroupNoMatchingInstance()
	{
		final Optional<CsticGroupModel> group = classUnderTest.findGroup(rootInstance, UNKNOWN, GROUP_NAME);
		assertFalse(group.isPresent());
	}

	@Test
	public void testFindGroupNoMatchingGroup()
	{
		final Optional<CsticGroupModel> group = classUnderTest.findGroup(rootInstance, INST_ID, UNKNOWN);
		assertFalse(group.isPresent());
	}

	@Test(expected = NullPointerException.class)
	public void testFindGroupInstanceNull()
	{
		classUnderTest.findGroup(null, INST_ID, GROUP_NAME);
	}

	@Test
	public void testFindInstance()
	{
		final Optional<InstanceModel> instance = classUnderTest.findInstance(rootInstance, INST_ID);
		assertTrue(instance.isPresent());
	}

	@Test(expected = NullPointerException.class)
	public void testFindInstanceInstanceNull()
	{
		classUnderTest.findInstance(null, INST_ID);

	}

	@Test
	public void testFindInstanceNoMatchingInstance()
	{
		final Optional<InstanceModel> instance = classUnderTest.findInstance(rootInstance, UNKNOWN);
		assertFalse(instance.isPresent());
	}

	@Test
	public void testItemModelConverter()
	{
		assertEquals(itemModelConverter, classUnderTest.getItemModelConverter());
	}

	@Test
	public void testMasterDataService()
	{
		assertEquals(configurationMasterDataService, classUnderTest.getMasterDataService());
	}

	public void testHasGroupVisibleCstic()
	{
		assertTrue(classUnderTest.hasGroupVisibleCstic(subInstance, csticGroup));
		csticGroup.setCsticNames(Arrays.asList(CSTIC_NAME_INVISIBLE, CSTIC_NAME));
		assertTrue(classUnderTest.hasGroupVisibleCstic(subInstance, csticGroup));
	}

	@Test
	public void testHasGroupVisibleCsticFalse()
	{
		csticGroup.setCsticNames(Arrays.asList(CSTIC_NAME_INVISIBLE, CSTIC_NAME_INVISIBLE));
		assertFalse(classUnderTest.hasGroupVisibleCstic(subInstance, csticGroup));
	}

	@Test
	public void testHasGroupVisibleCsticEmpty()
	{
		csticGroup.setCsticNames(new ArrayList<>());
		assertFalse(classUnderTest.hasGroupVisibleCstic(subInstance, csticGroup));
	}

	@Test
	public void testFindFirstGroupWithVisibleCstic()
	{
		rootInstance.setCsticGroups(Arrays.asList(rootGroup, csticGroup));
		rootInstance.setCstics(Arrays.asList(cstic, invisibleCstic));
		rootGroup.setCsticNames(Arrays.asList(CSTIC_NAME_INVISIBLE, CSTIC_NAME));
		final Pair<CsticGroupModel, InstanceModel> groupAndInstance = classUnderTest.findFirstGroupWithVisibleCstic(rootInstance);
		assertEquals(ROOT_GROUP_NAME, groupAndInstance.getLeft().getName());
	}

	@Test
	public void testFindFirstGroupWithVisibleCsticNone()
	{
		rootGroup.setCsticNames(Arrays.asList(CSTIC_NAME_INVISIBLE, CSTIC_NAME_INVISIBLE));
		csticGroup.setCsticNames(Arrays.asList(CSTIC_NAME_INVISIBLE, CSTIC_NAME_INVISIBLE));
		rootInstance.setCstics(Arrays.asList(invisibleCstic));
		final Pair<CsticGroupModel, InstanceModel> groupAndInstance = classUnderTest.findFirstGroupWithVisibleCstic(rootInstance);
		assertNull(groupAndInstance);
	}

	@Test
	public void testFindFirstGroupWithVisibleCsticFirstGroupInvisible()
	{
		csticGroup.setCsticNames(Arrays.asList(CSTIC_NAME_INVISIBLE, CSTIC_NAME_INVISIBLE));
		rootGroup.setCsticNames(Arrays.asList(CSTIC_NAME, CSTIC_NAME_INVISIBLE));
		rootInstance.setCsticGroups(Arrays.asList(csticGroup, rootGroup));
		rootInstance.setCstics(Arrays.asList(cstic, invisibleCstic));
		final Pair<CsticGroupModel, InstanceModel> groupAndInstance = classUnderTest.findFirstGroupWithVisibleCstic(rootInstance);
		assertEquals(ROOT_GROUP_NAME, groupAndInstance.getLeft().getName());
	}

	@Test
	public void testFindFirstGroupWithVisibleCsticOnSubInstance()
	{
		final Pair<CsticGroupModel, InstanceModel> groupAndInstance = classUnderTest.findFirstGroupWithVisibleCstic(rootInstance);
		assertEquals(GROUP_NAME, groupAndInstance.getLeft().getName());
	}

	@Test
	public void testFindFirstGroupWithVisibleCsticOnSubInstanceSecondGroup()
	{
		final CsticGroupModel csticGroup2 = new CsticGroupModelImpl();
		csticGroup2.setName(GROUP_NAME_2);
		csticGroup2.setCsticNames(Arrays.asList(CSTIC_NAME_INVISIBLE, CSTIC_NAME));
		csticGroup.setCsticNames(Arrays.asList(CSTIC_NAME_INVISIBLE, CSTIC_NAME_INVISIBLE));
		subInstance.setCsticGroups(Arrays.asList(csticGroup, csticGroup2));
		subInstance.setCstics(Arrays.asList(cstic, invisibleCstic));

		final Pair<CsticGroupModel, InstanceModel> groupAndInstance = classUnderTest.findFirstGroupWithVisibleCstic(rootInstance);
		assertEquals(GROUP_NAME_2, groupAndInstance.getLeft().getName());
	}

	@Test
	public void testFindFirstGroupWithVisibleCsticOnSecondSubInstance()
	{
		csticGroup.setCsticNames(Arrays.asList(CSTIC_NAME_INVISIBLE, CSTIC_NAME_INVISIBLE));
		final CsticGroupModel csticGroup2 = new CsticGroupModelImpl();
		csticGroup2.setName(GROUP_NAME_2);
		csticGroup2.setCsticNames(Arrays.asList(CSTIC_NAME_INVISIBLE, CSTIC_NAME));
		final InstanceModel subInstance2 = new InstanceModelImpl();
		subInstance2.setId(INST_ID_2);
		subInstance2.setCsticGroups(Arrays.asList(csticGroup2));
		subInstance2.setCstics(Arrays.asList(invisibleCstic, cstic));
		rootInstance.setSubInstances(Arrays.asList(subInstance, subInstance2));
		final Pair<CsticGroupModel, InstanceModel> groupAndInstance = classUnderTest.findFirstGroupWithVisibleCstic(rootInstance);
		assertEquals(GROUP_NAME_2, groupAndInstance.getLeft().getName());
	}

	@Test
	public void testFindFirstGroupWithVisibleCsticOnSubSubInstance()
	{
		csticGroup.setCsticNames(Arrays.asList(CSTIC_NAME_INVISIBLE, CSTIC_NAME_INVISIBLE));
		final CsticGroupModel csticGroup2 = new CsticGroupModelImpl();
		csticGroup2.setName(GROUP_NAME_2);
		csticGroup2.setCsticNames(Arrays.asList(CSTIC_NAME_INVISIBLE, CSTIC_NAME));
		final InstanceModel subInstance2 = new InstanceModelImpl();
		subInstance2.setId(INST_ID_2);
		subInstance2.setCsticGroups(Arrays.asList(csticGroup2));
		subInstance2.setCstics(Arrays.asList(invisibleCstic, cstic));
		subInstance.setSubInstances(Arrays.asList(subInstance2));
		final Pair<CsticGroupModel, InstanceModel> groupAndInstance = classUnderTest.findFirstGroupWithVisibleCstic(rootInstance);
		assertEquals(GROUP_NAME_2, groupAndInstance.getLeft().getName());
	}

	@Test
	public void testEnrichModelWithGroup() throws ConfigurationEngineException
	{
		final String instanceId = "1";
		final String groupName = "Group";
		assertEquals(0, genericTestModel.getGroupsReadCompletely().size());

		classUnderTest.enrichModelWithGroupForInstance(genericTestModel, instanceId, Arrays.asList(groupName));
		verify(charonFacade, times(1)).getItemWithGroupDetails(genericTestModel.getId(), instanceId, Arrays.asList(groupName));
		assertEquals(1, genericTestModel.getGroupsReadCompletely().size());
	}



	@Test
	public void testProcessRequiredGroupsCurrentGroupIsNotConflictGroup() throws ConfigurationEngineException
	{
		final CPSConflictGroupHandlerImpl conflictGroupHandlerSpy = spy(conflictGroupHandler);
		classUnderTest.setConflictGroupHandler(conflictGroupHandlerSpy);

		final ConfigModel configModel = prepareConfigModel(false);
		final Map<String, List<String>> conflictingGroups = prepareConflictingGroups();
		final String currentGroup = "InstanceId3-IstanceName3@GroupNameX";

		Mockito.when(itemModelConverter.convertWithContext(Mockito.any(), Mockito.any())).thenReturn(configModel.getRootInstance());

		willReturn(conflictingGroups).given(conflictGroupHandlerSpy).retrieveNotCachedUniqueGroupIds(configModel);

		classUnderTest.processRequiredGroups(configModel, currentGroup);

		verify(charonFacade, times(1)).getItemWithGroupDetails(CONFIG_ID, "InstanceId1",
				Arrays.asList("GroupName11", "GroupName12"));
		verify(charonFacade, times(1)).getItemWithGroupDetails(CONFIG_ID, "InstanceId2",
				Arrays.asList("GroupName21", "GroupName22"));
		verify(charonFacade, times(1)).getItemWithGroupDetails(CONFIG_ID, "InstanceId3", Arrays.asList("GroupNameX"));
	}

	@Test
	public void testProcessRequiredGroupsCurrentGroupIsConflictGroup() throws ConfigurationEngineException
	{
		final CPSConflictGroupHandlerImpl conflictGroupHandlerSpy = spy(conflictGroupHandler);
		classUnderTest.setConflictGroupHandler(conflictGroupHandlerSpy);

		final ConfigModel configModel = prepareConfigModel(true);
		final Map<String, List<String>> conflictingGroups = prepareConflictingGroups();
		final String currentGroup = "InstanceId2-IstanceName2@GroupNameX";

		Mockito.when(itemModelConverter.convertWithContext(Mockito.any(), Mockito.any())).thenReturn(configModel.getRootInstance());

		willReturn(conflictingGroups).given(conflictGroupHandlerSpy).retrieveNotCachedUniqueGroupIds(configModel);

		classUnderTest.processRequiredGroups(configModel, currentGroup);

		verify(charonFacade, times(1)).getItemWithGroupDetails(CONFIG_ID, "InstanceId1",
				Arrays.asList("GroupName11", "GroupName12"));
		verify(charonFacade, times(1)).getItemWithGroupDetails(CONFIG_ID, "InstanceId2",
				Arrays.asList("GroupName21", "GroupName22", "GroupNameX"));
		verify(charonFacade, never()).getItemWithGroupDetails(CONFIG_ID, "InstanceId3", Arrays.asList("GroupNameX"));
	}

	@Test
	public void testProcessRequiredGroupsInputNull() throws ConfigurationEngineException
	{

		classUnderTest.setConflictGroupHandler(conflictGroupHandler);
		final ConfigModel configModel = prepareConfigModel(true);

		Mockito.when(itemModelConverter.convertWithContext(Mockito.any(), Mockito.any()))
				.thenReturn(configModel.getRootInstance().getSubInstances().get(0));

		classUnderTest.processRequiredGroups(configModel, null);

		verify(charonFacade, times(1)).getItemWithGroupDetails(CONFIG_ID, "InstanceId1", Arrays.asList("GroupName11"));
		verify(charonFacade, never()).getItemWithGroupDetails(CONFIG_ID, "InstanceId3", Arrays.asList("GroupNameX"));
	}

	@Test
	public void testProcessRequiredGroupsCurrentGroupNotPartOfConflict() throws ConfigurationEngineException
	{
		final CPSConflictGroupHandlerImpl conflictGroupHandlerSpy = spy(conflictGroupHandler);
		classUnderTest.setConflictGroupHandler(conflictGroupHandlerSpy);
		final ConfigModel configModel = prepareConfigModelSl();
		willReturn(prepareConflictingGroupsSl()).given(conflictGroupHandlerSpy).retrieveNotCachedUniqueGroupIds(configModel);

		Mockito.when(itemModelConverter.convertWithContext(Mockito.any(), Mockito.any())).thenReturn(configModel.getRootInstance());
		final InstanceModel rootInstance = configModel.getRootInstance();
		final CsticGroupModel currentGroupModel = rootInstance.getCsticGroups().get(1);
		final CsticGroup currentGroup = new CsticGroupImpl();
		currentGroup.setName(currentGroupModel.getName());

		classUnderTest.processRequiredGroups(configModel, keyGenerator.generateGroupIdForGroup(rootInstance, currentGroup));

		//We verify that the current group along with the conflict group is requested for details
		verify(charonFacade, times(1)).getItemWithGroupDetails(CONFIG_ID, "root", Arrays.asList("GroupName11", "GroupName12"));
	}

	@Test
	public void testProcessRequiredGroupsCurrentGroupPartOfConflict() throws ConfigurationEngineException
	{
		final CPSConflictGroupHandlerImpl conflictGroupHandlerSpy = spy(conflictGroupHandler);
		classUnderTest.setConflictGroupHandler(conflictGroupHandlerSpy);
		final ConfigModel configModel = prepareConfigModelSl();
		willReturn(prepareConflictingGroupsSl()).given(conflictGroupHandlerSpy).retrieveNotCachedUniqueGroupIds(configModel);

		Mockito.when(itemModelConverter.convertWithContext(Mockito.any(), Mockito.any())).thenReturn(configModel.getRootInstance());
		final InstanceModel rootInstance = configModel.getRootInstance();
		final CsticGroupModel currentGroupModel = rootInstance.getCsticGroups().get(0);
		final CsticGroup currentGroup = new CsticGroupImpl();
		currentGroup.setName(currentGroupModel.getName());

		classUnderTest.processRequiredGroups(configModel, keyGenerator.generateGroupIdForGroup(rootInstance, currentGroup));

		//We verify that the current group along with the conflict group is requested for details
		verify(charonFacade, times(1)).getItemWithGroupDetails(CONFIG_ID, "root", Arrays.asList("GroupName11"));
	}



	protected Map<String, List<String>> prepareConflictingGroups()
	{
		final Map<String, List<String>> conflictingGroups = new HashMap<>();
		conflictingGroups.put("InstanceId1", Arrays.asList("GroupName11", "GroupName12"));
		conflictingGroups.put("InstanceId2", Arrays.asList("GroupName21", "GroupName22"));
		return conflictingGroups;
	}

	protected Map<String, List<String>> prepareConflictingGroupsSl()
	{
		final Map<String, List<String>> conflictingGroups = new HashMap<>();
		conflictingGroups.put("root", Arrays.asList("GroupName11"));
		return conflictingGroups;
	}

	protected ConfigModel prepareConfigModel(final boolean currentGroupIsConflictGroup)
	{
		final ConfigModel configModel = new ConfigModelImpl();
		configModel.setId(CONFIG_ID);
		final InstanceModel root = new InstanceModelImpl();
		root.setId("root");
		configModel.setRootInstance(root);
		final InstanceModel si1 = new InstanceModelImpl();
		si1.setId("InstanceId1");
		final InstanceModel si2 = new InstanceModelImpl();
		si2.setId("InstanceId2");
		final InstanceModel si3 = new InstanceModelImpl();
		si3.setId("InstanceId3");
		root.setSubInstances(Arrays.asList(si1, si2, si3));
		final CsticGroupModel group11 = new CsticGroupModelImpl();
		final CsticGroupModel group12 = new CsticGroupModelImpl();
		final CsticGroupModel group21 = new CsticGroupModelImpl();
		final CsticGroupModel group22 = new CsticGroupModelImpl();
		final CsticGroupModel groupX = new CsticGroupModelImpl();
		group11.setName("GroupName11");
		group12.setName("GroupName12");
		group21.setName("GroupName21");
		group22.setName("GroupName22");
		groupX.setName("GroupNameX");
		si1.setCsticGroups(Arrays.asList(group11, group12));
		if (currentGroupIsConflictGroup)
		{
			si2.setCsticGroups(Arrays.asList(group21, group22, groupX));
		}
		else
		{
			si2.setCsticGroups(Arrays.asList(group21, group22));
			si3.setCsticGroups(Arrays.asList(groupX));
		}

		si1.addCstic(cstic);
		group11.setCsticNames(Arrays.asList(cstic.getName()));
		return configModel;
	}

	protected ConfigModel prepareConfigModelSl()
	{
		final ConfigModel configModel = new ConfigModelImpl();
		configModel.setId(CONFIG_ID);
		final InstanceModel root = new InstanceModelImpl();
		root.setId("root");
		configModel.setRootInstance(root);

		final CsticGroupModel group11 = new CsticGroupModelImpl();
		final CsticGroupModel group12 = new CsticGroupModelImpl();

		group11.setName("GroupName11");
		group12.setName("GroupName12");

		root.setCsticGroups(Arrays.asList(group11, group12));
		return configModel;
	}


}
