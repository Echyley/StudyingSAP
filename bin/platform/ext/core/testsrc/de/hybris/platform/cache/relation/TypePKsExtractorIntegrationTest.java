/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cache.relation;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.persistence.EJBItemNotFoundException;
import de.hybris.platform.persistence.SystemEJB;
import de.hybris.platform.persistence.type.ComposedTypeRemote;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.Test;

@IntegrationTest
public class TypePKsExtractorIntegrationTest extends ServicelayerBaseTest
{
	@Test
	public void shouldReturnEmptyCollectionOnIgnoredTypeCode()
	{
		final TypePKsExtractor extractor = givenExtractor(123, 456);

		final Collection<PK> pks123 = extractor.extractTypePKs(TypeId.fromTypeCode(123));
		assertThat(pks123).isEmpty();

		final Collection<PK> pks456 = extractor.extractTypePKs(TypeId.fromTypeCode(456));
		assertThat(pks456).isEmpty();
	}

	@Test
	public void shouldReturnEmptyCollectionOnUnusedTypeCode()
	{
		final TypePKsExtractor extractor = givenExtractor();
		final int unusedTypeCode = findUnusedTypeCode();

		final Collection<PK> pks = extractor.extractTypePKs(TypeId.fromTypeCode(unusedTypeCode));
		assertThat(pks).isEmpty();
	}

	@Test
	public void shouldReturnEmptyCollectionOnUnknownTypeName()
	{
		final TypePKsExtractor extractor = givenExtractor();

		final Collection<PK> pks = extractor.extractTypePKs(TypeId.fromTypeName(uniqueString()));
		assertThat(pks).isEmpty();
	}

	@Test
	public void shouldReturnTypePkWhenTypPkIsAvailable()
	{
		final TypePKsExtractor extractor = givenExtractor();
		final PK typePk = PK.createCounterPK(123);

		final Collection<PK> pks = extractor.extractTypePKs(TypeId.fromTypePk(typePk));
		assertThat(pks).containsExactly(typePk);
	}

	@Test
	public void shouldReturnSubtypesForAGivenTypeCode()
	{
		final TypePKsExtractor extractor = givenExtractor();
		final int typeCode = getComposedType(UserModel._TYPECODE).getItemTypeCode();
		final PK rootPk = getComposedType(UserModel._TYPECODE).getPK();
		final PK subPk1 = getComposedType(CustomerModel._TYPECODE).getPK();
		final PK subPk2 = getComposedType(EmployeeModel._TYPECODE).getPK();

		final Collection<PK> pks = extractor.extractTypePKs(TypeId.fromTypeCode(typeCode));
		assertThat(pks).isNotEmpty().contains(rootPk, subPk1, subPk2);
	}

	@Test
	public void shouldReturnOnlyOnePkWhenTypeNameIsGiven()
	{
		final TypePKsExtractor extractor = givenExtractor();
		final String rootTypeName = UserModel._TYPECODE;
		final PK rootPk = getComposedType(rootTypeName).getPK();
		final String subTypeName = CustomerModel._TYPECODE;
		final PK subPk = getComposedType(subTypeName).getPK();

		final Collection<PK> pksRoot = extractor.extractTypePKs(TypeId.fromTypeName(rootTypeName));
		assertThat(pksRoot).isNotEmpty().containsExactly(rootPk);

		final Collection<PK> pksSub = extractor.extractTypePKs(TypeId.fromTypeName(subTypeName));
		assertThat(pksSub).isNotEmpty().containsExactly(subPk);
	}

	private ComposedTypeRemote getComposedType(final String code)
	{
		try
		{
			return SystemEJB.getInstance().getTypeManager().getComposedType(code);
		}
		catch (final EJBItemNotFoundException e)
		{
			throw new IllegalStateException("Unexpected", e);
		}
	}

	private int findUnusedTypeCode()
	{
		final Collection<?> allTypes = SystemEJB.getInstance().getTypeManager().getAllItemTypes();
		final Set<Integer> allTypeCodes = allTypes.stream()
		                                          .filter(ComposedTypeRemote.class::isInstance)
		                                          .map(ComposedTypeRemote.class::cast)
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

	private String uniqueString()
	{
		return UUID.randomUUID().toString();
	}

	private TypePKsExtractor givenExtractor(final Integer... typeCodesToExclude)
	{
		return new TypePKsExtractor(Set.of(typeCodesToExclude));
	}
}