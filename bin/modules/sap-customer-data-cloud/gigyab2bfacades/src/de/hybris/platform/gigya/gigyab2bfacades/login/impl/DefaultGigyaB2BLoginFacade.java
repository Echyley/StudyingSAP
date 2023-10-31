/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyab2bfacades.login.impl;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gigya.socialize.GSKeyNotFoundException;
import com.gigya.socialize.GSObject;
import com.gigya.socialize.GSResponse;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.gigya.gigyab2bservices.auth.GigyaAuthService;
import de.hybris.platform.gigya.gigyab2bservices.auth.impl.DefaultGigyaAuthService;
import de.hybris.platform.gigya.gigyab2bservices.auth.impl.GigyaAssetsAuthService;
import de.hybris.platform.gigya.gigyab2bservices.data.GigyaGroupsData;
import de.hybris.platform.gigya.gigyab2bservices.data.GigyaOrganisationData;
import de.hybris.platform.gigya.gigyafacades.login.impl.DefaultGigyaLoginFacade;
import de.hybris.platform.gigya.gigyaservices.api.exception.GigyaApiException;
import de.hybris.platform.gigya.gigyaservices.data.GigyaUserObject;
import de.hybris.platform.gigya.gigyaservices.model.GigyaConfigModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * Gigya B2B Login facade to handle Customer registration in the B2B Context
 */
public class DefaultGigyaB2BLoginFacade extends DefaultGigyaLoginFacade {

	private static final Logger LOG = LoggerFactory.getLogger(DefaultGigyaB2BLoginFacade.class);

	private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;

	private ModelService sapB2BModelService;

	private DefaultGigyaAuthService gigyaAuthService;

	private GigyaAssetsAuthService gigyaAssetsAuthService;

	/**
	 * Creates a new customer in Commerce
	 *
	 * @param gigyaConfig Gigya Config Model
	 * @param uid         User UID
	 * @return UserModel UserModel object of the created user
	 *
	 */
	@Override
	public UserModel createNewCustomer(final GigyaConfigModel gigyaConfig, final String uid)
			throws DuplicateUidException {
		final GigyaUserObject gigyaUserObject = getGigyaLoginService().fetchGigyaInfo(gigyaConfig, uid);

		if (StringUtils.isEmpty(gigyaUserObject.getEmail())) {
			throw new GigyaApiException("Gigya User does not have an email address");
		}

		if (gigyaUserObject.getGroups() != null
				&& CollectionUtils.isNotEmpty(gigyaUserObject.getGroups().getOrganizations())) {
			return createB2BCustomer(gigyaConfig, uid, gigyaUserObject);
		} else {
			return createCustomer(gigyaConfig, uid, gigyaUserObject);
		}
	}

	/**
	 * Creates a B2B Customer in Commerce
	 *
	 * @param gigyaConfig     Gigya Config Model for the base site
	 * @param uid             User UID
	 * @param gigyaUserObject Gigya User Object
	 * @return B2BCustomerModel B2BCustomerModel object
	 */
	private B2BCustomerModel createB2BCustomer(final GigyaConfigModel gigyaConfig, final String uid,
			final GigyaUserObject gigyaUserObject) {
		try {
			// Update B2B Customer if possible as it can be replicated from S/4
			final UserModel user = getUserService().getUserForUID(gigyaUserObject.getEmail());
			if (user instanceof B2BCustomerModel) {
				final B2BCustomerModel b2bCustomer = (B2BCustomerModel) user;
				b2bCustomer.setGyUID(gigyaUserObject.getUID());
				b2bCustomer.setGyApiKey(gigyaConfig.getGigyaApiKey());
				b2bCustomer.setName(getCustomerNameStrategy().getName(gigyaUserObject.getFirstName(),
						gigyaUserObject.getLastName()));
				if(StringUtils.isEmpty(b2bCustomer.getCustomerID())) {
					b2bCustomer.setCustomerID(UUID.randomUUID().toString());
				}				
				assignB2BUnits(gigyaUserObject.getGroups(), b2bCustomer, gigyaConfig);
				getGigyaAuthServiceForGigyaConfig(gigyaConfig).assignAuthorisationsToCustomer(b2bCustomer);
				getModelService().save(b2bCustomer);
				getGigyaConsentFacade().synchronizeConsents(getPreferencesObject(gigyaUserObject), b2bCustomer);
				scheduleDataSyncFromCDCToCommerce(b2bCustomer);

				return b2bCustomer;
			}
		} catch (final UnknownIdentifierException e) {
			LOG.debug("User with email already exists.");
		}

		final B2BCustomerModel gigyaUser = getModelService().create(B2BCustomerModel.class);
		gigyaUser.setGyIsOriginGigya(true);
		gigyaUser.setName(
				getCustomerNameStrategy().getName(gigyaUserObject.getFirstName(), gigyaUserObject.getLastName()));
		gigyaUser.setUid(gigyaUserObject.getEmail());
		gigyaUser.setOriginalUid(gigyaUserObject.getEmail());
		gigyaUser.setGyUID(uid);
		gigyaUser.setCustomerID(UUID.randomUUID().toString());
		gigyaUser.setGyApiKey(gigyaConfig.getGigyaApiKey());
		gigyaUser.setSessionLanguage(getCommonI18NService().getCurrentLanguage());
		gigyaUser.setSessionCurrency(getCommonI18NService().getCurrentCurrency());
		gigyaUser.setEmail(gigyaUserObject.getEmail());

		assignB2BUnits(gigyaUserObject.getGroups(), gigyaUser, gigyaConfig);
		getGigyaAuthServiceForGigyaConfig(gigyaConfig).assignAuthorisationsToCustomer(gigyaUser);
		getSapB2BModelService().save(gigyaUser);

		getGigyaConsentFacade().synchronizeConsents(getPreferencesObject(gigyaUserObject), gigyaUser);
		scheduleDataSyncFromCDCToCommerce(gigyaUser);
		return gigyaUser;
	}

