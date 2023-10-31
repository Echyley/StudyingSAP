/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf;

/**
 * Represents the OverallStatus object. <br>
 * 
 */
public interface OverallStatus extends BusinessStatus {
    /**
     * @return <code>true</code>, only if the document was already cancelled
     */
    boolean isCancelled();

}
