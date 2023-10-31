/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.services.ProductConfigSessionAttributeContainer;
import de.hybris.platform.sap.productconfig.services.SessionAccessService;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;


/**
 * Default implementation of {@link SessionAccessService}
 */
public class SessionAccessServiceImpl implements SessionAccessService
{
	private static final String TRACE_MESSAGE_FOR_CART_ENTRY = "for cart entry: ";
	private static final String TRACE_MESSAGE_FOR_PRODUCT = "for product: ";
	private static final Logger LOG = Logger.getLogger(SessionAccessServiceImpl.class);
	private SessionService sessionService;


	@Override
	public void setConfigIdForCartEntry(final String cartEntryKey, final String configId)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Put config ID " + configId + " into session for cart entry: " + cartEntryKey);
		}

		final String other = getCartEntryForConfigId(configId);
		if (other != null)
		{
			removeConfigIdForCartEntry(other);
		}
		getCartEntryConfigCache().put(cartEntryKey, configId);
	}

	@Override
	public String getConfigIdForCartEntry(final String cartEntryKey)
	{
		final Map<String, String> sessionConfigCartEntryCache = retrieveSessionAttributeContainer().getCartEntryConfigurations();
		final String configId = sessionConfigCartEntryCache.get(cartEntryKey);

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Get config ID " + configId + " from session for cart entry: " + cartEntryKey);
		}

		return configId;
	}

	@Override
	public String getDraftConfigIdForCartEntry(final String cartEntryKey)
	{
		final Map<String, String> sessionDraftConfigCartEntryCache = retrieveSessionAttributeContainer()
				.getCartEntryDraftConfigurations();
		final String configId = sessionDraftConfigCartEntryCache.get(cartEntryKey);

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Get draft config ID " + configId + " from session for cart entry: " + cartEntryKey);
		}

		return configId;
	}


	@Override
	public <T> T getUiStatusForCartEntry(final String cartEntryKey)
	{
		return getUiStatusFromSession(cartEntryKey, true, TRACE_MESSAGE_FOR_CART_ENTRY);
	}


	/**
	 * Retrieves UiStatus from session
	 *
	 * @param key
	 *           Key of object in map
	 * @param forCart
	 *           true for UI Statuses for cart entries, false for catalog products
	 * @param traceMessage
	 *           Post fix of the trace message which identifies the type of key
	 * @return UiStatus
	 */
	protected <T> T getUiStatusFromSession(final String key, final boolean forCart, final String traceMessage)
	{
		final Map<String, Object> sessionUiStatusCache;
		if (forCart)
		{
			sessionUiStatusCache = retrieveSessionAttributeContainer().getCartEntryUiStatuses();
		}
		else
		{
			sessionUiStatusCache = retrieveSessionAttributeContainer().getProductUiStatuses();
		}
		final Object uiStatus = sessionUiStatusCache.get(key);

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Get UiStatus " + uiStatus + " from session " + traceMessage + key);
		}

		return (T) uiStatus;
	}


	@Override
	public void setUiStatusForCartEntry(final String cartEntryKey, final Object uiStatus)
	{
		setUiStatusIntoSession(cartEntryKey, uiStatus, true, TRACE_MESSAGE_FOR_CART_ENTRY);
	}

	@Override
	public Object getUiStatusForProduct(final String productKey)
	{
		return getUiStatusFromSession(productKey, false, TRACE_MESSAGE_FOR_PRODUCT);
	}


	@Override
	public void setUiStatusForProduct(final String productKey, final Object uiStatus)
	{
		setUiStatusIntoSession(productKey, uiStatus, false, TRACE_MESSAGE_FOR_PRODUCT);
	}

	/**
	 * Puts UiStatus object into session
	 *
	 * @param key
	 *           Key for object
	 * @param uiStatus
	 *           The object we want to store in session
	 * @param forCart
	 *           true for UI Statuses for cart entries, false for catalog products
	 * @param traceMessage
	 *           Post fix of the trace message which identifies the type of key
	 */
	protected void setUiStatusIntoSession(final String key, final Object uiStatus, final boolean forCart,
			final String traceMessage)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Put UiStatus " + uiStatus + " into session " + traceMessage + key);
		}

		final Map<String, Object> sessionUiStatusEntryCache;
		if (forCart)
		{
			sessionUiStatusEntryCache = retrieveSessionAttributeContainer().getCartEntryUiStatuses();
		}
		else
		{
			sessionUiStatusEntryCache = retrieveSessionAttributeContainer().getProductUiStatuses();
		}

		sessionUiStatusEntryCache.put(key, uiStatus);
	}


	@Override
	public void removeUiStatusForCartEntry(final String cartEntryKey)
	{
		removeUiStatusFromSession(cartEntryKey, true, TRACE_MESSAGE_FOR_CART_ENTRY);
	}

	/**
	 * Removes UiStatus object from session
	 *
	 * @param key
	 *           Key for object
	 * @param forCart
	 *           true for UI Statuses for cart entries, false for catalog products
	 * @param traceMessage
	 *           Post fix of the trace message which identifies the type of key
	 */
	protected void removeUiStatusFromSession(final String key, final boolean forCart, final String traceMessage)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Remove UiStatus from session " + traceMessage + key);
		}

		final Map<String, Object> uiStatusMap;
		if (forCart)
		{
			uiStatusMap = retrieveSessionAttributeContainer().getCartEntryUiStatuses();
		}
		else
		{
			uiStatusMap = retrieveSessionAttributeContainer().getProductUiStatuses();
		}

		if (!MapUtils.isEmpty(uiStatusMap))
		{
			uiStatusMap.remove(key);
		}
		else
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Map does not exist in session");
			}
		}
	}

	@Override
	public void removeUiStatusForProduct(final String productKey)
	{
		removeUiStatusFromSession(productKey, false, TRACE_MESSAGE_FOR_PRODUCT);
	}

	@Override
	public String getCartEntryForConfigId(final String configId)
	{
		final Map<String, String> sessionCartEntryConfigurations = retrieveSessionAttributeContainer().getCartEntryConfigurations();

		final List<String> matches = findConfigIdInMap(configId, sessionCartEntryConfigurations);

		if (matches.size() > 1)
		{
			throw new IllegalStateException("Multiple matches for configuration: " + configId);
		}
		if (!matches.isEmpty())
		{
			final String cartEntryKey = matches.get(0);
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Get cart entry key " + cartEntryKey + " from session for config ID" + configId);
			}
			return cartEntryKey;
		}

		return null;
	}

	@Override
	public String getCartEntryForDraftConfigId(final String configId)
	{
		final Map<String, String> sessionCartEntryDraftConfigurations = retrieveSessionAttributeContainer()
				.getCartEntryDraftConfigurations();

		final List<String> matches = findConfigIdInMap(configId, sessionCartEntryDraftConfigurations);

		if (matches.size() > 1)
		{
			throw new IllegalStateException("Multiple matches for draft configuration: " + configId);
		}
		if (!matches.isEmpty())
		{
			final String cartEntryKey = matches.get(0);
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Get cart entry key " + cartEntryKey + " from session for draft config ID" + configId);
			}
			return cartEntryKey;
		}

		return null;
	}


	protected List<String> findConfigIdInMap(final String configId, final Map<String, String> sessionCartEntryConfigurations)
	{
		final List<String> matches = sessionCartEntryConfigurations.entrySet().stream()//
				.filter(entry -> entry.getValue() != null ? entry.getValue().equals(configId) : (configId == null))//
				.map(entry -> entry.getKey())//
				.collect(Collectors.toList());
		return matches;
	}


	@Override
	public void removeSessionArtifactsForCartEntry(final String cartEntryId)
	{
		// consider draft as well
		final String configId = getConfigIdForCartEntry(cartEntryId);
		final String draftConfigId = getDraftConfigIdForCartEntry(cartEntryId);

		//remove configuration ID if needed
		removeConfigIdForCartEntry(cartEntryId);

		//remove draft configuration ID if needed
		removeDraftConfigIdForCartEntry(cartEntryId);

		//remove UI status attached to cart entry
		removeUiStatusForCartEntry(cartEntryId);

		//check if configuration & draft configuration are maintained at product level also
		removeProductRelatedSessionArtifacts(configId);
		removeProductRelatedSessionArtifacts(draftConfigId);
	}


	protected void removeProductRelatedSessionArtifacts(final String configId)
	{
		if (null != configId)
		{
			String productKey = null;
			for (final Entry<String, String> entry : retrieveSessionAttributeContainer().getProductConfigurations().entrySet())
			{
				// consider draft as well
				if (configId.equals(entry.getValue()))
				{
					productKey = entry.getKey();
					break;
				}
			}
			if (null != productKey)
			{
				removeUiStatusForProduct(productKey);
				removeConfigIdForProduct(productKey);
			}
		}
	}


	@Override
	public void removeConfigIdForCartEntry(final String cartEntryKey)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Remove config ID for cart entry: " + cartEntryKey);
		}

		getCartEntryConfigCache().remove(cartEntryKey);

	}

	/**
	 * @return Map: Configuration ID's for cart entry
	 */
	protected Map<String, String> getCartEntryConfigCache()
	{
		return retrieveSessionAttributeContainer().getCartEntryConfigurations();
	}

	protected Map<String, String> getCartEntryDraftConfigCache()
	{
		return retrieveSessionAttributeContainer().getCartEntryDraftConfigurations();
	}


	protected ProductConfigSessionAttributeContainer retrieveSessionAttributeContainer()
	{
		return retrieveSessionAttributeContainer(true);
	}

	protected ProductConfigSessionAttributeContainer retrieveSessionAttributeContainer(final boolean createLazy)
	{

		synchronized (getSessionService().getCurrentSession())
		{
			ProductConfigSessionAttributeContainer attributeContainer = getSessionService()
					.getAttribute(PRODUCT_CONFIG_SESSION_ATTRIBUTE_CONTAINER);
			if (attributeContainer == null && createLazy)
			{
				attributeContainer = new ProductConfigSessionAttributeContainer();
				getSessionService().setAttribute(PRODUCT_CONFIG_SESSION_ATTRIBUTE_CONTAINER, attributeContainer);
			}
			return attributeContainer;
		}
	}


	@Override
	public void purge()
	{
		getSessionService().setAttribute(SessionAccessServiceImpl.PRODUCT_CONFIG_SESSION_ATTRIBUTE_CONTAINER, null);
	}

	/**
	 * @param sessionService
	 *           the sessionService to set
	 */
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}


	protected SessionService getSessionService()
	{
		return sessionService;
	}


	@Override
	public String getConfigIdForProduct(final String productCode)
	{
		final Map<String, String> sessionProductConfigurationsCache = retrieveSessionAttributeContainer()
				.getProductConfigurations();

		final String configId = sessionProductConfigurationsCache.get(productCode);
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Get config ID " + configId + " from session for product.");
		}

		return configId;
	}


	@Override
	public void setConfigIdForProduct(final String productCode, final String configId)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Put cartEntryId " + configId + " into session for product: " + configId);
		}
		retrieveSessionAttributeContainer().getProductConfigurations().put(productCode, configId);
	}

	@Override
	public void removeConfigIdForProduct(final String pCode)
	{
		retrieveSessionAttributeContainer().getProductConfigurations().remove(pCode);
	}


	@Override
	public void setDraftConfigIdForCartEntry(final String cartEntryKey, final String configId)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Put draft config ID " + configId + " into session for cart entry: " + cartEntryKey);
		}
		getCartEntryDraftConfigCache().put(cartEntryKey, configId);

	}


	@Override
	public void removeDraftConfigIdForCartEntry(final String cartEntryKey)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Remove draft config ID for cart entry: " + cartEntryKey);
		}

		getCartEntryDraftConfigCache().remove(cartEntryKey);
	}

	@Override
	public String getProductForConfigId(final String configId)
	{
		if (configId != null)
		{
			final Map<String, String> sessionProductConfigurationsCache = retrieveSessionAttributeContainer()
					.getProductConfigurations();
			final Optional<Entry<String, String>> productConfigIdPair = sessionProductConfigurationsCache.entrySet().stream()
					.filter(element -> configId.equals(element.getValue())).findFirst();
			if (productConfigIdPair.isPresent())
			{
				return productConfigIdPair.get().getKey();
			}
		}
		return null;
	}

}
