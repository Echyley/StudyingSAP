/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services.impl;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.ProviderFactory;
import de.hybris.platform.sap.productconfig.runtime.interf.external.Configuration;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ConfigurationRetrievalOptions;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.SolvableConflictModel;
import de.hybris.platform.sap.productconfig.runtime.interf.services.ConfigurationProductUtil;
import de.hybris.platform.sap.productconfig.runtime.interf.services.impl.ProductConfigurationRelatedObjectType;
import de.hybris.platform.sap.productconfig.services.exceptions.ProductConfigurationAccessException;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationAccessControlService;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderEntryLinkStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAssignmentResolverStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationLifecycleStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationModelCacheStrategy;
import de.hybris.platform.sap.productconfig.services.tracking.TrackingRecorder;
import de.hybris.platform.webservicescommons.util.YSanitizer;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the {@link ProductConfigurationService}.<br>
 * This implementation will synchronize access to the {@link ConfigurationProvider}, so that it is guaranteed that only
 * exactly one thread will access the configuration provider for a given configuration session. Furthermore a simple
 * session based read cache ensures that subsequent calls to read the same configuration result only into exactly one
 * read request to the configuration engine.
 *
 * @see ProductConfigurationServiceImpl#setMaxLocksPerMap(int)
 */
public class ProductConfigurationServiceImpl implements ProductConfigurationService
{
	public static final String NOT_ALLOWED_TO_UPDATE_CONFIGURATION = "Not allowed to update configuration";
	public static final String NOT_ALLOWED_TO_READ_CONFIGURATION = "Not allowed to read configuration";
	public static final String NOT_ALLOWED_TO_RELEASE_CONFIGURATION = "Not allowed to release configuration";
	protected static final String DEBUG_CONFIG_WITH_ID = "Config with id '";
	protected static final String RETRIEVED_FROM_CACHE = "' retrieved from cache";
	protected static final String RETRIEVING_CONFIGURATION_FAILED = "Retrieving configuration failed";
	static final Object PROVIDER_LOCK = new Object();

	private static final Logger LOG = Logger.getLogger(ProductConfigurationServiceImpl.class);

	public static final int DEFAULT_MAX_LOCKS_PER_MAP = 1024;
	public static final double MAP_INITIAL_CAPACITY_CALCULATE_DIVISOR = 0.75;

	private static int maxLocksPerMap = DEFAULT_MAX_LOCKS_PER_MAP;
	private static Map<String, ReentrantLock> locks = new HashMap<>(
			(int) (maxLocksPerMap / MAP_INITIAL_CAPACITY_CALCULATE_DIVISOR + 1));
	private static Map<String, ReentrantLock> oldLocks = new HashMap<>(
			(int) (maxLocksPerMap / MAP_INITIAL_CAPACITY_CALCULATE_DIVISOR + 1));

	private ProviderFactory providerFactory;
	private ConfigurationLifecycleStrategy configLifecycleStrategy;
	private ConfigurationModelCacheStrategy configModelCacheStrategy;
	private ConfigurationAbstractOrderEntryLinkStrategy configurationAbstractOrderEntryLinkStrategy;
	private TrackingRecorder recorder;
	private ProductConfigurationAccessControlService productConfigurationAccessControlService;
	private ConfigurationAssignmentResolverStrategy assignmentResolverStrategy;
	private CPQConfigurableChecker cpqConfigurableChecker;
	private ConfigurationProductUtil configurationProductUtil;
	private boolean readDomainValuesOnDemand;

	@Override
	public ConfigModel createDefaultConfiguration(final KBKey kbKey)
	{
		// no need to synchronize create, because config session (identified by
		// the config ID)
		// is only exposed once the object has been created
		final ConfigModel config = getConfigLifecycleStrategy().createDefaultConfiguration(kbKey);
		recorder.recordCreateConfiguration(config, kbKey);

		return afterDefaultConfigCreated(config);

	}

