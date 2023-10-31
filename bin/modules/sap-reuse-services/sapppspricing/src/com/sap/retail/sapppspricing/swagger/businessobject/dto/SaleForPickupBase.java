/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.sapppspricing.swagger.businessobject.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/**
 * A flavor of a line item whereby customers purchase an item and pick it up from a location on a future date and time. SaleForPickup is currently not supported. 
 */
@Schema(description = "A flavor of a line item whereby customers purchase an item and pick it up from a location on a future date and time. SaleForPickup is currently not supported. ")
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2022-09-26T19:16:22.658615400+05:30[Asia/Calcutta]")
public class SaleForPickupBase {
  @JsonProperty("any")
  private List<Object> any = null;

  @JsonProperty("ItemID")
  private List<ItemID> itemID = null;

  @JsonProperty("MerchandiseHierarchy")
  private List<MerchandiseHierarchyCommonData> merchandiseHierarchy = null;

  @JsonProperty("RegularSalesUnitPrice")
  private UnitPriceCommonData regularSalesUnitPrice = null;

  @JsonProperty("ExtendedAmount")
  private ExtendedAmountType extendedAmount = null;

  @JsonProperty("ExtendedDiscountAmount")
  private AmountCommonData extendedDiscountAmount = null;

  @JsonProperty("Quantity")
  private List<QuantityCommonData> quantity = null;

  @JsonProperty("RetailPriceModifier")
  private List<RetailPriceModifierDomainSpecific> retailPriceModifier = null;

  @JsonProperty("ItemLink")
  private List<ItemLink> itemLink = null;

  @JsonProperty("ItemType")
  private String itemType = null;

  @JsonProperty("NonDiscountableFlag")
  private Boolean nonDiscountableFlag = null;

  @JsonProperty("FixedPriceFlag")
  private Boolean fixedPriceFlag = null;

  @JsonProperty("TaxIncludedInPriceFlag")
  private Boolean taxIncludedInPriceFlag = null;

  @JsonProperty("NonPieceGoodFlag")
  private Boolean nonPieceGoodFlag = null;

  @JsonProperty("FrequentShopperPointsEligibilityFlag")
  private Boolean frequentShopperPointsEligibilityFlag = null;

  @JsonProperty("DiscountTypeCode")
  private String discountTypeCode = null;

  @JsonProperty("PriceTypeCode")
  private String priceTypeCode = null;

  @JsonProperty("NotConsideredByPriceEngineFlag")
  private Boolean notConsideredByPriceEngineFlag = null;

  @JsonProperty("FrequentShopperPointsModifier")
  private List<FrequentShopperPointsModifierType> frequentShopperPointsModifier = null;

  @JsonProperty("PromotionPriceDerivationRuleReference")
  private List<PromotionPriceDerivationRuleReferenceType> promotionPriceDerivationRuleReference = null;

  @JsonProperty("PromotionManualTrigger")
  private List<PromotionManualTriggerType> promotionManualTrigger = null;

  @JsonProperty("LineItemAttribute")
  private List<LineItemAttributeType> lineItemAttribute = null;

  public SaleForPickupBase any(List<Object> any) {
    this.any = any;
    return this;
  }

  public SaleForPickupBase addAnyItem(Object anyItem) {
    if (this.any == null) {
      this.any = new ArrayList<Object>();
    }
    this.any.add(anyItem);
    return this;
  }

   /**
   * This is currently not supported.
   * @return any
  **/
  @Schema(description = "This is currently not supported.")
  public List<Object> getAny() {
    return any;
  }

  public void setAny(List<Object> any) {
    this.any = any;
  }

  public SaleForPickupBase itemID(List<ItemID> itemID) {
    this.itemID = itemID;
    return this;
  }

  public SaleForPickupBase addItemIDItem(ItemID itemIDItem) {
    if (this.itemID == null) {
      this.itemID = new ArrayList<ItemID>();
    }
    this.itemID.add(itemIDItem);
    return this;
  }

