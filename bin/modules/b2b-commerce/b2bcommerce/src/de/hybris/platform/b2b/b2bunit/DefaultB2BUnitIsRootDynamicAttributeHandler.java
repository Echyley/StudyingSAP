/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.b2bunit;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.servicelayer.model.attribute.DynamicAttributeHandler;

public class DefaultB2BUnitIsRootDynamicAttributeHandler implements DynamicAttributeHandler<Boolean, B2BUnitModel> {

	private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;

	public void setB2bUnitService(final B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService)
	{
		this.b2bUnitService = b2bUnitService;
	}

	@Override
	public Boolean get(B2BUnitModel b2BUnitModel) {
		return b2bUnitService.getParent(b2BUnitModel) == null;
	}

	@Override
	public void set(B2BUnitModel model, Boolean value) {
		throw new UnsupportedOperationException();
	}
}
