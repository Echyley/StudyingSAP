/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.cmsitems.populators;

import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.components.SimpleCMSComponentModel;
import de.hybris.platform.cmsfacades.cmsitems.attributeconverters.UniqueIdentifierAttributeToDataContentConverter;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/*
 * To avoid the AbstractCMSComponent's children is empty when components data was imported by impex file
 */
public class CMSComponentChildrenModelToDataAttributePopulator extends AbstractCMSItemPopulator
{
	private TypeService typeService;
	private ModelService modelService;
	private UniqueIdentifierAttributeToDataContentConverter<ItemModel> uniqueIdentifierAttributeToDataContentConverter;

	@Override
	public void populate(ItemModel itemModel, Map<String, Object> stringObjectMap) throws ConversionException
	{
		if (itemModel instanceof AbstractCMSComponentModel)
		{
			Set<String> children = new HashSet<>();
			AbstractCMSComponentModel cmsComponentModel = (AbstractCMSComponentModel)itemModel;
			final ComposedTypeModel composedType = getTypeService().getComposedTypeForCode(itemModel.getItemtype());
			composedType.getDeclaredattributedescriptors().stream().filter(AttributeDescriptorModel::getWritable)
					.filter(attribute -> !attribute.getQualifier().equals(SimpleCMSComponentModel.CONTAINERS))
					.map(attribute -> getModelService().getAttributeValue(cmsComponentModel, attribute.getQualifier()))
					.filter(Objects::nonNull)
					.forEach(attributeValue -> updateChildren(attributeValue, children));
			stringObjectMap.put(AbstractCMSComponentModel.CHILDREN, children);
		}
	}

	private void updateChildren(final Object childValue, Set<String> children)
	{
		if (childValue instanceof AbstractCMSComponentModel)
		{
			children.add(
					this.getUniqueIdentifierAttributeToDataContentConverter().convert((AbstractCMSComponentModel) childValue)
			);
		}
		else if (childValue instanceof Collection)
		{
			final List<?> cmsComponentModels = ((Collection<?>) childValue).stream()
					.filter(AbstractCMSComponentModel.class::isInstance).collect(Collectors.toList());

			for (final AbstractCMSComponentModel child : (List<AbstractCMSComponentModel>) cmsComponentModels)
			{
				children.add(
						this.getUniqueIdentifierAttributeToDataContentConverter().convert(child)
				);
			}
		}
	}

	protected TypeService getTypeService()
	{
		return typeService;
	}

	public void setTypeService(TypeService typeService)
	{
		this.typeService = typeService;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	public void setModelService(ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected UniqueIdentifierAttributeToDataContentConverter<ItemModel> getUniqueIdentifierAttributeToDataContentConverter()
	{
		return uniqueIdentifierAttributeToDataContentConverter;
	}

	public void setUniqueIdentifierAttributeToDataContentConverter(
			final UniqueIdentifierAttributeToDataContentConverter<ItemModel> uniqueIdentifierAttributeToDataContentConverter)
	{
		this.uniqueIdentifierAttributeToDataContentConverter = uniqueIdentifierAttributeToDataContentConverter;
	}
}
