/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationbackoffice.widgets.modals.generator;

import static de.hybris.platform.integrationservices.util.IntegrationTestUtil.importImpEx;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeUnitModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.integrationbackoffice.BeanRegisteringRule;
import de.hybris.platform.integrationservices.model.AbstractIntegrationObjectItemAttributeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemVirtualAttributeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectVirtualAttributeDescriptorModel;
import de.hybris.platform.integrationservices.util.IntegrationObjectTestUtil;
import de.hybris.platform.integrationservices.util.IntegrationTestUtil;
import de.hybris.platform.servicelayer.ServicelayerTest;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

@IntegrationTest
public class DefaultIntegrationObjectImpexGenerationIntegrationTest extends ServicelayerTest
{
	private static final String IMPEX_GENERATOR = "integrationObjectImpexGenerator";
	private static final String TEST_NAME = "DefaultIntegrationObjectImpexGeneration";
	private static final String ITEM_TYPE_MATCH_IO = TEST_NAME + "_StockLevelIO";
	private static final String VIRTUAL_ATTRIBUTE_IO = TEST_NAME + "_ProductIO_1";
	private static final String CLASSIFICATION_ATTR_IO = TEST_NAME + "_ProductIO_2";
	private static final String CLASSIFICATION_SYSTEM = TEST_NAME + "_Electronics";
	private static final String VERSION = "Staged";
	private static final String SYSTEM_VERSION = CLASSIFICATION_SYSTEM + ":" + VERSION;

	private IntegrationObjectImpexGenerator integrationObjectImpexGenerator;

	@Rule
	public BeanRegisteringRule rule = new BeanRegisteringRule().register(DefaultIntegrationObjectImpexGenerator.class,
			IMPEX_GENERATOR);

	@Before
	public void setUp()
	{
		integrationObjectImpexGenerator = (IntegrationObjectImpexGenerator) rule.getBean(
				DefaultIntegrationObjectImpexGenerator.class, IMPEX_GENERATOR);
	}

	@After
	public void tearDown()
	{
		Arrays.asList(ITEM_TYPE_MATCH_IO, VIRTUAL_ATTRIBUTE_IO, CLASSIFICATION_ATTR_IO)
		      .forEach(objectCode -> IntegrationTestUtil.removeSafely(IntegrationObjectModel.class,
				      it -> it.getCode().equals(objectCode)));
		IntegrationTestUtil.remove(ClassAttributeAssignmentModel.class,
				it -> it.getClassificationClass().getCatalogVersion().getCatalog().getId().equals(CLASSIFICATION_SYSTEM));
		IntegrationTestUtil.remove(ClassificationAttributeModel.class,
				it -> it.getSystemVersion().getCatalog().getId().equals(CLASSIFICATION_SYSTEM));
		IntegrationTestUtil.remove(ClassificationAttributeUnitModel.class,
				it -> it.getSystemVersion().getCatalog().getId().equals(CLASSIFICATION_SYSTEM));
		IntegrationTestUtil.remove(ClassificationClassModel.class,
				it -> it.getCatalogVersion().getCatalog().getId().equals(CLASSIFICATION_SYSTEM));
		IntegrationTestUtil.remove(ClassificationSystemModel.class, it -> it.getId().equals(CLASSIFICATION_SYSTEM));
	}

