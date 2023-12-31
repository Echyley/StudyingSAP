/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyaservices.retention;

import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.gigya.socialize.GSResponse;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.gigya.gigyaservices.api.exception.GigyaApiException;
import de.hybris.platform.gigya.gigyaservices.constants.GigyaservicesConstants;
import de.hybris.platform.gigya.gigyaservices.model.GigyaConfigModel;
import de.hybris.platform.gigya.gigyaservices.service.GigyaService;
import de.hybris.platform.retention.hook.ItemCleanupHook;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;

/**
 * Hook to delete user from gigya if delete flag in GigyaConfig is set to 'true'
 */
public class GigyaUserCleanupHook implements ItemCleanupHook<CustomerModel>
{

	private static final Logger LOG = LoggerFactory.getLogger(GigyaUserCleanupHook.class);

	private GigyaService gigyaService;

	private GenericDao<GigyaConfigModel> gigyaConfigGenericDao;

	@Override
	public void cleanupRelatedObjects(final CustomerModel gigyaUser)
	{
		if (StringUtils.isNotEmpty(gigyaUser.getGyApiKey()))
		{
			final List<GigyaConfigModel> gigyaConfigs = getGigyaConfigGenericDao()
					.find(Collections.singletonMap(GigyaConfigModel.GIGYAAPIKEY, gigyaUser.getGyApiKey()));
			if (CollectionUtils.isNotEmpty(gigyaConfigs) && BooleanUtils.isTrue(gigyaConfigs.get(0).getDeleteUser()))
			{
				try
				{
					final GSResponse res = getGigyaService().callRawGigyaApiWithConfig("accounts.deleteAccount",
							Collections.singletonMap("UID", gigyaUser.getGyUID()), gigyaConfigs.get(0),
							GigyaservicesConstants.MAX_RETRIES, GigyaservicesConstants.TRY_NUM);
					if (res != null && res.getErrorCode() == 0)
					{
						LOG.info("Gigya user with UID: {} and gigya UID: {} deleted.", gigyaUser.getUid(),
								gigyaUser.getGyUID());
					}
					else
					{
						throw new GigyaApiException(
								String.format("Error while deleting gigya user with uid=%s and gigya uid=%s.",
										gigyaUser.getUid(), gigyaUser.getGyUID()));
					}
				}
				catch (final GigyaApiException e)
				{
					throw new GigyaApiException(String.format("Error in deleting user from gigya for UID: %s", gigyaUser.getUid()), e);
				}
			}
		}
	}

	public GigyaService getGigyaService()
	{
		return gigyaService;
	}

	@Required
	public void setGigyaService(final GigyaService gigyaService)
	{
		this.gigyaService = gigyaService;
	}

	public GenericDao<GigyaConfigModel> getGigyaConfigGenericDao()
	{
		return gigyaConfigGenericDao;
	}

	@Required
	public void setGigyaConfigGenericDao(final GenericDao<GigyaConfigModel> gigyaConfigGenericDao)
	{
		this.gigyaConfigGenericDao = gigyaConfigGenericDao;
	}
}
