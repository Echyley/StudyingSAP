/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.controllers;

import de.hybris.platform.assistedservicefacades.customer360.AdditionalInformationFrameworkFacade;
import de.hybris.platform.assistedservicefacades.customer360.Fragment;
import de.hybris.platform.assistedservicewebservices.adapters.C360FragmentDataAdapter;
import de.hybris.platform.assistedservicewebservices.dto.Customer360QueryListWsDTO;
import de.hybris.platform.assistedservicewebservices.dto.Customer360DataWsDTO;
import de.hybris.platform.assistedservicewebservices.dto.Customer360ListWsDTO;
import de.hybris.platform.assistedservicewebservices.dto.Customer360QueryWsDTO;
import de.hybris.platform.assistedservicewebservices.exceptions.AssistedServiceCustomer360Exception;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.RequestParameterException;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@Tag(name = "ASM Customer 360")
public class AssistedServiceCustomer360Controller {
    @Resource(name = "additionalInformationFrameworkFacade")
    private AdditionalInformationFrameworkFacade additionalInformationFrameworkFacade;
    @Resource(name = "customer360DataTypeSectionMap")
    private Map<String, String> dataTypeSectionMap;
    @Resource(name = "customer360DataTypeFragmentMap")
    private Map<String, String> dataTypeFragmentMap;
    @Resource
    private Validator customer360QueryListValidator;
    @Resource(name = "dataMapper")
    private DataMapper dataMapper;
    @Resource(name = "customer360FragmentDataAdaptersMap")
    private Map<String, C360FragmentDataAdapter> fragmentDataAdaptersMap;

    @Resource(name = "userService")
    private UserService userService;

    @Resource(name = "CSACustomerCouponsFragment")
    private Fragment csaCustomerCouponsFragment;

    @Resource(name = "promotionSectionFragments")
    private List<Fragment> promotionSectionFragments;

    @PostMapping(value="/{baseSiteId}/users/{userId}/customer360", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(operationId = "searchCustomer360", summary = "Retrieves the Customer 360 data.", description = "Retrieves a list of Customer 360 data.")
    @ApiBaseSiteIdAndUserIdParam
    @ResponseBody
    public Customer360ListWsDTO searchCustomer360(
            @Parameter(description = "Base site ID", required = true) @PathVariable(required = true) final String baseSiteId,
            @Parameter(description = "User identifier", required = true) @PathVariable(required = true) final String userId,
            @Parameter(description = "The list of Customer 360 Data to be queried", required = true) @RequestBody @Nonnull final Customer360QueryListWsDTO customer360QueryList
    ) {
        validate(customer360QueryList, "customer360QueryList", getCustomer360QueryListValidator());

        final UserModel currentUser = userService.getCurrentUser();
        // only customers can search c360
        if (currentUser == null || !CustomerModel.class.isAssignableFrom(currentUser.getClass())) {
            throw new RequestParameterException("request path parameter userId must be a customer userId", RequestParameterException.INVALID, "userId");
        }
        // in case promotionSection's fragment is override by customercouponaddon if customer don't apply new customer coupon code
        ensureCSACustomerCouponsFragmentAdded();
        final List<Customer360DataWsDTO> customer360Data = customer360QueryList.getCustomer360Queries()
                .stream()
                .map(this::getFragmentData)
                .collect(Collectors.toList());

        final Customer360ListWsDTO customer360List = new Customer360ListWsDTO();
        customer360List.setValue(customer360Data);

        return customer360List;
    }

    private void ensureCSACustomerCouponsFragmentAdded()
    {
        if (!promotionSectionFragments.contains(csaCustomerCouponsFragment))
        {
            promotionSectionFragments.add(csaCustomerCouponsFragment);
        }
    }

    protected void validate(final Object object, final String objectName, final Validator validator)
    {
        final Errors errors = new BeanPropertyBindingResult(object, objectName);
        validator.validate(object, errors);
        if (errors.hasErrors())
        {
            throw new WebserviceValidationException(errors);
        }
    }

    public Map<String, String> getDataTypeSectionMap() { return dataTypeSectionMap; }

    public Map<String, String> getDataTypeFragmentMap()
    {
        return dataTypeFragmentMap;
    }

    public Validator getCustomer360QueryListValidator() {
        return customer360QueryListValidator;
    }

    public void setCustomer360QueryListValidator(Validator customer360QueryListValidator)
    {
        this.customer360QueryListValidator = customer360QueryListValidator;
    }

    public DataMapper getDataMapper() { return dataMapper; }

    public void setDataMapper(DataMapper dataMapper)
    {
        this.dataMapper = dataMapper;
    }

    public AdditionalInformationFrameworkFacade getAdditionalInformationFrameworkFacade()
    {
        return additionalInformationFrameworkFacade;
    }

    public void setAdditionalInformationFrameworkFacade(AdditionalInformationFrameworkFacade additionalInformationFrameworkFacade)
    {
        this.additionalInformationFrameworkFacade = additionalInformationFrameworkFacade;
    }

    public void setDataTypeSectionMap(Map<String, String> dataTypeSectionMap)
    {
        this.dataTypeSectionMap = dataTypeSectionMap;
    }

    public void setDataTypeFragmentMap(Map<String, String> dataTypeFragmentMap)
    {
        this.dataTypeFragmentMap = dataTypeFragmentMap;
    }

    public Map<String, C360FragmentDataAdapter> getFragmentDataAdaptersMap()
    {
        return fragmentDataAdaptersMap;
    }

    public void setFragmentDataAdaptersMap(final Map<String, C360FragmentDataAdapter> fragmentDataAdaptersMap)
    {
        this.fragmentDataAdaptersMap = fragmentDataAdaptersMap;
    }

    public void setPromotionSectionFragments(final List<Fragment> fragments)
    {
        this.promotionSectionFragments = fragments;
    }

    private Customer360DataWsDTO getFragmentData(Customer360QueryWsDTO query)
    {
        final String type = query.getType();
        Map<String, String> additionalRequestParameters =  query.getAdditionalRequestParameters();
        if (additionalRequestParameters == null)
        {
            additionalRequestParameters = new HashMap<>();
        }
        additionalRequestParameters.put("isSiteRelated", "true");
        final Fragment fragment = additionalInformationFrameworkFacade.getFragment(
              getDataTypeSectionMap().get(type),
              getDataTypeFragmentMap().get(type),
              additionalRequestParameters
        );
        if (fragment == null)
        {
            throw new AssistedServiceCustomer360Exception(String.format("Encountered an error when getting data type %s", type));
        }
        final C360FragmentDataAdapter fragmentDataAdapter = getFragmentDataAdaptersMap().get(type);
        if (fragmentDataAdapter != null)
        {
            return fragmentDataAdapter.adapt(fragment.getData());
        }
        return getDataMapper().map(fragment.getData(), Customer360DataWsDTO.class);
    }

    public UserService getUserService()
    {
        return userService;
    }

    public void setUserService(UserService userService)
    {
        this.userService = userService;
    }
}
