/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modals.data;

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;


/**
 * Represents attributes which have been selected on wizard. Moreover the class contains information whether 'use full
 * qualifiers' checkbox is selected or not.
 */
public class SelectedClassificationAttributesData
{
	private final Collection<ClassAttributeAssignmentModel> assignments;
	private final boolean useFullQualifier;

	public SelectedClassificationAttributesData(final Collection<ClassAttributeAssignmentModel> assignments,
	                                            final boolean useFullQualifier)
	{
		this.assignments = CollectionUtils.isNotEmpty(assignments) ? new ArrayList<>(assignments): new ArrayList<>();
		this.useFullQualifier = useFullQualifier;
	}

	public Collection<ClassAttributeAssignmentModel> getAssignments()
	{
		return List.copyOf(assignments);
	}

	public boolean isUseFullQualifier()
	{
		return useFullQualifier;
	}

}
