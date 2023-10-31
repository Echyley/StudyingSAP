/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bocc.v2.controllers;

import de.hybris.platform.b2bocc.security.SecuredAccessConstants;
import de.hybris.platform.b2bocc.strategy.OrgUnitUserRoleManagementStrategy;
import de.hybris.platform.b2bocc.strategy.OrgUnitUsersDisplayStrategy;
import de.hybris.platform.b2bocc.strategy.UserRoleManagementStrategy;
import de.hybris.platform.b2bocc.v2.helper.OrgUnitsHelper;
import de.hybris.platform.b2bcommercefacades.company.B2BUnitFacade;
import de.hybris.platform.b2bcommercefacades.company.data.B2BSelectionData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitNodeData;
import de.hybris.platform.b2bwebservicescommons.dto.company.B2BApprovalProcessListWsDTO;
import de.hybris.platform.b2bwebservicescommons.dto.company.B2BSelectionDataWsDTO;
import de.hybris.platform.b2bwebservicescommons.dto.company.B2BUnitNodeListWsDTO;
import de.hybris.platform.b2bwebservicescommons.dto.company.B2BUnitNodeWsDTO;
import de.hybris.platform.b2bwebservicescommons.dto.company.B2BUnitWsDTO;
import de.hybris.platform.b2bwebservicescommons.dto.company.OrgUnitUserListWsDTO;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercewebservicescommons.dto.user.AddressListWsDTO;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.commercewebservicescommons.dto.user.AddressWsDTO;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.RequestParameterException;
import de.hybris.platform.webservicescommons.errors.exceptions.AlreadyExistsException;
import de.hybris.platform.webservicescommons.errors.exceptions.NotFoundException;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;

import java.util.Map;
import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;


@RestController
@RequestMapping(value = "/{baseSiteId}/users/{userId}")
@ApiVersion("v2")
@Secured({ SecuredAccessConstants.ROLE_B2BADMINGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT })
@Tag(name = "Organizational Unit Management")
public class OrgUnitsController extends BaseController
{
	private static final String OBJECT_NAME_ORG_UNIT = "OrgUnit";

	private static final String UNIT_NOT_FOUND_MESSAGE = "Organizational unit with id [%s] was not found";
	private static final String ROLE_NOT_FOUND_MESSAGE = "Supplied parameter [%s] is not valid";
	private static final String UNIT_ALREADY_EXISTS_MESSAGE = "Organizational unit with uid [%s] already exists";
	private static final String ADDRESS_NOT_FOUND_MESSAGE = "Address with id [%s] is not found";

	@Resource(name = "b2bUnitFacade")
	protected B2BUnitFacade b2bUnitFacade;

	@Resource(name = "wsUserFacade")
	private UserFacade wsUserFacade;

	@Resource(name = "b2BUnitWsDTOValidator")
	protected Validator b2BUnitWsDTOValidator;

	@Resource(name = "addressDTOValidator")
	private Validator addressDTOValidator;

	@Resource(name = "orgUnitsHelper")
	private OrgUnitsHelper orgUnitsHelper;

	@Resource(name = "userRoleManagementStrategyMap")
	protected Map<String, UserRoleManagementStrategy> userRoleManagementStrategyMap;

	@Resource(name = "orgUnitUsersDisplayStrategyMap")
	protected Map<String, OrgUnitUsersDisplayStrategy> orgUnitUsersDisplayStrategyMap;

	@Resource(name = "orgUnitUserRoleManagementStrategyMap")
	protected Map<String, OrgUnitUserRoleManagementStrategy> orgUnitUserRoleManagementStrategyMap;

