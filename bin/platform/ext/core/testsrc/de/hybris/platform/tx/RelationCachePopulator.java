/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.tx;

import de.hybris.platform.core.PK;

import java.util.List;

public interface RelationCachePopulator
{
	PK createSourceItem();

	PK createTargetItem(PK sourcePK);

	List<PK> getTargetSide(PK sourcePK);

	boolean deleteItem(PK sourcePK);

	boolean deleteTargetSideItems(PK sourcePK);

	boolean deleteRelationInstanceNonPersistenceLayer(PK sourcePK, PK targetPK);

	boolean deleteRelationInstancePersistenceLayer(PK sourcePK, PK targetPK);
}
