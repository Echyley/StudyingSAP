/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.occ.util.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mockStatic;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.occ.ConfigurationOverviewWsDTO;
import de.hybris.platform.sap.productconfig.occ.ConfigurationWsDTO;
import de.hybris.platform.sap.productconfig.occ.CsticValueWsDTO;
import de.hybris.platform.sap.productconfig.occ.CsticWsDTO;
import de.hybris.platform.sap.productconfig.occ.GroupOverviewWsDTO;
import de.hybris.platform.sap.productconfig.occ.GroupWsDTO;
import de.hybris.platform.sap.productconfig.occ.OverviewCsticValueWsDTO;
import de.hybris.platform.sap.productconfig.occ.ProductConfigMessageWsDTO;
import de.hybris.platform.util.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;


@UnitTest
public class MessageHandlerImplTest
{
	private final MessageHandlerImpl classUnderTest = new MessageHandlerImpl();
	private static final String SPACE = "&#x20;";

	@Before
	public void initialize()
	{

	}

	@Test
	public void testProcessMessage()
	{
		final ProductConfigMessageWsDTO message = new ProductConfigMessageWsDTO();
		message.setMessage("Config" + SPACE + "Message");
		message.setExtendedMessage("Extended" + SPACE + "Config" + SPACE + "Message");
		classUnderTest.processMessage(message);
		assertEquals("Config Message", message.getMessage());
		assertEquals("Extended Config Message", message.getExtendedMessage());
	}

	@Test
	public void testProcessMessageNotExists()
	{
		final ProductConfigMessageWsDTO message = new ProductConfigMessageWsDTO();
		message.setMessage(null);
		message.setExtendedMessage(null);
		classUnderTest.processMessage(message);
		assertNull(message.getMessage());
		assertNull(message.getExtendedMessage());
	}

	@Test
	public void testProcessValue()
	{
		final CsticValueWsDTO value = prepareValue("CsticName", "ValueName", 2);
		try (MockedStatic mocked = mockStatic(Config.class))
		{
			mocked.when(() -> Config.getBoolean("toggle.sapproductconfigservices.exposeProductConfigMessages.enabled", false))
					.thenReturn(true);
			classUnderTest.processValue(value);
		}
		verifyValue(value, "CsticName", "ValueName", 2);
	}

	@Test
	public void testProcessAttribute()
	{
		final CsticWsDTO attribute = prepareAttribute("CsticName", 2, 2);
		try (MockedStatic mocked = mockStatic(Config.class))
		{
			mocked.when(() -> Config.getBoolean("toggle.sapproductconfigservices.exposeProductConfigMessages.enabled", false))
					.thenReturn(true);
			classUnderTest.processAttribute(attribute);
		}
		verifyAttribute(attribute, "CsticName", 2, 2);
	}

	@Test
	public void testProcessGroup()
	{
		final GroupWsDTO group = prepareGroup("GroupName", Arrays.asList("SubGroupName1", "SubGroupName2"), 2, 2, 2);
		try (MockedStatic mocked = mockStatic(Config.class))
		{
			mocked.when(() -> Config.getBoolean("toggle.sapproductconfigservices.exposeProductConfigMessages.enabled", false))
					.thenReturn(true);
			classUnderTest.processGroup(group);
		}
		verifyGroup(group, 2, "GroupName", 2, 2, 2);
	}

