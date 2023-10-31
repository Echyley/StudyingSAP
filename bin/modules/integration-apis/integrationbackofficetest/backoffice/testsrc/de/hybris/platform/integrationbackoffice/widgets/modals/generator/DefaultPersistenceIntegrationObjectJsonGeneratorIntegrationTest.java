/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationbackoffice.widgets.modals.generator;

import static de.hybris.platform.integrationservices.util.IntegrationTestUtil.importImpEx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeUnitModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.integrationbackoffice.BeanRegisteringRule;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.ReadService;
import de.hybris.platform.integrationservices.config.ReadOnlyAttributesConfiguration;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.integrationservices.util.IntegrationObjectTestUtil;
import de.hybris.platform.integrationservices.util.IntegrationTestUtil;
import de.hybris.platform.integrationservices.util.JsonObject;
import de.hybris.platform.odata2services.odata.schema.SchemaGenerator;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.type.TypeService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

@IntegrationTest
public class DefaultPersistenceIntegrationObjectJsonGeneratorIntegrationTest extends ServicelayerTest
{
	private static final String READ_SERVICE = "readService";
	private static final String JSON_GENERATOR = "jsonGenerator";
	private static final String TEST_NAME = "DefaultIntegrationObjectJsonGenerator";
	private static final String BASIC_JSON_IO = TEST_NAME + "_ProductIO_1";
	private static final String MAP_JSON_IO = TEST_NAME + "_ProductIO_2";
	private static final String COLLECTION_JSON_IO = TEST_NAME + "_ProductIO_3";
	private static final String MAP_NOT_SUPPORTED_JSON_IO = TEST_NAME + "_ProductIO_4";
	private static final String CIRCULAR_DEPENDENCY_JSON_IO = TEST_NAME + "_OrderIO_1";
	private static final String CIRCULAR_DEPENDENCY_COMPLEX_JSON_IO = TEST_NAME + "_OrderIO_2";
	private static final String PRODUCT_CLASSIFICATION_IO = TEST_NAME + "_ProductIO_5";
	private static final String PRODUCT_CLASSIFICATION_ENUM_REF_AND_MULTIVALUE_IO = TEST_NAME + "_ProductIO_6";
	private static final String READ_ONLY_JSON_IO = TEST_NAME + "_ProductIO_7";
	private static final String VA_JSON_IO = TEST_NAME + "_ProductIO_8";
	private static final String MULTI_TYPE_DEFINITION_IO = TEST_NAME + "_CategoryIO_1";
	private static final String CLASSIFICATION_SYSTEM = TEST_NAME + "_Electronics";

	@Resource
	private FlexibleSearchService flexibleSearchService;
	@Resource
	private TypeService typeService;
	@Resource
	private SchemaGenerator oDataSchemaGenerator;
	@Resource
	private ReadOnlyAttributesConfiguration defaultIntegrationServicesConfiguration;

	@Rule
	public BeanRegisteringRule rule = new BeanRegisteringRule().register(ReadService.class, READ_SERVICE)
	                                                           .register(DefaultPersistenceIntegrationObjectJsonGenerator.class,
			                                                           JSON_GENERATOR);

	private DefaultPersistenceIntegrationObjectJsonGenerator jsonGenerator;
	private Gson gson;

	@Before
	public void setUp()
	{
		ReadService readService = (ReadService) rule.getBean(ReadService.class, READ_SERVICE);
		readService.setFlexibleSearchService(flexibleSearchService);
		readService.setTypeService(typeService);
		readService.setODataDefaultSchemaGenerator(oDataSchemaGenerator);
		readService.setReadOnlyAttributesConfiguration(defaultIntegrationServicesConfiguration);

		jsonGenerator = (DefaultPersistenceIntegrationObjectJsonGenerator) rule.getBean(
				DefaultPersistenceIntegrationObjectJsonGenerator.class, JSON_GENERATOR);
		gson = new GsonBuilder().setPrettyPrinting().create();
	}

