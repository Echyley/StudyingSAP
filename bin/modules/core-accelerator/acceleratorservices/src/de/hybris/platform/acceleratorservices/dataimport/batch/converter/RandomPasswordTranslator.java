/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.dataimport.batch.converter;

import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.impex.jalo.translators.ConvertPlaintextToEncodedUserPasswordTranslator;
import de.hybris.platform.jalo.Item;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;


/**
 * This translator adds some random text to the orginal password and passes this value to
 * ConvertPlaintestToEncodedUserPasswordTranslator.
 */
public class RandomPasswordTranslator extends ConvertPlaintextToEncodedUserPasswordTranslator
{
	private static final Logger LOG = Logger.getLogger(RandomPasswordTranslator.class);
	private static final String RANDOM_ALGORITHM = "SHA1PRNG";
	private static final int RANDOM_STRING_LENGTH = 6;

	@Override
	public void performImport(final String cellValue, final Item processedItem) throws ImpExException
	{
		try {

			SecureRandom sRnd = SecureRandom.getInstance(RANDOM_ALGORITHM);
			String randomVal = RandomStringUtils.random(RANDOM_STRING_LENGTH, 0, 0,
					true, true, null, sRnd);
			final String newCellValue = (cellValue == null ? "" : cellValue.trim()) + randomVal;
			super.performImport(newCellValue, processedItem);

		} catch (NoSuchAlgorithmException e) {
			LOG.error(e.getMessage(), e);
		}
	}
}
