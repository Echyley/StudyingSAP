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

import  org.mockito.Mockito;

import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.storelocator.GPS;
import de.hybris.platform.storelocator.GeoWebServiceWrapper;
import de.hybris.platform.storelocator.PointOfServiceDao;
import de.hybris.platform.storelocator.data.AddressData;
import de.hybris.platform.storelocator.exception.GeoLocatorException;
import de.hybris.platform.storelocator.exception.GeoServiceWrapperException;
import de.hybris.platform.storelocator.exception.LocationInstantiationException;
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
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.MockitoJUnitRunner;
import org.junit.runner.RunWith;



@RunWith(MockitoJUnitRunner.class)
public class LocationMapServiceTest
{
	private static final double RADIUS_MAX = 100D;
	private static final double RADIUS_STEP = 20D;

	@Mock
	private GeoWebServiceWrapper geoServiceWrapper;

	@Mock
	private PointOfServiceDao pointOfServiceDao;

	@Mock
	private MapService mapService;

	private final DefaultPointOfServiceService pointOfServiceService = new DefaultPointOfServiceService();

	@Spy
	private final LocationService locationService = new DefaultLocationService();

	private final DefaultLocationMapService locationMapService = new DefaultLocationMapService();

	private BaseStoreModel baseStore;
	private GPS gps;

	private List<Location> locations;

	@Before
	public void setUp() throws GeoLocatorException, LocationInstantiationException
	{

		locationMapService.setGeoServiceWrapper(geoServiceWrapper);
		locationMapService.setMapService(mapService);
		locationMapService.setRadiusMax(RADIUS_MAX);
		locationMapService.setRadiusStep(RADIUS_STEP);
		locationMapService.setLocationService(locationService);

		((DefaultLocationService) locationService).setMapService(mapService);
		((DefaultLocationService) locationService).setLocationMapService(locationMapService);

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

		final ArrayList<Location> locationsNearby = new ArrayList<Location>();
		locationsNearby.add(location);
		for (int i = 0; i < 9; i++)
		{
			locationsNearby.add(new DistanceUnawareLocation(pos));
		}
	}

	@Test
	public void testGetMapOfLocations() throws GeoServiceWrapperException, LocationServiceException, MapServiceException
	{
		final Map map = Mockito.mock(Map.class);
		final MapService localMapService = Mockito.mock(MapService.class);
		locationMapService.setMapService(localMapService);

		Mockito.lenient().when(geoServiceWrapper.geocodeAddress((AddressData) Mockito.any())).thenReturn(gps);
		Mockito.doReturn(locations).when(locationService).getSortedLocationsNearby(ArgumentMatchers.argThat(gpsMatcher()),
				Mockito.anyDouble(), ArgumentMatchers.argThat(baseStoreMatcher()));
		Mockito.lenient().when(localMapService.getMap(gps, 100D, locations, "")).thenReturn(map);

		final Map result = locationMapService.getMapOfLocations("44-100", "PL", 10, baseStore);
		Assert.assertEquals(map, result);
	}

	private ArgumentMatcher<GPS> gpsMatcher()
	{
		return new ArgumentMatcher<GPS>()
		{
			@Override
			public boolean matches(final GPS argument)
			{
				return argument.toString().equals(gps.toString());
			}
		};
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

}
