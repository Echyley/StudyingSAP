/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.excel.template.cell;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.nullable;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import org.mockito.Mockito;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class DefaultExcelCellServiceTest
{

	@Spy
	private CellValue stringCellValue = new StringCellValue();
	@Spy
	private CellValue numericCellValue = new NumericCellValue();
	@Spy
	private CellValue dataCellValue = new DataCellValue();
	@Spy
	private CellValue formulaCellValue = new FormulaCellValue();

	private DefaultExcelCellService excelCellService = new DefaultExcelCellService();

	@Before
	public void setUp()
	{
		excelCellService.setCellValues(Lists.newArrayList(stringCellValue, numericCellValue, formulaCellValue, dataCellValue));
	}

	@Test
	public void shouldGetCellValueBeNullSafe()
	{
		// expect
		assertThat(excelCellService.getCellValue(null)).isEqualTo(StringUtils.EMPTY);
	}

	@Test
	public void shouldFirstMatchTerminateIterationAbstractFacetSearchConfigDAOTest()
	{
		// given
		final Cell cell = mock(Cell.class);
		given(cell.getCellType()).willReturn(CellType.STRING);

		// when
		excelCellService.getCellValue(cell);

		// then
		verify(stringCellValue).getValue(cell);
		verify(numericCellValue, never()).getValue(any());
		verify(formulaCellValue, never()).getValue(any());
		verify(dataCellValue, never()).getValue(any());
	}

	@Test
	public void shouldDataCellValueBeInvokedAsDefault()
	{
		// given
		final Cell cell = mock(Cell.class);
		given(cell.getCellType()).willReturn(null);
		doReturn(Optional.of(StringUtils.EMPTY)).when(dataCellValue).getValue(any());

		// when
		excelCellService.getCellValue(cell);

		// then
		verify(dataCellValue).getValue(cell);
		verify(numericCellValue, never()).getValue(any());
		verify(formulaCellValue, never()).getValue(any());
		verify(stringCellValue, never()).getValue(any());
	}

	@Test
	public void shouldImportedValueBeEscaped()
	{
		// given
		final String val = "=X";
		final String nonEscapedVal = "'" + val;
		final Cell cell = mock(Cell.class);
		given(cell.getCellType()).willReturn(CellType.STRING);
		doReturn(Optional.of(nonEscapedVal)).when(stringCellValue).getValue(cell);
		// when
		final String returned = excelCellService.getCellValue(cell);

		// then
		assertThat(returned).isEqualTo(val);
	}

	@Test
	public void shouldImportedValueBeNotEscaped()
	{
		// given
		final String val = "'value";
		final Cell cell = mock(Cell.class);
		given(cell.getCellType()).willReturn(CellType.STRING);
		doReturn(Optional.of(val)).when(stringCellValue).getValue(cell);

		// when
		final String returned = excelCellService.getCellValue(cell);

		// then
		assertThat(returned).isEqualTo(val);
	}

	@Test
	public void shouldExportedValueBeEscaped()
	{
		// given
		final String val = "=X";
		final String escapedVal = "'" + val;


		// when
		final String returned = excelCellService.escapeExportFormula(val);

		// then
		assertThat(returned).isEqualTo(escapedVal);
	}

}
