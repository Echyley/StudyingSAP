/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modals.builders;

import de.hybris.platform.integrationservices.model.IntegrationObjectModel;

import java.util.List;
import java.util.Map;

public class IntegrationObjectAuditReportBuilder extends AbstractAuditReportBuilder
{
	private static final String RETURN_INTEGRATION_OBJECT_ITEM = "returnIntegrationObjectItem";
	private static final String COMPOSED_TYPE = "ComposedType_";
	private static final String COMPOSED_TYPE_OF_ITEM = "ComposedTypeOfItem";

	@Override
	public void traversePayload(final Map<String, Object> map)
	{
		if (!containsReturnIntegrationObjectItem(map))
		{
			map.entrySet().forEach(object -> {

				final Map.Entry entry = (Map.Entry) object;
				final Object value = entry.getValue();
				traverseMapOrList(value);
			});
		}
	}

	private void traverseMapOrList(final Object value)
	{
		if (value instanceof Map)
		{
			traversePayload((Map) value);
		}
		else if (value instanceof List)
		{
			for (final Object listElement : (List) value)
			{
				if (listElement instanceof Map)
				{
					traversePayload((Map) listElement);
				}
			}
		}
	}

	private boolean containsReturnIntegrationObjectItem(final Map<String, Object> map)
	{
		if (map.containsKey(RETURN_INTEGRATION_OBJECT_ITEM))
		{
			replaceReturnIntegrationObjectItemWithComposedType(map);
			return true;
		}
		return false;
	}

	private void replaceReturnIntegrationObjectItemWithComposedType(final Map<String, Object> map)
	{

		final Object itemObject = map.get(RETURN_INTEGRATION_OBJECT_ITEM);
		String type = "";

		if (itemObject instanceof Map)
		{
			if (((Map) itemObject).containsKey(COMPOSED_TYPE_OF_ITEM)
					&& ((Map) itemObject).get(COMPOSED_TYPE_OF_ITEM) instanceof Map
					&& ((Map) ((Map) itemObject).get(COMPOSED_TYPE_OF_ITEM)).get(COMPOSED_TYPE) instanceof String)
			{
				type = (String) ((Map) ((Map) itemObject).get(COMPOSED_TYPE_OF_ITEM)).get(COMPOSED_TYPE);
				map.put(COMPOSED_TYPE, type);
			}

			map.remove(RETURN_INTEGRATION_OBJECT_ITEM);
		}
	}

	@Override
	public String getDownloadFileName()
	{
		if (this.getSelectedModel() == null)
		{
			throw new ItemModelNotSelectedForReportException(IntegrationObjectModel._TYPECODE);
		}
		return ((IntegrationObjectModel) this.getSelectedModel()).getCode();
	}
}
