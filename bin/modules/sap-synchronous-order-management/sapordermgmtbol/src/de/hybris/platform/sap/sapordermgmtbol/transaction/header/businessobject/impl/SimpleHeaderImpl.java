/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject.impl;

import de.hybris.platform.sap.core.bol.businessobject.BusinessObjectBase;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.sapcommonbol.transaction.util.impl.PrettyPrinter;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.order.businessobject.interf.Text;
import de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject.interf.SimpleHeader;

import java.util.Map;

import javax.annotation.Resource;


/**
 */
@SuppressWarnings("squid:S2160")
public class SimpleHeaderImpl extends BusinessObjectBase implements SimpleHeader
{

	private boolean dirty = false;
	private String description;
	@Resource(name = SapordermgmtbolConstants.ALIAS_BEAN_TEXT)

	@SuppressWarnings("squid:S1948")
	private Text text;


	@Override
	public void clear()
	{
		dirty = true;
		description = null;
		if (null != text)
		{
			text.clear();
		}

	}

	@Override
	public boolean isDirty()
	{
		return dirty;
	}

	@Override
	public void setDirty(final boolean isDirty)
	{
		this.dirty = isDirty;
	}

	@Override
	public String getDescription()
	{
		return description;
	}

	@Override
	public Text getText()
	{
		return text;
	}

	@Override
	public void setDescription(final String description)
	{
		this.description = description;
	}

	@Override
	public void setText(final Text text)
	{
		this.text = text;
	}

	/**
	 * Instead of a shallow-copy this returns a deep-copy of this <tt>SimpleHeaderImpl</tt> instance.
	 *
	 * @return a deep-copy of this SimpleHeader
	 */
	@Override
	@SuppressWarnings("squid:S2975")
	public Object clone()
	{

		SimpleHeaderImpl myClone;
		try
		{
			myClone = (SimpleHeaderImpl) super.clone();
		}
		catch (final CloneNotSupportedException ex)
		{
			// should not happen, because we are clone able
			throw new ApplicationBaseRuntimeException(
					"Failed to clone Object, check whether Cloneable Interface is still implemented", ex);
		}

		if (text != null)
		{
			myClone.text = text.clone();
		}

		return myClone;
	}

	// we don't get the map type safe from the backend
	@Override
	public Map<String, Object> getTypedExtensionMap()
	{
		return this.getExtensionMap();
	}

	@Override
	public String toString()
	{
		final PrettyPrinter pp = new PrettyPrinter(super.toString());
		pp.add(description, "description");
		pp.add(Boolean.valueOf(dirty), "isDirty");
		return pp.toString();
	}
}
