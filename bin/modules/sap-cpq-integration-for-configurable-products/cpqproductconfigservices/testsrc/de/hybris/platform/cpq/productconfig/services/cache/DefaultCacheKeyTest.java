/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.services.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.regioncache.key.CacheUnitValueType;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;


/**
 * Unit test for {@link DefaultCacheKey}
 */
@UnitTest
public class DefaultCacheKeyTest
{
	private static final String ANOTHER_VALUE = "another value";
	private static final String ANOTHER_KEY = "another key";
	private static final String VALUE = "value";
	private static final String KEY = "key";
	private static final String TENANT_ID = "tenant id";
	private static final String TYPE_CODE = "type code";
	private DefaultCacheKey classUnderTest;
	private Map<String, String> keys;

	@Before
	public void setup()
	{
		keys = new HashMap<>();
		keys.put(KEY, VALUE);
		classUnderTest = new DefaultCacheKey(keys, CacheUnitValueType.SERIALIZABLE, TYPE_CODE, TENANT_ID);
	}

	@Test
	public void testConstructor()
	{
		assertEquals(CacheUnitValueType.SERIALIZABLE, classUnderTest.getCacheValueType());
		assertEquals(TYPE_CODE, classUnderTest.getTypeCode());
		assertEquals(TENANT_ID, classUnderTest.getTenantId());
		assertEquals(keys, classUnderTest.getKeys());
	}

	@Test
	public void testConstructorNullKeys()
	{
		final DefaultCacheKey cacheKey = new DefaultCacheKey(null, CacheUnitValueType.SERIALIZABLE, TYPE_CODE, TENANT_ID);
		assertNull(cacheKey.getKeys());
	}

	@Test
	public void testAlternativeConstructor()
	{
		classUnderTest = new DefaultCacheKey(keys, TYPE_CODE, TENANT_ID);
		assertEquals(CacheUnitValueType.NON_SERIALIZABLE, classUnderTest.getCacheValueType());
	}

	@Test
	public void testAlternativeConstructorNull()
	{
		classUnderTest = new DefaultCacheKey(null, TYPE_CODE, TENANT_ID);
		assertNull(classUnderTest.getKeys());
	}

	@Test
	public void testHashCode()
	{
		assertNotEquals(0, classUnderTest.hashCode());
	}

	@Test
	public void testHashCodeIsEqual()
	{
		final DefaultCacheKey anotherCacheKey = new DefaultCacheKey(keys, CacheUnitValueType.SERIALIZABLE, TYPE_CODE, TENANT_ID);
		assertEquals(classUnderTest.hashCode(), anotherCacheKey.hashCode());
	}

	@Test
	public void testEqualsNotEqual()
	{
		keys.put(ANOTHER_KEY, ANOTHER_VALUE);
		final DefaultCacheKey anotherCacheKey = new DefaultCacheKey(keys, CacheUnitValueType.SERIALIZABLE, TYPE_CODE, TENANT_ID);
		assertNotEquals(classUnderTest,anotherCacheKey);
	}

	@Test
	public void testEquals()
	{
		assertEquals(classUnderTest, classUnderTest);
		assertNotEquals(
				classUnderTest, new DefaultCacheKey(keys, CacheUnitValueType.SERIALIZABLE, "another type code", TENANT_ID));
		assertNotEquals(classUnderTest, new DefaultCacheKey(keys, CacheUnitValueType.SERIALIZABLE, TYPE_CODE, "another tenant"));
		assertNotNull(classUnderTest);
	}

	@Test
	public void testEqualsKeysNull()
	{
		final DefaultCacheKey cacheKeyNullKeys = new DefaultCacheKey(null, CacheUnitValueType.SERIALIZABLE, TYPE_CODE, TENANT_ID);
		final DefaultCacheKey cacheKeyNullKeys2 = new DefaultCacheKey(null, CacheUnitValueType.SERIALIZABLE, TYPE_CODE, TENANT_ID);
		assertNotEquals(classUnderTest, cacheKeyNullKeys);
		assertNotEquals(cacheKeyNullKeys, classUnderTest);
		assertEquals(cacheKeyNullKeys, cacheKeyNullKeys2);
	}

