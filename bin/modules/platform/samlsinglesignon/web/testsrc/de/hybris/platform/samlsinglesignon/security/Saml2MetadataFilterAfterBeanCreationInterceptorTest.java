/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.samlsinglesignon.security;

import static de.hybris.platform.samlsinglesignon.constants.SamlsinglesignonConstants.SSO_LEGACY_ENDPOINTS_ENABLED;
import static de.hybris.platform.samlsinglesignon.security.Saml2MetadataFilterAfterBeanCreationInterceptor.METADATA_FILENAME_FIELD_NAME;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.converters.impl.AbstractConverter;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.testframework.PropertyConfigSwitcher;

import javax.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.saml2.provider.service.metadata.OpenSamlMetadataResolver;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistration;
import org.springframework.security.saml2.provider.service.web.Saml2MetadataFilter;

@IntegrationTest
public class Saml2MetadataFilterAfterBeanCreationInterceptorTest extends ServicelayerBaseTest
{
	private final PropertyConfigSwitcher legacyEndpoints = new PropertyConfigSwitcher(SSO_LEGACY_ENDPOINTS_ENABLED);
	private Saml2MetadataFilterAfterBeanCreationInterceptor creationInterceptor;
	private Saml2MetadataFilter saml2MetadataFilter;

	@Before
	public void setUp()
	{
		saml2MetadataFilter = createFilter();
		creationInterceptor = new Saml2MetadataFilterAfterBeanCreationInterceptor(saml2MetadataFilter);
	}

	@After
	public void tearDown()
	{
		legacyEndpoints.switchBackToDefault();
	}

	@Test
	public void shouldSetLegacyMetadataFileNameAfterPropertiesSetWhenLegacyEndpointsEnabled()
			throws NoSuchFieldException, IllegalAccessException
	{
		legacyEndpoints.switchToValue("true");
		creationInterceptor.afterPropertiesSet();

		assertThat(metadataFileName()).isEqualTo("spring_saml_metadata.xml");
	}

	@Test
	public void shouldSetDefaultMetadataFileNameAfterPropertiesSetWhenLegacyEndpointsDisabled()
			throws NoSuchFieldException, IllegalAccessException
	{
		legacyEndpoints.switchToValue("false");
		creationInterceptor.afterPropertiesSet();

		assertThat(metadataFileName()).isEqualTo("saml-{registrationId}-metadata.xml");
	}

	@Test
	public void shouldSetCustomMetadataFileNameAfterPropertiesSetWhenLegacyEndpointsDisabled()
			throws NoSuchFieldException, IllegalAccessException
	{
		legacyEndpoints.switchToValue("false");
		saml2MetadataFilter.setMetadataFilename("my_custom_name{registrationId}.xml");
		creationInterceptor.afterPropertiesSet();

		assertThat(metadataFileName()).isEqualTo("my_custom_name{registrationId}.xml");
	}

	@Test
	public void shouldNotSetCustomMetadataFileNameAfterPropertiesSetWhenLegacyEndpointsEnabled()
			throws NoSuchFieldException, IllegalAccessException
	{
		legacyEndpoints.switchToValue("true");
		saml2MetadataFilter.setMetadataFilename("my_custom_name{registrationId}.xml");
		creationInterceptor.afterPropertiesSet();

		assertThat(metadataFileName()).isEqualTo("spring_saml_metadata.xml");
	}

	private Saml2MetadataFilter createFilter()
	{
		return new Saml2MetadataFilter(
				new AbstractConverter<>()
				{
					@Override
					public void populate(final HttpServletRequest request,
					                     final RelyingPartyRegistration relyingPartyRegistration)
					{
					}
				}, new OpenSamlMetadataResolver());
	}

	private String metadataFileName() throws NoSuchFieldException, IllegalAccessException
	{
		final var metadataFilename = Saml2MetadataFilter.class.getDeclaredField(METADATA_FILENAME_FIELD_NAME);
		metadataFilename.setAccessible(true);
		return (String) metadataFilename.get(saml2MetadataFilter);
	}
}
