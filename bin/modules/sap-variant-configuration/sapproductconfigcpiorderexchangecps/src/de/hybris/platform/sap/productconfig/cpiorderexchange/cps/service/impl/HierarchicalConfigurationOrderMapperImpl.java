/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.cpiorderexchange.cps.service.impl;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.productconfig.cpiorderexchange.cps.service.MappingDecisionStrategy;
import de.hybris.platform.sap.productconfig.cpiorderexchange.model.SAPCpiOutboundOrderS4hcConfigHeaderModel;
import de.hybris.platform.sap.productconfig.cpiorderexchange.model.SAPCpiOutboundOrderS4hcConfigStructureNodeModel;
import de.hybris.platform.sap.productconfig.cpiorderexchange.model.SAPCpiOutboundOrderS4hcConfigValuationModel;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSCommerceExternalConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalCharacteristic;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalItem;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalValue;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderItemModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderModel;
import de.hybris.platform.sap.sapcpiorderexchange.service.SapCpiOrderMapperService;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;


public class HierarchicalConfigurationOrderMapperImpl implements SapCpiOrderMapperService<OrderModel, SAPCpiOutboundOrderModel>
{
	protected static final String AUTHOR_EXTERNAL = "X";
	protected static final String AUTHOR_USER = "";
	protected static final String TARGET_AUTHOR_USER = "";
	protected static final String TARGET_AUTHOR_EXTERNAL = "CONST";
	private ObjectMapper objectMapper = null;
	private MappingDecisionStrategy mappingDecisionStrategy;
	private boolean disabledAuthorCheck = false;

	protected boolean isDisabledAuthorCheck()
	{
		return disabledAuthorCheck;
	}

	/**
	 * Don't inject per constructor as this would cause an incompatible change
	 *
	 * @param mappingDecisionStrategy
	 *           the mappingDecisionStrategy to set
	 */
	public void setMappingDecisionStrategy(final MappingDecisionStrategy mappingDecisionStrategy)
	{
		this.mappingDecisionStrategy = mappingDecisionStrategy;
	}

	@Override
	public void map(final OrderModel source, final SAPCpiOutboundOrderModel target)
	{

		source.getEntries().stream().forEach(entry -> mapEntry(entry, target));
	}

	protected void mapEntry(final AbstractOrderEntryModel entry, final SAPCpiOutboundOrderModel target)
	{
		if (entry.getExternalConfiguration() != null && getMappingDecisionStrategy().isA2AServiceMappingActive(entry.getOrder()))
		{
			final SAPCpiOutboundOrderItemModel targetEntry = findTargetEntry(entry, target);
			targetEntry.setS4hcConfigHeader(createConfiguration(entry.getExternalConfiguration(), targetEntry));
		}
	}


	protected SAPCpiOutboundOrderS4hcConfigHeaderModel createConfiguration(final String externalConfiguration,
			final SAPCpiOutboundOrderItemModel targetEntry)
	{
		final SAPCpiOutboundOrderS4hcConfigHeaderModel configurationHeader = new SAPCpiOutboundOrderS4hcConfigHeaderModel();
		configurationHeader.setSapCpiS4hcConfigStructureNodes(new HashSet<>());
		configurationHeader.setSapCpiS4hcConfigValuations(new HashSet<>());
		configurationHeader.setEntryNumber(targetEntry.getEntryNumber());
		configurationHeader.setOrderId(targetEntry.getOrderId());
		final CPSCommerceExternalConfiguration configuration = parseConfiguration(externalConfiguration);
		final CPSExternalItem rootItem = configuration.getExternalConfiguration().getRootItem();
		addCharacteristics(rootItem, configurationHeader, configurationHeader.getSapCpiS4hcConfigValuations());
		addSubItems(configurationHeader, rootItem.getSubItems(), rootItem, rootItem.getId());
		return configurationHeader;
	}

	protected void addSubItems(final SAPCpiOutboundOrderS4hcConfigHeaderModel configurationHeader,
			final List<CPSExternalItem> subItems, final CPSExternalItem parentItem, final String rootItemId)
	{
		if (subItems != null)
		{
			subItems.forEach(subItem -> addSubItem(configurationHeader, subItem, parentItem, rootItemId));
		}

	}

	protected void addCharacteristics(final CPSExternalItem item,
			final SAPCpiOutboundOrderS4hcConfigHeaderModel configurationHeader,
			final Set<SAPCpiOutboundOrderS4hcConfigValuationModel> valuations)
	{
		if (item.getCharacteristics() != null)
		{
			item.getCharacteristics()
					.forEach(cstic -> addCharacteristicValues(cstic, configurationHeader, valuations, item.getId()));
		}
	}