	private void setupItemTypeMatch() throws ImpExException
	{
		importImpEx(
				"$ioCode=" + ITEM_TYPE_MATCH_IO,
				"INSERT_UPDATE IntegrationObject; code[unique = true];",
				"                               ; $ioCode",

				"$io = integrationObject(code)[unique = true]",
				"INSERT_UPDATE IntegrationObjectItem; $io    ; code[unique = true]; type(code)    ; root[default = false] ;itemTypeMatch(code)",
				"                                   ; $ioCode; Product            ; Product       ;                       ; ALL_SUB_AND_SUPER_TYPES	;",
				"                                   ; $ioCode; CatalogVersion     ; CatalogVersion;                       ; RESTRICT_TO_ITEM_TYPE  	;",
				"                                   ; $ioCode; StockLevel         ; StockLevel    ; true                  ; ALL_SUB_AND_SUPER_TYPES	;",
				"                                   ; $ioCode; Catalog            ; Catalog       ;                       ; ALL_SUBTYPES           	;",

				"$integrationItem = integrationObjectItem(integrationObject(code), code)[unique = true]",
				"$attrName = attributeName[unique = true]",
				"$attrDescriptor = attributeDescriptor(enclosingType(code), qualifier)",
				"$attributeType=returnIntegrationObjectItem(integrationObject(code), code)",
				"INSERT_UPDATE IntegrationObjectItemAttribute; $integrationItem      ; $attrName     ; $attrDescriptor       ; $attributeType        ; unique[default = false]; autoCreate[default = false]",
				"                                            ; $ioCode:Product       ; catalogVersion; Product:catalogVersion; $ioCode:CatalogVersion; true	;",
				"                                            ; $ioCode:Product       ; code          ; Product:code          ;                       ; true	;",
				"                                            ; $ioCode:CatalogVersion; catalog       ; CatalogVersion:catalog; $ioCode:Catalog       ; true	;",
				"                                            ; $ioCode:CatalogVersion; version       ; CatalogVersion:version;                       ; true	;",
				"                                            ; $ioCode:StockLevel    ; product       ; StockLevel:product    ; $ioCode:Product       ;      ;",
				"                                            ; $ioCode:StockLevel    ; productCode   ; StockLevel:productCode;                       ; true	;",
				"                                            ; $ioCode:Catalog       ; id            ; Catalog:id            ;                       ; true	;"
		);
	}

