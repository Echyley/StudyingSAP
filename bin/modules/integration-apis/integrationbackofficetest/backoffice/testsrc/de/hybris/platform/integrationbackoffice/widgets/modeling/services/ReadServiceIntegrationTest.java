/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modeling.services;

import static junit.framework.TestCase.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.integrationbackoffice.BeanRegisteringRule;
import de.hybris.platform.integrationservices.config.ReadOnlyAttributesConfiguration;
import de.hybris.platform.odata2services.odata.schema.SchemaGenerator;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.type.TypeService;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

@IntegrationTest
public class ReadServiceIntegrationTest extends ServicelayerTest
{
	private static final String READ_SERVICE_BEAN = "readService";
	@Resource
	private FlexibleSearchService flexibleSearchService;
	@Resource
	private TypeService typeService;
	@Resource
	private SchemaGenerator oDataSchemaGenerator;
	@Resource
	private ReadOnlyAttributesConfiguration defaultIntegrationServicesConfiguration;

	private ReadService readService;

	@Rule
	public BeanRegisteringRule rule = new BeanRegisteringRule().register(ReadService.class, READ_SERVICE_BEAN);

	@Before
	public void setUp()
	{
		readService = (ReadService) rule.getBean(ReadService.class, READ_SERVICE_BEAN);
		readService.setFlexibleSearchService(flexibleSearchService);
		readService.setTypeService(typeService);
		readService.setODataDefaultSchemaGenerator(oDataSchemaGenerator);
		readService.setReadOnlyAttributesConfiguration(defaultIntegrationServicesConfiguration);
	}

	@Test
	public void testFlexibleTypes()
	{
		assertTrue(readService.isCollectionType("CollectionType"));
		assertTrue(readService.isComposedType("ComposedType"));
		assertTrue(readService.isEnumerationMetaType("EnumerationMetaType"));
		assertTrue(readService.isAtomicType("AtomicType"));
		assertTrue(readService.isMapType("MapType"));
		assertTrue(readService.isProductType("Product"));
	}
}
