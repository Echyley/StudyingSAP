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
package de.hybris.platform.storelocator.location;

import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.storelocator.AbstractGeocodingTest;
import de.hybris.platform.storelocator.GPS;
import de.hybris.platform.storelocator.GeoWebServiceWrapper;
import de.hybris.platform.storelocator.PointOfServiceDao;
import de.hybris.platform.storelocator.data.AddressData;
import de.hybris.platform.storelocator.exception.GeoServiceWrapperException;
import de.hybris.platform.storelocator.exception.LocationServiceException;
import de.hybris.platform.storelocator.exception.MapServiceException;
import de.hybris.platform.storelocator.impl.DefaultGPS;
import de.hybris.platform.storelocator.location.impl.DefaultLocationMapService;
import de.hybris.platform.storelocator.location.impl.DefaultLocationService;
import de.hybris.platform.storelocator.location.impl.DistanceUnawareLocation;
import de.hybris.platform.storelocator.map.Map;
import de.hybris.platform.storelocator.map.MapService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.storelocator.pos.impl.DefaultPointOfServiceService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.ArgumentMatchers;
import org.junit.After;
import org.mockito.MockitoSession;
import org.mockito.quality.Strictness;


public class LocationServiceTest extends AbstractGeocodingTest
{
	private static final double RADIUS_MAX = 100D;
	private static final double RADIUS_STEP = 20D;

	@Mock
	private GeoWebServiceWrapper geoServiceWrapper;

	@Mock
	private LocationService mockedLocationService;

	@Mock
	private PointOfServiceDao pointOfServiceDao;

	@Mock
	private MapService mapService;

	@Spy
	private final DefaultLocationService locationService = new DefaultLocationService();

	private final DefaultPointOfServiceService pointOfServiceService = new DefaultPointOfServiceService();

	private final DefaultLocationMapService locationMapService = new DefaultLocationMapService();

	private BaseStoreModel baseStore;
	private GPS gps;

	private List<Location> locations;
	private List<Location> locationsNearby;

