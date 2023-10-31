/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.mock.provider.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willAnswer;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.PriceModelImpl;
import de.hybris.platform.sap.productconfig.runtime.mock.impl.RunTimeConfigMockFactory;
import de.hybris.platform.sap.productconfig.runtime.mock.persistence.ConfigurationMockPersistenceService;
import de.hybris.platform.sap.productconfig.runtime.mock.util.ModelCloneFactory;
import de.hybris.platform.sap.productconfig.runtime.mock.util.impl.DefaultConfigurationMockIdGenarator;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ConfigurationProviderMockImplTest
{
	private static final String VERSION_NUMBER_AFTER_CHANGE = "1";
	private static final BigDecimal PRICE = BigDecimal.valueOf(124);
	private static final String PRODUCT_CODE_BASE_PRODUCT_CHANGEABLE_VARIANT = "CONF_M_PIPE";
	private static final String PRODUCT_CODE_CHANGEABLE_VARIANT = "CONF_M_PIPE-1-1-T";
	private static final String PRODUCT_CODE_INVALID_RT_VERSION = "WEC_DRAGON_BUS";
	private static final String QUERY = "GET { ProductConfigurationMock } WHERE {configId} = ?configId";

	@InjectMocks
	private ConfigurationProviderMockImpl classUnderTest;

	@Mock
	private ProductService productService;
	@Mock
	private ProductModel productModelChangeableVariant;
	@Mock
	private ConfigurationMockPersistenceService configurationMockPersistenceService;

	private KBKey ysapKbKey;
	private ConfigModel model;
	private PriceModel valuePrice;

	private final HashMap<String, ConfigModel> savedConfigs = new HashMap<>();
	private final HashMap<String, ConfigModel> savedExtConfigs = new HashMap<>();

	@Before
	public void setUp()
	{
		savedConfigs.clear();
		classUnderTest.setConfigMockFactory(new RunTimeConfigMockFactory());
		classUnderTest.setMockIdGenerator(new DefaultConfigurationMockIdGenarator());

		ysapKbKey = new KBKeyImpl("YSAP_SIMPLE_POC_KB", "YSAP_SIMPLE_POC_KB",null,null);
		valuePrice = new PriceModelImpl();
		valuePrice.setPriceValue(PRICE);
		given(productService.getProductForCode(PRODUCT_CODE_CHANGEABLE_VARIANT)).willReturn(productModelChangeableVariant);

		given(configurationMockPersistenceService.readConfigModel(any(String.class)))
				.willAnswer(invocation -> savedConfigs.get(invocation.getArgument(0)));

		willAnswer(invocation -> saveConfigLocally((ConfigModel) invocation.getArgument(0))).given(configurationMockPersistenceService)
				.writeConfigModel(any(ConfigModel.class));

		given(configurationMockPersistenceService.readExtConfigModel(any(String.class)))
				.willAnswer(invocation -> savedExtConfigs.get(invocation.getArgument(0)));

		willAnswer(invocation -> saveExtConfigLocally((ConfigModel) invocation.getArgument(0))).given(configurationMockPersistenceService)
				.writeExtConfigModel(any(ConfigModel.class));

		model = classUnderTest.createDefaultConfiguration(ysapKbKey);
	}


	private Object saveConfigLocally(final ConfigModel model)
	{
		return savedConfigs.put(model.getId(), ModelCloneFactory.cloneConfigModel(model));
	}

	private Object saveExtConfigLocally(final ConfigModel model)
	{
		return savedExtConfigs .put(model.getId(), ModelCloneFactory.cloneConfigModel(model));
	}



	@Test
	public void testCreateConfigMockForVariant()
	{
		final ConfigModel configModel = classUnderTest.createConfigMock(PRODUCT_CODE_BASE_PRODUCT_CHANGEABLE_VARIANT,
				PRODUCT_CODE_CHANGEABLE_VARIANT, null, false);
		assertEquals(PRODUCT_CODE_CHANGEABLE_VARIANT, configModel.getKbKey().getProductCode());
	}

	@Test(expected = NullPointerException.class)
	public void testNullKB()
	{
		final ConfigModel configModel = classUnderTest.createDefaultConfiguration(null);
	}

	@Test(expected = NullPointerException.class)
	public void testKBWithNullName()
	{
		final KBKey kbKey = new KBKeyImpl(null, null, null, null);
		final ConfigModel configModel = classUnderTest.createDefaultConfiguration(kbKey);
	}


	public void testBuildNumberFilled()
	{
		final ConfigModel configModel = classUnderTest.createConfigMock("sapId", null, null, false);
		assertEquals("123", configModel.getKbBuildNumber());
	}

	@Test
	public void testKBWithEmptyName()
	{
		final KBKey kbKey = new KBKeyImpl("", "", "", "");
		final ConfigModel configModel = classUnderTest.createDefaultConfiguration(kbKey);
		assertNotNull(configModel);
	}

	@Test
	public void testGettingMultipleConfigsCorrectlyBasedOnConfigId() throws ConfigurationEngineException
	{

		final KBKey wcemKbKey = new KBKeyImpl("WCEM_SIMPLE_TEST", "WCEM_SIMPLE_TEST", "", "");
		final KBKey ysapKbKey = new KBKeyImpl("YSAP_SIMPLE_POC", "YSAP_SIMPLE_POC_KB", "", "");

		final Set<String> ids = new HashSet<>();
		final ConfigModel wcemModel = classUnderTest.createDefaultConfiguration(wcemKbKey);
		assertTrue(ids.add(wcemModel.getId()));


		final ConfigModel ysapModel = classUnderTest.createDefaultConfiguration(ysapKbKey);
		assertTrue(ids.add(ysapModel.getId()));


		final ConfigModel wcemSecondModel = classUnderTest.retrieveConfigurationModel(wcemModel.getId());
		assertEquals(wcemModel.getName(), wcemSecondModel.getName());


		final ConfigModel ysapSecondModel = classUnderTest.retrieveConfigurationModel(ysapModel.getId());
		assertEquals(ysapModel.getName(), ysapSecondModel.getName());

	}

	@Test
	public void testGetExternalConfigNotNull() throws ConfigurationEngineException
	{
		final KBKey ysapKbKey = new KBKeyImpl("", "YSAP_SIMPLE_POC_KB", "", "");
		final ConfigModel model = classUnderTest.createDefaultConfiguration(ysapKbKey);

		final String xml = classUnderTest.retrieveExternalConfiguration(model.getId());
		assertNotNull(xml);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetExternalConfigExceptionForInvalidConfigId() throws ConfigurationEngineException
	{
		classUnderTest.retrieveExternalConfiguration("123");
	}

	@Test
	public void testGetExternalConfigExceptionForUnknownConfigId() throws ConfigurationEngineException
	{
		final String xml = classUnderTest.retrieveExternalConfiguration("110031d5-3483-471e-9ef7-d1e65e185782@WCEM_SIMPLE_TEST@");
		assertNotNull(xml);
	}


	@Test
	public void testGetExternalConfigWellformed()
			throws ParserConfigurationException, SAXException, IOException, ConfigurationEngineException
	{
		final KBKey ysapKbKey = new KBKeyImpl("", "YSAP_SIMPLE_POC_KB", "", "");
		final ConfigModel model = classUnderTest.createDefaultConfiguration(ysapKbKey);

		final String xmlString = classUnderTest.retrieveExternalConfiguration(model.getId());


		final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		final InputStream source = new ByteArrayInputStream(xmlString.getBytes("UTF-8"));
		final Document doc = dBuilder.parse(source);
		assertNotNull(doc);
	}


	@Test
	public void testRetrieveExternalConfiguration()
			throws ParserConfigurationException, SAXException, IOException, ConfigurationEngineException
	{
		final KBKey wcemKbKey = new KBKeyImpl("WCEM_SIMPLE_TEST", "WCEM_SIMPLE_TEST", "", "");
		final KBKey ysapKbKey = new KBKeyImpl("YSAP_SIMPLE_POC", "YSAP_SIMPLE_POC_KB", "", "");
		final KBKey colorKbKey = new KBKeyImpl("WEC_COLOR_TEST", "WEC_COLOR_TEST", "", "");
		// Create 3 distinct configurations to be stored in the test-session
		final Set<String> ids = new HashSet<>();
		final ConfigModel wcemModel = classUnderTest.createDefaultConfiguration(wcemKbKey);
		assertTrue(ids.add(wcemModel.getId()));

		final ConfigModel ysapModel = classUnderTest.createDefaultConfiguration(ysapKbKey);
		assertTrue(ids.add(ysapModel.getId()));

		final ConfigModel colorModel = classUnderTest.createDefaultConfiguration(colorKbKey);
		assertTrue(ids.add(colorModel.getId()));

		final String extConfigYsap = classUnderTest.retrieveExternalConfiguration(ysapModel.getId());
		// mock remembers the model even after release session so it can restore it from extConfig
		classUnderTest.releaseSession(ysapModel.getId());

		final ConfigModel newModel = classUnderTest.createConfigurationFromExternalSource(ysapKbKey, extConfigYsap);
		assertTrue(ids.add(newModel.getId()));
	}

	@Test
	public void testRetrieveExternalConfigurationNonExistingRTVersion()
			throws ParserConfigurationException, SAXException, IOException, ConfigurationEngineException
	{
		final KBKey wcemKbKey = new KBKeyImpl("", "WCEM_SIMPLE_TEST", "", "");

		final ConfigModel configModel = classUnderTest.createDefaultConfiguration(wcemKbKey);
		final ConfigModel savedModel = savedConfigs.get(configModel.getId());
		savedModel.getRootInstance().setName(PRODUCT_CODE_INVALID_RT_VERSION);

		classUnderTest.retrieveExternalConfiguration(configModel.getId());
		assertEquals(ConfigurationProviderMockImpl.INVALID_RT_VERSION, savedModel.getKbKey().getKbVersion());
	}


	@Test
	public void testCreateConfigurationFromExternalSource() throws ConfigurationEngineException
	{
		final KBKey colorKbKey = new KBKeyImpl("WEC_COLOR_TEST", "WEC_COLOR_TEST", "", "");
		final KBKey ysapKbKey = new KBKeyImpl("YSAP_SIMPLE_POC", "YSAP_SIMPLE_POC_KB", "", "");
		final KBKey wcemKbKey = new KBKeyImpl("WCEM_SIMPLE_TEST", "WCEM_SIMPLE_TEST", "", "");
		// Create 3 distinct configurations to be stored in the test-session
		final Set<String> ids = new HashSet<>();
		final ConfigModel colorModel = classUnderTest.createDefaultConfiguration(colorKbKey);
		assertTrue(ids.add(colorModel.getId()));

		final ConfigModel ysapModel = classUnderTest.createDefaultConfiguration(ysapKbKey);
		assertTrue(ids.add(ysapModel.getId()));


		final ConfigModel wcemModel = classUnderTest.createDefaultConfiguration(wcemKbKey);
		assertTrue(ids.add(wcemModel.getId()));

		// Retrieve dummy XML of first configuration
		final String extConfigColor = classUnderTest.retrieveExternalConfiguration(colorModel.getId());

		// Use extConfig of first configuration to create a configuration model. Id should not be the same as the id of old colorModel.
		final ConfigModel newColorModel = classUnderTest.createConfigurationFromExternalSource(colorKbKey, extConfigColor);
		assertNotEquals(colorModel.getId(), newColorModel.getId());
	}

	@Test
	public void testCreateDefaultMockConfiguration()
	{
		final String LOGSYS = "RR5CLNT910";
		final KBKeyImpl kbKey_SAP_SIMPLE_POC = new KBKeyImpl("YSAP_SIMPLE_POC", "YSAP_SIMPLE_POC_KB", LOGSYS, "3800");
		final ConfigModel configModel = classUnderTest.createDefaultConfiguration(kbKey_SAP_SIMPLE_POC);
		assertNotNull(configModel.getVersion());

		final List<CsticModel> cstics = configModel.getRootInstance().getCstics();

		Assert.assertEquals("Configuration schould have four cstcis: ", 4, cstics.size());

		// first characteristic is a check box
		CsticModel cstic = cstics.get(0);
		Assert.assertEquals("Cstic " + cstic.getName() + " should be X", "X", cstic.getAssignedValues().get(0).getName());
		Assert.assertEquals("Cstic Type " + cstic.getName() + " should be a STRING", CsticModel.TYPE_STRING, cstic.getValueType());
		Assert.assertEquals("Cstic " + cstic.getName() + " has a wrong language specific name ", "Simple Flag: Hide options",
				cstic.getLanguageDependentName());

		// second characteristic a numeric without default value
		cstic = cstics.get(1);
		Assert.assertEquals("Cstic " + cstic.getName() + " has no default value", 0, cstic.getAssignedValues().size());
		Assert.assertEquals("Cstic Type " + cstic.getName() + " should be numeric", CsticModel.TYPE_INTEGER, cstic.getValueType());
		Assert.assertEquals("Cstic " + cstic.getName() + " has a wrong language specific name ", "Num w/o decimal",
				cstic.getLanguageDependentName());
		Assert.assertFalse("Cstic Type " + cstic.getName() + " should not be visible", cstic.isVisible());

		// second characteristic a numeric without default value
		cstic = cstics.get(2);
		Assert.assertEquals("Cstic " + cstic.getName() + " has no default value", 0, cstic.getAssignedValues().size());
		Assert.assertEquals("Cstic Type " + cstic.getName() + " should be numeric", CsticModel.TYPE_INTEGER, cstic.getValueType());
		Assert.assertEquals("Cstic " + cstic.getName() + " has a wrong language specific name ", "Expected Number of Users",
				cstic.getLanguageDependentName());
		Assert.assertFalse("Cstic Type " + cstic.getName() + " should not be visible", cstic.isVisible());

		// fourth characteristic is radio button with a default value
		cstic = cstics.get(3);
		Assert.assertEquals("Cstic " + cstic.getName() + " should be ", "VAL3", cstic.getAssignedValues().get(0).getName());
		Assert.assertEquals("Cstic Type " + cstic.getName() + " should be radio button", CsticModel.TYPE_STRING,
				cstic.getValueType());
		Assert.assertEquals("Characteristic has 4 possible values:", 4, cstic.getAssignableValues().size());
		Assert.assertEquals("Cstic " + cstic.getName() + " has a wrong language specific name ", "Radio Button Group",
				cstic.getLanguageDependentName());
		Assert.assertFalse("Cstic Type " + cstic.getName() + " should not be visible", cstic.isVisible());
	}

	@Test
	public void testGetAllValuePricesForAssignedValuesNoValuePrices()
	{
		assertEquals("/0/0", classUnderTest.getAllValuePricesForAssignedValues(model));
	}

	@Test
	public void testGetAllValuePricesForAssignedValues()
	{
		model.getRootInstance().getCstics().get(0).getAssignedValues().get(0).setValuePrice(valuePrice);
		assertEquals("/" + PRICE + "/0", classUnderTest.getAllValuePricesForAssignedValues(model));
	}

	@Test
	public void testIncrementVersion()
	{
		assertEquals(ConfigurationProviderMockImpl.VERSION_NUMBER_INITIAL, model.getVersion());
		final String newVersion = classUnderTest.incrementVersion(model);
		assertEquals(VERSION_NUMBER_AFTER_CHANGE, newVersion);
	}

	@Test
	public void testChangeConfiguration() throws ConfigurationEngineException
	{
		assertEquals(VERSION_NUMBER_AFTER_CHANGE, classUnderTest.changeConfiguration(model));
	}

	@Test
	public void testRetrieveWillGiveCorrectVersionId() throws ConfigurationEngineException
	{
		classUnderTest.changeConfiguration(model);
		assertEquals(VERSION_NUMBER_AFTER_CHANGE, classUnderTest.retrieveConfigurationModel(model.getId()).getVersion());
	}

	@Test
	public void testGetProductModel() throws ConfigurationEngineException
	{
		assertEquals(productModelChangeableVariant, classUnderTest.getProductModel(PRODUCT_CODE_CHANGEABLE_VARIANT));
	}

	@Test
	public void testGetProductModelNotAvailable() throws ConfigurationEngineException
	{
		assertNull(classUnderTest.getProductModel(PRODUCT_CODE_INVALID_RT_VERSION));
	}


}
