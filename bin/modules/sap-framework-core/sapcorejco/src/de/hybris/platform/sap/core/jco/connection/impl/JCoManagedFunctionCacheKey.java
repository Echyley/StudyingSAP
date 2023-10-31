/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.jco.connection.impl;

import de.hybris.platform.regioncache.key.CacheKey;
import de.hybris.platform.regioncache.key.CacheUnitValueType;


/**
 * The <code>JCoManagedFunctionCacheKey</code> is a simple implemantation of the <code>CacheKey</code> and is used within JCoManagedFunctionCache. <br>
 * 
 */
public class JCoManagedFunctionCacheKey implements CacheKey
{

	/**
	 * Default typecode used if no other is specified.
	 */
	public static final String DEFAULT_JCO_TYPECODE = "JCoManagedFunction";
	
	/**
	 * Typecode of the JCoManagedFunctionCacheKey.
	 */
	private final Object typeCode;

	/**
	 * Tenant of the JCoManagedFunctionCacheKey.
	 */
	private String tenant;

	/**
	 * Key of the JCoManagedFunctionCacheKey.
	 */
	private Object key;

	/**
	 * Valuetype of the JCoManagedFunctionCacheKey.
	 */
	private CacheUnitValueType valueType;

	/**
	 * Creates GenericCacheKey for given key and type code.
	 * 
	 * @param key
	 *           key of the GenericCacheKey
	 * @param typeCode
	 *           typecode of the GenericCacheKey
	 */
	public JCoManagedFunctionCacheKey(final Object key, final String typeCode)
	{
		this.key = key;
		this.typeCode = typeCode;
	}

	/**
	 * Creates GenericCacheKey for given key and type code.
	 * 
	 * @param key
	 *           key of the GenericCacheKey
	 */
	public JCoManagedFunctionCacheKey(final Object key)
	{
		this.key = key;
		this.typeCode = null;
	}



	@Override
	public int hashCode()
	{
		return this.key.hashCode();
	}


	@Override
	public String getTenantId()
	{
		return tenant;
	}

	@Override
	public Object getTypeCode()
	{
		if (typeCode != null)
		{
			return typeCode.toString();
		}
		return DEFAULT_JCO_TYPECODE;
	}


	@Override
	public CacheUnitValueType getCacheValueType()
	{
		return valueType;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		
		return true;
	}

}
