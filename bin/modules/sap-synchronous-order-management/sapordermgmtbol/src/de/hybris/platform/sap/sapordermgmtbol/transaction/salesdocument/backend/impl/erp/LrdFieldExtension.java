/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp;

import java.util.ArrayList;
import java.util.List;


/**
 * Represents a LO-API specific extension. Only possible to extend the header and item segment.
 * 
 */
public class LrdFieldExtension
{

	/** Header object */
	public static final String objectHead = "HEAD";
	/** Item object */
	public static final String objectItem = "ITEM";

	/**
	 * The specific extension types.
	 * 
	 */
	public enum FieldType
	{

		/** Changeable header fields */
		HeadComV(objectHead),
		/** Readonly header fields */
		HeadComR(objectHead),
		/** Changeable item fields */
		ItemComV(objectItem),
		/** Readonly item fields */
		ItemComR(objectItem);

		private String objectType = null;

		private FieldType(final String objectType)
		{
			this.objectType = objectType;
		}

		/**
		 * @return Object type (HEAD or ITEM)
		 */
		public String getObjectType()
		{
			return this.objectType;
		}

	}

	private List<String> fieldnames = null;
	private FieldType fieldType = null;

	/**
	 * @return list of field names
	 */
	public List<String> getFieldnames()
	{
		return fieldnames;
	}

	/**
	 * @param type
	 *           field type
	 */
	public LrdFieldExtension(final FieldType type)
	{
		this.fieldnames = new ArrayList<String>();
		this.fieldType = type;
	}

	/**
	 * @param field
	 *           to be addes
	 */
	public void addField(final String field)
	{
		this.fieldnames.add(field);
	}

	/**
	 * @return field type
	 */
	public FieldType getFieldType()
	{
		return fieldType;
	}

}
