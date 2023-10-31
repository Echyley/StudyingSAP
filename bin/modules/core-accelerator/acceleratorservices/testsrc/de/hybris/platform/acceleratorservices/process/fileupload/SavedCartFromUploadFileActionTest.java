/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.process.fileupload;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.model.process.SavedCartFileUploadProcessModel;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commerceservices.impersonation.ImpersonationContext;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.UserModel;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SavedCartFromUploadFileActionTest
{
	private SavedCartFromUploadFileAction savedCartFromUploadFileAction;

	@Mock
	private SavedCartFileUploadProcessModel savedCartFileUploadProcessModel;
	@Mock
	private CurrencyModel currencyModel;
	@Mock
	private LanguageModel languageModel;
	@Mock
	private UserModel userModel;
	@Mock
	private CMSSiteModel baseSiteModel;
	@Mock
	private CartModel savedCartModel;
	@Mock
	private CatalogModel catalogModel;
	@Mock
	private CatalogVersionModel catalogVersionModel;

	@Before
	public void setUp() throws Exception
	{
		savedCartFromUploadFileAction = Mockito.spy(new SavedCartFromUploadFileAction());
	}

	@Test
	public void setSavedCartFromUploadFileActionCreateImpersonationContext()
	{
		Mockito.when(savedCartFileUploadProcessModel.getCurrency()).thenReturn(currencyModel);
		Mockito.when(savedCartFileUploadProcessModel.getLanguage()).thenReturn(languageModel);
		Mockito.when(savedCartFileUploadProcessModel.getUser()).thenReturn(userModel);
		Mockito.when(savedCartFileUploadProcessModel.getSite()).thenReturn(baseSiteModel);
		Mockito.when(savedCartFileUploadProcessModel.getSavedCart()).thenReturn(savedCartModel);

		Mockito.when(baseSiteModel.getDefaultCatalog()).thenReturn(catalogModel);
		Mockito.when(catalogModel.getActiveCatalogVersion()).thenReturn(catalogVersionModel);

		ImpersonationContext impersonationContext = savedCartFromUploadFileAction.createImpersonationContext(savedCartFileUploadProcessModel);
		Assert.assertEquals(currencyModel, impersonationContext.getCurrency());
		Assert.assertEquals(languageModel, impersonationContext.getLanguage());
		Assert.assertEquals(userModel, impersonationContext.getUser());
		Assert.assertEquals(savedCartModel, impersonationContext.getOrder());
		Assert.assertEquals(Collections.singletonList(catalogVersionModel), impersonationContext.getCatalogVersions());
	}
}
