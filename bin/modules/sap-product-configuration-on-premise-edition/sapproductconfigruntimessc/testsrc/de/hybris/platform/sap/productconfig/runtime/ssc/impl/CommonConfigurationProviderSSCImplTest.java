/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.ssc.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNotNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.Registry;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ConfigModelFactoryImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.VariantConditionModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticValueModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.InstanceModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.services.ConfigurationProductUtil;
import de.hybris.platform.sap.productconfig.runtime.ssc.ConfigurationContextAndPricingWrapper;
import de.hybris.platform.sap.productconfig.runtime.ssc.ConfigurationSessionContainer;
import de.hybris.platform.sap.productconfig.runtime.ssc.SolvableConflictAdapter;
import de.hybris.platform.sap.productconfig.runtime.ssc.wrapper.KBOCacheWrapper;
import de.hybris.platform.sap.sapmodel.model.ERPVariantProductModel;
import de.hybris.platform.util.Config;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.sap.custdev.projects.fbs.slc.cfg.IConfigSession;
import com.sap.custdev.projects.fbs.slc.cfg.client.IConfigContainer;
import com.sap.custdev.projects.fbs.slc.cfg.client.IConfigInfoData;
import com.sap.custdev.projects.fbs.slc.cfg.client.IVariantCondKeyData;
import com.sap.custdev.projects.fbs.slc.cfg.command.beans.ConfigInfoData;
import com.sap.custdev.projects.fbs.slc.cfg.command.beans.VariantCondKeyData;
import com.sap.custdev.projects.fbs.slc.cfg.exception.IpcCommandException;
import com.sap.custdev.projects.fbs.slc.cfg.ipintegration.InteractivePricingException;
import com.sap.custdev.projects.fbs.slc.kbo.local.OrchestratedCstic;
import com.sap.custdev.projects.fbs.slc.kbo.local.OrchestratedInstance;
import com.sap.sce.front.base.Cstic;
import com.sap.sce.front.base.CsticType;
import com.sap.sce.front.base.Instance;
import com.sap.sce.front.base.InstanceType;
import com.sap.sce.front.base.KnowledgeBase;
import com.sap.sce.front.base.PricingConditionRate;
import com.sap.sce.kbrt.kb_descriptor;
import com.sap.sxe.sys.SAPDate;


@UnitTest
public class CommonConfigurationProviderSSCImplTest extends ConfigurationProviderSSCTestBase
{
	private static final String QUALIFIED_ID = "qualified id@plain id";
	private static final String PLAIN_ID = "plain id";


	private CommonConfigurationProviderSSCImpl classUnderTest;

	private final static String valueName = "ABC";
	private static final String LOGSYS = "logsys";
	private static final String P_CODE = "pCode";
	private static final String VERSION = "version";
	private static final String INSTACE_NAME = "instanceName";
	private static final String CURRENCY = "USD";

	@Mock
	protected OrchestratedCstic mockedOrchestratedCstic;
	@Mock
	private Cstic mockedFirstSharedCstic;
	@Mock
	private PricingConditionRate mockedPricingConditionRate;
	@Mock
	private CsticType mockedCsticType;
	@Mock
	private IConfigContainer mockedConfigContainer;
	@Mock
	private IConfigSession mockedSession;
	@Mock
	private ConfigurationProductUtil mockedConfigurationProductUtil;
	@Mock
	private ConfigurationSessionContainer configurationSessionContainer;
	@Mock
	private SolvableConflictAdapter conflictAdapter;
	@Mock
	private ConfigurationContextAndPricingWrapper contextAndPricingWrapper;
	@Mock
	private KBOCacheWrapper kboCache;
	@Mock
	private kb_descriptor kbDescriptor;

	private final InstanceModel rootInstanceModel = new InstanceModelImpl();
	private final ConfigModel configModel = new ConfigModelImpl();

	private Date kbDate;