   /**
   * Get itemID
   * @return itemID
  **/
  @Schema(description = "")
  public List<ItemID> getItemID() {
    return itemID;
  }

  public void setItemID(List<ItemID> itemID) {
    this.itemID = itemID;
  }

  public SaleForPickupBase merchandiseHierarchy(List<MerchandiseHierarchyCommonData> merchandiseHierarchy) {
    this.merchandiseHierarchy = merchandiseHierarchy;
    return this;
  }

  public SaleForPickupBase addMerchandiseHierarchyItem(MerchandiseHierarchyCommonData merchandiseHierarchyItem) {
    if (this.merchandiseHierarchy == null) {
      this.merchandiseHierarchy = new ArrayList<MerchandiseHierarchyCommonData>();
    }
    this.merchandiseHierarchy.add(merchandiseHierarchyItem);
    return this;
  }

   /**
   * Get merchandiseHierarchy
   * @return merchandiseHierarchy
  **/
  @Schema(description = "")
  public List<MerchandiseHierarchyCommonData> getMerchandiseHierarchy() {
    return merchandiseHierarchy;
  }

  public void setMerchandiseHierarchy(List<MerchandiseHierarchyCommonData> merchandiseHierarchy) {
    this.merchandiseHierarchy = merchandiseHierarchy;
  }

  public SaleForPickupBase regularSalesUnitPrice(UnitPriceCommonData regularSalesUnitPrice) {
    this.regularSalesUnitPrice = regularSalesUnitPrice;
    return this;
  }

   /**
   * Get regularSalesUnitPrice
   * @return regularSalesUnitPrice
  **/
  @Schema(description = "")
  public UnitPriceCommonData getRegularSalesUnitPrice() {
    return regularSalesUnitPrice;
  }

  public void setRegularSalesUnitPrice(UnitPriceCommonData regularSalesUnitPrice) {
    this.regularSalesUnitPrice = regularSalesUnitPrice;
  }

  public SaleForPickupBase extendedAmount(ExtendedAmountType extendedAmount) {
    this.extendedAmount = extendedAmount;
    return this;
  }

   /**
   * Get extendedAmount
   * @return extendedAmount
  **/
  @Schema(description = "")
  public ExtendedAmountType getExtendedAmount() {
    return extendedAmount;
  }

  public void setExtendedAmount(ExtendedAmountType extendedAmount) {
    this.extendedAmount = extendedAmount;
  }

  public SaleForPickupBase extendedDiscountAmount(AmountCommonData extendedDiscountAmount) {
    this.extendedDiscountAmount = extendedDiscountAmount;
    return this;
  }

   /**
   * Get extendedDiscountAmount
   * @return extendedDiscountAmount
  **/
  @Schema(description = "")
  public AmountCommonData getExtendedDiscountAmount() {
    return extendedDiscountAmount;
  }

  public void setExtendedDiscountAmount(AmountCommonData extendedDiscountAmount) {
    this.extendedDiscountAmount = extendedDiscountAmount;
  }

  public SaleForPickupBase quantity(List<QuantityCommonData> quantity) {
    this.quantity = quantity;
    return this;
  }

  public SaleForPickupBase addQuantityItem(QuantityCommonData quantityItem) {
    if (this.quantity == null) {
      this.quantity = new ArrayList<QuantityCommonData>();
    }
    this.quantity.add(quantityItem);
    return this;
  }

   /**
   * Get quantity
   * @return quantity
  **/
  @Schema(description = "")
  public List<QuantityCommonData> getQuantity() {
    return quantity;
  }

  public void setQuantity(List<QuantityCommonData> quantity) {
    this.quantity = quantity;
  }

  public SaleForPickupBase retailPriceModifier(List<RetailPriceModifierDomainSpecific> retailPriceModifier) {
    this.retailPriceModifier = retailPriceModifier;
    return this;
  }

  public SaleForPickupBase addRetailPriceModifierItem(RetailPriceModifierDomainSpecific retailPriceModifierItem) {
    if (this.retailPriceModifier == null) {
      this.retailPriceModifier = new ArrayList<RetailPriceModifierDomainSpecific>();
    }
    this.retailPriceModifier.add(retailPriceModifierItem);
    return this;
  }

