/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.types.populator;

import de.hybris.platform.cmsfacades.common.populator.LocalizedPopulator;
import de.hybris.platform.cmsfacades.data.ComponentTypeData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;


/**
 * This populator will convert the {@link ComposedTypeModel#getCode()} and {@link ComposedTypeModel#getName()} only.
 */
public class BasicComponentTypePopulator implements Populator<ComposedTypeModel, ComponentTypeData>
{
	private LocalizedPopulator localizedPopulator;
	public BasicComponentTypePopulator(LocalizedPopulator localizedPopulator) {
		super();
		this.localizedPopulator = localizedPopulator;
	}

	@Override
	public void populate(final ComposedTypeModel source, final ComponentTypeData target) throws ConversionException
	{
		target.setCode(source.getCode());
		target.setName(source.getName());

		// https://help.sap.com/viewer/9d346683b0084da2938be8a285c0c27a/2005/en-US/2a5fc77a3fa74cc09852505816f08912.html
		final Map<String, String> nameMap = Optional.ofNullable(target.getNameWithLocale()).orElseGet(() -> getNewNameMap(target));
		this.localizedPopulator.populate( //
				(locale, value) -> nameMap.put(this.localizedPopulator.getLanguage(locale), value),
				locale -> source.getName(locale));
	}

	protected Map<String, String> getNewNameMap(final ComponentTypeData target)
	{
		target.setNameWithLocale(new LinkedHashMap<>());
		return target.getNameWithLocale();
	}

}
