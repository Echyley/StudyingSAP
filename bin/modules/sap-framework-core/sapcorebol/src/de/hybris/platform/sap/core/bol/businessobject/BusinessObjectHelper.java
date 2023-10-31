/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.bol.businessobject;

import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.bol.logging.LogCategories;
import de.hybris.platform.sap.core.bol.logging.LogSeverity;
import de.hybris.platform.sap.core.common.message.Message;
import de.hybris.platform.sap.core.jco.exceptions.BackendCommunicationException;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.core.jco.exceptions.BackendLogonException;
import de.hybris.platform.sap.core.jco.exceptions.BackendServerStartupException;
import de.hybris.platform.sap.core.jco.exceptions.BackendSystemFailureException;


/**
 * The <code>BusinessObjectHelper</code> splits the {@link BackendException} used by the back end into some more
 * meaningful and back end independent exceptions.
 */
public abstract class BusinessObjectHelper
{

	private static final Log4JWrapper LOG = Log4JWrapper.getInstance(BusinessObjectHelper.class.getName());
	private static final String AN_EXCEPTION_WHILE_COMMUNICATE_WITH_THE_BACKEND_0 = "An exception while communicate with the backend: {0}";

	/**
	 * Splits the {@link BackendException} used by the back end into some more meaningful and back end independent
	 * exceptions.
	 * 
	 * @param ex
	 *           The backend exception to be split
	 * @throws CommunicationException
	 *            {@link CommunicationException}
	 * @throws LogonException
	 *            {@link LogonException}
	 * @throws SystemFailureException
	 *            {@link SystemFailureException}
	 * @throws ServerStartupException
	 *            {@link ServerStartupException}
	 * @throws BORuntimeException
	 *            {@link BORuntimeException}
	 */
	public static void splitException(final BackendException ex) throws CommunicationException, LogonException,
			SystemFailureException, ServerStartupException, BORuntimeException
	{

		LOG.debug(ex);

		if (ex instanceof BackendLogonException)
		{
			final String messageKey = "exception.communication";
			LOG.log(LogSeverity.DEBUG, LogCategories.APPS_COMMON_CORE, AN_EXCEPTION_WHILE_COMMUNICATE_WITH_THE_BACKEND_0,
					new String[]
					{ ex.getMessage() });
			final LogonException wrapperEx = new LogonException(ex.getMessage(), new Message(Message.ERROR, messageKey, null, null),
					ex);
			throw wrapperEx;
		}
		else if (ex instanceof BackendSystemFailureException)
		{
			final String messageKey = "exception.communication";
			LOG.log(LogSeverity.DEBUG, LogCategories.APPS_COMMON_CORE, AN_EXCEPTION_WHILE_COMMUNICATE_WITH_THE_BACKEND_0,
					new String[]
					{ ex.getMessage() });
			final SystemFailureException wrapperEx = new SystemFailureException(ex.getMessage(), new Message(Message.ERROR,
					messageKey, null, null), ex);
			throw wrapperEx;
		}
		else if (ex instanceof BackendServerStartupException)
		{
			final String messageKey = "exception.communication";
			LOG.log(LogSeverity.DEBUG, LogCategories.APPS_COMMON_CORE, AN_EXCEPTION_WHILE_COMMUNICATE_WITH_THE_BACKEND_0,
					new String[]
					{ ex.getMessage() });
			final ServerStartupException wrapperEx = new ServerStartupException(ex.getMessage(), new Message(Message.ERROR,
					messageKey, null, null), ex);
			throw wrapperEx;
		}
		else if (ex instanceof BackendCommunicationException)
		{
			final String messageKey = "exception.communication";
			LOG.log(LogSeverity.DEBUG, LogCategories.APPS_COMMON_CORE, AN_EXCEPTION_WHILE_COMMUNICATE_WITH_THE_BACKEND_0,
					new String[]
					{ ex.getMessage() });
			final CommunicationException wrapperEx = new CommunicationException(ex.getMessage(), new Message(Message.ERROR,
					messageKey, null, null), ex);
			throw wrapperEx;
		}
		else
		{
			LOG.log(LogSeverity.DEBUG, LogCategories.APPS_COMMON_CORE, AN_EXCEPTION_WHILE_COMMUNICATE_WITH_THE_BACKEND_0,
					new String[]
					{ ex.getMessage() });
			final BORuntimeException wrapperEx = new BORuntimeException(ex.getMessage(), ex);
			throw wrapperEx;
		}

	}

}
