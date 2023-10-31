/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saps4omservices.cache.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.saps4omservices.cache.exceptions.SAPS4OMHybrisCacheException;
import de.hybris.platform.sap.core.saps4omservices.cache.service.impl.CacheAccessImpl;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CacheAccessImplTest {
	
	CacheAccessImpl cacheAccessImpl;
	
	@Before
	public void setup() {
		cacheAccessImpl = new CacheAccessImpl("test1", 10);
		cacheAccessImpl.init();
	}
	
	@Test
	public void testConstructors() {
		
		CacheAccessImpl cacheAccess = new CacheAccessImpl("test1", 10);
		assertNotNull(cacheAccess);
		
		cacheAccess = new CacheAccessImpl("test1", 10, "LIFO");
		assertNotNull(cacheAccess);
		
		cacheAccess = new CacheAccessImpl("test1", 10, "LIFO", false, false);
		assertNotNull(cacheAccess);
		
		cacheAccess = new CacheAccessImpl("test1", 10, "LIFO", false, false, 2000L);
		assertNotNull(cacheAccess);
	}
	
	@Test
	public void testGet() {
		
		Object value = cacheAccessImpl.get("abcf");
		assertNull(value);
		
	}

	@Test
	public void testGetKeysEmpty() {
		
		assertEquals(0, cacheAccessImpl.getKeys().size());
		
	}
	
	@Test
	public void testGetKeys() {
		
		try {
			cacheAccessImpl.put("key", "value");
			assertEquals(1, cacheAccessImpl.getKeys().size());
			cacheAccessImpl.remove("key");
		} catch (SAPS4OMHybrisCacheException e) {
			assertNotNull(e);
		}
		
	}
	
	@Test
	public void testPutIfAbsent() {
		
		try {
			cacheAccessImpl.putIfAbsent("key", "value");
			assertEquals(1, cacheAccessImpl.getKeys().size());
			cacheAccessImpl.remove("key");
		} catch (SAPS4OMHybrisCacheException e) {
			assertNotNull(e);
		}
		
	}
	
	@Test
	public void testPutIfAbsentWhenKeyExists() {
		
		try {
			cacheAccessImpl.put("key", "value");
			cacheAccessImpl.putIfAbsent("key", "value");
			assertEquals(1, cacheAccessImpl.getKeys().size());
			cacheAccessImpl.remove("key");
		} catch (SAPS4OMHybrisCacheException e) {
			assertNotNull(e);
		}
		
	}
	
	@Test
	public void testClearCache() {
		
		try {
			cacheAccessImpl.put("key", "value");
			cacheAccessImpl.clearCache();
			assertEquals(0, cacheAccessImpl.getNumObjects());
		} catch (SAPS4OMHybrisCacheException e) {
			assertNotNull(e);
		}
		
	}
}
