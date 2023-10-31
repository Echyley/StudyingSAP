/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.customerticketingocc.validators;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.customerticketingocc.dto.ticket.TicketAssociatedObjectWsDTO;
import de.hybris.platform.customerticketingocc.dto.ticket.TicketEventWsDTO;
import de.hybris.platform.customerticketingocc.dto.ticket.TicketStarterWsDTO;
import de.hybris.platform.customerticketingocc.dto.ticket.TicketStatusWsDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OptionalObjectValidatorTest
{
	private static final String OBJECT_NAME_TICKET_EVENT = "ticketEvent";
	private static final String OBJECT_NAME_TICKET_STARTER = "ticketStarter";
	private static final String FIELD_PATH_TO_STATUS = "ToStatus";
	private static final String FIELD_PATH_ASSOCIATED_TO = "associatedTo";

	@InjectMocks
	private OptionalObjectValidator optionalObjectValidator;

	@Mock
	private Validator validator;

	@Test
	public void testValidateTicketEventWhenToStatusIsNull()
	{
		final TicketEventWsDTO ticketEvent = new TicketEventWsDTO();
		final Errors errors = new BeanPropertyBindingResult(ticketEvent, OBJECT_NAME_TICKET_EVENT);
		optionalObjectValidator.setFieldPath(FIELD_PATH_TO_STATUS);

		optionalObjectValidator.validate(ticketEvent, errors);

		verifyNoMoreInteractions(validator);
	}

	@Test
	public void testValidateTicketStarterWhenAssociatedToIsNull()
	{
		final TicketStarterWsDTO ticketStarter = new TicketStarterWsDTO();
		final Errors errors = new BeanPropertyBindingResult(ticketStarter, OBJECT_NAME_TICKET_STARTER);
		optionalObjectValidator.setFieldPath(FIELD_PATH_ASSOCIATED_TO);

		optionalObjectValidator.validate(ticketStarter, errors);

		verifyNoMoreInteractions(validator);
	}

	@Test
	public void testValidateTicketEventWhenToStatusIsNotNull()
	{
		final TicketEventWsDTO ticketEvent = new TicketEventWsDTO();
		final TicketStatusWsDTO ticketStatus = new TicketStatusWsDTO();
		ticketEvent.setToStatus(ticketStatus);
		final Errors errors = new BeanPropertyBindingResult(ticketEvent, OBJECT_NAME_TICKET_EVENT);
		optionalObjectValidator.setFieldPath(FIELD_PATH_TO_STATUS);

		optionalObjectValidator.validate(ticketEvent, errors);

		verify(validator).validate(ticketEvent, errors);
	}

	@Test
	public void testValidateTicketStarterWhenAssociatedToIsNotNull()
	{
		final TicketStarterWsDTO ticketStarter = new TicketStarterWsDTO();
		final TicketAssociatedObjectWsDTO ticketAssociatedObjectWsDTO = new TicketAssociatedObjectWsDTO();
		ticketStarter.setAssociatedTo(ticketAssociatedObjectWsDTO);
		final Errors errors = new BeanPropertyBindingResult(ticketStarter, OBJECT_NAME_TICKET_STARTER);
		optionalObjectValidator.setFieldPath(FIELD_PATH_ASSOCIATED_TO);

		optionalObjectValidator.validate(ticketStarter, errors);

		verify(validator).validate(ticketStarter, errors);
	}

	@Test
	public void testValidateWhenErrorsIsNull()
	{
		final Object object = new Object();

		assertThatThrownBy(() -> optionalObjectValidator.validate(object, null))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Errors object must not be null.");
	}
}
