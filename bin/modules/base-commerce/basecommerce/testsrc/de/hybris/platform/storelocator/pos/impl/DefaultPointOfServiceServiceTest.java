/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.storelocator.pos.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.pojo.StoreCountInfo;
import de.hybris.platform.store.pojo.StoreCountType;
import de.hybris.platform.storelocator.PointOfServiceDao;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.mockito.Mockito;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPointOfServiceServiceTest
{
	public static final int SYRIA_STORE_COUNT = 0;
	public static final int GERMANY_STORE_COUNT = 5;
	public static final int GERMANREGION1COUNT = 3;
	public static final int GERMANREGION2COUNT = 2;
	public static final int JAPAN_STORE_COUNT = 3;
	public static final String GERMANY = "Germany";
	public static final String JAPAN = "Japan";
	public static final String SYRIA = "Syria";
	public static final String REGION = "_Region";
	@InjectMocks
	private DefaultPointOfServiceService serviceUnderTest;
	@Mock
	private PointOfServiceDao pointOfServiceDao;
	@Mock
	private RegionModel germanRegion1, germanRegion2, japanRegion;
	@Mock
	private CountryModel germany, japan, syria;
	@Mock
	private BaseStoreModel baseStore;


	@Before
	public void setUp()
	{
		final Map<CountryModel, Integer> countryCountMap = new HashMap<>();
		Mockito.lenient().when(germany.getName()).thenReturn(GERMANY);
		Mockito.lenient().when(japan.getName()).thenReturn(JAPAN);
		Mockito.lenient().when(syria.getName()).thenReturn(SYRIA);

		countryCountMap.put(syria, SYRIA_STORE_COUNT);
		countryCountMap.put(germany, GERMANY_STORE_COUNT);
		countryCountMap.put(japan, JAPAN_STORE_COUNT);

		Mockito.lenient().when(pointOfServiceDao.getPointOfServiceCountPerCountryForStore(baseStore)).thenReturn(countryCountMap);
	}

	@Test
	public void getPointOfServiceCountsOnlyCountry()
	{
		//Mockito.lenient().when
		final List<StoreCountInfo> pointOfServiceCounts = serviceUnderTest.getPointOfServiceCounts(baseStore);
		//then
		checkCountryCounts(pointOfServiceCounts);
	}

	@Test
	public void getPointOfServiceCountsCountryWithRegions()
	{
		//given
		final Map<RegionModel, Integer> germanRegionCountMap = new HashMap<>();
		final Map<RegionModel, Integer> japanRegionCountMap = new HashMap<>();
		final Map<RegionModel, Integer> syrianRegionCountMap = new HashMap<>();
		Mockito.lenient().when(germanRegion1.getName()).thenReturn(GERMANY + REGION);
		Mockito.lenient().when(germanRegion2.getName()).thenReturn(GERMANY + REGION + "2");
		Mockito.lenient().when(japanRegion.getName()).thenReturn(JAPAN + REGION);

		Mockito.lenient().when(japan.getRegions()).thenReturn(Collections.singletonList(japanRegion));
		Mockito.lenient().when(germany.getRegions()).thenReturn(Arrays.asList(germanRegion1, germanRegion2));

		germanRegionCountMap.put(germanRegion1, GERMANREGION1COUNT);
		germanRegionCountMap.put(germanRegion2, GERMANREGION2COUNT);
		japanRegionCountMap.put(japanRegion, JAPAN_STORE_COUNT);

		Mockito.lenient().when(pointOfServiceDao.getPointOfServiceRegionCountForACountryAndStore(germany,baseStore)).thenReturn(germanRegionCountMap);
		Mockito.lenient().when(pointOfServiceDao.getPointOfServiceRegionCountForACountryAndStore(japan,baseStore)).thenReturn(japanRegionCountMap);
		Mockito.lenient().when(pointOfServiceDao.getPointOfServiceRegionCountForACountryAndStore(syria,baseStore)).thenReturn(syrianRegionCountMap);
		//Mockito.lenient().when
		final List<StoreCountInfo> pointOfServiceCounts = serviceUnderTest.getPointOfServiceCounts(baseStore);
		//then
		checkCountryCounts(pointOfServiceCounts);
		//check syria
		final StoreCountInfo syriaStoreCountInfo = getStoreCountInfo(pointOfServiceCounts, SYRIA);
		assertTrue(syriaStoreCountInfo.getStoreCountInfoList().isEmpty());
		//check germany
		final StoreCountInfo germanyStoreCountInfo = getStoreCountInfo(pointOfServiceCounts, GERMANY);
		assertEquals(2, germanyStoreCountInfo.getStoreCountInfoList().size());
		final StoreCountInfo germanRegion1StoreCountInfo = getRegionStoreCountInfo(germanyStoreCountInfo, GERMANY + REGION);
		assertEquals(germanRegion1StoreCountInfo.getCount(), Integer.valueOf(GERMANREGION1COUNT));
		final StoreCountInfo germanRegion2StoreCountInfo = getRegionStoreCountInfo(germanyStoreCountInfo, GERMANY + REGION + 2);
		assertEquals(germanRegion2StoreCountInfo.getCount(), Integer.valueOf(GERMANREGION2COUNT));
		//check japan
		final StoreCountInfo japanStoreCountInfo = getStoreCountInfo(pointOfServiceCounts, JAPAN);
		assertEquals(1, japanStoreCountInfo.getStoreCountInfoList().size());
		final StoreCountInfo japanRegionStoreCountInfo = getRegionStoreCountInfo(japanStoreCountInfo, JAPAN + REGION);
		assertEquals(japanRegionStoreCountInfo.getCount(), Integer.valueOf(JAPAN_STORE_COUNT));
	}

	@Test
	public void populateRegionStoreCountInfo()
	{
		//given
		final Map<RegionModel, Integer> germanRegionCountMap = new HashMap<>();
		germanRegionCountMap.put(germanRegion1, GERMANREGION1COUNT);
		germanRegionCountMap.put(germanRegion2, GERMANREGION2COUNT);
		Mockito.lenient().when(germany.getRegions()).thenReturn(Arrays.asList(germanRegion1, germanRegion2));
		Mockito.lenient().when(pointOfServiceDao.getPointOfServiceRegionCountForACountryAndStore(germany,baseStore)).thenReturn(germanRegionCountMap);
		//Mockito.lenient().when
		final List<StoreCountInfo> pointOfServiceCounts = serviceUnderTest.populateRegionStoreCountInfo(germany,baseStore);
		//then
		assertEquals(2, pointOfServiceCounts.size());
		assertEquals(StoreCountType.REGION, pointOfServiceCounts.stream().findAny().get().getType());
	}

	/**
	 * given a {@link StoreCountInfo} and a region name
	 * will return the nested {@link StoreCountInfo} corresponding to the given region name
	 * will return null if couldn't find any
	 */
	protected StoreCountInfo getRegionStoreCountInfo(final StoreCountInfo storeCountInfo, final String regionName)
	{
		return storeCountInfo.getStoreCountInfoList().stream().filter(sci -> sci.getName().equalsIgnoreCase(regionName)).findFirst()
				.orElse(null);
	}

	/**
	 * given a {@link StoreCountInfo} and a region name
	 * will return the nested {@link StoreCountInfo} corresponding to the given region name
	 * will return null if couldn't find any
	 */
	protected StoreCountInfo getStoreCountInfo(final List<StoreCountInfo> list, final String country)
	{
		return list.stream().filter(storeCountInfo -> storeCountInfo.getName().equalsIgnoreCase(country)).findAny().orElse(null);
	}

	/*
	 * verify the given pointOfServiceCounts list
	 */
	protected void checkCountryCounts(final List<StoreCountInfo> pointOfServiceCounts)
	{
		assertEquals(3, pointOfServiceCounts.size());
		assertEquals(1,
				pointOfServiceCounts.stream().filter(storeCountInfo -> storeCountInfo.getName().equalsIgnoreCase(SYRIA)).count());
		assertEquals(1,
				pointOfServiceCounts.stream().filter(storeCountInfo -> storeCountInfo.getName().equalsIgnoreCase(GERMANY)).count());
		assertEquals(1,
				pointOfServiceCounts.stream().filter(storeCountInfo -> storeCountInfo.getName().equalsIgnoreCase(JAPAN)).count());

		final StoreCountInfo syriaStoreCountInfo = getStoreCountInfo(pointOfServiceCounts, SYRIA);
		assertEquals(syriaStoreCountInfo.getCount(), Integer.valueOf(SYRIA_STORE_COUNT));
		assertEquals(StoreCountType.COUNTRY, syriaStoreCountInfo.getType());

		final StoreCountInfo germanyStoreCountInfo = getStoreCountInfo(pointOfServiceCounts, GERMANY);
		assertEquals(germanyStoreCountInfo.getCount(), Integer.valueOf(GERMANY_STORE_COUNT));
		assertEquals(StoreCountType.COUNTRY, germanyStoreCountInfo.getType());

		final StoreCountInfo japanStoreCountInfo = getStoreCountInfo(pointOfServiceCounts, JAPAN);
		assertEquals(japanStoreCountInfo.getCount(), Integer.valueOf(JAPAN_STORE_COUNT));
		assertEquals(StoreCountType.COUNTRY, japanStoreCountInfo.getType());
	}
}
