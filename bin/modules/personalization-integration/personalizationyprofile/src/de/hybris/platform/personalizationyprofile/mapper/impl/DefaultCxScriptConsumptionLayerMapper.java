/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.personalizationyprofile.mapper.impl;

import de.hybris.platform.personalizationintegration.mapping.mapper.impl.CxScriptProfileMapper;
import de.hybris.platform.personalizationintegration.model.CxMapperScriptModel;
import de.hybris.platform.personalizationyprofile.mapper.CxConsumptionLayerProfileMapper;
import de.hybris.platform.personalizationyprofile.yaas.Profile;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;


public class DefaultCxScriptConsumptionLayerMapper extends CxScriptProfileMapper<Profile> implements
		CxConsumptionLayerProfileMapper
{
	@Override
	public Set<String> getRequiredFields()
	{
		final List<CxMapperScriptModel> scripts = findMapperScripts();

		final Set<String> requiredFields = scripts
				.stream()
				.map(CxMapperScriptModel::getRequiredFields)
				.filter(Objects::nonNull)
				.flatMap(Collection::stream)
				.filter(StringUtils::isNotBlank)
				.map(String::trim)
				.collect(Collectors.toSet());
		return requiredFields;
	}
}
