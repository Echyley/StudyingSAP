/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.y2ysync.util;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import de.hybris.bootstrap.annotations.UnitTest;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SerializationException;
import org.junit.Test;

/**
 * Tests for {@link Y2YSyncSerializationUtils}
 */
@UnitTest
public class Y2YSyncSerializationUtilsTest
{

	@Test
	public void shouldThrowIllegalArgumentExceptionWhenInputIsNull()
	{
		assertThatThrownBy(() -> Y2YSyncSerializationUtils.deserialize(null))
				.as("expected IllegalArgumentException but not thrown")
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("The data to be deserialized must not be null");
	}

	@Test
	public void shouldThrowSerializationExceptionForWrongData()
	{
		final byte[] data = IOUtils.byteArray();
		assertThatThrownBy(() -> Y2YSyncSerializationUtils.deserialize(data))
				.as("expected SerializationException but not thrown")
				.isInstanceOf(SerializationException.class)
				.hasMessage("Could not deserialize given media as List of ItemChangeDTO");
	}
}
