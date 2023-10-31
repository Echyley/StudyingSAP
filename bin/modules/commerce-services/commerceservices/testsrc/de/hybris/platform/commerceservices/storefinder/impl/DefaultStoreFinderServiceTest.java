/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.storefinder.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.search.dao.PagedGenericDao;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.storefinder.data.StoreFinderSearchPageData;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.storelocator.GPS;
import de.hybris.platform.storelocator.GeoWebServiceWrapper;
import de.hybris.platform.storelocator.PointOfServiceDao;
import de.hybris.platform.storelocator.data.AddressData;
import de.hybris.platform.storelocator.impl.DefaultGPS;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.BDDMockito.given;


/**
 * Test suite for {@link DefaultStoreFinderService}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultStoreFinderServiceTest
{
	private static final int FIRST_PAGE = 0;
	private static final int SECOND_PAGE = 1;
	private static final String COUNTRY_ISO_CODE = "CA";
	private static final String REGION_ISO_CODE = "CA-QC";

	@InjectMocks
	private DefaultStoreFinderService service;

	@Mock
	private BaseStoreModel baseStoreModel;

	@Mock
	private GeoWebServiceWrapper geoWebServiceWrapper;

	@Mock
	private PointOfServiceDao pointOfServiceDao;

	@Mock
	private PagedGenericDao<PointOfServiceModel> pointOfServicePagedGenericDao;

	@Mock
	private GenericDao<PointOfServiceModel> pointOfServiceGenericDao;

	private List<PointOfServiceModel> posModels;

	@Before
	public void prepare()
	{
		service = new DefaultStoreFinderService();
		service.setGeoWebServiceWrapper(geoWebServiceWrapper);
		service.setPointOfServiceDao(pointOfServiceDao);
		service.setPointOfServiceGenericDao(pointOfServiceGenericDao);
		service.setPointOfServicePagedGenericDao(pointOfServicePagedGenericDao);

		posModels = new ArrayList<>();
	}

	@Test
	public void testCalculateBoundsPositive()
	{
		final PointOfServiceModel[] singlePoss = { createPos(20, 20), //
				createPos(20, 30),//
				createPos(30, 20),//
				createPos(30, 30),//
		};

		given(pointOfServiceDao.getAllGeocodedPOS(Mockito.any(GPS.class), Mockito.anyDouble(), Mockito.any(BaseStoreModel.class)))
				.willReturn(Arrays.asList(singlePoss));
		given(geoWebServiceWrapper.geocodeAddress(Mockito.any(AddressData.class))).willReturn(new DefaultGPS(20, 20));
		//
		final PageableData pageData = preparePageMetaData(FIRST_PAGE, 5);

		final StoreFinderSearchPageData<?> result = service.locationSearch(baseStoreModel, "sendai", pageData, 1000.0);
		Assert.assertNotNull(result.getResults());
		Assert.assertEquals(4, result.getResults().size());

		Assert.assertEquals(30, result.getBoundEastLongitude(), 0);
		Assert.assertEquals(30, result.getBoundNorthLatitude(), 0);
		Assert.assertEquals(10, result.getBoundSouthLatitude(), 0);
		Assert.assertEquals(10, result.getBoundWestLongitude(), 0);
	}

	@Test
	public void testCalculateBoundsNegative()
	{
		final PointOfServiceModel[] singlePoss = { createPos(-20, -20), //
				createPos(-20, -30),//
				createPos(-30, -20),//
				createPos(-30, -30),//
		};

		given(pointOfServiceDao.getAllGeocodedPOS(Mockito.any(GPS.class), Mockito.anyDouble(), Mockito.any(BaseStoreModel.class)))
				.willReturn(Arrays.asList(singlePoss));
		given(geoWebServiceWrapper.geocodeAddress(Mockito.any(AddressData.class))).willReturn(new DefaultGPS(-20, -20));
		//
		final PageableData pageData = preparePageMetaData(FIRST_PAGE, 5);

		final StoreFinderSearchPageData<?> result = service.locationSearch(baseStoreModel, "sendai", pageData, 1000.0);
		Assert.assertNotNull(result.getResults());
		Assert.assertEquals(4, result.getResults().size());

		Assert.assertEquals(-10, result.getBoundEastLongitude(), 0);
		Assert.assertEquals(-10, result.getBoundNorthLatitude(), 0);
		Assert.assertEquals(-30, result.getBoundSouthLatitude(), 0);
		Assert.assertEquals(-30, result.getBoundWestLongitude(), 0);
	}

	@Test
	public void testCalculateBoundsNegativeCenterOutSide()
	{
		final PointOfServiceModel[] singlePoss = { createPos(-20, -20), //
				createPos(-20, -30),//
				createPos(-30, -20),//
				createPos(-30, -30),//
		};

		given(pointOfServiceDao.getAllGeocodedPOS(Mockito.any(GPS.class), Mockito.anyDouble(), Mockito.any(BaseStoreModel.class)))
				.willReturn(Arrays.asList(singlePoss));
		given(geoWebServiceWrapper.geocodeAddress(Mockito.any(AddressData.class))).willReturn(new DefaultGPS(-40, -40));
		//
		final PageableData pageData = preparePageMetaData(FIRST_PAGE, 5);

		final StoreFinderSearchPageData<?> result = service.locationSearch(baseStoreModel, "sendai", pageData, 1000.0);
		Assert.assertNotNull(result.getResults());
		Assert.assertEquals(4, result.getResults().size());

		Assert.assertEquals(-20, result.getBoundEastLongitude(), 0);
		Assert.assertEquals(-20, result.getBoundNorthLatitude(), 0);
		Assert.assertEquals(-60, result.getBoundSouthLatitude(), 0);
		Assert.assertEquals(-60, result.getBoundWestLongitude(), 0);
	}


	@Test
	public void testCalculateBoundsMixed()
	{
		final PointOfServiceModel[] singlePoss = { createPos(-20, 20), //
				createPos(20, -30),//
				createPos(30, -20),//
				createPos(-30, 30),//
		};

		given(pointOfServiceDao.getAllGeocodedPOS(Mockito.any(GPS.class), Mockito.anyDouble(), Mockito.any(BaseStoreModel.class)))
				.willReturn(Arrays.asList(singlePoss));
		given(geoWebServiceWrapper.geocodeAddress(Mockito.any(AddressData.class))).willReturn(new DefaultGPS(-20, -20));
		//
		final PageableData pageData = preparePageMetaData(FIRST_PAGE, 5);

		final StoreFinderSearchPageData<?> result = service.locationSearch(baseStoreModel, "sendai", pageData, 1000.0);
		Assert.assertNotNull(result.getResults());
		Assert.assertEquals(4, result.getResults().size());

		Assert.assertEquals(30, result.getBoundEastLongitude(), 0);
		Assert.assertEquals(30, result.getBoundNorthLatitude(), 0);
		Assert.assertEquals(-70, result.getBoundSouthLatitude(), 0);
		Assert.assertEquals(-70, result.getBoundWestLongitude(), 0);
	}


	@Test
	public void testCalculateBoundsMixedCenterOutSideBounds()
	{
		final PointOfServiceModel[] singlePoss = { createPos(-20, 20), //
				createPos(20, -30),//
				createPos(30, -20),//
				createPos(-30, 30),//
		};

		given(pointOfServiceDao.getAllGeocodedPOS(Mockito.any(GPS.class), Mockito.anyDouble(), Mockito.any(BaseStoreModel.class)))
				.willReturn(Arrays.asList(singlePoss));
		given(geoWebServiceWrapper.geocodeAddress(Mockito.any(AddressData.class))).willReturn(new DefaultGPS(50, 50));
		//
		final PageableData pageData = preparePageMetaData(FIRST_PAGE, 5);

		final StoreFinderSearchPageData<?> result = service.locationSearch(baseStoreModel, "sendai", pageData, 1000.0);
		Assert.assertNotNull(result.getResults());
		Assert.assertEquals(4, result.getResults().size());

		Assert.assertEquals(130, result.getBoundEastLongitude(), 0);
		Assert.assertEquals(130, result.getBoundNorthLatitude(), 0);
		Assert.assertEquals(-30, result.getBoundSouthLatitude(), 0);
		Assert.assertEquals(-30, result.getBoundWestLongitude(), 0);
	}


	@Test
	public void testCalculateBoundsSendai()
	{
		final PointOfServiceModel[] singlePoss = createSendaiNearPos();

		given(pointOfServiceDao.getAllGeocodedPOS(Mockito.any(GPS.class), Mockito.anyDouble(), Mockito.any(BaseStoreModel.class)))
				.willReturn(Arrays.asList(singlePoss));
		given(geoWebServiceWrapper.geocodeAddress(Mockito.any(AddressData.class)))
				.willReturn(new DefaultGPS(38.268215, 140.8693558));
		//
		final PageableData pageData = preparePageMetaData(FIRST_PAGE, 5);

		final StoreFinderSearchPageData<?> result = service.locationSearch(baseStoreModel, "sendai", pageData, 1000.0);
		Assert.assertNotNull(result.getResults());
		Assert.assertEquals(5, result.getResults().size());
		verifySendaiCoords(result);
	}

	@Test
	public void testRangedSearchWhenPageSizeGreaterThanPointOfService()
	{
		final PointOfServiceModel[] singlePoss = createSendaiNearPos();

		given(pointOfServiceDao.getAllGeocodedPOS(Mockito.any(GPS.class), Mockito.anyDouble(), Mockito.any(BaseStoreModel.class)))
				.willReturn(Arrays.asList(singlePoss));

		given(geoWebServiceWrapper.geocodeAddress(Mockito.any(AddressData.class)))
				.willReturn(new DefaultGPS(38.268215, 140.8693558));
		//
		final PageableData pageData = preparePageMetaData(FIRST_PAGE, 6); //6 page size

		final StoreFinderSearchPageData<?> result = service.locationSearch(baseStoreModel, "sendai", pageData, 1000.0);
		Assert.assertNotNull(result.getResults());
		Assert.assertEquals(5, result.getResults().size());

		verifySendaiCoords(result);
	}


	@Test
	public void testRangedSearchWhenPageSizeLessThanPointOfService()
	{

		createSendaiNearPos();

		given(pointOfServiceDao.getAllGeocodedPOS(Mockito.any(GPS.class), Mockito.anyDouble(), Mockito.any(BaseStoreModel.class)))
				.willThrow(new ExpectedDaoException());

		given(geoWebServiceWrapper.geocodeAddress(Mockito.any(AddressData.class)))
				.willReturn(new DefaultGPS(38.268215, 140.8693558));
		//
		final PageableData pageData = preparePageMetaData(FIRST_PAGE, 3); //3 page size

		try
		{
			service.locationSearch(baseStoreModel, "sendai", pageData, 1000.0);
			Assert.fail("Should call a pointOfServiceDao ");
		}
		catch (final Exception e)
		{
			Assert.assertTrue(e instanceof ExpectedDaoException);
		}
	}

	@Test
	public void testRangedSearchWhenPageSizeStartExceedsSizeThanPointOfService()
	{

		createSendaiNearPos();

		given(pointOfServiceDao.getAllGeocodedPOS(Mockito.any(GPS.class), Mockito.anyDouble(), Mockito.any(BaseStoreModel.class)))
				.willThrow(new ExpectedDaoException());

		given(geoWebServiceWrapper.geocodeAddress(Mockito.any(AddressData.class)))
				.willReturn(new DefaultGPS(38.268215, 140.8693558));
		//
		final PageableData pageData = preparePageMetaData(SECOND_PAGE, 10); //10 page size

		try
		{
			service.locationSearch(baseStoreModel, "sendai", pageData, 1000.0);
			Assert.fail("Should call a pointOfServiceDao ");
		}
		catch (final Exception e)
		{
			//e.printStackTrace();
			Assert.assertTrue(e instanceof ExpectedDaoException);
		}
	}

	@Test
	public void testGetAllPosForCountrySuccess()
	{
		given(pointOfServiceDao.getPosForCountry(Mockito.anyString(), Mockito.any(BaseStoreModel.class))).willReturn(posModels);

		final List<PointOfServiceModel> result = service.getAllPosForCountry(COUNTRY_ISO_CODE, baseStoreModel);

		Assert.assertEquals(posModels, result);
	}

	@Test
	public void testGetAllPosForRegionSuccess()
	{
		given(pointOfServiceDao.getPosForRegion(Mockito.anyString(), Mockito.anyString(), Mockito.any(BaseStoreModel.class)))
				.willReturn(posModels);

		final List<PointOfServiceModel> result = service.getAllPosForRegion(REGION_ISO_CODE, COUNTRY_ISO_CODE, baseStoreModel);

		Assert.assertEquals(posModels, result);
	}

	private PointOfServiceModel[] createSendaiNearPos()
	{
		final PointOfServiceModel[] singlePoss = { createPos(35.7409, 140.8064), //
				createPos(35.8269, 139.8701),//
				createPos(35.7982, 139.93037),//
				createPos(35.7915, 139.93351),//
				createPos(35.731772, 139.779495) };
		return singlePoss;
	}

	private void verifySendaiCoords(final StoreFinderSearchPageData<?> result)
	{
		Assert.assertEquals(141.9592166, result.getBoundEastLongitude(), 0);
		Assert.assertEquals(40.804657999999996, result.getBoundNorthLatitude(), 0);
		Assert.assertEquals(35.731772, result.getBoundSouthLatitude(), 0);
		Assert.assertEquals(139.779495, result.getBoundWestLongitude(), 0);
	}

	protected PointOfServiceModel createPos(final double lat, final double lon)
	{
		final PointOfServiceModel singlePos = new PointOfServiceModel();
		singlePos.setLatitude(Double.valueOf(lat));
		singlePos.setLongitude(Double.valueOf(lon));
		return singlePos;
	}

	protected PageableData preparePageMetaData(final int start, final int size)
	{
		final PageableData pageData = new PageableData();
		pageData.setCurrentPage(start);
		pageData.setPageSize(size);
		return pageData;
	}

	class ExpectedDaoException extends RuntimeException
	{
		//
	}
}
