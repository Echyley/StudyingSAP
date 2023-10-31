/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.labelproviders;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.order.EntryGroup;

import org.junit.Test;

@UnitTest
public class EntryGroupLabelProviderTest
{
	private final EntryGroupLabelProvider labelProvider = new EntryGroupLabelProvider();

	@Test
	public void shouldSurviveNullGroup()
	{
		assertEquals("", labelProvider.getLabel(null));
	}

	@Test
	public void shouldAddGroupLabel()
	{
		final EntryGroup group = new EntryGroup();
		group.setLabel("group-label");
		group.setGroupNumber(Integer.valueOf(1));
		assertThat(labelProvider.getLabel(group), containsString("group-label"));
	}

	@Test
	public void shouldSurviveNullGroupNumber()
	{
		final EntryGroup group = new EntryGroup();
		group.setLabel("group-label");
		final String label = labelProvider.getLabel(group);
		assertThat(label, not(containsString("null")));
		assertThat(label, containsString("group-label"));
	}

	@Test
	public void shouldSurviveNullLabel()
	{
		final EntryGroup group = new EntryGroup();
		group.setGroupNumber(Integer.valueOf(8));
		final String label = labelProvider.getLabel(group);
		assertThat(label, not(containsString("null")));
		assertThat(label, containsString("8"));
	}

	@Test
	public void shouldUseRefIdWhenLabelIsEmpty()
	{
		final EntryGroup group = new EntryGroup();
		group.setGroupNumber(Integer.valueOf(1));
		group.setLabel(" ");
		group.setExternalReferenceId("REF");
		assertThat(labelProvider.getLabel(group), containsString("REF"));
	}

	@Test
	public void shouldIgnoreRefIfLabelIsNotBlank()
	{
		final EntryGroup group = new EntryGroup();
		group.setGroupNumber(Integer.valueOf(1));
		group.setLabel("LABEL");
		group.setExternalReferenceId("REF");
		final String label = labelProvider.getLabel(group);
		assertThat(label, containsString("LABEL"));
		assertThat(label, not(containsString("REF")));
	}

	@Test
	public void shouldAddGroupNumber()
	{
		final EntryGroup group = new EntryGroup();
		group.setLabel("group-label");
		group.setGroupNumber(Integer.valueOf(99));
		final String label = labelProvider.getLabel(group);
		assertThat(label, containsString("99"));
		assertThat(label, containsString("group-label"));
	}

	@Test
	public void shouldReturnADescription()
	{
		assertNotNull(labelProvider.getDescription(new EntryGroup()));
	}
}
