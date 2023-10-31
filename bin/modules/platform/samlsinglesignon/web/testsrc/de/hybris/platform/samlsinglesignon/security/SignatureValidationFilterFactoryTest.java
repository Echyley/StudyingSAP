/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.samlsinglesignon.security;

import static de.hybris.platform.mediaweb.assertions.assertj.Assertions.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;

import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Set;

import org.junit.Test;
import org.opensaml.saml.metadata.resolver.filter.impl.SignatureValidationFilter;
import org.opensaml.security.x509.PKIXTrustEvaluator;
import org.opensaml.security.x509.PKIXValidationInformation;
import org.opensaml.security.x509.PKIXValidationInformationResolver;
import org.opensaml.security.x509.X509Support;
import org.opensaml.security.x509.impl.BasicPKIXValidationInformation;
import org.opensaml.security.x509.impl.BasicX509CredentialNameEvaluator;
import org.opensaml.security.x509.impl.CertPathPKIXTrustEvaluator;
import org.opensaml.security.x509.impl.CertPathPKIXValidationOptions;
import org.opensaml.security.x509.impl.StaticPKIXValidationInformationResolver;
import org.opensaml.security.x509.impl.X509CredentialNameEvaluator;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xmlsec.keyinfo.impl.BasicProviderKeyInfoCredentialResolver;
import org.opensaml.xmlsec.signature.support.impl.PKIXSignatureTrustEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.shibboleth.utilities.java.support.resolver.CriteriaSet;

@UnitTest
public class SignatureValidationFilterFactoryTest
{
	private static final Logger LOG = LoggerFactory.getLogger(SignatureValidationFilterFactoryTest.class);
	private static final Integer VERIFICATION_DEPTH = 4;

	@Test
	public void shouldCreateSignatureValidationFilter()
	{
		//given
		final Collection<X509Certificate> certificate = Set.of(getTestX509Certificate());

		//when
		final SignatureValidationFilter signatureFilter = SignatureValidationFilterFactory.getSignatureValidationFilter(
				certificate);

		//then
		assertThat(signatureFilter).isNotNull();
		assertSignatureTrustEngine(signatureFilter);
	}

	private void assertSignatureTrustEngine(final SignatureValidationFilter signatureValidationFilter)
	{
		final PKIXSignatureTrustEngine signatureTrustEngine = (PKIXSignatureTrustEngine) signatureValidationFilter.getSignatureTrustEngine();

		assertThat(signatureTrustEngine).isNotNull().isInstanceOf(PKIXSignatureTrustEngine.class);
		assertPKIXValidationInformationResolver(signatureTrustEngine);
		assertKeyInfoCredentialResolver(signatureTrustEngine);
		assertPKIXTrustEvaluator(signatureTrustEngine);
		assertX509CredentialNameEvaluator(signatureTrustEngine);
	}

	private void assertPKIXValidationInformationResolver(final PKIXSignatureTrustEngine signatureTrustEngine)
	{
		final PKIXValidationInformationResolver pkixResolver = signatureTrustEngine.getPKIXResolver();

		assertThat(pkixResolver).isNotNull().isInstanceOf(StaticPKIXValidationInformationResolver.class);
		assertPKIXValidationInformation((StaticPKIXValidationInformationResolver) pkixResolver);
	}

	private void assertPKIXValidationInformation(final StaticPKIXValidationInformationResolver pkixResolver)
	{
		PKIXValidationInformation pkixValidationInformation = null;

		try
		{
			pkixValidationInformation = pkixResolver.resolveSingle(new CriteriaSet());
		}
		catch (final Exception e)
		{
			LOG.info(e.getMessage());
		}

		assertThat(pkixValidationInformation).isNotNull().isInstanceOf(BasicPKIXValidationInformation.class);
		assertThat(pkixValidationInformation.getCertificates()).contains(getTestX509Certificate());
		assertThat(pkixValidationInformation.getVerificationDepth()).isEqualTo(VERIFICATION_DEPTH);
	}

	private void assertKeyInfoCredentialResolver(final PKIXSignatureTrustEngine signatureTrustEngine)
	{
		final KeyInfoCredentialResolver keyInfoResolver = signatureTrustEngine.getKeyInfoResolver();

		assertThat(keyInfoResolver).isNotNull().isInstanceOf(BasicProviderKeyInfoCredentialResolver.class);
	}

	private void assertPKIXTrustEvaluator(final PKIXSignatureTrustEngine signatureTrustEngine)
	{
		final PKIXTrustEvaluator pkixTrustEvaluator = signatureTrustEngine.getPKIXTrustEvaluator();

		assertThat(pkixTrustEvaluator).isNotNull().isInstanceOf(CertPathPKIXTrustEvaluator.class);
		assertThat(
				((CertPathPKIXValidationOptions) pkixTrustEvaluator.getPKIXValidationOptions()).isForceRevocationEnabled()).isFalse();
	}

	private void assertX509CredentialNameEvaluator(final PKIXSignatureTrustEngine signatureTrustEngine)
	{
		final X509CredentialNameEvaluator credentialNameEvaluator = signatureTrustEngine.getX509CredentialNameEvaluator();

		assertThat(credentialNameEvaluator).isNotNull().isInstanceOf(BasicX509CredentialNameEvaluator.class);
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
}