	private final IConfigInfoData configInfo = new ConfigInfoData();
	@Mock
	private OrchestratedInstance orchestratedInstance;
	@Mock
	private Instance instance;
	@Mock
	private InstanceType type;
	private final OrchestratedCstic[] orchestratedCstics = new OrchestratedCstic[0];
	private final CsticModelImpl model = new CsticModelImpl();

	@Override
	@Before
	public void setUp() throws IpcCommandException
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new CommonConfigurationProviderSSCImpl();
		classUnderTest.setConfigModelFactory(new ConfigModelFactoryImpl());
		classUnderTest.setConfigurationProductUtil(mockedConfigurationProductUtil);
		classUnderTest.setConfigurationSessionContainer(configurationSessionContainer);
		classUnderTest.setConflictAdapter(conflictAdapter);
		classUnderTest.setContextAndPricingWrapper(contextAndPricingWrapper);
		classUnderTest.setKboCache(kboCache);

		when(contextAndPricingWrapper.isPricingConfigurationActive()).thenReturn(true);
		when(mockedOrchestratedCstic.getType()).thenReturn(mockedCsticType);
		when(mockedOrchestratedCstic.getFirstSharedCstic()).thenReturn(mockedFirstSharedCstic);
		when(mockedFirstSharedCstic.getDetailedPrice(valueName)).thenReturn(mockedPricingConditionRate);
		when(mockedOrchestratedCstic.getValueLangDependentName(valueName)).thenReturn("abc");
		when(Boolean.valueOf(mockedOrchestratedCstic.isValueUserOwned(valueName))).thenReturn(Boolean.TRUE);
		when(mockedPricingConditionRate.getConditionRateValue()).thenReturn(BigDecimal.ONE);
		when(mockedPricingConditionRate.getConditionRateUnitName()).thenReturn(CURRENCY);
		when(Integer.valueOf(mockedCsticType.getNumberScale())).thenReturn(Integer.valueOf(3));
		when(configurationSessionContainer.retrieveConfigSession(QUALIFIED_ID)).thenReturn(mockedSession);

		when(mockedSession.getConfigInfo(PLAIN_ID, false)).thenReturn(configInfo);
		when(mockedSession.getRootInstanceLocal(PLAIN_ID)).thenReturn(orchestratedInstance);
		when(orchestratedInstance.getFirstSharedInstance()).thenReturn(instance);
		when(instance.getType()).thenReturn(type);
		when(instance.getLangDepName()).thenReturn("-");
		when(orchestratedInstance.getCstics()).thenReturn(orchestratedCstics);
		when(orchestratedInstance.getCsticGroups(false)).thenReturn(new String[0]);
		when(mockedSession.getConfigItemInfo(PLAIN_ID)).thenReturn(mockedConfigContainer);
		when(mockedSession.getSessionId()).thenReturn("session id");
		when(mockedConfigContainer.getArrVariantCondKeyContainer()).thenReturn(new IVariantCondKeyData[0]);

		configModel.setKbKey(new KBKeyImpl(P_CODE, P_CODE, LOGSYS, VERSION));
		rootInstanceModel.setName(INSTACE_NAME);
		kbDate = configModel.getKbKey().getDate();

