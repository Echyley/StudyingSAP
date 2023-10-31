/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.samlsinglesignon.security;

import static de.hybris.platform.mediaweb.assertions.assertj.Assertions.assertThat;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.opensaml.xmlsec.signature.support.SignatureConstants.ALGO_ID_MAC_HMAC_SHA256;
import static org.opensaml.xmlsec.signature.support.SignatureConstants.ALGO_ID_SIGNATURE_DSA;
import static org.opensaml.xmlsec.signature.support.SignatureConstants.ALGO_ID_SIGNATURE_DSA_SHA256;
import static org.opensaml.xmlsec.signature.support.SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA256;
import static org.opensaml.xmlsec.signature.support.SignatureConstants.ALGO_ID_SIGNATURE_RSA;
import static org.opensaml.xmlsec.signature.support.SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.samlsinglesignon.constants.SamlsinglesignonConstants;
import de.hybris.platform.samlsinglesignon.exceptions.InvalidMetadataException;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.testframework.PropertyConfigSwitcher;

import java.io.File;
import java.io.IOException;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.opensaml.saml.metadata.resolver.filter.impl.SignatureValidationFilter;
import org.opensaml.security.x509.X509Support;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.saml2.Saml2Exception;
import org.springframework.security.saml2.core.Saml2X509Credential;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistration;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrationRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@IntegrationTest
public class SamlObjectsFactoryTest extends ServicelayerBaseTest
{
	private static final String METADATA_LOCATION = "classpath:samlsinglesignon/test/testmetadata.xml";
	private static final String REGISTRATION_ID = "testRegistrationId";
	private static final String ENTITY_ID = "test.identityProvider.com";
	private static final String SSO_SERVICE_LOCATION = "https://test.identityProvider.com/saml2/idp/sso/test.identityProvider.com";
	private static final String KEY_PAIR_GENERATOR_ALGORITHM = "DSA";
	private static final Logger LOG = Logger.getLogger(SamlObjectsFactoryTest.class);
	private static final String TEST_ENTITY_ID = "testEntityId";
	private static final List<String> DEFAULT_SIGNING_ALGORITHMS = List.of(ALGO_ID_SIGNATURE_RSA_SHA256,
			ALGO_ID_SIGNATURE_DSA_SHA256, ALGO_ID_SIGNATURE_ECDSA_SHA256, ALGO_ID_MAC_HMAC_SHA256);

	private final PropertyConfigSwitcher relyingPartyRegistrationIdSwitcher = new PropertyConfigSwitcher(
			SamlsinglesignonConstants.SSO_RELYING_PARTY_REGISTRATION_ID);

	private final PropertyConfigSwitcher legacyEndpointsSwitcher = new PropertyConfigSwitcher(
			SamlsinglesignonConstants.SSO_LEGACY_ENDPOINTS_ENABLED);

	private final PropertyConfigSwitcher filterEndpoint = new PropertyConfigSwitcher(
			SamlsinglesignonConstants.SSO_FILTER_PROCESSES_URI);

	private final PropertyConfigSwitcher metadataEndpoint = new PropertyConfigSwitcher(
			SamlsinglesignonConstants.SSO_METADATA_URI);

	private final PropertyConfigSwitcher ssoSigningAlgorithm = new PropertyConfigSwitcher(
			SamlsinglesignonConstants.SSO_SIGNING_ALGORITHMS);

	private final PropertyConfigSwitcher metadataVerificationSwitcher = new PropertyConfigSwitcher(
			SamlsinglesignonConstants.SSO_METADATA_VERIFICATION_ENABLED);

	private final ResourceLoader resourceLoader = new DefaultResourceLoader();

	@Before
	public void setUp()
	{
		relyingPartyRegistrationIdSwitcher.switchToValue(REGISTRATION_ID);
		disableMetadataVerification();
	}

	@After
	public void tearDown()
	{
		relyingPartyRegistrationIdSwitcher.switchBackToDefault();
		legacyEndpointsSwitcher.switchBackToDefault();
		filterEndpoint.switchBackToDefault();
		metadataEndpoint.switchBackToDefault();
		ssoSigningAlgorithm.switchBackToDefault();
	}