	@Test
	public void testProcessConfiguration()
	{
		final ConfigurationWsDTO configuration = new ConfigurationWsDTO();

		final List<ProductConfigMessageWsDTO> messages = new ArrayList(2);
		for (int i = 0; i < 2; i++)
		{
			final ProductConfigMessageWsDTO message = new ProductConfigMessageWsDTO();
			message.setMessage("Message" + SPACE + "Configuration" + SPACE + i);
			message.setExtendedMessage("Extended" + SPACE + "Message" + SPACE + "Configuration" + SPACE + i);
			messages.add(message);
		}
		configuration.setMessages(messages);

		final GroupWsDTO group1 = prepareGroup("GroupName1", Arrays.asList("SubGroupName1", "SubGroupName2"), 2, 2, 2);
		final GroupWsDTO group2 = prepareGroup("GroupName2", null, 2, 2, 2);
		final List<GroupWsDTO> groups = new ArrayList<>(2);
		groups.add(group1);
		groups.add(group2);
		configuration.setGroups(groups);

		try (MockedStatic mocked = mockStatic(Config.class))
		{
			mocked.when(() -> Config.getBoolean("toggle.sapproductconfigservices.exposeProductConfigMessages.enabled", false))
					.thenReturn(true);
			classUnderTest.processConfiguration(configuration);
		}

		final List<ProductConfigMessageWsDTO> messagesProcessed = configuration.getMessages();
		assertEquals(2, messagesProcessed.size());
		for (int i = 0; i < 2; i++)
		{
			final ProductConfigMessageWsDTO messageProcessed = messagesProcessed.get(i);
			assertEquals("Message Configuration " + i, messageProcessed.getMessage());
			assertEquals("Extended Message Configuration " + i, messageProcessed.getExtendedMessage());
		}

		verifyGroup(configuration.getGroups().get(0), 2, "GroupName1", 2, 2, 2);
		verifyGroup(configuration.getGroups().get(1), 0, "GroupName2", 2, 2, 2);
	}

	@Test
	public void testProcessOverviewCharacteristicValue()
	{
		final OverviewCsticValueWsDTO value = prepareOverviewCharacteristicValue("CsticName", "ValueName", 2);
		try (MockedStatic mocked = mockStatic(Config.class))
		{
			mocked.when(() -> Config.getBoolean("toggle.sapproductconfigservices.exposeProductConfigMessages.enabled", false))
					.thenReturn(true);
			classUnderTest.processOverviewCharacteristicValue(value);
		}
		verifyOverviewCharacteristicValue(value, "CsticName", "ValueName", 2);
	}

	@Test
	public void testProcessOverviewGroup()
	{
		final GroupOverviewWsDTO group = prepareOverviewGroup("GroupName", Arrays.asList("SubGroupName1", "SubGroupName2"), 2,
				2);
		try (MockedStatic mocked = mockStatic(Config.class))
		{
			mocked.when(() -> Config.getBoolean("toggle.sapproductconfigservices.exposeProductConfigMessages.enabled", false))
					.thenReturn(true);
			classUnderTest.processOverviewGroup(group);
		}
		verifyOverviewGroup(group, 2, "GroupName", 2, 2);
	}

	@Test
	public void testProcessConfigurationOverview()
	{
		final ConfigurationOverviewWsDTO configuration = new ConfigurationOverviewWsDTO();

		final List<ProductConfigMessageWsDTO> messages = new ArrayList(2);
		for (int i = 0; i < 2; i++)
		{
			final ProductConfigMessageWsDTO message = new ProductConfigMessageWsDTO();
			message.setMessage("Message" + SPACE + "Configuration" + SPACE + i);
			message.setExtendedMessage("Extended" + SPACE + "Message" + SPACE + "Configuration" + SPACE + i);
			messages.add(message);
		}
		configuration.setMessages(messages);

		final GroupOverviewWsDTO group1 = prepareOverviewGroup("GroupName1", Arrays.asList("SubGroupName1", "SubGroupName2"), 2, 2);
		final GroupOverviewWsDTO group2 = prepareOverviewGroup("GroupName2", null, 2, 2);
		final List<GroupOverviewWsDTO> groups = new ArrayList<>(2);
		groups.add(group1);
		groups.add(group2);
		configuration.setGroups(groups);

		try (MockedStatic mocked = mockStatic(Config.class))
		{
			mocked.when(() -> Config.getBoolean("toggle.sapproductconfigservices.exposeProductConfigMessages.enabled", false))
					.thenReturn(true);
			classUnderTest.processConfigurationOverview(configuration);
		}

		final List<ProductConfigMessageWsDTO> messagesProcessed = configuration.getMessages();
		assertEquals(2, messagesProcessed.size());
		for (int i = 0; i < 2; i++)
		{
			final ProductConfigMessageWsDTO messageProcessed = messagesProcessed.get(i);
			assertEquals("Message Configuration " + i, messageProcessed.getMessage());
			assertEquals("Extended Message Configuration " + i, messageProcessed.getExtendedMessage());
		}

		verifyOverviewGroup(configuration.getGroups().get(0), 2, "GroupName1", 2, 2);
		verifyOverviewGroup(configuration.getGroups().get(1), 0, "GroupName2", 2, 2);
	}

