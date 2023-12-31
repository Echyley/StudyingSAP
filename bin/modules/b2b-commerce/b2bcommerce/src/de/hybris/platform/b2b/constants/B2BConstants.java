/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.constants;

public class B2BConstants
{
	private B2BConstants()
	{
		throw new IllegalAccessError("Constants Class B2BConstants");
	}

	public static final String EXTENSIONNAME = "b2bcommerce";
	public static final String CTX_ATTRIBUTE_BRANCH = "branch";
	public static final String CTX_ATTRIBUTE_UNIT = "unit";
	public static final String B2BCUSTOMERGROUP = "b2bcustomergroup";
	public static final String B2BADMINGROUP = "b2badmingroup";
	public static final String B2BAPPROVERGROUP = "b2bapprovergroup";
	public static final String B2BMANAGERGROUP = "b2bmanagergroup";
	public static final String B2BDEFAULTPRICEGROUP = "B2B_DEFAULT_PRICE_GROUP";
	public static final String B2BGROUP = "b2bgroup";
	public static final String UNITORDERVIEWERGROUP = "unitorderviewergroup";
	public static final String CTX_ATTRIBUTE_ROOTUNIT = "rootunit";
	public static final String CTX_ATTRIBUTE_ERP_PRICE = "erpprice";
	public static final String APPROVAL_PROCESS_NAMES_CONFIG_KEY = "b2bapprovalprocess.codes";
	public static final String DISABLE_RESTRICTIONS = "disableRestrictions";
	public static final String TWOPOE_APPROVERS_A_GROUP = "2POE_APPROVERS_A";
	public static final String TWOPOE_APPROVERS_B_GROUP = "2POE_APPROVERS_B";
	public static final String DEFAULT_SORT_CODE_PROP = "b2bcommerce.defaultSortCode";
	public static final String UNITLEVELORDERS_ENABLED = "b2bcommerce.unitlevelorders.enabled";

	public static final class Workflows
	{
		public static final String REGISTRATION_WORKFLOW = "B2BUserRegistration";

		private Workflows()
		{
			throw new IllegalAccessError("Constants Class Workflows");
		}
	}

	public static final class UserGroups
	{
		public static final String REGISTRATION_APPROVER_GROUP = "b2bregistrationapprovergroup";

		private UserGroups()
		{
			throw new IllegalAccessError("Constants Class UserGroups");
		}
	}

}
