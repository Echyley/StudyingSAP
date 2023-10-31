/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.interceptor;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.punchout.model.PunchOutCredentialModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.PersistenceOperation;
import de.hybris.platform.servicelayer.model.ModelService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.when;

import de.hybris.platform.core.PK;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PunchOutCredentialPrepareInterceptorTest
{
	private static final String OLD_SHARED_SECRET = "old_shared_secret";
	private static final String NEW_SHARED_SECRET = "new_shared_secret";
	@Mock
	private InterceptorContext ctx;
	@Mock
	private ModelService modelService;

	@InjectMocks
	public PunchOutCredentialPrepareInterceptor prepareInterceptor;

	private PunchOutCredentialModel existingModel;

	private PK pk;

	@Mock
	private PunchOutCredentialModel newModel;


	@Before
	public void setup()
	{
		pk = PK.fromLong(1);
		existingModel = new PunchOutCredentialModel();
		existingModel.setSharedsecret(OLD_SHARED_SECRET);
		doNothing().when(ctx).registerElementFor(newModel, PersistenceOperation.SAVE);
	}

	@Test
	public void testPrepareCredentialSaveWithoutPK()
	{
		when(newModel.getPk()).thenReturn(null);
		prepareInterceptor.onPrepare(newModel, ctx);
		verify(ctx).registerElementFor(newModel, PersistenceOperation.SAVE);
	}

	@Test
	public void testPrepareCredentialSaveWithPK()
	{
		when(newModel.getPk()).thenReturn(pk);
		when(newModel.getSharedsecret()).thenReturn(NEW_SHARED_SECRET);
		when(modelService.get(pk)).thenReturn(existingModel);
		prepareInterceptor.onPrepare(newModel, ctx);
		verify(ctx).registerElementFor(newModel, PersistenceOperation.SAVE);
	}

	@Test
	public void testPrepareCredentialSaveWithoutSharedSecretChange()
	{
		when(newModel.getPk()).thenReturn(pk);
		when(newModel.getSharedsecret()).thenReturn(OLD_SHARED_SECRET);
		when(modelService.get(pk)).thenReturn(existingModel);

		prepareInterceptor.onPrepare(newModel, ctx);
		verify(ctx, never()).registerElementFor(newModel, PersistenceOperation.SAVE);
	}

}
