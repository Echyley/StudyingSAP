/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.export;

import de.hybris.platform.odata2services.dto.ExportEntity;

import java.util.Set;

import javax.validation.constraints.NotNull;

/**
 * Augment export configuration with referenced entities.
 */
public interface ExportConfigurationDecorator
{
	/**
	 * Augment export entities.
	 *
	 * @param exportEntities export entities to be augmented
	 * @return augmented export entities
	 */
	Set<ExportEntity> decorate(@NotNull Set<ExportEntity> exportEntities);
}
