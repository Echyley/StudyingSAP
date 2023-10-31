/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modeling.utility;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.integrationservices.util.IntegrationObjectTestUtil;
import de.hybris.platform.integrationservices.util.IntegrationTestUtil;
import de.hybris.platform.servicelayer.ServicelayerTest;
import org.junit.After;
import org.junit.Test;

import java.util.Arrays;

import static de.hybris.platform.integrationservices.util.IntegrationTestUtil.importImpEx;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@IntegrationTest
public class IntegrationObjectRootUtilsIntegrationTest extends ServicelayerTest
{
	private static final String TEST_NAME = "IntegrationObjectRootUtils";
	private static final String ORG_UNIT_ROOT_IO = TEST_NAME + "_OrgUnitIO";
	private static final String NO_ROOT_IO = TEST_NAME + "_IO";
	private static final String MULTI_ROOT_IO = TEST_NAME + "_MultiRootIO";
	private static final String SINGLE_ROOT_CIRCULAR_IO = TEST_NAME + "_SingleRootCircularIO";

	@After
	public void tearDown()
	{
		Arrays.asList(ORG_UNIT_ROOT_IO, NO_ROOT_IO, MULTI_ROOT_IO, SINGLE_ROOT_CIRCULAR_IO)
				.forEach(objectCode -> IntegrationTestUtil.removeSafely(IntegrationObjectModel.class, it -> it.getCode().equals(objectCode)));
	}

	private void setSingleRootTestImpex() throws ImpExException
	{
		importImpEx(
				"$ioCode=" + ORG_UNIT_ROOT_IO,
				"INSERT_UPDATE IntegrationObject; code[unique = true];",
				"                               ; $ioCode",
				"$io = integrationObject(code)",
				"INSERT_UPDATE IntegrationObjectItem; $io[unique = true]; code[unique = true]; type(code); root[default = false] ",
				"; $ioCode; OrgUnit; OrgUnit; true;  ",
				"; $ioCode; Address; Address; ;  ",

				"INSERT_UPDATE IntegrationObjectItemAttribute; integrationObjectItem(integrationObject(code), code)[unique = true]; attributeName[unique = true]; attributeDescriptor(enclosingType(code), qualifier); returnIntegrationObjectItem(integrationObject(code), code); unique[default = false]; autoCreate[default = false] ",
				"; $ioCode:OrgUnit; uid           ; OrgUnit:uid           ;                  ; true;  ",
				"; $ioCode:OrgUnit; contactAddress; OrgUnit:contactAddress; $ioCode:Address; ;  ",
				"; $ioCode:Address; fax           ; Address:fax           ;                  ; ;  ",
				"; $ioCode:Address; company       ; Address:company       ;                  ; ;  ",
				"; $ioCode:Address; cellphone     ; Address:cellphone     ;                  ; ;  ",
				"; $ioCode:Address; email         ; Address:email         ;                  ; true;  "
		);
	}

	private void setNoRootTestImpex() throws ImpExException
	{
		importImpEx(
				"$ioCode=" + NO_ROOT_IO,
				"INSERT_UPDATE IntegrationObject; code[unique = true]",
				"; $ioCode",

				"$io = integrationObject(code)",
				"INSERT_UPDATE IntegrationObjectItem; $io[unique = true]; code[unique = true]; type(code); root[default = false] ",
				"; $ioCode; Product       ; Product       ; ;",
				"; $ioCode; Category      ; Category      ; ;",
				"; $ioCode; Catalog       ; Catalog       ; ;",
				"; $ioCode; CatalogVersion; CatalogVersion; ;",

				"INSERT_UPDATE IntegrationObjectItemAttribute; integrationObjectItem(integrationObject(code), code)[unique = true]; attributeName[unique = true]; attributeDescriptor(enclosingType(code), qualifier); returnIntegrationObjectItem(integrationObject(code), code); unique[default = false]; autoCreate[default = false]",
				"; $ioCode:Product       ; code          ; Product:code          ;                                        ; ;",
				"; $ioCode:Product       ; catalogVersion; Product:catalogVersion; $ioCode:CatalogVersion; ;",
				"; $ioCode:Category      ; code          ; Category:code         ;                                        ; true;",
				"; $ioCode:Category      ; name          ; Category:name         ;                                        ; ;",
				"; $ioCode:Category      ; products      ; Category:products     ; $ioCode:Product       ; ;",
				"; $ioCode:Catalog       ; id            ; Catalog:id            ;                                        ; ;",
				"; $ioCode:CatalogVersion; catalog       ; CatalogVersion:catalog; $ioCode:Catalog       ; ;",
				"; $ioCode:CatalogVersion; version       ; CatalogVersion:version;                                        ; ;",
				"; $ioCode:CatalogVersion; active        ; CatalogVersion:active ;                                        ; ;"
		);
	}