	@Override
	public void updateConfiguration(final ConfigModel model)
	{
		checkUpdateAllowed(model);

		final String id = model.getId();
		final ReentrantLock lock = ProductConfigurationServiceImpl.getLock(id);
		lock.lock();

		try
		{
			final boolean updateExecuted = getConfigLifecycleStrategy().updateConfiguration(model);
			if (updateExecuted)
			{
				recorder.recordUpdateConfiguration(model);
				if (LOG.isDebugEnabled())
				{
					LOG.debug(DEBUG_CONFIG_WITH_ID + model.getId() + "' updated, removing it from cache");
				}
				removeConfigAttributesFromCache(id);
			}
		}
		catch (final ConfigurationEngineException ex)
		{
			cleanUpAfterEngineError(id);
			throw new IllegalStateException("Updating configuration failed", ex);
		}
		finally
		{
			lock.unlock();
		}
	}

	protected void checkUpdateAllowed(final ConfigModel model)
	{
		if (!getProductConfigurationAccessControlService().isUpdateAllowed(model.getId()))
		{
			throw new ProductConfigurationAccessException(NOT_ALLOWED_TO_UPDATE_CONFIGURATION);
		}
	}


	protected void checkReadAllowed(final String configId)
	{
		if (!getProductConfigurationAccessControlService().isReadAllowed(configId))
		{
			throw new ProductConfigurationAccessException(NOT_ALLOWED_TO_READ_CONFIGURATION);
		}
	}

	/**
	 * @deprecated since 2211 - use {@link ProductConfigurationService#retrieveConfigurationModel(String, String)}
	 *             instead
	 */
	@Override
	@Deprecated(since = "2211", forRemoval = true)
	public ConfigModel retrieveConfigurationModel(final String configId)
	{
		final String sanitizedConfigId = YSanitizer.sanitize(configId);
		checkReadAllowed(sanitizedConfigId);
		final ReentrantLock lock = ProductConfigurationServiceImpl.getLock(sanitizedConfigId);
		lock.lock();
		try
		{
			ConfigModel cachedModel = getConfigModelCacheStrategy().getConfigurationModelEngineState(sanitizedConfigId);
			if (cachedModel == null)
			{
				cachedModel = retrieveConfigurationModelFromConfigurationEngine(sanitizedConfigId,
						retrieveCorrectPricingDate(sanitizedConfigId));
				cacheConfig(cachedModel);
				recorder.recordConfigurationStatus(cachedModel);
			}
			else
			{
				LOG.debug(DEBUG_CONFIG_WITH_ID + sanitizedConfigId + RETRIEVED_FROM_CACHE);
			}
			return cachedModel;
		}
		finally
		{
			lock.unlock();
		}
	}

	protected boolean isRelatedObjectReadOnly(final String configId, final ConfigurationRetrievalOptions retrievalOptions)
	{
		boolean required = false;

		final ProductConfigurationRelatedObjectType assignedTo = retrieveRelatedObjectType(configId, retrievalOptions);

		if (assignedTo == ProductConfigurationRelatedObjectType.ORDER_ENTRY
				|| assignedTo == ProductConfigurationRelatedObjectType.QUOTE_ENTRY
				|| assignedTo == ProductConfigurationRelatedObjectType.SAVEDCART_ENTRY)
		{
			required = true;
		}
		return required;
	}

	protected ProductConfigurationRelatedObjectType retrieveRelatedObjectType(final String configId,
			final ConfigurationRetrievalOptions retrievalOptions)
	{
		final ProductConfigurationRelatedObjectType assignedTo;
		if (retrievalOptions != null && retrievalOptions.getRelatedObjectType() != null)
		{
			assignedTo = retrievalOptions.getRelatedObjectType();
		}
		else
		{
			assignedTo = getAssignmentResolverStrategy().retrieveRelatedObjectType(configId);
		}
		return assignedTo;
	}