	/**
	 * Update the existing B2B User
	 *
	 * @param gigyaConfig Gigya Config Model for the base site
	 * @param user        User Model
	 *
	 */
	@Override
	public void updateUser(final GigyaConfigModel gigyaConfig, final UserModel user) throws GSKeyNotFoundException {
		// Update mandatory info i.e. UID, name and then update based on mappings
		final ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

		if (user instanceof CustomerModel) {
			final CustomerModel gigyaUser = (CustomerModel) user;
			if(StringUtils.isEmpty(gigyaUser.getCustomerID())) {
				gigyaUser.setCustomerID(UUID.randomUUID().toString());
			}
			final GSResponse accountInfo = getAccountInfo(gigyaConfig, mapper, gigyaUser);
			updateBasicInformation(gigyaUser, accountInfo, gigyaConfig);
			updateB2BInfo(gigyaConfig, gigyaUser, accountInfo);

			getModelService().save(gigyaUser);

			getGigyaConsentFacade().synchronizeConsents(getPreferenceData(accountInfo), gigyaUser);
			scheduleDataSyncFromCDCToCommerce(gigyaUser);
		}
	}

	/**
	 * Update the B2B Information for the customer
	 *
	 * @param gigyaConfig Gigya Config Model for the base site
	 * @param gigyaUser   The user to be updated
	 * @param accountInfo Gigya Response Object containing b2b groups information
	 */
	private void updateB2BInfo(final GigyaConfigModel gigyaConfig, final CustomerModel gigyaUser,
			final GSResponse accountInfo) {
		if (gigyaUser instanceof B2BCustomerModel) {
			final GigyaGroupsData groupsData = getGroupsData(accountInfo);
			if (groupsData != null && CollectionUtils.isNotEmpty(groupsData.getOrganizations())) {
				assignB2BUnits(groupsData, (B2BCustomerModel) gigyaUser, gigyaConfig);
			}
			
			getGigyaAuthServiceForGigyaConfig(gigyaConfig).removeAuthorisationsOfCustomer(gigyaUser);
			getGigyaAuthServiceForGigyaConfig(gigyaConfig).assignAuthorisationsToCustomer(gigyaUser);
		}
	}
	
	/**
	 * Invokes the appropriate GigyaAuthService implementation
	 * @param gigyaConfig
	 * @return
	 */
	protected GigyaAuthService getGigyaAuthServiceForGigyaConfig(final GigyaConfigModel gigyaConfig) {
		// If the legacy Authorization URL is configured, use it, otherwise
		// fetch roles using getAssets API.
		// In case the UserKey or UserSecret is not provided, fall back using
		// the Authorization URL.
		if(isLegacyAuthorizationConfigPresent(gigyaConfig) || StringUtils.isEmpty(gigyaConfig.getGigyaUserKey()) || StringUtils.isEmpty(gigyaConfig.getGigyaUserSecret())) {
			return gigyaAuthService;
		} else {
			return gigyaAssetsAuthService;
		}
	}

	/**
	 * Get the B2B Groups information from the Gigya Response object
	 *
	 * @param gsResponse Response of the Gigya Group information call
	 * @return GigyaGroupsData Groups information for the B2B User
	 */
	protected GigyaGroupsData getGroupsData(final GSResponse gsResponse) {
		final ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
		try {
			final GSObject gsObject = gsResponse.hasData() ? (GSObject) gsResponse.getData().get("groups") : null;
			return gsObject != null ? mapper.readValue(gsObject.toJsonString(), GigyaGroupsData.class) : null;
		} catch (final GSKeyNotFoundException e) {
			LOG.error("Error fetching groups data from SAP CDC", e);
		} catch (final JsonParseException e) {
			LOG.error("Error parsing groups data from SAP CDC", e);
		} catch (final JsonMappingException e) {
			LOG.error("Error mapping groups data from SAP CDC", e);
		} catch (final IOException e) {
			LOG.error("IOException while parsing groups data", e);
		}
		return null;
	}

