/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.mapper;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.assistedservicewebservices.dto.image.UserAvatarWsDTO;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commercewebservicescommons.dto.user.UserWsDTO;
import de.hybris.platform.util.Config;
import de.hybris.platform.webservicescommons.mapping.FieldSelectionStrategy;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CustomerDataMapperTest
{

	private final String CUSTOMER_LIST_FEATURE_FLAG = "toggle.customerList.enabled";
	private static final String IMAGE_DATA_URL = "medias/eh11.jpeg?context=bWFzdGVyfHJvb3R8NTMyMDB8aW1hZ2UvanBlZ3xhRGRpTDJnek9TODROems0TWpBeU1qTXlPRFl5TG1wd1p3fDY0OWMxNzAxMjE1YjdhNDQ2MDRlMDM2ZmNiMmFlZjBhMWFmZTEwNjczODRhOGQ4Nzk0ZmQ5OTdjZjc2Nzk4NzA";
	private static final String LATEST_CART_ID = "00008008";

	@Mock
	private MappingContext mappingContext;
	@Mock
	private FieldSelectionStrategy fieldSelectionStrategy;
	@Mock
	private MapperFacade mapperFacade;
	@Mock
	private UserWsDTO userWsDTO;
	@InjectMocks
	private CustomerDataMapper customerDataMapper;

	@Test
	public void testCustomerDataMapAtoB()
	{
		try (MockedStatic<Config> config = Mockito.mockStatic(Config.class))
		{
			config.when(() -> Config.getBoolean(CUSTOMER_LIST_FEATURE_FLAG, false)).thenReturn(true);
			Assert.assertTrue(Config.getBoolean(CUSTOMER_LIST_FEATURE_FLAG, false));
			final CustomerData customerData = new CustomerData();
			final UserWsDTO userWsDTO = new UserWsDTO();
			customerData.setLatestCartId(LATEST_CART_ID);
			final UserAvatarWsDTO userAvatarWsDTO = new UserAvatarWsDTO();
			userAvatarWsDTO.setUrl(IMAGE_DATA_URL);

			when(fieldSelectionStrategy.shouldMap(customerData, userWsDTO, mappingContext)).thenReturn(true);
			when(mapperFacade.map(null, UserAvatarWsDTO.class, mappingContext)).thenReturn(userAvatarWsDTO);

			customerDataMapper.mapAtoB(customerData, userWsDTO, mappingContext);
			Assert.assertEquals(IMAGE_DATA_URL, userWsDTO.getUserAvatar().getUrl());
			Assert.assertEquals(LATEST_CART_ID, userWsDTO.getLastCartId());
		}

	}

	@Test
	public void shouldNotMappingWhenCustomerListFeatureFlagIsFalse()
	{
		try (MockedStatic<Config> config = Mockito.mockStatic(Config.class))
		{
			config.when(() -> Config.getBoolean(CUSTOMER_LIST_FEATURE_FLAG, false)).thenReturn(false);
			Assert.assertFalse(Config.getBoolean(CUSTOMER_LIST_FEATURE_FLAG, false));
			final CustomerData customerData = new CustomerData();
			customerDataMapper.mapAtoB(customerData, userWsDTO, mappingContext);
			verify(userWsDTO).setHasOrder(null);
			verify(userWsDTO).setLastCartId(null);
			verify(userWsDTO).setUserAvatar(null);
			verifyNoInteractions(mappingContext);
			verifyNoInteractions(fieldSelectionStrategy);
		}
	}
}
