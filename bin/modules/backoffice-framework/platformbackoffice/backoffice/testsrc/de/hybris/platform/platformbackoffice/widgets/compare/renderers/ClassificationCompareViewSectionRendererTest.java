/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.widgets.compare.renderers;

import com.hybris.cockpitng.components.table.TableRow;
import com.hybris.cockpitng.components.table.TableRowsGroup;
import com.hybris.cockpitng.components.table.iterator.TableComponentIterator;
import com.hybris.cockpitng.config.compareview.jaxb.Section;
import com.hybris.cockpitng.dataaccess.facades.permissions.PermissionFacade;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.widgets.compare.model.ComparisonState;
import com.hybris.cockpitng.widgets.compare.model.PartialRendererData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.core.model.product.ProductModel;
import org.assertj.core.api.BDDAssertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ClassificationCompareViewSectionRendererTest
{

	@Spy
	@InjectMocks
	private ClassificationCompareViewSectionRenderer testSubject;

	@Mock
	private PermissionFacade permissionFacade;
	@Mock
	private TableRowsGroup tableRowsGroup;
	@Mock
	private TableRow tableRow;
	@Mock
	private Section section;
	@Mock
	private PartialRendererData partialRendererData;
	@Mock
	private DataType dataType;
	@Mock
	private WidgetInstanceManager widgetInstanceManager;


	@Test
	public void shouldCheckIfSectionRenderingIsPermittedWhenReferenceObjectIsPrroductInstance()
	{
		//given
		final Object reference = mock(ProductModel.class);
		final ComparisonState comparisonState = mock(ComparisonState.class);
		given(comparisonState.getReference()).willReturn(reference);
		given(partialRendererData.getComparisonState()).willReturn(comparisonState);
		final TableComponentIterator tableComponentIterator = mock(TableComponentIterator.class);
		given(tableRowsGroup.groupsIterator()).willReturn(tableComponentIterator);

		//when
		testSubject.renderSection(tableRowsGroup, tableRow, section, partialRendererData, dataType, widgetInstanceManager);

		//then
		then(testSubject).should().isPermitted();
	}

	@Test
	public void shouldNotPermittWhenUserHasNotReadAccessToClassificationClassType()
	{
		//given
		given(permissionFacade.canReadType(ClassificationClassModel._TYPECODE)).willReturn(false);

		//when
		final boolean permitted = testSubject.isPermitted();

		//then
		BDDAssertions.then(permitted).isFalse();
	}

	@Test
	public void shouldNotPermittWhenUserHasNotReadAccessToClassAttributeAssignmentType()
	{
		//given

		//when
		final boolean permitted = testSubject.isPermitted();

		//then
		BDDAssertions.then(permitted).isFalse();
	}

	@Test
	public void shouldPermittWhenUserHasReadAccessToClassAttributeAssignmentAndClassificationClassType()
	{
		//given
		given(permissionFacade.canReadType(ClassAttributeAssignmentModel._TYPECODE)).willReturn(true);
		given(permissionFacade.canReadType(ClassificationClassModel._TYPECODE)).willReturn(true);

		//when
		final boolean permitted = testSubject.isPermitted();

		//then
		BDDAssertions.then(permitted).isTrue();
	}
}
