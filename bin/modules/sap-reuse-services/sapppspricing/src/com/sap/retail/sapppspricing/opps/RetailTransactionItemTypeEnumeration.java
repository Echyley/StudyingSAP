/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.sapppspricing.opps;

public enum RetailTransactionItemTypeEnumeration {
	    STOCK("Stock"),
	    GRADE("Grade"),
	    SERVICE("Service"),
	    ALTERATION("Alteration"),
	    FEE("Fee"),
	    FEE_REFUND("FeeRefund"),
	    DEPOSIT("Deposit"),
	    DEPOSIT_REFUND("DepositRefund"),
	    TARE("Tare"),
	    ITEM_COLLECTION("ItemCollection"),
	    WARRANTY("Warranty"),
	    GIFT_CERTIFICATE("GiftCertificate");
	    private final String inputValue;

	    RetailTransactionItemTypeEnumeration(String v) {
	        inputValue = v;
	    }

	    public String value() {
	        return inputValue;
	    }

	    public static RetailTransactionItemTypeEnumeration fromValue(String v) {
	        for (RetailTransactionItemTypeEnumeration c: RetailTransactionItemTypeEnumeration.values()) {
	            if (c.inputValue.equals(v)) {
	                return c;
	            }
	        }
	        throw new IllegalArgumentException(v);
	    }
}
