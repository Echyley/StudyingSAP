/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.mediaconversion.integrationtest;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.fail;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.media.url.impl.LocalMediaWebURLStrategy;
import de.hybris.platform.mediaconversion.conversion.ConvertedMediaCreationStrategy;
import de.hybris.platform.mediaconversion.model.ConversionMediaFormatModel;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.testframework.PropertyConfigSwitcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Test;

@IntegrationTest
public class DefaultConvertedMediaCreationStrategyTest extends ServicelayerBaseTest
{

	private final PropertyConfigSwitcher prettyUrl = new PropertyConfigSwitcher(
			LocalMediaWebURLStrategy.MEDIA_LEGACY_PRETTY_URL);
	@Resource
	ConvertedMediaCreationStrategy convertedMediaCreationStrategy;
	@Resource
	ModelService modelService;
	@Resource
	MediaService mediaService;

	@After
	public void tearDown()
	{
		prettyUrl.switchBackToDefault();
	}

	@Test
	public void shouldKeepFileNameWithExtensionAfterConversionWithPrettyUrl() throws Exception
	{
		prettyUrl.switchToValue("true");

		final MediaModel originalMedia = TestMedia.TULIP.createMedia(this.modelService, this.mediaService,
				createContainer("testCNT"));
		final String originalRealFileName = originalMedia.getRealFileName();
		assertThat(originalRealFileName).endsWith("tulip.png");
		assertThat(originalMedia.getURL()).endsWith("tulip.png").doesNotContain("context");
		final ConversionMediaFormatModel normalFormat = createFormat("normal", null, "-resize 600x600 -normalize", null);
		final String expectedConvertedRealFileName = normalFormat.getQualifier() + "_" + originalRealFileName;

		final File tmpFile = File.createTempFile("tmp_", "." + "png");

		final MediaModel convertedMedia;
		try (final InputStream inputStream = new FileInputStream(tmpFile))
		{
			convertedMedia = convertedMediaCreationStrategy.createOrUpdate(originalMedia, normalFormat, inputStream);
			assertThat(convertedMedia).isNotNull();
			assertThat(convertedMedia.getRealFileName()).isEqualTo(expectedConvertedRealFileName);
			assertThat(convertedMedia.getURL()).endsWith("tulip.png").doesNotContain("context");
		}
		catch (final FileNotFoundException e)
		{
			fail("The specified file '" + tmpFile + "' could not be found.");
		}
		finally
		{
			tmpFile.delete();
		}

	}

	@Test
	public void shouldKeepFileNameWithExtensionAfterConversionWithoutPrettyUrl() throws Exception
	{
		prettyUrl.switchToValue("false");

		final MediaModel originalMedia = TestMedia.TULIP.createMedia(this.modelService, this.mediaService,
				createContainer("testCNT"));
		final String originalRealFileName = originalMedia.getRealFileName();
		assertThat(originalRealFileName).endsWith("tulip.png");
		assertThat(originalMedia.getURL()).contains("tulip.png").contains("context");
		final ConversionMediaFormatModel normalFormat = createFormat("normal", null, "-resize 600x600 -normalize", null);
		final String expectedConvertedRealFileName = normalFormat.getQualifier() + "_" + originalRealFileName;

		final File tmpFile = File.createTempFile("tmp_", "." + "png");

		final MediaModel convertedMedia;
		try (final InputStream inputStream = new FileInputStream(tmpFile))
		{
			convertedMedia = convertedMediaCreationStrategy.createOrUpdate(originalMedia, normalFormat, inputStream);
			assertThat(convertedMedia).isNotNull();
			assertThat(convertedMedia.getRealFileName()).isEqualTo(expectedConvertedRealFileName);
			assertThat(convertedMedia.getURL()).contains("tulip.png").contains("context");
		}
		catch (final FileNotFoundException e)
		{
			fail("The specified file '" + tmpFile + "' could not be found.");
		}
		finally
		{
			tmpFile.delete();
		}

	}

	private ConversionMediaFormatModel createFormat(final String code, final String mime, final String conversion,
	                                                final ConversionMediaFormatModel input)
	{
		final ConversionMediaFormatModel ret = this.modelService.create(ConversionMediaFormatModel.class);
		ret.setQualifier(code);
		ret.setConversion(conversion);
		ret.setMimeType(mime);
		ret.setConversionStrategy("imageMagickMediaConversionStrategy");
		ret.setInputFormat(input);
		this.modelService.save(ret);
		return ret;
	}

	private MediaContainerModel createContainer(final String name)
	{
		final MediaContainerModel container = this.modelService.create(MediaContainerModel.class);
		container.setQualifier(name);
		container.setCatalogVersion(TestDataFactory.someCatalogVersion(modelService));
		modelService.save(container);
		return container;
	}

}