		if (Registry.hasCurrentTenant())
		{
			Config.setParameter("sapproductconfigruntimessc.roundCsticValue", "true");
		}

	}

	@Test
	public void testNumericTypefloat()
	{
		when(Integer.valueOf(mockedCsticType.getValueType())).thenReturn(Integer.valueOf(CsticModel.TYPE_FLOAT));

		final CsticValueModel valueModel = classUnderTest.createModelValue(mockedOrchestratedCstic, "VALUE_NAME", false, false,
				null);
		assertEquals(CsticValueModelImpl.class, valueModel.getClass());
		assertTrue(valueModel.isNumeric());
	}

	@Test
	public void testNonNumericTypeString()
	{
		when(Integer.valueOf(mockedCsticType.getValueType())).thenReturn(Integer.valueOf(CsticModel.TYPE_STRING));

		final CsticValueModel valueModel = classUnderTest.createModelValue(mockedOrchestratedCstic, "VALUE_NAME", false, false,
				null);
		assertEquals(CsticValueModelImpl.class, valueModel.getClass());
	}

	@Test
	public void testDeltaPriceMapping() throws Exception
	{
		final CsticValueModel modelValue = classUnderTest.createModelValue(mockedOrchestratedCstic, valueName, true, true,
				mockedPricingConditionRate);

		assertEquals("wrong delta price", 0, BigDecimal.ONE.compareTo(modelValue.getDeltaPrice().getPriceValue()));
		assertEquals("wrong delta price currency", "USD", modelValue.getDeltaPrice().getCurrency());

		assertEquals("wrong value price", 0, BigDecimal.ONE.compareTo(modelValue.getValuePrice().getPriceValue()));
		assertEquals("wrong value price currency", "USD", modelValue.getValuePrice().getCurrency());
	}

	@Test
	public void testDeltaPriceMapping_emptyPrice() throws Exception
	{
		when(mockedPricingConditionRate.getConditionRateValue()).thenReturn(BigDecimal.ZERO);
		when(mockedPricingConditionRate.getConditionRateUnitName()).thenReturn("");

		final CsticValueModel modelValue = classUnderTest.createModelValue(mockedOrchestratedCstic, valueName, true, true,
				mockedPricingConditionRate);

		assertSame(PriceModel.NO_PRICE, modelValue.getDeltaPrice());
		assertSame(PriceModel.NO_PRICE, modelValue.getValuePrice());
	}

	@Test
	public void testDeltaPriceMapping_noPrice() throws Exception
	{
		Mockito.reset(mockedPricingConditionRate);

		final CsticValueModel modelValue = classUnderTest.createModelValue(mockedOrchestratedCstic, valueName, true, true, null);

		assertSame(PriceModel.NO_PRICE, modelValue.getDeltaPrice());
		assertSame(PriceModel.NO_PRICE, modelValue.getValuePrice());
	}

	@Test
	public void testDeltaPriceMapping_zeroPrice() throws Exception
	{
		when(mockedPricingConditionRate.getConditionRateValue()).thenReturn(BigDecimal.ZERO);

		final CsticValueModel modelValue = classUnderTest.createModelValue(mockedOrchestratedCstic, valueName, true, true,
				mockedPricingConditionRate);

		assertEquals("wrong delta price", 0, BigDecimal.ZERO.compareTo(modelValue.getDeltaPrice().getPriceValue()));
		assertEquals("wrong delta price currency", CURRENCY, modelValue.getDeltaPrice().getCurrency());

		assertEquals("wrong value price", 0, BigDecimal.ZERO.compareTo(modelValue.getValuePrice().getPriceValue()));
		assertEquals("wrong value price currency", CURRENCY, modelValue.getValuePrice().getCurrency());
	}

	@Test
	public void testCreateModelValueNoPricing() throws Exception
	{

		when(contextAndPricingWrapper.isPricingConfigurationActive()).thenReturn(false);

		final CsticValueModel modelValue = classUnderTest.createModelValue(mockedOrchestratedCstic, valueName, true, true, null);

		assertEquals("wrong delta price", PriceModel.NO_PRICE, modelValue.getDeltaPrice());
		assertEquals("wrong value price", PriceModel.NO_PRICE, modelValue.getValuePrice());
	}

	@Test
	public void testCreateCsticValuesNoPricing() throws Exception
	{
		when(contextAndPricingWrapper.isPricingConfigurationActive()).thenReturn(false);
		final CsticModel csticModel = new CsticModelImpl();
		csticModel.setAuthor(CsticModel.AUTHOR_USER);

		classUnderTest.createCsticValues(mockedOrchestratedCstic, csticModel);

		Mockito.verify(mockedFirstSharedCstic, Mockito.never()).getDeltaPrices();
	}

	@Test
	public void testCreateCsticValuesPriceCallIsDone() throws Exception
	{
		final CsticModel csticModel = new CsticModelImpl();
		csticModel.setAuthor(CsticModel.AUTHOR_USER);

		classUnderTest.createCsticValues(mockedOrchestratedCstic, csticModel);

		Mockito.verify(mockedFirstSharedCstic, Mockito.times(1)).getDeltaPrices();
	}

	@Test
	public void testisPricingActive()
	{
		assertTrue(classUnderTest.isPricingActive());
	}

	@Test
	public void testisPricingActiveFalseIfContextWrapperSaysSo()
	{
		when(contextAndPricingWrapper.isPricingConfigurationActive()).thenReturn(false);
		assertFalse(classUnderTest.isPricingActive());
	}

	@Test
	public void testisPricingActiveFalseIfNoContextWrapper()
	{
		classUnderTest.setContextAndPricingWrapper(null);
		assertFalse(classUnderTest.isPricingActive());
	}

	@Test
	public void testCreateCsticValues() throws Exception
	{
		final CsticModel csticModel = new CsticModelImpl();
		csticModel.setAllowsAdditionalValues(true);
		csticModel.setConstrained(true);
		csticModel.setAuthor(CsticModel.AUTHOR_USER);

		when(mockedOrchestratedCstic.getValues()).thenReturn("A D".split(" "));
		when(mockedOrchestratedCstic.getDynamicDomain()).thenReturn("A B C".split(" "));
		when(mockedOrchestratedCstic.getTypicalDomain()).thenReturn("A B C".split(" "));
		when(mockedFirstSharedCstic.getDeltaPrices()).thenReturn(null);

		when(mockedOrchestratedCstic.getValueLangDependentName(Mockito.anyString())).thenReturn("xxx");
		when(Boolean.valueOf(mockedOrchestratedCstic.isValueUserOwned(Mockito.anyString()))).thenReturn(Boolean.TRUE);
		when(Boolean.valueOf(mockedOrchestratedCstic.isValueDefault(Mockito.anyString()))).thenReturn(Boolean.TRUE);

		classUnderTest.createCsticValues(mockedOrchestratedCstic, csticModel);

		final List<CsticValueModel> assignableValues = csticModel.getAssignableValues();
		final List<CsticValueModel> assignedValues = csticModel.getAssignedValues();

		assertEquals("wrong number assignable values", 4, assignableValues.size());
		assertEquals("wrong number assigned values", 2, assignedValues.size());

		assertEquals("wrong assignable values [0]", "A", assignableValues.get(0).getName());
		assertEquals("wrong assignable values [1]", "B", assignableValues.get(1).getName());
		assertEquals("wrong assignable values [2]", "C", assignableValues.get(2).getName());
		assertEquals("wrong assignable values [3]", "D", assignableValues.get(3).getName());
		assertTrue("value should be selectable", assignableValues.get(0).isSelectable());
		assertTrue("value should be selectable", assignableValues.get(1).isSelectable());
		assertTrue("value should be selectable", assignableValues.get(2).isSelectable());
		assertTrue("value should be selectable", assignableValues.get(3).isSelectable());
		assertTrue("value should be a domain value", assignableValues.get(0).isDomainValue());
		assertTrue("value should be a domain value", assignableValues.get(1).isDomainValue());
		assertTrue("value should be a domain value", assignableValues.get(2).isDomainValue());
		assertTrue("value should not be a domain value", !assignableValues.get(3).isDomainValue());

		assertEquals("wrong assigned values [0]", "A", assignedValues.get(0).getName());
		assertEquals("wrong assigned values [1]", "D", assignedValues.get(1).getName());

		assertEquals("wrong cstic author", CsticModel.AUTHOR_DEFAULT, csticModel.getAuthor());
	}

	protected CsticModel prepareIntervals()
	{
		final CsticModel cstic = new CsticModelImpl();
		cstic.setValueType(CsticModel.TYPE_INTEGER);
		cstic.setIntervalInDomain(true);

		final List<CsticValueModel> assignableValues = new ArrayList<CsticValueModel>();
		final CsticValueModel csticValueInterval1 = new CsticValueModelImpl();
		csticValueInterval1.setName("10 - 20");
		csticValueInterval1.setDomainValue(true);
		assignableValues.add(csticValueInterval1);
		final CsticValueModel csticValueInterval2 = new CsticValueModelImpl();
		csticValueInterval2.setName("50 - 60");
		csticValueInterval2.setDomainValue(true);
		assignableValues.add(csticValueInterval2);
		cstic.setAssignableValues(assignableValues);
		return cstic;
	}

	@Test
	public void testAdjustIntervalInDomain() throws Exception
	{
		final CsticModel cstic = prepareIntervals();
		cstic.setAllowsAdditionalValues(true);
		cstic.setIntervalInDomain(false);

		classUnderTest.adjustIntervalInDomain(cstic);
		assertTrue(cstic.isIntervalInDomain());
	}

	@Test
	public void testFillConfigInfo()
	{
		configInfo.setKbId(123);
		configInfo.setConfigName("name");
		configInfo.setConfigId("id");
		configInfo.setSingleLevel(true);
		configInfo.setComplete(true);
		configInfo.setConsistent(true);
		configInfo.setKbName("name");
		configInfo.setKbLogSys("logsys");
		configInfo.setKbVersion("version");

		classUnderTest.fillConfigInfo(configModel, configInfo);

		assertEquals("123", configModel.getKbId());
		assertEquals("name", configModel.getName());
		assertTrue(configModel.isComplete());
		assertTrue(configModel.isConsistent());
		assertTrue(configModel.isSingleLevel());

		assertNull(configModel.getKbKey().getProductCode());
		assertEquals("name", configModel.getKbKey().getKbName());
		assertEquals("logsys", configModel.getKbKey().getKbLogsys());
		assertEquals("version", configModel.getKbKey().getKbVersion());
	}

	@Test
	public void testFillBuildNumber()
	{
		final IConfigInfoData configInfo = new ConfigInfoData();
		configInfo.setKbBuildNumber("1234");
		classUnderTest.fillConfigInfo(configModel, configInfo);
		assertEquals("1234", configModel.getKbBuildNumber());

	}

	@Test
	public void testRetrieveVariantConditionsForInstance() throws IpcCommandException
	{
		final String configId = "CONFIG_ID";
		final String instanceId = "INSTANCE_ID";
		final VariantCondKeyData[] variantConditionDataArray = prepareVariantConditionData(configId, instanceId);

		when(mockedSession.getConfigItemInfo(configId)).thenReturn(mockedConfigContainer);
		when(mockedConfigContainer.getArrVariantCondKeyContainer()).thenReturn(variantConditionDataArray);

		final List<VariantConditionModel> variantConditionList = classUnderTest.retrieveVariantConditionsForInstance(mockedSession,
				configId, instanceId);
		assertNotNull(variantConditionList);
		assertEquals(2, variantConditionList.size());
		assertEquals("VKEY1", variantConditionList.get(0).getKey());
		assertEquals(0, (new BigDecimal("1.0")).compareTo(variantConditionList.get(0).getFactor()));
		assertEquals("VKEY2", variantConditionList.get(1).getKey());
		assertEquals(0, (new BigDecimal("2.0")).compareTo(variantConditionList.get(1).getFactor()));
	}

	@Test
	public void testCreateVariantConditionModels()
	{
		final String configId = "CONFIG_ID";
		final String instanceId = "INSTANCE_ID";
		final VariantCondKeyData[] variantConditionDataArray = prepareVariantConditionData(configId, instanceId);


		final List<VariantConditionModel> variantConditionList = classUnderTest.createVariantConditionModels(instanceId,
				variantConditionDataArray);
		assertNotNull(variantConditionList);
		assertEquals(2, variantConditionList.size());
		assertEquals("VKEY1", variantConditionList.get(0).getKey());
		assertEquals(0, (new BigDecimal("1.0")).compareTo(variantConditionList.get(0).getFactor()));
		assertEquals("VKEY2", variantConditionList.get(1).getKey());
		assertEquals(0, (new BigDecimal("2.0")).compareTo(variantConditionList.get(1).getFactor()));
	}

	protected VariantCondKeyData[] prepareVariantConditionData(final String configId, final String instanceId)
	{
		final VariantCondKeyData[] variantConditionDataArray = new VariantCondKeyData[3];
		final VariantCondKeyData variantConditionData1 = new VariantCondKeyData();
		variantConditionData1.setConfigID(configId);
		variantConditionData1.setInstID(instanceId);
		variantConditionData1.setVkey("VKEY1");
		variantConditionData1.setFactor("1.0");
		variantConditionDataArray[0] = variantConditionData1;
		final VariantCondKeyData variantConditionData2 = new VariantCondKeyData();
		variantConditionData2.setConfigID(configId);
		variantConditionData2.setInstID(instanceId);
		variantConditionData2.setVkey("VKEY2");
		variantConditionData2.setFactor("2.0");
		variantConditionDataArray[1] = variantConditionData2;
		final VariantCondKeyData variantConditionData3 = new VariantCondKeyData();
		variantConditionData3.setConfigID(configId);
		variantConditionData3.setInstID("ANOTHER_INSTANCE_ID");
		variantConditionData3.setVkey("VKEY3");
		variantConditionData3.setFactor("3.0");
		variantConditionDataArray[2] = variantConditionData3;
		return variantConditionDataArray;
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testChangeConfiguration() throws ConfigurationEngineException
	{
		classUnderTest.changeConfiguration(null);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testReleaseSession() throws ConfigurationEngineException
	{
		classUnderTest.releaseSession("configId", "version");
	}

	@Test
	public void testPrepareConfigurationContext() throws IpcCommandException
	{
		classUnderTest.setContextAndPricingWrapper(new ConfigurationContextAndPricingWrapperImpl());
		classUnderTest.prepareConfigurationContext(mockedSession, new KBKeyImpl("PRODUCT"));
		Mockito.verify(mockedSession).setContext((Mockito.any()));
	}

	@Test
	public void testPrepareConfigurationContextNotSet() throws IpcCommandException
	{
		classUnderTest.setContextAndPricingWrapper(null);
		classUnderTest.prepareConfigurationContext(mockedSession, new KBKeyImpl("PRODUCT"));
		Mockito.verify(mockedSession, Mockito.never()).setContext((Mockito.any()));
	}

	@Test
	public void testDetermineProductForContextAndPricingChangeableVariant()
	{
		final String variantProductCode = "VARIANT";
		final String baseProductCode = "BASE";
		final ERPVariantProductModel changeableVariantProductModel = new ERPVariantProductModel();
		changeableVariantProductModel.setChangeable(true);
		when(mockedConfigurationProductUtil.getProductForCurrentCatalog(variantProductCode))
				.thenReturn(changeableVariantProductModel);
		assertEquals(variantProductCode, classUnderTest.determineProductForContextAndPricing(baseProductCode, variantProductCode));
	}

	@Test
	public void testDetermineProductForContextAndPricingNotChangeableVariant()
	{
		final String variantProductCode = "VARIANT";
		final String baseProductCode = "BASE";
		final ERPVariantProductModel changeableVariantProductModel = new ERPVariantProductModel();
		changeableVariantProductModel.setChangeable(false);
		when(mockedConfigurationProductUtil.getProductForCurrentCatalog(variantProductCode))
				.thenReturn(changeableVariantProductModel);
		assertEquals(baseProductCode, classUnderTest.determineProductForContextAndPricing(baseProductCode, variantProductCode));
	}

	@Test
	public void testUpdateKbKeyFromRootInstanceNoUpdate()
	{
		classUnderTest.updateKbKeyFromRootInstance(configModel, rootInstanceModel);
		assertEquals(P_CODE, configModel.getKbKey().getKbName());
		assertEquals(P_CODE, configModel.getKbKey().getProductCode());
		assertEquals(VERSION, configModel.getKbKey().getKbVersion());
		assertEquals(LOGSYS, configModel.getKbKey().getKbLogsys());
		assertEquals(kbDate, configModel.getKbKey().getDate());
	}

	@Test
	public void testUpdateKbKeyFromRootInstance()
	{
		final KBKey kbKey = new KBKeyImpl(null, null, LOGSYS, VERSION);

		configModel.setKbKey(kbKey);
		classUnderTest.updateKbKeyFromRootInstance(configModel, rootInstanceModel);
		assertEquals(INSTACE_NAME, configModel.getKbKey().getKbName());
		assertNull(configModel.getKbKey().getProductCode());
		assertEquals(VERSION, configModel.getKbKey().getKbVersion());
		assertEquals(LOGSYS, configModel.getKbKey().getKbLogsys());
		assertEquals(kbKey.getDate(), configModel.getKbKey().getDate());
	}

	@Test
	public void testProcessValueNameNoRoundingForNonFloat()
	{
		mockValueType(CsticModel.TYPE_INTEGER);
		classUnderTest.roundingEnbeled = true;
		final String actualValueName = classUnderTest.processValueName(mockedOrchestratedCstic, "100.00000001");
		assertEquals("100.00000001", actualValueName);
	}

	@Test
	public void testProcessValueNameNoRoundingWhenNotActivated()
	{
		// execute only in unit test mode, as the setting for the active tenant (Config) is true
		Assume.assumeFalse(Registry.hasCurrentTenant());
		mockValueType(CsticModel.TYPE_FLOAT);
		classUnderTest.roundingEnbeled = false;
		final String actualValueName = classUnderTest.processValueName(mockedOrchestratedCstic, "100.00000001");
		assertEquals("100.00000001", actualValueName);
	}

	@Test
	public void testProcessValueNameWithRounding()
	{
		mockValueType(CsticModel.TYPE_FLOAT);
		classUnderTest.roundingEnbeled = true;
		final String actualValueName = classUnderTest.processValueName(mockedOrchestratedCstic, "100.00000001");
		assertEquals("100.000", actualValueName);
	}

	@Test
	public void testProcessValueNameWithRoundingNegativeNumber()
	{
		mockValueType(CsticModel.TYPE_FLOAT);
		classUnderTest.roundingEnbeled = true;
		final String actualValueName = classUnderTest.processValueName(mockedOrchestratedCstic, "-3.0005");
		assertEquals("-3.001", actualValueName);
	}

	@Test
	public void testProcessValueNameWithRoundingEmptyValue()
	{
		mockValueType(CsticModel.TYPE_FLOAT);
		classUnderTest.roundingEnbeled = true;
		final String actualValueName = classUnderTest.processValueName(mockedOrchestratedCstic, "");
		assertEquals("", actualValueName);
	}

	@Test
	public void testProcessValueNameWithRoundingRangeValue()
	{
		mockValueType(CsticModel.TYPE_FLOAT);
		classUnderTest.roundingEnbeled = true;
		final String actualValueName = classUnderTest.processValueName(mockedOrchestratedCstic, "99-101");
		assertEquals("99-101", actualValueName);
	}

	@Test
	public void testProcessValueNameWithRoundingInfiniteIntervalGt()
	{
		mockValueType(CsticModel.TYPE_FLOAT);
		classUnderTest.roundingEnbeled = true;
		final String actualValueName = classUnderTest.processValueName(mockedOrchestratedCstic, ">99");
		assertEquals(">99", actualValueName);
	}

	@Test
	public void testProcessValueNameWithRoundingIntervalWithNegativeNumbers()
	{
		mockValueType(CsticModel.TYPE_FLOAT);
		classUnderTest.roundingEnbeled = true;
		final String actualValueName = classUnderTest.processValueName(mockedOrchestratedCstic, "-10 - -4");
		assertEquals("-10 - -4", actualValueName);
	}

	@Test
	public void testProcessValueNameWithRoundingInfiniteIntervalLt()
	{
		mockValueType(CsticModel.TYPE_FLOAT);
		classUnderTest.roundingEnbeled = true;
		final String actualValueName = classUnderTest.processValueName(mockedOrchestratedCstic, "<=99");
		assertEquals("<=99", actualValueName);
	}

	@Test
	public void testProcessValueNameScientificNotation()
	{
		mockValueType(CsticModel.TYPE_FLOAT);
		classUnderTest.roundingEnbeled = true;
		final String actualValueName = classUnderTest.processValueName(mockedOrchestratedCstic, "9.99999999999999E9");
		assertEquals("9.99999999999999E9", actualValueName);
	}


	@Test
	public void testProcessValueNameWithRoundingWhiteSpace()
	{
		mockValueType(CsticModel.TYPE_FLOAT);
		classUnderTest.roundingEnbeled = true;
		final String actualValueName = classUnderTest.processValueName(mockedOrchestratedCstic, " ");
		assertEquals(" ", actualValueName);
	}


	private void mockValueType(final int type)
	{
		when(Integer.valueOf(mockedCsticType.getValueType())).thenReturn(Integer.valueOf(type));
		when(Integer.valueOf(mockedOrchestratedCstic.getValueType())).thenReturn(Integer.valueOf(type));
	}

	@Test
	public void testGetStaticDomainLength()
	{
		assertEquals(0, classUnderTest.getStaticDomainLength(null));
		assertEquals(0, classUnderTest.getStaticDomainLength(new String[0]));
		assertEquals(3, classUnderTest.getStaticDomainLength(new String[3]));
	}

	@Test
	public void testFillConfigModel() throws IpcCommandException
	{
		final ConfigModel result = classUnderTest.fillConfigModel(QUALIFIED_ID);
		assertNotNull(result);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFillConfigModelExceptionConflict() throws IpcCommandException
	{
		classUnderTest.setConflictAdapter(null);
		classUnderTest.fillConfigModel(QUALIFIED_ID);
	}

	@Test(expected = IllegalStateException.class)
	public void testFillConfigModelExceptionConfig() throws IpcCommandException
	{
		when(mockedSession.getConfigInfo(PLAIN_ID, false)).thenThrow(new IpcCommandException());
		classUnderTest.fillConfigModel(QUALIFIED_ID);
	}

	@Test(expected = IllegalStateException.class)
	public void testFillConfigModelExceptionPricing() throws InteractivePricingException
	{
		doThrow(new InteractivePricingException()).when(contextAndPricingWrapper).processPrice(eq(mockedSession), eq(PLAIN_ID),
				any());
		classUnderTest.fillConfigModel(QUALIFIED_ID);
	}


	@After
	public void tearDown() throws Exception
	{
		if (Registry.hasCurrentTenant())
		{
			Config.setParameter("sapproductconfigruntimessc.roundCsticValue", "false");
		}
	}


	@Test
	public void testGetKbLogSys() throws IpcCommandException
	{
		final KnowledgeBase kb = mock(KnowledgeBase.class);
		when(kb.getKbLogsys()).thenReturn(LOGSYS);
		when(kbDescriptor.getAssociatedKB()).thenReturn(kb);
		when(kboCache.get_kb_Desc_from_cache(any(), any(), any(), any(), isNotNull(SAPDate.class), any(), any(), eq(P_CODE),
				eq("MARA"), isNotNull(String.class))).thenReturn(kbDescriptor);
		final String result = classUnderTest.getKbLogSys(P_CODE, "variant code", mockedSession);
		assertEquals(LOGSYS, result);
	}

	@Test
	public void testMapNumeric()
	{
		given(mockedCsticType.getValueType()).willReturn(5);
		given(mockedCsticType.getNumberScale()).willReturn(4);
		given(mockedCsticType.getTypeLength()).willReturn(3);

		classUnderTest.mapNumericFields(mockedOrchestratedCstic, model);

		assertEquals(5, model.getValueType());
		assertEquals(4, model.getNumberScale());
		assertEquals(3, model.getTypeLength());
	}

	@Test
	public void testMapNumericNull()
	{
		given(mockedCsticType.getValueType()).willReturn(0);
		given(mockedCsticType.getNumberScale()).willReturn(null);
		given(mockedCsticType.getTypeLength()).willReturn(null);

		classUnderTest.mapNumericFields(mockedOrchestratedCstic, model);

		assertEquals(0, model.getValueType());
		assertEquals(0, model.getNumberScale());
		assertEquals(0, model.getTypeLength());

	}


}