	protected void addSubItem(final SAPCpiOutboundOrderS4hcConfigHeaderModel configurationHeader, final CPSExternalItem subItem,
			final CPSExternalItem parentItem, final String rootItemId)
	{
		if (!isLeafWithoutCharacteristics(subItem))
		{
			final SAPCpiOutboundOrderS4hcConfigStructureNodeModel structureNode = new SAPCpiOutboundOrderS4hcConfigStructureNodeModel();
			structureNode.setSapCpiS4hcConfigValuations(new HashSet());
			structureNode.setNodeId(subItem.getId());
			if (!rootItemId.equals(parentItem.getId()))
			{
				structureNode.setParentNodeId(parentItem.getId());
			}
			structureNode.setBomPositionIdentifier(subItem.getBomPositionIdentifier());
			structureNode.setOrderId(configurationHeader.getOrderId());
			structureNode.setEntryNumber(configurationHeader.getEntryNumber());

			addCharacteristics(subItem, configurationHeader, structureNode.getSapCpiS4hcConfigValuations());
			configurationHeader.getSapCpiS4hcConfigStructureNodes().add(structureNode);
			addSubItems(configurationHeader, subItem.getSubItems(), subItem, rootItemId);
		}
	}

	protected boolean isLeafWithoutCharacteristics(final CPSExternalItem subItem)
	{

		return CollectionUtils.isEmpty(subItem.getSubItems()) && CollectionUtils.isEmpty(subItem.getCharacteristics());
	}

	protected void addCharacteristicValues(final CPSExternalCharacteristic cstic,
			final SAPCpiOutboundOrderS4hcConfigHeaderModel configurationHeader,
			final Set<SAPCpiOutboundOrderS4hcConfigValuationModel> valuations, final String itemId)
	{
		final String csticName = cstic.getId();
		final List<CPSExternalValue> values = cstic.getValues();
		if (values != null)
		{
			values.forEach(value -> addCharacteristicValue(value, csticName, configurationHeader, valuations, itemId));
		}
	}

	protected void addCharacteristicValue(final CPSExternalValue value, final String csticName,
			final SAPCpiOutboundOrderS4hcConfigHeaderModel configurationHeader,
			final Set<SAPCpiOutboundOrderS4hcConfigValuationModel> valuations, final String itemId)
	{
		if (isPartOfMessage(value))
		{
			final SAPCpiOutboundOrderS4hcConfigValuationModel valuation = new SAPCpiOutboundOrderS4hcConfigValuationModel();
			valuation.setAuthor(mapAuthor(value.getAuthor()));
			valuation.setCharacteristic(csticName);
			valuation.setEntryNumber(configurationHeader.getEntryNumber());
			valuation.setOrderId(configurationHeader.getOrderId());
			valuation.setNodeId(itemId);
			valuation.setValue(value.getValue());
			valuations.add(valuation);
		}
	}

	protected String mapAuthor(final String author)
	{

		if (author == null || AUTHOR_USER.equals(author.trim()))
		{
			return TARGET_AUTHOR_USER;
		}
		else if (AUTHOR_EXTERNAL.equals(author))
		{
			return TARGET_AUTHOR_EXTERNAL;
		}
		if (isDisabledAuthorCheck())
		{
			return author;
		}
		throw new IllegalStateException("Author must be either user or external when reaching this method");
	}

	protected boolean isPartOfMessage(final CPSExternalValue value)
	{
		final String author = value.getAuthor();
		return this.isDisabledAuthorCheck() || StringUtils.isEmpty(author) || AUTHOR_EXTERNAL.equals(author)
				|| AUTHOR_USER.equals(author.trim());
	}

	protected CPSCommerceExternalConfiguration parseConfiguration(final String externalConfiguration)
	{
		try
		{
			return getObjectMapper().readValue(externalConfiguration, CPSCommerceExternalConfiguration.class);
		}
		catch (final IOException e)
		{
			throw new IllegalStateException("Parsing external configuration failed: expected JSON of CPSExternalConfiguration", e);
		}
	}

	protected SAPCpiOutboundOrderItemModel findTargetEntry(final AbstractOrderEntryModel entry,
			final SAPCpiOutboundOrderModel target)
	{
		return target.getSapCpiOutboundOrderItems().stream()
				.filter(e -> e.getEntryNumber().equals(entry.getEntryNumber().toString())).findFirst().orElse(null);
	}

	protected ObjectMapper getObjectMapper()
	{
		if (objectMapper == null)
		{
			objectMapper = new ObjectMapper();
		}
		return objectMapper;
	}

	public MappingDecisionStrategy getMappingDecisionStrategy()
	{
		return this.mappingDecisionStrategy;
	}

	public void setDisabledAuthorCheck(final boolean b)
	{
		this.disabledAuthorCheck = b;

	}

}
