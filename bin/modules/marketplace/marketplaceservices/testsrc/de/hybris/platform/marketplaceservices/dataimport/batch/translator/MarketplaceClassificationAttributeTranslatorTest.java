/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.marketplaceservices.dataimport.batch.translator;

import static org.fest.assertions.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeValueModel;
import de.hybris.platform.classification.ClassificationService;
import de.hybris.platform.classification.features.Feature;
import de.hybris.platform.classification.features.FeatureList;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.impex.ImportConfig;
import de.hybris.platform.servicelayer.impex.ImportResult;
import de.hybris.platform.servicelayer.impex.ImportService;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class MarketplaceClassificationAttributeTranslatorTest extends ServicelayerTransactionalTest
{
	@Resource
	private ProductService productService;
	@Resource
	private ClassificationService classificationService;
	@Resource
	private ImportService importService;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
		createHardwareCatalog();
	}

	@Test
	public void testImportMarketplaceClassificationSuccess()
	{
		final StringBuilder builder = new StringBuilder();
		builder.append(
				"$clAttrModifiers=system='SampleClassification',version='1.0',translator=de.hybris.platform.marketplaceservices.dataimport.batch.translator.MarketplaceClassificationAttributeTranslator,lang=en");
		builder.append("\n").append("$feature=@dummy[$clAttrModifiers]");
		builder.append("\n").append("UPDATE Product;code[unique=true];$feature;");
		builder.append("catalogVersion[unique=true](catalog(id),version)[virtual=true,default=hwcatalog:Online]");
		builder.append("\n").append(";HW2200-0878;@lanSpeed*L100").append("\n");

		final ImportConfig importConfig = new ImportConfig();
		importConfig.setScript(builder.toString());
		importConfig.setRemoveOnSuccess(false);

		final ImportResult result = importService.importData(importConfig);

		assertThat(result).isNotNull();
		assertThat(result.isFinished()).isTrue();
		assertThat(result.hasUnresolvedLines()).isFalse();
		assertThat(result.isError()).isFalse();

		final ProductModel product = productService.getProductForCode("HW2200-0878");
		final FeatureList features = classificationService.getFeatures(product);
		final Feature feature = features.getFeatureByCode("SampleClassification/1.0/boards.lanspeed");

		assertThat(feature).isNotNull();
		assertThat(feature.getValues()).hasSize(1);
		assertThat(feature.getValue().getValue()).isInstanceOf(ClassificationAttributeValueModel.class);
		assertThat(((ClassificationAttributeValueModel) feature.getValue().getValue()).getCode()).isEqualTo("L100");
	}

}
