/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.excel.template;

import static com.hybris.backoffice.excel.template.ExcelTemplateConstants.HeaderPrompt.HEADER_ATTR_DISPLAYED_NAME;
import static com.hybris.backoffice.excel.template.ExcelTemplateConstants.HeaderPrompt.HEADER_TYPE_CODE;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.AtomicTypeModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.variants.model.VariantTypeModel;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.assertj.core.api.JUnitSoftAssertions;
import org.assertj.core.util.Lists;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;

import com.hybris.backoffice.excel.ExcelIntegrationTest;
import com.hybris.backoffice.excel.data.ExcelAttribute;
import com.hybris.backoffice.excel.data.ExcelAttributeDescriptorAttribute;
import com.hybris.backoffice.excel.data.ExcelClassificationAttribute;
import com.hybris.backoffice.excel.data.ExcelExportResult;
import com.hybris.backoffice.excel.template.workbook.ExcelWorkbookService;


public class ClassificationIncludedHeaderPromptPopulatorIntegrationTest extends ExcelIntegrationTest
{

	@Rule
	public JUnitSoftAssertions soft = new JUnitSoftAssertions();

	@Resource
	ExcelWorkbookService excelWorkbookService;
	@Resource
	FlexibleSearchService flexibleSearchService;
	@Spy
	@InjectMocks
	@Resource
	ClassificationIncludedHeaderPromptPopulator classificationIncludedHeaderPromptPopulator;

	private static String TEST_LONG_TYPE_CODE = "ElectronicsCapacityVariantProduct";
	private static String TEST_SHORT_SHEET_NAME = "ElectronicsCapacityVariantPro_1";

	@Test
	public void testPopulateWithVariant() throws Exception
	{
		loadClassificationImpex();

		// given
		final VariantTypeModel variantTypeModel = saveItem(prepareVariant());
		final ProductModel product = saveItem(prepareProductWithVariant( //
				prepareProduct("product", createCatalogVersionModel("catalog", "1.0")), //
				variantTypeModel));
		final ItemModel variant = saveItem(prepareVariantProductModel(product, variantTypeModel));

		final List<ExcelAttribute> assignments = prepareExcelClassificationAttributes();
		final List<ExcelAttribute> excelAttributes = prepareExcelAttributeDescriptorAttributes(variant);

		try (final Workbook workbook = excelWorkbookService.createWorkbook(loadResource("/excel/excelImpExMasterTemplate.xlsx")))
		{
			// when
			final ExcelExportResult excelExportResult = new ExcelExportResult(workbook, null, null, assignments, excelAttributes);
			excelWorkbookService.addProperty(workbook, TEST_SHORT_SHEET_NAME, TEST_LONG_TYPE_CODE);
			classificationIncludedHeaderPromptPopulator.populate(excelExportResult);

			// then
			final String column1Variant = "VariantProduct";
			final String column1Product = "Product";
			final String column2Localized = "description[en]";
			final String column2Unlocalized = "order";
			final String column2Classification = "software.manufacturerURL[en] - SampleClassification/1.0";
			final Sheet headerPrompt = workbook.getSheet(ExcelTemplateConstants.UtilitySheet.HEADER_PROMPT.getSheetName());


			// column 0
			soft.assertThat(headerPrompt.getRow(1).getCell(HEADER_TYPE_CODE.getIndex()).getStringCellValue())
					.isEqualTo(column1Variant);
			soft.assertThat(headerPrompt.getRow(2).getCell(HEADER_TYPE_CODE.getIndex()).getStringCellValue())
					.isEqualTo(column1Variant);
			soft.assertThat(headerPrompt.getRow(3).getCell(HEADER_TYPE_CODE.getIndex()).getStringCellValue())
					.isEqualTo(column1Variant);
			soft.assertThat(headerPrompt.getRow(4).getCell(HEADER_TYPE_CODE.getIndex()).getStringCellValue())
					.isEqualTo(column1Product);
			soft.assertThat(headerPrompt.getRow(5).getCell(HEADER_TYPE_CODE.getIndex()).getStringCellValue())
					.isEqualTo(column1Product);
			soft.assertThat(headerPrompt.getRow(6).getCell(HEADER_TYPE_CODE.getIndex()).getStringCellValue())
					.isEqualTo(column1Product);
			soft.assertThat(headerPrompt.getRow(7).getCell(HEADER_TYPE_CODE.getIndex()).getStringCellValue())
					.isEqualTo(TEST_SHORT_SHEET_NAME);

			// column 1
			soft.assertThat(headerPrompt.getRow(1).getCell(HEADER_ATTR_DISPLAYED_NAME.getIndex()).getStringCellValue())
					.isEqualTo(column2Localized);
			soft.assertThat(headerPrompt.getRow(2).getCell(HEADER_ATTR_DISPLAYED_NAME.getIndex()).getStringCellValue())
					.isEqualTo(column2Unlocalized);
			soft.assertThat(headerPrompt.getRow(3).getCell(HEADER_ATTR_DISPLAYED_NAME.getIndex()).getStringCellValue())
					.isEqualTo(column2Classification);
			soft.assertThat(headerPrompt.getRow(4).getCell(HEADER_ATTR_DISPLAYED_NAME.getIndex()).getStringCellValue())
					.isEqualTo(column2Localized);
			soft.assertThat(headerPrompt.getRow(5).getCell(HEADER_ATTR_DISPLAYED_NAME.getIndex()).getStringCellValue())
					.isEqualTo(column2Unlocalized);
			soft.assertThat(headerPrompt.getRow(6).getCell(HEADER_ATTR_DISPLAYED_NAME.getIndex()).getStringCellValue())
					.isEqualTo(column2Classification);

		}
	}

