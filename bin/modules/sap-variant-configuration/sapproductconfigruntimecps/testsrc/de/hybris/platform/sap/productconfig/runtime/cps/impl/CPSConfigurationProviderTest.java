/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.cps.CPSConfigurationChangeAdapter;
import de.hybris.platform.sap.productconfig.runtime.cps.CharonFacade;
import de.hybris.platform.sap.productconfig.runtime.cps.CharonKbDeterminationFacade;
import de.hybris.platform.sap.productconfig.runtime.cps.ConfigurationModificationHandler;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.pricing.PricingHandler;
import de.hybris.platform.sap.productconfig.runtime.cps.strategy.ExternalConfigurationFromVariantStrategy;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.external.Configuration;
import de.hybris.platform.sap.productconfig.runtime.interf.external.impl.ConfigurationImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ConfigurationRetrievalOptions;
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
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 * Unit test for {@link CPSConfigurationProvider}
 */
@UnitTest
public class CPSConfigurationProviderTest
{
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
	private static final String UNIQUE_FIRST_GROUP_ID = "1-$1-ROOT@Group";
	private static final String FIRST_GROUP_NAME = "Group";

	private final CPSConfigurationProvider classUnderTest = new CPSConfigurationProvider();
	private final ConfigModel enrichedConfiguration = new ConfigModelImpl();
	private KBKey kbKey = new KBKeyImpl(PRODUCT_CODE, KB_NAME, KB_LOGSYS, KB_VERSION);
	private ConfigModel genericTestModel;
	private final ConfigModel configModelMl = new ConfigModelImpl();
	private final CsticGroupModel firstGroup = new CsticGroupModelImpl();
	private final UniqueKeyGenerator keyGenerator = new UniqueKeyGeneratorImpl();
	private final CPSConfiguration configuration = new CPSConfiguration();


	@Mock
	private Converter<CPSConfiguration, ConfigModel> configModelConverter;
	@Mock
	private CharonFacade charonFacade;
	@Mock
	private PricingHandler pricingHandler;
	@Mock
	private CharonKbDeterminationFacade charonKbDeterminationFacade;
	@Mock
	private ExternalConfigurationFromVariantStrategy externalConfigurationFromVariantStrategy;
	@Mock
	private CPSConfigurationChangeAdapter configurationChangeAdapter;
	@Mock
	private ConfigurationModificationHandler configurationModificationHandler;
	@Mock
	private CPSDomainOnDemandStrategyImpl domainOnDemandStrategyImpl;

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
		classUnderTest.setConfigModelConverter(configModelConverter);
		classUnderTest.setCharonFacade(charonFacade);
		classUnderTest.setCharonKbDeterminationFacade(charonKbDeterminationFacade);
		classUnderTest.setExternalConfigurationFromVariantStrategy(externalConfigurationFromVariantStrategy);
		classUnderTest.setConfigurationChangeAdapter(configurationChangeAdapter);
		classUnderTest.setConfigurationModificationHandler(configurationModificationHandler);
		classUnderTest.setKeyGenerator(keyGenerator);
		classUnderTest.setDomainOnDemandStrategy(domainOnDemandStrategyImpl);

		genericTestModel = new GenericTestConfigModelImpl(new Properties()).createDefaultConfiguration();
		genericTestModel.getRootInstance().setCsticGroups(Arrays.asList(firstGroup));
		firstGroup.setName("Group");
		firstGroup.setCsticNames(Arrays.asList(CSTIC_NAME));
		genericTestModel.getRootInstance().setCstics(Arrays.asList(cstic));

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

