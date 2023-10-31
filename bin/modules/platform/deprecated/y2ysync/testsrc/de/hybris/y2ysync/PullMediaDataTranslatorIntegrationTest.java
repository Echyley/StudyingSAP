/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.y2ysync;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.impex.ImportConfig;
import de.hybris.platform.servicelayer.impex.ImportResult;
import de.hybris.platform.servicelayer.impex.ImportService;
import de.hybris.platform.servicelayer.impex.impl.ClasspathImpExResource;
import de.hybris.platform.servicelayer.media.MediaService;

import javax.annotation.Resource;

import org.junit.Test;


@IntegrationTest
public class PullMediaDataTranslatorIntegrationTest extends ServicelayerBaseTest
{
	@Resource
	private ImportService importService;
	@Resource
	private MediaService mediaService;

	@Test
	public void testSpecialColumnsWithStandardImpexViaImportService() throws Exception
	{
		//given
		final ImportConfig config = new ImportConfig();
		final ClasspathImpExResource impExResource = new ClasspathImpExResource("/test/testPullMediaDataTranslator.csv", "UTF-8");
		config.setScript(impExResource);

		// when
		final ImportResult result = importService.importData(config);

		// then
		assertThat(result.isSuccessful()).isTrue();
		assertThat(result.hasUnresolvedLines()).isFalse();

		final MediaModel media = mediaService.getMedia("y2ysync-01");
		checkImportedMediaData(media);

		assertThat(media.getRealFileName()).isEqualTo("img_01.jpg");
		assertThat(media.getMime()).isEqualTo("image/jpeg");
	}

	private void checkImportedMediaData(final MediaModel media)
	{
		assertThat(media).isNotNull();
		assertThat(mediaService.hasData(media)).isTrue();
	}
}
