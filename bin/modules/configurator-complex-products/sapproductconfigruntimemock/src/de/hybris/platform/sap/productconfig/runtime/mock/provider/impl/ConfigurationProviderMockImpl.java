/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.mock.provider.impl;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.ProviderFactory;
import de.hybris.platform.sap.productconfig.runtime.interf.external.Configuration;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;
import de.hybris.platform.sap.productconfig.runtime.mock.ConfigMock;
import de.hybris.platform.sap.productconfig.runtime.mock.ConfigMockFactory;
import de.hybris.platform.sap.productconfig.runtime.mock.data.ConfigurationId;
import de.hybris.platform.sap.productconfig.runtime.mock.persistence.ConfigurationMockPersistenceService;
import de.hybris.platform.sap.productconfig.runtime.mock.util.ConfigurationMockIdGenarator;
import de.hybris.platform.sap.productconfig.runtime.mock.util.ModelCloneFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;


public class ConfigurationProviderMockImpl implements ConfigurationProvider
{
	private static final Logger LOG = Logger.getLogger(ConfigurationProviderMockImpl.class);

	public static final String PREFIX_DEFAULT_CONFIG_FOR_DELTA_PRICING = "DEFAULT_CONFIG_FOR_DELTA_PRICING__";

	static final String VERSION_NUMBER_INITIAL = "0";
	static final String INVALID_RT_VERSION = "3700";

	// XML-Tags for adding test properties to external configuration, e.g.
	// config id: <TEST_PROPERTIES><CONFIG_ID>1</CONFIG_ID></TEST_PROPERTIES>
	private static final String DUMMY_XML_TEMPLATE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><SOLUTION><CONFIGURATION CFGINFO=\"\" CLIENT=\"000\" COMPLETE=\"F\" CONSISTENT=\"T\" KBBUILD=\"3\" KBNAME=\"DUMMY_KB\" KBPROFILE=\"DUMMY_KB\" KBVERSION=\"3800\" LANGUAGE=\"E\" LANGUAGE_ISO=\"EN\" NAME=\"SCE 5.0\" ROOT_NR=\"1\" SCEVERSION=\" \"><INST AUTHOR=\"5\" CLASS_TYPE=\"300\" COMPLETE=\"F\" CONSISTENT=\"T\" INSTANCE_GUID=\"\" INSTANCE_ID=\"01\" NR=\"1\" OBJ_KEY=\"DUMMY_KB\" OBJ_TXT=\"Dummy KB\" OBJ_TYPE=\"MARA\" QTY=\"1.0\" UNIT=\"ST\"><CSTICS><CSTIC AUTHOR=\"8\" CHARC=\"DUMMY_CSTIC\" CHARC_TXT=\"Dummy CStic\" VALUE=\"8\" VALUE_TXT=\"Value 8\"/></CSTICS></INST><PARTS/><NON_PARTS/></CONFIGURATION><SALES_STRUCTURE><ITEM INSTANCE_GUID=\"\" INSTANCE_ID=\"1\" INSTANCE_NR=\"1\" LINE_ITEM_GUID=\"\" PARENT_INSTANCE_NR=\"\"/></SALES_STRUCTURE><TEST_PROPERTIES CONFIG_ID=\"%s\"/></SOLUTION>";
	private static final String LOG_SYSTEM_MOCK = "MOCK";
	private static final String MOCK_NAME_PREFIX = LOG_SYSTEM_MOCK + " ";
	private static final String YSAP_SIMPLE_POC_KB = "YSAP_SIMPLE_POC_KB";
	private static final String WEC_DRAGON_BUS = "WEC_DRAGON_BUS";
	private static final String ACTION_CREATE = "CREATE";
	private static final String ACTION_CREATE_FROM_EXT = "CREATE_FROM_EXT";
	private static final String ACTION_GET = "GET";
	private static final String ACTION_UPDATE = "UPDATE";
	private static final String GET_EXT = "GET_EXT";

	private ConfigMockFactory configMockFactory;
	private ProviderFactory providerFactory;
	private ProductService productService;
	private ConfigurationMockPersistenceService configurationMockPersistenceService;
	private ConfigurationMockIdGenarator mockIdGenerator;

	/**
	 * if <code>true</code> behaves like CPS, if <code>false</code> behaves like SSC
	 */
	private boolean removePricesFromConfiguration = true;