		Mockito.when(charonKbDeterminationFacade.readKbIdForDate(Mockito.anyString(), Mockito.any())).thenReturn(KBID);
		Mockito.when(charonFacade.createConfigurationFromExternal(EXTERNAL_CONFIG, PRODUCT_CODE)).thenReturn(configuration);
		Mockito.when(charonFacade.createDefaultConfiguration(kbKey)).thenReturn(configuration);
		Mockito.when(charonFacade.isReadDomainValuesOnDemand()).thenReturn(false);
		Mockito.when(configModelConverter.convert(configuration)).thenReturn(genericTestModel);
		Mockito.when(externalConfigurationFromVariantStrategy.createExternalConfiguration(PRODUCT_CODE, "0"))
				.thenReturn(externalConfiguration);
		Mockito.when(charonFacade.createConfigurationFromExternal(externalConfiguration, PRODUCT_CODE)).thenReturn(configuration);
		Mockito.when(domainOnDemandStrategyImpl.determineFirstGroup(genericTestModel)).thenReturn(UNIQUE_FIRST_GROUP_ID);

	}

	@Test
	public void testCreateConfigurationWithDomainValues()
	{
		final ConfigModel model = classUnderTest.createDefaultConfiguration(kbKey);
		assertSame(genericTestModel, model);
	}

	@Test
	public void testCreateConfigurationWithoutDomainValues() throws ConfigurationEngineException
	{
		Mockito.when(charonFacade.isReadDomainValuesOnDemand()).thenReturn(true);

		final ConfigModel model = classUnderTest.createDefaultConfiguration(kbKey);
		assertSame(genericTestModel, model);
		verify(domainOnDemandStrategyImpl).processRequiredGroups(genericTestModel, null);
	}

	@Test(expected = IllegalStateException.class)
	public void testCreateConfigurationWithoutDomainValuesRereadIssue() throws ConfigurationEngineException
	{
		Mockito.when(charonFacade.isReadDomainValuesOnDemand()).thenReturn(true);
		Mockito.doThrow(ConfigurationEngineException.class).when(domainOnDemandStrategyImpl).processRequiredGroups(Mockito.any(),
				Mockito.any());
		classUnderTest.createDefaultConfiguration(kbKey);
	}

	@Test(expected = IllegalStateException.class)
	public void testCreateConfigurationFromExternalSourceWithoutDomainValuesRereadIssue() throws ConfigurationEngineException
	{
		Mockito.when(charonFacade.isReadDomainValuesOnDemand()).thenReturn(true);
		Mockito.doThrow(ConfigurationEngineException.class).when(domainOnDemandStrategyImpl).processRequiredGroups(Mockito.any(),
				Mockito.any());
		classUnderTest.createConfigurationFromExternalSource(kbKey, EXTERNAL_CONFIG);
	}

	@Test
	public void testEnrichModelWithGroup() throws ConfigurationEngineException
	{
		classUnderTest.enrichModelWithGroup(genericTestModel, UNIQUE_FIRST_GROUP_ID);
		verify(domainOnDemandStrategyImpl).enrichModelWithGroupForInstance(genericTestModel, ROOT_ID,
				Arrays.asList(FIRST_GROUP_NAME));
	}

	@Test
	public void testEnrichModelWithGroupForceDetermineFirstGroup() throws ConfigurationEngineException
	{
		classUnderTest.enrichModelWithGroup(genericTestModel, null);
		verify(domainOnDemandStrategyImpl).enrichModelWithGroupForInstance(genericTestModel, ROOT_ID,
				Arrays.asList(FIRST_GROUP_NAME));
	}

	@Test
	public void testConfigurationConverter()
	{
		assertEquals(configModelConverter, classUnderTest.getConfigModelConverter());
	}

	@Test
	public void testGetConfiguration() throws ConfigurationEngineException
	{
		classUnderTest.retrieveConfigurationModel(CONFIG_ID);
		verify(charonFacade).getConfiguration(CONFIG_ID);
		verify(configurationModificationHandler, Mockito.times(0)).adjustVariantConditions(any(), any());
		verify(configModelConverter).convert(Mockito.any());
	}

	@Test
	public void testRetrieveConfigurationWithOptions() throws ConfigurationEngineException
	{
		final ConfigurationRetrievalOptions options = new ConfigurationRetrievalOptions();
		options.setDiscountList(new ArrayList<>());
		classUnderTest.retrieveConfigurationModel(CONFIG_ID, options);
		verify(charonFacade).getConfiguration(CONFIG_ID);
		verify(configModelConverter).convert(any());
		verify(configurationModificationHandler).adjustVariantConditions(any(), eq(options));
	}

	@Test
	public void testRetrieveConfigurationWithOptionsDiscountsNull() throws ConfigurationEngineException
	{
		classUnderTest.retrieveConfigurationModel(CONFIG_ID, new ConfigurationRetrievalOptions());
		Mockito.verify(charonFacade).getConfiguration(CONFIG_ID);
		Mockito.verify(configurationModificationHandler, Mockito.times(0)).adjustVariantConditions(Mockito.any(ConfigModel.class),
				Mockito.any());
		Mockito.verify(configModelConverter).convert(Mockito.any());
		verify(configurationModificationHandler, times(0)).adjustVariantConditions(any(), any());
	}

	@Test
	public void testGetExternalConfiguration() throws ConfigurationEngineException
	{
		classUnderTest.retrieveExternalConfiguration(CONFIG_ID);
		Mockito.verify(charonFacade).getExternalConfiguration(CONFIG_ID);
	}

	@Test
	public void testCreateConfigurationFromExternal()
	{
		final KBKey kbKey = new KBKeyImpl(PRODUCT_CODE);
		Mockito.when(charonFacade.createConfigurationFromExternal(EXTERNAL_CONFIG, PRODUCT_CODE)).thenReturn(configuration);
		final ConfigModel model = classUnderTest.createConfigurationFromExternalSource(kbKey, EXTERNAL_CONFIG);
		assertSame(genericTestModel, model);
		Mockito.verify(charonFacade).createConfigurationFromExternal(EXTERNAL_CONFIG, PRODUCT_CODE);
		Mockito.verify(configModelConverter).convert(configuration);
	}

	@Test
	public void testCreateConfigurationFromExternalWithoutDomainValues() throws ConfigurationEngineException
	{
		Mockito.when(charonFacade.isReadDomainValuesOnDemand()).thenReturn(true);

		final KBKey kbKey = new KBKeyImpl(PRODUCT_CODE);

		Mockito.when(charonFacade.createConfigurationFromExternal(EXTERNAL_CONFIG, PRODUCT_CODE)).thenReturn(configuration);
		final ConfigModel model = classUnderTest.createConfigurationFromExternalSource(kbKey, EXTERNAL_CONFIG);
		assertSame(genericTestModel, model);
		Mockito.verify(charonFacade).createConfigurationFromExternal(EXTERNAL_CONFIG, PRODUCT_CODE);
		Mockito.verify(configModelConverter).convert(configuration);
		Mockito.verify(domainOnDemandStrategyImpl).processRequiredGroups(genericTestModel, null);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testCreateConfigurationFromExternal_som()
	{
		final Configuration extConfiguration = new ConfigurationImpl();
		classUnderTest.createConfigurationFromExternalSource(extConfiguration);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testReleaseSession()
	{
		classUnderTest.releaseSession(CONFIG_ID);
	}

	@Test
	public void testReleaseSessionWithVersion()
	{
		classUnderTest.releaseSession(CONFIG_ID, KB_VERSION);
		Mockito.verify(charonFacade).releaseSession(CONFIG_ID, KB_VERSION);
	}

	@Test
	public void testCharonKbDeterminationFacade()
	{
		assertEquals(charonKbDeterminationFacade, classUnderTest.getCharonKbDeterminationFacade());
	}

	@Test
	public void testKbForDateExists()
	{
		assertFalse(classUnderTest.isKbForDateExists(PRODUCT_CODE, NOW));
	}

	@Test
	public void testKbVersionExists()
	{
		final KBKey kbKey = new KBKeyImpl(PRODUCT_CODE, KB_NAME, KB_LOGSYS, KB_VERSION);
		assertFalse(classUnderTest.isKbVersionExists(kbKey));
		verify(charonKbDeterminationFacade).hasKBForKey(kbKey);
	}

	@Test
	public void testKbVersionValid()
	{
		assertFalse(classUnderTest.isKbVersionValid(kbKey));
		verify(charonKbDeterminationFacade).hasValidKBForKey(kbKey);
	}

	@Test
	public void testFindKbId()
	{
		final Integer idFound = classUnderTest.findKbId(kbKey);
		assertEquals(KBID, idFound);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFindKbIdNullKbKey()
	{
		classUnderTest.findKbId(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFindKbIdNullProduct()
	{
		kbKey = new KBKeyImpl(null, KB_NAME, KB_LOGSYS, KB_VERSION);
		classUnderTest.findKbId(kbKey);
	}

	@Test
	public void testGetExternalConfigurationFromVariantStrategy()
	{
		assertEquals(externalConfigurationFromVariantStrategy, classUnderTest.getExternalConfigurationFromVariantStrategy());
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testUpdateConfiguration() throws ConfigurationEngineException
	{
		classUnderTest.updateConfiguration(genericTestModel);
	}

	@Test
	public void testChangeConfiguration() throws ConfigurationEngineException
	{
		final String updatedVersion = "updated version";
		Mockito.when(charonFacade.updateConfiguration(Mockito.any())).thenReturn(updatedVersion);
		assertEquals(updatedVersion, classUnderTest.changeConfiguration(genericTestModel));
	}

	@Test
	public void testChangeConfigurationNoUpdate() throws ConfigurationEngineException
	{
		Mockito.when(charonFacade.updateConfiguration(Mockito.any())).thenReturn(GenericTestConfigModelImpl.VERSION);
		assertEquals(GenericTestConfigModelImpl.VERSION, classUnderTest.changeConfiguration(genericTestModel));
	}

	@Test
	public void testKeyGenerator()
	{
		assertEquals(keyGenerator, classUnderTest.getKeyGenerator());
	}

	@Test
	public void testIsGroupBasedConfigurationReadSupportedTrue()
	{
		assertTrue(classUnderTest.isReadDomainValuesOnDemandSupported());
	}

	@Test
	public void testRetrieveConfigurationWithDesiredGroupForConfiguration() throws ConfigurationEngineException
	{

		final ConfigurationRetrievalOptions options = new ConfigurationRetrievalOptions();
		given(classUnderTest.retrieveConfigurationModel(CONFIG_ID, options)).willReturn(genericTestModel);
		assertEquals(genericTestModel, classUnderTest.retrieveConfigurationModel(CONFIG_ID, null, false, options));
		verify(domainOnDemandStrategyImpl).processRequiredGroups(genericTestModel, null);
	}

	@Test
	public void testRetrieveConfigurationWithDesiredGroupForOverview() throws ConfigurationEngineException
	{
		final ConfigurationRetrievalOptions options = new ConfigurationRetrievalOptions();
		given(classUnderTest.retrieveConfigurationModel(CONFIG_ID, options)).willReturn(genericTestModel);
		assertEquals(genericTestModel, classUnderTest.retrieveConfigurationModel(CONFIG_ID, null, true, options));
		verify(domainOnDemandStrategyImpl, never()).processRequiredGroups(configModelMl, null);
	}

	@Test
	public void testRetrieveConfigurationFromVariant() throws ConfigurationEngineException
	{
		final ConfigModel configModel = classUnderTest.retrieveConfigurationFromVariant(BASE_PRODUCT_CODE, PRODUCT_CODE);
		assertSame(genericTestModel, configModel);
		verify(charonFacade, times(0)).getItemWithGroupDetails(any(), any(), any());
		assertEquals(0, genericTestModel.getGroupsReadCompletely().size());
	}

	@Test
	public void testRetrieveConfigurationFromVariantDomainOnDemand() throws ConfigurationEngineException
	{
		Mockito.when(charonFacade.isReadDomainValuesOnDemand()).thenReturn(true);
		final ConfigModel configModel = classUnderTest.retrieveConfigurationFromVariant(BASE_PRODUCT_CODE, PRODUCT_CODE);
		assertSame(genericTestModel, configModel);
		verify(domainOnDemandStrategyImpl).processRequiredGroups(genericTestModel, null);
	}

	@Test(expected = IllegalStateException.class)
	public void testRetrieveConfigurationFromVariantDomainOnDemandEnrichFails() throws ConfigurationEngineException
	{
		Mockito.when(charonFacade.isReadDomainValuesOnDemand()).thenReturn(true);
		Mockito.doThrow(ConfigurationEngineException.class).when(domainOnDemandStrategyImpl).processRequiredGroups(Mockito.any(),
				Mockito.any());

		classUnderTest.retrieveConfigurationFromVariant(BASE_PRODUCT_CODE, PRODUCT_CODE);
	}



}
