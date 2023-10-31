/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicefacades.customer360.provider;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.assistedservicefacades.customer360.CustomerOverviewData;
import de.hybris.platform.assistedservicefacades.customer360.populators.CustomerOverviewDataPopulator;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class CustomerOverviewDataProviderTest
{
	@Mock
	private Converter<MediaModel, ImageData> imageConverter;
	@Mock
	private Converter<AddressModel, AddressData> addressConverter;

	@InjectMocks
	private CustomerOverviewDataPopulator customerOverviewDataPopulator;




	@Before
	public void setup()
	{
		customerOverviewDataPopulator = new CustomerOverviewDataPopulator();
	}

	@Test
	public void getModelTest()
	{
		final String userUID = "atest@atest.com";
		final String name = "atest atest";
		final Date creationTime = new Date();

		final CustomerModel customerModel = Mockito.mock(CustomerModel.class);


		Mockito.when(customerModel.getUndecoratedUid()).thenReturn(userUID);
		Mockito.when(customerModel.getName()).thenReturn(name);
		Mockito.when(customerModel.getCreationtime()).thenReturn(creationTime);
		Mockito.when(customerModel.getDefaultShipmentAddress()).thenReturn(null);
		Mockito.when(customerModel.getProfilePicture()).thenReturn(null);


		final CustomerOverviewData customerOverviewData = new CustomerOverviewData();
		customerOverviewDataPopulator.populate(customerModel, customerOverviewData);

		Assert.assertNotNull(customerOverviewData);
		Assert.assertEquals(userUID, customerOverviewData.getEmail());
		Assert.assertEquals(name, customerOverviewData.getFullName());
		Assert.assertEquals(creationTime, customerOverviewData.getSignedUp());
	}

	@Test
	public void undecortedUidNullTest()
	{
		final String userUID = "atest@atest.com";
		final String name = "atest atest";
		final Date creationTime = new Date();

		final CustomerModel customerModel = Mockito.mock(CustomerModel.class);


		Mockito.when(customerModel.getUndecoratedUid()).thenReturn(null);
		Mockito.when(customerModel.getUid()).thenReturn(userUID);
		Mockito.when(customerModel.getName()).thenReturn(name);
		Mockito.when(customerModel.getCreationtime()).thenReturn(creationTime);
		Mockito.when(customerModel.getDefaultShipmentAddress()).thenReturn(null);
		Mockito.when(customerModel.getProfilePicture()).thenReturn(null);


		final CustomerOverviewData customerOverviewData = new CustomerOverviewData();
		customerOverviewDataPopulator.populate(customerModel, customerOverviewData);

		Assert.assertEquals(userUID, customerOverviewData.getEmail());
	}

}