	protected ConfigurationRetrievalOptions retrieveCorrectPricingDate(final String configId)
	{
		if (isRelatedObjectReadOnly(configId, null))
		{
			final ConfigurationRetrievalOptions options = new ConfigurationRetrievalOptions();
			//use past pricing date
			options.setPricingDate(getAssignmentResolverStrategy().retrieveCreationDateForRelatedEntry(configId));
			return options;
		}
		//use current pricing date
		return null;
	}

	protected ConfigModel retrieveConfigurationModelFromConfigurationEngine(final String configId,
			final ConfigurationRetrievalOptions options)
	{
		try
		{
			final ConfigModel configModel;
			if (options == null)
			{
				configModel = getConfigLifecycleStrategy().retrieveConfigurationModel(configId);
			}
			else
			{
				configModel = getConfigLifecycleStrategy().retrieveConfigurationModel(configId, options);
			}

			//this is needed as AssignmentResolverStrategy accesses the cache
			cacheConfig(configModel);
			final String productCode = getAssignmentResolverStrategy().retrieveRelatedProductCode(configId);

			if (productCode != null)
			{
				updateKbKeyWithProductCode(configModel, productCode);
			}

			return configModel;
		}
		catch (final ConfigurationEngineException ex)
		{
			cleanUpAfterEngineError(configId);
			throw new IllegalStateException(RETRIEVING_CONFIGURATION_FAILED, ex);
		}
	}

	protected void updateKbKeyForVariants(final ConfigModel configModel, final String baseProductCode, final String variantCode)
	{
		updateKbKeyWithProductCode(configModel, isChangeableVariant(variantCode) ? variantCode : baseProductCode);
	}

	protected boolean isChangeableVariant(final String variantCode)
	{
		final ProductModel variantModel = getConfigurationProductUtil().getProductForCurrentCatalog(variantCode);
		return variantModel != null && getCpqConfigurableChecker().isCPQChangeableVariantProduct(variantModel);
	}

	protected void updateKbKeyWithProductCode(final ConfigModel configModel, final String productCode)
	{
		final KBKey oldKey = configModel.getKbKey();
		configModel.setKbKey(
				new KBKeyImpl(productCode, oldKey.getKbName(), oldKey.getKbLogsys(), oldKey.getKbVersion(), oldKey.getDate()));
	}

	protected void cleanUpAfterEngineError(final String configId)
	{
		getConfigModelCacheStrategy().purge();
		removeConfigAttributesFromCache(configId);
	}

	@Override
	public String retrieveExternalConfiguration(final String configId)
	{
		checkReadAllowed(configId);
		final ReentrantLock lock = getLock(configId);
		lock.lock();

		try
		{
			return getConfigLifecycleStrategy().retrieveExternalConfiguration(configId);
		}
		catch (final ConfigurationEngineException e)
		{
			cleanUpAfterEngineError(configId);
			throw new IllegalStateException("Retrieving external configuration failed", e);
		}
		finally
		{
			lock.unlock();
		}

	}

	/**
	 * @param providerFactory
	 *           inject factory to access the providers
	 */
	@Required
	public void setProviderFactory(final ProviderFactory providerFactory)
	{
		this.providerFactory = providerFactory;
	}

	/**
	 * A configuration provider lock ensures, that there are no concurrent requests send to the configuration engine for
	 * the same configuration session.<br>
	 * We might not always get informed when a configuration session is released, hence we do not rely on this. Instead
	 * we just keep a maximum number of locks and release the oldest locks, when there are to many. The maximum number
	 * can be configured by this setter. <br>
	 * A look can be re-created in case it had already been deleted. The number should be high enough, so that locks do
	 * not get deleted while some concurrent threads are still using the lock, as this could cause concurrency issue.
	 * <b>The maximum number heavily depends on the number of concurrent threads expected.</b> Default is 1024.
	 *
	 * @param maxLocksPerMap
	 *           sets the maximum number of Configuration Provider Locks kept.
	 */
	public static void setMaxLocksPerMap(final int maxLocksPerMap)
	{
		ProductConfigurationServiceImpl.maxLocksPerMap = maxLocksPerMap;
	}