	@After
	public void tearDown()
	{
		Arrays.asList(BASIC_JSON_IO, MAP_JSON_IO, COLLECTION_JSON_IO, MAP_NOT_SUPPORTED_JSON_IO,
				CIRCULAR_DEPENDENCY_JSON_IO, CIRCULAR_DEPENDENCY_COMPLEX_JSON_IO, PRODUCT_CLASSIFICATION_IO,
				PRODUCT_CLASSIFICATION_ENUM_REF_AND_MULTIVALUE_IO, READ_ONLY_JSON_IO, MULTI_TYPE_DEFINITION_IO)
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

	private void setBasicJSONTest() throws ImpExException
	{
		importImpEx(
				"$ioCode=" + BASIC_JSON_IO,
				"INSERT_UPDATE IntegrationObject; code[unique = true];",
				"                               ; $ioCode",
				"$io = integrationObject(code)[unique = true]",
				"INSERT_UPDATE IntegrationObjectItem; $io    ; code[unique = true]; type(code)    ; root[default = false]",
				"                                   ; $ioCode; CatalogVersion; CatalogVersion; ;",
				"                                   ; $ioCode; Product       ; Product       ; true;",
				"                                   ; $ioCode; Catalog       ; Catalog       ; ;",
				"$integrationItem = integrationObjectItem(integrationObject(code), code)[unique = true]",
				"$attrName = attributeName[unique = true]",
				"$attrDescriptor = attributeDescriptor(enclosingType(code), qualifier)",
				"$attributeType=returnIntegrationObjectItem(integrationObject(code), code)",
				"INSERT_UPDATE IntegrationObjectItemAttribute; $integrationItem      ; $attrName         ; $attrDescriptor           ; $attributeType         ; unique[default = false]; autoCreate[default = false]",
				"                                            ; $ioCode:CatalogVersion; active            ; CatalogVersion:active     ;                        ; ;",
				"                                            ; $ioCode:CatalogVersion; catalog           ; CatalogVersion:catalog    ; $ioCode:Catalog        ; true;",
				"                                            ; $ioCode:CatalogVersion; version           ; CatalogVersion:version    ;                        ; true;",
				"                                            ; $ioCode:Product       ; manufacturerAID   ; Product:manufacturerAID   ;                        ; ;",
				"                                            ; $ioCode:Product       ; numberContentUnits; Product:numberContentUnits;                        ; ;",
				"                                            ; $ioCode:Product       ; catalogVersion    ; Product:catalogVersion    ; $ioCode:CatalogVersion ; true;",
				"                                            ; $ioCode:Product       ; offlineDate       ; Product:offlineDate       ;                        ; ;",
				"                                            ; $ioCode:Product       ; code              ; Product:code              ;                        ; true;",
				"                                            ; $ioCode:Product       ; numberOfReviews   ; Product:numberOfReviews   ;                        ; ;",
				"                                            ; $ioCode:Catalog       ; id                ; Catalog:id                ;                        ; true;"
		);
	}

	private void setMapJSONTest() throws ImpExException
	{
		importImpEx(
				"$ioCode=" + MAP_JSON_IO,
				"INSERT_UPDATE IntegrationObject; code[unique = true];",
				"                               ; $ioCode",
				"$io = integrationObject(code)[unique = true]",
				"INSERT_UPDATE IntegrationObjectItem; $io    ; code[unique = true]; type(code)    ; root[default = false]",
				"                                   ; $ioCode; Catalog       ; Catalog       ; ;  ",
				"                                   ; $ioCode; CatalogVersion; CatalogVersion; ;  ",
				"                                   ; $ioCode; Product       ; Product       ; true;  ",
				"$integrationItem = integrationObjectItem(integrationObject(code), code)[unique = true]",
				"$attrName = attributeName[unique = true]",
				"$attrDescriptor = attributeDescriptor(enclosingType(code), qualifier)",
				"$attributeType=returnIntegrationObjectItem(integrationObject(code), code)",
				"INSERT_UPDATE IntegrationObjectItemAttribute; $integrationItem      ; $attrName     ; $attrDescriptor       ; $attributeType        ; unique[default = false]; autoCreate[default = false]",
				"                                            ; $ioCode:Catalog       ; id            ; Catalog:id            ;                       ; true;  ",
				"                                            ; $ioCode:CatalogVersion; catalog       ; CatalogVersion:catalog; $ioCode:Catalog       ; true;  ",
				"                                            ; $ioCode:CatalogVersion; version       ; CatalogVersion:version;                       ; true;  ",
				"                                            ; $ioCode:Product       ; catalogVersion; Product:catalogVersion; $ioCode:CatalogVersion; true;  ",
				"                                            ; $ioCode:Product       ; code          ; Product:code          ;                       ; true;  ",
				"                                            ; $ioCode:Product       ; description   ; Product:description   ;                       ; ;  ",
				"                                            ; $ioCode:Product       ; deliveryTime  ; Product:deliveryTime  ;                       ; ;  "
		);
	}

	private void setCollectionJSONTest() throws ImpExException
	{
		importImpEx(
				"$ioCode=" + COLLECTION_JSON_IO,
				"INSERT_UPDATE IntegrationObject; code[unique = true];",
				"                               ; $ioCode",
				"$io = integrationObject(code)[unique = true]",
				"INSERT_UPDATE IntegrationObjectItem; $io    ; code[unique = true]; type(code)    ; root[default = false]",
				"                                   ; $ioCode; DeliveryMode  ; DeliveryMode  ; ;  ",
				"                                   ; $ioCode; CatalogVersion; CatalogVersion; ;  ",
				"                                   ; $ioCode; Media         ; Media         ; ;  ",
				"                                   ; $ioCode; Catalog       ; Catalog       ; ;  ",
				"                                   ; $ioCode; Product       ; Product       ; true;  ",
				"$integrationItem = integrationObjectItem(integrationObject(code), code)[unique = true]",
				"$attrName = attributeName[unique = true]",
				"$attrDescriptor = attributeDescriptor(enclosingType(code), qualifier)",
				"$attributeType=returnIntegrationObjectItem(integrationObject(code), code)",
				"INSERT_UPDATE IntegrationObjectItemAttribute; $integrationItem      ; $attrName     ; $attrDescriptor       ; $attributeType        ; unique[default = false]; autoCreate[default = false]",
				"                                            ; $ioCode:DeliveryMode  ; code          ; DeliveryMode:code     ;                       ; true;  ",
				"                                            ; $ioCode:CatalogVersion; version       ; CatalogVersion:version;                       ; true;  ",
				"                                            ; $ioCode:CatalogVersion; catalog       ; CatalogVersion:catalog; $ioCode:Catalog       ; true;  ",
				"                                            ; $ioCode:Media         ; catalogVersion; Media:catalogVersion  ; $ioCode:CatalogVersion; true;  ",
				"                                            ; $ioCode:Media         ; code          ; Media:code            ;                       ; true;  ",
				"                                            ; $ioCode:Catalog       ; id            ; Catalog:id            ;                       ; true;  ",
				"                                            ; $ioCode:Product       ; code          ; Product:code          ;                       ; true;  ",
				"                                            ; $ioCode:Product       ; catalogVersion; Product:catalogVersion; $ioCode:CatalogVersion; true;  ",
				"                                            ; $ioCode:Product       ; data_sheet    ; Product:data_sheet    ; $ioCode:Media         ; ;  ",
				"                                            ; $ioCode:Product       ; deliveryTime  ; Product:deliveryTime  ;                       ; ;  ",
				"                                            ; $ioCode:Product       ; deliveryModes ; Product:deliveryModes ; $ioCode:DeliveryMode  ; ;  "
		);
	}

	private void setMapNotSupportedJSONTest() throws ImpExException
	{
		importImpEx(
				"$ioCode=" + MAP_NOT_SUPPORTED_JSON_IO,
				"INSERT_UPDATE IntegrationObject; code[unique = true];",
				"                               ; $ioCode",
				"$io = integrationObject(code)[unique = true]",
				"INSERT_UPDATE IntegrationObjectItem; $io    ; code[unique = true]; type(code); root[default = false]",
				"                                   ; $ioCode; Product       ; Product        ; true",
				"                                   ; $ioCode; Catalog       ; Catalog        ;",
				"                                   ; $ioCode; CatalogVersion; CatalogVersion ;",
				"                                   ; $ioCode; ArticleStatus ; ArticleStatus  ;",
				"$integrationItem = integrationObjectItem(integrationObject(code), code)[unique = true]",
				"$attrName = attributeName[unique = true]",
				"$attrDescriptor = attributeDescriptor(enclosingType(code), qualifier)",
				"$attributeType=returnIntegrationObjectItem(integrationObject(code), code)",
				"INSERT_UPDATE IntegrationObjectItemAttribute[disable.interceptor.types=validate]; $integrationItem      ; $attrName     ; $attrDescriptor              ; $attributeType         ; unique[default = false]; autoCreate[default = false]",
				"                                                                                ; $ioCode:Product       ; articleStatus ; Product:articleStatus        ; $ioCode:ArticleStatus; ;",
				"INSERT_UPDATE IntegrationObjectItemAttribute; $integrationItem      ; $attrName     ; $attrDescriptor              ; $attributeType         ; unique[default = false]; autoCreate[default = false]",
				"                                            ; $ioCode:Product       ; code          ; Product:code                 ;                        ; true;  ",
				"                                            ; $ioCode:Product       ; catalogVersion; Product:catalogVersion       ; $ioCode:CatalogVersion ; true;  ",
				"                                            ; $ioCode:Product       ; averageRating ; Product:averageRating        ;                        ; ;  ",
				"                                            ; $ioCode:Catalog       ; id            ; Catalog:id                   ;                        ; true;  ",
				"                                            ; $ioCode:CatalogVersion; generationDate; CatalogVersion:generationDate;                        ; ;  ",
				"                                            ; $ioCode:CatalogVersion; version       ; CatalogVersion:version       ;                        ; true;  ",
				"                                            ; $ioCode:CatalogVersion; generatorInfo ; CatalogVersion:generatorInfo ;                        ; ;  ",
				"                                            ; $ioCode:CatalogVersion; inclAssurance ; CatalogVersion:inclAssurance ;                        ; ;  ",
				"                                            ; $ioCode:CatalogVersion; catalog       ; CatalogVersion:catalog       ; $ioCode:Catalog        ; true;",
				"                                            ; $ioCode:ArticleStatus ; code          ; ArticleStatus:code           ;                        ; true;"
		);
	}

	private void setCircularDependencyJSONTest() throws ImpExException
	{
		importImpEx(
				"$ioCode=" + CIRCULAR_DEPENDENCY_JSON_IO,
				"INSERT_UPDATE IntegrationObject; code[unique = true];",
				"                               ; $ioCode",
				"$io = integrationObject(code)[unique = true]",
				"INSERT_UPDATE IntegrationObjectItem; $io    ; code[unique = true]; type(code); root[default = false]",
				"                                   ; $ioCode; OrderEntry         ; OrderEntry; ;  ",
				"                                   ; $ioCode; Order              ; Order     ; true;  ",
				"$integrationItem = integrationObjectItem(integrationObject(code), code)[unique = true]",
				"$attrName = attributeName[unique = true]",
				"$attrDescriptor = attributeDescriptor(enclosingType(code), qualifier)",
				"$attributeType=returnIntegrationObjectItem(integrationObject(code), code)",
				"INSERT_UPDATE IntegrationObjectItemAttribute; $integrationItem  ; $attrName; $attrDescriptor ; $attributeType    ; unique[default = false]; autoCreate[default = false]",
				"                                            ; $ioCode:OrderEntry; order    ; OrderEntry:order; $ioCode:Order     ; true;  ",
				"                                            ; $ioCode:Order     ; entries  ; Order:entries   ; $ioCode:OrderEntry; ;  ",
				"                                            ; $ioCode:Order     ; code     ; Order:code      ;                   ; true;  "
		);
	}

	private void setCircularDependencyComplexJSONTest() throws ImpExException
	{
		importImpEx(
				"$ioCode=" + CIRCULAR_DEPENDENCY_COMPLEX_JSON_IO,
				"INSERT_UPDATE IntegrationObject; code[unique = true];",
				"                               ; $ioCode",
				"$io = integrationObject(code)[unique = true]",
				"INSERT_UPDATE IntegrationObjectItem; $io    ; code[unique = true]; type(code); root[default = false]",
				"                                   ; $ioCode; Order     ; Order     ; true;  ",
				"                                   ; $ioCode; User      ; User      ; ;  ",
				"                                   ; $ioCode; OrderEntry; OrderEntry; ;  ",
				"$integrationItem = integrationObjectItem(integrationObject(code), code)[unique = true]",
				"$attrName = attributeName[unique = true]",
				"$attrDescriptor = attributeDescriptor(enclosingType(code), qualifier)",
				"$attributeType=returnIntegrationObjectItem(integrationObject(code), code)",
				"INSERT_UPDATE IntegrationObjectItemAttribute; $integrationItem  ; $attrName      ; $attrDescriptor      ; $attributeType    ; unique[default = false]; autoCreate[default = false]",
				"                                            ; $ioCode:Order     ; entries        ; Order:entries        ; $ioCode:OrderEntry; ;  ",
				"                                            ; $ioCode:Order     ; user           ; Order:user           ; $ioCode:User      ; true;  ",
				"                                            ; $ioCode:Order     ; originalVersion; Order:originalVersion; $ioCode:Order     ; ;  ",
				"                                            ; $ioCode:Order     ; placedBy       ; Order:placedBy       ; $ioCode:User      ; ;  ",
				"                                            ; $ioCode:User      ; uid            ; User:uid             ;                   ; true;  ",
				"                                            ; $ioCode:OrderEntry; order          ; OrderEntry:order     ; $ioCode:Order     ; true;  "
		);
	}

	private void setProductIOClassificationTest() throws ImpExException
	{
		importImpEx(
				"# The following ImpEx provides a full example of",
				"# 1) Creating classifications",
				"# 2) Creating Integration Object with classification attributes",
				"# ***************************************************************************************",
				"#     Set up the classifications",
				"# ***************************************************************************************",
				"$SYSTEM=" + CLASSIFICATION_SYSTEM,
				"$VERSION=Staged",
				"$SYSTEM_VERSION=$SYSTEM:$VERSION",
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
				"                                      ; $SYSTEM_VERSION:dimensions; $SYSTEM_VERSION:depth    ; $SYSTEM_VERSION:centimeters     ; number",
				"# ***************************************************************************************",
				"#     Set up integration objects with regular attributes and classification attributes",
				"# ***************************************************************************************",
				"$ioCode=" + PRODUCT_CLASSIFICATION_IO,
				"INSERT_UPDATE IntegrationObject; code[unique = true];",
				"                               ; $ioCode",
				"$io = integrationObject(code)[unique = true]",
				"INSERT_UPDATE IntegrationObjectItem; $io    ; code[unique = true]; type(code); root[default = false]",
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
				"$SYSTEM_VERSION=" + CLASSIFICATION_SYSTEM + ":Staged",
				"$item=integrationObjectItem(integrationObject(code), code)",
				"$systemVersionHeader=systemVersion(catalog(id), version)",
				"$classificationClassHeader=classificationClass(catalogVersion(catalog(id), version), code)",
				"$classificationAttributeHeader=classificationAttribute($systemVersionHeader, code)",
				"$classificationAssignment=classAttributeAssignment($classificationClassHeader, $classificationAttributeHeader)",
				"INSERT_UPDATE IntegrationObjectItemClassificationAttribute; $item[unique = true]; attributeName[unique = true]; $classificationAssignment",
				"                                                          ; $ioCode:Product         ; height                      ; $SYSTEM_VERSION:dimensions:$SYSTEM_VERSION:height",
				"                                                          ; $ioCode:Product         ; depth                       ; $SYSTEM_VERSION:dimensions:$SYSTEM_VERSION:depth",
				"                                                          ; $ioCode:Product         ; width                       ; $SYSTEM_VERSION:dimensions:$SYSTEM_VERSION:width"
		);
	}

	private void setProductIOClassificationEnumReferenceAndMultivalued() throws ImpExException
	{
		importImpEx(
				"# The following ImpEx provides a full example of",
				"# 1) Creating classifications",
				"# 2) Creating Integration Object with classification attributes",
				"# ***************************************************************************************",
				"#     Set up the classifications",
				"# ***************************************************************************************",
				"$SYSTEM=" + CLASSIFICATION_SYSTEM,
				"$VERSION=Staged",
				"$SYSTEM_VERSION=$SYSTEM:$VERSION",
				"$catalogVersionHeader=catalogVersion(catalog(id), version)",
				"$systemVersionHeader=systemVersion(catalog(id), version)",
				"INSERT_UPDATE ClassificationSystem; id[unique = true]",
				"                                  ; $SYSTEM",
				"                                  ; Alternative",
				"INSERT_UPDATE ClassificationSystemVersion; catalog(id)[unique = true]; version[unique = true]",
				"                                         ; $SYSTEM                   ; $VERSION",
				"                                         ; Alternative               ; Products",
				"INSERT_UPDATE ClassificationClass; code[unique = true]; $catalogVersionHeader[unique = true]",
				"                                 ; dimensions         ; $SYSTEM_VERSION",
				"                                 ; QA                 ; $SYSTEM_VERSION",
				"                                 ; alternativeProduct ; Alternative:Products",


				"INSERT_UPDATE ClassificationAttributeUnit; $systemVersionHeader[unique = true]; code[unique = true]; symbol; unitType",
				"                                         ; $SYSTEM_VERSION                    ; centimeters        ; cm    ; measurement",
				"INSERT_UPDATE ClassificationAttribute; code[unique = true]; $systemVersionHeader[unique = true]",
				"                                     ;depth               ; $SYSTEM_VERSION",
				"                                     ;bool               ; $SYSTEM_VERSION",
				"                                     ;valueList           ; $SYSTEM_VERSION",
				"                                     ;ReferenceTypeM      ; $SYSTEM_VERSION",
				"                                     ;tester              ; $SYSTEM_VERSION",
				"                                     ;dateM              ; $SYSTEM_VERSION",
				"                                     ;valueListM         ; $SYSTEM_VERSION",
				"                                     ;stringType          ; $SYSTEM_VERSION",
				"                                     ;numberM             ; $SYSTEM_VERSION",
				"                                     ;stringTypeM         ; $SYSTEM_VERSION",
				"                                     ;date                ; $SYSTEM_VERSION",
				"                                     ;classificationName  ; Alternative:Products",
				"                                     ;boolM              ; $SYSTEM_VERSION",

				"$class=classificationClass($catalogVersionHeader, code)",
				"$attribute=classificationAttribute($systemVersionHeader, code)",
				"INSERT_UPDATE ClassAttributeAssignment; $class[unique = true]     ; $attribute[unique = true]; unit($systemVersionHeader, code); attributeType(code);multiValued ; referenceType(code)",
				"                                      ; $SYSTEM_VERSION:dimensions; $SYSTEM_VERSION:dateM    ;      ; date;true",
				"                                      ; $SYSTEM_VERSION:dimensions; $SYSTEM_VERSION:stringTypeM   ;      ; string;true",
				"                                      ; $SYSTEM_VERSION:dimensions; $SYSTEM_VERSION:numberM    ;      ; number;true",
				"                                      ; $SYSTEM_VERSION:dimensions; $SYSTEM_VERSION:boolM    ;      ; boolean;true",
				"                                      ; $SYSTEM_VERSION:QA; $SYSTEM_VERSION:tester    ;      ; reference ; false ; Employee",
				"                                      ; $SYSTEM_VERSION:dimensions; $SYSTEM_VERSION:valueListM    ;      ; enum;true",
				"                                      ; $SYSTEM_VERSION:QA; $SYSTEM_VERSION:ReferenceTypeM    ;      ; reference;true;Employee",
				"                                      ; $SYSTEM_VERSION:dimensions; $SYSTEM_VERSION:depth    ; $SYSTEM_VERSION:centimeters     ; number;false ;",
				"                                      ; $SYSTEM_VERSION:dimensions; $SYSTEM_VERSION:bool    ;      ; boolean; false ;",
				"                                      ; $SYSTEM_VERSION:dimensions; $SYSTEM_VERSION:valueList    ;      ; enum; false ;",
				"                                      ; $SYSTEM_VERSION:dimensions; $SYSTEM_VERSION:stringType    ;      ; string; false ;",
				"                                      ; $SYSTEM_VERSION:dimensions; $SYSTEM_VERSION:date    ;      ; date; false;",
				"                                      ; Alternative:Products:alternativeProduct;Alternative:Products:classificationName; ;string; false ;",
				"$ioCode=" + PRODUCT_CLASSIFICATION_ENUM_REF_AND_MULTIVALUE_IO,
				"INSERT_UPDATE IntegrationObject; code[unique = true];",
				"                               ; $ioCode",
				"$io = integrationObject(code)[unique = true]",
				"INSERT_UPDATE IntegrationObjectItem; $io    ; code[unique = true]; type(code); root[default = false]",
				"                                   ; $ioCode ; Catalog         ; Catalog         ;   ;",
				"                                   ; $ioCode ; Product         ; Product         ; true  ;",
				"                                   ; $ioCode ; Employee        ; Employee        ;   ;",
				"                                   ; $ioCode ; CatalogVersion  ; CatalogVersion  ;   ;",

				"INSERT_UPDATE IntegrationObjectItemAttribute; integrationObjectItem(integrationObject(code), code)[unique = true]; attributeName[unique = true]; attributeDescriptor(enclosingType(code), qualifier); returnIntegrationObjectItem(integrationObject(code), code); unique[default = false]; autoCreate[default = false]",
				"                                            ; $ioCode:Catalog         ; id              ; Catalog:id              ;                                   ; true  ;",
				"                                            ; $ioCode:Product         ; code            ; Product:code            ;                                   ; true  ;",
				"                                            ; $ioCode:Product         ; catalogVersion  ; Product:catalogVersion  ; $ioCode:CatalogVersion  ; true  ;",
				"                                            ; $ioCode:Employee        ; uid             ; Employee:uid            ;                                   ; true  ;",
				"                                            ; $ioCode:CatalogVersion  ; catalog         ; CatalogVersion:catalog  ; $ioCode:Catalog         ; true  ;",
				"                                            ; $ioCode:CatalogVersion  ; version         ; CatalogVersion:version  ;                                   ; true  ;",

				"$SYSTEM_VERSION=" + CLASSIFICATION_SYSTEM + ":Staged",
				"$item=integrationObjectItem(integrationObject(code), code)",
				"$systemVersionHeader=systemVersion(catalog(id), version)",
				"$classificationClassHeader=classificationClass(catalogVersion(catalog(id), version), code)",
				"$classificationAttributeHeader=classificationAttribute($systemVersionHeader, code)",
				"$classificationAssignment=classAttributeAssignment($classificationClassHeader, $classificationAttributeHeader)",
				"INSERT_UPDATE IntegrationObjectItemClassificationAttribute; $item[unique = true]; attributeName[unique = true]; $classificationAssignment; returnIntegrationObjectItem(integrationObject(code), code)",
				"                                                          ; $ioCode:Product         ; bool                ; $SYSTEM_VERSION:dimensions:$SYSTEM_VERSION:bool",
				"                                                          ; $ioCode:Product         ; valueList           ; $SYSTEM_VERSION:dimensions:$SYSTEM_VERSION:valueList",
				"                                                          ; $ioCode:Product         ; ReferenceTypeM      ; $SYSTEM_VERSION:QA:$SYSTEM_VERSION:ReferenceTypeM;$ioCode:Employee",
				"                                                          ; $ioCode:Product         ; depth               ; $SYSTEM_VERSION:dimensions:$SYSTEM_VERSION:depth",
				"                                                          ; $ioCode:Product         ; tester              ; $SYSTEM_VERSION:QA:$SYSTEM_VERSION:tester; $ioCode:Employee",
				"                                                          ; $ioCode:Product         ; dateM               ; $SYSTEM_VERSION:dimensions:$SYSTEM_VERSION:dateM",
				"                                                          ; $ioCode:Product         ; valueListM          ; $SYSTEM_VERSION:dimensions:$SYSTEM_VERSION:valueListM",
				"                                                          ; $ioCode:Product         ; stringType          ; $SYSTEM_VERSION:dimensions:$SYSTEM_VERSION:stringType",
				"                                                          ; $ioCode:Product         ; numberM             ; $SYSTEM_VERSION:dimensions:$SYSTEM_VERSION:numberM",
				"                                                          ; $ioCode:Product         ; stringTypeM         ; $SYSTEM_VERSION:dimensions:$SYSTEM_VERSION:stringTypeM",
				"                                                          ; $ioCode:Product         ; date                ; $SYSTEM_VERSION:dimensions:$SYSTEM_VERSION:date",
				"                                                          ; $ioCode:Product         ; boolM               ; $SYSTEM_VERSION:dimensions:$SYSTEM_VERSION:boolM",
				"                                                          ; $ioCode:Product         ; classificationName  ; Alternative:Products:alternativeProduct:Alternative:Products:classificationName"
		);
	}

	private void setReadOnlyJSONTest() throws ImpExException
	{
		importImpEx(
				"$ioCode=" + READ_ONLY_JSON_IO,
				"INSERT_UPDATE IntegrationObject; code[unique = true];",
				"                               ; $ioCode",
				"$io = integrationObject(code)[unique = true]",
				"INSERT_UPDATE IntegrationObjectItem; $io    ; code[unique = true]; type(code); root[default = false]",
				"                                   ; $ioCode; Catalog       ; Catalog       ; ;",
				"                                   ; $ioCode; Product       ; Product       ; true;",
				"                                   ; $ioCode; CatalogVersion; CatalogVersion; ;",

				"INSERT_UPDATE IntegrationObjectItemAttribute; integrationObjectItem(integrationObject(code), code)[unique = true]; attributeName[unique = true]; attributeDescriptor(enclosingType(code), qualifier); returnIntegrationObjectItem(integrationObject(code), code); unique[default = false]; autoCreate[default = false]",
				"                                            ; $ioCode:Catalog       ; id            ; Catalog:id            ;                     ; true;",
				"                                            ; $ioCode:Product       ; creationtime  ; Product:creationtime  ;                     ; ;",
				"                                            ; $ioCode:Product       ; code          ; Product:code          ;                     ; true;",
				"                                            ; $ioCode:Product       ; catalogVersion; Product:catalogVersion; $ioCode:CatalogVersion; true;",
				"                                            ; $ioCode:Product       ; modifiedtime  ; Product:modifiedtime  ;                     ; ;",
				"                                            ; $ioCode:CatalogVersion; version       ; CatalogVersion:version;                     ; true;",
				"                                            ; $ioCode:CatalogVersion; catalog       ; CatalogVersion:catalog; $ioCode:Catalog       ; true;"
		);
	}

	private void setVirtualAttributeJSONTest() throws ImpExException
	{
		importImpEx(
				"$ioCode=" + VA_JSON_IO,
				"INSERT_UPDATE IntegrationObject; code[unique = true];",
				"                               ; $ioCode",
				"$io = integrationObject(code)[unique = true]",
				"INSERT_UPDATE IntegrationObjectItem; $io    ; code[unique = true]; type(code); root[default = false]",
				"                                   ; $ioCode; Catalog       ; Catalog       ; ;",
				"                                   ; $ioCode; Product       ; Product       ; true;",
				"                                   ; $ioCode; CatalogVersion; CatalogVersion; ;",

				"INSERT_UPDATE IntegrationObjectItemAttribute; integrationObjectItem(integrationObject(code), code)[unique = true]; attributeName[unique = true]; attributeDescriptor(enclosingType(code), qualifier); returnIntegrationObjectItem(integrationObject(code), code); unique[default = false]; autoCreate[default = false]",
				"                                            ; $ioCode:Catalog       ; id            ; Catalog:id            ;                     ; true;",
				"                                            ; $ioCode:Product       ; creationtime  ; Product:creationtime  ;                     ; ;",
				"                                            ; $ioCode:Product       ; code          ; Product:code          ;                     ; true;",
				"                                            ; $ioCode:Product       ; catalogVersion; Product:catalogVersion; $ioCode:CatalogVersion; true;",
				"                                            ; $ioCode:Product       ; modifiedtime  ; Product:modifiedtime  ;                     ; ;",
				"                                            ; $ioCode:CatalogVersion; version       ; CatalogVersion:version;                     ; true;",
				"                                            ; $ioCode:CatalogVersion; catalog       ; CatalogVersion:catalog; $ioCode:Catalog       ; true;",

				"INSERT_UPDATE IntegrationObjectItemVirtualAttribute; integrationObjectItem(integrationObject(code), code)[unique = true]; attributeName[unique = true]; retrievalDescriptor(code)",
				"; $ioCode:Product       ; testVa; testVa",

				"INSERT_UPDATE IntegrationObjectVirtualAttributeDescriptor; code[unique = true]; logicLocation; type(code)",
				"; testVa; model://modelScriptForTest; java.lang.String "
		);
	}

	private void setMultiTypeDefinitionJSONTest() throws ImpExException
	{
		importImpEx(
				"$ioCode=" + MULTI_TYPE_DEFINITION_IO,
				"INSERT_UPDATE IntegrationObject; code[unique = true]; integrationType(code)",
				"; $ioCode; INBOUND",

				"INSERT_UPDATE IntegrationObjectItem; integrationObject(code)[unique = true]; code[unique = true]; type(code); root[default = false]; itemTypeMatch(code)",
				"; $ioCode	; CatalogVersion	; CatalogVersion	; 	    ; ALL_SUB_AND_SUPER_TYPES	;",
				"; $ioCode	; Category      	; Category      	; true	; ALL_SUB_AND_SUPER_TYPES	;",
				"; $ioCode	; Media2        	; Media         	; 	    ; ALL_SUB_AND_SUPER_TYPES	;",
				"; $ioCode	; Catalog       	; Catalog       	; 	    ; ALL_SUB_AND_SUPER_TYPES	;",
				"; $ioCode	; Media         	; Media         	; 	    ; ALL_SUB_AND_SUPER_TYPES	;",

				"INSERT_UPDATE IntegrationObjectItemAttribute; integrationObjectItem(integrationObject(code), code)[unique = true]; attributeName[unique = true]; attributeDescriptor(enclosingType(code), qualifier); returnIntegrationObjectItem(integrationObject(code), code); unique[default = false]; autoCreate[default = false]",
				"; $ioCode:CatalogVersion	; version       	; CatalogVersion:version 	;                                 	; true	;",
				"; $ioCode:CatalogVersion	; catalog       	; CatalogVersion:catalog 	; $ioCode:Catalog       	        ; true	;",
				"; $ioCode:Category      	; thumbnail     	; Category:thumbnail     	; $ioCode:Media2        	        ; 	    ;",
				"; $ioCode:Category      	; catalogVersion	; Category:catalogVersion	; $ioCode:CatalogVersion	        ; true	;",
				"; $ioCode:Category      	; picture       	; Category:picture       	; $ioCode:Media         	        ; 	    ;",
				"; $ioCode:Category      	; detail        	; Category:detail        	; $ioCode:Media         	        ; 	    ;",
				"; $ioCode:Category      	; code          	; Category:code          	;                                 	; true	;",
				"; $ioCode:Media2        	; code          	; Media:code             	;                                 	; true	;",
				"; $ioCode:Media2        	; altText       	; Media:altText          	;                                 	; 	    ;",
				"; $ioCode:Media2        	; catalogVersion	; Media:catalogVersion   	; $ioCode:CatalogVersion	        ; true	;",
				"; $ioCode:Catalog       	; id            	; Catalog:id             	;                                 	; true	;",
				"; $ioCode:Media         	; code          	; Media:code             	;                                 	; true	;",
				"; $ioCode:Media         	; catalogVersion	; Media:catalogVersion   	; $ioCode:CatalogVersion	        ; true	;",
				"; $ioCode:Media         	; dataPK        	; Media:dataPK           	;                                 	; 	    ;"
		);
	}

	@Test
	public void basicJsonTest() throws FileNotFoundException, ImpExException
	{
		setBasicJSONTest();
		final Map<String, Object> jsonObject = loadPayload("test/json/persistence/BasicJSONTestExpected.json");
		jsonObject.put("numberOfReviews", 123); //to fix Integer formatting
		final String expectedJsonString = gson.toJson(jsonObject);

		final IntegrationObjectModel integrationObjectModel = IntegrationObjectTestUtil.findIntegrationObjectByCode(
				BASIC_JSON_IO);

		final String actualJsonString = jsonGenerator.generateJson(integrationObjectModel);
		assertEquals(expectedJsonString, actualJsonString);

		IntegrationTestUtil.removeSafely(IntegrationObjectModel.class, object -> object.getCode().equals(BASIC_JSON_IO));
	}

	@Test
	public void mapJsonTest() throws FileNotFoundException, ImpExException
	{
		setMapJSONTest();
		final Map<String, Object> jsonObject = loadPayload("test/json/persistence/MapJSONTestExpected.json");
		final String expectedJsonString = gson.toJson(jsonObject);

		final IntegrationObjectModel integrationObjectModel = IntegrationObjectTestUtil.findIntegrationObjectByCode(MAP_JSON_IO);

		final String actualJsonString = jsonGenerator.generateJson(integrationObjectModel);
		assertEquals(expectedJsonString, actualJsonString);

		IntegrationTestUtil.removeSafely(IntegrationObjectModel.class, object -> object.getCode().equals(MAP_JSON_IO));
	}

	@Test
	public void collectionJsonTest() throws FileNotFoundException, ImpExException
	{
		setCollectionJSONTest();
		final Map<String, Object> jsonObject = loadPayload("test/json/persistence/CollectionJSONTestExpected.json");
		final String expectedJsonString = gson.toJson(jsonObject);

		final IntegrationObjectModel integrationObjectModel = IntegrationObjectTestUtil.findIntegrationObjectByCode(
				COLLECTION_JSON_IO);

		final String actualJsonString = jsonGenerator.generateJson(integrationObjectModel);

		assertEquals(expectedJsonString, actualJsonString);

		IntegrationTestUtil.removeSafely(IntegrationObjectModel.class, object -> object.getCode().equals(COLLECTION_JSON_IO));
	}

	@Test
	public void mapOfMapsNotSupportedJsonTest() throws FileNotFoundException, ImpExException
	{
		setMapNotSupportedJSONTest();
		final Map<String, Object> jsonObject = loadPayload("test/json/persistence/MapNotSupportedJSONTestExpected.json");
		final String expectedJsonString = gson.toJson(jsonObject);

		final IntegrationObjectModel integrationObjectModel = IntegrationObjectTestUtil.findIntegrationObjectByCode(
				MAP_NOT_SUPPORTED_JSON_IO);

		final String actualJsonString = jsonGenerator.generateJson(integrationObjectModel);

		assertEquals(expectedJsonString, actualJsonString);

		IntegrationTestUtil.removeSafely(IntegrationObjectModel.class,
				object -> object.getCode().equals(MAP_NOT_SUPPORTED_JSON_IO));
	}

	@Test
	public void circularDependencyJsonTest() throws FileNotFoundException, ImpExException
	{
		setCircularDependencyJSONTest();
		final Map<String, Object> jsonObject = loadPayload("test/json/persistence/CircularDependencyJSONTestExpected.json");
		final String expectedJsonString = gson.toJson(jsonObject);

		final IntegrationObjectModel integrationObjectModel = IntegrationObjectTestUtil.findIntegrationObjectByCode(
				CIRCULAR_DEPENDENCY_JSON_IO);

		final String actualJsonString = jsonGenerator.generateJson(integrationObjectModel);

		assertEquals(expectedJsonString, actualJsonString);

		IntegrationTestUtil.removeSafely(IntegrationObjectModel.class,
				object -> object.getCode().equals(CIRCULAR_DEPENDENCY_JSON_IO));
	}

	@Test
	public void circularDependencyComplexJsonTest() throws FileNotFoundException, ImpExException
	{
		setCircularDependencyComplexJSONTest();
		final Map<String, Object> jsonObject = loadPayload(
				"test/json/persistence/CircularDependencyComplexJSONTestExpected.json");
		final String expectedJsonString = gson.toJson(jsonObject);

		final IntegrationObjectModel integrationObjectModel = IntegrationObjectTestUtil.findIntegrationObjectByCode(
				CIRCULAR_DEPENDENCY_COMPLEX_JSON_IO);

		final String actualJsonString = jsonGenerator.generateJson(integrationObjectModel);

		assertEquals(expectedJsonString, actualJsonString);

		IntegrationTestUtil.removeSafely(IntegrationObjectModel.class,
				object -> object.getCode().equals(CIRCULAR_DEPENDENCY_COMPLEX_JSON_IO));
	}

	@Test
	public void classificationJsonTest() throws FileNotFoundException, ImpExException
	{
		setProductIOClassificationTest();
		final Map<String, Object> jsonObject = loadPayload("test/json/persistence/ProductIOClassificationJSONTestExpected.json");
		final String expectedJsonString = gson.toJson(jsonObject);

		final IntegrationObjectModel integrationObjectModel = IntegrationObjectTestUtil.findIntegrationObjectByCode(
				PRODUCT_CLASSIFICATION_IO);

		final String actualJsonString = jsonGenerator.generateJson(integrationObjectModel);

		assertEquals(expectedJsonString, actualJsonString);

		IntegrationTestUtil.removeSafely(IntegrationObjectModel.class,
				object -> object.getCode().equals(PRODUCT_CLASSIFICATION_IO));
	}

	@Test
	public void classificationCollectionEnumReferenceJsonTest() throws FileNotFoundException, ImpExException
	{
		setProductIOClassificationEnumReferenceAndMultivalued();
		final Map<String, Object> jsonObject = loadPayload(
				"test/json/persistence/ProductIOClassificationEnumReferenceAndMultivalued.json");
		final String expectedJsonString = gson.toJson(jsonObject);

		final IntegrationObjectModel integrationObjectModel = IntegrationObjectTestUtil.findIntegrationObjectByCode(
				PRODUCT_CLASSIFICATION_ENUM_REF_AND_MULTIVALUE_IO);

		final String actualJsonString = jsonGenerator.generateJson(integrationObjectModel);

		assertEquals(expectedJsonString, actualJsonString);

		IntegrationTestUtil.removeSafely(IntegrationObjectModel.class,
				object -> object.getCode().equals(PRODUCT_CLASSIFICATION_ENUM_REF_AND_MULTIVALUE_IO));
	}

	@Test
	public void readOnlyJsonTest() throws FileNotFoundException, ImpExException
	{
		setReadOnlyJSONTest();
		final Map<String, Object> jsonObject = loadPayload("test/json/persistence/ReadOnlyJSONTestExpected.json");
		final String expectedJsonString = gson.toJson(jsonObject);

		final IntegrationObjectModel integrationObjectModel = IntegrationObjectTestUtil.findIntegrationObjectByCode(
				READ_ONLY_JSON_IO);

		final String actualJsonString = jsonGenerator.generateJson(integrationObjectModel);

		assertEquals(expectedJsonString, actualJsonString);

		IntegrationTestUtil.removeSafely(IntegrationObjectModel.class, object -> object.getCode().equals(READ_ONLY_JSON_IO));
	}

	@Test
	public void virtualAttributeJsonTest() throws FileNotFoundException, ImpExException
	{
		setVirtualAttributeJSONTest();
		// VA wouldn't be shown so the expected json is the same as readonly
		final Map<String, Object> jsonObject = loadPayload("test/json/persistence/ReadOnlyJSONTestExpected.json");
		final String expectedJsonString = gson.toJson(jsonObject);

		final IntegrationObjectModel integrationObjectModel = IntegrationObjectTestUtil.findIntegrationObjectByCode(VA_JSON_IO);

		final String actualJsonString = jsonGenerator.generateJson(integrationObjectModel);

		assertEquals(expectedJsonString, actualJsonString);

		IntegrationTestUtil.removeSafely(IntegrationObjectModel.class, object -> object.getCode().equals(VA_JSON_IO));
	}

	@Test
	public void multiTypeDefinitionJsonTest() throws FileNotFoundException, ImpExException
	{
		setMultiTypeDefinitionJSONTest();
		final Map<String, Object> jsonObject = loadPayload(
				"test/json/persistence/MultiTypeDefinitionJSONTestExpected.json");

		final IntegrationObjectModel integrationObjectModel = IntegrationObjectTestUtil.findIntegrationObjectByCode(
				MULTI_TYPE_DEFINITION_IO);

		final String actualJsonString = jsonGenerator.generateJson(integrationObjectModel);
		final JsonObject actualJsonObject = JsonObject.createFrom(actualJsonString);

		final boolean thumbnailAltTextExists = actualJsonObject.exists("thumbnail.altText");
		final boolean pictureAltTextDoesNotExist = !actualJsonObject.exists("picture.altText");
		final boolean detailAltTextDoesNotExist = !actualJsonObject.exists("detail[0].altText");
		final boolean thumbnailDataPKDoesNotExist = !actualJsonObject.exists("thumbnail.dataPK");
		final boolean pictureDataPKExists = actualJsonObject.exists("picture.dataPK");
		final boolean detailDataPKExists = actualJsonObject.exists("detail[0].dataPK");

		assertTrue(thumbnailAltTextExists);
		assertTrue(pictureAltTextDoesNotExist);
		assertTrue(detailAltTextDoesNotExist);
		assertTrue(thumbnailDataPKDoesNotExist);
		assertTrue(pictureDataPKExists);
		assertTrue(detailDataPKExists);

		IntegrationTestUtil.removeSafely(IntegrationObjectModel.class,
				object -> object.getCode().equals(MULTI_TYPE_DEFINITION_IO));
	}

	public static Map<String, Object> loadPayload(final String payloadLocation) throws FileNotFoundException
	{
		final ClassLoader classLoader = DefaultPersistenceIntegrationObjectJsonGeneratorIntegrationTest.class.getClassLoader();
		final URL url = classLoader.getResource(payloadLocation);
		if (url != null)
		{
			final File file = new File(url.getFile());
			final Gson gson = new Gson();
			final JsonReader reader = new JsonReader(new FileReader(file));
			return gson.fromJson(reader, Map.class);
		}
		return null;
	}
}
