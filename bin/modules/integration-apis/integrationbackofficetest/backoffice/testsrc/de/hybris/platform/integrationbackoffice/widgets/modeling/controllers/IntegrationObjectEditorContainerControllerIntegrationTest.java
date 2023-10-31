/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modeling.controllers;

import static de.hybris.platform.integrationservices.util.IntegrationTestUtil.importImpEx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.integrationbackoffice.BeanRegisteringRule;
import de.hybris.platform.integrationbackoffice.utility.DefaultItemTypeMatchSelector;
import de.hybris.platform.integrationbackoffice.widgets.modeling.builders.DefaultDataStructureBuilder;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.IntegrationObjectDefinition;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.IntegrationObjectPresentation;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.SubtypeData;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.IntegrationMapKeyDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.IntegrationObjectPresentationPopulator;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.ListItemAttributeDTOUpdater;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.ReadService;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.impl.DefaultIntegrationObjectDefinitionConverter;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.impl.DefaultIntegrationObjectItemTypeMatchService;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.impl.DefaultListItemAttributeDTOUpdater;
import de.hybris.platform.integrationbackoffice.widgets.modeling.utility.DefaultEditorAttributesFilteringService;
import de.hybris.platform.integrationbackoffice.widgets.modeling.utility.EditorAttributesFilteringService;
import de.hybris.platform.integrationservices.config.ReadOnlyAttributesConfiguration;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.integrationservices.util.IntegrationObjectTestUtil;
import de.hybris.platform.integrationservices.util.IntegrationTestUtil;
import de.hybris.platform.odata2services.odata.schema.SchemaGenerator;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.HashSet;
import java.util.Objects;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

@IntegrationTest
public class IntegrationObjectEditorContainerControllerIntegrationTest extends ServicelayerTest
{
	private static final String READ_SERVICE = "readService";
	private static final String EDITOR_FILTER_SERVICE = "editorAttrFilterService";
	private static final String ITEM_MATCH_SELECTOR = "itemTypeMatchSelector";
	private static final String ITEM_MATCH_SERVICE = "itemTypeMatchService";
	private static final String DATA_STRUCTURE_BUILDER = "dataStructureBuilder";
	private static final String EDITOR_PRESENTATION_POPULATOR = "editorPresentationPopulator";

	private static final String EDITOR_PRESENTATION = "editorPresentation";
	private static final String IO_DEFINITION_CONVERTER = "integrationObjectDefinitionConverter";
	private static final String TEST_NAME = "IntegrationObjectEditorContainerController";
	private static final String IO_CODE = TEST_NAME + "_OrderIO";
	private static final String DTO_UPDATER = "integrationListItemDTOUpdater";

	@Resource
	private FlexibleSearchService flexibleSearchService;
	@Resource
	private TypeService typeService;
	@Resource
	private SchemaGenerator oDataSchemaGenerator;
	@Resource
	private ReadOnlyAttributesConfiguration defaultIntegrationServicesConfiguration;
	@Resource
	private EditorAttributesFilteringService editorAttrFilterService;
	@Resource(name = "integrationListItemDTOUpdater")
	private ListItemAttributeDTOUpdater integrationListItemAttributeDTOUpdater;


	@Rule
	public BeanRegisteringRule rule = new BeanRegisteringRule().register(ReadService.class, READ_SERVICE)
	                                                           .register(DefaultEditorAttributesFilteringService.class,
			                                                           EDITOR_FILTER_SERVICE)
	                                                           .register(DefaultItemTypeMatchSelector.class, ITEM_MATCH_SELECTOR)
	                                                           .register(DefaultIntegrationObjectItemTypeMatchService.class,
			                                                           ITEM_MATCH_SERVICE)
	                                                           .register(DefaultDataStructureBuilder.class,
			                                                           DATA_STRUCTURE_BUILDER)
	                                                           .register(IntegrationObjectPresentationPopulator.class,
			                                                           EDITOR_PRESENTATION_POPULATOR)
	                                                           .register(IntegrationObjectPresentation.class, EDITOR_PRESENTATION)
	                                                           .register(DefaultIntegrationObjectDefinitionConverter.class,
			                                                           IO_DEFINITION_CONVERTER)
	                                                           .register(DefaultListItemAttributeDTOUpdater.class, DTO_UPDATER);


	private ReadService readService;
	private DefaultDataStructureBuilder dataStructureBuilder;
	private IntegrationObjectPresentation integrationObjectPresentation;
	private DefaultIntegrationObjectDefinitionConverter integrationObjectDefinitionConverter;

	final private IntegrationObjectEditorContainerController controller = new IntegrationObjectEditorContainerController();