   /**
   * Get retailPriceModifier
   * @return retailPriceModifier
  **/
  @Schema(description = "")
  public List<RetailPriceModifierDomainSpecific> getRetailPriceModifier() {
    return retailPriceModifier;
  }

  public void setRetailPriceModifier(List<RetailPriceModifierDomainSpecific> retailPriceModifier) {
    this.retailPriceModifier = retailPriceModifier;
  }

  public SaleForPickupBase itemLink(List<ItemLink> itemLink) {
    this.itemLink = itemLink;
    return this;
  }

  public SaleForPickupBase addItemLinkItem(ItemLink itemLinkItem) {
    if (this.itemLink == null) {
      this.itemLink = new ArrayList<ItemLink>();
    }
    this.itemLink.add(itemLinkItem);
    return this;
  }

   /**
   * Get itemLink
   * @return itemLink
  **/
  @Schema(description = "")
  public List<ItemLink> getItemLink() {
    return itemLink;
  }

  public void setItemLink(List<ItemLink> itemLink) {
    this.itemLink = itemLink;
  }

  public SaleForPickupBase itemType(String itemType) {
    this.itemType = itemType;
    return this;
  }

   /**
   * Get itemType
   * @return itemType
  **/
  @Schema(description = "")
  public String getItemType() {
    return itemType;
  }

  public void setItemType(String itemType) {
    this.itemType = itemType;
  }

  public SaleForPickupBase nonDiscountableFlag(Boolean nonDiscountableFlag) {
    this.nonDiscountableFlag = nonDiscountableFlag;
    return this;
  }

   /**
   * Get nonDiscountableFlag
   * @return nonDiscountableFlag
  **/
  @Schema(description = "")
  public Boolean isNonDiscountableFlag() {
    return nonDiscountableFlag;
  }

  public void setNonDiscountableFlag(Boolean nonDiscountableFlag) {
    this.nonDiscountableFlag = nonDiscountableFlag;
  }

  public SaleForPickupBase fixedPriceFlag(Boolean fixedPriceFlag) {
    this.fixedPriceFlag = fixedPriceFlag;
    return this;
  }

   /**
   * Get fixedPriceFlag
   * @return fixedPriceFlag
  **/
  @Schema(description = "")
  public Boolean isFixedPriceFlag() {
    return fixedPriceFlag;
  }

  public void setFixedPriceFlag(Boolean fixedPriceFlag) {
    this.fixedPriceFlag = fixedPriceFlag;
  }

  public SaleForPickupBase taxIncludedInPriceFlag(Boolean taxIncludedInPriceFlag) {
    this.taxIncludedInPriceFlag = taxIncludedInPriceFlag;
    return this;
  }

   /**
   * Get taxIncludedInPriceFlag
   * @return taxIncludedInPriceFlag
  **/
  @Schema(description = "")
  public Boolean isTaxIncludedInPriceFlag() {
    return taxIncludedInPriceFlag;
  }

  public void setTaxIncludedInPriceFlag(Boolean taxIncludedInPriceFlag) {
    this.taxIncludedInPriceFlag = taxIncludedInPriceFlag;
  }

  public SaleForPickupBase nonPieceGoodFlag(Boolean nonPieceGoodFlag) {
    this.nonPieceGoodFlag = nonPieceGoodFlag;
    return this;
  }

   /**
   * Get nonPieceGoodFlag
   * @return nonPieceGoodFlag
  **/
  @Schema(description = "")
  public Boolean isNonPieceGoodFlag() {
    return nonPieceGoodFlag;
  }

  public void setNonPieceGoodFlag(Boolean nonPieceGoodFlag) {
    this.nonPieceGoodFlag = nonPieceGoodFlag;
  }

  public SaleForPickupBase frequentShopperPointsEligibilityFlag(Boolean frequentShopperPointsEligibilityFlag) {
    this.frequentShopperPointsEligibilityFlag = frequentShopperPointsEligibilityFlag;
    return this;
  }

