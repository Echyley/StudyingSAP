/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.mapper;

import de.hybris.platform.assistedservicewebservices.dto.image.UserAvatarWsDTO;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commercewebservicescommons.dto.user.UserWsDTO;
import de.hybris.platform.util.Config;
import de.hybris.platform.webservicescommons.mapping.mappers.AbstractCustomMapper;
import ma.glasnost.orika.MappingContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class CustomerDataMapper extends AbstractCustomMapper<CustomerData, UserWsDTO>
{
	private static final String PROFILE_PICTURE = "profilePicture";
	private static final String USER_AVATAR = "userAvatar";

	private static final String LATEST_CART_ID = "latestCartId";
	private static final String LAST_CART_ID = "lastCartID";

	private static final String ORG_UNIT = "orgUnit";
	private static final String UNIT = "unit";

	private static final Logger LOG = LoggerFactory.getLogger(CustomerDataMapper.class);


	@Override
	public void mapAtoB(final CustomerData customerData, final UserWsDTO userWsDTO, final MappingContext context)
	{
		// other fields are mapped automatically
		if (!Config.getBoolean("toggle.customerList.enabled", false)) {
			userWsDTO.setHasOrder(null);
			userWsDTO.setLastCartId(null);
			userWsDTO.setUserAvatar(null);
			return;
		}
		mapAToBUserAvatar(customerData, userWsDTO, context);
		mapAToBLatestCartId(customerData, userWsDTO, context);
		mapAToBOrgUnit(customerData, userWsDTO, context);
	}

	private void mapAToBUserAvatar(final CustomerData customerData, final UserWsDTO userWsDTO, final MappingContext context)
	{
		context.beginMappingField(PROFILE_PICTURE, getAType(), customerData, USER_AVATAR, getBType(), userWsDTO);
		try
		{
			if (shouldMap(customerData, userWsDTO, context))
			{
				userWsDTO.setUserAvatar(mapperFacade.map(customerData.getProfilePicture(), UserAvatarWsDTO.class, context));
			}
		}
		finally
		{
			context.endMappingField();
		}
	}

	private void mapAToBLatestCartId(final CustomerData customerData, final UserWsDTO userWsDTO, final MappingContext context)
	{
		context.beginMappingField(LATEST_CART_ID, getAType(), customerData, LAST_CART_ID, getBType(), userWsDTO);
		try
		{
			if (shouldMap(customerData, userWsDTO, context))
			{
				userWsDTO.setLastCartId(customerData.getLatestCartId());
			}
		}
		finally
		{
			context.endMappingField();
		}
	}


	private void mapAToBOrgUnit(final CustomerData customerData, final UserWsDTO userWsDTO, final MappingContext context)
	{
		Class<?> b2bUnitWsDTOClass =  null;
		try
		{
			b2bUnitWsDTOClass = Class.forName("de.hybris.platform.b2bwebservicescommons.dto.company.B2BUnitWsDTO");
		}
		catch (ClassNotFoundException e)
		{
			return;
		}

		context.beginMappingField(UNIT, getAType(), customerData, ORG_UNIT, getBType(), userWsDTO);
		try
		{
			final Method getUnit = customerData.getClass().getMethod("getUnit");
			final Object b2bUnitData = getUnit.invoke(customerData);
			if (shouldMap(customerData, userWsDTO, context) && b2bUnitData != null)
			{
				final Method setOrgUnit = userWsDTO.getClass().getMethod("setOrgUnit", b2bUnitWsDTOClass);
				setOrgUnit.invoke(userWsDTO, mapperFacade.map(b2bUnitData, b2bUnitWsDTOClass, context));
			}
		}
		catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e)
		{
			LOG.error("CustomerDataMapper.mapAToBOrgUnit(CustomerData, UserWsDTO).field('unit', 'orgUnit'): Error: " + e.getMessage(), e);
		}
		finally
		{
			context.endMappingField();
		}
	}

}