	@Test
	public void shouldCreateRelyingPartyRegistrationRepositoryFromMetadataFile()
	{
		//given
		relyingPartyRegistrationIdSwitcher.switchToValue(REGISTRATION_ID);
		final File metadataFile = getFileForResource(METADATA_LOCATION);

		//when
		final RelyingPartyRegistrationRepository relyingPartyRegistrationRepository = SamlObjectsFactory
				.getRelyingPartyRegistrationRepository(metadataFile, TEST_ENTITY_ID,
						getSigningAndDecryptionSaml2Credentials(), getSignatureValidationFilter());
		final RelyingPartyRegistration relyingPartyRegistration = getRelyingPartyRegistration(relyingPartyRegistrationRepository);

		//then
		assertThat(relyingPartyRegistrationRepository).isNotNull();
		assertThat(relyingPartyRegistration).isNotNull();
		assertThat(relyingPartyRegistration.getRegistrationId()).isEqualTo(REGISTRATION_ID);
		assertThat(relyingPartyRegistration.getSigningX509Credentials()).isNotNull();
		assertThat(relyingPartyRegistration.getDecryptionX509Credentials()).isNotNull();
		assertThat(getEntityId(relyingPartyRegistration)).isEqualTo(ENTITY_ID);
		assertThat(getSingleSignOnServiceLocation(relyingPartyRegistration)).isEqualTo(SSO_SERVICE_LOCATION);
		assertThat(getWantAuthnRequestsSigned(relyingPartyRegistration)).isTrue();
	}

	@Test
	public void shouldCreateRelyingPartyRegistrationRepositoryWithDefaultSigningAlgorithms()
	{
		//given
		ssoSigningAlgorithm.switchBackToDefault();
		final File metadataFile = getFileForResource(METADATA_LOCATION);

		//when
		final RelyingPartyRegistrationRepository relyingPartyRegistrationRepository = SamlObjectsFactory
				.getRelyingPartyRegistrationRepository(metadataFile, TEST_ENTITY_ID,
						getSigningAndDecryptionSaml2Credentials(), getSignatureValidationFilter());
		final RelyingPartyRegistration relyingPartyRegistration = getRelyingPartyRegistration(relyingPartyRegistrationRepository);

		final List<String> signingAlgorithms = signingAlgorithms(relyingPartyRegistration);

		//then
		assertThat(signingAlgorithms).containsAll(DEFAULT_SIGNING_ALGORITHMS);
	}

	@Test
	public void shouldCreateRelyingPartyRegistrationRepositoryWithCustomSigningAlgorithms()
	{
		//given
		ssoSigningAlgorithm.switchToValue(String.format("%s,%s", ALGO_ID_SIGNATURE_RSA, ALGO_ID_SIGNATURE_DSA));
		final File metadataFile = getFileForResource(METADATA_LOCATION);

		//when
		final RelyingPartyRegistrationRepository relyingPartyRegistrationRepository = SamlObjectsFactory
				.getRelyingPartyRegistrationRepository(metadataFile, TEST_ENTITY_ID,
						getSigningAndDecryptionSaml2Credentials(), getSignatureValidationFilter());
		final RelyingPartyRegistration relyingPartyRegistration = getRelyingPartyRegistration(relyingPartyRegistrationRepository);

		final List<String> signingAlgorithms = signingAlgorithms(relyingPartyRegistration);

		//then
		assertThat(signingAlgorithms).containsOnly(ALGO_ID_SIGNATURE_RSA, ALGO_ID_SIGNATURE_DSA);
	}

