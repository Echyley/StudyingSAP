/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.configurablebundleservices.bundle.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.commerceservices.order.CommerceRemoveEntryGroupStrategy;
import de.hybris.platform.commerceservices.order.impl.OrderEntryModifiableChecker;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.order.EntryGroup;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.util.Config;

import java.util.Arrays;

import javax.annotation.Nonnull;

import org.springframework.beans.factory.annotation.Required;


/**
 * Tests whether an order/cart entry can be remove or not
 */
public class BundleOrderEntryModifiableChecker extends OrderEntryModifiableChecker
{
	private static final String AUTO_PICK_ENABLED = "configurablebundleservices.autopick.enabled";

	private BundleTemplateService bundleTemplateService;
	private Boolean isAutoPickEnabled;

	@Override
	public boolean canModify(@Nonnull final AbstractOrderEntryModel entryToUpdate)
	{
		validateParameterNotNullStandardMessage("entryToUpdate", entryToUpdate);

		if (!isAutoPickEnabled())
		{
			return super.canModify(entryToUpdate);
		}

		final EntryGroup bundleGroup = bundleTemplateService.getBundleEntryGroup(entryToUpdate);

		if (bundleGroup == null)
		{
			return super.canModify(entryToUpdate);
		}

		if (isCalledFromRemoveEntryGroup())
		{
			return super.canModify(entryToUpdate);
		}

		final BundleTemplateModel component = getComponent(bundleGroup.getExternalReferenceId());
		if (getBundleTemplateService().isAutoPickComponent(component))
		{
			return false;
		}
		return super.canModify(entryToUpdate);
	}

	protected boolean isCalledFromRemoveEntryGroup()
	{
		return Arrays.stream(Thread.currentThread().getStackTrace())
				.anyMatch(this::isClassImplementsCommerceRemoveEntryGroupStrategy);
	}

	private boolean isClassImplementsCommerceRemoveEntryGroupStrategy(StackTraceElement element)
	{
		try
		{
			return Arrays.asList(Class.forName(element.getClassName()).getInterfaces())
				.contains(CommerceRemoveEntryGroupStrategy.class);
		}
		catch (ClassNotFoundException e)
		{
			return false;
		}
	}

	@Nonnull
	protected BundleTemplateModel getComponent(@Nonnull final String componentId)
	{
		final BundleTemplateModel component;
		try
		{
			component = getBundleTemplateService().getBundleTemplateForCode(componentId);
		}
		catch (final ModelNotFoundException e)
		{
			throw new IllegalArgumentException("Bundle template " + componentId + " was not found", e);
		}
		return component;
	}

	protected BundleTemplateService getBundleTemplateService()
	{
		return bundleTemplateService;
	}

	@Required
	public void setBundleTemplateService(final BundleTemplateService bundleTemplateService)
	{
		this.bundleTemplateService = bundleTemplateService;
	}

	protected boolean isAutoPickEnabled()
	{
		if (isAutoPickEnabled == null)
		{
			return Config.getBoolean(AUTO_PICK_ENABLED, false);
		}

		return isAutoPickEnabled;
	}

	protected void setAutoPickEnabled(final boolean autoPickEnabled)
	{
		isAutoPickEnabled = autoPickEnabled;
	}
}
