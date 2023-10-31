/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.interf.model.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.model.ConflictingAssumptionModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.SolvableConflictModel;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Default implementation of {@link SolvableConflictModel}
 */
public class SolvableConflictModelImpl extends BaseModelImpl implements SolvableConflictModel
{

	private String description;
	private List<ConflictingAssumptionModel> conflictingAssumptions;
	private String id;

	@Override
	public void setDescription(final String description)
	{
		this.description = description;

	}

	@Override
	public String getDescription()
	{
		return description;
	}

	@Override
	public void setConflictingAssumptions(final List<ConflictingAssumptionModel> assumptions)
	{
		this.conflictingAssumptions = Optional.ofNullable(assumptions).map(List::stream).orElseGet(Stream::empty)
				.collect(Collectors.toList());

	}

	@Override
	public List<ConflictingAssumptionModel> getConflictingAssumptions()
	{
		return Optional.ofNullable(conflictingAssumptions).map(List::stream).orElseGet(Stream::empty).collect(Collectors.toList());
	}

	@Override
	public String toString()
	{
		final StringBuilder builder = new StringBuilder(50);
		builder.append("\n[SolvableConflictModelImpl [description=");
		builder.append(description);
		builder.append(", conflictingAssumptions=");
		builder.append(conflictingAssumptions);
		builder.append("]]");
		return builder.toString();
	}


	@Override
	public void setId(final String id)
	{
		this.id = id;
	}

	@Override
	public String getId()
	{
		return id;
	}

}
