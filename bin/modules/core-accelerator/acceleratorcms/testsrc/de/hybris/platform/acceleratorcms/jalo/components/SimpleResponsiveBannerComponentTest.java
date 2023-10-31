/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorcms.jalo.components;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.platform.acceleratorcms.model.components.SimpleResponsiveBannerComponentModel;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.pages.PageTemplateModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import org.junit.Test;
import org.junit.Before;
import javax.annotation.Resource;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.core.model.product.ProductModel;

@IntegrationTest
public class SimpleResponsiveBannerComponentTest extends ServicelayerTest
{
    private static final String CATEGORY_CODE = "testCategory";
    private static final String PRODUCT_CODE = "testProduct";
    private static final String CONTENT_PAGE_UID = "testContentPage";
    private static final String CATALOG_ID = "testCatalog";
    private static final String CATALOG_VERSION_ID = "1.0";
    private static final String MASTER_PAGE_TEMPLATE_LOCAL_ID = "MasterTemplateLocal";
    private static final String SIMPLE_RESPONSIVE_BANNER_COMPONENT_ID = "testSimpleBannerComponent";

    @Resource
    private ModelService modelService;

    @Resource
    private FlexibleSearchService flexibleSearchService;

    private SimpleResponsiveBannerComponentModel simpleResponsiveBannerComponent;

    @Before
    public void setUp()
    {
        final CatalogModel catalog = new CatalogModel();
        catalog.setId(CATALOG_ID);

        final CatalogVersionModel catalogVersion = new CatalogVersionModel();
        catalogVersion.setCatalog(catalog);
        catalogVersion.setVersion(CATALOG_VERSION_ID);

        final CategoryModel categoryModel = modelService.create(CategoryModel.class);
        categoryModel.setCode(CATEGORY_CODE);
        categoryModel.setCatalogVersion(catalogVersion);

        final ProductModel productModel = modelService.create(ProductModel.class);
        productModel.setCode(PRODUCT_CODE);
        productModel.setCatalogVersion(catalogVersion);

        final PageTemplateModel masterPageTemplateLocal = modelService.create(PageTemplateModel.class);
        masterPageTemplateLocal.setUid(MASTER_PAGE_TEMPLATE_LOCAL_ID);
        masterPageTemplateLocal.setCatalogVersion(catalogVersion);

        final ContentPageModel contentPageModel = modelService.create(ContentPageModel.class);
        contentPageModel.setUid(CONTENT_PAGE_UID);
        contentPageModel.setCatalogVersion(catalogVersion);
        contentPageModel.setMasterTemplate(masterPageTemplateLocal);

        simpleResponsiveBannerComponent = modelService.create(SimpleResponsiveBannerComponentModel.class);
        simpleResponsiveBannerComponent.setUid(SIMPLE_RESPONSIVE_BANNER_COMPONENT_ID);
        simpleResponsiveBannerComponent.setCatalogVersion(catalogVersion);

        simpleResponsiveBannerComponent.setCategory(categoryModel);
        simpleResponsiveBannerComponent.setProduct(productModel);
        simpleResponsiveBannerComponent.setContentPage(contentPageModel);

        modelService.saveAll(catalog, catalogVersion, categoryModel, productModel, contentPageModel, simpleResponsiveBannerComponent);
    }

    @Test
    public void testCategoryAttribute()
    {
        final FlexibleSearchQuery fQuery = new FlexibleSearchQuery(
                "SELECT {s.PK} FROM { SimpleResponsiveBannerComponent As s Join Category As c ON {s.category}={c.PK} } WHERE {s.uid}=?uid AND {c.code}=?code");
        fQuery.addQueryParameter("uid", SIMPLE_RESPONSIVE_BANNER_COMPONENT_ID);
        fQuery.addQueryParameter("code", CATEGORY_CODE);

        final SearchResult<SimpleResponsiveBannerComponentModel> result = flexibleSearchService.search(fQuery);

        assertNotNull(result);
        assertEquals(CATEGORY_CODE, result.getResult().get(0).getCategory().getCode());
    }

    @Test
    public void testProductAttribute()
    {
        final FlexibleSearchQuery fQuery = new FlexibleSearchQuery(
                "SELECT {s.PK} FROM { SimpleResponsiveBannerComponent As s Join Product As p ON {s.product}={p.PK} } WHERE {s.uid}=?uid AND {p.code}=?code");
        fQuery.addQueryParameter("uid", SIMPLE_RESPONSIVE_BANNER_COMPONENT_ID);
        fQuery.addQueryParameter("code", PRODUCT_CODE);

        final SearchResult<SimpleResponsiveBannerComponentModel> result = flexibleSearchService.search(fQuery);

        assertNotNull(result);
        assertEquals(PRODUCT_CODE, result.getResult().get(0).getProduct().getCode());
    }

    @Test
    public void testContentPageAttribute()
    {
        final FlexibleSearchQuery fQuery = new FlexibleSearchQuery(
                "SELECT {s.PK} FROM { SimpleResponsiveBannerComponent As s Join ContentPage As cp ON {s.contentPage}={cp.PK} } WHERE {s.uid}=?uid AND {cp.uid}=?cp_uid");
        fQuery.addQueryParameter("uid", SIMPLE_RESPONSIVE_BANNER_COMPONENT_ID);
        fQuery.addQueryParameter("cp_uid", CONTENT_PAGE_UID);

        final SearchResult<SimpleResponsiveBannerComponentModel> result = flexibleSearchService.search(fQuery);

        assertNotNull(result);
        assertEquals(CONTENT_PAGE_UID, result.getResult().get(0).getContentPage().getUid());
    }
}
