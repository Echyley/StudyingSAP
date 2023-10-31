/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.azure.media.storage;

import static de.hybris.platform.media.storage.MediaStorageConfigService.MediaFolderConfig;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.media.storage.MediaStorageConfigService;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;

import javax.annotation.Resource;

import org.junit.Test;

@IntegrationTest
public class WindowsAzureBlobStorageStrategyIntegrationTest extends ServicelayerBaseTest
{
	@Resource(name = "windowsAzureBlobStorageStrategy")
	private WindowsAzureBlobStorageStrategy strategy;
	@Resource
	private MediaStorageConfigService mediaStorageConfigService;

	@Test
	public void shouldReturnFalseForInvalidMediaFolderNames()
	{
		for (final String qualifier : getInvalidMediaFolderQualifiers())
		{
			//given
			final MediaFolderConfig config = mediaStorageConfigService.getConfigForFolder(qualifier);

			//when
			final boolean hasValidFolderName = strategy.hasValidMediaFolderName(config);

			//then
			assertThat(hasValidFolderName).isFalse();
		}
	}

	@Test
	public void shouldReturnTrueForValidMediaFolderNames()
	{
		for (final String qualifier : getValidMediaFolderQualifiers())
		{
			//given
			final MediaFolderConfig config = mediaStorageConfigService.getConfigForFolder(qualifier);

			//when
			final boolean hasValidFolderName = strategy.hasValidMediaFolderName(config);

			//then
			assertThat(hasValidFolderName).isTrue();
		}
	}

	private String[] getInvalidMediaFolderQualifiers()
	{
		return new String[]
				{
						"test-",
						"test--test",
						"test-test--test",
						"test_-test",
						"_test-",
						"test_",
						"test__test",
						"#test",
						"test:test",
						"test#test",
						"test,test",
						"/test#",
				};
	}

	private String[] getValidMediaFolderQualifiers()
	{
		return new String[]
				{
						"test",
						"test-test",
						"test-test-test",
						"test-test-test-test",
						"test1234",
						"test-1234",
						"test12-test34",
						"1234",
						"12-34",
						"Test",
						"TEST",
						"teSt"
				};
	}
}
