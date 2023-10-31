/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.hac.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.hac.facade.HacMaintenanceFacade;
import de.hybris.platform.regioncache.CacheConfiguration;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class MaintainControllerMediaTest
{

	@Mock
	private HacMaintenanceFacade hacMaintenanceFacade;

	@Mock
	private CacheConfiguration cacheConfiguration;

	@Test
	public void shouldClassifyMediaAsOrphanedWhenFileNameIsPKButMediaIsNotPresentInDB()
	{
		// given
		when(hacMaintenanceFacade.getMediasForPk(any())).thenReturn(Collections.emptyList());
		final MaintainController maintainController = new MaintainController(hacMaintenanceFacade, cacheConfiguration);
		final PK pk = PK.parse("1234");
		// when
		final MaintainController.MediaType classifyMediaForNonCloudEnv = maintainController.classifyMedia(
				() -> Collections.emptySet(), pk.toString());
		// then
		assertThat(classifyMediaForNonCloudEnv).isEqualTo(MaintainController.MediaType.ORPHANED);

	}

	@Test
	public void shouldClassifyMediaAsExistWhenFileNameIsPKAndMediaIsPresentInDB()
	{
		// given
		when(hacMaintenanceFacade.getMediasForPk(any())).thenReturn(Arrays.asList(new MediaModel()));
		final MaintainController maintainController = new MaintainController(hacMaintenanceFacade, cacheConfiguration);
		final PK pk = PK.parse("1234");
		// when
		final MaintainController.MediaType classifyMediaForNonCloudEnv = maintainController.classifyMedia(
				() -> Collections.emptySet(), pk.toString());
		// then
		assertThat(classifyMediaForNonCloudEnv).isEqualTo(MaintainController.MediaType.EXIST);

	}

	@Test
	public void shouldClassifyMediaAsNotHybrisWhenFileNameIsNotPkAndNotInCache()
	{
		// given
		final MaintainController maintainController = Mockito.spy(
				new MaintainController(hacMaintenanceFacade, cacheConfiguration));
		doReturn(false).when(maintainController).isFromMediaCacheRegion(Mockito.any(), Mockito.anyString());
		final String notValidPK = "ABC";
		// when
		final MaintainController.MediaType classifyMediaForNonCloudEnv = maintainController.classifyMedia(
				() -> Collections.emptySet(), notValidPK);
		// then
		assertThat(classifyMediaForNonCloudEnv).isEqualTo(MaintainController.MediaType.NOT_HYBRIS);

	}


	@Test
	public void shouldClassifyMediaAsExistWhenFileNameIsNotPkButIsInCacheAsExactlyFilename()
	{
		// given
		final MaintainController maintainController = Mockito.spy(
				new MaintainController(hacMaintenanceFacade, cacheConfiguration));
		final String notValidPK = "ABC";
		final Set<String> mediaValues = Set.of("ABC", "DEF");
		// when
		final MaintainController.MediaType classifyMediaForNonCloudEnv = maintainController.classifyMedia(
				() -> mediaValues, notValidPK);
		// then
		assertThat(classifyMediaForNonCloudEnv).isEqualTo(MaintainController.MediaType.EXIST);

	}

	@Test
	public void shouldClassifyMediaAsNotExistWhenFileNameIsNotPkButIsInCacheAsFilenameWithAdditionalInfo()
	{
		// given
		final MaintainController maintainController = Mockito.spy(
				new MaintainController(hacMaintenanceFacade, cacheConfiguration));
		final String notValidPK = "ABC";
		final Set<String> mediaValues = Set.of("pathToABC\\ABC", "pathToDEF\\DEF");
		// when
		final MaintainController.MediaType classifyMediaForNonCloudEnv = maintainController.classifyMedia(
				() -> mediaValues, notValidPK);
		// then
		assertThat(classifyMediaForNonCloudEnv).isEqualTo(MaintainController.MediaType.NOT_HYBRIS);

	}

}
