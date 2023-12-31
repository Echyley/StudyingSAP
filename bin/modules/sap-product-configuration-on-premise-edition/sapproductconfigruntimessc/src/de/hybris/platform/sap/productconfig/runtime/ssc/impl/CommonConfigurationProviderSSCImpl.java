/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.ssc.impl;

import de.hybris.platform.core.Registry;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticGroupModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.VariantConditionModel;
import de.hybris.platform.sap.productconfig.runtime.interf.services.ConfigurationProductUtil;
import de.hybris.platform.sap.productconfig.runtime.ssc.ConfigurationContextAndPricingWrapper;
import de.hybris.platform.sap.sapmodel.model.ERPVariantProductModel;
import de.hybris.platform.util.Config;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.sap.custdev.projects.fbs.slc.cfg.IConfigSession;
import com.sap.custdev.projects.fbs.slc.cfg.client.IConfigContainer;
import com.sap.custdev.projects.fbs.slc.cfg.client.IConfigHeader;
import com.sap.custdev.projects.fbs.slc.cfg.client.IConfigInfoData;
import com.sap.custdev.projects.fbs.slc.cfg.client.IVariantCondKeyData;
import com.sap.custdev.projects.fbs.slc.cfg.command.beans.ConfigContainer;
import com.sap.custdev.projects.fbs.slc.cfg.exception.IpcCommandException;
import com.sap.custdev.projects.fbs.slc.cfg.ipintegration.InteractivePricingException;
import com.sap.custdev.projects.fbs.slc.kbo.local.OrchestratedCstic;
import com.sap.custdev.projects.fbs.slc.kbo.local.OrchestratedInstance;
import com.sap.custdev.projects.fbs.slc.kbo.util.ExternalConfigConverter;
import com.sap.sce.casebase.Case;
import com.sap.sce.casebase.CaseBase;
import com.sap.sce.front.base.DecompItem;
import com.sap.sce.front.base.ExtConfig;
import com.sap.sce.front.base.PricingConditionRate;
import com.sap.sce.kbrt.kb_descriptor;
import com.sap.sxe.sys.SAPDate;


/**
 * Default implementation of the {@link ConfigurationProvider}.
 */
public class CommonConfigurationProviderSSCImpl extends BaseConfigurationProviderSSCImpl
{

	/**
	 * Should be matched using find() and only for a string where we know that it is number/interval.<br>
	 * If we see within a number string a digit which is followed by a minus sign with an optional white space in
	 * between, we can consider this safely as an interval, as an simple number can only have a minus sign in front as a
	 * negative indicator.
	 */
	private static final Pattern INTERVAL_PATTERN = Pattern.compile("\\d\\s?-");
	/**
	 * Should be matched using find() and only for a string where we know that it is number/interval.<br>
	 * If we see within a number string either a greater(equals) or a less(equals) sign which is followed by a (negative)
	 * number with a white space in between we can consider this safely as an interval (with infinity).
	 */
	private static final Pattern INFINITY_PATTERN = Pattern.compile("[<>≤≥]=?\\s?-?\\d");

	private static final String VERSIONING_NOT_SUPPORTED = "The SSC runtime does not support versioning for runtime configurations.";
	private static final Logger LOG = Logger.getLogger(CommonConfigurationProviderSSCImpl.class);
	private static final boolean IS_VALUE_SELECTABLE = true;
	public static final int NUMBER_ELEMENTS_FOR_INTERVAL_VALUE_TYPE = 2;
	private ConfigurationProductUtil configurationProductUtil;
	boolean roundingEnbeled = false;

