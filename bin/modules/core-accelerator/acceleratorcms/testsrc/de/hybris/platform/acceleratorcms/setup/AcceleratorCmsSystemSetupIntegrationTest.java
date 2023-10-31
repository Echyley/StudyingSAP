/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorcms.setup;

import static de.hybris.platform.cms2.constants.Cms2Constants.CMS_SYNC_USER_ID;
import static org.hamcrest.Matchers.hasSize;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cms2.servicelayer.services.CMSSyncSearchRestrictionService;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.type.SearchRestrictionModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;


@IntegrationTest
public class AcceleratorCmsSystemSetupIntegrationTest extends ServicelayerTest
{
	@Resource
	private CMSSyncSearchRestrictionService cmsSyncSearchRestrictionService;

	@Resource
	private AcceleratorCmsSystemSetup acceleratorCmsSystemSetup;

	@Resource
	private FlexibleSearchService flexibleSearchService;

	@Resource
	private TypeService typeService;

	@Test
	public void shouldCreateSearchRestrictionSucceed()
	{
		acceleratorCmsSystemSetup.createSyncSearchRestrictions();

		List<SearchRestrictionModel> mediaContainerSearchRestrictions = getSearchRestrictionByCode("Sync_Only_Approved_Media_Container_Restriction");
		List<SearchRestrictionModel> mediaSearchRestrictions = getSearchRestrictionByCode("Sync_Only_Approved_Media_Restriction");

		Assert.assertThat(mediaContainerSearchRestrictions, hasSize(1));
		Assert.assertThat(mediaSearchRestrictions, hasSize(1));

		final SearchRestrictionModel mediaContainerSearchRestriction = mediaContainerSearchRestrictions.get(0);
		final ComposedTypeModel mediaContainerRestrictedType = typeService.getComposedTypeForClass(MediaContainerModel.class);

		Assert.assertEquals(" {item:pk} IN ({{ SELECT {mediaComponent:media[ANY]} FROM {AbstractMediaContainerComponent AS mediaComponent} }}) OR {item:pk} IN ({{ SELECT {vc:thumbnail[ANY]} FROM {VideoComponent AS vc} }})", mediaContainerSearchRestriction.getQuery());
		Assert.assertEquals(CMS_SYNC_USER_ID, mediaContainerSearchRestriction.getPrincipal().getUid());
		Assert.assertEquals(mediaContainerRestrictedType, mediaContainerSearchRestriction.getRestrictedType());

		final SearchRestrictionModel mediaSearchRestriction = mediaSearchRestrictions.get(0);
		final ComposedTypeModel mediaRestrictedType = typeService.getComposedTypeForClass(MediaModel.class);

		Assert.assertEquals(" {item:mediaContainer} IS NULL " +
				// Get only media that is used by approved MediaContainer (search restrictions for MediaContainer type is implicitly applied)
				"OR EXISTS ({{ SELECT 1 FROM {MediaContainer AS mc} WHERE {item:mediaContainer} = {mc.pk} }})", mediaSearchRestriction.getQuery());
		Assert.assertEquals(CMS_SYNC_USER_ID, mediaSearchRestriction.getPrincipal().getUid());
		Assert.assertEquals(mediaRestrictedType, mediaSearchRestriction.getRestrictedType());
	}

	protected List<SearchRestrictionModel> getSearchRestrictionByCode(final String restrictionCode)
	{
		final String queryString = "SELECT {pk} FROM {" + SearchRestrictionModel._TYPECODE
				+ "} WHERE {" + SearchRestrictionModel.CODE + "} = ?code";
		final FlexibleSearchQuery fQuery = new FlexibleSearchQuery(queryString);

		final Map<String, Object> queryParameters = new HashMap<>();
		queryParameters.put("code", restrictionCode);
		fQuery.addQueryParameters(queryParameters);
		final SearchResult<SearchRestrictionModel> result = flexibleSearchService.search(fQuery);

		return result.getResult();
	}
}
