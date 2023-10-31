package de.hybris.platform.test;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.hmc.jalo.UserProfile;
import de.hybris.platform.jalo.ConsistencyCheckException;
import de.hybris.platform.jalo.c2l.Language;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.jalo.user.UserGroup;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.internal.jalo.ServicelayerManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.Test;

@IntegrationTest
public class UserProfileTest extends ServicelayerBaseTest
{
	ServicelayerManager servicelayerManager = ServicelayerManager.getInstance();
	private final String ISOCODE_EN = "en";
	private final String ISOCODE_DE = "de";

	@Test
	public void testUserGetsWriteableRightsToTheReadableLanguageWhenNoWriteableOneWasSpecified()
			throws ConsistencyCheckException
	{
		final Language languageEn = getOrCreateLanguage(ISOCODE_EN);
		final UserWithGroupStructure user = new UserWithGroupStructure()
				.withGroups(1)
				.withReadableLanguages(0, Set.of(languageEn));

		assertThat(user.getUserProfile().getAllReadableLanguages()).contains(languageEn);
		assertThat(user.getUserProfile().getAllWritableLanguages()).contains(languageEn);
		assertThat(user.getUserProfile().getAllWritableLanguages()).containsExactlyElementsOf(
				user.getUserProfile().getAllReadableLanguages());
	}

	@Test
	public void testUserGetsWriteableRightsToTheReadableLanguageWhenNoWriteableOneWasSpecifiedAndDenyWriteFlagExplicitlyFalse()
			throws ConsistencyCheckException
	{
		final Language languageEn = getOrCreateLanguage(ISOCODE_EN);
		final UserWithGroupStructure user = new UserWithGroupStructure()
				.withGroups(1)
				.withReadableLanguages(0, Set.of(languageEn))
				.withDenyFlag(0, false);

		assertThat(user.getUserProfile().getAllReadableLanguages()).contains(languageEn);
		assertThat(user.getUserProfile().getAllWritableLanguages()).contains(languageEn);
		assertThat(user.getUserProfile().getAllWritableLanguages()).containsExactlyElementsOf(
				user.getUserProfile().getAllReadableLanguages());
	}

	@Test
	public void testUserDoesntGetReadableLanguageAsWriteableWhenADifferentOneIsSpecified() throws ConsistencyCheckException
	{
		final Language languageEn = getOrCreateLanguage(ISOCODE_EN);
		final Language languageDe = getOrCreateLanguage(ISOCODE_DE);
		final UserWithGroupStructure user = new UserWithGroupStructure()
				.withGroups(1)
				.withReadableLanguages(0, Set.of(languageEn))
				.withWriteableLanguages(0, Set.of(languageDe));

		assertThat(user.getUserProfile().getAllReadableLanguages()).contains(languageEn);
		assertThat(user.getUserProfile().getAllWritableLanguages()).contains(languageDe);
		assertThat(user.getUserProfile().getAllWritableLanguages()).doesNotContainAnyElementsOf(
				user.getUserProfile().getAllReadableLanguages());
	}

	@Test
	public void testUserDoesntGetReadableLanguageAsWriteableWhenADifferentOneIsSpecifiedAndDenyFlagExplicitlyFalse()
			throws ConsistencyCheckException
	{
		final Language languageEn = getOrCreateLanguage(ISOCODE_EN);
		final Language languageDe = getOrCreateLanguage(ISOCODE_DE);
		final UserWithGroupStructure user = new UserWithGroupStructure()
				.withGroups(1)
				.withReadableLanguages(0, Set.of(languageEn))
				.withWriteableLanguages(0, Set.of(languageDe))
				.withDenyFlag(0, false);

		assertThat(user.getUserProfile().getAllReadableLanguages()).contains(languageEn);
		assertThat(user.getUserProfile().getAllWritableLanguages()).contains(languageDe);
		assertThat(user.getUserProfile().getAllWritableLanguages()).doesNotContainAnyElementsOf(
				user.getUserProfile().getAllReadableLanguages());
	}

	@Test
	public void testUserDoesntGetAnyWriteableWhenDenyWritePermissionForAllLanguagesSetToTrue() throws ConsistencyCheckException
	{
		final Language languageEn = getOrCreateLanguage(ISOCODE_EN);
		final UserWithGroupStructure user = new UserWithGroupStructure()
				.withGroups(1)
				.withReadableLanguages(0, Set.of(languageEn))
				.withDenyFlag(0, true);

		assertThat(user.getUserProfile().getAllReadableLanguages()).contains(languageEn);
		assertThat(user.getUserProfile().getAllWritableLanguages()).isEmpty();
	}

