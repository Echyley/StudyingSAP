/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.customerticketingocc.validators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.customerticketingocc.dto.ticket.TicketCategoryWsDTO;
import de.hybris.platform.customerticketingocc.dto.ticket.TicketStarterWsDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class TicketStarterCategoryValidatorTest
{
    private static final String OBJECT_NAME = "ticketStarterWsDTO";
    private static final String FIELD_REQUIRED_ERROR_CODE = "field.required";
    private static final String TICKET_CATEGORY_NAME_ENQUIRY = "ENQUIRY";

    @InjectMocks
    private TicketStarterCategoryValidator ticketStarterCategoryValidator;

    @Test
    public void testValidateWhenCategoryIsNull()
    {
        final TicketStarterWsDTO ticketStarter = new TicketStarterWsDTO();
        final Errors errors = new BeanPropertyBindingResult(ticketStarter, OBJECT_NAME);

        ticketStarterCategoryValidator.validate(ticketStarter, errors);

        assertThat(errors.getErrorCount()).isEqualTo(0);
    }

    @Test
    public void testValidateWhenCategoryIdIsNull()
    {
        final TicketStarterWsDTO ticketStarter = new TicketStarterWsDTO();
        final TicketCategoryWsDTO ticketCategoryWsDTO = new TicketCategoryWsDTO();
        ticketStarter.setTicketCategory(ticketCategoryWsDTO);
        final Errors errors = new BeanPropertyBindingResult(ticketStarter, OBJECT_NAME);

        ticketStarterCategoryValidator.validate(ticketStarter, errors);

        assertThat(errors.getErrorCount()).isEqualTo(1);
    }

    @Test
    public void testValidateWhenCategoryIdIsEmpty()
    {
        final TicketStarterWsDTO ticketStarter = new TicketStarterWsDTO();
        final TicketCategoryWsDTO ticketCategoryWsDTO = new TicketCategoryWsDTO();
        ticketCategoryWsDTO.setId("");
        ticketStarter.setTicketCategory(ticketCategoryWsDTO);
        final Errors errors = new BeanPropertyBindingResult(ticketStarter, OBJECT_NAME);

        ticketStarterCategoryValidator.validate(ticketStarter, errors);

        assertThat(errors.getErrorCount()).isEqualTo(1);
        assertThat(errors.getFieldError().getField()).isEqualTo("ticketCategory.id");
        assertThat(errors.getFieldError().getCode()).isEqualTo(FIELD_REQUIRED_ERROR_CODE);
    }

    @Test
    public void testValidateWhenCategoryIdIsInvalid()
    {
        final TicketStarterWsDTO ticketStarter = new TicketStarterWsDTO();
        final TicketCategoryWsDTO ticketCategoryWsDTO = new TicketCategoryWsDTO();
        ticketCategoryWsDTO.setId("test");
        ticketStarter.setTicketCategory(ticketCategoryWsDTO);
        final Errors errors = new BeanPropertyBindingResult(ticketStarter, OBJECT_NAME);

        ticketStarterCategoryValidator.validate(ticketStarter, errors);

        assertThat(errors.getErrorCount()).isEqualTo(1);
        assertThat(errors.getFieldError().getField()).isEqualTo("ticketCategory.id");
        assertThat(errors.getFieldError().getCode()).isEqualTo(FIELD_REQUIRED_ERROR_CODE);
    }

    @Test
    public void testValidateWhenCategoryIdIsValid()
    {
        final TicketStarterWsDTO ticketStarter = new TicketStarterWsDTO();
        final TicketCategoryWsDTO ticketCategoryWsDTO = new TicketCategoryWsDTO();
        ticketCategoryWsDTO.setId(TICKET_CATEGORY_NAME_ENQUIRY);
        ticketStarter.setTicketCategory(ticketCategoryWsDTO);
        final Errors errors = new BeanPropertyBindingResult(ticketStarter, OBJECT_NAME);

        ticketStarterCategoryValidator.validate(ticketStarter, errors);

        assertThat(errors.getErrorCount()).isEqualTo(0);
    }

    @Test
    public void testValidateWhenErrorsIsNull()
    {
        final TicketStarterWsDTO ticketStarter = new TicketStarterWsDTO();

        assertThatThrownBy(() -> ticketStarterCategoryValidator.validate(ticketStarter, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Errors object must not be null.");
    }
}
