/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.synchronization.itemvisitors.impl;

import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.VISITORS_CTX_LOCALES;
import de.hybris.platform.cms2.model.contents.components.VideoComponentModel;
import de.hybris.platform.cmsfacades.synchronization.itemvisitors.AbstractCMSComponentModelVisitor;
import de.hybris.platform.core.model.ItemModel;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class VideoComponentModelVisitor extends AbstractCMSComponentModelVisitor<VideoComponentModel>
{
	@Override
	public List<ItemModel> visit(final VideoComponentModel source, final List<ItemModel> path, final Map<String, Object> ctx)
	{
		final List<ItemModel> collectedItems = super.visit(source, path, ctx);
		Optional.ofNullable(ctx.get(VISITORS_CTX_LOCALES))
				.map(o -> (List<Locale>) o)
				.ifPresent(locales -> locales
						.stream()
						.map(source::getThumbnail).filter(Objects::nonNull)
						.forEach(collectedItems::add) //
				);
		return collectedItems;
	}
}