	@Test
	public void shouldCreateRelyingPartyRegistrationWithLegacyConfiguration()
	{
		//given
		final File metadataFile = getFileForResource(METADATA_LOCATION);
		legacyEndpointsSwitcher.switchToValue("true");
		filterEndpoint.switchToValue(SamlObjectsFactory.DEFAULT_LEGACY_FILTER_PROCESSES_URI);
		metadataEndpoint.switchToValue(SamlObjectsFactory.DEFAULT_LEGACY_METADATA_URI);
		final RelyingPartyRegistrationRepository relyingPartyRegistrationRepository = SamlObjectsFactory
				.getRelyingPartyRegistrationRepository(metadataFile, TEST_ENTITY_ID,
						getSigningAndDecryptionSaml2Credentials(), getSignatureValidationFilter());
		final RelyingPartyRegistration relyingPartyRegistration = getRelyingPartyRegistration(relyingPartyRegistrationRepository);
		//then
		assertThat(relyingPartyRegistrationRepository).isNotNull();
		assertThat(relyingPartyRegistration).isNotNull();
		assertThat(relyingPartyRegistration.getAssertionConsumerServiceLocation()).isEqualTo("{baseUrl}/saml/SSO");
		assertThat(relyingPartyRegistration.getEntityId()).isEqualTo(TEST_ENTITY_ID);
		assertThat(SamlObjectsFactory.getFilterProcessesUrl()).isEqualTo(
				SamlObjectsFactory.DEFAULT_LEGACY_FILTER_PROCESSES_URI);
		assertThat(SamlObjectsFactory.getMetadataRequestMatcher()).isInstanceOf(
				AntPathRequestMatcher.class);
		assertThat(
				((AntPathRequestMatcher) SamlObjectsFactory.getMetadataRequestMatcher()).getPattern()).isEqualTo(
				SamlObjectsFactory.DEFAULT_LEGACY_METADATA_URI);

	}

	@Test
	public void shouldCreateRelyingPartyRegistrationWithNewConfiguration()
	{
		//given
		final File metadataLocation = getFileForResource(METADATA_LOCATION);
		legacyEndpointsSwitcher.switchToValue("false");
		filterEndpoint.switchToValue(SamlObjectsFactory.DEFAULT_FILTER_PROCESSES_URI);
		final RelyingPartyRegistrationRepository relyingPartyRegistrationRepository = SamlObjectsFactory
				.getRelyingPartyRegistrationRepository(metadataLocation, TEST_ENTITY_ID,
						getSigningAndDecryptionSaml2Credentials(), getSignatureValidationFilter());
		final RelyingPartyRegistration relyingPartyRegistration = getRelyingPartyRegistration(relyingPartyRegistrationRepository);
		//then
		assertThat(relyingPartyRegistrationRepository).isNotNull();
		assertThat(relyingPartyRegistration).isNotNull();
		assertThat(relyingPartyRegistration.getAssertionConsumerServiceLocation()).isEqualTo(
				"{baseUrl}/login/saml2/sso/{registrationId}");
		assertThat(relyingPartyRegistration.getEntityId()).isEqualTo(TEST_ENTITY_ID);
		assertThat(SamlObjectsFactory.getFilterProcessesUrl()
		                             .equals(SamlObjectsFactory.DEFAULT_FILTER_PROCESSES_URI));
		assertThat(SamlObjectsFactory.getMetadataRequestMatcher()).isInstanceOf(
				AntPathRequestMatcher.class);
		assertThat(
				((AntPathRequestMatcher) SamlObjectsFactory.getMetadataRequestMatcher()).getPattern()).isEqualTo(
				SamlObjectsFactory.DEFAULT_METADATA_URI);

	}

	@Test
	public void shouldReturnLegacyConfigurationFromProperties()
	{
		legacyEndpointsSwitcher.switchToValue("true");
		filterEndpoint.switchToValue("/configured/filter/endpoint/");
		metadataEndpoint.switchToValue("/configured/metadata/endpoint");
		assertThat(SamlObjectsFactory.getFilterProcessesUrl()).contains("/configured/filter/endpoint/");
		assertThat(getUrlFromRequestMatcher(SamlObjectsFactory.getMetadataRequestMatcher())).contains(
				"/configured/metadata/endpoint");
	}

	@Test
	public void shouldReturnConfigurationFromPropertiesWithRegistrationIdInUri()
	{
		legacyEndpointsSwitcher.switchToValue("false");
		filterEndpoint.switchToValue("/configured/filter/endpoint/{registrationId}");
		metadataEndpoint.switchToValue("/configured/metadata/endpoint/{registrationId}");
		assertThat(SamlObjectsFactory.getFilterProcessesUrl()).contains(
				"/configured/filter/endpoint/{registrationId}");
		assertThat(getUrlFromRequestMatcher(SamlObjectsFactory.getMetadataRequestMatcher())).contains(
				"/configured/metadata/endpoint/{registrationId}");
	}

