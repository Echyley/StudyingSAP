package com.hybris.backoffice.personalization.listview.renderer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zul.Div;
import org.zkoss.zul.Window;

import com.hybris.backoffice.personalization.PersonalizationConstants;
import com.hybris.cockpitng.util.CockpitComponentsUtils;


@RunWith(MockitoJUnitRunner.class)
public class ExtendedAttributeChooserRendererTest
{
	private static final String SECTION_ID_AVAILABLE = "available";
	private static final String SECTION_ID_SELECTED = "selected";
	private static final String BUTTON_ID_REMOVE = "remove";
	private static final String BUTTON_ID_ADD = "add";

	@Mock
	private ExtendedAttributeChooserTreeItemRenderer extendedAttributeChooserTreeItemRenderer;

	@InjectMocks
	@Spy
	private ExtendedAttributeChooserRenderer attributePicker;


	@Test
	public void shouldRenderFilterInAvailableSection()
	{
		assertThat(attributePicker.isNeedRenderFilter(SECTION_ID_AVAILABLE)).isTrue();
	}

	@Test
	public void shouldNotRenderFilterInSelectedSection()
	{
		assertThat(attributePicker.isNeedRenderFilter(SECTION_ID_SELECTED)).isFalse();
	}

	@Test
	public void shouldSortWhenClickRemoveButtonToMoveItemsToAvailableTree()
	{
		assertThat(attributePicker.isNeedSort(BUTTON_ID_REMOVE)).isTrue();
	}

	@Test
	public void shouldNotSortWhenClickAddButtonToMoveItemsToSelectedTree()
	{
		assertThat(attributePicker.isNeedSort(BUTTON_ID_ADD)).isFalse();
	}

	@Test
	public void shouldSortItemsWhenRenderAvailableTree()
	{
		assertThat(attributePicker.isNeedSort(SECTION_ID_AVAILABLE)).isTrue();
	}

	@Test
	public void shouldNotSortItemsWhenRenderSelectedTree()
	{
		assertThat(attributePicker.isNeedSort(SECTION_ID_SELECTED)).isFalse();
	}

	@Test
	public void shouldReturnExtendedAttributeChooserTreeItemRenderer()
	{
		assertThat(attributePicker.getSelectedTreeItemRenderer()).isEqualTo(extendedAttributeChooserTreeItemRenderer);
	}

	@Test
	public void shouldAddPersonalizationSclassToWindow()
	{
		try (final var utilsMock = mockStatic(CockpitComponentsUtils.class))
		{
			//given
			final var parent = new Div();
			final var window = new Window();
			utilsMock.when(() -> CockpitComponentsUtils.findClosestComponent(parent, HtmlBasedComponent.class,
					PersonalizationConstants.SCLASS_YW_MODAL_CONFIGURABLEFLOW)).thenReturn(Optional.of(window));

			//when
			attributePicker.addPersonalizationSclassToWindow(parent);

			// then
			assertThat(window.getSclass()).isEqualTo(PersonalizationConstants.SCLASS_Y_PERSONALIZATION);
		}
	}

}