	private void setMultiRootTestImpex() throws ImpExException
	{
		importImpEx(
				"$ioCode=" + MULTI_ROOT_IO,
				"INSERT_UPDATE IntegrationObject; code[unique = true]",
				"; $ioCode",

				"$io = integrationObject(code)",
				"INSERT_UPDATE IntegrationObjectItem; $io[unique = true]; code[unique = true]; type(code); root[default = false] ",
				"; $ioCode; Product       ; Product       ;   ;",
				"; $ioCode; Category      ; Category      ; true;",
				"; $ioCode; Catalog       ; Catalog       ; ;",
				"; $ioCode; CatalogVersion; CatalogVersion; ;",

				"INSERT_UPDATE IntegrationObjectItemAttribute; integrationObjectItem(integrationObject(code), code)[unique = true]; attributeName[unique = true]; attributeDescriptor(enclosingType(code), qualifier); returnIntegrationObjectItem(integrationObject(code), code); unique[default = false]; autoCreate[default = false]",
				"; $ioCode:Product       ; code          ; Product:code          ;                                        ; ;",
				"; $ioCode:Product       ; catalogVersion; Product:catalogVersion; $ioCode:CatalogVersion; ;",
				"; $ioCode:Category      ; code          ; Category:code         ;                                        ; true;",
				"; $ioCode:Category      ; name          ; Category:name         ;                                        ; ;",
				"; $ioCode:Category      ; products      ; Category:products     ; $ioCode:Product       ; ;",
				"; $ioCode:Catalog       ; id            ; Catalog:id            ;                                        ; ;",
				"; $ioCode:CatalogVersion; catalog       ; CatalogVersion:catalog; $ioCode:Catalog       ; ;",
				"; $ioCode:CatalogVersion; version       ; CatalogVersion:version;                                        ; ;",
				"; $ioCode:CatalogVersion; active        ; CatalogVersion:active ;                                        ; ;"
		);
	}

	private void setSingleRootCircularDepTestImpex() throws ImpExException
	{
		importImpEx(
				"$ioCode=" + SINGLE_ROOT_CIRCULAR_IO,
				"INSERT_UPDATE IntegrationObject; code[unique = true]",
				"; $ioCode",

				"$io = integrationObject(code)",
				"INSERT_UPDATE IntegrationObjectItem; $io[unique = true]; code[unique = true]; type(code); root[default = false] ",
				"; $ioCode; OrderEntry; OrderEntry; ;",
				"; $ioCode; Order     ; Order     ; true;",

				"INSERT_UPDATE IntegrationObjectItemAttribute; integrationObjectItem(integrationObject(code), code)[unique = true]; attributeName[unique = true]; attributeDescriptor(enclosingType(code), qualifier); returnIntegrationObjectItem(integrationObject(code), code); unique[default = false]; autoCreate[default = false]",
				"; $ioCode:OrderEntry; order  ; OrderEntry:order; $ioCode:Order     ; true;",
				"; $ioCode:Order     ; code   ; Order:code      ;                 ; true;",
				"; $ioCode:Order     ; entries; Order:entries   ; $ioCode:OrderEntry; ;"
		);
	}

