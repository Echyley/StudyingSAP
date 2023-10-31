/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.cmsitems.impl;

import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_CLONE_ACTION;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_CONTENT_SLOT_UUID;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_IS_SLOT_CUSTOM;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_PAGE_UUID;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_UUID;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cms2.enums.CloneAction;
import de.hybris.platform.cms2.enums.CmsApprovalStatus;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminPageService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminSiteService;
import de.hybris.platform.cmsfacades.cmsitems.CMSItemFacade;
import de.hybris.platform.cmsfacades.data.ItemSynchronizationData;
import de.hybris.platform.cmsfacades.data.PageContentSlotData;
import de.hybris.platform.cmsfacades.data.SlotStatus;
import de.hybris.platform.cmsfacades.pagescontentslots.PageContentSlotFacade;
import de.hybris.platform.cmsfacades.uniqueidentifier.EncodedItemComposedKey;
import de.hybris.platform.cmsfacades.util.BaseIntegrationTest;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultMulticountryCMSItemFacadeIntegrationTest extends BaseIntegrationTest
{
	public static String NEWLY_CREATED_SLOT_NAME = "electronics-uk-homepage";
	public static String GLOBAL_SEARCH_BOX_SLOT_ID = "SearchBoxSlot";
	public static String GLOBAL_SEARCH_BOX_SLOT_POSITION = "SearchBox";
	public static String GLOBAL_CATALOG_NAME = "MultiCountryTestContentCatalog";
	public static String REGION_CATALOG_NAME = "MultiCountryTestContentCatalog-region";
	public static String LOCAL_CATALOG_NAME = "MultiCountryTestContentCatalog-local";

	public static String LOCAL_HOMEPAGE = "homepage-local";
	public static String LOCAL_ANOTHER_PAGE = "homepage-local-clone";

	public static String ONLINE_CATALOG_VERSION = "OnlineVersion";
	public static String STAGED_CATALOG_VERSION = "StagedVersion";

	@Resource
	private CMSItemFacade defaultCMSItemFacade;

	@Resource
	private CMSAdminSiteService cmsAdminSiteService;

	@Resource
	private PageContentSlotFacade pageContentSlotFacade;

	@Resource
	private CMSAdminPageService cmsAdminPageService;

	@Resource
	private ModelService modelService;



	@Before
	public void setUp() throws Exception
	{
		importCsv("/test/cmsMultiCountryPagesTestData.csv", "utf-8");
		setEnv();
		cmsAdminSiteService.setActiveCatalogVersion(LOCAL_CATALOG_NAME, STAGED_CATALOG_VERSION);
	}

	@Test
	public void shouldCreateCustomSlotForHomePageUk() throws CMSItemNotFoundException
	{
		// GIVEN
		final Map<String, Object> slotToCreate = preparePayloadForNewSlot(NEWLY_CREATED_SLOT_NAME, true, getUuid(GLOBAL_SEARCH_BOX_SLOT_ID, GLOBAL_CATALOG_NAME, ONLINE_CATALOG_VERSION), CloneAction.CLONE);

		// WHEN
		final Map<String, Object> createdSlot = defaultCMSItemFacade.createItem(slotToCreate);

		// THEN
		final String newlyCreatedSlotUid = createdSlot.get(ContentSlotModel.UID).toString();
		final Optional<PageContentSlotData> contentSlotForLocalPageOptional =
				getNewCreatedContentDataSlotForPage(LOCAL_HOMEPAGE, newlyCreatedSlotUid);
		final Optional<PageContentSlotData> contentSlotForAnotherLocalPageOptional = getNewCreatedContentDataSlotForPage(LOCAL_ANOTHER_PAGE, newlyCreatedSlotUid);
		final ArrayList clonedComponents = (ArrayList) createdSlot.get(ContentSlotModel.CMSCOMPONENTS);
		assertThat("Name of newly created slot must be the same from payload", createdSlot.get(ContentSlotModel.NAME), is(NEWLY_CREATED_SLOT_NAME));
		assertTrue("Newly created slot must contain cloned components from local catalog version", componentsBelongToCatalogVersion(clonedComponents, LOCAL_CATALOG_NAME, STAGED_CATALOG_VERSION));
		assertTrue("Homepage must contain custom slot that was just created", contentSlotForLocalPageOptional.isPresent());
		final PageContentSlotData pageContentSlot = contentSlotForLocalPageOptional.get();
		assertEquals("Slot status must be OVERRIDE", SlotStatus.OVERRIDE, pageContentSlot.getSlotStatus());
		assertFalse("Slot must not be shared", pageContentSlot.isSlotShared());
		assertEquals("Slot position must be the same as original one", pageContentSlot.getPosition(),
				GLOBAL_SEARCH_BOX_SLOT_POSITION);
		assertTrue("Another page from the same catalog version must not contain newly created slot cause it is custom for another page", contentSlotForAnotherLocalPageOptional.isEmpty());
	}

	@Test
	public void shouldChangePageStatusToCheckAfterCreatingCustomSlot() throws CMSItemNotFoundException
	{
		// GIVEN
		final Map<String, Object> slotToCreate = preparePayloadForNewSlot(NEWLY_CREATED_SLOT_NAME, true, getUuid(GLOBAL_SEARCH_BOX_SLOT_ID, GLOBAL_CATALOG_NAME, ONLINE_CATALOG_VERSION), CloneAction.CLONE);

		// WHEN
		final Map<String, Object> createdSlot = defaultCMSItemFacade.createItem(slotToCreate);

		// THEN
		AbstractPageModel page = cmsAdminPageService.getPageForIdFromActiveCatalogVersion(LOCAL_HOMEPAGE);
		assertEquals("After creating custom slot the page status is CHECK", CmsApprovalStatus.CHECK, page.getApprovalStatus());
	}

	@Test
	public void shouldChangePageStatusToCheckAfterCustomSlotIsRemoved() throws CMSItemNotFoundException
	{
		// GIVEN
		AbstractPageModel pageBefore = cmsAdminPageService.getPageForIdFromActiveCatalogVersion(LOCAL_HOMEPAGE);
		pageBefore.setApprovalStatus(CmsApprovalStatus.APPROVED);
		modelService.save(pageBefore);
		pageBefore = cmsAdminPageService.getPageForIdFromActiveCatalogVersion(LOCAL_HOMEPAGE);
		assertEquals("Before removing custom slot the page status is APPROVED", CmsApprovalStatus.APPROVED, pageBefore.getApprovalStatus());

		// WHEN
		defaultCMSItemFacade.deleteCMSItemByUuid(getUuid("HeaderLinks-local", LOCAL_CATALOG_NAME, STAGED_CATALOG_VERSION));

		// THEN
		AbstractPageModel pageAfter = cmsAdminPageService.getPageForIdFromActiveCatalogVersion(LOCAL_HOMEPAGE);
		assertEquals("After removing custom slot the page status is CHECK", CmsApprovalStatus.CHECK, pageAfter.getApprovalStatus());
	}

	protected ItemSynchronizationData getItemSyncData(String uid, String catalogId, String catalogVersion, String itemType)
	{
		ItemSynchronizationData itemSynchronizationData = new ItemSynchronizationData();
		itemSynchronizationData.setItemId(getUuid(uid, catalogId, catalogVersion));
		itemSynchronizationData.setItemType(itemType);
		return itemSynchronizationData;

	}

	@Test
	public void shouldCreateLocalSlotWithingElectronicsUkCatalog() throws CMSItemNotFoundException
	{
		// GIVEN
		final Map<String, Object> slotToCreate = preparePayloadForNewSlot(NEWLY_CREATED_SLOT_NAME, false, getUuid(GLOBAL_SEARCH_BOX_SLOT_ID, GLOBAL_CATALOG_NAME, ONLINE_CATALOG_VERSION), CloneAction.CLONE);

		// WHEN
		final Map<String, Object> createdSlot = defaultCMSItemFacade.createItem(slotToCreate);

		// THEN
		final String newlyCreatedSlotUid = createdSlot.get(ContentSlotModel.UID).toString();
		final Optional<PageContentSlotData> contentSlotForHomepageOptional = getNewCreatedContentDataSlotForPage(LOCAL_HOMEPAGE
				, newlyCreatedSlotUid);
		final Optional<PageContentSlotData> contentSlotForAnotherPageOptional =
				getNewCreatedContentDataSlotForPage(LOCAL_ANOTHER_PAGE, newlyCreatedSlotUid);

		final ArrayList clonedComponents = (ArrayList) createdSlot.get(ContentSlotModel.CMSCOMPONENTS);
		assertThat("Name of newly created slot must be the same from payload", createdSlot.get(ContentSlotModel.NAME), is(NEWLY_CREATED_SLOT_NAME));
		assertTrue("Newly created slot must contain cloned components from local catalog version", componentsBelongToCatalogVersion(clonedComponents, LOCAL_CATALOG_NAME, STAGED_CATALOG_VERSION));
		assertTrue("Homepage must contain local slot cause it is local for the whole catalog",
				contentSlotForHomepageOptional.isPresent());
		final PageContentSlotData pageContentSlot = contentSlotForHomepageOptional.get();
		assertEquals("Slot status must be TEMPLATE", SlotStatus.TEMPLATE, pageContentSlot.getSlotStatus());
		assertTrue("Slot must be shared", pageContentSlot.isSlotShared());
		assertEquals("Slot position must be the same as original one", pageContentSlot.getPosition(),
				GLOBAL_SEARCH_BOX_SLOT_POSITION);
		assertTrue("Another page must contain newly created slot cause it is local for the whole catalog", contentSlotForAnotherPageOptional.isPresent());
	}

	@Test
	public void shouldCreateCustomSlotOnTopOfAlreadyCreatedLocalSlotInSameCatalogVersion() throws CMSItemNotFoundException
	{
		// GIVEN
		final Map<String, Object> payloadLocalSlot = preparePayloadForNewSlot(NEWLY_CREATED_SLOT_NAME + "_local", false, getUuid(GLOBAL_SEARCH_BOX_SLOT_ID, GLOBAL_CATALOG_NAME, ONLINE_CATALOG_VERSION), CloneAction.CLONE);
		final Map<String, Object> createdLocalSlotPayload = defaultCMSItemFacade.createItem(payloadLocalSlot);

		// WHEN
		final Map<String, Object> payloadCustomSlot = preparePayloadForNewSlot(NEWLY_CREATED_SLOT_NAME + "_custom", true, createdLocalSlotPayload.get(FIELD_UUID).toString(), CloneAction.CLONE);
		final Map<String, Object> createdCustomSlotPayload = defaultCMSItemFacade.createItem(payloadCustomSlot);

		// THEN
		final String createdCustomSlotUid = createdCustomSlotPayload.get(ContentSlotModel.UID).toString();
		final String createdLocalSlotUid = createdLocalSlotPayload.get(ContentSlotModel.UID).toString();
		final Optional<PageContentSlotData> homepageCustomSlotOptional = getNewCreatedContentDataSlotForPage(LOCAL_HOMEPAGE, createdCustomSlotUid);
		final Optional<PageContentSlotData> anotherPageLocalSlotOptional = getNewCreatedContentDataSlotForPage(LOCAL_ANOTHER_PAGE, createdLocalSlotUid);
		final Optional<PageContentSlotData> anotherPageCustomSlotOptional = getNewCreatedContentDataSlotForPage(LOCAL_ANOTHER_PAGE, createdCustomSlotUid);

		assertTrue("Home page must contain custom slot", homepageCustomSlotOptional.isPresent());
		assertTrue("Another page must contain only local slot cause the slot was overridden only in homepage", anotherPageLocalSlotOptional.isPresent());
		assertTrue("Another page does not contain custom slot cause it is only available in homepage", anotherPageCustomSlotOptional.isEmpty());
	}

	@Test
	public void shouldCreateEmptyCustomSlot() throws CMSItemNotFoundException
	{
		// GIVEN
		final Map<String, Object> slotToCreate = preparePayloadForNewSlot(NEWLY_CREATED_SLOT_NAME, true, getUuid(GLOBAL_SEARCH_BOX_SLOT_ID, GLOBAL_CATALOG_NAME, ONLINE_CATALOG_VERSION), CloneAction.REMOVE);

		// WHEN
		final Map<String, Object> createdSlot = defaultCMSItemFacade.createItem(slotToCreate);

		// THEN
		final String newlyCreatedSlotUid = createdSlot.get(ContentSlotModel.UID).toString();
		final Optional<PageContentSlotData> contentSlotForHomepageOptional = getNewCreatedContentDataSlotForPage(LOCAL_HOMEPAGE
				, newlyCreatedSlotUid);
		final Optional<PageContentSlotData> contentSlotForAnotherPageOptional =
				getNewCreatedContentDataSlotForPage(LOCAL_ANOTHER_PAGE, newlyCreatedSlotUid);
		final ArrayList clonedComponents = (ArrayList) createdSlot.get(ContentSlotModel.CMSCOMPONENTS);
		assertThat("Name of newly created custom slot must be the same from payload", createdSlot.get(ContentSlotModel.NAME), is(NEWLY_CREATED_SLOT_NAME));
		assertTrue("Newly created custom slot must not contain components", clonedComponents.isEmpty());
		assertTrue("Homepage must contain custom slot that was just created", contentSlotForHomepageOptional.isPresent());
		final PageContentSlotData pageContentSlot = contentSlotForHomepageOptional.get();
		assertEquals("Slot status must be OVERRIDE", SlotStatus.OVERRIDE, pageContentSlot.getSlotStatus());
		assertFalse("Slot must not be shared", pageContentSlot.isSlotShared());
		assertEquals("Slot position must be the same as original one", pageContentSlot.getPosition(),
				GLOBAL_SEARCH_BOX_SLOT_POSITION);
		assertTrue("Another page from the same catalog version must not contain newly created slot cause it is custom for another page", contentSlotForAnotherPageOptional.isEmpty());
	}

	@Test
	public void shouldCreateEmptyLocalSlot() throws CMSItemNotFoundException
	{
		// GIVEN
		final Map<String, Object> slotToCreate = preparePayloadForNewSlot(NEWLY_CREATED_SLOT_NAME, false, getUuid(GLOBAL_SEARCH_BOX_SLOT_ID, GLOBAL_CATALOG_NAME, ONLINE_CATALOG_VERSION), CloneAction.REMOVE);

		// WHEN
		final Map<String, Object> createdSlot = defaultCMSItemFacade.createItem(slotToCreate);

		// THEN
		final ArrayList clonedComponents = (ArrayList) createdSlot.get(ContentSlotModel.CMSCOMPONENTS);
		assertTrue("Newly created local slot must not contain components", clonedComponents.isEmpty());
	}

	@Test
	public void shouldCreateCustomSlotWithOriginalReferencedComponents() throws CMSItemNotFoundException
	{
		// GIVEN
		final Map<String, Object> slotToCreate = preparePayloadForNewSlot(NEWLY_CREATED_SLOT_NAME, true, getUuid(GLOBAL_SEARCH_BOX_SLOT_ID, GLOBAL_CATALOG_NAME, ONLINE_CATALOG_VERSION), CloneAction.REFERENCE);

		// WHEN
		final Map<String, Object> createdSlot = defaultCMSItemFacade.createItem(slotToCreate);

		// THEN
		final ArrayList clonedComponents = (ArrayList) createdSlot.get(ContentSlotModel.CMSCOMPONENTS);
		assertTrue("Newly created slot must contain referenced components from global online catalog version", componentsBelongToCatalogVersion(clonedComponents, GLOBAL_CATALOG_NAME, ONLINE_CATALOG_VERSION));
	}


	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionWhenTryingToOverrideCustomSlotWithAnotherCustomSlot() throws CMSItemNotFoundException
	{
		// GIVEN
		final Map<String, Object> slotToCreate = preparePayloadForNewSlot(NEWLY_CREATED_SLOT_NAME, true, getUuid(GLOBAL_SEARCH_BOX_SLOT_ID, GLOBAL_CATALOG_NAME, ONLINE_CATALOG_VERSION), CloneAction.REMOVE);
		final Map<String, Object> createdSlot = defaultCMSItemFacade.createItem(slotToCreate);

		// WHEN
		final Map<String, Object> slotToCreate2 = preparePayloadForNewSlot(NEWLY_CREATED_SLOT_NAME + "just_new_name", true, getUuid(GLOBAL_SEARCH_BOX_SLOT_ID, GLOBAL_CATALOG_NAME, ONLINE_CATALOG_VERSION), CloneAction.REMOVE);
		final Map<String, Object> createdSlot2 = defaultCMSItemFacade.createItem(slotToCreate2);
	}

	/**
	 * Verifies whether the each uuid in the list belongs to provided catalog version.
	 * This method used to verify that compoents were cloned (or referenced) and a new slot.
	 * @param componentUuids the list of component uuids
	 * @param catalog the catalog
	 * @param catalogVersion the catalog version
	 * @return true if all uuids belongs to provided catalog version, false otherwise.
	 */
	protected boolean componentsBelongToCatalogVersion(List<String> componentUuids, String catalog, String catalogVersion)
	{
		return componentUuids.stream().allMatch(uuid -> {
			final EncodedItemComposedKey itemComposedKey = new EncodedItemComposedKey.Builder(uuid).encoded().build();
			return itemComposedKey.getCatalogId().equals(catalog) && itemComposedKey.getCatalogVersion().equals(catalogVersion);
		});
	}

	/**
	 * Converts uid, catalog and catalog version to uuid.
	 * @param uid the id of the item
	 * @param catalogId the catalog
	 * @param catalogVersion the catalog version
	 * @return the uuid
	 */
	protected String getUuid(final String uid, final String catalogId, final String catalogVersion)
	{
		final EncodedItemComposedKey itemComposedKey = new EncodedItemComposedKey();
		itemComposedKey.setCatalogId(catalogId);
		itemComposedKey.setCatalogVersion(catalogVersion);
		itemComposedKey.setItemId(uid);
		return itemComposedKey.toEncoded();
	}

	/**
	 * Prepares payload for a new slot
	 * @param slotName the slot name
	 * @param isSlotCustom whether the slot is custom or not (local)
	 * @param sourceSlotUUID the source slot that will be overridden
	 * @param cloneAction action that says what to do with components inside (clone, reference, remove)
	 * @return the payload for a new slot
	 */
	protected Map<String, Object> preparePayloadForNewSlot(final String slotName, final Boolean isSlotCustom, final String sourceSlotUUID,
			final CloneAction cloneAction)
	{
		final Map<String, Object> slotToCreate = new HashMap<>();
		slotToCreate.put(ContentSlotModel.NAME, slotName);
		slotToCreate.put(FIELD_CONTENT_SLOT_UUID, sourceSlotUUID);
		slotToCreate.put(ContentSlotModel.ITEMTYPE, ContentSlotModel._TYPECODE);
		slotToCreate.put(ContentSlotModel.CATALOGVERSION, LOCAL_CATALOG_NAME + "/" + STAGED_CATALOG_VERSION); // target catalog version
		slotToCreate.put(FIELD_PAGE_UUID, LOCAL_HOMEPAGE); // target page
		slotToCreate.put(FIELD_CLONE_ACTION, cloneAction.name());
		slotToCreate.put("onlyOneRestrictionMustApply", false);
		slotToCreate.put(FIELD_IS_SLOT_CUSTOM, isSlotCustom);
		return slotToCreate;
	}

	/**
	 * Sets the environment
	 */
	protected void setEnv()
	{
		setCurrentUser("cmsmanager");
		final CMSSiteModel localMultiCountryTestSite = cmsAdminSiteService.getSiteForId("LocalMultiCountryTestSite");
		cmsAdminSiteService.setActiveSite(localMultiCountryTestSite);
	}

	/**
	 * Retrieves the list of content slots (PageContentSlotData) for provided page and finds there a slot with provided uid.
	 * @param pageName the page name for which the list of slots will be retrieved.
	 * @param slotUid the slot id that needs to be in the list of page slots
	 * @return the slot that matches the provided uid or nothing. The result is wrapped in Optional.
	 * @throws CMSItemNotFoundException
	 */
	protected Optional<PageContentSlotData> getNewCreatedContentDataSlotForPage(final String pageName, final String slotUid)
			throws CMSItemNotFoundException
	{
		final List<PageContentSlotData> slotsForPage = pageContentSlotFacade.getContentSlotsByPage(pageName);
		return slotsForPage.stream().filter(slot -> slot.getSlotId().equals(slotUid)).findFirst();
	}
}
