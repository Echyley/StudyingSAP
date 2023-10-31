/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.assistedservicewebservices.util;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.assistedservicewebservices.utils.PaginationUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static de.hybris.platform.testframework.Assert.assertEquals;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PaginationUtilsTest
{

	private static Map testCode = new HashMap<>();

	private static int pageSize = 20;

	@Before
	public void testDataPagination()
	{
		// input key is result and value is totalPage;
		testCode.put(0, 1);
		testCode.put(1, 1);
		testCode.put(100, 5);
		testCode.put(101, 6);
	}

	@Test
	public void buildPaginationDataTest()
	{
		var arr = new ArrayList<>();
		var pageableData = PaginationUtils.createPageableData(1, pageSize, "");

		for (Object key : testCode.keySet())
		{
			setArraySize(arr, (int) key);
			var res = PaginationUtils.buildPaginationData(pageableData, arr);
			var expectedTotalPage = ((Integer)testCode.get(key)).intValue();
			assertEquals(res.getNumberOfPages(), expectedTotalPage);
		}

	}

	private void setArraySize(List arr, int size)
	{
		arr.clear();
		while (size-- != 0)
		{
			arr.add("");
		}
	}
}
