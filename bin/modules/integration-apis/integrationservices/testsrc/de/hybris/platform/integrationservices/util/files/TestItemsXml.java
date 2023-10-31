/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.util.files;

import de.hybris.platform.core.Registry;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.constraints.NotNull;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Representation of items types in an items.xml file for the test purposes.
 */
public class TestItemsXml implements ItemsXml
{
	private static final String CODE_ATTRIBUTE = "code";
	private static final String TYPE_ATTRIBUTE = "type";
	private static final String QUALIFIER_ATTRIBUTE = "qualifier";

	private static TypeService typeService;

	private final FileItems itemsInFile;
	private final ContextItemTypes itemsInFileContext;

	/**
	 * Instantiates this items xml.
	 *
	 * @param doc the xml we are looking at.
	 */
	TestItemsXml(@NotNull final Document doc)
	{
		itemsInFileContext = ContextItemTypes.loadContextItemsFrom(doc);
		itemsInFile = itemsInFileContext.getItemsDefinedInTheFile();
	}

	@Override
	public Collection<String> getContextTypeCodes()
	{
		return itemsInFileContext.getTypeCodes();
	}

	@Override
	public Collection<String> getCreatedTypeCodes()
	{
		return getItemTypes().stream()
		                     .filter(ItemType::isCreated)
		                     .map(ItemType::getTypeCode)
		                     .collect(Collectors.toSet());
	}

	@Override
	public Collection<ItemType> getItemTypes()
	{
		return itemsInFile.getItemTypes();
	}


	@Override
	public boolean containsAttribute(final String itemType, final String attributeName)
	{
		return itemsInFileContext.containsAttribute(itemType, attributeName);
	}

	static void setTypeService(final TypeService service)
	{
		typeService = service;
	}

	private static TypeService getTypeService()
	{
		if (typeService == null)
		{
			typeService = Registry.getApplicationContext().getBean("typeService", TypeService.class);
		}
		return typeService;
	}

	private static String getCode(final Element el)
	{
		return el.hasAttribute(CODE_ATTRIBUTE)
				? el.getAttribute(CODE_ATTRIBUTE)
				: getType(el);
	}

	private static String getType(final Element el)
	{
		return el.getAttribute(TYPE_ATTRIBUTE);
	}

	private static String getAttributeName(final Element el)
	{
		return el.getAttribute(QUALIFIER_ATTRIBUTE);
	}

	/**
	 * A path through the XML elements, e.g. {@code items -> itemtypes -> itemtype} or {@code relation -> sourceElement}
	 */
	private static class XmlPath
	{
		private final String[] segments;

		public XmlPath(final String... steps)
		{
			segments = steps;
		}

		public Element getOneFrom(final Node node)
		{
			final List<Element> children = applyTo(node);
			return children.isEmpty() ? null : children.get(0);
		}

		public List<Element> applyTo(final Node node)
		{
			return applyTo(node, 0);
		}

		private List<Element> applyTo(final Node node, final int level)
		{
			if (level < segments.length)
			{
				final List<Element> children = getChildElements(node, segments[level]);
				return level == segments.length - 1
						? children
						: children.stream()
						          .map(el -> applyTo(el, level + 1))
						          .flatMap(Collection::stream)
						          .collect(Collectors.toList());
			}
			return Collections.emptyList();
		}

		private List<Element> getChildElements(final Node parent, final String elName)
		{
			final NodeList allChildren = parent.getChildNodes();
			final List<Element> matchingChildren = new ArrayList<>(allChildren.getLength());
			for (int i = 0; i < allChildren.getLength(); i++)
			{
				asElement(allChildren.item(i))
						.filter(el -> isNamed(el, elName))
						.ifPresent(matchingChildren::add);
			}
			return matchingChildren;
		}

		private Optional<Element> asElement(final Node node)
		{
			return Optional.ofNullable(node)
			               .filter(this::isElement)
			               .map(Element.class::cast);
		}

		private boolean isElement(final Node n)
		{
			return n.getNodeType() == Node.ELEMENT_NODE;
		}

		private boolean isNamed(final Node n, final String nodeName)
		{
			return nodeName == null || nodeName.equals(n.getNodeName());
		}

	}