	@Test
	public void shouldReturnDefaultConfigurationForInvalidConfigurationFromProperties()
	{
		legacyEndpointsSwitcher.switchToValue("false");
		filterEndpoint.switchToValue("/configured/filter/endpoint/");
		metadataEndpoint.switchToValue("/configured/metadata/endpoint/");
		assertThat(SamlObjectsFactory.getFilterProcessesUrl()).contains(
				SamlObjectsFactory.DEFAULT_FILTER_PROCESSES_URI);
		assertThat(getUrlFromRequestMatcher(SamlObjectsFactory.getMetadataRequestMatcher())).contains(
				SamlObjectsFactory.DEFAULT_METADATA_URI);
	}

	@Test
	public void shouldThrowExceptionWhenFilteringMetadataWithInvalidSignature() throws IOException
	{
		//given
		final File metadataFile = getFileForResource(METADATA_LOCATION);
		enableMetadataVerification();

		//when, then
		assertThatThrownBy(() -> SamlObjectsFactory.getRelyingPartyRegistrationRepository
				(metadataFile, TEST_ENTITY_ID,
						getSigningAndDecryptionSaml2Credentials(),
						getSignatureValidationFilter()))
				.isInstanceOf(InvalidMetadataException.class)
				.hasMessageStartingWith("Error filtering metadata from")
				.hasMessageContaining(metadataFile.getName());
	}

	@Test
	public void shouldNotFilterMetadataIfSignatureVerificationIsDisabled()
	{
		//given
		final File metadataFile = getFileForResource(METADATA_LOCATION);
		disableMetadataVerification();
		boolean exception = false;

		//when
		try
		{
			SamlObjectsFactory.getRelyingPartyRegistrationRepository(metadataFile, TEST_ENTITY_ID,
					getSigningAndDecryptionSaml2Credentials(), getSignatureValidationFilter());
		}
		catch (final Exception e)
		{
			exception = true;
		}

		//then
		assertThat(exception).isFalse();
	}

	@Test
	public void shouldThrowExceptionForNotExistingMetadataFile()
	{

		final File nonExistingMetadataFile = new File("notExistingFileOfMetadata.xml");
		final RelyingPartyRegistrationRepository relyingPartyRegistrationRepository = SamlObjectsFactory.getRelyingPartyRegistrationRepository
			(nonExistingMetadataFile, TEST_ENTITY_ID,
					getSigningAndDecryptionSaml2Credentials(),
					getSignatureValidationFilter());

		assertThatThrownBy(() -> relyingPartyRegistrationRepository.findByRegistrationId("samlIsNotProperlyConfigured")).isInstanceOf(
				Saml2Exception.class);

	}

	private String getUrlFromRequestMatcher(final RequestMatcher antPathRequestMatcher)
	{
		return ((AntPathRequestMatcher) antPathRequestMatcher).getPattern();
	}

	private String getEntityId(final RelyingPartyRegistration registration)
	{
		return getAssertingPartyDetails(registration).getEntityId();
	}

	private String getSingleSignOnServiceLocation(final RelyingPartyRegistration registration)
	{
		return getAssertingPartyDetails(registration).getSingleSignOnServiceLocation();
	}

	private boolean getWantAuthnRequestsSigned(final RelyingPartyRegistration registration)
	{
		return getAssertingPartyDetails(registration).getWantAuthnRequestsSigned();
	}

	private RelyingPartyRegistration.AssertingPartyDetails getAssertingPartyDetails(final RelyingPartyRegistration registration)
	{
		return registration.getAssertingPartyDetails();
	}

	private RelyingPartyRegistration getRelyingPartyRegistration(final RelyingPartyRegistrationRepository registrationRepository)
	{
		return registrationRepository.findByRegistrationId(REGISTRATION_ID);
	}

	private List<String> signingAlgorithms(final RelyingPartyRegistration relyingPartyRegistration)
	{
		return relyingPartyRegistration.getAssertingPartyDetails().getSigningAlgorithms();
	}

	private Saml2X509Credential getSigningAndDecryptionSaml2Credentials()
	{
		return new Saml2X509Credential(getPrivateKey(), getTestX509Certificate(),
				Saml2X509Credential.Saml2X509CredentialType.SIGNING, Saml2X509Credential.Saml2X509CredentialType.DECRYPTION);
	}

