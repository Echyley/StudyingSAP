/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.samlsinglesignon.utils;

import static de.hybris.platform.mediaweb.assertions.assertj.Assertions.assertThat;
import static de.hybris.platform.mediaweb.assertions.assertj.Assertions.assertThatThrownBy;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.samlsinglesignon.exceptions.IncorrectAliasException;
import de.hybris.platform.samlsinglesignon.exceptions.KeyStoreInitializationException;

import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.saml2.core.Saml2X509Credential;

@UnitTest
public class JKSUtilTest
{
	private static final String KEY_STORE_LOCATION = "samlsinglesignon/test/testKeystore.jks";
	private static final String INCORRECT_KEY_STORE_LOCATION = "samlsinglesignon/incorrect/testKeystore.jks";
	private static final String PRIVATE_KEY_ENTRY_ALIAS = "testAlias";
	private static final String KEY_STORE_PASSWORD = "testPassword";
	private static final String PRIVATE_KEY_PASSWORD = "testPassword";
	private static final String TEST_CERTIFICATE_1_DN_NAME = "CN=test, OU=test, O=test, L=test, ST=test, C=te";
	private static final String TEST_CERTIFICATE_2_DN_NAME = "CN=test2, OU=test2, O=test2, L=test2, ST=test2, C=te";
	private static final int CERTIFICATES_SIZE = 2;

	@Test
	public void shouldReturnSigningAndDecryptionSaml2Credentials()
	{
		//given
		final Resource keyStoreFile = getKeyStoreFile(KEY_STORE_LOCATION);

		//when
		final Saml2X509Credential saml2Credentials = JKSUtil.getSigningAndDecryptionSaml2Credentials(keyStoreFile, KEY_STORE_PASSWORD,
				getPrivateKeyPassword(), PRIVATE_KEY_ENTRY_ALIAS);

		//then
		assertThat(saml2Credentials).isNotNull();
		assertThat(saml2Credentials.getPrivateKey()).isNotNull();
		assertThat(saml2Credentials.getCertificate()).isNotNull();
		assertThat(saml2Credentials.isSigningCredential()).isTrue();
		assertThat(saml2Credentials.isDecryptionCredential()).isTrue();
	}

	@Test
	public void shouldThrowExceptionWhenProvidingIncorrectKeyStoreLocation()
	{
		//given
		final Resource keyStoreFile = getKeyStoreFile(INCORRECT_KEY_STORE_LOCATION);


		//when, then
		assertThatThrownBy(() -> JKSUtil.getSigningAndDecryptionSaml2Credentials(keyStoreFile, KEY_STORE_PASSWORD, getPrivateKeyPassword(),
				PRIVATE_KEY_ENTRY_ALIAS)).isInstanceOf(KeyStoreInitializationException.class)
		                                 .hasMessageContaining("Error initializing keystore");
	}

	@Test
	public void shouldThrowExceptionWhenProvidingIncorrectPrivateKeyEntryAlias()
	{
		//given
		final Resource keyStoreFile = getKeyStoreFile(KEY_STORE_LOCATION);
		final String incorrectPrivateKeyEntryAlias = getRandomUUID();

		//when, then
		assertThatThrownBy(() -> JKSUtil.getSigningAndDecryptionSaml2Credentials(keyStoreFile, KEY_STORE_PASSWORD, getPrivateKeyPassword(),
				incorrectPrivateKeyEntryAlias)).isInstanceOf(KeyStoreInitializationException.class)
		                                       .hasMessageContaining("Cant obtain key entry");
	}

	@Test
	public void shouldThrowExceptionWhenProvidingIncorrectPrivateKeyPassword()
	{
		//given
		final Resource keyStoreFile = getKeyStoreFile(KEY_STORE_LOCATION);
		final Map<String, String> incorrectPrivateKeyPassword = Map.of(PRIVATE_KEY_ENTRY_ALIAS, getRandomUUID());

		//when, then
		assertThatThrownBy(
				() -> JKSUtil.getSigningAndDecryptionSaml2Credentials(keyStoreFile, KEY_STORE_PASSWORD, incorrectPrivateKeyPassword,
						PRIVATE_KEY_ENTRY_ALIAS)).isInstanceOf(KeyStoreInitializationException.class)
		                                         .hasMessageContaining("Cant obtain key entry");
	}