	@Test
	public void testDenyWritePermissionInOneGroupDoesntInterfereWithLanguagesDefinedInOtherGroups()
			throws ConsistencyCheckException
	{
		final Language languageEn = getOrCreateLanguage(ISOCODE_EN);
		final Language languageDe = getOrCreateLanguage(ISOCODE_DE);
		final UserWithGroupStructure user = new UserWithGroupStructure()
				.withGroups(2)
				.withReadableLanguages(0, Set.of(languageEn))
				.withDenyFlag(0, true)
				.withReadableLanguages(1, Set.of(languageDe))
				.withWriteableLanguages(1, Set.of(languageDe));

		assertThat(user.getUserProfile().getAllReadableLanguages()).containsExactlyInAnyOrder(languageEn, languageDe);
		assertThat(user.getUserProfile().getAllWritableLanguages()).contains(languageDe);
		assertThat(user.getUserProfile().getAllWritableLanguages()).doesNotContain(languageEn);
	}

	@Test
	public void testUserGetsWritePermissionToAllDefinedLanguagesWhenOneGroupDeniesWriteToAllAndOtherDoesNotDefineAnyLanguage()
			throws ConsistencyCheckException
	{
		final Language languageEn = getOrCreateLanguage(ISOCODE_EN);
		final Language languageDe = getOrCreateLanguage(ISOCODE_DE);
		final UserWithGroupStructure user = new UserWithGroupStructure()
				.withGroups(2)
				.withReadableLanguages(0, Set.of(languageEn))
				.withDenyFlag(0, true)
				.withReadableLanguages(1, Set.of(languageDe));

		assertThat(user.getUserProfile().getAllReadableLanguages()).containsExactlyInAnyOrder(languageEn, languageDe);
		assertThat(user.getUserProfile().getAllWritableLanguages()).containsExactlyInAnyOrder(languageEn, languageDe);
	}

	@Test
	public void testUserHaveNoWritePermissionWhenMultipleGroupsDefineDenyWritePermissionForAllLanguages()
			throws ConsistencyCheckException
	{
		final Language languageEn = getOrCreateLanguage(ISOCODE_EN);
		final Language languageDe = getOrCreateLanguage(ISOCODE_DE);
		final UserWithGroupStructure user = new UserWithGroupStructure()
				.withGroups(2)
				.withReadableLanguages(0, Set.of(languageEn))
				.withDenyFlag(0, true)
				.withReadableLanguages(1, Set.of(languageDe))
				.withDenyFlag(1, true);

		assertThat(user.getUserProfile().getAllReadableLanguages()).containsExactlyInAnyOrder(languageEn, languageDe);
		assertThat(user.getUserProfile().getAllWritableLanguages()).isEmpty();
	}

	// boilerplate code for user & group creation
	private class UserWithGroupStructure
	{
		private final List<UserGroup> groups;
		private final User user;

		private UserWithGroupStructure() throws ConsistencyCheckException
		{
			user = UserManager.getInstance().createEmployee(UUID.randomUUID().toString());
			final UserProfile userProfile = servicelayerManager.getOrCreateUserProfile();
			userProfile.setOwner(user);
			user.setUserprofile(userProfile);
			groups = new ArrayList<>();
		}

		private UserWithGroupStructure withGroups(final int count) throws ConsistencyCheckException
		{
			groups.clear();
			for (int i = 0; i < count; i++)
			{
				groups.add(UserManager.getInstance().createUserGroup(UUID.randomUUID().toString()));
			}
			user.setGroups(new HashSet<>(groups));
			return this;
		}

		private UserWithGroupStructure withReadableLanguages(final int groupIndex, final Set<Language> languages)
		{
			groups.get(groupIndex).setReadableLanguages(languages);
			return this;
		}

		private UserWithGroupStructure withWriteableLanguages(final int groupIndex, final Set<Language> languages)
		{
			groups.get(groupIndex).setWriteableLanguages(languages);
			return this;
		}

		private UserWithGroupStructure withDenyFlag(final int groupIndex, final Boolean flagValue)
		{
			groups.get(groupIndex).setDenyWritePermissionForAllLanguages(flagValue);
			return this;
		}

		private UserProfile getUserProfile()
		{
			return user.getUserprofile();
		}
	}
}
