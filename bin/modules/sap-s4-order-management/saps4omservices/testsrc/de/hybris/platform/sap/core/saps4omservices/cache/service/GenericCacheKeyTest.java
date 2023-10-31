/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.saps4omservices.cache.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.regioncache.key.CacheUnitValueType;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class GenericCacheKeyTest {	
	
	@Mock
	CacheUnitValueType cacheUnitValueType;
	
	private String typecode = "carrierTypeCode";
	
	private final Object[] key = new String[]
	{ "structureName", "fieldName" };
	
	private final Object[] key2 = new String[]
	{ "abc", "123" };
	
	private final Object[] key3 = new String[]
	{ null, "123" };
	
	private final GenericCacheKey genericCacheKey1 = new GenericCacheKey(key, typecode);
	private final GenericCacheKey genericCacheKey2 = new GenericCacheKey(key, "carrierTypeCode1");
	private final GenericCacheKey genericCacheKey3 = new GenericCacheKey(key2, typecode);
	private final GenericCacheKey genericCacheKey4 = new GenericCacheKey(key2, null);
	private final GenericCacheKey genericCacheKey5 = new GenericCacheKey(key2, "typecode", "tenant", cacheUnitValueType );
	private final GenericCacheKey genericCacheKey6 = new GenericCacheKey(key, "typecode", "tenant", cacheUnitValueType );
	private final GenericCacheKey genericCacheKey7 = new GenericCacheKey(key3, null);

	/**
	 * genericCacheKey Equals test.
	 */
	@Test
	public void testEquals()
	{
		assertEquals(genericCacheKey1, genericCacheKey2);
		assertNotEquals(genericCacheKey1, genericCacheKey3);
		assertNotEquals(genericCacheKey5,genericCacheKey6);
		assertNotEquals(genericCacheKey2, genericCacheKey7);

	}

	/**
	 * genericCacheKey type code test.
	 */
	@Test
	public void testTypeCode()
	{
		assertEquals("carrierTypeCode", genericCacheKey1.getTypeCode());
		assertEquals("SAPObjects", genericCacheKey4.getTypeCode());
		
	}

	@Test
	public void testGetCacheValueType() {
		assertNull(genericCacheKey1.getCacheValueType());
		assertNotNull(genericCacheKey1.toString());
	}
	
	@Test
	public void testToString() {
		assertNotNull(genericCacheKey1.toString());
	}

	@Test
	public void testHashCode() {
		assertFalse(genericCacheKey1.hashCode() > 0);
	}

}
