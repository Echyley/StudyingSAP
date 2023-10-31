package de.hybris.platform.acceleratorservices.metainformation;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.storefront.data.MetaElementData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class HtmlMetaTagTest {
	private HtmlMetaTag htmlMetaTag;

	@Test
	public void testSetItemsWithNull() {
		htmlMetaTag = new HtmlMetaTag();
		htmlMetaTag.setItems(null);
		assertEquals(0, htmlMetaTag.metaElementDataList.size());

	}

	@Test
	public void testSetItemsForNullMetaList() {
		htmlMetaTag = new HtmlMetaTag();
		htmlMetaTag.reset();
		htmlMetaTag.setItems(Collections.emptyList());
		assertEquals(0, htmlMetaTag.metaElementDataList.size());
	}

	@Test
	public void testSetItemsWithEmptyList() {
		htmlMetaTag = new HtmlMetaTag();
		htmlMetaTag.setItems(Collections.emptyList());
		assertEquals(0, htmlMetaTag.metaElementDataList.size());
	}

	@Test
	public void testSetItemsWithList()
	{
		htmlMetaTag = new HtmlMetaTag();
		htmlMetaTag.setItems(List.of(new MetaElementData()));
		assertEquals(1, htmlMetaTag.metaElementDataList.size());
	}
}
