/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapcommonbol.common.salesarea.businessobject.interf;

/**
 * Interface provides the mapping information for the sales organisation and division. <br>
 *
 * @version 1.0
 */
public interface DivisionMapping {

    /**
     * @return alternative division for the customer data
     */
    String getDivisionForCustomers();

    /**
     * @param divisionForCustomers alternative division for the customer data
     */
    void setDivisionForCustomers(String divisionForCustomers);

    /**
     * @return division for the condition technique (pricing for instance)
     */
    String getDivisionForConditions();

    /**
     * @param divisionForConditions division for the condition technique (pricing for instance)
     */
    void setDivisionForConditions(String divisionForConditions);

    /**
     * @return division for the document management
     */
    String getDivisionForDocumentTypes();

    /**
     * @param divisionForDocumentTypes division for the document management
     */
    void setDivisionForDocumentTypes(String divisionForDocumentTypes);

}
