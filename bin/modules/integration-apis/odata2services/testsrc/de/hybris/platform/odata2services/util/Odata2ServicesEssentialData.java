/*
 *  Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.odata2services.util;

import static de.hybris.platform.inboundservices.util.InboundServicesEssentialData.inboundServicesEssentialData;

import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.integrationservices.util.IntegrationTestUtil;
import de.hybris.platform.integrationservices.util.impex.EssentialDataFile;
import de.hybris.platform.integrationservices.util.impex.ModuleEssentialData;

import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

/**
 * A utility for loading and cleaning data in all essentialdata impex files inside odata2services module.
 */
public class Odata2ServicesEssentialData extends ModuleEssentialData
{
	private static final List<ModuleEssentialData> DEPENDENCIES = List.of(inboundServicesEssentialData());
	private static final List<EssentialDataFile> IMPEX_FILES = List.of(new OData2ServicesImpex());
	private static final Odata2ServicesEssentialData instance = new Odata2ServicesEssentialData();

	private Odata2ServicesEssentialData()
	{
		super(IMPEX_FILES);
	}

	public static Odata2ServicesEssentialData odata2ServicesEssentialData()
	{
		return instance;
	}

	public Odata2ServicesEssentialData withDependencies()
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

	private static class OData2ServicesImpex extends EssentialDataFile
	{
		private static final String FILE_PATH = "/impex/essentialdata-odata2services.impex";
		private static final Set<String> ROLES_IN_FILE = Set.of("integrationusergroup", "integrationadmingroup",
				"integrationcreategroup", "integrationviewgroup", "integrationdeletegroup");
		private static final Set<String> TYPES_GRANTED_IN_FILE = Set.of("AttributeDescriptor", "BasicCredential",
				"ClassAttributeAssignment", "ClassificationAttribute", "ClassificationClass", "ClassificationSystem",
				"ClassificationSystemVersion", "ComposedType", "ConsumedDestination", "ConsumedOAuthCredential",
				"DestinationTarget", "Endpoint", "InboundChannelConfiguration", "IntegrationObject", "IntegrationObjectItem",
				"IntegrationObjectItemAttribute", "IntegrationObjectItemClassificationAttribute",
				"IntegrationObjectItemVirtualAttribute", "IntegrationObjectVirtualAttributeDescriptor",
				"OutboundChannelConfiguration", "OutboundSyncCronJob", "OutboundSyncJob", "OutboundSyncStreamConfiguration",
				"OutboundSyncStreamConfigurationContainer", "Script", "Trigger", "Type");

		private OData2ServicesImpex()
		{
			super(FILE_PATH);
		}

		@Override
		public void cleanData()
		{
			IntegrationTestUtil.remove(UserGroupModel.class, m -> ROLES_IN_FILE.contains(m.getUid()));
			ModuleEssentialData.clearAccessRightsForTypes(TYPES_GRANTED_IN_FILE);
		}
	}
}
