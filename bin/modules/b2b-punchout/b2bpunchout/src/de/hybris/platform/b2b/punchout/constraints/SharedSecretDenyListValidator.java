/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.constraints;

import de.hybris.platform.b2b.punchout.model.PunchOutCredentialModel;
import de.hybris.platform.core.Registry;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;
import com.google.common.io.Files;


import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Set;


/**
 * Validates if shared secret is out of the deny list file.
 * {@link PunchOutCredentialModel#getSharedsecret()}
 */
public class SharedSecretDenyListValidator implements ConstraintValidator<SharedSecretDenyList, String>
{
	private static final Logger LOG = LoggerFactory.getLogger(SharedSecretDenyListValidator.class);
	private Set<String> deniedSecrets = Collections.emptySet();
	private static final String SECRET_DENYLIST_FILE = "b2bpunchout.sharedsecret.denylist.file";

	@Override
	public void initialize(SharedSecretDenyList constraintAnnotation)
	{
		final String secretDenyListFile = Registry.getCurrentTenantNoFallback().getConfig().getParameter(SECRET_DENYLIST_FILE);
		if (StringUtils.isBlank(secretDenyListFile))
		{
			LOG.info("Secret deny list file is not set. SharedSecretDenyListValidator will not be active.");
			return;
		}
		final URL resource = Thread.currentThread().getContextClassLoader().getResource(secretDenyListFile);
		if (resource == null)
		{
			LOG.info("Can not find secret deny list file: {}. SharedSecretDenyListValidator will not be active.",
					secretDenyListFile);
			return;
		}

		try
		{
			final File denyListFile = new File(resource.toURI());
			this.deniedSecrets = Sets.newHashSet(Files.readLines(denyListFile, StandardCharsets.UTF_8));
		}
		catch (URISyntaxException | IOException e)
		{
			LOG.warn("Failed to read deny list file: {}, SharedSecretDenyListValidator will not be active.",
					secretDenyListFile, e);
		}
	}

	@Override
	public boolean isValid(final String sharedSecret, final ConstraintValidatorContext validatorContext)
	{
		return !this.deniedSecrets.contains(sharedSecret);
	}
}