	@Test
	public void getIntegrationObjectSingleBooleanRoot() throws ImpExException
	{
		setSingleRootTestImpex();
		IntegrationObjectModel integrationObjectModel = IntegrationObjectTestUtil.findIntegrationObjectByCode(
				ORG_UNIT_ROOT_IO);
		assertNotNull(integrationObjectModel);

		final String expectedRoot = "OrgUnit";
		final String actualRoot = IntegrationObjectRootUtils.resolveIntegrationObjectRoot(integrationObjectModel)
		                                                    .getRootItem()
		                                                    .getCode();

		assertEquals(ORG_UNIT_ROOT_IO, integrationObjectModel.getCode());
		assertEquals(expectedRoot, actualRoot);

		IntegrationTestUtil.removeSafely(IntegrationObjectModel.class,
				object -> object.getCode().equals(ORG_UNIT_ROOT_IO));

		IntegrationTestUtil.removeSafely(IntegrationObjectModel.class, object -> object.getCode().equals(ORG_UNIT_ROOT_IO));
	}

	@Test
	public void getIntegrationObjectNoBooleanRoot() throws ImpExException
	{
		setNoRootTestImpex();
		IntegrationObjectModel integrationObjectModel = IntegrationObjectTestUtil.findIntegrationObjectByCode(
				NO_ROOT_IO);
		assertNotNull(integrationObjectModel);

		final String expectedRoot = "Category";
		final String actualRoot = IntegrationObjectRootUtils.resolveIntegrationObjectRoot(integrationObjectModel)
		                                                    .getRootItem()
		                                                    .getCode();

		assertEquals(NO_ROOT_IO, integrationObjectModel.getCode());
		assertEquals(expectedRoot, actualRoot);

		IntegrationTestUtil.removeSafely(IntegrationObjectModel.class, object -> object.getCode().equals(NO_ROOT_IO));
	}

	@Test
	public void getIntegrationObjectMultiBooleanRoot() throws ImpExException
	{
		setMultiRootTestImpex();
		IntegrationObjectModel integrationObjectModel = IntegrationObjectTestUtil.findIntegrationObjectByCode(
				MULTI_ROOT_IO);
		assertNotNull(integrationObjectModel);

		integrationObjectModel.getItems().forEach(item -> {
			if (item.getCode().equals("Product"))
			{
				item.setRoot(true);
			}
		});

		final String expectedRoot = "Category";
		final String actualRoot = IntegrationObjectRootUtils.resolveIntegrationObjectRoot(integrationObjectModel)
		                                                    .getRootItem()
		                                                    .getCode();

		assertEquals(MULTI_ROOT_IO, integrationObjectModel.getCode());
		assertEquals(expectedRoot, actualRoot);

		IntegrationTestUtil.removeSafely(IntegrationObjectModel.class,
				object -> object.getCode().equals(MULTI_ROOT_IO));
	}

	@Test
	public void getIntegrationObjectSingleBooleanRootCircularDep() throws ImpExException
	{
		setSingleRootCircularDepTestImpex();
		IntegrationObjectModel integrationObjectModel = IntegrationObjectTestUtil.findIntegrationObjectByCode(
				SINGLE_ROOT_CIRCULAR_IO);
		assertNotNull(integrationObjectModel);

		final String expectedRoot = "Order";
		final String actualRoot = IntegrationObjectRootUtils.resolveIntegrationObjectRoot(integrationObjectModel)
		                                                    .getRootItem()
		                                                    .getCode();

		assertEquals(SINGLE_ROOT_CIRCULAR_IO, integrationObjectModel.getCode());
		assertEquals(expectedRoot, actualRoot);

		IntegrationTestUtil.removeSafely(IntegrationObjectModel.class,
				object -> object.getCode().equals(SINGLE_ROOT_CIRCULAR_IO));
	}
}
