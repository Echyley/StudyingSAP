/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.samlsinglesignon.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.Registry;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.saml2.Saml2Exception;
import org.springframework.security.saml2.core.OpenSamlInitializationService;

@UnitTest
public class MetadataUtilTest
{
	private static final String METADATA_LOCATION = "classpath:samlsinglesignon/test/testmetadata.xml";
	private static final String BROKEN_METADATA_LOCATION = "classpath:samlsinglesignon/test/invalidtestmetadata.xml";
	private static final String METADATA_INCORRECT_LOCATION = "samlsinglesignon/incorrect/testmetadata.xml";

	private static final String ENTITY_ID = "test.identityProvider.com";
	private static final String ID = "c000bd2a-1f36-4a1a-a342-059e25c54d99";

	@Before
	public void setUp()
	{
		OpenSamlInitializationService.initialize();
	}

	private final ResourceLoader resourceLoader = new DefaultResourceLoader();

	@Test
	public void shouldUnmarshallMetadata() throws IOException
	{
		//when
		final XMLObject xmlIdpMetadata = MetadataUtil.unmarshallMetadata(getFileForResource(METADATA_LOCATION));

		//then
		assertThat(xmlIdpMetadata).isNotNull().isInstanceOf(EntityDescriptor.class);
		assertThat(((EntityDescriptor) xmlIdpMetadata).getEntityID()).isEqualTo(ENTITY_ID);
		assertThat(((EntityDescriptor) xmlIdpMetadata).getID()).isEqualTo(ID);
		assertThat(((EntityDescriptor) xmlIdpMetadata).isSigned()).isTrue();
	}

	@Test
	public void shouldThrowExceptionWhenProvidingBrokenMetadata() throws IOException
	{
		//when, then
		final File brokenMetadataLocation = getFileForResource(BROKEN_METADATA_LOCATION);
		assertThatThrownBy(() -> MetadataUtil.unmarshallMetadata(brokenMetadataLocation))
				.isInstanceOf(Saml2Exception.class)
				.hasMessageStartingWith("Cannot unmarshall metadata")
				.hasMessageContaining(BROKEN_METADATA_LOCATION.replace("classpath:",""));
	}

	@Test
	public void shouldThrowExceptionWhenProvidingIncorrectMetadataLocation()
	{
		//when, then
		final File incorrectMetadataLocation = new File(METADATA_INCORRECT_LOCATION);
		assertThatThrownBy(() -> MetadataUtil.unmarshallMetadata(incorrectMetadataLocation))
				.isInstanceOf(Saml2Exception.class)
				.hasMessageStartingWith("Cannot unmarshall metadata")
				.hasMessageContaining(METADATA_INCORRECT_LOCATION);

	}

	private File getFileForResource(final String location) throws IOException
	{
		return resourceLoader.getResource(location).getFile();
	}
}