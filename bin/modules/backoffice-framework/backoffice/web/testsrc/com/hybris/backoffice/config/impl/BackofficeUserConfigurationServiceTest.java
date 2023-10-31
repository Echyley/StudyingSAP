/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved
 */

package com.hybris.backoffice.config.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.backoffice.cockpitng.util.impl.PlatformTypeContextStrategy;
import com.hybris.backoffice.media.MediaUtil;
import com.hybris.cockpitng.core.config.CockpitConfigurationException;
import com.hybris.cockpitng.core.config.impl.DefaultCockpitConfigurationService;
import com.hybris.cockpitng.core.config.impl.DefaultConfigContext;
import com.hybris.cockpitng.core.config.impl.jaxb.Config;
import com.hybris.cockpitng.core.config.impl.jaxb.Context;
import com.hybris.cockpitng.core.config.impl.jaxb.listview.ListColumn;
import com.hybris.cockpitng.core.config.impl.jaxb.listview.ListView;


@RunWith(MockitoJUnitRunner.class)
public class BackofficeUserConfigurationServiceTest
{

	@InjectMocks
	@Spy
	private BackofficeUserConfigurationService backofficeUserConfigurationService;

	@Mock
	private ModelService modelService;

	@Mock
	private UserService userService;

	@Mock
	private MediaService mediaService;

	@Mock
	private DefaultCockpitConfigurationService cockpitConfigurationService;

	@Mock
	private BackofficeUserConfigurationCache userConfigurationCache;

	@Mock
	private PlatformTypeContextStrategy strategy;

	@Spy
	private MediaUtil backofficeMediaUtil;

	private UserModel testUser = mock(UserModel.class);

	@Before
	public void setUp() throws Exception
	{
		backofficeUserConfigurationService.setModelService(modelService);
		backofficeUserConfigurationService.setUserService(userService);
		backofficeUserConfigurationService.setMediaService(mediaService);
		backofficeUserConfigurationService.setCockpitConfigurationService(cockpitConfigurationService);
		backofficeUserConfigurationService.setUserConfigurationCache(userConfigurationCache);
		backofficeUserConfigurationService.setBackofficeMediaUtil(backofficeMediaUtil);
		backofficeUserConfigurationService.setStrategy(strategy);

		doReturn(testUser).when(userService).getCurrentUser();
	}

	@Test
	public void testStoreUserConfigurationWhenUserHasConfig() throws CockpitConfigurationException
	{
		// given
		final Config root = new Config();
		doReturn(root).when(userConfigurationCache).getRoot();
		doReturn(mock(MediaModel.class)).when(testUser).getWidgetsConfigForBackoffice();
		doReturn(mock(ByteArrayOutputStream.class)).when(backofficeUserConfigurationService).applyRootConfigStream(any(), any());

		// when
		backofficeUserConfigurationService.storeUserConfiguration(new DefaultConfigContext(), new Object());

		// then
		verify(mediaService, times(1)).setDataForMedia(any(), any());
		verify(userConfigurationCache, times(1)).setRoot(any());
	}

	@Test
	public void testStoreUserConfigurationWhenUserNotHasConfig() throws CockpitConfigurationException
	{
		// given
		final MediaModel media = mock(MediaModel.class);
		final Config root = new Config();
		doReturn(root).when(userConfigurationCache).getRoot();
		doReturn(null).when(testUser).getWidgetsConfigForBackoffice();
		doReturn(mock(ByteArrayOutputStream.class)).when(backofficeUserConfigurationService).applyRootConfigStream(any(), any());
		doReturn(media).when(backofficeMediaUtil).createMedia(any(), any(), any(), any(), any());

		// when
		backofficeUserConfigurationService.storeUserConfiguration(new DefaultConfigContext(), new Object());

		// then
		verify(mediaService, times(1)).setDataForMedia(any(), any());
		verify(modelService, times(1)).save(testUser);
		verify(userConfigurationCache, times(1)).setRoot(any());
	}

	@Test
	public void testLoadUserConfigurationWhenRootConfigIsNull() throws CockpitConfigurationException
	{
		// when
		final ListView listView = backofficeUserConfigurationService.loadUserConfiguration(new DefaultConfigContext(),
				ListView.class, new ListView());

		// then
		assertNull(listView);
	}

	@Test
	public void verifyMergeWithSystemConfig() throws CockpitConfigurationException
	{
		// given
		final String type = "Product";
		final String component = "list-view";
		final ListView systemConfig = buildListView(Arrays.asList("code", "name"));
		final ListView userConfig = buildListView(Arrays.asList("code", "name"));
		applyMergeMode(userConfig, Arrays.asList("remove", ""));
		final ListView expectedResult = userConfig;


		final Context context = backofficeUserConfigurationService.buildContextElement(new DefaultConfigContext(component, type),
				userConfig);

		final Config root = spy(Config.class);
		lenient().doReturn(Arrays.asList(context)).when(root).getContext();
		lenient().doReturn(root).when(userConfigurationCache).getRoot();
		doReturn(userConfig).when(cockpitConfigurationService).mergeContexts(any(), any(), any());
		doReturn(userConfig).when(cockpitConfigurationService).adaptConfigAfterLoad(any(), any(), any());
		doReturn(new ArrayList<>()).when(strategy).getParentContexts(type);

		// when
		final ListView listView = backofficeUserConfigurationService
				.loadUserConfiguration(new DefaultConfigContext(component, type), ListView.class, systemConfig);

		// then
		assertEquals(expectedResult, listView);
	}

	@Test
	public void verifyLoadUserCurrentTypeConfiguration() throws CockpitConfigurationException
	{
		// given
		final String type = "Product";
		final String component = "list-view";
		final ListView userConfig = buildListView(Arrays.asList("code"));

		final Context context = backofficeUserConfigurationService.buildContextElement(new DefaultConfigContext(component, type),
				userConfig);

		final Config root = spy(Config.class);
		lenient().doReturn(Arrays.asList(context)).when(root).getContext();
		doReturn(root).when(userConfigurationCache).getRoot();

		doReturn(userConfig).when(cockpitConfigurationService).getConfigElement(any(), any());

		// when
		final ListView listView = backofficeUserConfigurationService
				.loadUserCurrentTypeConfiguration(new DefaultConfigContext(component, type), ListView.class);

		// then
		assertEquals(userConfig, listView);
	}

	private ListView buildListView(final List<String> columnQualifierLists)
	{
		final ListView listView = new ListView();
		final List<ListColumn> columnList = new ArrayList<>();
		columnQualifierLists.forEach(qualifier -> {
			final ListColumn column = new ListColumn();
			column.setQualifier(qualifier);
			columnList.add(column);
		});
		return listView;
	}

	private void applyMergeMode(final ListView listView, final List<String> mergeModes)
	{
		for (int i = 0; i < listView.getColumn().size(); i++)
		{
			listView.getColumn().get(i).setMergeMode(mergeModes.get(i));
		}
	}

}
