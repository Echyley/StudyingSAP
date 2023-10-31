/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.daos;

import de.hybris.platform.b2b.punchout.jalo.PunchOutCredential;
import de.hybris.platform.b2b.punchout.model.PunchOutCredentialModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;

import java.util.List;


/**
 * DAO for entity {@link PunchOutCredential}.
 */
public interface PunchOutCredentialDao
{
	/**
	 * Get a {@link PunchOutCredentialModel} for a given domain-identity pair.
	 * 
	 * @param domain
	 *           The PunchOut domain used for the identity (e.g.: DUNS)
	 * @param identity
	 *           The identity value.
	 * @return The {@link PunchOutCredentialModel}, or null, if there is no matching pair.
	 * @throws AmbiguousIdentifierException
	 *            If there is more the one {@link PunchOutCredentialModel} for the given pair.
	 */
	PunchOutCredentialModel getPunchOutCredential(final String domain, final String identity) throws AmbiguousIdentifierException;

	/**
	 * Get the expired credentials that shared secret has not changed for given days.
	 * @param expiredDays
	 *           The given days that shared secret has not been changed.
	 * @return A bundle of {@link PunchOutCredentialModel} or empty list, if there is not any expired credentials.
	 */
	List<PunchOutCredentialModel> getExpiredPunchOutCredentials(final int expiredDays);
}
