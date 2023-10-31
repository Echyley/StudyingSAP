/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.audit.provider.internal.resolver.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.audit.provider.internal.resolver.AuditRecordInternalIndex;
import de.hybris.platform.audit.view.impl.AuditEvent;
import de.hybris.platform.core.PK;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.common.collect.Sets;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AuditTypeContextTest
{

	private final PK pk1 = PK.fromLong(1L);

	@Mock
	private AuditRecordInternalIndex<AuditEvent> auditRecordInternalIndex;

	@Mock
	private AuditEvent pk1EventA, pk1EventB, pk2EventA, pk2EventB;

	private final String type = "someType";

	private final String baseType = "someBaseType";


	@Before
	public void setUp()
	{
		when(auditRecordInternalIndex.getRecords(pk1)).thenReturn(Sets.newHashSet(pk1EventA, pk1EventB));
		when(auditRecordInternalIndex.getRecords(baseType)).thenReturn(
				Sets.newHashSet(pk1EventA, pk1EventB, pk2EventA, pk2EventB));
	}

	@Test
	public void getPayloadsPK()
	{
		final AuditTypeContext<AuditEvent> context = new AuditTypeContext<>(auditRecordInternalIndex, type, baseType,
				Sets.newHashSet(), Sets.newHashSet());

		assertThat(context.getPayloads(pk1)).isEqualTo(Sets.newHashSet(pk1EventA, pk1EventB));
	}

	@Test
	public void getPayloadsType()
	{
		final AuditTypeContext<AuditEvent> context = new AuditTypeContext<>(auditRecordInternalIndex, type, baseType,
				Sets.newHashSet(), Sets.newHashSet());

		assertThat(context.getPayloads(baseType)).isEqualTo(Sets.newHashSet(pk1EventA, pk1EventB, pk2EventA, pk2EventB));
	}
}