	MockitoSession mockito;
	@Before
	public void setUp() throws Exception
	{

		mockito = Mockito.mockitoSession()
				.initMocks(this)
				.strictness(Strictness.STRICT_STUBS)
				.startMocking();
		locationMapService.setLocationService(mockedLocationService);
		locationMapService.setGeoServiceWrapper(geoServiceWrapper);
		locationMapService.setMapService(mapService);
		locationMapService.setRadiusMax(RADIUS_MAX);
		locationMapService.setRadiusStep(RADIUS_STEP);

		locationService.setMapService(mapService);
		locationService.setPointOfServiceDao(pointOfServiceDao);
		locationService.setModelService(getModelService());
		locationService.setCommonI18NService(getCommonI18NService());
		locationService.setLocationMapService(locationMapService);

		pointOfServiceService.setPointOfServiceDao(pointOfServiceDao);

		baseStore = new BaseStoreModel();
		baseStore.setUid("littleShop");

		gps = new DefaultGPS();
		gps = gps.create("20\u00B0S", "150\u00B0W");

		final PointOfServiceModel pos = new PointOfServiceModel();
		pos.setName("myPos");
		pos.setBaseStore(baseStore);

		final Location location = new DistanceUnawareLocation(pos);
		locations = new ArrayList<Location>();
		locations.add(location);

		locationsNearby = new ArrayList<Location>();
		locationsNearby.add(location);
		for (int i = 0; i < 9; i++)
		{
			locationsNearby.add(new DistanceUnawareLocation(pos));
		}
	}
	@After
	public void tearDown()
	{
		mockito.finishMocking();
	}
	@Test
	public void testGetLocationsNearbyByPostcode() throws GeoServiceWrapperException, LocationServiceException, MapServiceException
	{
		final Map map = Mockito.mock(Map.class);

		Mockito.lenient().when(geoServiceWrapper.geocodeAddress((AddressData) Mockito.any())).thenReturn(gps);
		Mockito.lenient().when(mockedLocationService.getSortedLocationsNearby(ArgumentMatchers.argThat(gpsMatcher()), Mockito.anyDouble(),
				ArgumentMatchers.argThat(baseStoreMatcher()))).thenReturn(locations);
		Mockito.lenient().when(mapService.getMap(gps, RADIUS_MAX, locations, "")).thenReturn(map);
		Mockito.lenient().when(map.getPointsOfInterest()).thenReturn(locations);

		final List<Location> listLocation = locationService.getLocationsForPostcode("44-100", "PL", 10, baseStore);
		Assert.assertEquals(locations, listLocation);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetLocationsNearbyByPostcodeInputData() throws GeoServiceWrapperException, LocationServiceException
	{
		Mockito.lenient().when(geoServiceWrapper.geocodeAddress((AddressData) Mockito.any())).thenReturn(gps);
		Mockito.lenient().when(mockedLocationService.getSortedLocationsNearby(ArgumentMatchers.argThat(gpsMatcher()), Mockito.anyDouble(),
				ArgumentMatchers.argThat(baseStoreMatcher()))).thenReturn(locations);

		locationService.getLocationsForPostcode("", "", 10, baseStore);

	}

	@Test
	public void testGetLocationsNearbyByTown() throws MapServiceException, GeoServiceWrapperException, LocationServiceException
	{
		final Map map = Mockito.mock(Map.class);

		Mockito.lenient().when(geoServiceWrapper.geocodeAddress((AddressData) Mockito.any())).thenReturn(gps);
		Mockito.lenient().when(mockedLocationService.getSortedLocationsNearby(ArgumentMatchers.argThat(gpsMatcher()), Mockito.anyDouble(),
				ArgumentMatchers.argThat(baseStoreMatcher()))).thenReturn(locations);
		Mockito.lenient().when(mapService.getMap(gps, RADIUS_MAX, locations, "")).thenReturn(map);
		Mockito.lenient().when(map.getPointsOfInterest()).thenReturn(locations);

		final List<Location> listLocation = locationService.getLocationsForTown("Gliwice", 10, baseStore);
		Assert.assertEquals(locations, listLocation);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetLocationsNearbyByTownInputData() throws GeoServiceWrapperException, LocationServiceException
	{
		Mockito.lenient().when(geoServiceWrapper.geocodeAddress((AddressData) Mockito.any())).thenReturn(gps);
		Mockito.lenient().when(mockedLocationService.getSortedLocationsNearby(ArgumentMatchers.argThat(gpsMatcher()), Mockito.anyDouble(),
				ArgumentMatchers.argThat(baseStoreMatcher()))).thenReturn(locations);

		locationService.getLocationsForTown("", 10, baseStore);
	}

	//testing situation, when we can return all found shops
	@Test
	public void testGetLocationsNearby() throws MapServiceException, GeoServiceWrapperException, LocationServiceException
	{
		final Map map = Mockito.mock(Map.class);

		Mockito.lenient().when(geoServiceWrapper.geocodeAddress((Location) Mockito.any())).thenReturn(gps);
		Mockito.lenient().when(mockedLocationService.getSortedLocationsNearby(ArgumentMatchers.argThat(gpsMatcher()), Mockito.anyDouble(),
				ArgumentMatchers.argThat(baseStoreMatcher()))).thenReturn(locations);
		Mockito.lenient().when(mapService.getMap(gps, RADIUS_MAX, locations, "")).thenReturn(map);
		Mockito.lenient().when(map.getPointsOfInterest()).thenReturn(locations);

		final List<Location> listLocation = locationService.getLocationsForPoint(gps, 10, baseStore);
		Assert.assertEquals(locations, listLocation);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetLocationsNearbyInputData() throws LocationServiceException
	{
		locationService.getLocationsForPoint(null, 10, baseStore);
		Assert.fail("The IllegalArgumentException should be thrown.");
	}

	//testing situation, when there were more shops found than user have configured
	@Test
	public void testGetLocationsNearbySubList() throws MapServiceException, GeoServiceWrapperException, LocationServiceException
	{
		final Map map = Mockito.mock(Map.class);

		Mockito.lenient().when(geoServiceWrapper.geocodeAddress((Location) Mockito.any())).thenReturn(gps);
		Mockito.lenient().when(mockedLocationService.getSortedLocationsNearby(ArgumentMatchers.argThat(gpsMatcher()), Mockito.anyDouble(),
				ArgumentMatchers.argThat(baseStoreMatcher()))).thenReturn(locationsNearby);
		Mockito.lenient().when(mapService.getMap(gps, RADIUS_STEP, locations, "")).thenReturn(map);
		Mockito.lenient().when(map.getPointsOfInterest()).thenReturn(locations);

		final List<Location> listLocation = locationService.getLocationsForPoint(gps, 1, baseStore);
		Assert.assertEquals(locations, listLocation);
	}

	//testing situation radius
	@Test
	public void testGetLocationsNearbyRadius() throws MapServiceException, GeoServiceWrapperException, LocationServiceException
	{
		final Map map = Mockito.mock(Map.class);

		Mockito.lenient().when(geoServiceWrapper.geocodeAddress((Location) Mockito.any())).thenReturn(gps);
		Mockito.lenient().when(mockedLocationService.getSortedLocationsNearby(ArgumentMatchers.argThat(gpsMatcher()), Mockito.eq(500.0),
				ArgumentMatchers.argThat(baseStoreMatcher()))).thenReturn(locationsNearby.subList(0, 2));
		Mockito.lenient().when(mapService.getMap(gps, RADIUS_MAX, Collections.<Location> emptyList(), "")).thenReturn(map);
		Mockito.lenient().when(map.getPointsOfInterest()).thenReturn(locationsNearby.subList(0, 2));

		final List<Location> listLocation = locationService.getLocationsForPoint(gps, 2, baseStore);
		Assert.assertEquals(2, listLocation.size());
	}

	@Test
	public void testGetLocationsForSearch() throws MapServiceException, GeoServiceWrapperException, LocationServiceException
	{
		final Map map = Mockito.mock(Map.class);
		Mockito.lenient().when(geoServiceWrapper.geocodeAddress((AddressData) Mockito.any())).thenReturn(gps);
		Mockito.lenient().when(mockedLocationService.getSortedLocationsNearby(ArgumentMatchers.argThat(gpsMatcher()), Mockito.anyDouble(),
				ArgumentMatchers.argThat(baseStoreMatcher()))).thenReturn(locations);
		Mockito.lenient().when(mapService.getMap(gps, RADIUS_MAX, locations, "")).thenReturn(map);
		Mockito.lenient().when(map.getPointsOfInterest()).thenReturn(locations);

		final List<Location> listLocation = locationService.getLocationsForSearch("44-100", "PL", 10, baseStore);
		Assert.assertEquals(locations, listLocation);
	}

	private ArgumentMatcher<BaseStoreModel> baseStoreMatcher()
	{
		return new ArgumentMatcher<BaseStoreModel>()
		{
			@Override
			public boolean matches(final BaseStoreModel argument)
			{
				return argument.equals(baseStore);
			}
		};
	}

	private ArgumentMatcher<GPS> gpsMatcher()
	{
		return new ArgumentMatcher<GPS>()
		{
			@Override
			public boolean matches(final GPS argument)
			{
				return  argument.toString().equals(gps.toString());
			}
		};
	}

}
