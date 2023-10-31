/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.integrationtests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.integrationtests.CPQFacadeLayerTest;
import de.hybris.platform.sap.productconfig.runtime.interf.services.ProductConfigSessionAttributeContainer;
import de.hybris.platform.servicelayer.session.SessionService;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang3.SerializationException;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


/**
 * Integration test for CPQ Session Fail Over Integration
 */
@IntegrationTest
public class CPQSessionFailOverIntegrationTest extends CPQFacadeLayerTest
{

	private static final String CPQ_PACKAGE_PREFIX = "de.hybris.platform.sap.productconfig";
	private static final Logger LOG = Logger.getLogger(CPQSessionFailOverIntegrationTest.class);
	protected static final Set<String> blackList;
	protected static final Set<String> whiteList;

	private List<String> failures;

	static
	{
		blackList = new HashSet();

		whiteList = new HashSet();
		whiteList.add(ProductConfigSessionAttributeContainer.class.getName());
		whiteList.add("de.hybris.platform.sap.productconfig.runtime.cps.cache.CPSSessionAttributeContainer");
		whiteList.add("sapProductConfigActiveProviderName");

	}

	@Before
	public void setUp() throws Exception
	{
		prepareCPQData();
		failures = new ArrayList();
	}

	@Override
	protected void prepareCPQData() throws Exception
	{
		super.prepareCPQData();
	}

	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Test
	public void testSeriliazeDeSerializeSession() throws IOException, CommerceCartModificationException
	{

		//Ensure that some CPQ objects are in session, by starting configuring
		ConfigurationData configData = cpqFacade.getConfiguration(KB_KEY_CPQ_LAPTOP);
		facadeConfigValueHelper.setCstic(configData, "CPQ_DISPLAY", "17");
		cpqFacade.updateConfiguration(configData);
		configData = cpqFacade.getConfiguration(configData);

		final Map<String, Object> sessionMap = sessionService.getCurrentSession().getAllAttributes();
		for (final Entry<String, Object> sesssionEntry : sessionMap.entrySet())
		{
			final Object sessionMember = sesssionEntry.getValue();
			if (null == sessionMember)
			{
				LOG.info("Ignoreing NULL-VALUE fo sessio key: " + sesssionEntry.getKey());
				continue;
			}

			if (isCPQRelatedSessionmember(sessionMember, sesssionEntry.getKey()))
			{
				checkSeriliazeDeSerializeCPQAttribute(sessionMember, sesssionEntry.getKey());
			}
			else
			{
				LOG.info("Ignoreing class '" + sessionMember.getClass().getName()
						+ "' as it is not a CPQ related attribute. Strored under key: " + sesssionEntry.getKey());
			}
		}

		if (failures.size() > 0)
		{
			throwErrors();
		}

	}

	protected void throwErrors() throws AssertionError
	{
		int counter = 1;
		final StringBuilder builder = new StringBuilder();
		builder.append("Detected (De-)Serialisation issues with " + failures.size() + " CPQ attribues. [");
		for (final String error : failures)
		{
			builder.append("(" + counter + ": " + error + ")");
			if (counter != failures.size())
			{
				builder.append(", ");
			}
			counter++;
		}
		builder.append("]");

		final AssertionError err = new AssertionError(builder.toString());
		throw err;
	}

	private void checkSeriliazeDeSerializeCPQAttribute(final Object sessionMember, final String sessionKey)
	{
		final String className = sessionMember.getClass().getName();
		if (blackList.contains(className) || blackList.contains(sessionKey))
		{
			LOG.info("Testing and expecting FAILURE for BLACKLISTED CPQ class '" + sessionMember.getClass().getName()
					+ "'. Strored under key: " + sessionKey);
			serializeExpectFailure(sessionMember, sessionKey);
		}
		else if (whiteList.contains(className) || whiteList.contains(sessionKey))
		{
			LOG.info("Testing and expecting SUCCESS for WHITELISTED CPQ class '" + sessionMember.getClass().getName()
					+ "'. Strored under key: " + sessionKey);
			serializeDeSerializeExpectSuccess(sessionMember, sessionKey);
		}
		else
		{
			LOG.info("Aborting Test and reporting FAILURE for UNKNOWN CPQ class '" + sessionMember.getClass().getName()
					+ "'. Strored under key: " + sessionKey);
			final String error = "Unknown CPQ Attribute className='" + className + "'; stored under key='" + sessionKey
					+ "'. Please add either to black or white list of test.";
			failures.add(error);
		}
	}


	private void serializeExpectFailure(final Object sessionMember, final String sessionKey)
	{
		try
		{
			SerializationUtils.serialize((Serializable) sessionMember);
			final String error = "Was able to serialitze black listed attribute: class='" + sessionMember.getClass().getName()
					+ "'; stored under key='" + sessionKey + "'. Please add attribute to whitelist.";
			failures.add(error);
		}
		catch (final SerializationException | ClassCastException ex)
		{
			LOG.debug("Expected Serialization failure for className='" + sessionMember.getClass().getName() + "'; stored under key='"
					+ sessionKey + "'.", ex);
		}
	}

	protected void serializeDeSerializeExpectSuccess(final Object sessionMember, final String sessionKey)
	{
		final byte[] serializedObject = SerializationUtils.serialize((Serializable) sessionMember);
		final Object deSerializedObject = SerializationUtils.deserialize(serializedObject);
		try
		{
			assertNotNull(deSerializedObject);
			assertNotSame(sessionMember, deSerializedObject);
		}
		catch (final AssertionError | SerializationException | ClassCastException ex)
		{
			final String error = "Serilization test failed for white listed attribute: class='" + sessionMember.getClass().getName()
					+ "'; stored under key='" + sessionKey + "'. Message: " + ex.getMessage();
			failures.add(error);
			LOG.debug(error, ex);
		}
	}

	private boolean isCPQRelatedSessionmember(final Object sessionMember, final String sessionKey)
	{
		return sessionMember.getClass().getName().startsWith(CPQ_PACKAGE_PREFIX) || sessionKey.startsWith(CPQ_PACKAGE_PREFIX)
				|| sessionKey.toLowerCase().contains("productconfig");
	}
}