	@Override
	public ConfigModel createDefaultConfiguration(final KBKey kbKey)
	{
		final String sapProductId = kbKey.getProductCode();
		return createConfigMock(sapProductId, null, null, true);
	}

	@Override
	public ConfigModel createConfigurationFromExternalSource(final Configuration extConfig)
	{
		throw new UnsupportedOperationException(
				"createConfigurationFromExternalSource(Configuration extConfig) not supported by this mock");
	}

	protected ConfigModel createConfigMock(final String sapProductId, final String variantProductCode, final String configIdToUse,
			final boolean persistent)
	{
		final long startTime = startPerfTrace();

		final String variantId = variantProductCode != null ? variantProductCode : "";
		final String configId = null != configIdToUse ? configIdToUse
				: getMockIdGenerator().generateConfigId(sapProductId, variantId);
		LOG.info(String.format("create default MOCKED config session for sapProductId='%s', variantId='%s': assigned configId='%s'",
				sapProductId, variantId, configId));

		final ConfigMock mock = getConfigMockFactory().createConfigMockForProductCode(sapProductId, variantProductCode);
		final ConfigModel model = mock.createDefaultConfiguration();

		// init model fields
		model.setId(configId);
		model.getRootInstance().setName(sapProductId);
		model.setName(MOCK_NAME_PREFIX + sapProductId);
		model.setVersion(VERSION_NUMBER_INITIAL);
		model.setKbBuildNumber("12");
		model.setKbKey(createKbKey(sapProductId, variantProductCode, mock.isChangeableVariant()));

		// check default state
		mock.checkModel(model);
		resetChangedByFrontEnd(model.getRootInstance());

		//add product facets if needed
		mock.addProductAttributes(model, getProductModel(variantProductCode != null ? variantProductCode : sapProductId));

		// save
		if (persistent)
		{
			saveCopyForDeltaPricing(model);
			getConfigurationMockPersistenceService().writeConfigModel(model); // save the original model from the mock (with prices)
		}
		removePricesIfNeeded(model);

		endPerfTrace(startTime, ACTION_CREATE);
		return model;
	}


	@Override
	public ConfigModel createConfigurationFromExternalSource(final KBKey kbKey, final String extConfig)
	{
		final long startTime = startPerfTrace();

		final String configId = extractConfigIdFromExtConfig(extConfig);
		ConfigurationId structuredConfigId;
		LOG.info("create from external MOCKED config session for '" + configId + "'");

		ConfigModel configModel = getConfigurationMockPersistenceService().readExtConfigModel(configId);
		configModel = createDefaultIfNull(configModel, configId, false);

		structuredConfigId = getMockIdGenerator().getStructuredConfigIdFromString(configId);
		final String newConfigId = mockIdGenerator.generateConfigId(structuredConfigId.getProductId(),
				structuredConfigId.getVariantId());

		configModel.setId(newConfigId);
		configModel.setKbId(newConfigId);
		LOG.info(String.format("create from external MOCKED config session for '%s' mapped to new id '%s'", configId, newConfigId));

		getConfigurationMockPersistenceService().writeConfigModel(configModel);
		saveCopyForDeltaPricing(configModel);

		removePricesIfNeeded(configModel);

		endPerfTrace(startTime, ACTION_CREATE_FROM_EXT);
		return configModel;
	}

	@Override
	public ConfigModel retrieveConfigurationModel(final String configId) throws ConfigurationEngineException
	{
		final long startTime = startPerfTrace();

		LOG.info("GET MOCKED config session for '" + configId + "'");

		ConfigModel model = getConfigurationMockPersistenceService().readConfigModel(configId);

		if (model != null)
		{
			final ConfigurationId structuredConfigId = getMockIdGenerator().getStructuredConfigIdFromString(model.getId());
			final ConfigMock configMock = getConfigMockFactory().createConfigMockForProductCode(structuredConfigId.getProductId(),
					structuredConfigId.getVariantId());
			configMock.enrichConfigurationModel(model);
		}

		model = createDefaultIfNull(model, configId, true);

		removePricesIfNeeded(model);

		endPerfTrace(startTime, ACTION_GET);
		return model;

	}