	/**
	 * All items declared in this XML file. This object contains only items explicitly declared as {@code itemtype},
	 * {@code enumtype}, {@code maptype}.
	 */
	private static class FileItems
	{
		private static final XmlPath ITEMTYPE_PATH = new XmlPath("items", "itemtypes", "itemtype");
		private static final XmlPath COLLECTIONTYPE_PATH = new XmlPath("items", "collectiontypes", "collectiontype");
		private static final XmlPath MAPTYPE_PATH = new XmlPath("items", "maptypes", "maptype");
		private static final XmlPath ENUMTYPE_PATH = new XmlPath("items", "enumtypes", "enumtype");
		private static final XmlPath ITEM_TO_ATTRIBUTE_PATH = new XmlPath("attributes", "attribute");
		private static final XmlPath ENUM_TO_VALUE_PATH = new XmlPath("value");

		private final Map<String, ItemType> itemsInFile;

		private FileItems()
		{
			itemsInFile = new HashMap<>();
		}

		/**
		 * Creates new instance and populates it with the item types defined in the specified items XML file.
		 *
		 * @param xml an items XML document
		 * @return new instance populated with the types from the document.
		 */
		public static FileItems loadFrom(final Document xml)
		{
			final var fileItems = new FileItems();
			fileItems.derivedItemTypes(xml);
			return fileItems;
		}

		private void derivedItemTypes(final Document xml)
		{
			Stream.of(deriveItemTypes(xml), deriveEnumTypes(xml), deriveCollectionTypes(xml), deriveMapTypes(xml))
			      .flatMap(Collection::stream)
			      .forEach(this::addItem);
		}

		private Collection<ItemType> deriveItemTypes(final Document xml)
		{
			return deriveTypes(xml, ITEMTYPE_PATH, this::createItemType);
		}

		private ItemType createItemType(final Element el)
		{
			final Collection<String> attributes = ITEM_TO_ATTRIBUTE_PATH.applyTo(el)
			                                                            .stream()
			                                                            .map(TestItemsXml::getAttributeName)
			                                                            .collect(Collectors.toSet());
			return ItemType.create(el, getTypeService()).withAttributes(attributes);
		}

		private Collection<ItemType> deriveEnumTypes(final Document xml)
		{
			return deriveTypes(xml, ENUMTYPE_PATH, this::createEnumType);
		}

		private ItemType createEnumType(final Element el)
		{
			final Collection<String> values = ENUM_TO_VALUE_PATH.applyTo(el)
			                                                    .stream()
			                                                    .map(TestItemsXml::getCode)
			                                                    .collect(Collectors.toSet());
			return ItemType.create(el).withAttributes(values);
		}

		private Collection<ItemType> deriveCollectionTypes(final Document xml)
		{
			return deriveTypes(xml, COLLECTIONTYPE_PATH, this::createCollectionType);
		}

		private Collection<ItemType> deriveMapTypes(final Document xml)
		{
			return deriveTypes(xml, MAPTYPE_PATH, this::createCollectionType);
		}

		private ItemType createCollectionType(final Element el)
		{
			return ItemType.create(el);
		}

		private Collection<ItemType> deriveTypes(final Document xml, final XmlPath path,
		                                         final Function<Element, ItemType> itemFactory)
		{
			return path
					.applyTo(xml)
					.stream()
					.map(itemFactory)
					.collect(Collectors.toSet());
		}

		private void addItem(final ItemType item)
		{
			itemsInFile.put(item.getTypeCode(), item);
		}

		public Collection<ItemType> getItemTypes()
		{
			return itemsInFile.values();
		}

		public ItemType getItemType(final String code)
		{
			return itemsInFile.get(code);
		}

		public boolean contains(final ItemType t)
		{
			return itemsInFile.containsKey(t.getTypeCode());
		}
	}

	/**
	 * All items types that are indirectly present in the context of the items.xml file. These are all supertypes of the items
	 * defined in the items.xml and items referenced in the relations but not defined in the items.xml.
	 */
	private static class ContextItemTypes
	{
		private final FileItems fileItems;
		private final Map<String, ItemType> itemsInFileContext;

		private ContextItemTypes(final FileItems items)
		{
			fileItems = items;
			itemsInFileContext = createInitialItems(items);
		}

		private Map<String, ItemType> createInitialItems(final FileItems items)
		{
			return items.getItemTypes().stream()
			            .collect(Collectors.toMap(ItemType::getTypeCode, it -> it));
		}

