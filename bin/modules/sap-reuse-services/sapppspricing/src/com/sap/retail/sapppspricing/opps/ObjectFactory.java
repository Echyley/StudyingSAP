/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.sapppspricing.opps;

import com.sap.retail.sapppspricing.swagger.businessobject.dto.ARTSCommonHeaderType;
import com.sap.retail.sapppspricing.swagger.businessobject.dto.PriceCalculate;
import com.sap.retail.sapppspricing.swagger.businessobject.dto.PriceCalculateBase;
import com.sap.retail.sapppspricing.swagger.businessobject.dto.BusinessUnitCommonData;
import com.sap.retail.sapppspricing.swagger.businessobject.dto.DateTimeCommonData;
import com.sap.retail.sapppspricing.swagger.businessobject.dto.IDCommonData;
import com.sap.retail.sapppspricing.swagger.businessobject.dto.MerchandiseHierarchyCommonData;
import com.sap.retail.sapppspricing.swagger.businessobject.dto.QuantityCommonData;
import com.sap.retail.sapppspricing.swagger.businessobject.dto.SaleBase;
import com.sap.retail.sapppspricing.swagger.businessobject.dto.LineItemDomainSpecific;
import com.sap.retail.sapppspricing.swagger.businessobject.dto.ShoppingBasketBase;
import com.sap.retail.sapppspricing.swagger.businessobject.dto.ItemID;
import com.sap.retail.sapppspricing.swagger.businessobject.dto.HeaderDateTime;
import com.sap.retail.sapppspricing.swagger.businessobject.dto.MessageID;

public class ObjectFactory {

	
    public ObjectFactory() {
    	/**
         * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: removeInclDot.com.sap.retail.sapppspricing.opps
         * 
         */
    }

    /**
     * Create an instance of {@link ARTSCommonHeaderType }
     * 
     */
    public ARTSCommonHeaderType createARTSCommonHeaderType() {
        return new ARTSCommonHeaderType();
    }

    /**
     * Create an instance of {@link PriceCalculate }
     * 
     */
    public PriceCalculate createPriceCalculate() {
        return new PriceCalculate();
    }

    /**
     * Create an instance of {@link PriceCalculateBase }
     * 
     */
    public PriceCalculateBase createPriceCalculateBase() {
        return new PriceCalculateBase();
    }
    
    /**
     * Create an instance of {@link BusinessUnitCommonData }
     * 
     */
    public BusinessUnitCommonData createBusinessUnitCommonData() {
        return new BusinessUnitCommonData();
    }

    /**
     * Create an instance of {@link DateTimeCommonData }
     * 
     */
    public DateTimeCommonData createDateTimeCommonData() {
        return new DateTimeCommonData();
    }

    /**
     * Create an instance of {@link IDCommonData }
     * 
     */
    public IDCommonData createIDCommonData() {
        return new IDCommonData();
    }


    /**
     * Create an instance of {@link MerchandiseHierarchyCommonData }
     * 
     */
    public MerchandiseHierarchyCommonData createMerchandiseHierarchyCommonData() {
        return new MerchandiseHierarchyCommonData();
    }

    /**
     * Create an instance of {@link QuantityCommonData }
     * 
     */
    public QuantityCommonData createQuantityCommonData() {
        return new QuantityCommonData();
    }

    /**
     * Create an instance of {@link SaleBase }
     * 
     */
    public SaleBase createSaleBase() {
        return new SaleBase();
    }


    /**
     * Create an instance of {@link LineItemDomainSpecific }
     * 
     */
    public LineItemDomainSpecific createLineItemDomainSpecific() {
        return new LineItemDomainSpecific();
    }

    /**
     * Create an instance of {@link ShoppingBasketBase }
     * 
     */
    public ShoppingBasketBase createShoppingBasketBase() {
        return new ShoppingBasketBase();
    }


    /**
     * Create an instance of {@link ItemBase.ItemID }
     * 
     */
    public ItemID createItemBaseItemID() {
        return new ItemID();
    }


    /**
     * Create an instance of {@link ARTSCommonHeaderType.DateTime }
     * 
     */
    public HeaderDateTime createARTSCommonHeaderTypeDateTime() {
       return new HeaderDateTime();
   }
    
    /**
     * Create an instance of {@link ARTSCommonHeaderType.MessageID }
     * 
     */
    public MessageID createARTSCommonHeaderTypeMessageID() {
        return new MessageID();
    }

}