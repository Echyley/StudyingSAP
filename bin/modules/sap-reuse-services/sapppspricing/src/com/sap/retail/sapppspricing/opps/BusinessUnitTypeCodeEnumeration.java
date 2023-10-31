/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.sapppspricing.opps;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BusinessUnitTypeCodeEnumeration.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="BusinessUnitTypeCodeEnumeration"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN"&gt;
 *     &lt;enumeration value="RetailStore"/&gt;
 *     &lt;enumeration value="DistributionCenter"/&gt;
 *     &lt;enumeration value="AdministrationCenter"/&gt;
 *     &lt;enumeration value="CallCenter"/&gt;
 *     &lt;enumeration value="WebSite"/&gt;
 *     &lt;enumeration value="Distributor"/&gt;
 *     &lt;enumeration value="Filler"/&gt;
 *     &lt;enumeration value="TransitCellar"/&gt;
 *     &lt;enumeration value="Producer"/&gt;
 *     &lt;enumeration value="Grower"/&gt;
 *     &lt;enumeration value="Factory"/&gt;
 *     &lt;enumeration value="Building"/&gt;
 *     &lt;enumeration value="Dormitory"/&gt;
 *     &lt;enumeration value="Vendor"/&gt;
 *     &lt;enumeration value="Customer"/&gt;
 *     &lt;enumeration value="DistributionChain"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "BusinessUnitTypeCodeEnumeration", namespace = "http://www.sap.com/IXRetail/namespace/")
@XmlEnum
public enum BusinessUnitTypeCodeEnumeration {

    @XmlEnumValue("RetailStore")
    RETAIL_STORE("RetailStore"),
    @XmlEnumValue("DistributionCenter")
    DISTRIBUTION_CENTER("DistributionCenter"),
    @XmlEnumValue("AdministrationCenter")
    ADMINISTRATION_CENTER("AdministrationCenter"),
    @XmlEnumValue("CallCenter")
    CALL_CENTER("CallCenter"),
    @XmlEnumValue("WebSite")
    WEB_SITE("WebSite"),
    @XmlEnumValue("Distributor")
    DISTRIBUTOR("Distributor"),
    @XmlEnumValue("Filler")
    FILLER("Filler"),
    @XmlEnumValue("TransitCellar")
    TRANSIT_CELLAR("TransitCellar"),
    @XmlEnumValue("Producer")
    PRODUCER("Producer"),
    @XmlEnumValue("Grower")
    GROWER("Grower"),
    @XmlEnumValue("Factory")
    FACTORY("Factory"),
    @XmlEnumValue("Building")
    BUILDING("Building"),
    @XmlEnumValue("Dormitory")
    DORMITORY("Dormitory"),
    @XmlEnumValue("Vendor")
    VENDOR("Vendor"),
    @XmlEnumValue("Customer")
    CUSTOMER("Customer"),
    @XmlEnumValue("DistributionChain")
    DISTRIBUTION_CHAIN("DistributionChain");
    private final String inputValue;

    BusinessUnitTypeCodeEnumeration(String v) {
        inputValue = v;
    }

    public String value() {
        return inputValue;
    }

    public static BusinessUnitTypeCodeEnumeration fromValue(String v) {
        for (BusinessUnitTypeCodeEnumeration c: BusinessUnitTypeCodeEnumeration.values()) {
            if (c.inputValue.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