	private void setupClassification() throws ImpExException
	{
		importImpEx(
				"$SYSTEM=" + CLASSIFICATION_SYSTEM,
				"$VERSION=" + VERSION,
				"$SYSTEM_VERSION=" + SYSTEM_VERSION,
				"$catalogVersionHeader=catalogVersion(catalog(id), version)",
				"$systemVersionHeader=systemVersion(catalog(id), version)",
				"INSERT_UPDATE ClassificationSystem; id[unique = true]",
				"                                  ; $SYSTEM",
				"INSERT_UPDATE ClassificationSystemVersion; catalog(id)[unique = true]; version[unique = true]",
				"                                         ; $SYSTEM                   ; $VERSION",
				"INSERT_UPDATE ClassificationClass; code[unique = true]; $catalogVersionHeader[unique = true]",
				"                                 ; dimensions         ; $SYSTEM_VERSION",
				"INSERT_UPDATE ClassificationAttributeUnit; $systemVersionHeader[unique = true]; code[unique = true]; symbol; unitType",
				"                                         ; $SYSTEM_VERSION                    ; centimeters        ; cm    ; measurement",
				"INSERT_UPDATE ClassificationAttribute; code[unique = true]; $systemVersionHeader[unique = true]",
				"                                     ; height             ; $SYSTEM_VERSION",
				"                                     ; width              ; $SYSTEM_VERSION",
				"                                     ; depth              ; $SYSTEM_VERSION",
				"$class=classificationClass($catalogVersionHeader, code)",
				"$attribute=classificationAttribute($systemVersionHeader, code)",
				"INSERT_UPDATE ClassAttributeAssignment; $class[unique = true]     ; $attribute[unique = true]; unit($systemVersionHeader, code); attributeType(code)",
				"                                      ; $SYSTEM_VERSION:dimensions; $SYSTEM_VERSION:height   ; $SYSTEM_VERSION:centimeters     ; number",
				"                                      ; $SYSTEM_VERSION:dimensions; $SYSTEM_VERSION:width    ; $SYSTEM_VERSION:centimeters     ; number",
				"                                      ; $SYSTEM_VERSION:dimensions; $SYSTEM_VERSION:depth    ; $SYSTEM_VERSION:centimeters     ; number");

		importImpEx(
				"$ioCode=" + CLASSIFICATION_ATTR_IO,
				"INSERT_UPDATE IntegrationObject; code[unique = true];",
				"                               ; $ioCode",
				"$io = integrationObject(code)[unique = true]",
				"INSERT_UPDATE IntegrationObjectItem; $io ; code[unique = true]; type(code)     ; root[default = false]",
				"                                   ; $ioCode                                   ; Product            ; Product   ; true",
				"                                   ; $ioCode                                   ; Catalog            ; Catalog",
				"                                   ; $ioCode                                   ; CatalogVersion     ; CatalogVersion",
				"$item=integrationObjectItem(integrationObject(code), code)",
				"$descriptor=attributeDescriptor(enclosingType(code), qualifier)",
				"$attributeType=returnIntegrationObjectItem(integrationObject(code), code)",
				"INSERT_UPDATE IntegrationObjectItemAttribute; $item[unique = true]; attributeName[unique = true]; $descriptor           ; $attributeType;",
				"                                            ; $ioCode:Product         ; code                        ; Product:code",
				"                                            ; $ioCode:Product         ; catalogVersion              ; Product:catalogVersion; $ioCode:CatalogVersion",
				"                                            ; $ioCode:CatalogVersion  ; version                     ; CatalogVersion:version",
				"                                            ; $ioCode:CatalogVersion  ; catalog                     ; CatalogVersion:catalog; $ioCode:Catalog",
				"                                            ; $ioCode:Catalog         ; id                          ; Catalog:id",
				"$SYSTEM_VERSION=" + SYSTEM_VERSION,
				"$item=integrationObjectItem(integrationObject(code), code)",
				"$systemVersionHeader=systemVersion(catalog(id), version)",
				"$classificationClassHeader=classificationClass(catalogVersion(catalog(id), version), code)",
				"$classificationAttributeHeader=classificationAttribute($systemVersionHeader, code)",
				"$classificationAssignment=classAttributeAssignment($classificationClassHeader, $classificationAttributeHeader)",
				"INSERT_UPDATE IntegrationObjectItemClassificationAttribute; $item[unique = true]; attributeName[unique = true]; $classificationAssignment",
				"                                                          ; $ioCode:Product         ; height                      ; $SYSTEM_VERSION:dimensions:$SYSTEM_VERSION:height",
				"                                                          ; $ioCode:Product         ; depth                       ; $SYSTEM_VERSION:dimensions:$SYSTEM_VERSION:depth",
				"                                                          ; $ioCode:Product         ; width                       ; $SYSTEM_VERSION:dimensions:$SYSTEM_VERSION:width");
	}