	@Before
	public void setUp() throws Exception
	{
		readService = (ReadService) rule.getBean(ReadService.class, READ_SERVICE);
		readService.setFlexibleSearchService(flexibleSearchService);
		readService.setTypeService(typeService);
		readService.setODataDefaultSchemaGenerator(oDataSchemaGenerator);
		readService.setReadOnlyAttributesConfiguration(defaultIntegrationServicesConfiguration);

		dataStructureBuilder = (DefaultDataStructureBuilder) rule.getBean(DefaultDataStructureBuilder.class,
				DATA_STRUCTURE_BUILDER);
		dataStructureBuilder.setReadService(readService);
		dataStructureBuilder.setEditorAttrFilterService(editorAttrFilterService);
		dataStructureBuilder.setIntegrationListItemAttributeDTOUpdater(integrationListItemAttributeDTOUpdater);

		integrationObjectPresentation = (IntegrationObjectPresentation) rule.getBean(
				IntegrationObjectPresentation.class, EDITOR_PRESENTATION);
		integrationObjectDefinitionConverter = (DefaultIntegrationObjectDefinitionConverter) rule.getBean(
				DefaultIntegrationObjectDefinitionConverter.class, IO_DEFINITION_CONVERTER);

		controller.setEditorPresentation(integrationObjectPresentation);
		setCompileSubtypeSetTestImpex();
	}

	@After
	public void tearDown()
	{
		IntegrationTestUtil.removeSafely(IntegrationObjectModel.class, object -> object.getCode().equals(IO_CODE));
	}

	private void setCompileSubtypeSetTestImpex() throws ImpExException
	{
		importImpEx("$ioCode=" + IO_CODE, "INSERT_UPDATE IntegrationObject; code[unique = true];",
				"                               ; $ioCode", "$io = integrationObject(code)",
				"INSERT_UPDATE IntegrationObjectItem; $io[unique = true]; code[unique = true]; type(code); root[default = false] ",
				"                                   ; $ioCode           ; Order              ; Order     ; true",
				"                                   ; $ioCode           ; Customer           ; Customer  ;     ",
				"$integrationItem = integrationObjectItem(integrationObject(code), code)[unique = true]",
				"$attrDescriptor = attributeDescriptor(enclosingType(code), qualifier)",
				"INSERT_UPDATE IntegrationObjectItemAttribute; $integrationItem; attributeName[unique = true]; $attrDescriptor    ; returnIntegrationObjectItem(integrationObject(code), code); unique[default = false]; autoCreate[default = false] ",
				"                                            ; $ioCode:Order   ; user                        ; Order:user         ; $ioCode:Customer;                                         ;",
				"                                            ; $ioCode:Order   ; code                        ; Order:code         ;                                                           ;   true;",
				"                                            ; $ioCode:Customer; name                        ; Customer:name      ;                                                           ;       ;",
				"                                            ; $ioCode:Customer; customerID                  ; Customer:customerID;                                                           ;   true;");
	}

	@Test
	public void readServiceTest()
	{
		assertTrue(Objects.nonNull(readService));
	}

	@Test
	public void compileSubtypeDataSetTest()
	{
		final IntegrationObjectModel integrationObjectModel = IntegrationObjectTestUtil.findIntegrationObjectByCode(IO_CODE);
		assertNotNull(integrationObjectModel);

		final IntegrationObjectDefinition integrationObjectDefinition = integrationObjectDefinitionConverter.toDefinitionMap(
				integrationObjectModel);

		integrationObjectPresentation.setSubtypeDataSet(
				dataStructureBuilder.compileSubtypeDataSet(integrationObjectDefinition, new HashSet<>()));

		final SubtypeData subtypeData = (SubtypeData) integrationObjectPresentation.getSubtypeDataSet().toArray()[0];

		assertEquals(IO_CODE, integrationObjectModel.getCode());
		assertEquals(1, integrationObjectPresentation.getSubtypeDataSet().size());
		assertEquals("User", subtypeData.getBaseType().getCode());
		assertEquals("Customer", subtypeData.getSubtype().getCode());
		assertEquals("Order", subtypeData.getParentNodeKey().getCode());
		assertEquals("user", subtypeData.getAttributeAlias());
	}

	@Test
	public void findSubtypeMatchTest()
	{
		final IntegrationObjectModel integrationObjectModel = IntegrationObjectTestUtil.findIntegrationObjectByCode(IO_CODE);
		assertNotNull(integrationObjectModel);

		final IntegrationObjectDefinition integrationObjectDefinition = integrationObjectDefinitionConverter.toDefinitionMap(
				integrationObjectModel);
		integrationObjectPresentation.setSubtypeDataSet(
				dataStructureBuilder.compileSubtypeDataSet(integrationObjectDefinition, new HashSet<>()));

		final ComposedTypeModel order = integrationObjectModel.getRootItem().getType();
		final IntegrationMapKeyDTO orderKey = new IntegrationMapKeyDTO(order, "Order");
		final SubtypeData subtypeData = (SubtypeData) integrationObjectPresentation.getSubtypeDataSet().toArray()[0];
		final ComposedTypeModel attributeType = (ComposedTypeModel) subtypeData.getBaseType();
		final String qualifier = "user";
		final ComposedTypeModel expectedSubtype = (ComposedTypeModel) subtypeData.getSubtype();

		final ComposedTypeModel actualSubtype = dataStructureBuilder.findSubtypeMatch(orderKey, qualifier, attributeType,
				integrationObjectPresentation.getSubtypeDataSet());

		assertEquals(IO_CODE, integrationObjectModel.getCode());
		assertEquals(expectedSubtype, actualSubtype);
	}
}
