/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.customerticketingocc.errors.exceptions;

import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceException;

public class TicketEventCreateException extends WebserviceException
{
    private static final String TYPE = "TicketEventCreateError";
    private static final String SUBJECT_TYPE = "entry";

    public TicketEventCreateException(final String message)
    {
        super(message);
    }

    public TicketEventCreateException(final String message, final String reason)
    {
        super(message, reason);
    }

    public TicketEventCreateException(final String message, final String reason, final Throwable cause)
    {
        super(message, reason, cause);
    }

    public TicketEventCreateException(final String message, final String reason, final String subject)
    {
        super(message, reason, subject);
    }

    public TicketEventCreateException(final String message, final String reason, final String subject, final Throwable cause)
    {
        super(message, reason, subject, cause);
    }

    @Override
    public String getType()
    {
        return TYPE;
    }

    @Override
    public String getSubjectType()
    {
        return SUBJECT_TYPE;
    }
}
