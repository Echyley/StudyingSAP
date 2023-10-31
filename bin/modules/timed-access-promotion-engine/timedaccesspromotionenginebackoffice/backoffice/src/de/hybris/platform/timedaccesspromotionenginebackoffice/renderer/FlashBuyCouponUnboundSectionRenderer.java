/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.timedaccesspromotionenginebackoffice.renderer;

import java.util.List;


import com.hybris.cockpitng.core.config.impl.jaxb.editorarea.Attribute;
import com.hybris.cockpitng.core.config.impl.jaxb.editorarea.Section;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.widgets.editorarea.sections.UnboundSectionRenderer;


public class FlashBuyCouponUnboundSectionRenderer extends UnboundSectionRenderer
{
	private List<String> unDisplayedFlashBuyCouponAttrList;

	@Override
	protected Section getUnboundSection(final WidgetInstanceManager widgetInstanceManager)
	{
		final Section unboundSection = super.getUnboundSection(widgetInstanceManager);
		unboundSection.getAttributeOrCustom().removeIf(p -> {
			final Attribute attr = (Attribute) p;
			return getUnDisplayedFlashBuyCouponAttrList().contains(attr.getQualifier());
		});

		return unboundSection;
	}

	protected List<String> getUnDisplayedFlashBuyCouponAttrList()
	{
		return unDisplayedFlashBuyCouponAttrList;
	}


	public void setUnDisplayedFlashBuyCouponAttrList(final List<String> unDisplayedFlashBuyCouponAttrList)
	{
		this.unDisplayedFlashBuyCouponAttrList = unDisplayedFlashBuyCouponAttrList;
	}
}
