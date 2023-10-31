/*
 *  Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.odata2webservices.util;

import static de.hybris.platform.odata2services.util.Odata2ServicesEssentialData.odata2ServicesEssentialData;

import de.hybris.platform.core.model.type.SearchRestrictionModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.integrationservices.util.IntegrationTestUtil;
import de.hybris.platform.integrationservices.util.impex.EssentialDataFile;
import de.hybris.platform.integrationservices.util.impex.ModuleEssentialData;

import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

/**
 * A utility for loading and cleaning data in all essentialdata impex files inside odata2webservices module.
 */
public class Odata2WebServicesEssentialData extends ModuleEssentialData
{
	private static final List<ModuleEssentialData> DEPENDENCIES = List.of(odata2ServicesEssentialData());
	private static final List<EssentialDataFile> IMPEX_FILES = List.of(new OData2WebservicesImpex());
	private static final Odata2WebServicesEssentialData instance = new Odata2WebServicesEssentialData();

	private Odata2WebServicesEssentialData()
	{
		super(IMPEX_FILES, Set.of("IntegrationService"));
	}

	public static Odata2WebServicesEssentialData odata2WebservicesEssentialData()
	{
		return instance;
	}

	public Odata2WebServicesEssentialData withDependencies()
	{
		enableDependencies();
		return this;
	}

	@Nonnull
	@Override
	protected List<ModuleEssentialData> getDependencies()
	{
		return DEPENDENCIES;
	}

	private static class OData2WebservicesImpex extends EssentialDataFile
	{
		private static final String FILE_PATH = "/impex/essentialdata-odata2webservices.impex";
		private static final Set<String> ROLES_IN_FILE = Set.of("integrationmonitoringgroup", "integrationservicegroup",
				"outboundsyncgroup", "scriptservicegroup");
		private static final Set<String> TYPES_GRANTED_IN_FILE = Set.of("HttpMethod", "InboundRequest", "InboundRequestError",
				"InboundUser", "IntegrationRequestStatus", "OutboundRequest", "OutboundSource", "User");
		private static final Set<String> SEARCH_RESTRICTIONS_IN_FILE = Set.of("inboundMonitoringIntegrationVisibility",
				"outboundMonitoringIntegrationVisibility", "integrationServiceVisibility", "outboundChannelConfigVisibility",
				"scriptServiceVisibility", "webhookServiceVisibility", "runtimeAttributeVisibility");

		private OData2WebservicesImpex()
		{
			super(FILE_PATH);
		}

		@Override
		public void cleanData()
		{
			IntegrationTestUtil.remove(SearchRestrictionModel.class, m -> SEARCH_RESTRICTIONS_IN_FILE.contains(m.getCode()));
			IntegrationTestUtil.remove(UserGroupModel.class, m -> ROLES_IN_FILE.contains(m.getUid()));
			clearAccessRightsForTypes(TYPES_GRANTED_IN_FILE);
		}
	}
}
