/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto;

import de.hybris.platform.catalog.enums.ClassificationAttributeTypeEnum;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.integrationbackoffice.utility.QualifierNameUtils;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.IntegrationObjectDefinition;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.ReadService;

import java.util.NoSuchElementException;
import java.util.Optional;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.BooleanUtils;

import com.google.common.base.Preconditions;

/**
 * Represents a {@link de.hybris.platform.integrationservices.model.IntegrationObjectItemClassificationAttributeModel} stored as the
 * value of a {@link org.zkoss.zul.Listitem} in the modeling view.
 */
public class ListItemClassificationAttributeDTO extends AbstractListItemDTO
{
	private final ClassAttributeAssignmentModel classAttributeAssignmentModel;
	private final String classificationAttributeCode;
	private final String categoryCode;

	/**
	 * Represents a {@link de.hybris.platform.integrationservices.model.IntegrationObjectItemClassificationAttributeModel} stored as the
	 * value of a {@link org.zkoss.zul.Listitem} in the modeling view.
	 *
	 * @param selected                      Flag for whether the listitem is selected in the UI
	 * @param customUnique                  If the unique checkbox has been checked by the user through the UI
	 * @param autocreate                    If the autocreate checkbox has been checked by the user through the UI
	 * @param classAttributeAssignmentModel Contains the type information of a classification attribute
	 * @param alias                         The attribute name of the {@link de.hybris.platform.integrationservices.model.IntegrationObjectItemClassificationAttributeModel}
	 */
	ListItemClassificationAttributeDTO(final boolean selected, final boolean customUnique, final boolean autocreate,
	                                   @NotNull final ClassAttributeAssignmentModel classAttributeAssignmentModel,
	                                   final String alias, final String typeAlias)
	{
		super(selected, customUnique, autocreate, typeAlias);
		Preconditions.checkArgument(classAttributeAssignmentModel != null, "Class attribute assignment can't be null.");
		this.classAttributeAssignmentModel = classAttributeAssignmentModel;
		this.classificationAttributeCode = createClassificationAttributeCode(classAttributeAssignmentModel);
		this.categoryCode = classAttributeAssignmentModel.getClassificationClass().getCode();
		this.alias = createAlias(alias);
		createDescription();
	}

	/**
	 * Instantiates a builder for this class.
	 *
	 * @param classAttributeAssignmentModel the object containing the type information of the classification attribute the list item represents.
	 * @return a builder for this class.
	 */
	public static ListItemClassificationAttributeDTOBuilder builder(
			@NotNull final ClassAttributeAssignmentModel classAttributeAssignmentModel)
	{
		Preconditions.checkArgument(classAttributeAssignmentModel != null, "Class attribute assignment can't be null.");
		return new ListItemClassificationAttributeDTOBuilder(classAttributeAssignmentModel);
	}

	@Override
	public void setAlias(final String alias)
	{
		this.alias = createAlias(alias);
	}

	public ClassAttributeAssignmentModel getClassAttributeAssignmentModel()
	{
		return classAttributeAssignmentModel;
	}

	public String getClassificationAttributeCode()
	{
		return classificationAttributeCode;
	}

	public String getCategoryCode()
	{
		return categoryCode;
	}

	@Override
	public AbstractListItemDTO findMatch(final IntegrationObjectDefinition currentAttributesMap,
	                                     final IntegrationMapKeyDTO parentKey)
	{
		final ListItemClassificationAttributeDTO match;
		final Optional<ListItemClassificationAttributeDTO> optionalListItemClassificationAttributeDTO = currentAttributesMap
				.getAttributesByKey(parentKey)
				.stream()
				.filter(ListItemClassificationAttributeDTO.class::isInstance)
				.map(ListItemClassificationAttributeDTO.class::cast)
				.filter(listItemDTO -> listItemDTO
						.getCategoryCode()
						.equals(categoryCode)
						&& listItemDTO
						.getClassificationAttributeCode()
						.equals(classificationAttributeCode))
				.findFirst();
		match = optionalListItemClassificationAttributeDTO
				.orElseThrow(() -> new NoSuchElementException(
						String.format("No ClassificationAttribute was found for %s", classificationAttributeCode)));

		return match;
	}

	@Override
	public boolean isComplexType(final ReadService readService)
	{
		return classAttributeAssignmentModel.getReferenceType() != null;
	}

	@Override
	public String getQualifier()
	{
		return getClassificationAttributeCode();
	}

	@Override
	public TypeModel getType()
	{
		return classAttributeAssignmentModel.getReferenceType();
	}

	@Override
	public boolean isStructureType()
	{
		return false;
	}

	private String createClassificationAttributeCode(final ClassAttributeAssignmentModel classAttributeAssignmentModel)
	{
		final String attributeCode = classAttributeAssignmentModel.getClassificationAttribute().getCode();
		return QualifierNameUtils.removeNonAlphaNumericCharacters(attributeCode);
	}

	private String createAlias(final String alias)
	{
		return "".equals(alias) ? classificationAttributeCode : alias;
	}

	@Override
	public final void createDescription()
	{
		final boolean isLocalized = BooleanUtils.isTrue(classAttributeAssignmentModel.getLocalized());
		String classificationType = "";

		if (isLocalized)
		{
			classificationType = "localized:";
		}
		if (classAttributeAssignmentModel.getReferenceType() == null)
		{
			if (classAttributeAssignmentModel.getAttributeType() == ClassificationAttributeTypeEnum.ENUM)
			{
				classificationType += "ValueList";
			}
			else
			{
				classificationType += classAttributeAssignmentModel.getAttributeType().getCode();
			}
		}
		else
		{
			classificationType += classAttributeAssignmentModel.getReferenceType().getCode();
		}

		final Boolean isMultivalued = classAttributeAssignmentModel.getMultiValued();
		if (Boolean.TRUE.equals(isMultivalued))
		{
			description = String.format("Collection [%s]", classificationType);
		}
		else
		{
			description = classificationType;
		}
	}

	@Override
	public String getTypeCode()
	{
		if (StringUtils.isNotBlank(typeAlias))
		{
			return typeAlias;
		}
		else if (getType() != null)
		{
			return getType().getCode();
		}
		return classAttributeAssignmentModel.getAttributeType().getCode();
	}
}
