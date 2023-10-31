/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.excel.template.populator;

import static com.hybris.backoffice.excel.template.ExcelTemplateConstants.ClassificationTypeSystemColumns.FULL_NAME;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemVersionModel;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.backoffice.excel.data.ExcelAttribute;
import com.hybris.backoffice.excel.data.ExcelClassificationAttribute;
import com.hybris.backoffice.excel.data.ExcelExportResult;
import com.hybris.backoffice.excel.template.ExcelTemplateConstants;
import com.hybris.backoffice.excel.template.ExcelTemplateConstants.ClassificationTypeSystemColumns;
import com.hybris.backoffice.excel.template.cell.DefaultExcelCellService;
import com.hybris.backoffice.excel.template.cell.ExcelCellService;


@RunWith(MockitoJUnitRunner.class)
public class ClassificationTypeSystemSheetPopulatorTest
{
	private static final String CLASSIFICATION_ATTRIBUTE_FULL = "classificationAttributeFull";

	@Mock
	ClassificationTypeSystemSheetCompressor mockedCompressor;
	private final ExcelCellService excelCellService = new DefaultExcelCellService();
	@InjectMocks
	ClassificationTypeSystemSheetPopulator populator;

	@Before
	public void setUp()
	{
		populator.setExcelCellService(excelCellService);
	}

	@Test
	public void shouldPopulateClassificationTypeSystemSheetWithAttributes() throws IOException
	{
		// given
		final Set<ExcelAttribute> classAttributeAssignmentModels = asSet(createClassificationAttributeMock());
		populator.setCellValuePopulators(
				new EnumMap<ClassificationTypeSystemColumns, ExcelClassificationCellPopulator>(ClassificationTypeSystemColumns.class)
				{
					{
						put(FULL_NAME, ignored -> CLASSIFICATION_ATTRIBUTE_FULL);
					}
				});
		final Collection<Map<ClassificationTypeSystemColumns, String>> rows = Collections
				.singletonList(new EnumMap<ClassificationTypeSystemColumns, String>(ClassificationTypeSystemColumns.class)
				{
					{
						put(FULL_NAME, CLASSIFICATION_ATTRIBUTE_FULL);
					}
				});
		given(mockedCompressor.compress(rows)).willReturn(rows);

		try (final Workbook workbook = new XSSFWorkbook())
		{
			// when
			populator.populate(createExcelExportResultWithSelectedAttributes(workbook, classAttributeAssignmentModels));

			// then
			final Sheet classificationTypeSystemSheet = workbook
					.getSheet(ExcelTemplateConstants.UtilitySheet.CLASSIFICATION_TYPE_SYSTEM.getSheetName());
			assertThat(classificationTypeSystemSheet).isNotNull();

			final Row firstRow = classificationTypeSystemSheet.getRow(0);
			assertThat(getCellValue(firstRow, FULL_NAME)).isEqualTo(CLASSIFICATION_ATTRIBUTE_FULL);

			final Row secondRow = classificationTypeSystemSheet.getRow(1);
			assertThat(secondRow).isNull();

			final Row thirdRow = classificationTypeSystemSheet.getRow(2);
			assertThat(thirdRow).isNull();
		}
	}

	private ExcelClassificationAttribute createClassificationAttributeMock()
	{
		final ClassificationSystemVersionModel classificationSystemVersionModel = mock(ClassificationSystemVersionModel.class);
		Mockito.lenient().when(classificationSystemVersionModel.getVersion()).thenReturn("classificationSystemVersion");
		Mockito.lenient().when(classificationSystemVersionModel.getCategorySystemID()).thenReturn("classificationSystemId");

		final ClassificationClassModel classificationClassModel = mock(ClassificationClassModel.class);
		Mockito.lenient().when(classificationClassModel.getName(Locale.ENGLISH)).thenReturn("classificationClassEnglish");
		Mockito.lenient().when(classificationClassModel.getName(Locale.GERMAN)).thenReturn("classificationClassGerman");

		final ClassificationAttributeModel classificationAttributeModel = mock(ClassificationAttributeModel.class);
		Mockito.lenient().when(classificationAttributeModel.getName(Locale.ENGLISH)).thenReturn("classificationAttributeNameEnglish");
		Mockito.lenient().when(classificationAttributeModel.getName(Locale.GERMAN)).thenReturn("classificationAttributeNameGerman");
		Mockito.lenient().when(classificationAttributeModel.getSystemVersion()).thenReturn(classificationSystemVersionModel);

		final ClassAttributeAssignmentModel classAttributeAssignmentModel = mock(ClassAttributeAssignmentModel.class);
		Mockito.lenient().when(classAttributeAssignmentModel.getClassificationAttribute()).thenReturn(classificationAttributeModel);
		Mockito.lenient().when(classAttributeAssignmentModel.getClassificationClass()).thenReturn(classificationClassModel);
		Mockito.lenient().when(classAttributeAssignmentModel.getSystemVersion()).thenReturn(classificationSystemVersionModel);
		Mockito.lenient().when(classAttributeAssignmentModel.getLocalized()).thenReturn(false);
		Mockito.lenient().when(classAttributeAssignmentModel.getMandatory()).thenReturn(true);

		final ExcelClassificationAttribute attribute = new ExcelClassificationAttribute();
		attribute.setAttributeAssignment(classAttributeAssignmentModel);
		return attribute;
	}

	private ExcelExportResult createExcelExportResultWithSelectedAttributes(final Workbook workbook,
			final Collection<ExcelAttribute> attributes)
	{
		return new ExcelExportResult(workbook, null, null, attributes, attributes);
	}

	private String getCellValue(final Row row, final ClassificationTypeSystemColumns column)
	{
		return row.getCell(column.getIndex()).getStringCellValue();
	}

	@SafeVarargs
	private static <T> Set<T> asSet(final T... elements)
	{
		return new HashSet<>(Arrays.asList(elements));
	}
}
