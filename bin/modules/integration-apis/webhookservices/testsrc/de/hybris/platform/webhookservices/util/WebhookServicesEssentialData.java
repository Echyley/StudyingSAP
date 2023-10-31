/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.webhookservices.util;

import static de.hybris.platform.outboundservices.util.OutboundServicesEssentialData.outboundServicesEssentialData;

import de.hybris.platform.apiregistryservices.model.DestinationTargetModel;
import de.hybris.platform.apiregistryservices.model.events.EventConfigurationModel;
import de.hybris.platform.integrationservices.util.IntegrationTestUtil;
import de.hybris.platform.integrationservices.util.impex.EssentialDataFile;
import de.hybris.platform.integrationservices.util.impex.ModuleEssentialData;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

/**
 * A utility for loading and cleaning data in all essentialdata impex files inside webhookservices module.
 */
public class WebhookServicesEssentialData extends ModuleEssentialData
{
	private static final List<ModuleEssentialData> DEPENDENCIES = List.of(outboundServicesEssentialData());
	private static final Set<String> ALL_INTEGRATION_OBJECT_CODES = new HashSet<>();
	private static final List<EssentialDataFile> IMPEX_FILES = List.of(new WebhookServicesImpex(), new WebhookServiceApiImpex());
	private static final WebhookServicesEssentialData instance = new WebhookServicesEssentialData();

	private WebhookServicesEssentialData()
	{
		super(IMPEX_FILES, ALL_INTEGRATION_OBJECT_CODES);
	}

	public static WebhookServicesEssentialData webhookServicesEssentialData()
	{
		return instance;
	}

	public WebhookServicesEssentialData withDependencies() {
		enableDependencies();
		return this;
	}

	@Nonnull
	@Override
	protected List<ModuleEssentialData> getDependencies()
	{
		return DEPENDENCIES;
	}

	private static class WebhookServicesImpex extends EssentialDataFile
	{
		private static final String FILE_PATH = "/impex/essentialdata-webhookservices.impex";
		private static final Set<String> TARGETS_IN_FILE = Set.of("webhookServices");
		private static final Set<String> TYPES_GRANTED_IN_FILE = Set.of("WebhookConfiguration", "EventConfiguration");

		private WebhookServicesImpex()
		{
			super(FILE_PATH);
		}

		@Override
		public void cleanData()
		{
			IntegrationTestUtil.remove(EventConfigurationModel.class,
					m -> m.getDestinationTarget() != null && TARGETS_IN_FILE.contains(m.getDestinationTarget().getId()));
			IntegrationTestUtil.remove(DestinationTargetModel.class, m -> TARGETS_IN_FILE.contains(m.getId()));
			clearAccessRightsForTypes(TYPES_GRANTED_IN_FILE);
		}
	}

	private static class WebhookServiceApiImpex extends EssentialDataFile
	{
		private static final String FILE_PATH = "/impex/essentialdata-webhookservice-api.impex";
		private static final Set<String> IOs_IN_FILE = registerIntegrationObjects(ALL_INTEGRATION_OBJECT_CODES, "WebhookService");
		private static final Set<String> TYPES_GRANTED_IN_FILE = Set.of(
				"BasicCredential", "ConsumedDestination", "ConsumedOAuthCredential", "DestinationChannel", "DestinationTarget",
				"Endpoint", "EventConfiguration", "IntegrationObject", "WebhookConfiguration");
		private WebhookServiceApiImpex()
		{
			super(FILE_PATH);
		}

		@Override
		public void cleanData()
		{
			deleteInboundChannelConfigurations(IOs_IN_FILE);
			deleteIntegrationObjects(IOs_IN_FILE);
			clearAccessRightsForTypes(TYPES_GRANTED_IN_FILE);
		}
	}

}
