/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cache.relation;

import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.MapAssert.entry;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.constants.CoreConstants;
import de.hybris.platform.core.Constants;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.type.SearchRestrictionModel;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.link.Link;
import de.hybris.platform.jalo.security.Principal;
import de.hybris.platform.jalo.security.PrincipalGroup;
import de.hybris.platform.persistence.EJBItemNotFoundException;
import de.hybris.platform.persistence.SystemEJB;
import de.hybris.platform.persistence.type.ComposedTypeRemote;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.testframework.TestUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;

@IntegrationTest
public class TypeSystemRelationAttributesProviderIntegrationTest extends ServicelayerBaseTest
{
	@Test
	public void shouldNotFailOnUnknownTypeCode()
	{
		final TypeSystemRelationAttributesProvider provider = givenProvider();
		final PK typePk = newPk(findUnusedTypeCode());

		TestUtils.disableFileAnalyzer("Expecting exception in the logs.", 256);
		final RelationAttributes attributes = provider.get(typePk);

		assertThat(attributes.containsAnyAttribute()).isFalse();
	}

	@Test
	public void shouldNotFailOnUnknownType()
	{
		final TypeSystemRelationAttributesProvider provider = givenProvider();
		final PK typePk = newPk(getComposedTypeTypeCode());

		TestUtils.disableFileAnalyzer("Expecting exception in the logs.", 256);
		final RelationAttributes attributes = provider.get(typePk);

		assertThat(attributes.containsAnyAttribute()).isFalse();
	}

	@Test
	public void shouldNotFailOnNotTypePk()
	{
		final TypeSystemRelationAttributesProvider provider = givenProvider();
		final PK typePk = JaloSession.getCurrentSession().getUser().getPK();

		final RelationAttributes attributes = provider.get(typePk);

		assertThat(attributes.containsAnyAttribute()).isFalse();
	}

	@Test
	public void shouldReturnSourceAndTargetForManyToManyRelationType()
	{
		final TypeSystemRelationAttributesProvider provider = new TypeSystemRelationAttributesProvider();
		final PK typePk = getTypePk(CoreConstants.Relations.PRINCIPALGROUPRELATION);

		final RelationAttributes attributes = provider.get(typePk);

		assertThat(attributes.containsAnyAttribute()).isTrue();
		assertThat(attributes.getOwningTypeCode()).isEqualTo(CoreConstants.Relations.PRINCIPALGROUPRELATION);
		final Map<String, String> fkToMany = getFkToManyMap(attributes);
		assertThat(fkToMany).includes(entry(Link.TARGET, PrincipalGroup.MEMBERS), entry(Link.SOURCE, Principal.GROUPS));
	}

	@Test
	public void shouldReturnForeignKeysForComposedType()
	{
		final TypeSystemRelationAttributesProvider provider = new TypeSystemRelationAttributesProvider();
		final PK typePk = getTypePk(SearchRestrictionModel._TYPECODE);

		final RelationAttributes attributes = provider.get(typePk);

		assertThat(attributes.containsAnyAttribute()).isTrue();
		assertThat(attributes.getOwningTypeCode()).isEqualTo(SearchRestrictionModel._TYPECODE);
		final Map<String, String> fkToMany = getFkToManyMap(attributes);
		assertThat(fkToMany).includes(entry(SearchRestrictionModel.PRINCIPAL, PrincipalModel.SEARCHRESTRICTIONS));
	}

	private Map<String, String> getFkToManyMap(final RelationAttributes attributes)
	{
		final Map<String, String> result = new HashMap<>();
		attributes.forEachAttribute(result::put);
		return result;
	}

	private PK getTypePk(final String code)
	{
		try
		{
			return SystemEJB.getInstance().getTypeManager().getType(code).getPK();
		}
		catch (final EJBItemNotFoundException e)
		{
			throw new RuntimeException(e);
		}
	}

	private PK newPk(final int typeCode)
	{
		return PK.createCounterPK(typeCode);
	}

	private int getComposedTypeTypeCode()
	{
		try
		{
			return SystemEJB.getInstance().getTypeManager().getComposedType(Constants.TYPES.ComposedType).getItemTypeCode();
		}
		catch (final EJBItemNotFoundException e)
		{
			throw new RuntimeException(e);
		}
	}

	private int findUnusedTypeCode()
	{
		final Collection<ComposedTypeRemote> allTypes = SystemEJB.getInstance().getTypeManager().getAllItemTypes();
		final Set<Integer> allTypeCodes = allTypes.stream()
		                                          .map(ComposedTypeRemote::getItemTypeCode)
		                                          .collect(Collectors.toSet());
		for (int i = 1; i < 100_000; i++)
		{
			if (!allTypeCodes.contains(i))
			{
				return i;
			}
		}

		throw new IllegalStateException("Couldn't find any unused type code.");
	}

	private TypeSystemRelationAttributesProvider givenProvider()
	{
		return new TypeSystemRelationAttributesProvider();
	}

}