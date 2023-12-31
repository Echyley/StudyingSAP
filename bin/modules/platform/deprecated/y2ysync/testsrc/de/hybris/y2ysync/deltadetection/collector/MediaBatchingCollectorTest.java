/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.y2ysync.deltadetection.collector;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.deltadetection.ItemChangeDTO;
import de.hybris.deltadetection.enums.ChangeType;
import de.hybris.platform.catalog.model.CatalogUnawareMediaModel;
import de.hybris.platform.core.PK;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.y2ysync.util.Y2YSyncSerializationUtils;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;


@IntegrationTest
public class MediaBatchingCollectorTest extends ServicelayerBaseTest
{

	@Resource
	private ModelService modelService;
	@Resource
	private MediaService mediaService;

	@Test
	public void shouldCreateSeparateMediaForEachBatchOfChangesOfGivenSize()
	{

		// given
		final MediaBatchingCollector collector = new MediaBatchingCollector("testDeltaMedia", 3, modelService, mediaService);
		collector.setId("1");

		// when
		for (int i = 0; i < 10; i++)
		{
			collector.collect(dto(i));
		}
		collector.finish();

		// then

		final List<PK> mediaPks = collector.getPksOfBatches();
		assertThat(mediaPks).hasSize(4);

		final CatalogUnawareMediaModel media0 = (CatalogUnawareMediaModel) mediaService.getMedia("testDeltaMedia-1-0");
		final CatalogUnawareMediaModel media1 = (CatalogUnawareMediaModel) mediaService.getMedia("testDeltaMedia-1-1");
		final CatalogUnawareMediaModel media2 = (CatalogUnawareMediaModel) mediaService.getMedia("testDeltaMedia-1-2");
		final CatalogUnawareMediaModel media3 = (CatalogUnawareMediaModel) mediaService.getMedia("testDeltaMedia-1-3");

		assertThat(media0.getPk()).isEqualTo(mediaPks.get(0));
		assertThat(media1.getPk()).isEqualTo(mediaPks.get(1));
		assertThat(media2.getPk()).isEqualTo(mediaPks.get(2));
		assertThat(media3.getPk()).isEqualTo(mediaPks.get(3));

		final byte[] data0 = mediaService.getDataFromMedia(media0);
		final List<ItemChangeDTO> deserialized0 = getDeserializedChanges(data0);
		assertThat(deserialized0).hasSize(3);
		assertThat(deserialized0.get(0).getItemPK()).isEqualTo(0);
		assertThat(deserialized0.get(1).getItemPK()).isEqualTo(1);
		assertThat(deserialized0.get(2).getItemPK()).isEqualTo(2);

		final byte[] data3 = mediaService.getDataFromMedia(media3);
		final List<ItemChangeDTO> deserialized3 = getDeserializedChanges(data3);
		assertThat(deserialized3).hasSize(1);
		assertThat(deserialized3.get(0).getItemPK()).isEqualTo(9);
	}

	private ItemChangeDTO dto(final long pk)
	{
		return new ItemChangeDTO(Long.valueOf(pk), new Date(), ChangeType.MODIFIED, "", "Product", "ProductStream");
	}

	private List<ItemChangeDTO> getDeserializedChanges(final byte[] data)
	{
		return Y2YSyncSerializationUtils.deserialize(data);
	}
}