	@Override
	protected ConfigModel fillConfigModel(final String qualifiedId)
	{
		final ConfigModel configModel = getConfigModelFactory().createInstanceOfConfigModel();

		try
		{
			final IConfigSession session = retrieveConfigSession(qualifiedId);

			// Configuration Model
			configModel.setId(qualifiedId);
			final String configId = retrievePlainConfigId(qualifiedId);

			final IConfigInfoData configInfo;

			configInfo = session.getConfigInfo(configId, false);
			if (LOG.isDebugEnabled())
			{
				LOG.debug(String.format("fetched configInfo for configId=%s: %s", configId, configInfo));
			}

			fillConfigInfo(configModel, configInfo);

			// Root instance
			final OrchestratedInstance rootOrchestratedInstance = session.getRootInstanceLocal(configId);

			// Prepare instances (starting with root instance)
			final InstanceModel rootInstanceModel = prepareInstanceModel(session, configModel, rootOrchestratedInstance);

			configModel.setRootInstance(rootInstanceModel);
			updateKbKeyFromRootInstance(configModel, rootInstanceModel);

			// Retrieve root instance price
			retrievePrice(session, configModel);

			// transfer solvable conflicts
			if (getConflictAdapter() != null)
			{
				getConflictAdapter().transferSolvableConflicts(session, configId, configModel);
			}
			else
			{
				throw new IllegalArgumentException("Conflict Adapter is not provided");
			}
		}

		catch (final IpcCommandException e)
		{
			throw new IllegalStateException("Cannot fill configuration model", e);
		}

		catch (final InteractivePricingException e)
		{
			throw new IllegalStateException("Cannot fill retrieve price", e);
		}

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Filled config with data: " + configModel.toString());
		}

