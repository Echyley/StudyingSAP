/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.widgets.compare.renderers;

import com.hybris.cockpitng.components.table.TableCell;
import com.hybris.cockpitng.config.compareview.jaxb.Section;
import com.hybris.cockpitng.dataaccess.facades.permissions.PermissionFacade;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.labels.LabelService;
import com.hybris.cockpitng.widgets.compare.model.PartialRendererData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import org.assertj.core.api.BDDAssertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Label;
import org.zkoss.zul.Separator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ClassificationCompareViewSectionHeaderRendererTest
{

	private static final String LABEL_NO_READ_ACCESS = "backoffice.data.not.visible";
	private static final String SCLASS_SECTION_NAME_NO_READ_ACCESS_LABEL = "yw-compareview-section-name-label-no-read-access";
	private static final String LABEL_NO_READ_ACCESS_YTEST_ID = "compareview-classification-no-read-access";

	@InjectMocks
	@Spy
	private ClassificationCompareViewSectionHeaderRenderer testSubject;

	@Mock
	private PermissionFacade permissionFacade;

	@Mock
	private LabelService labelService;

	@Before
	public void setUp() throws Exception
	{
	}


	@Test
	public void shouldPermitWhenUserCanReadAllClassificationRelatedTypes()
	{
		//given
		userCanReadAllClassificationRelatedTypes();

		//when
		final boolean permitted = testSubject.isPermitted();

		//then
		BDDAssertions.then(permitted).isTrue();
	}

	@Test
	public void shouldNotAllowWhenUserCanNotReadClassificationClassType()
	{
		//given
		userCanNotReadClassificationClassType();

		//when
		final boolean permitted = testSubject.isPermitted();

		//then
		BDDAssertions.then(permitted).isFalse();
	}

	@Test
	public void shouldNotAllowWhenUserCanNotReadClassAttributeAssignmentType()
	{
		//given
		userCanNotReadClassAttributeAssignmentType();

		//when
		final boolean permitted = testSubject.isPermitted();

		//then
		BDDAssertions.then(permitted).isFalse();
	}

	@Test
	public void shouldRenderWhenSuffixLabelShouldBeAddedEvenSuperConditionsAreNotMet()
	{
		//given
		userCanNotReadClassificationClassType();
		final TableCell tableCellMock = mock(TableCell.class);
		final Section sectionMock = mock(Section.class);
		final PartialRendererData partialRendererDataMock = mock(PartialRendererData.class);
		final DataType dataTypeMock = mock(DataType.class);
		final WidgetInstanceManager widgetInstanceManagerMock = mock(WidgetInstanceManager.class);
		final List<Component> children = new ArrayList<>();
		children.add(mock(Component.class));
		given(tableCellMock.getChildren()).willReturn(children);

		//when
		final boolean shouldRender = testSubject.requiresSectionHeaderTitleRendering(tableCellMock, sectionMock,
				partialRendererDataMock, dataTypeMock, widgetInstanceManagerMock);

		//then
		BDDAssertions.then(shouldRender).isTrue();
	}

	@Test
	public void shouldRenderWhenSuffixLabelShouldNotBeAddedButSuperConditionsAreMet()
	{
		//given
		userCanReadAllClassificationRelatedTypes();
		final TableCell tableCellMock = mock(TableCell.class);
		final Section sectionMock = mock(Section.class);
		final PartialRendererData partialRendererDataMock = mock(PartialRendererData.class);
		final DataType dataTypeMock = mock(DataType.class);
		final WidgetInstanceManager widgetInstanceManagerMock = mock(WidgetInstanceManager.class);
		given(tableCellMock.getChildren()).willReturn(Collections.emptyList());

		//when
		final boolean shouldRender = testSubject.requiresSectionHeaderTitleRendering(tableCellMock, sectionMock,
				partialRendererDataMock, dataTypeMock, widgetInstanceManagerMock);

		//then
		BDDAssertions.then(shouldRender).isTrue();
	}

	@Test
	public void shouldNotRenderWhenSuffixLabelShouldNotBeAddedAndSuperConditionsAreNotMet()
	{
		//given
		userCanReadAllClassificationRelatedTypes();
		final TableCell tableCellMock = mock(TableCell.class);
		final Section sectionMock = mock(Section.class);
		final PartialRendererData partialRendererDataMock = mock(PartialRendererData.class);
		final DataType dataTypeMock = mock(DataType.class);
		final WidgetInstanceManager widgetInstanceManagerMock = mock(WidgetInstanceManager.class);
		final List<Component> children = new ArrayList<>();
		children.add(mock(Component.class));
		given(tableCellMock.getChildren()).willReturn(children);

		//when
		final boolean shouldRender = testSubject.requiresSectionHeaderTitleRendering(tableCellMock, sectionMock,
				partialRendererDataMock, dataTypeMock, widgetInstanceManagerMock);

		//then
		BDDAssertions.then(shouldRender).isFalse();
	}

	@Test
	public void shouldCreateSuffixLabelWhenUserCanNotReadClassificationClassType()
	{
		//given
		userCanNotReadClassificationClassType();

		//when
		final Optional<Component> sectionHeaderTitleSuffixLabel = testSubject.createSectionHeaderTitleSuffixLabel(any(), any(),
				any(), any(), any());

		//then
		BDDAssertions.then(sectionHeaderTitleSuffixLabel).isPresent();
		BDDAssertions.then(sectionHeaderTitleSuffixLabel.get().getChildren().get(0)).isInstanceOf(Separator.class);
		BDDAssertions.then(sectionHeaderTitleSuffixLabel.get().getChildren().get(1)).isInstanceOf(Label.class);
	}

	@Test
	public void shouldCreateSuffixLabelWhenUserCanNotReadClassAttributeAssignmentType()
	{
		//given
		userCanNotReadClassAttributeAssignmentType();

		//when
		final Optional<Component> sectionHeaderTitleSuffixLabel = testSubject.createSectionHeaderTitleSuffixLabel(any(), any(),
				any(), any(), any());

		//then
		BDDAssertions.then(sectionHeaderTitleSuffixLabel).isPresent();
		BDDAssertions.then(sectionHeaderTitleSuffixLabel.get().getChildren().get(0)).isInstanceOf(Separator.class);
		BDDAssertions.then(sectionHeaderTitleSuffixLabel.get().getChildren().get(1)).isInstanceOf(Label.class);
	}

	private void userCanReadAllClassificationRelatedTypes()
	{
		given(permissionFacade.canReadType(ClassificationClassModel._TYPECODE)).willReturn(true);
		given(permissionFacade.canReadType(ClassAttributeAssignmentModel._TYPECODE)).willReturn(true);
	}

	private void userCanNotReadClassificationClassType()
	{
		given(permissionFacade.canReadType(ClassificationClassModel._TYPECODE)).willReturn(false);
	}

	private void userCanNotReadClassAttributeAssignmentType()
	{
		given(permissionFacade.canReadType(ClassificationClassModel._TYPECODE)).willReturn(true);
		given(permissionFacade.canReadType(ClassAttributeAssignmentModel._TYPECODE)).willReturn(false);
	}

}
