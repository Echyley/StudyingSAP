package com.hybris.backoffice.personalization.listview.renderer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.AbstractTreeModel;
import org.zkoss.zul.Button;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treeitem;

import com.google.common.collect.Sets;
import com.hybris.backoffice.attributechooser.Attribute;
import com.hybris.backoffice.attributechooser.AttributesChooserNodesOperationsHandler;
import com.hybris.cockpitng.testing.util.CockpitTestUtil;


@RunWith(MockitoJUnitRunner.class)
public class ExtendedAttributeChooserTreeItemRendererTest
{

	@Spy
	private final AttributesChooserNodesOperationsHandler nodeOperationsHandler = new AttributesChooserNodesOperationsHandler();

	@Spy
	private Treeitem treeitem = new Treeitem();
	@Spy
	private Tree tree = new Tree();
	private List<DefaultTreeNode<Attribute>> treeNodes;

	@InjectMocks
	@Spy
	private ExtendedAttributeChooserTreeItemRenderer renderer;

	@Before
	public void setUp()
	{
		CockpitTestUtil.mockZkEnvironment();
		when(treeitem.getTree()).thenReturn(tree);

		final Attribute name = new Attribute("name", "Name", false);
		final Attribute id = new Attribute("id", "id", true);
		final Attribute pk = new Attribute("pk", "pk", false);
		final Attribute des = new Attribute("des", "des", true);

		treeNodes = nodeOperationsHandler.createTreeNodes(Sets.newHashSet(name, id, pk, des));
		final DefaultTreeModel<Attribute> model = new DefaultTreeModel<>(new DefaultTreeNode<>(null, treeNodes));
	}

	@Test
	public void shouldRenderEnabledMoveUpAndMoveDownButtonOnTreeItem()
	{
		//given
		final var index = 1;

		//when
		renderer.render(treeitem, treeNodes.get(index), index);
		final var moveUpButton = findMoveUpButton(treeitem);
		final var moveDownButton = findMoveDownButton(treeitem);

		//then
		assertThat(moveUpButton).isNotNull();
		assertThat(moveDownButton).isNotNull();
		assertThat(moveUpButton.isDisabled()).isFalse();
		assertThat(moveDownButton.isDisabled()).isFalse();
	}

	@Test
	public void shouldDisableMoveUpButtonOnFirstTreeItem()
	{
		//given
		final var index = 0;

		//when
		renderer.render(treeitem, treeNodes.get(index), index);
		final var moveUpButton = findMoveUpButton(treeitem);
		final var moveDownButton = findMoveDownButton(treeitem);

		//then
		assertThat(moveUpButton.isDisabled()).isTrue();
		assertThat(moveDownButton.isDisabled()).isFalse();
	}

	@Test
	public void shouldDisableMoveDownButtonOnLastTreeItem()
	{
		//given
		final var index = treeNodes.size() - 1;

		//when
		renderer.render(treeitem, treeNodes.get(index), index);
		final var moveUpButton = findMoveUpButton(treeitem);
		final var moveDownButton = findMoveDownButton(treeitem);

		//then
		assertThat(moveUpButton.isDisabled()).isFalse();
		assertThat(moveDownButton.isDisabled()).isTrue();
	}

	@Test
	public void shouldMoveItemUpWhenClickMoveUpButton()
	{
		//given
		final var model = mock(AbstractTreeModel.class);
		when(tree.getModel()).thenReturn(model);
		final var index = 1;
		final var data = treeNodes.get(index);

		//when
		renderer.render(treeitem, data, index);
		click(findMoveUpButton(treeitem));

		//then
		assertThat(data.getParent().getChildren().indexOf(data)).isEqualTo(index - 1);
		verify(nodeOperationsHandler).refreshFilteredTree(model);
	}

	@Test
	public void shouldMoveItemDownWhenClickMoveDownButton()
	{
		//given
		final var model = mock(AbstractTreeModel.class);
		when(tree.getModel()).thenReturn(model);
		final var index = 1;
		final var data = treeNodes.get(index);

		//when
		renderer.render(treeitem, data, index);
		click(findMoveDownButton(treeitem));

		//then
		assertThat(data.getParent().getChildren().indexOf(data)).isEqualTo(index + 1);
		verify(nodeOperationsHandler).refreshFilteredTree(model);
	}

	@Test
	public void shouldKeepSelectionWhenMoveItemUpOrDown()
	{
		//given
		final var model = mock(AbstractTreeModel.class);
		when(tree.getModel()).thenReturn(model);
		final var index = 1;
		final var data = treeNodes.get(index);
		when(model.getSelection()).thenReturn(Sets.newHashSet(data));

		//when
		renderer.render(treeitem, data, index);
		click(findMoveDownButton(treeitem));

		//then
		verify(model).addToSelection(data);
	}

	private Button findMoveUpButton(final Component parent)
	{
		return (Button) parent.query(".y-attributepicker__btn--up.y-btn-transparent");
	}

	private Button findMoveDownButton(final Treeitem parent)
	{
		return (Button) parent.query(".y-attributepicker__btn--down.y-btn-transparent");
	}

	private static void click(final Component button)
	{
		CockpitTestUtil.simulateEvent(button, Events.ON_CLICK, null);
	}

}
