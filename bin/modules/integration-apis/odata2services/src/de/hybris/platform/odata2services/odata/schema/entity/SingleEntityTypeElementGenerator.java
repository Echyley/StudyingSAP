/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.schema.entity;

import de.hybris.platform.integrationservices.model.TypeDescriptor;
import de.hybris.platform.integrationservices.util.Log;
import de.hybris.platform.odata2services.odata.persistence.exception.MissingKeyException;
import de.hybris.platform.odata2services.odata.schema.KeyGenerator;
import de.hybris.platform.odata2services.odata.schema.SchemaElementGenerator;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.apache.olingo.odata2.api.edm.provider.EntityType;
import org.apache.olingo.odata2.api.edm.provider.Key;
import org.apache.olingo.odata2.api.edm.provider.Property;
import org.slf4j.Logger;

import com.google.common.base.Preconditions;

/**
 * Base implementation class for entity type generators, which generate either a single entity type or no entity types depending
 * on certain conditions.
 */
public abstract class SingleEntityTypeElementGenerator implements SchemaElementGenerator<Set<EntityType>, TypeDescriptor>
{
	private static final Logger LOG = Log.getLogger(SingleEntityTypeElementGenerator.class);
	private final KeyGenerator keyGenerator;
	private final SchemaElementGenerator<List<Property>, TypeDescriptor> propertiesGenerator;

	/**
	 * Instantiates a new single entity type generator.
	 *
	 * @param keyGenerator a key generator
	 * @param propertiesGenerator a property generator
	 */
	protected SingleEntityTypeElementGenerator(@NotNull final KeyGenerator keyGenerator,
	                                           @NotNull final SchemaElementGenerator<List<Property>, TypeDescriptor> propertiesGenerator)
	{
		Preconditions.checkArgument(keyGenerator != null, "keyGenerator must not be null");
		Preconditions.checkArgument(propertiesGenerator != null, "propertiesGenerator must not be null");

		this.keyGenerator = keyGenerator;
		this.propertiesGenerator = propertiesGenerator;
	}

	/**
	 * {@inheritDoc}
	 * Implementation checks whether this generator is applicable to the specified item by calling {@link #isApplicable(TypeDescriptor)}
	 * and depending on the result proceeds to {@link #generateEntityType(TypeDescriptor)} or returns an empty set.
	 *
	 * @param typeDescriptor an item type descriptor, for which an EDMX entity type has to be generated
	 * @return a list with a single generated entity type or an empty set, if this generator is not {@code isApplicable()}.
	 */
	@Override
	public Set<EntityType> generate(@NotNull final TypeDescriptor typeDescriptor)
	{
		return isApplicable(typeDescriptor)
				? Collections.singleton(generateEntityType(typeDescriptor))
				: Collections.emptySet();
	}

	/**
	 * Determines whether this generator is applicable to the specified item and can generate at least a single EDMX entity type.
	 *
	 * @param typeDescriptor an item type descriptor, based on which the decision has to be made.
	 * @return {@code true}, if at least one entity type can be generated for the given item; {@code false}, otherwise.
	 */
	protected abstract boolean isApplicable(TypeDescriptor typeDescriptor);

	/**
	 * Generates a single entity type for the specified item delegating the entity parts creation to:
	 * <ul>
	 *     <li>{@link #generateEntityTypeName(TypeDescriptor)}</li> for the entity type name generation
	 *     <li>{@link #propertiesGenerator}</li> for the entity type properties generation
	 *     <li>{@link #keyGenerator}</li> for the entity type key generation. If the key is not generated, i.e. {@code !Optional<Key>.isPresent()},
	 *     an {@code IllegalStateException} is thrown.
	 * </ul>
	 *
	 * @param typeDescriptor an item type descriptor to generate the EDMX entity type for.
	 * @return the generated entity type.<br/>
	 * <b>Subclasses</b> make sure never return {@code null} from this method. If item cannot be generated, then
	 * {@link #isApplicable(TypeDescriptor)} should return {@code false} instead.
	 */
	protected EntityType generateEntityType(final TypeDescriptor typeDescriptor)
	{
		final String name = generateEntityTypeName(typeDescriptor);
		LOG.debug("Generating {} EntityType", name);

		final List<Property> properties;
		properties = propertiesGenerator.generate(typeDescriptor);

		final Key key = generateKey(name, properties);

		return new EntityType()
				.setName(name)
				.setProperties(properties)
				.setKey(key);
	}

	protected Key generateKey(final String name, final List<Property> properties)
	{
		final Optional<Key> key = keyGenerator.generate(properties);

		return key.orElseThrow(() -> new MissingKeyException(name));
	}

	/**
	 * Generates name for the entity type being generated.
	 *
	 * @param typeDescriptor an item type descriptor, for which entity type is being generated.
	 * @return a valid EDMX entity type name.
	 */
	protected abstract String generateEntityTypeName(TypeDescriptor typeDescriptor);
}