		/**
		 * Creates an instance and populates it using the specified xml file.
		 *
		 * @param xml the items.xml document to read the context from.
		 * @return an instance populated with all items implicitly or explicitly referenced/present in the specified document.
		 */
		public static ContextItemTypes loadContextItemsFrom(final Document xml)
		{
			final var fileItems = FileItems.loadFrom(xml);
			final var contextItems = new ContextItemTypes(fileItems);
			contextItems.deriveReferencedTypes(xml);
			return contextItems;
		}

		private void deriveReferencedTypes(final Document xml)
		{
			Stream.of(deriveInheritedItems(), deriveRelationItems(xml))
			      .flatMap(Collection::stream)
			      .distinct()
			      .filter(Predicate.not(fileItems::contains))
			      .forEach(this::addItem);
		}

		private Collection<ItemType> deriveRelationItems(final Document xml)
		{
			final var relationItems = RelationItems.loadFrom(xml, fileItems);
			return relationItems.getItemTypes();
		}

		private Collection<ItemType> deriveInheritedItems()
		{
			return fileItems.getItemTypes().stream()
			                .map(ItemType::getInheritedItems)
			                .flatMap(Collection::stream)
			                .collect(Collectors.toSet());
		}

		private void addItem(final ItemType itemType)
		{
			itemsInFileContext.put(itemType.getTypeCode(), itemType);
		}

		public FileItems getItemsDefinedInTheFile()
		{
			return fileItems;
		}

		public Collection<String> getTypeCodes()
		{
			return itemsInFileContext.keySet();
		}

		public boolean containsAttribute(final String itemType, final String attributeName)
		{
			return Optional.ofNullable(getItemType(itemType))
			               .map(it -> it.containsAttribute(attributeName))
			               .orElse(false);
		}

		public ItemType getItemType(final String typeCode)
		{
			return itemsInFileContext.get(typeCode);
		}
	}

	/**
	 * All item types referenced in {@code relation} elements of the items XML file.
	 */
	private static class RelationItems
	{
		private static final XmlPath RELATION_PATH = new XmlPath("items", "relations", "relation");
		private static final XmlPath RELATION_TO_SOURCEELEMENT_PATH = new XmlPath("sourceElement");
		private static final XmlPath RELATION_TO_TARGETELEMENT_PATH = new XmlPath("targetElement");

		private final FileItems fileItems;
		private final Map<String, ItemType> itemsInRelations;

		private RelationItems(final FileItems items)
		{
			fileItems = items;
			itemsInRelations = new HashMap<>();
		}

		/**
		 * Creates new instance and loads it with item types present in {@code relation} elements excluding those types, which are
		 * defined in the items XML
		 *
		 * @param xml   an items.xml document to load relation items from.
		 * @param items items explicitly defined in the items.xml and therefore will be excluded from the relation item types.
		 * @return an instance populated with the item types, which are present in relation elements but not defined in the file.
		 */
		public static RelationItems loadFrom(final Document xml, final FileItems items)
		{
			final var relationItems = new RelationItems(items);
			relationItems.deriveItemsIn(xml);
			return relationItems;
		}

		private void deriveItemsIn(final Document xml)
		{
			RELATION_PATH.applyTo(xml)
			             .stream()
			             .flatMap(this::toItems)
			             .distinct()
			             .forEach(this::addItem);
		}

		private void addItem(final ItemType item)
		{
			itemsInRelations.put(item.getTypeCode(), item);
		}

		private Stream<ItemType> toItems(final Element relation)
		{
			final Element source = RELATION_TO_SOURCEELEMENT_PATH.getOneFrom(relation);
			final Element target = RELATION_TO_TARGETELEMENT_PATH.getOneFrom(relation);
			return source != null && target != null
					? Stream.of(
					lookupItem(source).withAttribute(getAttributeName(target)),
					lookupItem(target).withAttribute(getAttributeName(source)))
					: Stream.empty();
		}

		private ItemType lookupItem(final Element el)
		{
			final ItemType existingItem = fileItems.getItemType(getCode(el));
			return existingItem == null
					? getOrCreateRelationItem(el)
					: existingItem;
		}

		private ItemType getOrCreateRelationItem(final Element el)
		{
			final ItemType existingItem = itemsInRelations.get(getType(el));
			return existingItem == null
					? ItemType.create(el, getTypeService())
					: existingItem;
		}

		public Collection<ItemType> getItemTypes()
		{
			return itemsInRelations.values();
		}
	}
}
