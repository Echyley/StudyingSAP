/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.namedquery.validator;

import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmsfacades.data.MediaData;
import de.hybris.platform.cmsfacades.data.NamedQueryData;

import java.util.function.Predicate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;



@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class NamedQueryDataValidatorTest
{
	private static final String NAMED_QUERY = "named-query";
	private static final String SORT = "code:ASC,url:DESC";
	private static final String PARAMS = "code:banner,description:banner,altText:banner";
	private static final String PAGE_SIZE = "5";
	private static final String CURRENT_PAGE = "1";
	private static final Class<?> QUERY_TYPE = MediaData.class;
	private static final Integer MAX_PAGE_SIZE = Integer.valueOf(10);

	@InjectMocks
	private final NamedQueryDataValidator validator = new NamedQueryDataValidator();

	@Mock
	private Predicate<String> namedQueryExistsPredicate;

	private NamedQueryData target;
	private Errors errors;

	@Before
	public void setUp()
	{
		target = new NamedQueryData();
		target.setCurrentPage(CURRENT_PAGE);
		target.setNamedQuery(NAMED_QUERY);
		target.setPageSize(PAGE_SIZE);
		target.setParams(PARAMS);
		target.setQueryType(QUERY_TYPE);
		target.setSort(SORT);

		errors = new BeanPropertyBindingResult(target, target.getClass().getSimpleName());

		validator.setMaxPageSize(MAX_PAGE_SIZE);

		when(namedQueryExistsPredicate.test(NAMED_QUERY)).thenReturn(Boolean.TRUE);
	}

	@Test
	public void shouldHaveNoFailures_AllParamsPresent()
	{
		validator.validate(target, errors);
		assertFalse(errors.toString(), errors.hasErrors());
	}

	@Test
	public void shouldHaveNoFailures_NoSort()
	{
		target.setSort(null);

		validator.validate(target, errors);
		assertFalse(errors.toString(), errors.hasErrors());
	}

	@Test
	public void shouldHaveNoFailures_NoPageSize()
	{
		target.setPageSize(null);

		validator.validate(target, errors);
		assertFalse(errors.toString(), errors.hasErrors());
	}

	@Test
	public void shouldHaveNoFailures_NoCurrentPage()
	{
		target.setCurrentPage(null);

		validator.validate(target, errors);
		assertFalse(errors.toString(), errors.hasErrors());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFail_NoQueryType()
	{
		target.setQueryType(null);
		validator.validate(target, errors);
	}

	@Test
	public void shouldFail_NoNamedQuery()
	{
		target.setNamedQuery(null);
		validator.validate(target, errors);
		assertEquals(errors.toString(), 1, errors.getFieldErrorCount());
	}

	@Test
	public void shouldFail_NamedQueryDoesNotExist()
	{
		when(namedQueryExistsPredicate.test(NAMED_QUERY)).thenReturn(Boolean.FALSE);

		validator.validate(target, errors);
		assertEquals(errors.toString(), 1, errors.getFieldErrorCount());
	}

	@Test
	public void shouldFail_PageSizeIsNotValid() {
		testPageSizeInvalid("NaN");//Invalid
		testPageSizeInvalid("0"); //too small
		testPageSizeInvalid("11"); //too large
	}

	private void testPageSizeInvalid(String pagesize)
	{
		setUp();
		target.setPageSize(pagesize);
		validator.validate(target, errors);
		assertEquals(errors.toString(), 1, errors.getFieldErrorCount());
	}

	@Test
	public void shouldFail_CurrentPageTooSmall()
	{
		target.setCurrentPage("-1");
		validator.validate(target, errors);
		assertEquals(errors.toString(), 1, errors.getFieldErrorCount());
	}

	@Test
	public void shouldFail_CurrentPageNaN()
	{
		target.setCurrentPage("NaN");
		validator.validate(target, errors);
		assertEquals(errors.toString(), 1, errors.getFieldErrorCount());
	}

	@Test
	public void shouldFail_SortInvalid()
	{
		testInvalidSortDirection("code:invalid");
		testInvalidSortDirection("invalid:ASC");
		testInvalidSortDirection("code-ASC");
	}

	private void testInvalidSortDirection(String sortDirection) {
		setUp();
		target.setSort(sortDirection);
		validator.validate(target, errors);
		assertEquals(errors.toString(), 1, errors.getFieldErrorCount());
	}

	@Test
	public void shouldHaveNoFailures_EmptyParamValue()
	{
		target.setParams("code:");
		validator.validate(target, errors);
		assertThat(errors.toString(), errors.getFieldErrors(), empty());
	}

	@Test
	public void shouldFail_ParamsInvalid()
	{
		testInvalidParams(null);
		testInvalidParams("code-banner");
		testInvalidParams("invalid:banner");
	}

	private void testInvalidParams(String parameter) {
		setUp();
		target.setParams(parameter);
		validator.validate(target, errors);
		assertEquals(errors.toString(), 1, errors.getFieldErrorCount());
	}

}
