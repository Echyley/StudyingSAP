/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.services.handlers;

import com.hybris.cockpitng.labels.LabelService;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.interceptor.impl.MandatoryAttributesValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.eq;

@RunWith(MockitoJUnitRunner.class)
public class MissingMandatoryAttributesExceptionTranslationHandlerTest
{
	private final static String LOCALIZED_MESSAGE = "localized message";
	private final static String ATTR_KEY_ORIGINAL = "code";
	private final static String ATTR_KEY_LOCALIZATION = "Article Number";
	private final static String ITEM_TYPE_ORIGINAL = "product";
	private final static String ITEM_TYPE_LOCALIZATION = "Product";

	@InjectMocks
	@Spy
	private MissingMandatoryAttributesExceptionTranslationHandler handler;
	@Mock
	private LabelService labelService;
	@Mock
	private MandatoryAttributesValidator.MissingMandatoryAttributesException mandatoryAttributesException;
	@Mock
	ItemModel model;
	@Captor
	private ArgumentCaptor<Set> argumentCaptor;

	@Before
	public void setUp()
	{
		doReturn(LOCALIZED_MESSAGE).when(handler).getLocalizedMessage(any(), any(), any());
		when(mandatoryAttributesException.getMissingAttributes()).thenReturn(new HashSet<String>(Arrays.asList(ATTR_KEY_ORIGINAL)));
		when(model.getItemtype()).thenReturn(ITEM_TYPE_ORIGINAL);
		when(mandatoryAttributesException.getModel()).thenReturn(model);
		when(labelService.getObjectLabel(ITEM_TYPE_ORIGINAL + "." + ATTR_KEY_ORIGINAL)).thenReturn(ATTR_KEY_LOCALIZATION);
		when(labelService.getObjectLabel(ITEM_TYPE_ORIGINAL)).thenReturn(ITEM_TYPE_LOCALIZATION);
	}


	@Test
	public void toStringShouldReturnStaticLabel()
	{
		assertThat(handler.toString(new Exception(mandatoryAttributesException))).isSameAs(LOCALIZED_MESSAGE);

		verify(handler).getLocalizedMessage(eq(MissingMandatoryAttributesExceptionTranslationHandler.LABEL_KEY), argumentCaptor.capture(), eq(ITEM_TYPE_LOCALIZATION));
		assertThat(argumentCaptor.getValue()).contains(ATTR_KEY_LOCALIZATION);
	}

	@Test
	public void toStringShouldReturnNothing()
	{
		assertThat(handler.toString(new Exception())).isNull();
	}

}