	@Test
	public void testEqualsDifferentClass()
	{
		final DifferentCacheKey differentCacheKey = new DifferentCacheKey(keys, CacheUnitValueType.SERIALIZABLE, TYPE_CODE,
				TENANT_ID);
		assertNotEquals(classUnderTest,differentCacheKey);
	}

	@Test
	public void testEqualsCacheUnitValueTypeIsIgnored()
	{
		assertEquals(classUnderTest, new DefaultCacheKey(keys, CacheUnitValueType.NON_SERIALIZABLE, TYPE_CODE, TENANT_ID));
	}

	@Test
	public void testEqualsIsEqual()
	{
		final DefaultCacheKey anotherCacheKey = new DefaultCacheKey(keys, CacheUnitValueType.SERIALIZABLE, TYPE_CODE, TENANT_ID);
		assertEquals(classUnderTest, anotherCacheKey);
	}

	@Test
	public void testEqualsIsNotEqualToNull()
	{
		assertNotNull(classUnderTest);
	}


	@Test
	public void testEqualsIsEqualIndependentObjects()
	{
		final Map<String, String> anotherKeys = new HashMap();
		anotherKeys.put(KEY, VALUE);
		final DefaultCacheKey anotherCacheKey = new DefaultCacheKey(anotherKeys, CacheUnitValueType.SERIALIZABLE, TYPE_CODE,
				TENANT_ID);
		assertEquals(classUnderTest, anotherCacheKey);
	}


	@Test
	public void testHashCodeIsNotEqual()
	{
		keys.put(ANOTHER_KEY, ANOTHER_VALUE);
		final DefaultCacheKey anotherCacheKey = new DefaultCacheKey(keys, CacheUnitValueType.SERIALIZABLE, TYPE_CODE, TENANT_ID);
		assertNotEquals(classUnderTest.hashCode(), anotherCacheKey.hashCode());
	}

	@Test
	public void testHashCodeIsNotEqualNull()
	{
		final DefaultCacheKey anotherCacheKey = new DefaultCacheKey(null, CacheUnitValueType.SERIALIZABLE, TYPE_CODE, TENANT_ID);
		assertNotEquals(classUnderTest.hashCode(), anotherCacheKey.hashCode());
	}

	@Test
	public void testHashCodeIsCached()
	{
		assertTrue(classUnderTest.toString().contains("0"));
		final int hashCode = classUnderTest.hashCode();
		assertTrue(classUnderTest.toString().contains(String.valueOf(hashCode)));
		final int hashCodeSecondCall = classUnderTest.hashCode();
		assertEquals(hashCode, hashCodeSecondCall);
	}

	@Test
	public void testKeysAreImmutable()
	{
		keys.put(ANOTHER_KEY, ANOTHER_VALUE);
		final DefaultCacheKey anotherCacheKey = new DefaultCacheKey(keys, CacheUnitValueType.SERIALIZABLE, TYPE_CODE, TENANT_ID);
		assertEquals(1, classUnderTest.getKeys().size());
		assertEquals(2, anotherCacheKey.getKeys().size());
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testGetKeysImmutable()
	{
		classUnderTest.getKeys().put(ANOTHER_KEY, ANOTHER_VALUE);
	}

	@Test
	public void testToString()
	{
		final String result = classUnderTest.toString();
		assertNotNull(result);
		assertTrue(result.contains(KEY));
		assertTrue(result.contains(VALUE));
		assertTrue(result.contains(TYPE_CODE));
		assertTrue(result.contains(TENANT_ID));
		assertTrue(result.contains(CacheUnitValueType.SERIALIZABLE.toString()));
		assertTrue(result.contains("0"));
	}

	private class DifferentCacheKey extends DefaultCacheKey
	{
		public DifferentCacheKey(final Map<String, String> keys, final CacheUnitValueType valueType, final String typeCode,
				final String tenantId)
		{
			super(keys, valueType, typeCode, tenantId);
		}

	}

}
