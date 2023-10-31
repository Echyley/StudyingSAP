/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp;

import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.ItemBuffer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Standard implementation for {@link ItemBuffer}.
 * 
 */
public class ItemBufferImpl implements ItemBuffer
{
	/**
	 * The list of items which represents the ERP status. We use it to compile a delta to the status we get from the BO
	 * layer. Aim is to optimise the LO-API call
	 */
	private Map<String, Item> itemsERPState;

	/**
	 * Initialization
	 */
	public void init()
	{
		this.setItemsERPState(new HashMap<String, Item>());
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.hybris.app.esales.module.transaction.salesdocument.backend.impl. erp.ItemBuffer#getItemsERPState()
	 */
	@Override
	public Map<String, Item> getItemsERPState()
	{
		return itemsERPState;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.hybris.app.esales.module.transaction.salesdocument.backend.impl.
	 * erp.ItemBuffer#setItemsERPState(java.util.Map)
	 */
	@Override
	public void setItemsERPState(final Map<String, Item> itemsERPState)
	{
		this.itemsERPState = itemsERPState;
	}

	@Override
	public void removeItemERPState(final String idAsString)
	{
		// first remove main item
		itemsERPState.remove(idAsString);

		// now remove childs
		final List<String> childs = new ArrayList<String>();
		for (final Item item : itemsERPState.values())
		{

			final TechKey parentId = item.getParentId();
			if (parentId != null && parentId.getIdAsString().equals(idAsString))
			{
				childs.add(item.getTechKey().getIdAsString());
			}
		}

		for (final String key : childs)
		{
			itemsERPState.remove(key);
		}

	}

	@Override
	public void clearERPBuffer()
	{
		if (getItemsERPState() != null)
		{
			getItemsERPState().clear();
		}
	}
}