	private void setupVirtualAttributesTest() throws ImpExException
	{
		importImpEx(
				"$ioCode=" + VIRTUAL_ATTRIBUTE_IO,
				"INSERT_UPDATE IntegrationObject; code[unique = true];",
				"                               ; $ioCode",
				"$io = integrationObject(code)[unique = true]",
				"INSERT_UPDATE IntegrationObjectItem; $io    ; code[unique = true]; type(code)     ; root[default = false] ;itemTypeMatch(code)",
				"                                   ; $ioCode; Catalog            ; Catalog        ;      ; ;",
				"                                   ; $ioCode; CatalogVersion     ; CatalogVersion ;      ; ;",
				"                                   ; $ioCode; Product            ; Product        ; true ; ;",
				"INSERT_UPDATE IntegrationObjectItemAttribute; integrationObjectItem(integrationObject(code), code)[unique = true]; attributeName[unique = true]; attributeDescriptor(enclosingType(code), qualifier); returnIntegrationObjectItem(integrationObject(code), code); unique[default = false]; autoCreate[default = false]",
				"                                            ; $ioCode:Catalog        ; id             ; Catalog:id             ;                                     ; ;",
				"                                            ; $ioCode:CatalogVersion ; version        ; CatalogVersion:version ;                                     ; ;",
				"                                            ; $ioCode:CatalogVersion ; catalog        ; CatalogVersion:catalog ; $ioCode:Catalog        ; ;",
				"                                            ; $ioCode:Product        ; code           ; Product:code           ;                                     ; ;",
				"                                            ; $ioCode:Product        ; catalogVersion ; Product:catalogVersion ; $ioCode:CatalogVersion ; ;",
				"                                            ; $ioCode:Product        ; onlineDate     ; Product:onlineDate",
				"INSERT_UPDATE IntegrationObjectItemVirtualAttribute; integrationObjectItem(integrationObject(code), code)[unique = true]; attributeName[unique = true]; retrievalDescriptor(code)",
				"                                                   ; $ioCode:Product ; virtintValueDescriptor        ; intValueDescriptor",
				"                                                   ; $ioCode:Product ; virtfloatValueDescriptor      ; floatValueDescriptor",
				"                                                   ; $ioCode:Product ; virtdoubleValueDescriptor     ; doubleValueDescriptor",
				"                                                   ; $ioCode:Product ; virtbooleanValueDescriptor    ; booleanValueDescriptor",
				"                                                   ; $ioCode:Product ; virtbyteValueDescriptor       ; byteValueDescriptor",
				"                                                   ; $ioCode:Product ; virtlongValueDescriptor       ; longValueDescriptor",
				"                                                   ; $ioCode:Product ; virtshortValueDescriptor      ; shortValueDescriptor",
				"                                                   ; $ioCode:Product ; virtcharValueDescriptor       ; charValueDescriptor",
				"                                                   ; $ioCode:Product ; virtbigDecimalValueDescriptor ; bigDecimalValueDescriptor",
				"                                                   ; $ioCode:Product ; formattedOnlineDate           ; formattedOnlineDate",
				"INSERT_UPDATE IntegrationObjectVirtualAttributeDescriptor; code[unique = true]; logicLocation; type(code)",
				"                                                         ; booleanValueDescriptor    ; model://booleanValue    ; java.lang.Boolean",
				"                                                         ; charValueDescriptor       ; model://charValue       ; java.lang.Character",
				"                                                         ; byteValueDescriptor       ; model://byteValue       ; java.lang.Byte",
				"                                                         ; floatValueDescriptor      ; model://floatValue      ; java.lang.Float",
				"                                                         ; doubleValueDescriptor     ; model://doubleValue     ; java.lang.Double",
				"                                                         ; longValueDescriptor       ; model://longValue       ; java.lang.Long",
				"                                                         ; bigDecimalValueDescriptor ; model://bigDecimalValue ; java.math.BigDecimal",
				"                                                         ; intValueDescriptor        ; model://intValue        ; java.lang.Integer",
				"                                                         ; shortValueDescriptor      ; model://shortValue      ; java.lang.Short",
				"                                                         ; formattedOnlineDate       ; model://formattedOnlineDateScript"
		);
	}

	@Test
	public void testImpexStringWithClassificationAttributes() throws ImpExException
	{
		setupClassification();
		IntegrationObjectModel integrationObjectModel = IntegrationObjectTestUtil.findIntegrationObjectByCode(
				CLASSIFICATION_ATTR_IO);
		assertThat(integrationObjectModel.getCode()).isEqualTo(CLASSIFICATION_ATTR_IO);

		final String generatedImpex = integrationObjectImpexGenerator.generateImpex(integrationObjectModel);
		IntegrationTestUtil.removeSafely(IntegrationObjectModel.class,
				object -> object.getCode().equals(CLASSIFICATION_ATTR_IO));
		integrationObjectModel = IntegrationObjectTestUtil.findIntegrationObjectByCode(CLASSIFICATION_ATTR_IO);
		assertThat(integrationObjectModel).isNull();

		importImpEx(generatedImpex);
		integrationObjectModel = IntegrationObjectTestUtil.findIntegrationObjectByCode(CLASSIFICATION_ATTR_IO);
		assertThat(integrationObjectModel.getCode()).isEqualTo(CLASSIFICATION_ATTR_IO);
		assertThat(integrationObjectModel.getItems()).hasSize(3);

		final Set<IntegrationObjectItemModel> itemSet = integrationObjectModel.getItems();

		final Set<String> itemSet1 = itemSet.stream()
		                                    .map(IntegrationObjectItemModel::getCode)
		                                    .collect(Collectors.toSet());
		assertThat(itemSet1).containsAll(Arrays.asList("Product", "Catalog", "CatalogVersion"));

		final Set<String> itemSet2 = itemSet.stream()
		                                    .flatMap(item -> item.getAttributes().stream())
		                                    .map(IntegrationObjectItemAttributeModel::getAttributeName)
		                                    .collect(Collectors.toSet());
		assertThat(itemSet2).containsExactlyInAnyOrder("code", "catalogVersion", "id", "version", "catalog");

		final Set<String> itemSet3 = itemSet.stream()
		                                    .filter(item -> item.getCode().equals("Product"))
		                                    .flatMap(item -> item.getClassificationAttributes().stream())
		                                    .map(AbstractIntegrationObjectItemAttributeModel::getAttributeName)
		                                    .collect(Collectors.toSet());
		assertThat(itemSet3).containsAll(Arrays.asList("height", "depth", "width"));

		IntegrationTestUtil.removeSafely(IntegrationObjectModel.class,
				object -> object.getCode().equals(CLASSIFICATION_ATTR_IO));
	}

