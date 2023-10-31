/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapcommonbol.common.salesarea.businessobject.interf;


/**
 * Interface builds the key for the sales organisation and division mapping. <br>
 *
 * @version 1.0
 */
public interface DivisionMappingKey {

    /**
     * @return sales organisation
     */
    String getSalesOrg();

    /**
     * @param salesOrg  sales organisation
     */
    void setSalesOrg(String salesOrg);

    /**
     * @return division
     */
    String getDivision();

    /**
     * @param division division
     */
    void setDivision(String division);

}
