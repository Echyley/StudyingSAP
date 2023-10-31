/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cms2.cloning.service.impl;

import static de.hybris.platform.cms2.cloning.service.impl.DefaultCMSItemDeepCloningService.ATTRIBUTE_NAME_MAX_LENGTH;
import static de.hybris.platform.cms2.cloning.service.impl.DefaultCMSItemDeepCloningService.ATTRIBUTE_NAME_PREFIX_KEY;
import static de.hybris.platform.cms2.cloning.service.impl.DefaultCMSItemDeepCloningService.ATTRIBUTE_NAME_SEPARATOR_KEY;
import static de.hybris.platform.cms2.cloning.service.impl.DefaultCMSItemDeepCloningService.ATTRIBUTE_UID_MAX_LENGTH;
import static de.hybris.platform.cms2.cloning.service.impl.DefaultCMSItemDeepCloningService.ATTRIBUTE_UID_PREFIX_KEY;
import static de.hybris.platform.cms2.cloning.service.impl.DefaultCMSItemDeepCloningService.ATTRIBUTE_UID_SEPARATOR_KEY;
import static de.hybris.platform.cms2.cloning.service.impl.DefaultCMSItemDeepCloningService.DEFAULT_ATTRIBUTE_NAME_MAX_LENGTH;
import static de.hybris.platform.cms2.cloning.service.impl.DefaultCMSItemDeepCloningService.DEFAULT_ATTRIBUTE_NAME_PREFIX;
import static de.hybris.platform.cms2.cloning.service.impl.DefaultCMSItemDeepCloningService.DEFAULT_ATTRIBUTE_NAME_SEPARATOR;
import static de.hybris.platform.cms2.cloning.service.impl.DefaultCMSItemDeepCloningService.DEFAULT_ATTRIBUTE_UID_MAX_LENGTH;
import static de.hybris.platform.cms2.cloning.service.impl.DefaultCMSItemDeepCloningService.DEFAULT_ATTRIBUTE_UID_PREFIX;
import static de.hybris.platform.cms2.cloning.service.impl.DefaultCMSItemDeepCloningService.DEFAULT_ATTRIBUTE_UID_SEPARATOR;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.keygenerator.impl.PersistentKeyGenerator;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class DefaultCMSItemDeepCloningServiceTest
{
	private static final Object GENERATED_UID = "1122";
	private static final String BASE_NAME = "some_name";

	@Mock
	private PersistentKeyGenerator cloneUidGenerator;
	@Mock
	private ConfigurationService configurationService;
	@Mock
	private Configuration configuration;

	@InjectMocks
	@Spy
	private DefaultCMSItemDeepCloningService itemDeepCloningService;

	@Before
	public void setUp()
	{
		when(cloneUidGenerator.generate()).thenReturn(GENERATED_UID);
		when(configurationService.getConfiguration()).thenReturn(configuration);
	}

	@Test
	public void generateCloneUid()
	{
		// GIVEN
		when(configuration.getString(ATTRIBUTE_UID_PREFIX_KEY, DEFAULT_ATTRIBUTE_UID_PREFIX)).thenReturn(DEFAULT_ATTRIBUTE_UID_PREFIX);

		// WHEN
		final String newUid = itemDeepCloningService.generateCloneItemUid();

		// THEN
		assertThat(newUid, equalTo(DEFAULT_ATTRIBUTE_UID_PREFIX + GENERATED_UID));
	}

	@Test
	public void givenComponentName_WhenGenerateCloneComponentNameIsCalled_ThenItReturnsANameWithTheNewPostfix()
	{
		// GIVEN
		when(configuration.getString(ATTRIBUTE_NAME_PREFIX_KEY, DEFAULT_ATTRIBUTE_NAME_PREFIX)).thenReturn(DEFAULT_ATTRIBUTE_NAME_PREFIX);
		when(configuration.getString(ATTRIBUTE_NAME_SEPARATOR_KEY, DEFAULT_ATTRIBUTE_NAME_SEPARATOR)).thenReturn(DEFAULT_ATTRIBUTE_NAME_SEPARATOR);
		when(configuration.getInt(ATTRIBUTE_NAME_MAX_LENGTH, DEFAULT_ATTRIBUTE_NAME_MAX_LENGTH)).thenReturn(DEFAULT_ATTRIBUTE_NAME_MAX_LENGTH);

		// WHEN
		final String result = itemDeepCloningService.generateCloneComponentName(BASE_NAME);

		// THEN
		final String expectedResult = DEFAULT_ATTRIBUTE_NAME_PREFIX + DEFAULT_ATTRIBUTE_NAME_SEPARATOR +  BASE_NAME + DEFAULT_ATTRIBUTE_NAME_SEPARATOR + GENERATED_UID;
		assertThat(result, equalTo(expectedResult));
	}

	@Test
	public void givenComponentNameWithClonePrefix_WhenGenerateCloneComponentNameIsCalled_ThenItReturnsANameOnlyWithANewPostfix()
	{
		// GIVEN
		when(configuration.getString(ATTRIBUTE_NAME_PREFIX_KEY, DEFAULT_ATTRIBUTE_NAME_PREFIX)).thenReturn(DEFAULT_ATTRIBUTE_NAME_PREFIX);
		when(configuration.getString(ATTRIBUTE_NAME_SEPARATOR_KEY, DEFAULT_ATTRIBUTE_NAME_SEPARATOR)).thenReturn(DEFAULT_ATTRIBUTE_NAME_SEPARATOR);
		when(configuration.getInt(ATTRIBUTE_NAME_MAX_LENGTH, DEFAULT_ATTRIBUTE_NAME_MAX_LENGTH)).thenReturn(DEFAULT_ATTRIBUTE_NAME_MAX_LENGTH);
		final String otherUid = "54321";
		final String originalName = BASE_NAME + " " + otherUid;

		// WHEN
		final String result = itemDeepCloningService.generateCloneComponentName(originalName);

		// THEN
		final String expectedResult = DEFAULT_ATTRIBUTE_NAME_PREFIX + DEFAULT_ATTRIBUTE_NAME_SEPARATOR + originalName + DEFAULT_ATTRIBUTE_NAME_SEPARATOR + GENERATED_UID;
		assertThat(result, equalTo(expectedResult));
	}

	@Test
	public void shouldIgnoreAttributeMaxLengthIfItIsLessThanMinimalSizeForAttributeValue()
	{
		// GIVEN
		when(configuration.getString(ATTRIBUTE_NAME_PREFIX_KEY, DEFAULT_ATTRIBUTE_NAME_PREFIX)).thenReturn(DEFAULT_ATTRIBUTE_NAME_PREFIX);
		when(configuration.getString(ATTRIBUTE_NAME_SEPARATOR_KEY, DEFAULT_ATTRIBUTE_NAME_SEPARATOR)).thenReturn(DEFAULT_ATTRIBUTE_NAME_SEPARATOR);
		when(configuration.getInt(ATTRIBUTE_NAME_MAX_LENGTH, DEFAULT_ATTRIBUTE_NAME_MAX_LENGTH)).thenReturn(5);

		// WHEN
		final String result = itemDeepCloningService.generateCloneComponentName(BASE_NAME);

		// THEN
		final String expectedResult = DEFAULT_ATTRIBUTE_NAME_PREFIX + DEFAULT_ATTRIBUTE_NAME_SEPARATOR +  BASE_NAME + DEFAULT_ATTRIBUTE_NAME_SEPARATOR + GENERATED_UID;
		assertThat(result, equalTo(expectedResult));
	}

	@Test
	public void shouldNotGeneratePrefixIfItAlreadyExists()
	{
		// GIVEN
		when(configuration.getString(ATTRIBUTE_NAME_PREFIX_KEY, DEFAULT_ATTRIBUTE_NAME_PREFIX)).thenReturn(DEFAULT_ATTRIBUTE_NAME_PREFIX);
		when(configuration.getString(ATTRIBUTE_NAME_SEPARATOR_KEY, DEFAULT_ATTRIBUTE_NAME_SEPARATOR)).thenReturn(DEFAULT_ATTRIBUTE_NAME_SEPARATOR);
		when(configuration.getInt(ATTRIBUTE_NAME_MAX_LENGTH, DEFAULT_ATTRIBUTE_NAME_MAX_LENGTH)).thenReturn(5);
		final String originalName = DEFAULT_ATTRIBUTE_NAME_PREFIX + DEFAULT_ATTRIBUTE_NAME_SEPARATOR + BASE_NAME;

		// WHEN
		final String result = itemDeepCloningService.generateCloneComponentName(originalName);

		// THEN
		final String expectedResult = DEFAULT_ATTRIBUTE_NAME_PREFIX + DEFAULT_ATTRIBUTE_NAME_SEPARATOR +  BASE_NAME + DEFAULT_ATTRIBUTE_NAME_SEPARATOR + GENERATED_UID;
		assertThat(result, equalTo(expectedResult));
	}

	@Test
	public void givenVeryLongOriginalUid_WhenGenerateCloneUidWithOriginalUid_ThenReturnsNewUidWithCroppedOriginalValue()
	{
		//GIVEN
		when(configuration.getString(ATTRIBUTE_UID_PREFIX_KEY, DEFAULT_ATTRIBUTE_UID_PREFIX)).thenReturn(DEFAULT_ATTRIBUTE_UID_PREFIX);
		when(configuration.getString(ATTRIBUTE_UID_SEPARATOR_KEY, DEFAULT_ATTRIBUTE_UID_SEPARATOR)).thenReturn(DEFAULT_ATTRIBUTE_UID_SEPARATOR);
		when(configuration.getInt(ATTRIBUTE_UID_MAX_LENGTH, DEFAULT_ATTRIBUTE_UID_MAX_LENGTH)).thenReturn(DEFAULT_ATTRIBUTE_UID_MAX_LENGTH);

		final String originalUid = "very-loooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooonnnnnnnnng-ID";

		// WHEN
		final String newUid = itemDeepCloningService.generateCloneItemUid(originalUid);

		// THEN
		assertThat(newUid, equalTo(DEFAULT_ATTRIBUTE_UID_PREFIX + DEFAULT_ATTRIBUTE_UID_SEPARATOR //
				+ "very-loooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo" //
				+ DEFAULT_ATTRIBUTE_UID_SEPARATOR //
				+ GENERATED_UID));
	}
}
