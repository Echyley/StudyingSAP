/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.validator;

import de.hybris.platform.assistedservicewebservices.dto.Customer360QueryListWsDTO;
import de.hybris.platform.assistedservicewebservices.dto.Customer360QueryWsDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.List;

public class Customer360QueryListWsDTOValidator implements Validator  {
    protected static final String CUSTOMER360QUERIES = "customer360Queries";
    protected static final String CUSTOMER360QUERIES_INDEX = "customer360Queries[%d]";
    protected static final String FIELD_REQUIRED = "field.required";

    private Validator customer360QueryValidator;

    @Override
    public boolean supports(final Class clazz)
    {
        return Customer360QueryListWsDTOValidator.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(final Object target, final Errors errors)
    {
        Customer360QueryListWsDTO customer360QueryListWsDTO = (Customer360QueryListWsDTO) target;

        if (CollectionUtils.isEmpty(customer360QueryListWsDTO.getCustomer360Queries()))
        {
            setEmptyListError(errors);
        }
        else
        {
            validateErrorsForQueries(customer360QueryListWsDTO.getCustomer360Queries(), errors);
        }
    }

    protected void validateErrorsForQueries(final List<Customer360QueryWsDTO> list, final Errors errors)
    {
        Integer index = 0;
        for (final Customer360QueryWsDTO query : list)
        {
            errors.pushNestedPath(String.format(CUSTOMER360QUERIES_INDEX, index));
            ValidationUtils.invokeValidator(getCustomer360QueryValidator(), query, errors);
            errors.popNestedPath();
            index++;
        }
    }

    protected void setEmptyListError(final Errors errors)
    {
        errors.rejectValue(CUSTOMER360QUERIES, FIELD_REQUIRED, new String[] { CUSTOMER360QUERIES }, "This field is required");
    }

    public Validator getCustomer360QueryValidator() {
        return customer360QueryValidator;
    }

    public void setCustomer360QueryValidator(Validator customer360QueryValidator)
    {
        this.customer360QueryValidator = customer360QueryValidator;
    }
}