	@Override
	public String retrieveExternalConfiguration(final String configId) throws ConfigurationEngineException
	{
		final long startTime = startPerfTrace();

		LOG.info("GET external MOCKED config session for '" + configId + "'");

		ConfigModel model = getConfigurationMockPersistenceService().readConfigModel(configId);
		model = createDefaultIfNull(model, configId, true);

		String dummyExtConfig = String.format(DUMMY_XML_TEMPLATE, StringEscapeUtils.escapeXml10(configId));
		// check on validity
		if (!extConfigBelongsToValidProduct(model))
		{
			LOG.info("External representation that points to non-existing RT version");
			dummyExtConfig = dummyExtConfig.replace("KBVERSION=\"3800\"", "KBVERSION=\"" + INVALID_RT_VERSION + "\"");
			final KBKey oldKey = model.getKbKey();
			model.setKbKey(new KBKeyImpl(oldKey.getProductCode(), oldKey.getKbName(), LOG_SYSTEM_MOCK, INVALID_RT_VERSION));
		}
		configurationMockPersistenceService.writeExtConfigModel(model);
		endPerfTrace(startTime, GET_EXT);
		return dummyExtConfig;

	}

	/**
	 * Simply returns the default configuration of the base product as a variant
	 */
	@Override
	public ConfigModel retrieveConfigurationFromVariant(final String baseProductCode, final String variantProductCode)
	{
		return createConfigMock(baseProductCode, variantProductCode, null, true);
	}

	@Override
	public boolean updateConfiguration(final ConfigModel model) throws ConfigurationEngineException
	{
		final long startTime = startPerfTrace();
		LOG.info("UPDATE MOCKED config session for '" + model.getId() + "'");

		// clone the model so changes are not visible to the caller - instead he should read the 'new' model state
		final ConfigModel updatedModel = ModelCloneFactory.cloneConfigModel(model);
		setNewAuthorForChangedCstics(updatedModel.getRootInstance(), CsticModel.AUTHOR_USER);

		final ConfigurationId structuredConfigId = getMockIdGenerator().getStructuredConfigIdFromString(model.getId());
		final ConfigMock configMock = getConfigMockFactory().createConfigMockForProductCode(structuredConfigId.getProductId(),
				structuredConfigId.getVariantId());
		configMock.checkModel(updatedModel);
		setNewAuthorForChangedCstics(updatedModel.getRootInstance(), CsticModel.AUTHOR_SYSTEM); //does NOT overwrite USER
		resetChangedByFrontEnd(updatedModel.getRootInstance());
		// reset changedByFrontend flag an sets the User as Author
		configMock.addProductAttributes(updatedModel, getProductModel(updatedModel.getKbKey().getProductCode()));
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Value prices at assigned value level: " + getAllValuePricesForAssignedValues(updatedModel));
		}

		getConfigurationMockPersistenceService().writeConfigModel(updatedModel);

		endPerfTrace(startTime, ACTION_UPDATE);