	@Test
	public void shouldReturnCorrectPrivateKeyPasswordForProvidedKeyEntryAlias()
	{
		//given
		final Map<String, String> privateKeyPasswords = getPrivateKeyPasswords();

		//when
		final KeyStore.PasswordProtection privateKey = JKSUtil.getPrivateKeyPasswordForProvidedAlias(privateKeyPasswords,
				PRIVATE_KEY_ENTRY_ALIAS);

		//then
		assertThat(privateKey.getPassword()).isEqualTo(PRIVATE_KEY_PASSWORD.toCharArray());
	}

	@Test
	public void shouldThrowExceptionWhenThereIsNoConfiguredPrivateKeyPasswordForProvidedAlias()
	{
		//given
		final Map<String, String> privateKeyPasswords = getPrivateKeyPasswords();
		final String privateKeyEntryAlias = getRandomUUID();

		//when, then
		assertThatThrownBy(() -> JKSUtil.getPrivateKeyPasswordForProvidedAlias(privateKeyPasswords,
				privateKeyEntryAlias)).isInstanceOf(IncorrectAliasException.class)
		                              .hasMessageContaining(
				                              JKSUtil.PRIVATE_KEY_PASSWORD + " for alias " + privateKeyEntryAlias + " not found");
	}

	@Test
	public void shouldReturnAllCertificates()
	{
		//given
		final Resource keyStoreFile = getKeyStoreFile(KEY_STORE_LOCATION);

		//when
		final Collection<X509Certificate> allCertificates = JKSUtil.getAllCertificates(keyStoreFile, KEY_STORE_PASSWORD);


		//then
		assertThat(allCertificates).isNotNull();
		assertThat(allCertificates).hasSize(CERTIFICATES_SIZE);
		assertThat(getCertificateDNNames(allCertificates)).contains(TEST_CERTIFICATE_1_DN_NAME, TEST_CERTIFICATE_2_DN_NAME);

	}

	@Test
	public void shouldThrowExceptionWhenProvidingIncorrectKeyStoreLocationForGettingAllCertificates()
	{
		//given
		final Resource keyStoreFile = getKeyStoreFile(INCORRECT_KEY_STORE_LOCATION);

		//when, then
		assertThatThrownBy(() -> JKSUtil.getAllCertificates(keyStoreFile, KEY_STORE_PASSWORD)).isInstanceOf(
				KeyStoreInitializationException.class).hasMessageContaining("Error initializing keystore");
	}

	@Test
	public void shouldThrowExceptionWhenProvidingIncorrectKeyStorePasswordForGettingAllCertificates()
	{
		//given
		final Resource keyStoreFile = getKeyStoreFile(KEY_STORE_LOCATION);

		//when, then
		assertThatThrownBy(() -> JKSUtil.getAllCertificates(keyStoreFile, getRandomUUID())).isInstanceOf(
				KeyStoreInitializationException.class).hasMessageContaining("Error initializing keystore");
	}

	private Map<String, String> getPrivateKeyPassword()
	{
		return Map.of(PRIVATE_KEY_ENTRY_ALIAS, PRIVATE_KEY_PASSWORD);
	}

	private Map<String, String> getPrivateKeyPasswords()
	{
		return Map.of(getRandomUUID(), getRandomUUID(),
				PRIVATE_KEY_ENTRY_ALIAS, PRIVATE_KEY_PASSWORD,
				getRandomUUID(), getRandomUUID());
	}

	private String getRandomUUID()
	{
		return UUID.randomUUID().toString();
	}

	private Resource getKeyStoreFile(final String keyStoreLocation)
	{
		return new ClassPathResource(keyStoreLocation);
	}

	private Collection<String> getCertificateDNNames(final Collection<X509Certificate> certificates)
	{
		return certificates.stream()
		                   .map(certificate -> certificate.getIssuerDN().getName())
		                   .collect(Collectors.toSet());
	}
}