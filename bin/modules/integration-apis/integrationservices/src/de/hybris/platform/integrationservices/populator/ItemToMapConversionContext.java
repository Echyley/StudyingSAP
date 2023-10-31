/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.populator;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.integrationservices.model.KeyDescriptor;
import de.hybris.platform.integrationservices.model.TypeDescriptor;

import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

/**
 * This class encapsulates the context of a conversion from a POJO to a {@link java.util.Map}
 */
public class ItemToMapConversionContext
{
	private final Object payloadObject;
	private final TypeDescriptor typeDescriptor;
	private final ItemToMapConversionContext parentContext;
	private Map<String, Object> conversionResult;

	/**
	 * Instantiates a top level context. The top level context does not have parent context associated with it.
	 *
	 * @param data item being converted.
	 * @param metadata integration object item descriptor defining the structure of the conversion result, e.g. if the {@code data} is
	 * being converted to a {@code Map}, then what attributes the map should have.
	 * @see #getParentContext()
	 * @deprecated use {@code #ItemToMapConversionContext(final Object payloadObject, final TypeDescriptor metadata)}
	 */
	@Deprecated(since = "2211.FP1", forRemoval = true)
	public ItemToMapConversionContext(final ItemModel data, final TypeDescriptor metadata)
	{
		this((Object) data, metadata);
	}

	/**
	 * Instantiates a top level context. The top level context does not have parent context associated with it.
	 *
	 * @param payloadObject payload object being converted. It can be {@link ItemModel} or any POJO.
	 * @param metadata integration object item descriptor defining the structure of the conversion result, e.g. if the {@code data} is
	 * being converted to a {@code Map}, then what attributes the map should have.
	 * @see #getParentContext()
	 */
	public ItemToMapConversionContext(final Object payloadObject, final TypeDescriptor metadata)
	{
		this(payloadObject, metadata, null);
	}

	private ItemToMapConversionContext(final Object payloadObject, final TypeDescriptor descriptor, final ItemToMapConversionContext parent)
	{

		Preconditions.checkArgument(payloadObject != null, "Payload is required for this context");
		Preconditions.checkArgument(descriptor != null, "TypeDescriptor is required for this context");
		Preconditions.checkArgument(descriptor.isInstance(payloadObject),"Payload %s does not match the integration object type definition %s",payloadObject, descriptor);

		this.payloadObject = payloadObject;
		typeDescriptor = descriptor;
		parentContext = parent;
	}

	/**
	 * Retrieves the item being converted.
	 *
	 * @return item being converted.
	 * @deprecated use {@link #getPayloadObject()}
	 */
	@Deprecated(since = "2211.FP1", forRemoval = true)
	public ItemModel getItemModel()
	{
		return getPayloadObject() instanceof ItemModel item ? item : null;
	}

	/**
	 * This will return the payload object according to the integration object specs. It can be traditional {@link ItemModel} or
	 * any POJO.
	 *
	 * @return the payload object.
	 */
	public Object getPayloadObject()
	{
		return payloadObject;
	}

	/**
	 * Retrieves the integration object item descriptor associated with the conversion.
	 *
	 * @return metadata describing the structure of the conversion result.
	 */
	public TypeDescriptor getTypeDescriptor()
	{
		return typeDescriptor;
	}

	/**
	 * Retrieves parent context.
	 *
	 * @return conversion context from which this context was created or {@code null}, if the context was created independently by
	 * the constructor.
	 * @see #createSubContext(Object, TypeDescriptor) 
	 */
	protected ItemToMapConversionContext getParentContext()
	{
		return parentContext;
	}

	/**
	 * Creates sub-context from this context.
	 *
	 * @param payloadObject pojo to be used in the context to create
	 * @param descriptor integration object item descriptor to be used in the context to create
	 * @return a new conversion context with current context as its parent.
	 */
	public ItemToMapConversionContext createSubContext(final Object payloadObject, final TypeDescriptor descriptor)
	{
		return new ItemToMapConversionContext(payloadObject, descriptor, this);
	}

	/**
	 * Creates sub-context from this context.
	 *
	 * @param itemModel  data item to be used in the context to create
	 * @param descriptor integration object item descriptor to be used in the context to create
	 * @return a new conversion context with current context as its parent
	 * @deprecated use {@link #createSubContext(Object, TypeDescriptor)} instead
	 */
	@Deprecated(since = "2211.FP1", forRemoval = true)
	public ItemToMapConversionContext createSubContext(final ItemModel itemModel, final TypeDescriptor descriptor)
	{
		return new ItemToMapConversionContext(itemModel, descriptor, this);
	}

	/**
	 * Sets conversion result for the item in this context.
	 *
	 * @param result map, to which the {@link #getItemModel()} was converted.
	 */
	public void setConversionResult(final Map<String, Object> result)
	{
		conversionResult = result;
	}

	/**
	 * Retrieves a conversion result for the item in this context. Because this method is used to retrieve result of current
	 * item conversion in the parent contexts, so that it can be injected to a nested item, this method returns only a map
	 * containing only key attribute values of the previously converted item. Without limiting the conversion result to key attributes
	 * there is a risk of causing an infinite loop in the conversion result structure.
	 * <p>Consider this. We need to convert an Order, which has code attribute (its key) and a collection of OrderEntry items. Each
	 * OrderEntry has a reference back to the Order it belongs to, some entryNumber attribute, which makes the entry unique when
	 * combined with the Order, and some other meaningful attributes, e.g. Product the line is associated with, quantity (how many
	 * products purchased), etc. During conversion when we will get to OrderEntry conversion and will attempt to convert its
	 * {@code order} attribute value we will detect that the Order has been converted already into a map. However, if we just take
	 * that map "as is" it will contain the {@code orderEntries}, each OrderEntry refers to the Order again and thus an infinite
	 * loop is formed. For that reason, for OrderEntries we include Order map with key attribute values only, i.e. {@code code}
	 * attribute.</p>
	 *
	 * @return a map containing key attribute values for the previous conversion result that was added to this context or to the
	 * parent(s) of this context. If the specified item was not converted yet, then {@code null} is returned.
	 */
	public Map<String, Object> getConversionResult()
	{
		final Map<String, Object> result = lookupConversionResult(payloadObject);
		return result == null
				? result
				: extractKeyAttributesOnly(result);
	}

	private Map<String, Object> lookupConversionResult(final Object item)
	{
		return payloadObject.equals(item) && conversionResult != null
				? conversionResult
				: lookupConversionResultInParent(item);
	}

	private Map<String, Object> lookupConversionResultInParent(final Object item)
	{
		return parentContext != null
				? parentContext.lookupConversionResult(item)
				: null;
	}

	private Map<String, Object> extractKeyAttributesOnly(final Map<String, Object> conversionResult)
	{
		final KeyDescriptor keyDesc = getTypeDescriptor().getKeyDescriptor();
		return conversionResult.entrySet().stream()
				.filter(entry -> keyDesc.isKeyAttribute(entry.getKey()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	@Override
	public String toString()
	{
		return "ItemToMapConversionContext{" +
				"payloadObject=" + payloadObject +
				", type=" + typeDescriptor +
				(parentContext != null ? (", parent=" + parentContext) : "") +
				'}';
	}
}
