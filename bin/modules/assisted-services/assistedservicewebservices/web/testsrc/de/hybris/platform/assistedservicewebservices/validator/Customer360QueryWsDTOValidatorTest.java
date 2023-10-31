/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.validator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.assistedservicewebservices.dto.Customer360QueryWsDTO;
import de.hybris.platform.util.Config;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test suite for {@link Customer360QueryWsDTOValidator}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class Customer360QueryWsDTOValidatorTest {
    private static final String OBJECT_NAME = "customer360QueryWsDTO";
    private static final String FIELD_REQUIRED_ERROR_CODE = "field.required";
    private static final String FIELD_INVALID_ERROR_CODE = "field.invalid";

    private static final String QUERY_TYPE = "c360StoreLocation";

    private static final String QUERY_TYPE2 = "c360CouponList";

    private static final String QUERY_TYPE3 = "c360ReviewList";

    private static final String LIST_SIZE_KEY = "listSize";

    private static final String ADDITIONAL_PARAMETERS ="additionalRequestParameters";

    protected static final String LIST_SIZE_INVALID_CODE = "assistedservice.list.size.field.invalid";

    private static final String CUSTOMER360_FEATURE_FLAG = "toggle.customer360.enabled";

    private Customer360QueryWsDTOValidator customer360QueryWsDTOValidator;

    @Before
    public void setUp()
    {
        customer360QueryWsDTOValidator = new Customer360QueryWsDTOValidator();
        customer360QueryWsDTOValidator.setValidTypes( Arrays.asList("c360StoreLocation", "c360ReviewList", "c360CouponList"));
    }

    @Test
    public void testValidateWhenTypeIsNull() {
        try (MockedStatic<Config> config = Mockito.mockStatic(Config.class)) {
            config.when(() -> Config.getBoolean(CUSTOMER360_FEATURE_FLAG, false)).thenReturn(false);
            assertThat(Config.getBoolean(CUSTOMER360_FEATURE_FLAG, false)).isFalse();

            Customer360QueryWsDTO customer360QueryWsDTO = new Customer360QueryWsDTO();
            final Errors errors = new BeanPropertyBindingResult(customer360QueryWsDTO, OBJECT_NAME);

            customer360QueryWsDTOValidator.validate(customer360QueryWsDTO, errors);

            assertThat(errors.getErrorCount()).isEqualTo(1);
            assertThat(errors.getFieldError().getField()).isEqualTo("type");
            assertThat(errors.getFieldError().getCode()).isEqualTo(FIELD_REQUIRED_ERROR_CODE);
        }
    }

    @Test
    public void testValidateWhenTypeIsEmpty() {
        try (MockedStatic<Config> config = Mockito.mockStatic(Config.class)) {
            config.when(() -> Config.getBoolean(CUSTOMER360_FEATURE_FLAG, false)).thenReturn(false);
            assertThat(Config.getBoolean(CUSTOMER360_FEATURE_FLAG, false)).isFalse();

            Customer360QueryWsDTO customer360QueryWsDTO = new Customer360QueryWsDTO();
            customer360QueryWsDTO.setType("");
            final Errors errors = new BeanPropertyBindingResult(customer360QueryWsDTO, OBJECT_NAME);

            customer360QueryWsDTOValidator.validate(customer360QueryWsDTO, errors);

            assertThat(errors.getErrorCount()).isEqualTo(1);
            assertThat(errors.getFieldError().getField()).isEqualTo("type");
            assertThat(errors.getFieldError().getCode()).isEqualTo(FIELD_REQUIRED_ERROR_CODE);
        }
    }

    @Test
    public void testValidateWhenTypeIsInvalid() {
        try (MockedStatic<Config> config = Mockito.mockStatic(Config.class)) {
            config.when(() -> Config.getBoolean(CUSTOMER360_FEATURE_FLAG, false)).thenReturn(false);
            assertThat(Config.getBoolean(CUSTOMER360_FEATURE_FLAG, false)).isFalse();

            Customer360QueryWsDTO customer360QueryWsDTO = new Customer360QueryWsDTO();
            customer360QueryWsDTO.setType("invalidType");
            final Errors errors = new BeanPropertyBindingResult(customer360QueryWsDTO, OBJECT_NAME);

            customer360QueryWsDTOValidator.validate(customer360QueryWsDTO, errors);

            assertThat(errors.getErrorCount()).isEqualTo(1);
            assertThat(errors.getFieldError().getField()).isEqualTo("type");
            assertThat(errors.getFieldError().getCode()).isEqualTo(FIELD_INVALID_ERROR_CODE);
        }
    }

    @Test
    public void testValidateWhenTypeIsValid() {
        try (MockedStatic<Config> config = Mockito.mockStatic(Config.class)) {
            config.when(() -> Config.getBoolean(CUSTOMER360_FEATURE_FLAG, false)).thenReturn(false);
            assertThat(Config.getBoolean(CUSTOMER360_FEATURE_FLAG, false)).isFalse();

            Customer360QueryWsDTO customer360QueryWsDTO = new Customer360QueryWsDTO();
            customer360QueryWsDTO.setType(QUERY_TYPE);
            final Errors errors = new BeanPropertyBindingResult(customer360QueryWsDTO, OBJECT_NAME);

            customer360QueryWsDTOValidator.validate(customer360QueryWsDTO, errors);

            assertThat(errors.getErrorCount()).isZero();
        }
    }

    @Test
    public void testValidateWhenFlagOpenAndTypeIsValid() {
        try (MockedStatic<Config> config = Mockito.mockStatic(Config.class)) {
            config.when(() -> Config.getBoolean(CUSTOMER360_FEATURE_FLAG, false)).thenReturn(true);
            assertThat(Config.getBoolean(CUSTOMER360_FEATURE_FLAG, false)).isTrue();

            Customer360QueryWsDTO customer360QueryWsDTO = new Customer360QueryWsDTO();
            customer360QueryWsDTO.setType(QUERY_TYPE2);
            final Errors errors = new BeanPropertyBindingResult(customer360QueryWsDTO, OBJECT_NAME);

            customer360QueryWsDTOValidator.validate(customer360QueryWsDTO, errors);

            assertThat(errors.getErrorCount()).isZero();
        }
    }

    @Test
    public void testValidateWhenListSizeIsNotInteger() {
        try (MockedStatic<Config> config = Mockito.mockStatic(Config.class)) {
            config.when(() -> Config.getBoolean(CUSTOMER360_FEATURE_FLAG, false)).thenReturn(false);
            assertThat(Config.getBoolean(CUSTOMER360_FEATURE_FLAG, false)).isFalse();

            Customer360QueryWsDTO customer360QueryWsDTO = new Customer360QueryWsDTO();
            customer360QueryWsDTO.setType(QUERY_TYPE3);
            final Map<String, String> additionalParameters = new HashMap<>();
            additionalParameters.put(LIST_SIZE_KEY, "a");
            customer360QueryWsDTO.setAdditionalRequestParameters(additionalParameters);
            final Errors errors = new BeanPropertyBindingResult(customer360QueryWsDTO, OBJECT_NAME);

            customer360QueryWsDTOValidator.validate(customer360QueryWsDTO, errors);

            assertThat(errors.getErrorCount()).isEqualTo(1);
            assertThat(errors.getFieldError().getField()).isEqualTo(ADDITIONAL_PARAMETERS);
            assertThat(errors.getFieldError().getCode()).isEqualTo(LIST_SIZE_INVALID_CODE);
        }
    }

    @Test
    public void testValidateWhenListSizeIsOutOfRange() {
        try (MockedStatic<Config> config = Mockito.mockStatic(Config.class)) {
            config.when(() -> Config.getBoolean(CUSTOMER360_FEATURE_FLAG, false)).thenReturn(true);
            assertThat(Config.getBoolean(CUSTOMER360_FEATURE_FLAG, false)).isTrue();

            Customer360QueryWsDTO customer360QueryWsDTO = new Customer360QueryWsDTO();
            customer360QueryWsDTO.setType(QUERY_TYPE3);
            final Map<String, String> additionalParameters = new HashMap<>();
            additionalParameters.put(LIST_SIZE_KEY, "-1");
            customer360QueryWsDTO.setAdditionalRequestParameters(additionalParameters);
            final Errors errors = new BeanPropertyBindingResult(customer360QueryWsDTO, OBJECT_NAME);

            customer360QueryWsDTOValidator.validate(customer360QueryWsDTO, errors);

            assertThat(errors.getErrorCount()).isEqualTo(1);
            assertThat(errors.getFieldError().getField()).isEqualTo(ADDITIONAL_PARAMETERS);
            assertThat(errors.getFieldError().getCode()).isEqualTo(LIST_SIZE_INVALID_CODE);
        }
    }

    @Test
    public void testValidateWhenListSizeSupportedAndValid() {
        try (MockedStatic<Config> config = Mockito.mockStatic(Config.class)) {
            config.when(() -> Config.getBoolean(CUSTOMER360_FEATURE_FLAG, false)).thenReturn(true);
            assertThat(Config.getBoolean(CUSTOMER360_FEATURE_FLAG, false)).isTrue();

            Customer360QueryWsDTO customer360QueryWsDTO = new Customer360QueryWsDTO();
            customer360QueryWsDTO.setType(QUERY_TYPE3);
            final Map<String, String> additionalParameters = new HashMap<>();
            additionalParameters.put(LIST_SIZE_KEY, "10");
            customer360QueryWsDTO.setAdditionalRequestParameters(additionalParameters);
            final Errors errors = new BeanPropertyBindingResult(customer360QueryWsDTO, OBJECT_NAME);

            customer360QueryWsDTOValidator.validate(customer360QueryWsDTO, errors);

            assertThat(errors.getErrorCount()).isZero();
        }
    }

    @Test
    public void testValidateWhenListSizeNotSupported() {
        try (MockedStatic<Config> config = Mockito.mockStatic(Config.class)) {
            config.when(() -> Config.getBoolean(CUSTOMER360_FEATURE_FLAG, false)).thenReturn(true);
            assertThat(Config.getBoolean(CUSTOMER360_FEATURE_FLAG, false)).isTrue();

            Customer360QueryWsDTO customer360QueryWsDTO = new Customer360QueryWsDTO();
            customer360QueryWsDTO.setType(QUERY_TYPE2);
            final Map<String, String> additionalParameters = new HashMap<>();
            additionalParameters.put(LIST_SIZE_KEY, "10");
            customer360QueryWsDTO.setAdditionalRequestParameters(additionalParameters);
            final Errors errors = new BeanPropertyBindingResult(customer360QueryWsDTO, OBJECT_NAME);

            customer360QueryWsDTOValidator.validate(customer360QueryWsDTO, errors);

            assertThat(errors.getErrorCount()).isZero();
        }
    }
}
