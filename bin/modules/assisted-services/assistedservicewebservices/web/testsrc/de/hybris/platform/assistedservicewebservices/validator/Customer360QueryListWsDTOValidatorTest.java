/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.validator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.assistedservicewebservices.dto.Customer360QueryListWsDTO;
import de.hybris.platform.assistedservicewebservices.dto.Customer360QueryWsDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;

/**
 * Test suite for {@link Customer360QueryListWsDTOValidator}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class Customer360QueryListWsDTOValidatorTest {
    private static final String OBJECT_NAME = "customer360QueryListWsDTO";
    private static final String FIELD_REQUIRED_ERROR_CODE = "field.required";
    private static final String QUERY_TYPE = "C360Type";
    private List<String> validTypes;
    @Mock
    private Customer360QueryWsDTOValidator customer360QueryWsDTOValidator;

    private Customer360QueryListWsDTOValidator customer360QueryListWsDTOValidator;

    @Before
    public void setUp() {
        customer360QueryListWsDTOValidator = new Customer360QueryListWsDTOValidator();
        customer360QueryListWsDTOValidator.setCustomer360QueryValidator(customer360QueryWsDTOValidator);
        validTypes = Arrays.asList(QUERY_TYPE);
    }

    @Test
    public void testValidateWhenQueriesIsNull() {
        Customer360QueryListWsDTO customer360QueryListWsDTO = new Customer360QueryListWsDTO();
        final Errors errors = new BeanPropertyBindingResult(customer360QueryListWsDTO, OBJECT_NAME);

        customer360QueryListWsDTOValidator.validate(customer360QueryListWsDTO, errors);

        assertThat(errors.getErrorCount()).isEqualTo(1);
        assertThat(errors.getFieldError().getField()).isEqualTo("customer360Queries");
        assertThat(errors.getFieldError().getCode()).isEqualTo(FIELD_REQUIRED_ERROR_CODE);
    }

    @Test
    public void testValidateWhenQueriesIsEmpty() {
        Customer360QueryListWsDTO customer360QueryListWsDTO = new Customer360QueryListWsDTO();
        List<Customer360QueryWsDTO> customer360QueryWsDTOList = new ArrayList<>();
        customer360QueryListWsDTO.setCustomer360Queries(customer360QueryWsDTOList);
        final Errors errors = new BeanPropertyBindingResult(customer360QueryListWsDTO, OBJECT_NAME);

        customer360QueryListWsDTOValidator.validate(customer360QueryListWsDTO, errors);

        assertThat(errors.getErrorCount()).isEqualTo(1);
        assertThat(errors.getFieldError().getField()).isEqualTo("customer360Queries");
        assertThat(errors.getFieldError().getCode()).isEqualTo(FIELD_REQUIRED_ERROR_CODE);
    }

    @Test
    public void testValidateWhenQueriesContainsValidQuery() {
        given(customer360QueryWsDTOValidator.supports(Customer360QueryWsDTO.class)).willReturn(true);

        Customer360QueryListWsDTO customer360QueryListWsDTO = new Customer360QueryListWsDTO();

        Customer360QueryWsDTO customer360QueryWsDTO = new Customer360QueryWsDTO();
        customer360QueryWsDTO.setType(QUERY_TYPE);
        List<Customer360QueryWsDTO> customer360QueryWsDTOList = Arrays.asList(customer360QueryWsDTO);
        customer360QueryListWsDTO.setCustomer360Queries(customer360QueryWsDTOList);

        final Errors errors = new BeanPropertyBindingResult(customer360QueryListWsDTO, OBJECT_NAME);

        doAnswer(invocationOnMock -> null).when(customer360QueryWsDTOValidator)
                .validate(eq(customer360QueryWsDTO), any());

        customer360QueryListWsDTOValidator.validate(customer360QueryListWsDTO, errors);

        assertThat(errors.getErrorCount()).isZero();
    }
}