	@Test
	public void testProcessValueMessagesNotSupported()
	{
		final CsticValueWsDTO value = prepareValue("CsticName", "ValueName", 2);
		try (MockedStatic mocked = mockStatic(Config.class))
		{
			mocked.when(() -> Config.getBoolean("toggle.sapproductconfigservices.exposeProductConfigMessages.enabled", false))
					.thenReturn(false);
			classUnderTest.processValue(value);
		}
		verifyValue(value, "CsticName", "ValueName", 0);
	}

	@Test
	public void testProcessAttributeMessagesNotSupported()
	{
		final CsticWsDTO attribute = prepareAttribute("CsticName", 2, 2);
		try (MockedStatic mocked = mockStatic(Config.class))
		{
			mocked.when(() -> Config.getBoolean("toggle.sapproductconfigservices.exposeProductConfigMessages.enabled", false))
					.thenReturn(false);
			classUnderTest.processAttribute(attribute);
		}
		verifyAttribute(attribute, "CsticName", 2, 0);
	}

	@Test
	public void testProcessConfigurationMessagesNotSupported()
	{
		final ConfigurationWsDTO configuration = new ConfigurationWsDTO();

		final List<ProductConfigMessageWsDTO> messages = new ArrayList(2);
		for (int i = 0; i < 2; i++)
		{
			final ProductConfigMessageWsDTO message = new ProductConfigMessageWsDTO();
			message.setMessage("Message" + SPACE + "Configuration" + SPACE + i);
			message.setExtendedMessage("Extended" + SPACE + "Message" + SPACE + "Configuration" + SPACE + i);
			messages.add(message);
		}
		configuration.setMessages(messages);
		configuration.setGroups(new ArrayList<GroupWsDTO>(0));

		try (MockedStatic mocked = mockStatic(Config.class))
		{
			mocked.when(() -> Config.getBoolean("toggle.sapproductconfigservices.exposeProductConfigMessages.enabled", false))
					.thenReturn(false);
			classUnderTest.processConfiguration(configuration);
		}
		assertEquals(0, configuration.getMessages().size());

	}

	@Test
	public void testProcessOverviewCharacteristicValueMessagesNotSupported()
	{
		final OverviewCsticValueWsDTO value = prepareOverviewCharacteristicValue("CsticName", "ValueName", 2);
		try (MockedStatic mocked = mockStatic(Config.class))
		{
			mocked.when(() -> Config.getBoolean("toggle.sapproductconfigservices.exposeProductConfigMessages.enabled", false))
					.thenReturn(false);
			classUnderTest.processOverviewCharacteristicValue(value);
		}
		verifyOverviewCharacteristicValue(value, "CsticName", "ValueName", 0);
	}

	@Test
	public void testProcessConfigurationOverviewMessagesNotSupported()
	{
		final ConfigurationOverviewWsDTO configuration = new ConfigurationOverviewWsDTO();

		final List<ProductConfigMessageWsDTO> messages = new ArrayList(2);
		for (int i = 0; i < 2; i++)
		{
			final ProductConfigMessageWsDTO message = new ProductConfigMessageWsDTO();
			message.setMessage("Message" + SPACE + "Configuration" + SPACE + i);
			message.setExtendedMessage("Extended" + SPACE + "Message" + SPACE + "Configuration" + SPACE + i);
			messages.add(message);
		}
		configuration.setMessages(messages);
		configuration.setGroups(new ArrayList<GroupOverviewWsDTO>(0));

		try (MockedStatic mocked = mockStatic(Config.class))
		{
			mocked.when(() -> Config.getBoolean("toggle.sapproductconfigservices.exposeProductConfigMessages.enabled", false))
					.thenReturn(false);
			classUnderTest.processConfigurationOverview(configuration);
		}
		assertEquals(0, configuration.getMessages().size());
	}

	protected CsticValueWsDTO prepareValue(final String csticName, final String valueName, final int numberOfMessages)
	{
		final CsticValueWsDTO value = new CsticValueWsDTO();
		final List<ProductConfigMessageWsDTO> messages = new ArrayList(numberOfMessages);
		for (int i = 0; i < numberOfMessages; i++)
		{
			final ProductConfigMessageWsDTO message = new ProductConfigMessageWsDTO();
			message.setMessage("Message" + SPACE + csticName + SPACE + valueName + SPACE + i);
			message.setExtendedMessage("Extended" + SPACE + "Message" + SPACE + csticName + SPACE + valueName + SPACE + i);
			messages.add(message);
		}
		value.setMessages(messages);
		return value;
	}

