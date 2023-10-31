/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.runtime;

import de.hybris.platform.regioncache.ConcurrentHashSet;
import de.hybris.platform.sap.core.common.util.GenericFactory;
import de.hybris.platform.servicelayer.event.events.BeforeSessionCloseEvent;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.session.Session;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * SAP Hybris Session Provider.
 */
public class SAPHybrisSessionProvider extends AbstractEventListener<BeforeSessionCloseEvent>
{

	/**
	 * Alias of the SAP hybris session.
	 */
	public static final String SAP_HYBRIS_SESSION_ALIAS = "sapCoreHybrisSession";

	/**
	 * Session attribute to store the SAP hybris session.
	 */
	public static final String SAP_HYBRIS_SESSION_ATTRIBUTE = "SAPHybrisSession";

	/**
	 * hybris {@link SessionService}.
	 */
	private SessionService sessionService;

	/**
	 * {@link GenericFactory} reference.
	 */
	protected GenericFactory genericFactory;

	/**
	 * Set of listeners which listens to the {@link SAPHybrisSessionEventListener} interface.
	 */
	protected final Set<SAPHybrisSessionEventListener> eventListener = new ConcurrentHashSet<>();

	/**
	 * Session which are about to be deleted.
	 */
	protected final Map<Session, SAPHybrisSession> sessionsToBeDeleted = new ConcurrentHashMap<>();

	public SessionService getSessionService() {
		throw new UnsupportedOperationException("Please override #getSessionService() or use <lookup-method> from Spring configuration.");
	}

	public void setSessionService(SessionService sessionService) {
		this.sessionService = sessionService;
	}

	/**
	 * Injection setter for the {@link GenericFactory}.
	 * 
	 * @param genericFactory
	 *           the {@link GenericFactory} to set
	 */
	public void setGenericFactory(final GenericFactory genericFactory)
	{
		this.genericFactory = genericFactory;
	}

	/**
	 * Gets the current {@link SAPHybrisSession}.
	 * 
	 * @return {@link SAPHybrisSession}
	 */
	public SAPHybrisSession getSAPHybrisSession()
	{
		return getSAPHybrisSessionInternal();
	}

	/**
	 * Destroys the current {@link SAPHybrisSession}.
	 */
	public void destroySAPHybrisSession()
	{
		destroySAPHybrisSessionInternal(getSessionService().getCurrentSession());
	}


	/**
	 * Checks if a {@link SAPHybrisSession} exists within the hybris {@link Session}.
	 * 
	 * @return true, if session exists
	 */
	public boolean existsSAPHybrisSession()
	{
		final Session defaultSession = getSessionService().getCurrentSession();
		SAPHybrisSession sapHybrisSession = defaultSession.getAttribute(SAP_HYBRIS_SESSION_ATTRIBUTE);
		if (sapHybrisSession == null)
		{
			// Check if this is a session to be deleted
			sapHybrisSession = sessionsToBeDeleted.get(defaultSession);
		}
		return (sapHybrisSession != null);
	}

	/**
	 * Registers a {@link SAPHybrisSessionEventListener}.
	 * 
	 * @param listener
	 *           Event listener
	 */
	public void registerEventListener(final SAPHybrisSessionEventListener listener)
	{
		eventListener.add(listener);
	}

	/**
	 * Unregisters a {@link SAPHybrisSessionEventListener}.
	 * 
	 * @param listener
	 *           Event listener
	 */
	public void unregisterEventListener(final SAPHybrisSessionEventListener listener)
	{
		eventListener.remove(listener);
	}

	@Override
	protected void onEvent(final BeforeSessionCloseEvent event)
	{
		// BeforeSessionCloseEvent
		if (event != null)
		{
			final Session toBeClosedSession = getSessionService().getBoundSession(event.getSource());
			destroySAPHybrisSessionInternal(toBeClosedSession);
		}
	}

	/**
	 * Creates the {@link SAPHybrisSession} if it does not exist yet and returns it.
	 * 
	 * @return {@link SAPHybrisSession}
	 */
	protected SAPHybrisSession getSAPHybrisSessionInternal()
	{
		final Session defaultSession = getSessionService().getCurrentSession();
		SAPHybrisSession sapHybrisSession = defaultSession.getAttribute(SAP_HYBRIS_SESSION_ATTRIBUTE);
		if (sapHybrisSession == null)
		{
			if (sessionsToBeDeleted.containsKey(defaultSession))
			{
				return sessionsToBeDeleted.get(defaultSession);
			}
			sapHybrisSession = (SAPHybrisSession) genericFactory.getBean(SAP_HYBRIS_SESSION_ALIAS);
			sapHybrisSession.setSessionId(defaultSession.getSessionId());
			defaultSession.setAttribute(SAP_HYBRIS_SESSION_ATTRIBUTE, sapHybrisSession);
		}
		return sapHybrisSession;
	}

	/**
	 * Destroys the current {@link SAPHybrisSession} if exists.
	 * 
	 * @param session
	 *           hybris {@link Session}
	 */
	protected void destroySAPHybrisSessionInternal(final Session session)
	{
		final SAPHybrisSession sapHybrisSession = (SAPHybrisSession) session.getAttribute(SAP_HYBRIS_SESSION_ATTRIBUTE);
		if (sapHybrisSession != null)
		{
			sessionsToBeDeleted.put(getSessionService().getCurrentSession(), sapHybrisSession);
			sapHybrisSession.destroy();
			sessionsToBeDeleted.remove(getSessionService().getCurrentSession());
			for (final SAPHybrisSessionEventListener sapHybrisSessionEventListener : eventListener)
			{
				sapHybrisSessionEventListener.onAfterDestroy(sapHybrisSession);
			}
			session.removeAttribute(SAP_HYBRIS_SESSION_ATTRIBUTE);
		}
	}

}
