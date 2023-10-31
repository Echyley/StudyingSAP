/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.validator;

import de.hybris.platform.assistedservicewebservices.dto.Customer360QueryWsDTO;
import de.hybris.platform.util.Config;
import org.apache.solr.common.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Customer360QueryWsDTOValidator implements Validator {
    protected static final String FIELD_REQUIRED = "field.required";
    protected static final String FIELD_INVALID = "field.invalid";
    protected static final String LIST_SIZE_INVALID = "assistedservice.list.size.field.invalid";
    protected static final String TYPE = "type";
    protected static final String ADDITIONAL_PARAMETERS ="additionalRequestParameters";
    protected static final String LIST_SIZE_KEY ="listSize";
    private static final int MAX_LIST_SIZE = 999;

    private List<String> c360PreviewTypes = Arrays.asList("c360CustomerProductInterestList", "c360CouponList",
            "c360PromotionList", "c360CustomerCouponList", "c360Cart", "c360SavedCart", "c360Overview", "c360CustomerProfile", "c360ActivityList", "c360TicketList");
    private List<String> supportListSizeTypes = Arrays.asList("c360CustomerProductInterestList", "c360ReviewList", "c360TicketList", "c360SavedCart", "c360ActivityList");
    private List<String> validTypes;

    private static final String CUSTOMER360_FEATURE_FLAG = "toggle.customer360.enabled";

    @Override
    public boolean supports(Class<?> clazz) {
        return Customer360QueryWsDTO.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Customer360QueryWsDTO customer360QueryWsDTO = (Customer360QueryWsDTO) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, TYPE, FIELD_REQUIRED);
        List<String> filteredTypes = validTypes;
        if(!Config.getBoolean(CUSTOMER360_FEATURE_FLAG, false))
        {
            filteredTypes = validTypes.stream()
                    .filter(type -> !c360PreviewTypes.contains(type))
                    .collect(Collectors.toList());
        }

        if (!StringUtils.isEmpty(customer360QueryWsDTO.getType()) && !filteredTypes.contains(customer360QueryWsDTO.getType()))
        {
            errors.rejectValue(TYPE, FIELD_INVALID, new String[] { TYPE }, "Invalid field: {0}");
            return;
        }

        final Map<String, String> requestParameters = customer360QueryWsDTO.getAdditionalRequestParameters();
        if (requestParameters != null && !StringUtils.isEmpty(requestParameters.get(LIST_SIZE_KEY))
				  && supportListSizeTypes.contains(customer360QueryWsDTO.getType()))
        {
            final String listSize = requestParameters.get(LIST_SIZE_KEY);
            int searchListSize;
            try
            {
                searchListSize = Integer.parseInt(listSize);
            }
            catch (final NumberFormatException formatException)
            {
                errors.rejectValue(ADDITIONAL_PARAMETERS, LIST_SIZE_INVALID, new String[] { LIST_SIZE_KEY, "1", Integer.toString(MAX_LIST_SIZE) }, "The field {0} should be an integer in a range from {1} to {2}.");
                return;
            }
            if (searchListSize <= 0 || searchListSize > MAX_LIST_SIZE)
            {
                errors.rejectValue(ADDITIONAL_PARAMETERS, LIST_SIZE_INVALID, new String[] { LIST_SIZE_KEY, "1", Integer.toString(MAX_LIST_SIZE) }, "The field {0} should be an integer in a range from {1} to {2}.");
            }
        }
    }

    public List<String> getValidTypes()
    {
        return validTypes;
    }

    public void setValidTypes(List<String> validTypes)
    {
        this.validTypes = validTypes;
    }
}
