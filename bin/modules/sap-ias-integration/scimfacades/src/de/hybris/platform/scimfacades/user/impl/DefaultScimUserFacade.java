/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.scimfacades.user.impl;

import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.scimfacades.ScimUser;
import de.hybris.platform.scimfacades.user.ScimUserFacade;
import de.hybris.platform.scimservices.exceptions.ScimException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of ScimUserFacade
 */
public class DefaultScimUserFacade implements ScimUserFacade
{

	private static final Logger LOG = Logger.getLogger(DefaultScimUserFacade.class);

	private ModelService modelService;

	private FlexibleSearchService flexibleSearchService;

	private Converter<ScimUser, UserModel> scimUserReverseConverter;

	private Converter<UserModel, ScimUser> scimUserConverter;


	private GenericDao<UserModel> scimUserGenericDao;

	private Converter<UserModel, ScimUser> enhancedScimUserConverter;

	@Override
	public List<ScimUser> getUsers()
	{
		LOG.info("ScimUsersFacade getUsers entry");
		try
		{
			final List<UserModel> userModels = scimUserGenericDao.find();
			final List<ScimUser> scimUsers = new ArrayList<>();
			for (final UserModel userModel : userModels)
			{
				if (userModel instanceof EmployeeModel)
				{
					scimUsers.add(this.getScimUserConverter().convert(userModel));
				}
			}
			return CollectionUtils.isNotEmpty(scimUsers) ? scimUsers : null;
		}
		catch (final ModelNotFoundException e)
		{
			LOG.error("No user model found", e);
		}
		return Collections.emptyList();
	}

	@Override
	public ScimUser createUser(final ScimUser scimUser)
	{
		// Primary email is treated as UID
		// Currently only userType 'employee' is supported, when new user types are supported all the logic can be moved to different service, each service handles
		// creation/updation of that type of user. And a Map<String,service> is injected in this class.
		if ("employee".equals(scimUser.getUserType()))
		{
			final EmployeeModel employee = modelService.create(EmployeeModel.class);
			scimUserReverseConverter.convert(scimUser, employee);
			modelService.save(employee);
			LOG.info("Employee with uid=" + employee.getUid() + " successfully created.");
			return getUser(scimUser.getId());
		}
		else
		{
			throw new ScimException("User with userType=" + scimUser.getUserType()
					+ " currently not supported. User creation aborted for userId=" + scimUser.getId());
		}
	}

	@Override
	public ScimUser updateUser(final String userId, final ScimUser scimUser)
	{
		final UserModel user = getUserForScimUserId(userId);
		if (user != null)
		{
			scimUserReverseConverter.convert(scimUser, user);
			persistUpdatedUser(user);	
			LOG.info("Employee with uid=" + user.getUid() + " successfully updated.");
			return getUser(userId);
		}
		else
		{
			throw new ScimException("Employee with scimUserId=" + scimUser.getId() + " doesn't exist, update failed.");
		}
	}

	protected void persistUpdatedUser(final UserModel user)	
	{	
		getModelService().save(user);	
	}	

	@Override
	public ScimUser getUser(final String userId)
	{
		final UserModel user = getUserForScimUserId(userId);

		if (user == null)
		{
			throw new ScimException("Error while fetching the resource=" + userId);
		}

		if (user instanceof EmployeeModel)
		{
			return scimUserConverter.convert(user);
		}

		return null;
	}

	@Override
	public List<ScimUser> getUsers(final String userId)
	{
		throw new ScimException("This API is not supported.");
	}

	@Override
	public boolean deleteUser(final String userId)
	{
		final UserModel user = getUserForScimUserId(userId);

		if (user != null)
		{
			// User is not deleted right away, rather his account is closed and after retention period he will be deleted	
			markUserAsDeactivated(user);
			modelService.save(user);
			LOG.info("User with uid=" + user.getUid() + " and scimUserId=" + user.getScimUserId() + " deactivated.");
			return true;
		}
		else
		{
			throw new ScimException("Error while deleting the resource=" + userId);
		}
	}

	protected void markUserAsDeactivated(final UserModel user)	
	{	
		user.setDeactivationDate(new Date());	
		user.setLoginDisabled(true);	
	}

	@Override
	public UserModel getUserForScimUserId(final String scimUserId)
	{
		try
		{
			final UserModel exampleUserModel = new UserModel();
			exampleUserModel.setScimUserId(scimUserId);
			final List<UserModel> userModels = flexibleSearchService.getModelsByExample(exampleUserModel);

			return CollectionUtils.isNotEmpty(userModels) ? userModels.get(0) : null;
		}
		catch (final ModelNotFoundException e)
		{
			LOG.error("No user model found with scimUserId=" + scimUserId, e);
		}
		return null;
	}

	public ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	public FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	@Required
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

	public Converter<ScimUser, UserModel> getScimUserReverseConverter()
	{
		return scimUserReverseConverter;
	}

	@Required
	public void setScimUserReverseConverter(final Converter<ScimUser, UserModel> scimUserReverseConverter)
	{
		this.scimUserReverseConverter = scimUserReverseConverter;
	}

	public Converter<UserModel, ScimUser> getScimUserConverter()
	{
		return scimUserConverter;
	}

	@Required
	public void setScimUserConverter(final Converter<UserModel, ScimUser> scimUserConverter)
	{
		this.scimUserConverter = scimUserConverter;
	}

	public GenericDao<UserModel> getScimUserGenericDao()
	{
		return scimUserGenericDao;
	}

	public void setScimUserGenericDao(final GenericDao<UserModel> scimUserGenericDao)
	{
		this.scimUserGenericDao = scimUserGenericDao;
	}

	public Converter<UserModel, ScimUser> getEnhancedScimUserConverter()
	{
		return enhancedScimUserConverter;
	}
	
	public void setEnhancedScimUserConverter(final Converter<UserModel, ScimUser> enhancedScimUserConverter)
	{
		this.enhancedScimUserConverter = enhancedScimUserConverter;
	}

}