	protected void verifyValue(final CsticValueWsDTO value, final String csticName, final String valueName,
			final int numberOfMessages)
	{
		final List<ProductConfigMessageWsDTO> messages = value.getMessages();
		assertEquals(numberOfMessages, messages.size());
		for (int i = 0; i < numberOfMessages; i++)
		{
			final ProductConfigMessageWsDTO message = messages.get(i);
			assertEquals("Message " + csticName + " " + valueName + " " + i, message.getMessage());
			assertEquals("Extended Message " + csticName + " " + valueName + " " + i, message.getExtendedMessage());
		}
	}	
	
	protected CsticWsDTO prepareAttribute(final String csticName, final int numberOfValues, final int numberOfMessages)
	{
		final CsticWsDTO attribute = new CsticWsDTO();
		final List<CsticValueWsDTO> values = new ArrayList(numberOfValues);
		for (int i = 0; i < numberOfValues; i++)
		{
			final CsticValueWsDTO value = prepareValue(csticName, "Value" + i, numberOfMessages);
			values.add(value);
		}
		attribute.setDomainValues(values);

		final List<ProductConfigMessageWsDTO> messages = new ArrayList(numberOfMessages);
		for (int i = 0; i < numberOfMessages; i++)
		{
			final ProductConfigMessageWsDTO message = new ProductConfigMessageWsDTO();
			message.setMessage("Message" + SPACE + csticName + SPACE + i);
			message.setExtendedMessage("Extended" + SPACE + "Message" + SPACE + csticName + SPACE + i);
			messages.add(message);
		}
		attribute.setMessages(messages);

		return attribute;
	}

	protected void verifyAttribute(final CsticWsDTO attribute, final String csticName, final int numberOfValues,
			final int numberOfMessages)
	{
		final List<CsticValueWsDTO> values = attribute.getDomainValues();
		assertEquals(numberOfValues, values.size());
		for (int i = 0; i < numberOfValues; i++)
		{
			final CsticValueWsDTO value = values.get(i);
			verifyValue(value, csticName, "Value" + i, numberOfMessages);
		}
		final List<ProductConfigMessageWsDTO> messages = attribute.getMessages();
		assertEquals(numberOfMessages, messages.size());
		for (int i = 0; i < numberOfMessages; i++)
		{
			final ProductConfigMessageWsDTO message = messages.get(i);
			assertEquals("Message " + csticName + " " + i, message.getMessage());
			assertEquals("Extended Message " + csticName + " " + i, message.getExtendedMessage());
		}
	}

	protected GroupWsDTO prepareGroup(final String groupName, final List<String> subGroupNames, final int numberOfCstics,
			final int numberOfValues,
			final int numberOfMessages)
	{
		final GroupWsDTO group = new GroupWsDTO();
		group.setName(groupName);
		final List<CsticWsDTO> attributes = new ArrayList(numberOfCstics);
		for (int i = 0; i < numberOfCstics; i++)
		{
			final CsticWsDTO attribute = prepareAttribute(groupName + "Attribute" + i, numberOfValues, numberOfMessages);
			attributes.add(attribute);
		}
		group.setAttributes(attributes);

		if (subGroupNames != null)
		{
			final List<GroupWsDTO> subGroups = new ArrayList<>(subGroupNames.size());
			for (final String subGroupName : subGroupNames)
			{
				final GroupWsDTO subGroup = prepareGroup(subGroupName, null, 2, 2, 2);
				subGroups.add(subGroup);
			}
			group.setSubGroups(subGroups);
		}
		return group;
	}

