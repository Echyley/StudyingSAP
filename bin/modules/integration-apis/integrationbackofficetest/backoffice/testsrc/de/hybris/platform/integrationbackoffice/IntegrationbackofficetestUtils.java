/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;


import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.integrationservices.model.AbstractIntegrationObjectItemAttributeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class IntegrationbackofficetestUtils
{
	public static ItemModel itemModelMock(final long pk)
	{
		final ItemModel itemModel = mock(ItemModel.class);
		lenient().when(itemModel.getPk()).thenReturn(PK.fromLong(pk));
		return itemModel;
	}

	public static Set<ItemModel> mutableSetOfItemModels(final ItemModel... itemModels)
	{
		final Set<ItemModel> set = new HashSet<>();
		Collections.addAll(set, itemModels);
		return set;
	}

	public static ComposedTypeModel composedTypeModel(final String code)
	{
		final ComposedTypeModel composedTypeModel = new ComposedTypeModel();
		composedTypeModel.setCode(code);
		return composedTypeModel;
	}

	public static AttributeDescriptorModel attributeDescriptorModel(final TypeModel typeModel, final String qualifier)
	{
		final AttributeDescriptorModel descriptorModel = new AttributeDescriptorModel();
		descriptorModel.setAttributeType(typeModel);
		descriptorModel.setQualifier(qualifier);
		return descriptorModel;
	}

	public static IntegrationObjectItemModel integrationObjectItemModel(final String code,
	                                                                    final ComposedTypeModel composedTypeModel)
	{
		final IntegrationObjectItemModel integrationObjectItemModel = new IntegrationObjectItemModel();
		integrationObjectItemModel.setCode(code);
		integrationObjectItemModel.setType(composedTypeModel);
		return integrationObjectItemModel;
	}

	public static IntegrationObjectModel integrationObjectModel(final String code)
	{
		final IntegrationObjectModel integrationObjectModel = new IntegrationObjectModel();
		integrationObjectModel.setCode(code);
		return integrationObjectModel;
	}

	public static IntegrationObjectModel integrationObjectModelMock(final IntegrationObjectItemModel rootItem)
	{
		final IntegrationObjectModel integrationObjectModel = mock(IntegrationObjectModel.class);
		lenient().when(integrationObjectModel.getRootItem()).thenReturn(rootItem);
		return integrationObjectModel;
	}

	public static IntegrationObjectModel integrationObjectModelMock(final String code, final IntegrationObjectItemModel rootItem)
	{
		final IntegrationObjectModel integrationObjectModel = integrationObjectModelMock(rootItem);
		lenient().when(integrationObjectModel.getCode()).thenReturn(code);
		return integrationObjectModel;
	}

	public static IntegrationObjectItemModel getItemInIntegrationObject(final IntegrationObjectModel integrationObject,
	                                                                    final String itemCode)
	{
		return integrationObject.getItems()
		                        .stream()
		                        .filter(item -> item.getCode().equals(itemCode))
		                        .findFirst()
		                        .orElseThrow(NullPointerException::new);
	}

	public static AbstractIntegrationObjectItemAttributeModel getAttributeInItem(final IntegrationObjectItemModel item,
	                                                                             final String attrName)
	{
		return item.getAttributes()
		           .stream()
		           .filter(attr -> attr.getAttributeName().equals(attrName))
		           .findFirst()
		           .orElseThrow(NullPointerException::new);
	}

}