	private PrivateKey getPrivateKey()
	{
		PrivateKey privateKey = null;

		try
		{
			privateKey = KeyPairGenerator.getInstance(KEY_PAIR_GENERATOR_ALGORITHM).generateKeyPair().getPrivate();
		}
		catch (final Exception e)
		{
			LOG.info(e.getMessage());
		}

		return privateKey;
	}

	private X509Certificate getTestX509Certificate()
	{
		X509Certificate certificate = null;

		try
		{
			certificate = X509Support.decodeCertificate(
					"MIIDTzCCAjegAwIBAgIEVHvPSDANBgkqhkiG9w0BAQsFADBYMQswCQYDVQQGEwJ0\n" +
							"ZTENMAsGA1UECBMEdGVzdDENMAsGA1UEBxMEdGVzdDENMAsGA1UEChMEdGVzdDEN\n" +
							"MAsGA1UECxMEdGVzdDENMAsGA1UEAxMEdGVzdDAeFw0yMTEwMjAwODA2MzBaFw0y\n" +
							"NDEwMDQwODA2MzBaMFgxCzAJBgNVBAYTAnRlMQ0wCwYDVQQIEwR0ZXN0MQ0wCwYD\n" +
							"VQQHEwR0ZXN0MQ0wCwYDVQQKEwR0ZXN0MQ0wCwYDVQQLEwR0ZXN0MQ0wCwYDVQQD\n" +
							"EwR0ZXN0MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAm5iuCs+k5fYj\n" +
							"tOLuNK4UBldJb9m7D+BxeIT0vKS0OH+uQ3FDYJXEz3/l4wGhYOS5YKr93KwJi7mI\n" +
							"v9rGSaGylmhfs9zcEbUvT6EbBkj5sFeg3B1hXOYD8w81HVv03U+WoEekVKFP7+qw\n" +
							"/6NaZh3+PzWr9K/M3E+V3K84icx+JzcXafrBmjfhNq2tKxYOGPO/YqqyCT8vtucS\n" +
							"2wQmyMv4Qfib0ACn0k59gy6YT6sqsPgrHgbS8iSEfNAaTXyG0oY3gso4Fsn2RGWo\n" +
							"2pr09JAmwmN6anYTe9MMGhyoV3nXoIKXbVh5K7fDomUv1VW+P581ZHPxma4DApa5\n" +
							"SZAyO0rTQQIDAQABoyEwHzAdBgNVHQ4EFgQUXCxq65C2/OL9k/PWMDkYCrK0kf8w\n" +
							"DQYJKoZIhvcNAQELBQADggEBAHMozhaxZonegT5UlipMc0IJdawRH0PcwbTkWxT+\n" +
							"OBoO3jcwAQ611Gh9FBe+edatFkLClsePiF5AQR/nJec8HbDdWld3lQkm1mGUgNqK\n" +
							"ozjQX/j8TwqNt7SOI9OaDP+UsmXwmKNz/CZ2QIirfd/TlwcYsuNRNHtRLKStq4Gy\n" +
							"nGfL2Luax2xCGpVTTyoFimMu04wvo+cO5OGIikJELWMsz2zwbA/VBmBiRl6rbniG\n" +
							"8TBRaNNfJOJg4wbPbTReP9oz+4nWmUpEtC6J0DwSnt92y+o4CyV3bJtM+JqPS5TG\n" +
							"SBKe4Zl2J8T+NSHArYeEKdv+tsDvPM9+rTgcK+3l9eUIMCU=");
		}
		catch (final Exception e)
		{
			LOG.info(e.getMessage());
		}

		return certificate;
	}

	private SignatureValidationFilter getSignatureValidationFilter()
	{
		return SignatureValidationFilterFactory.getSignatureValidationFilter(Set.of(getTestX509Certificate()));
	}

	private File getFileForResource(final String location)
	{
		File fileForResource = null;

		try
		{
			fileForResource = resourceLoader.getResource(location).getFile();
		}
		catch (final IOException e)
		{
			LOG.info(e.getMessage());
		}

		return fileForResource;
	}

	private void enableMetadataVerification()
	{
		metadataVerificationSwitcher.switchToValue("true");
	}

	private void disableMetadataVerification()
	{
		metadataVerificationSwitcher.switchToValue("false");
	}
}