		return true;
	}

	@Override
	public String changeConfiguration(final ConfigModel model) throws ConfigurationEngineException
	{
		final String newVersion = incrementVersion(model);
		model.setVersion(newVersion);
		updateConfiguration(model);
		return newVersion;
	}

	@Override
	public void releaseSession(final String configId)
	{
		getConfigurationMockPersistenceService().deleteConfigModel(configId);
		getConfigurationMockPersistenceService().deleteConfigModel(PREFIX_DEFAULT_CONFIG_FOR_DELTA_PRICING + configId);
		LOG.info("Mock Configuration session released, configId='" + configId + "'");
	}

	@Override
	public void releaseSession(final String configId, final String version)
	{
		releaseSession(configId);
	}

	@Override
	public KBKey extractKbKey(final String productCode, final String externalConfig)
	{
		final String kbVersion = parseAttributeValue(externalConfig, "CONFIGURATION", "KBVERSION");
		return new KBKeyImpl(productCode, productCode, LOG_SYSTEM_MOCK, kbVersion);
	}

	@Override
	public boolean isKbForDateExists(final String productCode, final Date kbDate)
	{
		return false;
	}

	@Override
	public boolean isKbVersionExists(final KBKey kbKey)
	{
		return null == kbKey.getKbVersion() || "3800".equals(kbKey.getKbVersion());
	}

	@Override
	public boolean isKbVersionValid(final KBKey kbKey)
	{
		return isKbVersionExists(kbKey)
				&& !(kbKey.getProductCode().equals(WEC_DRAGON_BUS) && kbKey.getKbName().equals(YSAP_SIMPLE_POC_KB));
	}

	protected void endPerfTrace(final long startTime, final String action)
	{
		if (LOG.isDebugEnabled())
		{
			final long duration = System.currentTimeMillis() - startTime;
			LOG.debug(String.format("%s MOCK took %d ms", action, duration));
		}
	}

	protected long startPerfTrace()
	{
		long startTime = 0;
		if (LOG.isDebugEnabled())
		{
			startTime = System.currentTimeMillis();
		}
		return startTime;
	}

	protected void removePricesIfNeeded(final ConfigModel configModel)
	{
		if (isRemovePricesFromConfiguration())
		{
			// remove prices form original model - to simulate async pricing mode
			removePrices(configModel);
		}
	}

	protected void saveCopyForDeltaPricing(final ConfigModel model)
	{
		// copy model (with prices) and save it with special key
		final ConfigModel modelWithPrices = ModelCloneFactory.cloneConfigModel(model);
		modelWithPrices.setId(PREFIX_DEFAULT_CONFIG_FOR_DELTA_PRICING + modelWithPrices.getId());
		getConfigurationMockPersistenceService().writeConfigModel(modelWithPrices);
		model.setKbId(model.getId()); // so that PricingProviderMockImpl can find the just saved model
	}

	protected KBKey createKbKey(final String sapProductId, final String variantProductCode, final boolean isChangeableVariantMock)
	{
		KBKey kbKey;
		if (isChangeableVariantMock && variantProductCode != null)
		{
			kbKey = new KBKeyImpl(variantProductCode, sapProductId, LOG_SYSTEM_MOCK, null);
		}
		else
		{
			kbKey = new KBKeyImpl(sapProductId, sapProductId, LOG_SYSTEM_MOCK, "3800");
		}
		return kbKey;
	}

	protected ProductModel getProductModel(final String productCode)
	{
		ProductModel productModel = null;
		try
		{
			productModel = getProductService().getProductForCode(productCode);
		}
		catch (final RuntimeException ex)
		{
			LOG.info(String.format("Product %s was not found when doing lookup from mock", productCode));
		}
		return productModel;
	}

	protected void removePrices(final ConfigModel model)
	{
		model.setBasePrice(PriceModel.PRICE_NA);
		model.setCurrentTotalPrice(PriceModel.PRICE_NA);
		model.setSelectedOptionsPrice(PriceModel.PRICE_NA);
		model.setCurrentTotalSavings(PriceModel.PRICE_NA);


		// remove all prices
		removePrices(model.getRootInstance());
	}

	protected void removePrices(final InstanceModel instance)
	{
		for (final CsticModel cstic : instance.getCstics())
		{
			if (cstic.getAssignableValues() != null)
			{
				for (final CsticValueModel value : cstic.getAssignableValues())
				{
					value.setDeltaPrice(PriceModel.PRICE_NA);
					value.setValuePrice(PriceModel.PRICE_NA);
				}
			}
		}
		for (final InstanceModel subInstance : instance.getSubInstances())
		{
			removePrices(subInstance);
		}
	}

	protected void resetChangedByFrontEnd(final InstanceModel instance)
	{
		final List<CsticModel> cstics = instance.getCstics();
		for (final CsticModel cstic : cstics)
		{
			cstic.setChangedByFrontend(false);
		}
		for (final InstanceModel subInstance : instance.getSubInstances())
		{
			resetChangedByFrontEnd(subInstance);
		}
	}

	protected void setNewAuthorForChangedCstics(final InstanceModel instance, final String author)
	{
		final List<CsticModel> cstics = instance.getCstics();
		for (final CsticModel cstic : cstics)
		{
			if (cstic.isChangedByFrontend() && !CsticModel.AUTHOR_USER.equals(cstic.getAuthor()))
			{
				cstic.setAuthor(author);
			}
		}
		for (final InstanceModel subInstance : instance.getSubInstances())
		{
			setNewAuthorForChangedCstics(subInstance, author);
		}
	}

	protected String getAllValuePricesForAssignedValues(final ConfigModel configModel)
	{
		final StringBuilder result = new StringBuilder("");
		final List<CsticModel> cstics = configModel.getRootInstance().getCstics();
		cstics.stream().flatMap(cstic -> cstic.getAssignedValues().stream())
				.forEach(value -> result.append("/").append(value.getValuePrice().getPriceValue()));
		return result.toString();
	}

	protected ConfigModel createDefaultIfNull(final ConfigModel model, final String configId, final boolean persistent)
	{
		if (null == model)
		{
			final ConfigurationId structuredConfigId = getMockIdGenerator().getStructuredConfigIdFromString(configId);
			LOG.warn("Configuration " + configId + "was not found and had to be restored in its default state");
			return createConfigMock(structuredConfigId.getProductId(), structuredConfigId.getVariantId(), configId, persistent);
		}
		return model;
	}

	protected boolean extConfigBelongsToValidProduct(final ConfigModel model) throws ConfigurationEngineException
	{
		return model == null || !model.getRootInstance().getName().equals(WEC_DRAGON_BUS);
	}

	/**
	 * Extracts the config id from an external configuration string. The config id is stored as child element of the
	 * TEST_PROPERTIES tag. Example: <TEST_PROPERTIES><CONFIG_ID>1</CONFIG_ID></TEST_PROPERTIES>
	 *
	 * @param extConfig
	 * @return configId
	 */
	protected String extractConfigIdFromExtConfig(final String extConfig)
	{
		return parseAttributeValue(extConfig, "TEST_PROPERTIES", "CONFIG_ID");
	}

	protected String parseAttributeValue(final String extConfig, final String tag, final String attribute)
	{
		String attrValue = null;
		try
		{
			final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			// completely disable DOCTYPE declaration (security)
			dbFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
			dbFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

			final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

			final InputStream source = new ByteArrayInputStream(extConfig.getBytes("UTF-8"));
			final Document doc = dBuilder.parse(source);
			final Node testPropertiesNode = doc.getElementsByTagName(tag).item(0);
			final NamedNodeMap attrList = testPropertiesNode.getAttributes();
			final Node namedItem = attrList.getNamedItem(attribute);
			attrValue = namedItem.getNodeValue();

		}
		catch (ParserConfigurationException | SAXException | IOException e)
		{
			LOG.warn(e);
		}
		return attrValue;
	}

	protected String incrementVersion(final ConfigModel model)
	{
		String newVersion;
		final String oldVersion = model.getVersion();
		if (null == oldVersion)
		{
			newVersion = VERSION_NUMBER_INITIAL;
			LOG.warn(String.format("Version was missing for config with id='%s', setting initial version='0'", model.getId()));
		}
		else
		{
			newVersion = String.valueOf(Integer.valueOf(oldVersion) + 1);
		}
		return newVersion;
	}

	protected ConfigMockFactory getConfigMockFactory()
	{
		return configMockFactory;
	}

	@Required
	public void setConfigMockFactory(final ConfigMockFactory configMockFactory)
	{
		this.configMockFactory = configMockFactory;
	}

	protected ProviderFactory getProviderFactory()
	{
		return providerFactory;
	}

	@Required
	public void setProviderFactory(final ProviderFactory providerFactory)
	{
		this.providerFactory = providerFactory;
	}

	protected ProductService getProductService()
	{
		return productService;
	}

	@Required
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	protected ConfigurationMockPersistenceService getConfigurationMockPersistenceService()
	{
		return configurationMockPersistenceService;
	}

	@Required
	public void setConfigurationMockPersistenceService(
			final ConfigurationMockPersistenceService configurationMockPersistenceService)
	{
		this.configurationMockPersistenceService = configurationMockPersistenceService;
	}

	protected boolean isRemovePricesFromConfiguration()
	{
		return removePricesFromConfiguration;
	}

	public void setRemovePricesFromConfiguration(final boolean removePricesFromConfiguration)
	{
		this.removePricesFromConfiguration = removePricesFromConfiguration;
	}

	protected ConfigurationMockIdGenarator getMockIdGenerator()
	{
		return mockIdGenerator;
	}

	@Required
	public void setMockIdGenerator(final ConfigurationMockIdGenarator mockIdGenerator)
	{
		this.mockIdGenerator = mockIdGenerator;
	}


}