	protected static int getMaxLocksPerMap()
	{
		return ProductConfigurationServiceImpl.maxLocksPerMap;
	}

	protected static ReentrantLock getLock(final String configId)
	{
		synchronized (PROVIDER_LOCK)
		{

			ReentrantLock lock = locks.get(configId);
			if (lock == null)
			{
				lock = oldLocks.get(configId);
				if (lock == null)
				{
					ensureThatLockMapIsNotTooBig();
					lock = new ReentrantLock();
					locks.put(configId, lock);
				}
			}
			return lock;
		}
	}

	protected static void ensureThatLockMapIsNotTooBig()
	{
		if (locks.size() >= maxLocksPerMap)
		{
			oldLocks.clear();
			oldLocks = locks;
			// avoid rehashing, create with sufficient capacity
			locks = new HashMap<>((int) (maxLocksPerMap / MAP_INITIAL_CAPACITY_CALCULATE_DIVISOR + 1));
		}
	}

	protected ConfigModel afterDefaultConfigCreated(final ConfigModel config)
	{
		cacheConfig(config);
		return config;
	}

	protected ConfigModel afterConfigCreated(final ConfigModel config, final ConfigurationRetrievalOptions retrievalOptions)
	{
		cacheConfig(config);
		return config;
	}

	@Override
	public ConfigModel createConfigurationFromExternal(final KBKey kbKey, final String externalConfiguration)
	{
		return createConfigurationFromExternal(kbKey, externalConfiguration, null);
	}

	@Override
	public ConfigModel createConfigurationFromExternal(final KBKey kbKey, final String externalConfiguration,
			final String cartEntryKey)
	{
		return createConfigurationFromExternal(kbKey, externalConfiguration, cartEntryKey, null);
	}

	@Override
	public ConfigModel createConfigurationFromExternal(final KBKey kbKey, final String externalConfiguration,
			final String cartEntryKey, final ConfigurationRetrievalOptions retrievalOptions)
	{

		final ConfigModel config = getConfigLifecycleStrategy().createConfigurationFromExternalSource(kbKey, externalConfiguration);
		updateKbKeyWithProductCode(config, kbKey.getProductCode());
		recorder.recordCreateConfigurationFromExternalSource(config);
		if (null != cartEntryKey)
		{
			getAbstractOrderEntryLinkStrategy().setConfigIdForCartEntry(cartEntryKey, config.getId());
		}

		return afterConfigCreated(config, retrievalOptions);
	}


	@Override
	public ConfigModel createConfigurationFromExternalSource(final Configuration extConfig)
	{
		final ConfigModel config = getConfigLifecycleStrategy().createConfigurationFromExternalSource(extConfig);
		recorder.recordCreateConfigurationFromExternalSource(config);

		return afterConfigCreated(config, null);
	}

	@Override
	public void releaseSession(final String configId)
	{
		releaseSession(configId, false);
	}

	@Override
	public void releaseSession(final String configId, final boolean keepModel)
	{
		checkReleaseAllowed(configId);
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Releasing config session with id " + configId);
		}

