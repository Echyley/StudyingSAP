/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.b2b.punchout.constraints;

import de.hybris.platform.b2b.punchout.model.PunchOutCredentialModel;
import de.hybris.platform.impex.constants.ImpExConstants;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.util.Config;
import de.hybris.platform.validation.exceptions.HybrisConstraintViolation;
import de.hybris.platform.validation.model.constraints.jsr303.AbstractConstraintTest;

import javax.annotation.Resource;

import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;



public class SharedSecretValidationTest extends AbstractConstraintTest
{
	protected static final String REGX_MESSAGE = "The shared secret must include both uppercase and lowercase letters, numerical digit(s), special character(s), and 8-100 total characters.";
	protected static final String DENYLIST_MESSAGE = "This shared secret is on the deny list.";
	protected static final String FIELD_MESSAGE = "localizedMessage";

	@Resource
	private I18NService i18NService;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
		Config.setParameter("b2bpunchout.sharedsecret.denylist.file", "sharedsecret-excludelist-test.txt");
		importCsv("/impex/essentialdata-b2bpunchout-constraints.impex", "UTF-8");
		importCsv("/impex/essentialdata-b2bpunchout-constraints_en.impex", "UTF-8");
		validationService.reloadValidationEngine();
	}

	@Test
	public void validateSharedSecretMatchPattern()
	{
		this.i18NService.setCurrentLocale(Locale.ENGLISH);
		final PunchOutCredentialModel punchOutCredentialModel = modelService.create(PunchOutCredentialModel.class);
		punchOutCredentialModel.setSharedsecret("Very@Secret123");
		Set<HybrisConstraintViolation> constraintViolations = validationService.validate(punchOutCredentialModel);
		assertEquals("validate punchout credential shared secret successfully", 0, constraintViolations.size());
	}


	@Test
	public void validateSharedSecretUnMatchPatternNoSpecChar()
	{
		this.i18NService.setCurrentLocale(Locale.ENGLISH);

		final PunchOutCredentialModel punchOutCredentialModel1 = modelService.create(PunchOutCredentialModel.class);
		punchOutCredentialModel1.setSharedsecret("VerySecret1234");
		Set<HybrisConstraintViolation> constraintViolations1 = validationService.validate(punchOutCredentialModel1);
		assertEquals("shared secret should include one more special character", 1, constraintViolations1.size());
		assertThat(constraintViolations1, hasItem(hasProperty(FIELD_MESSAGE, is(REGX_MESSAGE))));
	}

	@Test
	public void validateSharedSecretUnMatchPatternWithSpecChar()
	{
		this.i18NService.setCurrentLocale(Locale.ENGLISH);

		final String sharedSecretPerfix = "VerySe1";
		final String specialChar = "!\"#$%&'()*+,-./:;<=>?@[]^_`{|}~";
		specialChar.chars().forEach(i->{
			final String sharedSecret =  sharedSecretPerfix + Character.toChars(i);
			final PunchOutCredentialModel punchOutCredentialModel = modelService.create(PunchOutCredentialModel.class);
			punchOutCredentialModel.setSharedsecret(sharedSecret);
			Set<HybrisConstraintViolation> constraintViolations = validationService.validate(punchOutCredentialModel);
			assertEquals("validate punchout credential shared secret" + sharedSecret + "with special char successfully", 0, constraintViolations.size());
		});
	}


	@Test
	public void validateSharedSecretUnMatchPatternMinLength()
	{
		this.i18NService.setCurrentLocale(Locale.ENGLISH);

		final PunchOutCredentialModel punchOutCredentialModel2 = modelService.create(PunchOutCredentialModel.class);
		punchOutCredentialModel2.setSharedsecret("V1r%y31");
		Set<HybrisConstraintViolation> constraintViolations2 = validationService.validate(punchOutCredentialModel2);
		assertEquals("length of shared secret should > 8", 1, constraintViolations2.size());
		assertThat(constraintViolations2, hasItem(hasProperty(FIELD_MESSAGE, is(REGX_MESSAGE))));
	}

	@Test
	public void validateSharedSecretUnMatchPatternMaxLength()
	{
		this.i18NService.setCurrentLocale(Locale.ENGLISH);

		final String randomString = RandomStringUtils.randomAlphanumeric(97);
		final PunchOutCredentialModel punchOutCredentialModel6 = modelService.create(PunchOutCredentialModel.class);
		punchOutCredentialModel6.setSharedsecret(randomString + "Se1$");
		Set<HybrisConstraintViolation> constraintViolations6 = validationService.validate(punchOutCredentialModel6);
		assertEquals("length of shared secret should <= 100", 1, constraintViolations6.size());
		assertThat(constraintViolations6, hasItem(hasProperty(FIELD_MESSAGE, is(REGX_MESSAGE))));
	}

	@Test
	public void validateSharedSecretUnMatchPatternNoUppercase()
	{
		this.i18NService.setCurrentLocale(Locale.ENGLISH);

		final PunchOutCredentialModel punchOutCredentialModel4 = modelService.create(PunchOutCredentialModel.class);
		punchOutCredentialModel4.setSharedsecret("secret123$");
		Set<HybrisConstraintViolation> constraintViolations4 = validationService.validate(punchOutCredentialModel4);
		assertEquals("shared secret should include one more upper character", 1, constraintViolations4.size());
		assertThat(constraintViolations4, hasItem(hasProperty(FIELD_MESSAGE, is(REGX_MESSAGE))));
	}

	@Test
	public void validateSharedSecretUnMatchPatternNoLowercase()
	{
		this.i18NService.setCurrentLocale(Locale.ENGLISH);

		final PunchOutCredentialModel punchOutCredentialModel5 = modelService.create(PunchOutCredentialModel.class);
		punchOutCredentialModel5.setSharedsecret("SECRET123$");
		Set<HybrisConstraintViolation> constraintViolations5 = validationService.validate(punchOutCredentialModel5);
		assertEquals("shared secret should include one more lower character", 1, constraintViolations5.size());
		assertThat(constraintViolations5, hasItem(hasProperty(FIELD_MESSAGE, is(REGX_MESSAGE))));
	}

	@Test
	public void validateSharedSecretUnMatchPatternNoDigitalNum()
	{
		this.i18NService.setCurrentLocale(Locale.ENGLISH);

		final PunchOutCredentialModel punchOutCredentialModel3 = modelService.create(PunchOutCredentialModel.class);
		punchOutCredentialModel3.setSharedsecret("VerySecret$");
		Set<HybrisConstraintViolation> constraintViolations3 = validationService.validate(punchOutCredentialModel3);
		assertEquals("shared secret should include one more digital num", 1, constraintViolations3.size());
		assertThat(constraintViolations3, hasItem(hasProperty(FIELD_MESSAGE, is(REGX_MESSAGE))));
	}

	@Test
	public void validateSharedSecretOnDenyListFile()
	{
		this.i18NService.setCurrentLocale(Locale.ENGLISH);
		final PunchOutCredentialModel punchOutCredentialModel = modelService.create(PunchOutCredentialModel.class);
		punchOutCredentialModel.setSharedsecret("secretTest123$");
		Set<HybrisConstraintViolation> constraintViolations = validationService.validate(punchOutCredentialModel);
		assertEquals("shared secret should include special character", 1, constraintViolations.size());
		assertThat(constraintViolations, hasItem(hasProperty(FIELD_MESSAGE, is(DENYLIST_MESSAGE))));
	}

	@Test
	public void validateSharedSecretOnDenyListFileAndPattern() throws ImpExException
	{
		this.i18NService.setCurrentLocale(Locale.ENGLISH);
		final PunchOutCredentialModel punchOutCredentialModel = modelService.create(PunchOutCredentialModel.class);
		punchOutCredentialModel.setSharedsecret("123456");
		Set<HybrisConstraintViolation> constraintViolations = validationService.validate(punchOutCredentialModel);
		assertEquals("shared secret should include special character", 2, constraintViolations.size());
		assertThat(constraintViolations, hasItem(hasProperty(FIELD_MESSAGE, is(DENYLIST_MESSAGE))));
		assertThat(constraintViolations, hasItem(hasProperty(FIELD_MESSAGE, is(REGX_MESSAGE))));
	}

	@Test
	public void validateSharedSecretNotSetDenyListFile()
	{
		this.i18NService.setCurrentLocale(Locale.ENGLISH);
		Config.setParameter("b2bpunchout.sharedsecret.denylist.file", "");
		final PunchOutCredentialModel punchOutCredentialModel = modelService.create(PunchOutCredentialModel.class);
		punchOutCredentialModel.setSharedsecret("123456");
		Set<HybrisConstraintViolation> constraintViolations = validationService.validate(punchOutCredentialModel);
		assertEquals("shared secret should include special character", 1, constraintViolations.size());
		assertThat(constraintViolations, hasItem(hasProperty(FIELD_MESSAGE, is(REGX_MESSAGE))));
	}
}
