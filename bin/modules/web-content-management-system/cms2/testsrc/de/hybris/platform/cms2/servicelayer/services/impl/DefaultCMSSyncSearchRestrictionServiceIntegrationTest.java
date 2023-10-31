/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cms2.servicelayer.services.impl;

import static org.fest.assertions.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cms2.enums.CmsApprovalStatus;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.relations.ContentSlotForPageModel;
import de.hybris.platform.cms2.model.restrictions.AbstractRestrictionModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSyncSearchRestrictionService;
import de.hybris.platform.core.model.enumeration.EnumerationValueModel;
import de.hybris.platform.core.model.type.SearchRestrictionModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.type.TypeService;


import javax.annotation.Resource;

import org.junit.Test;


@IntegrationTest
public class DefaultCMSSyncSearchRestrictionServiceIntegrationTest extends ServicelayerTransactionalTest
{
	@Resource
	private CMSSyncSearchRestrictionService cmsSyncSearchRestrictionService;
	@Resource
	private TypeService typeService;
	private static String SYNCHRONIZATION_BLOCKED_IS_NULL_OR_FALSE = " ( {item:synchronizationBlocked} IS NULL OR {item:synchronizationBlocked} = 0 )";

	@Test
	public void testCreateCmsSyncSearchRestrictionOfAbstractPage()
	{
		EnumerationValueModel approvedStatus = typeService.getEnumerationValue(CmsApprovalStatus._TYPECODE,
				CmsApprovalStatus.APPROVED.getCode());
		final String code = "Sync_Only_Approved_Pages_Restriction";
		final String query = " {item:approvalStatus}=" + approvedStatus.getPk();
		SearchRestrictionModel pageRestriction = cmsSyncSearchRestrictionService
				.createCmsSyncSearchRestriction(code, AbstractPageModel.class, query);

		assertThat(pageRestriction).isNotNull();
		assertThat(pageRestriction.getCode()).isEqualTo(code);
		assertThat(pageRestriction.getQuery()).isEqualTo(query);

		SearchRestrictionModel newpageRestriction = cmsSyncSearchRestrictionService
				.createCmsSyncSearchRestriction(code, AbstractPageModel.class, query);
		assertThat(newpageRestriction).isNull();
	}

	@Test
	public void testCreateCmsSyncSearchRestrictionOfSlotsForPage()
	{
		final String code = "Sync_Only_Approved_Slots_For_Page_Restriction";
		final String query = " EXISTS ({{ SELECT 1 FROM { ContentSlotForPage AS slot4page JOIN AbstractPage AS p ON {p:pk} = {slot4page:page} } WHERE {item:pk} = {slot4page:pk} }})";
		SearchRestrictionModel restriction = cmsSyncSearchRestrictionService
				.createCmsSyncSearchRestriction(code, ContentSlotForPageModel.class, query);

		assertThat(restriction).isNotNull();
		assertThat(restriction.getCode()).isEqualTo(code);
		assertThat(restriction.getQuery()).isEqualTo(query);
	}

	@Test
	public void testCreateCmsSyncSearchRestrictionOfSlots()
	{
		final String code = "Sync_Only_Approved_Slots_Restriction";
		final String query = SYNCHRONIZATION_BLOCKED_IS_NULL_OR_FALSE;
		SearchRestrictionModel restriction = cmsSyncSearchRestrictionService
				.createCmsSyncSearchRestriction(code, ContentSlotModel.class, query);

		assertThat(restriction).isNotNull();
		assertThat(restriction.getCode()).isEqualTo(code);
		assertThat(restriction.getQuery()).isEqualTo(query);
	}

	@Test
	public void testCreateCmsSyncSearchRestrictionOfComponents()
	{
		final String code = "Sync_Only_Approved_Components_Restriction";
		final String query = SYNCHRONIZATION_BLOCKED_IS_NULL_OR_FALSE;
		SearchRestrictionModel restriction = cmsSyncSearchRestrictionService
				.createCmsSyncSearchRestriction(code, AbstractCMSComponentModel.class, query);

		assertThat(restriction).isNotNull();
		assertThat(restriction.getCode()).isEqualTo(code);
		assertThat(restriction.getQuery()).isEqualTo(query);
	}

	@Test
	public void testCreateCmsSyncSearchRestrictionOfRestrictions()
	{
		final String code = "Sync_Only_Approved_Restrictions_Restriction";
		final String query = SYNCHRONIZATION_BLOCKED_IS_NULL_OR_FALSE;
		SearchRestrictionModel restriction = cmsSyncSearchRestrictionService
				.createCmsSyncSearchRestriction(code, AbstractRestrictionModel.class, query);

		assertThat(restriction).isNotNull();
		assertThat(restriction.getCode()).isEqualTo(code);
		assertThat(restriction.getQuery()).isEqualTo(query);
	}
}