	protected List<ExcelAttribute> prepareExcelAttributeDescriptorAttributes(final ItemModel variant)
	{
		final String localizedAttribute = "Description";
		final String unlocalizedAttribute = "order";
		final AttributeDescriptorModel localizedProductAttributeDescriptor = getAttributeDescriptorOf(ProductModel.class,
				localizedAttribute);
		final AttributeDescriptorModel unlocalizedProductAttributeDescriptor = getAttributeDescriptorOf(ProductModel.class,
				unlocalizedAttribute);

		final AttributeDescriptorModel localizedVariantAttributeDescriptor = getAttributeDescriptorOf(variant, localizedAttribute);
		final AttributeDescriptorModel unlocalizedVariantAttributeDescriptor = getAttributeDescriptorOf(variant,
				unlocalizedAttribute);

		final AttributeDescriptorModel longCodeAttributeDescriptor = prepareLongTypeCodeAttributeDescriptor();

		return Lists.newArrayList(//
				new ExcelAttributeDescriptorAttribute(longCodeAttributeDescriptor), //
				new ExcelAttributeDescriptorAttribute(localizedProductAttributeDescriptor, "en"), //
				new ExcelAttributeDescriptorAttribute(unlocalizedProductAttributeDescriptor), //
				new ExcelAttributeDescriptorAttribute(localizedVariantAttributeDescriptor, "en"), //
				new ExcelAttributeDescriptorAttribute(unlocalizedVariantAttributeDescriptor)//
		);
	}

	protected AttributeDescriptorModel prepareLongTypeCodeAttributeDescriptor()
	{
		final AttributeDescriptorModel longCodeAttributeDescriptor = mock(AttributeDescriptorModel.class);

		final ComposedTypeModel composedTypeModel = mock(ComposedTypeModel.class);
		final AtomicTypeModel atomicTypeModel = mock(AtomicTypeModel.class);
		doReturn("testName").when(longCodeAttributeDescriptor).getName();
		doReturn(composedTypeModel).when(longCodeAttributeDescriptor).getEnclosingType();
		doReturn(atomicTypeModel).when(longCodeAttributeDescriptor).getAttributeType();
		doReturn(TEST_LONG_TYPE_CODE).when(composedTypeModel).getCode();
		return longCodeAttributeDescriptor;
	}

	protected List<ExcelAttribute> prepareExcelClassificationAttributes()
	{
		final String queryForAllClassificationAssignments = "SELECT {ClassAttributeAssignment:PK} FROM {ClassAttributeAssignment}";
		final List<ClassAttributeAssignmentModel> assignments = flexibleSearchService
				.<ClassAttributeAssignmentModel> search(queryForAllClassificationAssignments).getResult();
		return assignments.stream().map(assignment -> {
			final ExcelClassificationAttribute attribute = new ExcelClassificationAttribute();
			attribute.setAttributeAssignment(assignment);
			attribute.setIsoCode("en");
			return attribute;
		}).collect(Collectors.toList());

	}

	private void loadClassificationImpex() throws ImpExException
	{
		importStream(loadResource("/test/excel/classificationSystem.csv"), StandardCharsets.UTF_8.name(),
				"classificationSystem.csv");
	}

	private InputStream loadResource(final String path)
	{
		return Optional.ofNullable(getClass().getResourceAsStream(path))
				.orElseThrow(() -> new AssertionError("Could not load resource: " + path));
	}

}
