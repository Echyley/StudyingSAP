/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.widgets.compare.renderers;

import com.hybris.cockpitng.dataaccess.facades.permissions.PermissionFacade;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.labels.LabelService;
import com.hybris.cockpitng.widgets.compare.model.PartialRendererData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.classification.ClassificationSystemService;
import de.hybris.platform.platformbackoffice.widgets.compare.model.ClassificationDescriptor;
import org.assertj.core.api.BDDAssertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Label;
import org.zkoss.zul.Separator;

import java.util.Collection;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ClassificationCompareViewSubsectionHeaderRendererTest
{

	private static final String SCLASS_SUBSECTION_NAME_NO_READ_ACCESS_LABEL = "yw-compareview-subsection-name-label-no-read-access";

	@InjectMocks
	private ClassificationCompareViewSubsectionHeaderRenderer testSubject;

	@Mock
	private LabelService labelService;
	@Mock
	private ClassificationSystemService classificationSystemService;
	@Mock
	private PermissionFacade permissionFacade;

	@Test
	public void shouldCreateEmptySuffixWhenUserCanReadClassificationAttributeType()
	{
		//given
		final Component parent = mock(Component.class);
		final ClassificationDescriptor configuration = mock(ClassificationDescriptor.class);
		final PartialRendererData<Collection> data = mock(PartialRendererData.class);
		final DataType dataType = mock(DataType.class);
		final WidgetInstanceManager widgetInstanceManager = mock(WidgetInstanceManager.class);

		given(permissionFacade.canReadType(ClassificationAttributeModel._TYPECODE)).willReturn(true);

		//when
		final Optional<Component> sectionHeaderTitleSuffixLabel = testSubject.createSectionHeaderTitleSuffixLabel(parent,
				configuration, data, dataType, widgetInstanceManager);

		//then
		BDDAssertions.then(sectionHeaderTitleSuffixLabel).isEmpty();
	}

	@Test
	public void shouldCreateSuffixWithSeparatorAndLabelWithNoreadAccessSClassWhenUserCanNotReadClassificationAttributeType()
	{
		//given
		final Component parent = mock(Component.class);
		final ClassificationDescriptor configuration = mock(ClassificationDescriptor.class);
		final PartialRendererData<Collection> data = mock(PartialRendererData.class);
		final DataType dataType = mock(DataType.class);
		final WidgetInstanceManager widgetInstanceManager = mock(WidgetInstanceManager.class);

		given(permissionFacade.canReadType(ClassificationAttributeModel._TYPECODE)).willReturn(false);

		//when
		final Optional<Component> sectionHeaderTitleSuffixLabel = testSubject.createSectionHeaderTitleSuffixLabel(parent,
				configuration, data, dataType, widgetInstanceManager);

		//then
		BDDAssertions.then(sectionHeaderTitleSuffixLabel).isPresent();
		BDDAssertions.then(sectionHeaderTitleSuffixLabel.get().getFirstChild()).isInstanceOf(Separator.class);
		BDDAssertions.then(sectionHeaderTitleSuffixLabel.get().getLastChild()).isInstanceOf(Label.class);
		BDDAssertions.then(((Label) sectionHeaderTitleSuffixLabel.get().getLastChild()).getSclass())
				.isEqualTo(SCLASS_SUBSECTION_NAME_NO_READ_ACCESS_LABEL);
	}
}