	/**
	 * Assign B2B Units for the B2B Customer
	 *
	 * @param groupsData  B2B Group information of the user
	 * @param b2bCustomer B2B User to be updated
	 * @param gigyaConfig
	 *
	 */
	private void assignB2BUnits(final GigyaGroupsData groupsData, final B2BCustomerModel b2bCustomer,
			final GigyaConfigModel gigyaConfig) {
		if (groupsData != null && CollectionUtils.isNotEmpty(groupsData.getOrganizations())) {
			final Set<PrincipalGroupModel> organizations = new HashSet<>();
			groupsData.getOrganizations().forEach(item -> {

				if (gigyaConfig.isCreateOrganizationOnLogin()) {

					final B2BUnitModel b2bUnit = createOrFetchB2BUnit(gigyaConfig, item);
					b2bUnitService.addMember(b2bUnit, b2bCustomer);
					organizations.add(b2bUnit);
				} else {
					final B2BUnitModel b2bUnit = b2bUnitService.getUnitForUid(item.getBpid());
					setGigyaAttributesInB2BUnit(gigyaConfig, item, b2bUnit);

					b2bUnitService.addMember(b2bUnit, b2bCustomer);
					organizations.add(b2bUnit);
				}
			});
			b2bCustomer.setDefaultB2BUnit((B2BUnitModel) organizations.iterator().next());
		}
	}

	/**
	 * Create or Fetch B2B Unit information for the B2B User
	 *
	 * @param gigyaConfig Gigya Config Model for the base site
	 * @param item        Gigya Organisation information for the user
	 * @return B2BUnitModel B2B Unit information
	 */
	private B2BUnitModel createOrFetchB2BUnit(final GigyaConfigModel gigyaConfig, final GigyaOrganisationData item) {
		B2BUnitModel b2bUnit = b2bUnitService.getUnitForUid(item.getBpid());
		if (b2bUnit == null) {
			b2bUnit = getModelService().create(B2BUnitModel.class);
			b2bUnit.setUid(item.getBpid());
			b2bUnit.setName(item.getOrgName());
			b2bUnit.setGyUID(item.getOrgId());
			b2bUnit.setGyApiKey(gigyaConfig.getGigyaApiKey());
			getModelService().save(b2bUnit);
		}
		return b2bUnit;
	}

	/**
	 * Sets the Gigya UID and the Gigya API Key for the B2B Unit
	 *
	 * @param gigyaConfig Gigya Config Model for the base site
	 * @param item        Gigya Organisation information
	 * @param b2bUnit     B2B Unit in Commerce
	 */
	private void setGigyaAttributesInB2BUnit(final GigyaConfigModel gigyaConfig, final GigyaOrganisationData item,
			final B2BUnitModel b2bUnit) {
		b2bUnit.setGyUID(item.getOrgId());
		b2bUnit.setGyApiKey(gigyaConfig.getGigyaApiKey());
		getModelService().save(b2bUnit);
	}

	/**
	 * Sets the Gigya UID and the Gigya API Key for the B2B Unit
	 *
	 * @param gigyaConfig Gigya Config Model for the base site
	 * @return boolean, if the legacy configurations are present.
	 */
	private boolean isLegacyAuthorizationConfigPresent(final GigyaConfigModel gigyaConfig)
	{
		return !StringUtils.isEmpty(gigyaConfig.getAuthorizationUrl()) &&
		                !StringUtils.isEmpty(gigyaConfig.getAuthRequestKey()) &&
		                !StringUtils.isEmpty(gigyaConfig.getAuthRequestSecret());
	}

	public B2BUnitService<B2BUnitModel, B2BCustomerModel> getB2bUnitService() {
		return b2bUnitService;
	}

	public void setB2bUnitService(final B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService) {
		this.b2bUnitService = b2bUnitService;
	}

	public DefaultGigyaAuthService getGigyaAuthService() {
		return gigyaAuthService;
	}

	public void setGigyaAuthService(DefaultGigyaAuthService gigyaAuthService) {
		this.gigyaAuthService = gigyaAuthService;
	}

	public GigyaAssetsAuthService getGigyaAssetsAuthService() {
		return gigyaAssetsAuthService;
	}

	public void setGigyaAssetsAuthService(GigyaAssetsAuthService gigyaAssetsAuthService) {
		this.gigyaAssetsAuthService = gigyaAssetsAuthService;
	}

	public ModelService getSapB2BModelService() {
		return sapB2BModelService;
	}

	public void setSapB2BModelService(final ModelService sapB2BModelService) {
		this.sapB2BModelService = sapB2BModelService;
	}

}
