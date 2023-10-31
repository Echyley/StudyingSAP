/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.media;

import static de.hybris.platform.jalo.media.MediaManager.ROOT_FOLDER_QUALIFIER;
import static de.hybris.platform.media.interceptors.PreventRootMediaFolderRemovalInterceptor.MediaRootFolderRemovalLogic.IS_ROOT_REMOVABLE_CONFIG_FLAG;

import static java.lang.Boolean.FALSE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.media.MediaFolderModel;
import de.hybris.platform.jalo.ConsistencyCheckException;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.media.MediaFolder;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.exceptions.ModelRemovalException;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.testframework.PropertyConfigSwitcher;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Test;

@IntegrationTest
public class RootMediaFolderRemovalTest extends ServicelayerBaseTest
{
	private final PropertyConfigSwitcher allowRootMediaFolderRemoval = new PropertyConfigSwitcher(IS_ROOT_REMOVABLE_CONFIG_FLAG);
	@Resource
	MediaService mediaService;
	@Resource
	ModelService modelService;

	@After
	public void tearDown()
	{
		allowRootMediaFolderRemoval.switchBackToDefault();
	}

	@Test
	public void shouldNotBePossibleToRemoveRootMediaFolderUsingServiceLayer()
	{
		allowRootMediaFolderRemoval.switchToValue(FALSE.toString());
		final MediaFolderModel rootFolder = requireExistingRootFolderModel();

		assertThatExceptionOfType(ModelRemovalException.class).isThrownBy(() ->
				modelService.remove(rootFolder)
		).withCauseInstanceOf(InterceptorException.class).withMessageContaining(ROOT_FOLDER_QUALIFIER);
	}

	@Test
	public void shouldNotBePossibleToRemoveRootMediaFolderUsingJaloLayer()
	{
		allowRootMediaFolderRemoval.switchToValue(FALSE.toString());
		final MediaFolder rootFolder = requireExistingRootFolderJaloItem();

		assertThatExceptionOfType(ConsistencyCheckException.class).isThrownBy(rootFolder::remove)
		                                                          .withNoCause()
		                                                          .withMessageContaining(ROOT_FOLDER_QUALIFIER);
	}

	private MediaFolder requireExistingRootFolderJaloItem()
	{
		final MediaFolder rootFolder = JaloSession.lookupItem(requireExistingRootFolderModel().getPk());

		assertThat(rootFolder).isNotNull();
		assertThat(rootFolder.isAlive()).isTrue();

		return rootFolder;
	}

	private MediaFolderModel requireExistingRootFolderModel()
	{
		final MediaFolderModel rootFolder = mediaService.getRootFolder();

		assertThat(rootFolder).isNotNull();

		return rootFolder;
	}
}