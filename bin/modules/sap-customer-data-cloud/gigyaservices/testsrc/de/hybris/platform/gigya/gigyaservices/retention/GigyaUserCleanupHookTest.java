/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyaservices.retention;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.gigya.gigyaservices.api.exception.GigyaApiException;
import de.hybris.platform.gigya.gigyaservices.model.GigyaConfigModel;
import de.hybris.platform.gigya.gigyaservices.service.GigyaService;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;

import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.gigya.socialize.GSResponse;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class GigyaUserCleanupHookTest {

	@InjectMocks
	private final GigyaUserCleanupHook cleanUpHook = new GigyaUserCleanupHook();

	@Mock
	private GigyaService gigyaService;

	@Mock
	private GenericDao<GigyaConfigModel> gigyaConfigGenericDao;

	@Mock
	private CustomerModel gigyaUser;

	@Mock
	private GigyaConfigModel gigyaConfig;

	@Test
	public void testWhenGigyaConfigDoesntExist() {
		Mockito.lenient().when(gigyaConfigGenericDao.find(Mockito.anyMap())).thenReturn(Collections.EMPTY_LIST);

		cleanUpHook.cleanupRelatedObjects(gigyaUser);

		Mockito.verifyZeroInteractions(gigyaService);
	}

	@Test
	public void testWhenGigyaConfigExistsWithDeleteDisabled() {
		Mockito.lenient().when(gigyaConfigGenericDao.find(Mockito.anyMap())).thenReturn(Collections.singletonList(gigyaConfig));
		Mockito.lenient().when(gigyaConfig.getDeleteUser()).thenReturn(Boolean.FALSE);

		cleanUpHook.cleanupRelatedObjects(gigyaUser);

		Mockito.verifyZeroInteractions(gigyaService);
	}

	@Test
	public void testWhenGigyaConfigExistsWithDeleteEnabled() {
		Mockito.lenient().when(gigyaConfigGenericDao.find(Mockito.anyMap())).thenReturn(Collections.singletonList(gigyaConfig));
		Mockito.lenient().when(gigyaConfig.getDeleteUser()).thenReturn(Boolean.TRUE);
		Mockito.lenient().when(gigyaUser.getGyUID()).thenReturn("123");
		Mockito.lenient().when(gigyaUser.getGyApiKey()).thenReturn("123");
		final GSResponse gsResponse = Mockito.mock(GSResponse.class);
		Mockito.lenient().when(gigyaService.callRawGigyaApiWithConfig(Mockito.eq("accounts.deleteAccount"),
				Mockito.eq(Collections.singletonMap("UID", "123")), Mockito.eq(gigyaConfig), Mockito.eq(2),
				Mockito.eq(1))).thenReturn(gsResponse);
		Mockito.lenient().when(gsResponse.getErrorCode()).thenReturn(0);

		cleanUpHook.cleanupRelatedObjects(gigyaUser);

		Mockito.verify(gigyaService).callRawGigyaApiWithConfig(Mockito.eq("accounts.deleteAccount"),
				Mockito.eq(Collections.singletonMap("UID", "123")), Mockito.eq(gigyaConfig), Mockito.eq(2),
				Mockito.eq(1));
	}

	@Test(expected = GigyaApiException.class)
	public void testWhenGigyaConfigExistsWithDeleteEnabledWithException() {
		Mockito.lenient().when(gigyaConfigGenericDao.find(Mockito.anyMap())).thenReturn(Collections.singletonList(gigyaConfig));
		Mockito.lenient().when(gigyaConfig.getDeleteUser()).thenReturn(Boolean.TRUE);
		Mockito.lenient().when(gigyaUser.getGyUID()).thenReturn("123");
		Mockito.lenient().when(gigyaUser.getGyApiKey()).thenReturn("123");
		final GSResponse gsResponse = Mockito.mock(GSResponse.class);
		Mockito.lenient().when(gigyaService.callRawGigyaApiWithConfig(Mockito.eq("accounts.deleteAccount"),
				Mockito.eq(Collections.singletonMap("UID", "123")), Mockito.eq(gigyaConfig), Mockito.eq(2),
				Mockito.eq(1))).thenReturn(gsResponse);
		Mockito.lenient().when(gsResponse.getErrorCode()).thenReturn(1);

		cleanUpHook.cleanupRelatedObjects(gigyaUser);
	}
}