	@Test
	public void testImpexItemTypeMatch() throws ImpExException
	{
		setupItemTypeMatch();
		final IntegrationObjectModel objectModel = IntegrationObjectTestUtil.findIntegrationObjectByCode(ITEM_TYPE_MATCH_IO);
		assertThat(objectModel).isNotNull();
		assertThat(objectModel.getItems()).hasSize(4);
		assertThat(IntegrationObjectTestUtil.findIntegrationObjectItemByCodeAndIntegrationObject("Product", objectModel)
		                                    .getItemTypeMatch()
		                                    .getCode())
				.isEqualTo("ALL_SUB_AND_SUPER_TYPES");
		assertThat(IntegrationObjectTestUtil.findIntegrationObjectItemByCodeAndIntegrationObject("CatalogVersion", objectModel)
		                                    .getItemTypeMatch()
		                                    .getCode())
				.isEqualTo("RESTRICT_TO_ITEM_TYPE");
		assertThat(IntegrationObjectTestUtil.findIntegrationObjectItemByCodeAndIntegrationObject("StockLevel", objectModel)
		                                    .getItemTypeMatch()
		                                    .getCode())
				.isEqualTo("ALL_SUB_AND_SUPER_TYPES");
		assertThat(IntegrationObjectTestUtil.findIntegrationObjectItemByCodeAndIntegrationObject("Catalog", objectModel)
		                                    .getItemTypeMatch()
		                                    .getCode())
				.isEqualTo("ALL_SUBTYPES");

		IntegrationTestUtil.removeSafely(IntegrationObjectModel.class,
				object -> object.getCode().equals(ITEM_TYPE_MATCH_IO));
	}

