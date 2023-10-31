/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.classification.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.type.TypeService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.hybris.cockpitng.core.config.CockpitConfigurationFallbackStrategy;
import com.hybris.cockpitng.core.config.impl.DefaultConfigContext;
import com.hybris.cockpitng.core.config.impl.jaxb.editorarea.EditorArea;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.dataaccess.facades.type.TypeFacade;
import com.hybris.cockpitng.dataaccess.facades.type.exceptions.TypeNotFoundException;


public class ClassificationAwareEditorAreaConfigFallbackStrategyTest
{

	public static final String NON_PLATFORM_TYPE = "IDoNotExist";

	@InjectMocks
	private final CockpitConfigurationFallbackStrategy<EditorArea> strategy = new ClassificationAwareEditorAreaConfigFallbackStrategy();

	@Mock
	private TypeService typeService;

	@Mock
	private TypeFacade typeFacade;

	@Mock
	private DataType customType;

	@Before
	public void setUp() throws TypeNotFoundException
	{
		initMocks(this);
		when(typeService.getTypeForCode(NON_PLATFORM_TYPE)).thenThrow(UnknownIdentifierException.class);
		when(typeFacade.load(NON_PLATFORM_TYPE)).thenReturn(customType);
	}

	@Test
	public void loadFallbackConfiguration()
	{
		final DefaultConfigContext context = new DefaultConfigContext();
		context.addAttribute(DefaultConfigContext.CONTEXT_TYPE, NON_PLATFORM_TYPE);

		final EditorArea configuration = strategy.loadFallbackConfiguration(context, EditorArea.class);

		assertThat(configuration).isNotNull();

	}

}
