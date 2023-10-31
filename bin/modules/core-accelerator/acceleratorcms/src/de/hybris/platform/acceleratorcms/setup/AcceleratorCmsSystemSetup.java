/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorcms.setup;

import java.util.Collections;
import java.util.List;

import de.hybris.platform.acceleratorcms.constants.AcceleratorCmsConstants;
import de.hybris.platform.cms2.servicelayer.services.CMSSyncSearchRestrictionService;
import de.hybris.platform.commerceservices.setup.AbstractSystemSetup;
import de.hybris.platform.core.initialization.SystemSetup;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.core.initialization.SystemSetupParameter;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaModel;

@SystemSetup(extension = AcceleratorCmsConstants.EXTENSIONNAME)
public class AcceleratorCmsSystemSetup extends AbstractSystemSetup
{
	private final CMSSyncSearchRestrictionService cmsSyncSearchRestrictionService;

	public AcceleratorCmsSystemSetup(final CMSSyncSearchRestrictionService cmsSyncSearchRestrictionService)
	{
		this.cmsSyncSearchRestrictionService = cmsSyncSearchRestrictionService;
	}

	@Override
	public List<SystemSetupParameter> getInitializationOptions()
	{
		return Collections.emptyList();
	}

	@SystemSetup(type = SystemSetup.Type.ESSENTIAL, process = SystemSetup.Process.ALL)
	public void createEssentialData(final SystemSetupContext context)
	{
		importImpexFile(context, "/acceleratorcms/import/essential-data.impex", true);
	}

	/**
	 * In CMS ContentCatalog synchronization, some changes which are not approved by mananger will not allowed to sync to online.
	 * It use SearchRestrictin to stop the unapproved change to sync to online.
	 * This function will create searchRestrictions on MediaContainer and Media, others like searchRestriction of AbstractCMSComponent are created in cms2.
	 * Those SearchRestrictions' principal is 'cmssyncuser' which only be used as session user of cms content catalog synchronization.
	 */
	@SystemSetup(type = SystemSetup.Type.ESSENTIAL, process = SystemSetup.Process.ALL, patch = true, required = true)
	public void createSyncSearchRestrictions()
	{
		getCmsSyncSearchRestrictionService()
				.createCmsSyncSearchRestriction("Sync_Only_Approved_Media_Container_Restriction", MediaContainerModel.class,
						// Get only media containers that are used by approved AbstractMediaContainerComponent and approved VideoComponent (search restrictions for AbstractCMSComponent type is implicitly applied)
						" {item:pk} IN ({{ SELECT {mediaComponent:media[ANY]} FROM {AbstractMediaContainerComponent AS mediaComponent} }})" +
						" OR {item:pk} IN ({{ SELECT {vc:thumbnail[ANY]} FROM {VideoComponent AS vc} }})");
		getCmsSyncSearchRestrictionService()
				.createCmsSyncSearchRestriction("Sync_Only_Approved_Media_Restriction", MediaModel.class,
				// Get all media that is not used by a media container
				" {item:mediaContainer} IS NULL " +
						// Get only media that is used by approved MediaContainer (search restrictions for MediaContainer type is implicitly applied)
						"OR EXISTS ({{ SELECT 1 FROM {MediaContainer AS mc} WHERE {item:mediaContainer} = {mc.pk} }})");
	}

	protected CMSSyncSearchRestrictionService getCmsSyncSearchRestrictionService()
	{
		return cmsSyncSearchRestrictionService;
	}
}