	protected void verifyGroup(final GroupWsDTO group, final int expectedNumberOfSubGroups, final String groupName,
			final int numberOfCstics, final int numberOfValues,
			final int numberOfMessages)
	{
		final List<CsticWsDTO> attributes = group.getAttributes();
		assertEquals(numberOfCstics, attributes.size());
		for (int i = 0; i < numberOfCstics; i++)
		{
			final CsticWsDTO attribute = attributes.get(i);
			verifyAttribute(attribute, groupName + "Attribute" + i, numberOfValues, numberOfMessages);
		}
		// Verify subGroups
		if (expectedNumberOfSubGroups == 0)
		{
			assertNull(group.getSubGroups());
		}
		else
		{
			assertEquals(expectedNumberOfSubGroups, group.getSubGroups().size());
			for (final GroupWsDTO subGroup : group.getSubGroups())
			{
				verifyGroup(subGroup, 0, subGroup.getName(), numberOfCstics, numberOfValues, numberOfMessages);
			}
		}
	}

	protected OverviewCsticValueWsDTO prepareOverviewCharacteristicValue(final String csticName, final String valueName,
			final int numberOfMessages)
	{
		final OverviewCsticValueWsDTO value = new OverviewCsticValueWsDTO();
		final List<ProductConfigMessageWsDTO> messages = new ArrayList(numberOfMessages);
		for (int i = 0; i < numberOfMessages; i++)
		{
			final ProductConfigMessageWsDTO message = new ProductConfigMessageWsDTO();
			message.setMessage("Message" + SPACE + csticName + SPACE + valueName + SPACE + i);
			message.setExtendedMessage("Extended" + SPACE + "Message" + SPACE + csticName + SPACE + valueName + SPACE + i);
			messages.add(message);
		}
		value.setMessages(messages);
		return value;
	}

	protected void verifyOverviewCharacteristicValue(final OverviewCsticValueWsDTO value, final String csticName,
			final String valueName, final int numberOfMessages)
	{
		final List<ProductConfigMessageWsDTO> messages = value.getMessages();
		assertEquals(numberOfMessages, messages.size());
		for (int i = 0; i < numberOfMessages; i++)
		{
			final ProductConfigMessageWsDTO message = messages.get(i);
			assertEquals("Message " + csticName + " " + valueName + " " + i, message.getMessage());
			assertEquals("Extended Message " + csticName + " " + valueName + " " + i, message.getExtendedMessage());
		}
	}

	protected GroupOverviewWsDTO prepareOverviewGroup(final String groupName, final List<String> subGroupNames,
			final int numberOfCsticValues, final int numberOfMessages)
	{
		final GroupOverviewWsDTO group = new GroupOverviewWsDTO();
		group.setGroupDescription(groupName);
		final List<OverviewCsticValueWsDTO> csticValues = new ArrayList(numberOfCsticValues);
		for (int i = 0; i < numberOfCsticValues; i++)
		{
			final OverviewCsticValueWsDTO csticValue = prepareOverviewCharacteristicValue(groupName + "Characteristic" + i,
					"Value" + i, numberOfMessages);
			csticValues.add(csticValue);
		}
		group.setCharacteristicValues(csticValues);

		if (subGroupNames != null)
		{
			final List<GroupOverviewWsDTO> subGroups = new ArrayList<>(subGroupNames.size());
			for (final String subGroupName : subGroupNames)
			{
				final GroupOverviewWsDTO subGroup = prepareOverviewGroup(subGroupName, null, 2, 2);
				subGroups.add(subGroup);
			}
			group.setSubGroups(subGroups);
		}
		return group;
	}

	protected void verifyOverviewGroup(final GroupOverviewWsDTO group, final int expectedNumberOfSubGroups, final String groupName,
			final int numberOfCsticValues, final int numberOfMessages)
	{
		final List<OverviewCsticValueWsDTO> csticValues = group.getCharacteristicValues();
		assertEquals(numberOfCsticValues, csticValues.size());
		for (int i = 0; i < numberOfCsticValues; i++)
		{
			final OverviewCsticValueWsDTO csticValue = csticValues.get(i);
			verifyOverviewCharacteristicValue(csticValue, groupName + "Characteristic" + i, "Value" + i, numberOfMessages);
		}
		// Verify subGroups
		if (expectedNumberOfSubGroups == 0)
		{
			assertNull(group.getSubGroups());
		}
		else
		{
			assertEquals(expectedNumberOfSubGroups, group.getSubGroups().size());
			for (final GroupOverviewWsDTO subGroup : group.getSubGroups())
			{
				verifyOverviewGroup(subGroup, 0, subGroup.getGroupDescription(), numberOfCsticValues, numberOfMessages);
			}
		}
	}

}