   /**
   * Get frequentShopperPointsEligibilityFlag
   * @return frequentShopperPointsEligibilityFlag
  **/
  @Schema(description = "")
  public Boolean isFrequentShopperPointsEligibilityFlag() {
    return frequentShopperPointsEligibilityFlag;
  }

  public void setFrequentShopperPointsEligibilityFlag(Boolean frequentShopperPointsEligibilityFlag) {
    this.frequentShopperPointsEligibilityFlag = frequentShopperPointsEligibilityFlag;
  }

  public SaleForPickupBase discountTypeCode(String discountTypeCode) {
    this.discountTypeCode = discountTypeCode;
    return this;
  }

   /**
   * Get discountTypeCode
   * @return discountTypeCode
  **/
  @Schema(description = "")
  public String getDiscountTypeCode() {
    return discountTypeCode;
  }

  public void setDiscountTypeCode(String discountTypeCode) {
    this.discountTypeCode = discountTypeCode;
  }

  public SaleForPickupBase priceTypeCode(String priceTypeCode) {
    this.priceTypeCode = priceTypeCode;
    return this;
  }

   /**
   * Get priceTypeCode
   * @return priceTypeCode
  **/
  @Schema(description = "")
  public String getPriceTypeCode() {
    return priceTypeCode;
  }

  public void setPriceTypeCode(String priceTypeCode) {
    this.priceTypeCode = priceTypeCode;
  }

  public SaleForPickupBase notConsideredByPriceEngineFlag(Boolean notConsideredByPriceEngineFlag) {
    this.notConsideredByPriceEngineFlag = notConsideredByPriceEngineFlag;
    return this;
  }

   /**
   * Get notConsideredByPriceEngineFlag
   * @return notConsideredByPriceEngineFlag
  **/
  @Schema(description = "")
  public Boolean isNotConsideredByPriceEngineFlag() {
    return notConsideredByPriceEngineFlag;
  }

  public void setNotConsideredByPriceEngineFlag(Boolean notConsideredByPriceEngineFlag) {
    this.notConsideredByPriceEngineFlag = notConsideredByPriceEngineFlag;
  }

  public SaleForPickupBase frequentShopperPointsModifier(List<FrequentShopperPointsModifierType> frequentShopperPointsModifier) {
    this.frequentShopperPointsModifier = frequentShopperPointsModifier;
    return this;
  }

  public SaleForPickupBase addFrequentShopperPointsModifierItem(FrequentShopperPointsModifierType frequentShopperPointsModifierItem) {
    if (this.frequentShopperPointsModifier == null) {
      this.frequentShopperPointsModifier = new ArrayList<FrequentShopperPointsModifierType>();
    }
    this.frequentShopperPointsModifier.add(frequentShopperPointsModifierItem);
    return this;
  }

   /**
   * Get frequentShopperPointsModifier
   * @return frequentShopperPointsModifier
  **/
  @Schema(description = "")
  public List<FrequentShopperPointsModifierType> getFrequentShopperPointsModifier() {
    return frequentShopperPointsModifier;
  }

  public void setFrequentShopperPointsModifier(List<FrequentShopperPointsModifierType> frequentShopperPointsModifier) {
    this.frequentShopperPointsModifier = frequentShopperPointsModifier;
  }

  public SaleForPickupBase promotionPriceDerivationRuleReference(List<PromotionPriceDerivationRuleReferenceType> promotionPriceDerivationRuleReference) {
    this.promotionPriceDerivationRuleReference = promotionPriceDerivationRuleReference;
    return this;
  }

  public SaleForPickupBase addPromotionPriceDerivationRuleReferenceItem(PromotionPriceDerivationRuleReferenceType promotionPriceDerivationRuleReferenceItem) {
    if (this.promotionPriceDerivationRuleReference == null) {
      this.promotionPriceDerivationRuleReference = new ArrayList<PromotionPriceDerivationRuleReferenceType>();
    }
    this.promotionPriceDerivationRuleReference.add(promotionPriceDerivationRuleReferenceItem);
    return this;
  }

