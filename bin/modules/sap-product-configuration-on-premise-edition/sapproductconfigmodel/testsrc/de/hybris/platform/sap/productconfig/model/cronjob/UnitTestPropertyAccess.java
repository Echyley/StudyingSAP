/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.model.cronjob;

@SuppressWarnings("javadoc")
public class UnitTestPropertyAccess implements PropertyAccessFacade
{

	private boolean startDeltaloadAfterInitial = true;


	public void setStartDeltaloadAfterInitial(final boolean startDeltaloadAfterInitial)
	{
		this.startDeltaloadAfterInitial = startDeltaloadAfterInitial;
	}


	@Override
	public boolean getStartDeltaloadAfterInitial()
	{
		return startDeltaloadAfterInitial;
	}

}
