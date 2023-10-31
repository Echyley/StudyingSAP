package com.hybris.backoffice.searchservices.services.impl;

import com.hybris.backoffice.search.cache.BackofficeFacetSearchConfigCache;
import com.hybris.backoffice.search.daos.FacetSearchConfigDAO;
import com.hybris.backoffice.searchservices.model.BackofficeIndexedTypeToSearchservicesIndexConfigModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.searchservices.model.SnIndexTypeModel;
import de.hybris.platform.servicelayer.type.TypeService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


public class BackofficeSearchservicesFacetSearchConfigServiceTest
{

	private static final String PRODUCT_TYPECODE = "Product";
	private static final String INDEX_TYPE_ID = "Backoffice-Product";
	private static final String INDEX_TYPE_ID1 = "Other-Product";

	@Mock
	private TypeService typeService;

	@Mock
	private FacetSearchConfigDAO facetSearchConfigDAO;

	@Mock
	private BackofficeFacetSearchConfigCache backofficeFacetSearchConfigCache;

	@Mock
	private ComposedTypeModel composedType;

	@Mock
	private BackofficeIndexedTypeToSearchservicesIndexConfigModel searchConfig;

	@Mock
	private SnIndexTypeModel snIndexTypeModel;

	@InjectMocks
	private final BackofficeSearchservicesFacetSearchConfigService backofficeSearchservicesFacetSearchConfigService = new BackofficeSearchservicesFacetSearchConfigService();

	@Before
	public void init()
	{
		MockitoAnnotations.initMocks(this);
		when(snIndexTypeModel.getId()).thenReturn(INDEX_TYPE_ID);
		when(searchConfig.getSnIndexType()).thenReturn(snIndexTypeModel);
		when(searchConfig.getIndexedType()).thenReturn(composedType);
		when(typeService.getComposedTypeForCode(PRODUCT_TYPECODE)).thenReturn(composedType);
		when(facetSearchConfigDAO.findSearchConfigurationsForTypes(any())).thenReturn(Arrays.asList(searchConfig));
	}

	@Test
	public void shouldReturnSearchConfig()
	{
		final Optional<BackofficeIndexedTypeToSearchservicesIndexConfigModel> searchConfigOptional = backofficeSearchservicesFacetSearchConfigService
				.findSearchConfigForTypeCodeAndIndexTypeId(PRODUCT_TYPECODE, INDEX_TYPE_ID);
		assertEquals(searchConfigOptional, Optional.ofNullable(searchConfig));
	}

	@Test
	public void shouldReturnNullWhenIndexTypeIdNotMatch()
	{
		final Optional<BackofficeIndexedTypeToSearchservicesIndexConfigModel> searchConfigOptional = backofficeSearchservicesFacetSearchConfigService
				.findSearchConfigForTypeCodeAndIndexTypeId(PRODUCT_TYPECODE, INDEX_TYPE_ID1);

		assertEquals(searchConfigOptional, Optional.empty());
	}

	@Test
	public void shouldReturnNullWhenSearchConfigIsNull()
	{
		when(facetSearchConfigDAO.findSearchConfigurationsForTypes(any())).thenReturn(null);
		final Optional<BackofficeIndexedTypeToSearchservicesIndexConfigModel> searchConfigOptional = backofficeSearchservicesFacetSearchConfigService
				.findSearchConfigForTypeCodeAndIndexTypeId(PRODUCT_TYPECODE, INDEX_TYPE_ID1);

		assertEquals(searchConfigOptional, Optional.empty());
	}

}