   /**
   * Get promotionPriceDerivationRuleReference
   * @return promotionPriceDerivationRuleReference
  **/
  @Schema(description = "")
  public List<PromotionPriceDerivationRuleReferenceType> getPromotionPriceDerivationRuleReference() {
    return promotionPriceDerivationRuleReference;
  }

  public void setPromotionPriceDerivationRuleReference(List<PromotionPriceDerivationRuleReferenceType> promotionPriceDerivationRuleReference) {
    this.promotionPriceDerivationRuleReference = promotionPriceDerivationRuleReference;
  }

  public SaleForPickupBase promotionManualTrigger(List<PromotionManualTriggerType> promotionManualTrigger) {
    this.promotionManualTrigger = promotionManualTrigger;
    return this;
  }

  public SaleForPickupBase addPromotionManualTriggerItem(PromotionManualTriggerType promotionManualTriggerItem) {
    if (this.promotionManualTrigger == null) {
      this.promotionManualTrigger = new ArrayList<PromotionManualTriggerType>();
    }
    this.promotionManualTrigger.add(promotionManualTriggerItem);
    return this;
  }

   /**
   * Get promotionManualTrigger
   * @return promotionManualTrigger
  **/
  @Schema(description = "")
  public List<PromotionManualTriggerType> getPromotionManualTrigger() {
    return promotionManualTrigger;
  }

  public void setPromotionManualTrigger(List<PromotionManualTriggerType> promotionManualTrigger) {
    this.promotionManualTrigger = promotionManualTrigger;
  }

  public SaleForPickupBase lineItemAttribute(List<LineItemAttributeType> lineItemAttribute) {
    this.lineItemAttribute = lineItemAttribute;
    return this;
  }

  public SaleForPickupBase addLineItemAttributeItem(LineItemAttributeType lineItemAttributeItem) {
    if (this.lineItemAttribute == null) {
      this.lineItemAttribute = new ArrayList<LineItemAttributeType>();
    }
    this.lineItemAttribute.add(lineItemAttributeItem);
    return this;
  }

   /**
   * Get lineItemAttribute
   * @return lineItemAttribute
  **/
  @Schema(description = "")
  public List<LineItemAttributeType> getLineItemAttribute() {
    return lineItemAttribute;
  }

  public void setLineItemAttribute(List<LineItemAttributeType> lineItemAttribute) {
    this.lineItemAttribute = lineItemAttribute;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SaleForPickupBase saleForPickupBase = (SaleForPickupBase) o;
    return Objects.equals(this.any, saleForPickupBase.any) &&
        Objects.equals(this.itemID, saleForPickupBase.itemID) &&
        Objects.equals(this.merchandiseHierarchy, saleForPickupBase.merchandiseHierarchy) &&
        Objects.equals(this.regularSalesUnitPrice, saleForPickupBase.regularSalesUnitPrice) &&
        Objects.equals(this.extendedAmount, saleForPickupBase.extendedAmount) &&
        Objects.equals(this.extendedDiscountAmount, saleForPickupBase.extendedDiscountAmount) &&
        Objects.equals(this.quantity, saleForPickupBase.quantity) &&
        Objects.equals(this.retailPriceModifier, saleForPickupBase.retailPriceModifier) &&
        Objects.equals(this.itemLink, saleForPickupBase.itemLink) &&
        Objects.equals(this.itemType, saleForPickupBase.itemType) &&
        Objects.equals(this.nonDiscountableFlag, saleForPickupBase.nonDiscountableFlag) &&
        Objects.equals(this.fixedPriceFlag, saleForPickupBase.fixedPriceFlag) &&
        Objects.equals(this.taxIncludedInPriceFlag, saleForPickupBase.taxIncludedInPriceFlag) &&
        Objects.equals(this.nonPieceGoodFlag, saleForPickupBase.nonPieceGoodFlag) &&
        Objects.equals(this.frequentShopperPointsEligibilityFlag, saleForPickupBase.frequentShopperPointsEligibilityFlag) &&
        Objects.equals(this.discountTypeCode, saleForPickupBase.discountTypeCode) &&
        Objects.equals(this.priceTypeCode, saleForPickupBase.priceTypeCode) &&
        Objects.equals(this.notConsideredByPriceEngineFlag, saleForPickupBase.notConsideredByPriceEngineFlag) &&
        Objects.equals(this.frequentShopperPointsModifier, saleForPickupBase.frequentShopperPointsModifier) &&
        Objects.equals(this.promotionPriceDerivationRuleReference, saleForPickupBase.promotionPriceDerivationRuleReference) &&
        Objects.equals(this.promotionManualTrigger, saleForPickupBase.promotionManualTrigger) &&
        Objects.equals(this.lineItemAttribute, saleForPickupBase.lineItemAttribute);
  }