	@Test
	public void testImpexVirtualAttributes() throws ImpExException
	{
		setupVirtualAttributesTest();
		IntegrationObjectModel objectModel = IntegrationObjectTestUtil.findIntegrationObjectByCode(VIRTUAL_ATTRIBUTE_IO);
		assertThat(objectModel).isNotNull();

		final String generatedImpex = integrationObjectImpexGenerator.generateImpex(objectModel);
		IntegrationTestUtil.removeSafely(IntegrationObjectModel.class, object -> object.getCode().equals(VIRTUAL_ATTRIBUTE_IO));
		importImpEx(generatedImpex);
		objectModel = IntegrationObjectTestUtil.findIntegrationObjectByCode(VIRTUAL_ATTRIBUTE_IO);

		assertThat(objectModel).isNotNull();
		final Set<IntegrationObjectItemModel> items = objectModel.getItems();

		final Set<String> items1 = items.stream()
		                                .map(IntegrationObjectItemModel::getCode)
		                                .collect(Collectors.toSet());
		assertThat(items1).containsAll(Arrays.asList("Product", "Catalog", "CatalogVersion"));

		final Set<String> items2 = items.stream()
		                                .flatMap(item -> item.getAttributes().stream())
		                                .map(IntegrationObjectItemAttributeModel::getAttributeName)
		                                .collect(Collectors.toSet());
		assertThat(items2).containsAll(Arrays.asList("code", "catalogVersion", "id", "version", "catalog"));

		final Map<String, String> expectedVirtualAttributes = Map.of(
				"virtintValueDescriptor", "intValueDescriptor",
				"virtfloatValueDescriptor", "floatValueDescriptor",
				"virtdoubleValueDescriptor", "doubleValueDescriptor",
				"virtbooleanValueDescriptor", "booleanValueDescriptor",
				"virtbyteValueDescriptor", "byteValueDescriptor",
				"virtlongValueDescriptor", "longValueDescriptor",
				"virtshortValueDescriptor", "shortValueDescriptor",
				"virtcharValueDescriptor", "charValueDescriptor",
				"virtbigDecimalValueDescriptor", "bigDecimalValueDescriptor",
				"formattedOnlineDate", "formattedOnlineDate"
		);
		final Map<String, String> actualVirtualAttributes =
				items.stream()
				     .flatMap(item -> item.getVirtualAttributes().stream())
				     .collect(Collectors.toSet())
				     .stream()
				     .collect(Collectors.toMap(IntegrationObjectItemVirtualAttributeModel::getAttributeName,
						     va -> va.getRetrievalDescriptor().getCode()));
		assertThat(actualVirtualAttributes).isEqualTo(expectedVirtualAttributes);

		final Map<String, IntegrationObjectVirtualAttributeDescriptorModel> actualDescriptors =
				items.stream()
				     .flatMap(item -> item.getVirtualAttributes().stream())
				     .collect(Collectors.toSet())
				     .stream()
				     .map(IntegrationObjectItemVirtualAttributeModel::getRetrievalDescriptor)
				     .collect(Collectors.toSet())
				     .stream()
				     .collect(Collectors.toMap(IntegrationObjectVirtualAttributeDescriptorModel::getCode,
						     descriptor -> descriptor));
		final List<IntegrationObjectVirtualAttributeDescriptorModel> actualDescList = List.of(
				actualDescriptors.get("intValueDescriptor"),
				actualDescriptors.get("floatValueDescriptor"),
				actualDescriptors.get("doubleValueDescriptor"),
				actualDescriptors.get("booleanValueDescriptor"),
				actualDescriptors.get("byteValueDescriptor"),
				actualDescriptors.get("longValueDescriptor"),
				actualDescriptors.get("shortValueDescriptor"),
				actualDescriptors.get("charValueDescriptor"),
				actualDescriptors.get("bigDecimalValueDescriptor"),
				actualDescriptors.get("formattedOnlineDate")
		);

		final List<String> actualDescLogicLocationList =
				actualDescList.stream()
				              .map(IntegrationObjectVirtualAttributeDescriptorModel::getLogicLocation)
				              .collect(Collectors.toList());

		final List<String> expectedDescLogicLocationList = List.of(
				"model://intValue",
				"model://floatValue",
				"model://doubleValue",
				"model://booleanValue",
				"model://byteValue",
				"model://longValue",
				"model://shortValue",
				"model://charValue",
				"model://bigDecimalValue",
				"model://formattedOnlineDateScript"
		);
		assertThat(actualDescLogicLocationList).isEqualTo(expectedDescLogicLocationList);

		final List<String> actualDescTypeCodeList =
				actualDescList.stream()
				              .map(d -> d.getType().getCode())
				              .collect(Collectors.toList());

		final List<String> expectedDescTypeCodeList = List.of(
				"java.lang.Integer",
				"java.lang.Float",
				"java.lang.Double",
				"java.lang.Boolean",
				"java.lang.Byte",
				"java.lang.Long",
				"java.lang.Short",
				"java.lang.Character",
				"java.math.BigDecimal",
				"java.lang.String"
		);

		assertThat(actualDescTypeCodeList).isEqualTo(expectedDescTypeCodeList);

		IntegrationTestUtil.removeSafely(IntegrationObjectModel.class, object -> object.getCode().equals(VIRTUAL_ATTRIBUTE_IO));
	}
}