		final ReentrantLock lock = ProductConfigurationServiceImpl.getLock(configId);
		lock.lock();
		try
		{
			getConfigLifecycleStrategy().releaseSession(configId);
			if (!keepModel)
			{
				removeConfigAttributesFromCache(configId);
			}

			synchronized (PROVIDER_LOCK)
			{
				locks.remove(configId);
				oldLocks.remove(configId);
			}
		}
		finally
		{
			lock.unlock();
		}
	}

	protected void checkReleaseAllowed(final String configId)
	{
		if (!getProductConfigurationAccessControlService().isReleaseAllowed(configId))
		{
			throw new ProductConfigurationAccessException(NOT_ALLOWED_TO_RELEASE_CONFIGURATION);
		}
	}

	protected void removeConfigAttributesFromCache(final String configId)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Removing config with id '" + configId + "' from cache");
		}

		getConfigModelCacheStrategy().removeConfigAttributeState(configId);
	}

	protected void cacheConfig(final ConfigModel config)
	{
		getConfigModelCacheStrategy().setConfigurationModelEngineState(config.getId(), config);

		if (LOG.isDebugEnabled())
		{
			LOG.debug(DEBUG_CONFIG_WITH_ID + config.getId() + "' caching it for further access");
		}
	}

	protected ProviderFactory getProviderFactory()
	{
		return providerFactory;
	}

	protected ConfigurationLifecycleStrategy getConfigLifecycleStrategy()
	{
		return configLifecycleStrategy;
	}

	@Required
	public void setConfigLifecycleStrategy(final ConfigurationLifecycleStrategy configLifecycleStrategy)
	{
		this.configLifecycleStrategy = configLifecycleStrategy;
	}

	protected ConfigurationModelCacheStrategy getConfigModelCacheStrategy()
	{
		return configModelCacheStrategy;
	}

	@Required
	public void setConfigModelCacheStrategy(final ConfigurationModelCacheStrategy configModelCacheStrategy)
	{
		this.configModelCacheStrategy = configModelCacheStrategy;
	}

	protected ConfigurationAbstractOrderEntryLinkStrategy getAbstractOrderEntryLinkStrategy()
	{
		return configurationAbstractOrderEntryLinkStrategy;
	}

	@Required
	public void setAbstractOrderEntryLinkStrategy(
			final ConfigurationAbstractOrderEntryLinkStrategy configurationAbstractOrderEntryLinkStrategy)
	{
		this.configurationAbstractOrderEntryLinkStrategy = configurationAbstractOrderEntryLinkStrategy;
	}

	@Override
	public int calculateNumberOfIncompleteCsticsAndSolvableConflicts(final String configId)
	{
		final ConfigModel configurationModel = retrieveConfigurationOverview(configId);

		return countNumberOfIncompleteCstics(configurationModel.getRootInstance())
				+ countNumberOfSolvableConflicts(configurationModel);

	}


	@Override
	public int countNumberOfIncompleteCstics(final InstanceModel rootInstance)
	{
		int numberOfErrors = 0;
		for (final InstanceModel subInstace : rootInstance.getSubInstances())
		{
			numberOfErrors += countNumberOfIncompleteCstics(subInstace);
		}
		for (final CsticModel cstic : rootInstance.getCstics())
		{
			if (cstic.isRequired() && !cstic.isComplete())
			{
				numberOfErrors += countNumberOfIncompleteVisibleCstics(cstic);
			}
		}
		return numberOfErrors;
	}

	protected int countNumberOfIncompleteVisibleCstics(final CsticModel cstic)
	{
		if (cstic.isVisible())
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Mandatory Cstic missing: " + cstic.getName());
			}
			return 1;
		}
		else
		{
			LOG.warn("The mandatory CSTIC '" + cstic.getName() + "' is not complete, which is not visible to the user.");
		}
		return 0;
	}

	protected int countNumberOfNotConsistentCstics(final InstanceModel instance)
	{
		int result = (int) instance.getCstics().stream().filter(cstic -> !cstic.isConsistent()).count();

		for (final InstanceModel subInstance : instance.getSubInstances())
		{
			result += countNumberOfNotConsistentCstics(subInstance);
		}

		return result;
	}


	@Override
	public int countNumberOfSolvableConflicts(final ConfigModel configModel)
	{
		int result = 0;
		final List<SolvableConflictModel> solvableConflicts = configModel.getSolvableConflicts();
		if (!CollectionUtils.isEmpty(solvableConflicts))
		{
			return solvableConflicts.size();
		}

		if (!configModel.isConsistent())
		{
			result = countNumberOfNotConsistentCstics(configModel.getRootInstance());
		}

		return result;
	}

	@Override
	public ConfigModel createConfigurationForVariant(final String baseProductCode, final String variantProductCode)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug(
					"create variant configuration for base product " + baseProductCode + " of product variant " + variantProductCode);
		}
		final ConfigModel configModel = getConfigLifecycleStrategy().retrieveConfigurationFromVariant(baseProductCode,
				variantProductCode);
		updateKbKeyForVariants(configModel, baseProductCode, variantProductCode);
		recorder.recordCreateConfigurationForVariant(configModel, baseProductCode, variantProductCode);

		if (isChangeableVariant(variantProductCode))
		{
			return afterDefaultConfigCreated(configModel);
		}
		else
		{
			return afterConfigCreated(configModel, null);
		}
	}

	protected TrackingRecorder getRecorder()
	{
		return recorder;
	}

	/**
	 * @param recorder
	 *           inject the CPQ tracking recorder for tracking CPQ events
	 */
	@Required
	public void setRecorder(final TrackingRecorder recorder)
	{
		this.recorder = recorder;
	}

	@Override
	public boolean hasKbForDate(final String productCode, final Date kbDate)
	{
		return getConfigurationProvider().isKbForDateExists(productCode, kbDate);
	}


	@Override
	public boolean isKbVersionValid(final KBKey kbKey)
	{
		return getConfigurationProvider().isKbVersionValid(kbKey);
	}

	protected ConfigurationProvider getConfigurationProvider()
	{
		return getProviderFactory().getConfigurationProvider();
	}

	@Override
	public int getTotalNumberOfIssues(final ConfigModel configModel)
	{
		return countNumberOfIncompleteCstics(configModel.getRootInstance()) + countNumberOfSolvableConflicts(configModel);
	}

	@Override
	public KBKey extractKbKey(final String productCode, final String externalConfig)
	{
		return getConfigurationProvider().extractKbKey(productCode, externalConfig);
	}

	/**
	 * @param productConfigurationAccessControlService
	 */
	@Required
	public void setProductConfigurationAccessControlService(
			final ProductConfigurationAccessControlService productConfigurationAccessControlService)
	{
		this.productConfigurationAccessControlService = productConfigurationAccessControlService;

	}

	protected ProductConfigurationAccessControlService getProductConfigurationAccessControlService()
	{
		return productConfigurationAccessControlService;
	}


	protected ConfigurationAssignmentResolverStrategy getAssignmentResolverStrategy()
	{
		return assignmentResolverStrategy;
	}

	@Required
	public void setAssignmentResolverStrategy(final ConfigurationAssignmentResolverStrategy assignmentResolverStrategy)
	{
		this.assignmentResolverStrategy = assignmentResolverStrategy;
	}

	protected CPQConfigurableChecker getCpqConfigurableChecker()
	{
		return this.cpqConfigurableChecker;
	}

	@Required
	public void setCpqConfigurableChecker(final CPQConfigurableChecker cpqConfigurableChecker)
	{
		this.cpqConfigurableChecker = cpqConfigurableChecker;
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

	protected ConfigModel retrieveConfigurationModelFromConfigurationEngine(final String configId, final String groupId,
			final boolean overviewOnly, final ConfigurationRetrievalOptions options)
	{
		try
		{
			final ConfigModel configModel = getConfigLifecycleStrategy().retrieveConfigurationModel(configId, groupId, overviewOnly,
					options);

			//this is needed as AssignmentResolverStrategy accesses the cache
			cacheConfig(configModel);
			final String productCode = getAssignmentResolverStrategy().retrieveRelatedProductCode(configId);

			if (productCode != null)
			{
				updateKbKeyWithProductCode(configModel, productCode);
			}

			return configModel;
		}
		catch (final ConfigurationEngineException ex)
		{
			cleanUpAfterEngineError(configId);
			throw new IllegalStateException(RETRIEVING_CONFIGURATION_FAILED, ex);
		}
	}

	@Override
	public ConfigModel retrieveConfigurationModel(final String configId, final String currentGroup)
	{
		return retrieveConfigurationModel(configId, currentGroup, false);
	}

	protected ConfigModel retrieveConfigurationModel(final String configId, final String currentGroup, final boolean overviewOnly)
	{
		final String sanitizedConfigId = YSanitizer.sanitize(configId);
		checkReadAllowed(sanitizedConfigId);
		final ReentrantLock lock = ProductConfigurationServiceImpl.getLock(sanitizedConfigId);
		lock.lock();
		try
		{
			ConfigModel cachedModel = getConfigModelCacheStrategy().getConfigurationModelEngineState(sanitizedConfigId);
			final ConfigurationRetrievalOptions options = retrieveCorrectPricingDate(sanitizedConfigId);
			if (cachedModel == null)
			{
				cachedModel = retrieveConfigurationModelFromConfigurationEngineScenarioBased(sanitizedConfigId, currentGroup,
						overviewOnly, options);
				adjustConfigurationModel(cachedModel, null, overviewOnly);
				cacheConfig(cachedModel);
				recorder.recordConfigurationStatus(cachedModel);
			}
			else if (isEnrichModelWithGroupRequired(currentGroup, overviewOnly, cachedModel))
			{
				LOG.debug(DEBUG_CONFIG_WITH_ID + sanitizedConfigId + RETRIEVED_FROM_CACHE);
				//if we haven't read the data for the requested group, we need to do so now, in case
				//config model tells us to do so and we need this not for the configuration overview only
				try
				{
					getConfigurationProvider().enrichModelWithGroup(cachedModel, currentGroup);
					adjustConfigurationModel(cachedModel, null, overviewOnly);
					cacheConfig(cachedModel);
				}
				catch (final ConfigurationEngineException ex)
				{
					cleanUpAfterEngineError(configId);
					throw new IllegalStateException(RETRIEVING_CONFIGURATION_FAILED, ex);
				}
			}
			else
			{
				LOG.debug(DEBUG_CONFIG_WITH_ID + sanitizedConfigId + RETRIEVED_FROM_CACHE);
			}
			return cachedModel;
		}
		finally
		{
			lock.unlock();
		}
	}

	@Override
	public ConfigModel retrieveConfigurationOverview(final String configId)
	{
		return retrieveConfigurationModel(configId, null, true);
	}

	/**
	 * @return the readDomainValuesOnDemand
	 */
	protected boolean isReadDomainValuesOnDemand()
	{
		return readDomainValuesOnDemand;
	}

	/**
	 * @param readDomainValuesOnDemand
	 *           the readDomainValuesOnDemand to set
	 */
	public void setReadDomainValuesOnDemand(final boolean readDomainValuesOnDemand)
	{
		this.readDomainValuesOnDemand = readDomainValuesOnDemand;
	}

	protected void adjustConfigurationModel(final ConfigModel config, final String productCode, final boolean overviewOnly)
	{
		// not action needed
	}

	protected ConfigModel retrieveConfigurationModelFromConfigurationEngineScenarioBased(final String configId,
			final String currentGroup, final boolean overviewOnly, final ConfigurationRetrievalOptions options)
	{
		return isReadDomainValuesOnDemandSupported()
				? retrieveConfigurationModelFromConfigurationEngine(configId, currentGroup, overviewOnly, options)
				: retrieveConfigurationModelFromConfigurationEngine(configId, options);
	}

	protected boolean isReadDomainValuesOnDemandSupported()
	{
		return isReadDomainValuesOnDemand() && getConfigurationProvider().isReadDomainValuesOnDemandSupported();
	}

	protected boolean isEnrichModelWithGroupRequired(final String currentGroup, final boolean overviewOnly,
			final ConfigModel configModel)
	{
		//'CONFLICT' represents GroupType.CONFLICT 
		// A group with this prefix is a conflict group, others start with an instance ID 
		final var isConflictGroup = currentGroup != null && currentGroup.startsWith("CONFLICT");
		final var isInteractiveAndGroupNotRead = !overviewOnly
				&& (currentGroup == null || !configModel.getGroupsReadCompletely().contains(currentGroup));
		return !isConflictGroup && isReadDomainValuesOnDemandSupported() && isInteractiveAndGroupNotRead;
	}

}
