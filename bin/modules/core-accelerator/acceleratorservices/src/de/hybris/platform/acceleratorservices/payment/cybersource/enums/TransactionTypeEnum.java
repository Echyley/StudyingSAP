/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.payment.cybersource.enums;

/**
 * This Enum represents the different types of transactions that can be requested from the CyberSource Hosted Order Page Service.
 */
public enum TransactionTypeEnum
{
	// Constant names cannot be changed due to their usage in dependant extensions
	authorization, 
	sale, 
	subscription, 
	subscription_modify, 
	subscription_credit, 
	authReversal 
}