  @Override
  public int hashCode() {
    return Objects.hash(any, itemID, merchandiseHierarchy, regularSalesUnitPrice, extendedAmount, extendedDiscountAmount, quantity, retailPriceModifier, itemLink, itemType, nonDiscountableFlag, fixedPriceFlag, taxIncludedInPriceFlag, nonPieceGoodFlag, frequentShopperPointsEligibilityFlag, discountTypeCode, priceTypeCode, notConsideredByPriceEngineFlag, frequentShopperPointsModifier, promotionPriceDerivationRuleReference, promotionManualTrigger, lineItemAttribute);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SaleForPickupBase {\n");
    
    sb.append("    any: ").append(toIndentedString(any)).append("\n");
    sb.append("    itemID: ").append(toIndentedString(itemID)).append("\n");
    sb.append("    merchandiseHierarchy: ").append(toIndentedString(merchandiseHierarchy)).append("\n");
    sb.append("    regularSalesUnitPrice: ").append(toIndentedString(regularSalesUnitPrice)).append("\n");
    sb.append("    extendedAmount: ").append(toIndentedString(extendedAmount)).append("\n");
    sb.append("    extendedDiscountAmount: ").append(toIndentedString(extendedDiscountAmount)).append("\n");
    sb.append("    quantity: ").append(toIndentedString(quantity)).append("\n");
    sb.append("    retailPriceModifier: ").append(toIndentedString(retailPriceModifier)).append("\n");
    sb.append("    itemLink: ").append(toIndentedString(itemLink)).append("\n");
    sb.append("    itemType: ").append(toIndentedString(itemType)).append("\n");
    sb.append("    nonDiscountableFlag: ").append(toIndentedString(nonDiscountableFlag)).append("\n");
    sb.append("    fixedPriceFlag: ").append(toIndentedString(fixedPriceFlag)).append("\n");
    sb.append("    taxIncludedInPriceFlag: ").append(toIndentedString(taxIncludedInPriceFlag)).append("\n");
    sb.append("    nonPieceGoodFlag: ").append(toIndentedString(nonPieceGoodFlag)).append("\n");
    sb.append("    frequentShopperPointsEligibilityFlag: ").append(toIndentedString(frequentShopperPointsEligibilityFlag)).append("\n");
    sb.append("    discountTypeCode: ").append(toIndentedString(discountTypeCode)).append("\n");
    sb.append("    priceTypeCode: ").append(toIndentedString(priceTypeCode)).append("\n");
    sb.append("    notConsideredByPriceEngineFlag: ").append(toIndentedString(notConsideredByPriceEngineFlag)).append("\n");
    sb.append("    frequentShopperPointsModifier: ").append(toIndentedString(frequentShopperPointsModifier)).append("\n");
    sb.append("    promotionPriceDerivationRuleReference: ").append(toIndentedString(promotionPriceDerivationRuleReference)).append("\n");
    sb.append("    promotionManualTrigger: ").append(toIndentedString(promotionManualTrigger)).append("\n");
    sb.append("    lineItemAttribute: ").append(toIndentedString(lineItemAttribute)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}