		return configModel;
	}

	protected void updateKbKeyFromRootInstance(final ConfigModel configModel, final InstanceModel rootInstanceModel)
	{
		final KBKey oldKey = configModel.getKbKey();
		if (oldKey.getKbName() == null)
		{
			configModel.setKbKey(new KBKeyImpl(oldKey.getProductCode(), rootInstanceModel.getName(), oldKey.getKbLogsys(),
					oldKey.getKbVersion(), oldKey.getDate()));
		}

	}

	protected void fillConfigInfo(final ConfigModel configModel, final IConfigInfoData configInfo)
	{

		configModel.setName(configInfo.getConfigName());
		configModel.setKbId(String.valueOf(configInfo.getKbId()));
		configModel.setConsistent(configInfo.isConsistent());
		configModel.setComplete(configInfo.isComplete());
		configModel.setSingleLevel(configInfo.isSingleLevel());
		configModel.setKbBuildNumber(configInfo.getKbBuildNumber());
		configModel.setKbKey(new KBKeyImpl(null, configInfo.getKbName(), configInfo.getKbLogSys(), configInfo.getKbVersion()));

	}

	protected InstanceModel prepareInstanceModel(final IConfigSession session, final ConfigModel configModel,
			final OrchestratedInstance orchestratedInstance)
	{
		final InstanceModel instanceModel = createInstance(orchestratedInstance);

		final List<CsticGroupModel> goupModelList = prepareCsticGroups(orchestratedInstance);
		instanceModel.setCsticGroups(goupModelList);

		final List<CsticModel> csticModels = createCstics(orchestratedInstance, instanceModel.getId());
		instanceModel.setCstics(csticModels);

		instanceModel.setVariantConditions(
				retrieveVariantConditionsForInstance(session, retrievePlainConfigId(configModel.getId()), instanceModel.getId()));

		// Prepare subinstances
		final List<InstanceModel> subInstanceModelList = new ArrayList<>();

		final OrchestratedInstance[] orchestratedSubInstances = orchestratedInstance.getPartInstances();

		if (orchestratedSubInstances != null && orchestratedSubInstances.length > 0)
		{
			for (final OrchestratedInstance orchestratedSubInstance : orchestratedSubInstances)
			{
				final InstanceModel subInstanceModel = prepareInstanceModel(session, configModel, orchestratedSubInstance);
				subInstanceModelList.add(subInstanceModel);
			}
		}
		instanceModel.setSubInstances(subInstanceModelList);

		return instanceModel;
	}

	protected InstanceModel createInstance(final OrchestratedInstance orchestratedInstance)
	{
		final InstanceModel instanceModel = getConfigModelFactory().createInstanceOfInstanceModel();

		final String instanceId = String.valueOf(orchestratedInstance.getFirstSharedInstance().getUid());
		instanceModel.setId(instanceId);
		instanceModel.setName(orchestratedInstance.getFirstSharedInstance().getType().getName());

		final String ldn = orchestratedInstance.getFirstSharedInstance().getLangDepName();
		final int sepPos = ldn.indexOf('-');
		instanceModel.setLanguageDependentName(ldn.substring(sepPos + 1));

		String position = "";
		final DecompItem dItem = orchestratedInstance.getDecompItem();
		if (dItem != null)
		{
			position = dItem.getPosition();
		}
		instanceModel.setPosition(position);

		instanceModel.setConsistent(!orchestratedInstance.isConflicting());
		instanceModel.setComplete(orchestratedInstance.isComplete());
		instanceModel.setRootInstance(orchestratedInstance.isRootInstance());

		return instanceModel;
	}

	protected List<CsticModel> createCstics(final OrchestratedInstance orchestratedInstance, final String instId)
	{
		final List<CsticModel> csticModels = new ArrayList<>();

		final OrchestratedCstic[] orchestratedCstics = orchestratedInstance.getCstics();

		if (orchestratedCstics != null)
		{
			for (int ii = 0; ii < orchestratedCstics.length; ii++)
			{
				final OrchestratedCstic orchestratedCstic = orchestratedCstics[ii];

				final CsticModel csticModel = createCsticModel(orchestratedCstic, instId);
				createCsticValues(orchestratedCstic, csticModel);

				csticModels.add(csticModel);
			}
		}

		return csticModels;
	}

	protected CsticModel createCsticModel(final OrchestratedCstic orchestratedCstic, final String instId)
	{
		final CsticModel csticModel = getConfigModelFactory().createInstanceOfCsticModel();

		csticModel.setName(orchestratedCstic.getName());
		csticModel.setLanguageDependentName(orchestratedCstic.getLangDependentName());
		String description = orchestratedCstic.getDescription();
		description = getTextConverter().convertLongText(description);
		csticModel.setLongText(description);

		csticModel.setComplete(!orchestratedCstic.isRequired() || orchestratedCstic.hasValues());

		csticModel.setConsistent(!orchestratedCstic.isConflicting());

		csticModel.setConstrained(orchestratedCstic.isDomainConstrained());
		csticModel.setMultivalued(orchestratedCstic.isMultiValued());
		csticModel.setAllowsAdditionalValues(orchestratedCstic.getType().isAdditionalValuesAllowed());
		csticModel.setEntryFieldMask(orchestratedCstic.getType().getEntryFieldMask());
		csticModel.setIntervalInDomain(orchestratedCstic.isDomainAnInterval());
		csticModel.setReadonly(orchestratedCstic.isReadOnly());
		csticModel.setRequired(orchestratedCstic.isRequired());
		csticModel.setVisible(!orchestratedCstic.isInvisible());
		csticModel.setInstanceId(instId);

		final String csticAuthor = orchestratedCstic.isUserOwned() ? CsticModel.AUTHOR_USER : CsticModel.AUTHOR_SYSTEM;
		csticModel.setAuthor(csticAuthor);

		mapNumericFields(orchestratedCstic, csticModel);

		final String[] staticDomain = orchestratedCstic.getStaticDomain();
		final int staticDomainLength = getStaticDomainLength(staticDomain);
		csticModel.setStaticDomainLength(staticDomainLength);

		return csticModel;
	}

	protected void mapNumericFields(final OrchestratedCstic orchestratedCstic, final CsticModel csticModel)
	{
		csticModel.setValueType(orchestratedCstic.getType().getValueType());

		final Integer typeLength = orchestratedCstic.getType().getTypeLength();
		csticModel.setTypeLength(typeLength != null ? typeLength.intValue() : 0);

		final Integer numberScaleInt = orchestratedCstic.getType().getNumberScale();
		csticModel.setNumberScale((numberScaleInt != null) ? numberScaleInt.intValue() : 0);
	}

	protected int getStaticDomainLength(final String[] staticDomain)
	{
		return (staticDomain != null) ? staticDomain.length : 0;
	}

	protected boolean isPricingActive()
	{
		final ConfigurationContextAndPricingWrapper contextAndPricingWrapper = getContextAndPricingWrapper();
		return contextAndPricingWrapper != null && contextAndPricingWrapper.isPricingConfigurationActive();
	}

	protected void createCsticValues(final OrchestratedCstic orchestratedCstic, final CsticModel csticModel)
	{
		boolean containesValueSetByUser = false;

		// Prepare the list of relevant values
		final String[] valuesAssigned = orchestratedCstic.getValues();
		final String[] domain = getDomainValues(orchestratedCstic, csticModel);
		final List<String> valueNames = getValueNames(valuesAssigned, domain);

		// Retrieve delta price map
		Map<String, PricingConditionRate> deltaPriceMap = null;
		if (isPricingActive())
		{
			deltaPriceMap = orchestratedCstic.getFirstSharedCstic().getDeltaPrices();
		}

		final List<CsticValueModel> assignedValues = new ArrayList<>();
		final List<CsticValueModel> assignableValues = new ArrayList<>();

		// Process values
		for (String valueName : valueNames)
		{
			final boolean isDomainValue = isValueContained(valueName, domain);
			final boolean isValueAssigned = isValueContained(valueName, valuesAssigned);

			PricingConditionRate pricingConditionRate = null;
			if (deltaPriceMap != null)
			{
				pricingConditionRate = deltaPriceMap.get(valueName);
			}


			valueName = processValueName(orchestratedCstic, valueName);


			final CsticValueModel csticValueModel = createModelValue(orchestratedCstic, valueName, isDomainValue,
					IS_VALUE_SELECTABLE, pricingConditionRate);

			final String authorExternal = orchestratedCstic.getFirstSharedCstic().getValueDBAuthor(valueName);
			csticValueModel.setAuthorExternal(authorExternal);

			if (isValueAssigned)
			{
				assignedValues.add(csticValueModel);
				if (csticValueModel.getAuthor() != null
						&& !csticValueModel.getAuthor().equalsIgnoreCase(CsticValueModel.AUTHOR_SYSTEM)
						&& !orchestratedCstic.isValueDefault(valueName))
				{
					containesValueSetByUser = true;
				}
			}

			if (csticModel.isConstrained() || csticModel.isAllowsAdditionalValues())
			{
				assignableValues.add(csticValueModel);
			}
		}

		csticModel.setAssignedValuesWithoutCheckForChange(assignedValues);
		csticModel.setAssignableValues(assignableValues);

		adjustCsticAuthor(csticModel, containesValueSetByUser);
		adjustIntervalInDomain(csticModel);
	}

	protected String processValueName(final OrchestratedCstic orchestratedCstic, String valueName)
	{
		if (isRoundingEnabled() && orchestratedCstic.getValueType() == CsticModel.TYPE_FLOAT && valueName.trim().length() > 0
				&& !valueName.contains("E"))
		{

			final Matcher matchInterval = INTERVAL_PATTERN.matcher(valueName.trim());
			final Matcher matchInfinity = INFINITY_PATTERN.matcher(valueName.trim());
			if (!matchInterval.find() && !matchInfinity.find())
			{
				valueName = roundNumberCstic(orchestratedCstic, valueName);
			}
		}

		return valueName;
	}

	protected boolean isRoundingEnabled()
	{
		if (Registry.hasCurrentTenant())
		{
			// productive mode
			return Config.getBoolean("sapproductconfigruntimessc.roundCsticValue", false);
		}
		else
		{
			// unit test mode
			return this.roundingEnbeled;
		}
	}

	protected void adjustIntervalInDomain(final CsticModel csticModel)
	{
		// ssc engine retrieved false for "intervalInDomain" for cstics with
		// interval in domain AND "allowedAdditionalValues"
		// we set this flag to true in this case
		if (csticModel.isIntervalInDomain() || !csticModel.isAllowsAdditionalValues()
				|| (csticModel.getValueType() != CsticModel.TYPE_INTEGER && csticModel.getValueType() != CsticModel.TYPE_FLOAT))
		{
			return;
		}

		for (final CsticValueModel assignableValueModel : csticModel.getAssignableValues())
		{
			final String value = assignableValueModel.getName();
			final String[] splitedValue = value.split("-");
			if (splitedValue.length == NUMBER_ELEMENTS_FOR_INTERVAL_VALUE_TYPE)
			{
				csticModel.setIntervalInDomain(true);
				return;
			}
		}
	}

	protected List<String> getValueNames(final String[] valuesAssigned, final String[] domain)
	{
		final List<String> valueNames;
		if (domain == null || domain.length == 0)
		{
			valueNames = new ArrayList<>();
		}
		else
		{
			valueNames = new ArrayList<>(Arrays.asList(domain));
		}


		if (valuesAssigned != null && valuesAssigned.length > 0)
		{
			for (final String valueName : valuesAssigned)
			{
				if (!valueName.trim().isEmpty() && !valueNames.contains(valueName))
				{
					valueNames.add(valueName);
				}
			}
		}

		return valueNames;
	}

	protected String[] getDomainValues(final OrchestratedCstic orchestratedCstic, final CsticModel csticModel)
	{
		final String[] domain;
		if (csticModel.isAllowsAdditionalValues())
		{
			domain = orchestratedCstic.getTypicalDomain();
		}
		else
		{
			domain = orchestratedCstic.getDynamicDomain();
		}
		return domain;
	}

	protected CsticValueModel createModelValue(final OrchestratedCstic orchestratedCstic, final String valueName,
			final boolean isDomainValue, final boolean isValueSelectable, final PricingConditionRate pricingConditionRate)
	{
		final CsticValueModel csticValueModel = getConfigModelFactory()
				.createInstanceOfCsticValueModel(orchestratedCstic.getType().getValueType());

		csticValueModel.setName(valueName);
		csticValueModel.setLanguageDependentName(orchestratedCstic.getValueLangDependentName(valueName));

		final String csticValueAuthor = orchestratedCstic.isValueUserOwned(valueName) ? CsticValueModel.AUTHOR_USER
				: CsticValueModel.AUTHOR_SYSTEM;
		csticValueModel.setAuthor(csticValueAuthor);

		csticValueModel.setDomainValue(isDomainValue);
		csticValueModel.setSelectable(isValueSelectable);

		// Delta price
		csticValueModel.setDeltaPrice(createDeltaPrice(pricingConditionRate));

		// Absolute Value Price
		PricingConditionRate valueConditionRate = null;
		if (isPricingActive())
		{
			valueConditionRate = orchestratedCstic.getFirstSharedCstic().getDetailedPrice(valueName);
		}
		else if (LOG.isDebugEnabled())
		{
			LOG.debug("Pricing is not active, detailed price won't be requested from engine");
		}
		final PriceModel valuePriceModel = createDeltaPrice(valueConditionRate);

		csticValueModel.setValuePrice(valuePriceModel);

		return csticValueModel;
	}

	protected PriceModel createDeltaPrice(final PricingConditionRate pricingConditionRate)
	{
		final PriceModel deltaPrice;
		BigDecimal deltaPriceValue = null;
		String deltaPriceUnit = null;
		if (pricingConditionRate != null)
		{
			deltaPriceValue = pricingConditionRate.getConditionRateValue();
			deltaPriceUnit = pricingConditionRate.getConditionRateUnitName();
		}

		if (pricingConditionRate != null && deltaPriceValue != null && deltaPriceUnit != null && !deltaPriceUnit.isEmpty())
		{
			deltaPrice = getConfigModelFactory().createInstanceOfPriceModel();
			deltaPrice.setPriceValue(deltaPriceValue);
			deltaPrice.setCurrency(deltaPriceUnit);
		}
		else
		{
			deltaPrice = getConfigModelFactory().getZeroPriceModel();
		}
		return deltaPrice;
	}

	protected boolean isValueContained(final String valueName, final String[] values)
	{
		boolean isValueContained = false;
		if (values != null)
		{
			for (final String value : values)
			{
				if (valueName.equals(value))
				{
					isValueContained = true;
					break;
				}
			}
		}
		return isValueContained;
	}

	protected List<CsticGroupModel> prepareCsticGroups(final OrchestratedInstance orchestratedInstance)
	{
		// Group name
		final String[] groupNames = orchestratedInstance.getCsticGroups(false);
		// Group descriptions
		final String[] groupLanguageDependentNames = orchestratedInstance.getCsticGroups(true);

		// All cstics in instance
		final OrchestratedCstic[] orchastratedCstics = orchestratedInstance.getCstics();
		final List<String> csticNamesInInstance = new ArrayList<>();
		for (final OrchestratedCstic orchastratedCstic : orchastratedCstics)
		{
			csticNamesInInstance.add(orchastratedCstic.getName());
		}

		// Initialize cstic groups
		final List<CsticGroupModel> csticGroupModelList = new ArrayList<>();

		for (int i = 0; i < groupNames.length; i++)
		{
			final CsticGroupModel csticGroupModel = getConfigModelFactory().createInstanceOfCsticGroupModel();
			csticGroupModel.setName(groupNames[i]);
			csticGroupModel.setDescription(groupLanguageDependentNames[i]);
			csticGroupModel.setCsticNames(new ArrayList<>());
			csticGroupModelList.add(csticGroupModel);

			final OrchestratedCstic[] orchastratedCsticsInGroup = orchestratedInstance.getCstics(groupNames[i]);
			final List<String> csticList = new ArrayList<>();

			for (final OrchestratedCstic orchastratedCstic : orchastratedCsticsInGroup)
			{
				final String csticName = orchastratedCstic.getName();
				csticList.add(csticName);

				if (csticNamesInInstance.contains(csticName))
				{
					csticNamesInInstance.remove(csticName);
				}
			}
			csticGroupModel.setCsticNames(csticList);
		}

		// Add default group
		if (!csticNamesInInstance.isEmpty())
		{
			final CsticGroupModel defaultGroup = getConfigModelFactory().createInstanceOfCsticGroupModel();
			defaultGroup.setName(InstanceModel.GENERAL_GROUP_NAME);
			defaultGroup.setCsticNames(csticNamesInInstance);
			csticGroupModelList.add(0, defaultGroup);
		}

		return csticGroupModelList;
	}


	@Override
	public ConfigModel retrieveConfigurationFromVariant(final String baseProductCode, final String variantProductCode)
	{
		ConfigModel configModel = null;
		try
		{
			final IConfigSession configSession = createSession(null);
			final String kbLogSys = getKbLogSys(baseProductCode, variantProductCode, configSession);

			final ExtConfig extConfig = retrieveExternalConfigurationFromVariant(variantProductCode, kbLogSys);

			final IConfigContainer configContainer = mapExtConfigToConfigContainer(extConfig);

			final String productForContextAndPricing = determineProductForContextAndPricing(baseProductCode, variantProductCode);
			final KBKey kbKey = new KBKeyImpl(productForContextAndPricing);
			prepareConfigurationContext(configSession, kbKey);

			getTimer().start("createConfigFromVariant");
			final String configId = configSession.recreateConfig(configContainer);
			getTimer().stop();

			final String qualifiedId = retrieveQualifiedId(configSession.getSessionId(), configId);
			holdConfigSession(qualifiedId, configSession);

			preparePricingContext(configSession, configId, kbKey);

			configModel = fillConfigModel(qualifiedId);
			updateProductCode(configModel, kbKey.getProductCode());

		}
		catch (IpcCommandException | InteractivePricingException e)
		{
			throw new IllegalStateException("Cannot retrieve configuration based on variant [" + variantProductCode + "]", e);
		}
		return configModel;
	}

	protected void prepareConfigurationContext(final IConfigSession configSession, final KBKey kbKey) throws IpcCommandException
	{
		if (getContextAndPricingWrapper() != null)
		{
			configSession.setContext(getContextAndPricingWrapper().retrieveConfigurationContext(kbKey));
		}
	}

	protected String determineProductForContextAndPricing(final String baseProductCode, final String variantProductCode)
	{
		LOG.debug("Base product code: " + baseProductCode);
		LOG.debug("Variant product code: " + variantProductCode);
		final ERPVariantProductModel variantProduct = (ERPVariantProductModel) getConfigurationProductUtil()
				.getProductForCurrentCatalog(variantProductCode);
		LOG.debug("Variant product: " + variantProduct);
		return (variantProduct.isChangeable() ? variantProductCode : baseProductCode);
	}

	protected String getKbLogSys(final String baseProductCode, final String variantProductCode, final IConfigSession configSession)
			throws IpcCommandException
	{
		getTimer().start("findKB");
		final kb_descriptor kbDescriptor = getKboCache().get_kb_Desc_from_cache(null, null, null, null, new SAPDate(new Date()),
				null, null, baseProductCode, "MARA", configSession.getSessionId());
		getTimer().stop();
		if (kbDescriptor == null)
		{
			throw new IllegalStateException(
					"Cannot find knowledge base for base product [" + baseProductCode + "] of variant [" + variantProductCode + "]");
		}

		return kbDescriptor.getAssociatedKB().getKbLogsys();
	}

	protected IConfigContainer mapExtConfigToConfigContainer(final ExtConfig extConfig)
	{
		final IConfigContainer configContainer = new ConfigContainer();

		ExternalConfigConverter.getConfig(extConfig.getSceConfig(), configContainer);

		final IConfigHeader configHeader = configContainer.getConfigHeader();
		final String language = getI18NService().getCurrentLocale().getLanguage().toUpperCase(Locale.ENGLISH);
		configHeader.setKbLanguage(language);
		configContainer.setProductType(extConfig.getRootInstance().getObjectType());
		return configContainer;
	}

	/**
	 * retrives the external configuration for agiven configuration variant
	 *
	 * @param variantProductCode
	 *           productCode
	 * @param kbLogSys
	 *           logical system
	 * @return external configuraton
	 */
	public ExtConfig retrieveExternalConfigurationFromVariant(final String variantProductCode, final String kbLogSys)
	{
		final CaseBase cbase = CaseBase.getCaseBase();
		getTimer().start("getCBase");
		final Case caseVariant = cbase.getCase(kbLogSys, variantProductCode, TYPE_MARA);
		getTimer().stop();

		if (caseVariant == null)
		{
			LOG.error("Cannot find external configuration for variant [" + variantProductCode + "] in SSC database");
			throw new IllegalStateException("Cannot find external configuration for variant [" + variantProductCode + "]");
		}

		return caseVariant.getExtConfig();
	}

	protected List<VariantConditionModel> retrieveVariantConditionsForInstance(final IConfigSession session, final String configId,
			final String instanceId)
	{
		final List<VariantConditionModel> variantConditionList = new ArrayList<>();
		try
		{
			final IConfigContainer configContainer = session.getConfigItemInfo(configId);
			final IVariantCondKeyData[] varCondArray = configContainer.getArrVariantCondKeyContainer();

			if (varCondArray.length > 0)
			{
				variantConditionList.addAll(createVariantConditionModels(instanceId, varCondArray));
			}
		}
		catch (final IpcCommandException e)
		{
			throw new IllegalStateException("Cannot retrieve ConfigItemInfo", e);
		}
		return variantConditionList;
	}

	protected List<VariantConditionModel> createVariantConditionModels(final String instanceId,
			final IVariantCondKeyData[] varCondArray)
	{
		final List<VariantConditionModel> variantConditionList = new ArrayList<>();
		for (final IVariantCondKeyData varCond : varCondArray)
		{
			if (!varCond.getInstID().equals(instanceId))
			{
				continue;
			}
			final VariantConditionModel variantCondition = getConfigModelFactory().createInstanceOfVariantConditionModel();

			variantCondition.setKey(varCond.getVkey());
			variantCondition.setFactor(new BigDecimal(varCond.getFactor()));

			variantConditionList.add(variantCondition);
		}

		return variantConditionList;
	}

	@Override
	public String changeConfiguration(final ConfigModel model) throws ConfigurationEngineException
	{
		throw new UnsupportedOperationException(VERSIONING_NOT_SUPPORTED);
	}

	@Override
	public void releaseSession(final String configId, final String version)
	{
		throw new UnsupportedOperationException(VERSIONING_NOT_SUPPORTED);
	}

	protected ConfigurationProductUtil getConfigurationProductUtil()
	{
		return configurationProductUtil;
	}

	@Required
	public void setConfigurationProductUtil(final ConfigurationProductUtil configurationProductUtil)
	{
		this.configurationProductUtil = configurationProductUtil;
	}

	protected String roundNumberCstic(final OrchestratedCstic orchestratedCstic, String valueName)
	{
		final int scale = orchestratedCstic.getType().getNumberScale();
		final BigDecimal number = new BigDecimal(valueName);
		final BigDecimal numberRounded = number.setScale(scale, RoundingMode.HALF_UP);
		valueName = numberRounded.toString();
		return valueName;
	}
}