	@Secured({ SecuredAccessConstants.ROLE_B2BADMINGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT })
	@Operation(operationId = "getOrgUnit", summary = "Get an organizational unit.", description = "Returns a specific organizational unit based on specific id. The response contains detailed organizational unit information.")
	@GetMapping(value = "/orgUnits/{orgUnitId}", produces = MediaType.APPLICATION_JSON)
	@ApiBaseSiteIdAndUserIdParam
	public B2BUnitWsDTO getOrgUnit(
			@Parameter(description = "Organizational Unit identifier.", required = true) @PathVariable final String orgUnitId,
			@ApiFieldsParam @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields)
	{
		final B2BUnitData unitData = getUnitForUid(orgUnitId);
		return getDataMapper().map(unitData, B2BUnitWsDTO.class, fields);
	}

	@Secured({ SecuredAccessConstants.ROLE_B2BADMINGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT })
	@PostMapping(value = "/orgUnits", produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
	@ResponseStatus(value = HttpStatus.CREATED)
	@Operation(operationId = "createOrgUnit", summary = "Create a new organizational unit.", description = "Creates a new organizational unit.")
	@ApiBaseSiteIdAndUserIdParam
	public B2BUnitWsDTO createUnit(
			@Parameter(description = "Organizational Unit object.", required = true) @RequestBody final B2BUnitWsDTO orgUnit,
			@ApiFieldsParam @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields)
	{
		validate(orgUnit, OBJECT_NAME_ORG_UNIT, b2BUnitWsDTOValidator);

		if (b2bUnitFacade.getUnitForUid(orgUnit.getUid()) != null)
		{
			throw new AlreadyExistsException(String.format(UNIT_ALREADY_EXISTS_MESSAGE, orgUnit.getUid()));
		}

		final B2BUnitData unitData = getDataMapper().map(orgUnit, B2BUnitData.class);
		b2bUnitFacade.updateOrCreateBusinessUnit(unitData.getUid(), unitData);

		final B2BUnitData createdUnitData = b2bUnitFacade.getUnitForUid(unitData.getUid());
		return getDataMapper().map(createdUnitData, B2BUnitWsDTO.class, fields);
	}

	@Secured({ SecuredAccessConstants.ROLE_B2BADMINGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT })
	@Operation(operationId = "getAvailableParentUnits", summary = "Get available parent units.", description = "Returns a list of parent units for which the unit with id can be assigned as a child.")
	@GetMapping(value = "/orgUnits/{orgUnitId}/availableParents", produces = MediaType.APPLICATION_JSON)
	@ApiBaseSiteIdAndUserIdParam
	public B2BUnitNodeListWsDTO getAvailableParentUnits(
			@Parameter(description = "Organizational Unit identifier.", required = true) @PathVariable final String orgUnitId,
			@ApiFieldsParam @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields)
	{
		return orgUnitsHelper.getAvailableParentUnits(orgUnitId, fields);
	}

	@Secured({ SecuredAccessConstants.ROLE_B2BADMINGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT })
	@Operation(operationId = "updateOrgUnit", summary = "Update the organizational unit", description = "Updates the organizational unit. Only attributes provided in the request body will be changed.")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PatchMapping(value = "/orgUnits/{orgUnitId}", produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
	@ApiBaseSiteIdAndUserIdParam
	public void updateOrgUnit(
			@Parameter(description = "Organizational Unit identifier.", required = true) @PathVariable final String orgUnitId,
			@Parameter(description = "Organizational Unit object.", required = true) @RequestBody final B2BUnitWsDTO orgUnit)
	{
		// active status changes are handled later because b2bUnitFacade.updateOrCreateBusinessUnit overwrites it to true
		final Boolean isActive = orgUnit.getActive();
		orgUnit.setActive(null);

		final B2BUnitData unitData = getUnitForUid(orgUnitId);
		getDataMapper().map(orgUnit, unitData, false);

		final B2BUnitWsDTO unitToBeValidated = getDataMapper().map(unitData, B2BUnitWsDTO.class);
		validate(unitToBeValidated, OBJECT_NAME_ORG_UNIT, b2BUnitWsDTOValidator);

		b2bUnitFacade.updateOrCreateBusinessUnit(orgUnitId, unitData);

		if (isActive != null)
		{
			if (isActive)
			{
				b2bUnitFacade.enableUnit(unitData.getUid());
			}
			else
			{
				b2bUnitFacade.disableUnit(unitData.getUid());
			}
		}
	}

	@Secured({ SecuredAccessConstants.ROLE_B2BADMINGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT })
	@PostMapping(value = "/orgUnits/{orgUnitId}/addresses", produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
	@ResponseStatus(value = HttpStatus.CREATED)
	@Operation(operationId = "createOrgUnitAddress", summary = "Create a new organizational unit address", description = "Creates a new organizational unit address")
	@ApiBaseSiteIdAndUserIdParam
	public AddressWsDTO createOrgUnitAddress(
			@Parameter(description = "Address object.", required = true) @RequestBody final AddressWsDTO address,
			@Parameter(description = "Organizational unit id.", required = true) @PathVariable final String orgUnitId,
			@ApiFieldsParam @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields)
	{
		validate(address, OBJECT_NAME_ORG_UNIT, addressDTOValidator);

		final AddressData addressData = getDataMapper().map(address, AddressData.class);
		addressData.setBillingAddress(false);
		addressData.setShippingAddress(true);

		b2bUnitFacade.addAddressToUnit(addressData, orgUnitId);
		return getDataMapper().map(addressData, AddressWsDTO.class, fields);
	}

	@Secured({ SecuredAccessConstants.ROLE_B2BADMINGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT })
	@GetMapping(value = "/orgUnits/{orgUnitId}/addresses", produces = MediaType.APPLICATION_JSON)
	@ResponseStatus(value = HttpStatus.OK)
	@Operation(operationId = "getOrgUnitAddresses", summary = "Get organizational unit addresses", description = "Retrieves organizational unit addresses")
	@ApiBaseSiteIdAndUserIdParam
	public AddressListWsDTO getOrgUnitAddresses(
			@Parameter(description = "Organizational unit id.", required = true) @PathVariable final String orgUnitId,
			@ApiFieldsParam @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields)
	{
		final B2BUnitData unitData = getUnitForUid(orgUnitId);
		final B2BUnitWsDTO unitDataWsDTO = getDataMapper().map(unitData, B2BUnitWsDTO.class, fields);
		final List<AddressWsDTO> addressList = unitDataWsDTO.getAddresses();
		final AddressListWsDTO addressDataList = new AddressListWsDTO();
		addressDataList.setAddresses(addressList);
		return getDataMapper().map(addressDataList, AddressListWsDTO.class, fields);
	}

	@Secured({ SecuredAccessConstants.ROLE_B2BADMINGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT })
	@PatchMapping(value = "/orgUnits/{orgUnitId}/addresses/{addressId}", produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(operationId = "updateOrgUnitAddress", summary = "Update the organizational unit address.", description = "Updates the organizational unit address. Only attributes provided in the request body will be changed.")
	@ApiBaseSiteIdAndUserIdParam
	public void updateOrgUnitAddress(@Parameter(description = "Address object.", required = true) @RequestBody final AddressWsDTO address,
			@Parameter(description = "Organizational unit id.", required = true) @PathVariable final String orgUnitId,
			@Parameter(description = "Address id.", required = true) @PathVariable final String addressId)
	{
		final AddressData addressData = getAddressData(addressId, orgUnitId);

		getDataMapper().map(address, addressData, false);
		addressData.setId(addressId);
		addressData.setBillingAddress(false);
		addressData.setShippingAddress(true);

		final AddressWsDTO addressToBeValidated = getDataMapper().map(addressData, AddressWsDTO.class);
		validate(addressToBeValidated, OBJECT_NAME_ORG_UNIT, addressDTOValidator);

		b2bUnitFacade.editAddressOfUnit(addressData, orgUnitId);
	}


	@Secured({ SecuredAccessConstants.ROLE_B2BADMINGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT })
	@Operation(operationId = "removeOrgUnitAddress", summary = "Remove the organizational unit address.", description = "Removes the organizational unit address.")
	@ResponseStatus(HttpStatus.OK)
	@DeleteMapping(value = "/orgUnits/{orgUnitId}/addresses/{addressId}", produces = MediaType.APPLICATION_JSON)
	@ApiBaseSiteIdAndUserIdParam
	public void removeOrgUnitAddress(
			@Parameter(description = "Organizational unit id.", required = true) @PathVariable final String orgUnitId,
			@Parameter(description = "Address id.", required = true) @PathVariable final String addressId)
	{
		b2bUnitFacade.removeAddressFromUnit(orgUnitId, addressId);
	}

	@Secured({ SecuredAccessConstants.ROLE_B2BADMINGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT })
	@GetMapping(value = "/orgUnits/{orgUnitId}/availableUsers/{roleId}", produces = MediaType.APPLICATION_JSON)
	@Operation(operationId = "getOrgUnitUsers", summary = "Get users who belongs to the organization unit.", description = "Returns list of users which belongs to the organizational unit and can be assigned to a specific role. Users who are already assigned to the role are flagged by 'selected' attribute. ")
	@ApiBaseSiteIdAndUserIdParam
	public OrgUnitUserListWsDTO getOrgUnitUsers(
			@Parameter(description = "Organizational unit id.", required = true) @PathVariable final String orgUnitId,
			@Parameter(description = "Filtering parameter which is used to return a specific role. Example roles are: b2bapprovergroup, b2badmingroup, b2bmanagergroup, b2bcustomergroup", required = true) @PathVariable final String roleId,
			@Parameter(description = "The current result page requested.") @RequestParam(defaultValue = DEFAULT_CURRENT_PAGE) final int currentPage,
			@Parameter(description = "The number of results returned per page.") @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) final int pageSize,
			@Parameter(description = "Sorting method applied to the display search results.") @RequestParam(required = false) final String sort,
			@ApiFieldsParam @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields)
	{

		final OrgUnitUsersDisplayStrategy orgUnitUsersDisplayStrategy = getOrgUnitUsersDisplayStrategy(roleId);

		return orgUnitsHelper.convertPagedUsersForUnit(
				orgUnitUsersDisplayStrategy.getPagedUsersForUnit(currentPage, pageSize, sort, orgUnitId), fields);
	}

	@Secured({ SecuredAccessConstants.ROLE_B2BADMINGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT })
	@PostMapping(value = "/orgCustomers/{orgCustomerId}/roles", produces = MediaType.APPLICATION_JSON)
	@Operation(operationId = "doAddRoleToOrgCustomer", summary = "Add a role to a specific organizational customer", description = "Adds a role to a specific organizational customer")
	@ApiBaseSiteIdAndUserIdParam
	@ResponseStatus(value = HttpStatus.CREATED)
	@ResponseBody
	public B2BSelectionDataWsDTO addRoleToOrgCustomer(
			@Parameter(description = "Identifier of the organizational customer which the role will be added.", required = true) @PathVariable final String orgCustomerId,
			@Parameter(description = "The role which is added to the organizational customer. Example roles are: b2badmingroup, "
					+ "b2bmanagergroup, b2bcustomergroup", required = true) @RequestParam final String roleId,
			@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		final UserRoleManagementStrategy userRoleManagementStrategy = getUserRoleManagementStrategy(roleId);
		final B2BSelectionData selectionData = userRoleManagementStrategy.addRoleToUser(wsUserFacade.getUserUID(orgCustomerId));
		return getDataMapper().map(selectionData, B2BSelectionDataWsDTO.class, fields);
	}

	@Secured({ SecuredAccessConstants.ROLE_B2BADMINGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT })
	@DeleteMapping(value = "/orgCustomers/{orgCustomerId}/roles/{roleId}", produces = MediaType.APPLICATION_JSON)
	@Operation(operationId = "removeRoleFromOrgCustomer", summary = "Remove a role from a specific organizational customer", description = "Removes a role from a specific organizational customer")
	@ApiBaseSiteIdAndUserIdParam
	@ResponseBody
	public B2BSelectionDataWsDTO removeRoleFromOrgCustomer(
			@Parameter(description = "Identifier of the organizational customer which the role will be removed.", required = true) @PathVariable final String orgCustomerId,
			@Parameter(description = "The role which is removed from the user. Example roles are: b2badmingroup, b2bmanagergroup, "
					+ "b2bcustomergroup", required = true) @PathVariable final String roleId,
			@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		final UserRoleManagementStrategy userRoleManagementStrategy = getUserRoleManagementStrategy(roleId);
		final B2BSelectionData selectionData = userRoleManagementStrategy
				.removeRoleFromUser(wsUserFacade.getUserUID(orgCustomerId));
		return getDataMapper().map(selectionData, B2BSelectionDataWsDTO.class, fields);
	}

	@Secured({ SecuredAccessConstants.ROLE_B2BADMINGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT })
	@PostMapping(value = "/orgUnits/{orgUnitId}/orgCustomers/{orgCustomerId}/roles", produces = MediaType.APPLICATION_JSON)
	@Operation(operationId = "doAddOrgUnitRoleToOrgCustomer", summary = "Add an organizational unit dependent role to a specific organizational customer", description = "Adds an organizational unit dependent role to a specific organizational customer")
	@ApiBaseSiteIdAndUserIdParam
	@ResponseStatus(value = HttpStatus.CREATED)
	public void addOrgUnitRoleToOrgCustomer(
			@Parameter(description = "Organizational unit id.", required = true) @PathVariable final String orgUnitId,
			@Parameter(description = "Identifier of the organizational customer which the role will be added.", required = true) @PathVariable final String orgCustomerId,
			@Parameter(description = "The role which is added to the user. Example roles are: b2bapprovergroup", required = true) @RequestParam final String roleId)
	{
		final OrgUnitUserRoleManagementStrategy orgUnitUserRoleManagementStrategy = getOrgUnitUserRoleManagementStrategy(roleId);
		orgUnitUserRoleManagementStrategy.addRoleToUser(orgUnitId, wsUserFacade.getUserUID(orgCustomerId));
	}

	@Secured({ SecuredAccessConstants.ROLE_B2BADMINGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT })
	@DeleteMapping(value = "/orgUnits/{orgUnitId}/orgCustomers/{orgCustomerId}/roles/{roleId}", produces = MediaType.APPLICATION_JSON)
	@Operation(operationId = "removeOrgUnitRoleFromOrgCustomer", summary = "Remove an organizational unit dependent role from a specific organizational customer.", description = "Removes an organizational unit dependent role from a specific organizational customer.")
	@ApiBaseSiteIdAndUserIdParam
	public void removeOrgUnitRoleFromOrgCustomer(
			@Parameter(description = "Organizational unit id.", required = true) @PathVariable final String orgUnitId,
			@Parameter(description = "Identifier of the organizational customer which the role will be removed.", required = true) @PathVariable final String orgCustomerId,
			@Parameter(description = "The role which is removed from the user. Example roles are: b2bapprovergroup, b2badmingroup, b2bmanagergroup, b2bcustomergroup", required = true) @PathVariable final String roleId)
	{
		final OrgUnitUserRoleManagementStrategy orgUnitUserRoleManagementStrategy = getOrgUnitUserRoleManagementStrategy(roleId);
		orgUnitUserRoleManagementStrategy.removeRoleFromUser(orgUnitId, wsUserFacade.getUserUID(orgCustomerId));
	}

	@Secured({ SecuredAccessConstants.ROLE_B2BADMINGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT })
	@Operation(operationId = "getOrgUnitsAvailableApprovalProcesses", summary = "Get available approval business processes.", description = "Returns list of available approval business processes.")
	@GetMapping(value = "/orgUnitsAvailableApprovalProcesses", produces = MediaType.APPLICATION_JSON)
	@ApiBaseSiteIdAndUserIdParam
	public B2BApprovalProcessListWsDTO getOrgUnitsAvailableApprovalProcesses(
			@ApiFieldsParam @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields)
	{
		return orgUnitsHelper.getApprovalProcesses();
	}

	@Secured({ SecuredAccessConstants.ROLE_B2BADMINGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT })
	@Operation(operationId = "getOrgUnitsRootNodeTree", summary = "Get the root organizational unit node.", description = "Returns the root organizational unit node. The response contains detailed organizational unit node information and the child nodes associated to it.")
	@ResponseBody
	@GetMapping(value = "/orgUnitsRootNodeTree", produces = MediaType.APPLICATION_JSON)
	@ApiBaseSiteIdAndUserIdParam
	public B2BUnitNodeWsDTO getOrgUnitsRootNodeTree(
			@ApiFieldsParam @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields)
	{
		final B2BUnitNodeData unitNodeData = b2bUnitFacade.getParentUnitNode();
		return getDataMapper().map(unitNodeData, B2BUnitNodeWsDTO.class, fields);
	}

	@Secured({ SecuredAccessConstants.ROLE_B2BADMINGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT })
	@Operation(operationId = "getAvailableOrgUnitNodes", summary = "Get available organizational unit nodes.", description = "Returns list of available organizational unit nodes.")
	@ResponseBody
	@GetMapping(value = "/availableOrgUnitNodes", produces = MediaType.APPLICATION_JSON)
	@ApiBaseSiteIdAndUserIdParam
	public B2BUnitNodeListWsDTO getBranchNodes(
			@ApiFieldsParam @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields)
	{
		return orgUnitsHelper.getAvailableOrgUnitNodes(fields);
	}

	protected UserRoleManagementStrategy getUserRoleManagementStrategy(final String roleId)
	{
		final UserRoleManagementStrategy userRoleManagementStrategy = userRoleManagementStrategyMap.get(roleId);
		if (userRoleManagementStrategy == null)
		{
			throw new RequestParameterException(String.format(ROLE_NOT_FOUND_MESSAGE, sanitize(roleId)));
		}

		return userRoleManagementStrategy;
	}

	protected OrgUnitUserRoleManagementStrategy getOrgUnitUserRoleManagementStrategy(final String roleId)
	{
		final OrgUnitUserRoleManagementStrategy orgUnitUserRoleManagementStrategy = orgUnitUserRoleManagementStrategyMap
				.get(roleId);
		if (orgUnitUserRoleManagementStrategy == null)
		{
			throw new RequestParameterException(String.format(ROLE_NOT_FOUND_MESSAGE, sanitize(roleId)));
		}

		return orgUnitUserRoleManagementStrategy;
	}

	protected OrgUnitUsersDisplayStrategy getOrgUnitUsersDisplayStrategy(final String roleId)
	{
		final OrgUnitUsersDisplayStrategy orgUnitUsersDisplayStrategy = orgUnitUsersDisplayStrategyMap.get(roleId);
		if (orgUnitUsersDisplayStrategy == null)
		{
			throw new RequestParameterException(String.format(ROLE_NOT_FOUND_MESSAGE, sanitize(roleId)));
		}

		return orgUnitUsersDisplayStrategy;
	}

	protected B2BUnitData getUnitForUid(final String orgUnitId)
	{
		final B2BUnitData unitData = b2bUnitFacade.getUnitForUid(orgUnitId);
		if (unitData == null)
		{
			throw new NotFoundException(String.format(UNIT_NOT_FOUND_MESSAGE, sanitize(orgUnitId)));
		}
		return unitData;
	}

	protected AddressData getAddressData(final String addressId, final String orgUnitId)
	{
		final B2BUnitData unit = b2bUnitFacade.getUnitForUid(orgUnitId);

		final AddressData addressData = unit.getAddresses().stream()
				.filter(address -> StringUtils.equals(address.getId(), addressId)).findFirst()
				.orElseThrow(() -> new NotFoundException(String.format(ADDRESS_NOT_FOUND_MESSAGE, addressId)));

		return addressData;
	}
}
