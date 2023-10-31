/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.indexer.service.impl;

import de.hybris.platform.core.Registry;
import de.hybris.platform.core.Tenant;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.searchservices.admin.service.SnIndexTypeService;
import de.hybris.platform.searchservices.core.SnException;
import de.hybris.platform.searchservices.core.service.SnProgressTracker;
import de.hybris.platform.searchservices.enums.SnIndexerOperationType;
import de.hybris.platform.searchservices.indexer.service.SnIndexerBatchCallable;
import de.hybris.platform.searchservices.indexer.service.SnIndexerBatchResponse;
import de.hybris.platform.searchservices.indexer.service.SnIndexerBatchStrategy;
import de.hybris.platform.searchservices.indexer.service.SnIndexerBatchStrategyFactory;
import de.hybris.platform.searchservices.indexer.service.SnIndexerContext;
import de.hybris.platform.searchservices.indexer.service.SnIndexerItemSourceOperation;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.tenant.TenantService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link SnIndexerBatchCallable}.
 */
public class DefaultSnIndexerBatchCallable implements SnIndexerBatchCallable
{
	private TenantService tenantService;
	private SessionService sessionService;
	private UserService userService;
	private CommonI18NService commonI18NService;
	private SnIndexTypeService snIndexTypeService;
	private SnIndexerBatchStrategyFactory snIndexerBatchStrategyFactory;

	private String tenantId;
	private String sessionUser;
	private String sessionLanguage;
	private String sessionCurrency;
	private String indexTypeId;
	private String indexId;
	private SnIndexerOperationType indexerOperationType;
	private List<SnIndexerItemSourceOperation> indexerItemSourceOperations;
	private String indexerOperationId;
	private String indexerBatchId;
	private SnProgressTracker progressTracker;

	@Override
	public void initialize(final SnIndexerContext indexerContext,
			final List<SnIndexerItemSourceOperation> indexerItemSourceOperations, final String indexerBatchId)
	{
		tenantId = tenantService.getCurrentTenantId();

		final UserModel user = userService.getCurrentUser();
		if (user != null)
		{
			sessionUser = user.getUid();
		}

		final LanguageModel language = commonI18NService.getCurrentLanguage();
		if (language != null)
		{
			sessionLanguage = language.getIsocode();
		}

		final CurrencyModel currency = commonI18NService.getCurrentCurrency();
		if (currency != null)
		{
			sessionCurrency = currency.getIsocode();
		}

		this.indexTypeId = indexerContext.getIndexType().getId();
		this.indexId = indexerContext.getIndexId();
		this.indexerOperationType = indexerContext.getIndexerOperationType();
		this.indexerItemSourceOperations = indexerItemSourceOperations;
		this.indexerOperationId = indexerContext.getIndexerOperationId();
		this.indexerBatchId = indexerBatchId;
		this.progressTracker = indexerContext.getIndexerRequest().getProgressTracker();
	}

	@Override
	public SnIndexerBatchResponse call() throws SnException, InterruptedException
	{
		try
		{
			initializeSession();

			final DefaultSnIndexerBatchRequest indexerBatchRequest = new DefaultSnIndexerBatchRequest(indexTypeId, indexId,
					indexerOperationType, indexerItemSourceOperations, indexerOperationId, indexerBatchId, progressTracker);
			final SnIndexerBatchStrategy indexerBatchStrategy = snIndexerBatchStrategyFactory
					.getIndexerBatchStrategy(indexerBatchRequest);
			return indexerBatchStrategy.execute(indexerBatchRequest);
		}
		finally
		{
			destroySession();
		}
	}

	protected void initializeSession()
	{
		final Tenant tenant = Registry.getTenantByID(tenantId);
		Registry.setCurrentTenant(tenant);

		sessionService.createNewSession();

		final UserModel user = userService.getUserForUID(sessionUser);
		userService.setCurrentUser(user);

		final LanguageModel language = commonI18NService.getLanguage(sessionLanguage);
		commonI18NService.setCurrentLanguage(language);

		final CurrencyModel currency = commonI18NService.getCurrency(sessionCurrency);
		commonI18NService.setCurrentCurrency(currency);
	}

	protected void destroySession()
	{
		sessionService.closeCurrentSession();
		Registry.unsetCurrentTenant();
	}

	protected String getTaskName()
	{
		return "Indexer Batch: indexerBatchId=" + indexerBatchId;
	}

	public String getTenantId()
	{
		return tenantId;
	}

	public String getSessionUser()
	{
		return sessionUser;
	}

	public String getSessionLanguage()
	{
		return sessionLanguage;
	}

	public String getSessionCurrency()
	{
		return sessionCurrency;
	}

	public String getIndexTypeId()
	{
		return indexTypeId;
	}

	public String getIndexId()
	{
		return indexId;
	}

	public SnIndexerOperationType getIndexerOperationType()
	{
		return indexerOperationType;
	}

	public List<SnIndexerItemSourceOperation> getIndexerItemSourceOperations()
	{
		return indexerItemSourceOperations;
	}

	public String getIndexerOperationId()
	{
		return indexerOperationId;
	}

	@Override
	public String getIndexerBatchId()
	{
		return indexerBatchId;
	}

	public TenantService getTenantService()
	{
		return tenantService;
	}

	@Required
	public void setTenantService(final TenantService tenantService)
	{
		this.tenantService = tenantService;
	}

	public SessionService getSessionService()
	{
		return sessionService;
	}

	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	public UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	public CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	public SnIndexTypeService getSnIndexTypeService()
	{
		return snIndexTypeService;
	}

	@Required
	public void setSnIndexTypeService(final SnIndexTypeService snIndexTypeService)
	{
		this.snIndexTypeService = snIndexTypeService;
	}

	public SnIndexerBatchStrategyFactory getSnIndexerBatchStrategyFactory()
	{
		return snIndexerBatchStrategyFactory;
	}

	@Required
	public void setSnIndexerBatchStrategyFactory(final SnIndexerBatchStrategyFactory snIndexerBatchStrategyFactory)
	{
		this.snIndexerBatchStrategyFactory = snIndexerBatchStrategyFactory;
	}
}
