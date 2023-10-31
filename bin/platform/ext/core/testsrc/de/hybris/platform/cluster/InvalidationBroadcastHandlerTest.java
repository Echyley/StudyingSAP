/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.cluster;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cache.AdditionalInvalidationData;
import de.hybris.platform.core.PK;

import java.util.Optional;
import java.util.Random;

import org.junit.Test;

@UnitTest
public class InvalidationBroadcastHandlerTest
{
	private final String[] additionalInvalidationDataStrings =
			{
					"{REL={foo={8796551872713},bar={8796551872713}}}",
					"{REL={_foo={8796551872713},_bar={8796551872713}}}",
					"{REL={foo_={8796551872713},bar_={8796551872713}}}",
					"{REL={__foo={8796551872713},bar__={8796551872713}}}",
					"{REL={_foo_={8796551872713},_ba_r={8796551872713}}}",
					"{REL={_fo_o_={8796551872713},_ba_r_={8796551872713}}}",
					"{REL={___={8796551872713},___={8796551872713}}}",
					"{REL={~__={8796551872713},__~={8796551872713}}}",
					"{REL={~foo={8796551872713},~bar={8796551872713}}}",
					"{REL={_foo={8796551872713},~bar={8796551872713}}}",
					"{REL={_~foo={8796551872713},bar~={8796551872713}}}",
					"{REL={f~oo={8796551872713},b_ar={8796551872713}}}",
					"{REL={f~_o={8796551872713},b_~ar={8796551872713}}}",
					"{REL={~~~={8796551872713},___={8796551872713}}}",
					"{REL={_foo={8796551872713},|~bar={8796551872713}}}",
					"{REL={~foo={8796551872713},_|~bar={8796551872713}}}",
					"{REL={~fo__={8796551872713},bar|~={8796551872713}}}",
					"{REL={~fo_o~={8796551872713},||~bar={8796551872713}}}",
					"{REL={~foo_={8796551872713},||~||bar={8796551872713}}}",
					"{REL={fo|~o={8796551872713},||~||ba_r={8796551872713}}}",
					"{REL={~fo_o={8796551872713},||~|bar={8796551872713}}}",
			};

	private final String[][] foreignKeyNames = new String[][]
			{
					{ "foo", "bar" },
					{ "_foo", "_bar" },
					{ "foo_", "bar_" },
					{ "__foo", "bar__" },
					{ "_foo_", "_ba_r" },
					{ "_fo_o_", "_ba_r_" },
					{ "___", "___" },
					{ "~__", "__~" },
					{ "_foo", "~bar" },
					{ "_~foo", "bar~" },
					{ "f~oo", "b_ar" },
					{ "f~_o", "b_~ar" },
					{ "~~~", "___" },
					{ "_foo", "|~bar" },
					{ "~foo_", "bar|~" },
					{ "fo|~o", "_||~||bar" },
					{ "_foo", "bar." },
					{ "*foo", "b_r" },
					{ "_foo", "bar/" },
					{ "-foo", "bar_" },
					{ "-f-o-o", "b_a~r-" },
					{ "_f.o~*o", "/b|a-r" },
			};

	@Test
	public void shouldCorrectlyEscapeAndUnescapeKeyElementDelimitersFromAdditionalInvalidationData()
	{
		for (final String data : additionalInvalidationDataStrings)
		{
			//when
			final String escapedDataString = InvalidationBroadcastHandler.escapeKeyElement(data);
			final String unescapedDataString = InvalidationBroadcastHandler.unescapeKeyElement(escapedDataString)
			                                                               .get();

			//then
			assertThat(escapedDataString).doesNotContain(InvalidationBroadcastHandler.KEY_ELEMENT_DELIMITER);
			assertThat(unescapedDataString).isEqualTo(data);
		}
	}

	@Test
	public void shouldConvertAdditionalInvalidationDataWithDifferentKeyNamesToStringAndThenParseToObject()
	{
		for (final String[] foreignKeyName : foreignKeyNames)
		{
			//given
			final AdditionalInvalidationData data = givenAdditionalInvalidationData(foreignKeyName[0], foreignKeyName[1]);

			//when
			final String dataString = InvalidationBroadcastHandler.serializeKeyElement(data);
			final AdditionalInvalidationData parsedData = (AdditionalInvalidationData) InvalidationBroadcastHandler.parseKeyElement(
					dataString);

			//then
			assertThat(parsedData).isEqualTo(data);
		}
	}

	@Test
	public void shouldReturnEmptyOptionalWhenUnescapingIncorrectString()
	{
		//given
		final String[] incorrectStrings = { "fo|o", "f|oo", "|foo", "foo|" };

		for (final String incorrectString : incorrectStrings)
		{
			//when
			final Optional<String> unescapedDataString = InvalidationBroadcastHandler.unescapeKeyElement(incorrectString);

			//then
			assertThat(unescapedDataString).isEmpty();
		}
	}

	private AdditionalInvalidationData givenAdditionalInvalidationData(final String foreignKeyName1, final String foreignKeyName2)
	{
		return AdditionalInvalidationData.builder()
		                                 .addForeignKey(foreignKeyName1, givenUniquePK())
		                                 .addForeignKey(foreignKeyName2, givenUniquePK())
		                                 .build();
	}

	private PK givenUniquePK()
	{
		final long randomLong = Math.abs(new Random().nextLong());
		return PK.fromLong(randomLong);
	}
}