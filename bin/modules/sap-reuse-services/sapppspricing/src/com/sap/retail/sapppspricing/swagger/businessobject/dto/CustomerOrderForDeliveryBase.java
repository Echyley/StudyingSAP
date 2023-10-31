/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.sapppspricing.swagger.businessobject.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/**
 * A flavor of a line item whereby customers purchase an unavailable item. The store places a special order on behalf of this purchase, and the item is to be delivered as soon as available. The customer may, or may not pay for the item at the time of the order placement. 
 */
@Schema(description = "A flavor of a line item whereby customers purchase an unavailable item. The store places a special order on behalf of this purchase, and the item is to be delivered as soon as available. The customer may, or may not pay for the item at the time of the order placement. ")
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2022-09-26T19:16:22.658615400+05:30[Asia/Calcutta]")
public class CustomerOrderForDeliveryBase {
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

  /**
   * Reserved for future use.
   */
  public enum ItemTypeEnum {
    STOCK("Stock"),
    GIFTCERTIFICATE("GiftCertificate");

    private String value;

    ItemTypeEnum(String value) {
      this.value = value;
    }
    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }
    @JsonCreator
    public static ItemTypeEnum fromValue(String input) {
      for (ItemTypeEnum b : ItemTypeEnum.values()) {
        if (b.value.equals(input)) {
          return b;
        }
      }
      return null;
    }

  }  @JsonProperty("ItemType")
  private ItemTypeEnum itemType = null;

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

  /**
   * Reserved for future use. Value 00 corresponds to \&quot;regular price\&quot;.
   */
  public enum PriceTypeCodeEnum {
    _00("00"),
    _01("01");

    private String value;

    PriceTypeCodeEnum(String value) {
      this.value = value;
    }
    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }
    @JsonCreator
    public static PriceTypeCodeEnum fromValue(String input) {
      for (PriceTypeCodeEnum b : PriceTypeCodeEnum.values()) {
        if (b.value.equals(input)) {
          return b;
        }
      }
      return null;
    }

  }  @JsonProperty("PriceTypeCode")
  private PriceTypeCodeEnum priceTypeCode = null;

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

  public CustomerOrderForDeliveryBase any(List<Object> any) {
    this.any = any;
    return this;
  }

  public CustomerOrderForDeliveryBase addAnyItem(Object anyItem) {
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

  public CustomerOrderForDeliveryBase itemID(List<ItemID> itemID) {
    this.itemID = itemID;
    return this;
  }

  public CustomerOrderForDeliveryBase addItemIDItem(ItemID itemIDItem) {
    if (this.itemID == null) {
      this.itemID = new ArrayList<ItemID>();
    }
    this.itemID.add(itemIDItem);
    return this;
  }

   /**
   * Although this is an array, only 1 entry is supported.
   * @return itemID
  **/
  @Schema(description = "Although this is an array, only 1 entry is supported.")
  public List<ItemID> getItemID() {
    return itemID;
  }

  public void setItemID(List<ItemID> itemID) {
    this.itemID = itemID;
  }

  public CustomerOrderForDeliveryBase merchandiseHierarchy(List<MerchandiseHierarchyCommonData> merchandiseHierarchy) {
    this.merchandiseHierarchy = merchandiseHierarchy;
    return this;
  }

  public CustomerOrderForDeliveryBase addMerchandiseHierarchyItem(MerchandiseHierarchyCommonData merchandiseHierarchyItem) {
    if (this.merchandiseHierarchy == null) {
      this.merchandiseHierarchy = new ArrayList<MerchandiseHierarchyCommonData>();
    }
    this.merchandiseHierarchy.add(merchandiseHierarchyItem);
    return this;
  }

   /**
   * For information purposes only. The relevant list of hierarchy nodes is expected on the level of LineItemDomainSpecific.
   * @return merchandiseHierarchy
  **/
  @Schema(description = "For information purposes only. The relevant list of hierarchy nodes is expected on the level of LineItemDomainSpecific.")
  public List<MerchandiseHierarchyCommonData> getMerchandiseHierarchy() {
    return merchandiseHierarchy;
  }

  public void setMerchandiseHierarchy(List<MerchandiseHierarchyCommonData> merchandiseHierarchy) {
    this.merchandiseHierarchy = merchandiseHierarchy;
  }

  public CustomerOrderForDeliveryBase regularSalesUnitPrice(UnitPriceCommonData regularSalesUnitPrice) {
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

  public CustomerOrderForDeliveryBase extendedAmount(ExtendedAmountType extendedAmount) {
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

  public CustomerOrderForDeliveryBase extendedDiscountAmount(AmountCommonData extendedDiscountAmount) {
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

  public CustomerOrderForDeliveryBase quantity(List<QuantityCommonData> quantity) {
    this.quantity = quantity;
    return this;
  }

  public CustomerOrderForDeliveryBase addQuantityItem(QuantityCommonData quantityItem) {
    if (this.quantity == null) {
      this.quantity = new ArrayList<QuantityCommonData>();
    }
    this.quantity.add(quantityItem);
    return this;
  }

   /**
   * The quantity of this line item. Although this is an array, only 1 entry is allowed.
   * @return quantity
  **/
  @Schema(description = "The quantity of this line item. Although this is an array, only 1 entry is allowed.")
  public List<QuantityCommonData> getQuantity() {
    return quantity;
  }

  public void setQuantity(List<QuantityCommonData> quantity) {
    this.quantity = quantity;
  }

  public CustomerOrderForDeliveryBase retailPriceModifier(List<RetailPriceModifierDomainSpecific> retailPriceModifier) {
    this.retailPriceModifier = retailPriceModifier;
    return this;
  }

  public CustomerOrderForDeliveryBase addRetailPriceModifierItem(RetailPriceModifierDomainSpecific retailPriceModifierItem) {
    if (this.retailPriceModifier == null) {
      this.retailPriceModifier = new ArrayList<RetailPriceModifierDomainSpecific>();
    }
    this.retailPriceModifier.add(retailPriceModifierItem);
    return this;
  }

   /**
   * The list of modifiers of the sales price triggered by applied price derivation rules.
   * @return retailPriceModifier
  **/
  @Schema(description = "The list of modifiers of the sales price triggered by applied price derivation rules.")
  public List<RetailPriceModifierDomainSpecific> getRetailPriceModifier() {
    return retailPriceModifier;
  }

  public void setRetailPriceModifier(List<RetailPriceModifierDomainSpecific> retailPriceModifier) {
    this.retailPriceModifier = retailPriceModifier;
  }

  public CustomerOrderForDeliveryBase itemLink(List<ItemLink> itemLink) {
    this.itemLink = itemLink;
    return this;
  }

  public CustomerOrderForDeliveryBase addItemLinkItem(ItemLink itemLinkItem) {
    if (this.itemLink == null) {
      this.itemLink = new ArrayList<ItemLink>();
    }
    this.itemLink.add(itemLinkItem);
    return this;
  }

   /**
   * Reserved for future use.
   * @return itemLink
  **/
  @Schema(description = "Reserved for future use.")
  public List<ItemLink> getItemLink() {
    return itemLink;
  }

  public void setItemLink(List<ItemLink> itemLink) {
    this.itemLink = itemLink;
  }

  public CustomerOrderForDeliveryBase itemType(ItemTypeEnum itemType) {
    this.itemType = itemType;
    return this;
  }

   /**
   * Reserved for future use.
   * @return itemType
  **/
  @Schema(description = "Reserved for future use.")
  public ItemTypeEnum getItemType() {
    return itemType;
  }

  public void setItemType(ItemTypeEnum itemType) {
    this.itemType = itemType;
  }

  public CustomerOrderForDeliveryBase nonDiscountableFlag(Boolean nonDiscountableFlag) {
    this.nonDiscountableFlag = nonDiscountableFlag;
    return this;
  }

   /**
   * Specifies whether this line item may be discounted or not. If set to true, the line item may still be used as an eligibility for promotions. However, such an item does not contribute to fulfill the amount or quantity threshold that is needed for a promotion to become effective. 
   * @return nonDiscountableFlag
  **/
  @Schema(description = "Specifies whether this line item may be discounted or not. If set to true, the line item may still be used as an eligibility for promotions. However, such an item does not contribute to fulfill the amount or quantity threshold that is needed for a promotion to become effective. ")
  public Boolean isNonDiscountableFlag() {
    return nonDiscountableFlag;
  }

  public void setNonDiscountableFlag(Boolean nonDiscountableFlag) {
    this.nonDiscountableFlag = nonDiscountableFlag;
  }

  public CustomerOrderForDeliveryBase fixedPriceFlag(Boolean fixedPriceFlag) {
    this.fixedPriceFlag = fixedPriceFlag;
    return this;
  }

   /**
   * If set to true, the price calculation uses the provided value of RegularSalesUnitPrice as regular price and does not perform a price lookup for this line item.
   * @return fixedPriceFlag
  **/
  @Schema(description = "If set to true, the price calculation uses the provided value of RegularSalesUnitPrice as regular price and does not perform a price lookup for this line item.")
  public Boolean isFixedPriceFlag() {
    return fixedPriceFlag;
  }

  public void setFixedPriceFlag(Boolean fixedPriceFlag) {
    this.fixedPriceFlag = fixedPriceFlag;
  }

  public CustomerOrderForDeliveryBase taxIncludedInPriceFlag(Boolean taxIncludedInPriceFlag) {
    this.taxIncludedInPriceFlag = taxIncludedInPriceFlag;
    return this;
  }

   /**
   * Reserved for future use.
   * @return taxIncludedInPriceFlag
  **/
  @Schema(description = "Reserved for future use.")
  public Boolean isTaxIncludedInPriceFlag() {
    return taxIncludedInPriceFlag;
  }

  public void setTaxIncludedInPriceFlag(Boolean taxIncludedInPriceFlag) {
    this.taxIncludedInPriceFlag = taxIncludedInPriceFlag;
  }

  public CustomerOrderForDeliveryBase nonPieceGoodFlag(Boolean nonPieceGoodFlag) {
    this.nonPieceGoodFlag = nonPieceGoodFlag;
    return this;
  }

   /**
   * Set this flag to true, if the specified quantity has a different dimension than \&quot;piece\&quot;, for example, if fractional quantities are possible.
   * @return nonPieceGoodFlag
  **/
  @Schema(description = "Set this flag to true, if the specified quantity has a different dimension than \"piece\", for example, if fractional quantities are possible.")
  public Boolean isNonPieceGoodFlag() {
    return nonPieceGoodFlag;
  }

  public void setNonPieceGoodFlag(Boolean nonPieceGoodFlag) {
    this.nonPieceGoodFlag = nonPieceGoodFlag;
  }

  public CustomerOrderForDeliveryBase frequentShopperPointsEligibilityFlag(Boolean frequentShopperPointsEligibilityFlag) {
    this.frequentShopperPointsEligibilityFlag = frequentShopperPointsEligibilityFlag;
    return this;
  }

   /**
   * Set this flag to true if the corresponding line item is eligible for receiving loyalty points.
   * @return frequentShopperPointsEligibilityFlag
  **/
  @Schema(description = "Set this flag to true if the corresponding line item is eligible for receiving loyalty points.")
  public Boolean isFrequentShopperPointsEligibilityFlag() {
    return frequentShopperPointsEligibilityFlag;
  }

  public void setFrequentShopperPointsEligibilityFlag(Boolean frequentShopperPointsEligibilityFlag) {
    this.frequentShopperPointsEligibilityFlag = frequentShopperPointsEligibilityFlag;
  }

  public CustomerOrderForDeliveryBase discountTypeCode(String discountTypeCode) {
    this.discountTypeCode = discountTypeCode;
    return this;
  }

   /**
   * Reserved for future use.
   * @return discountTypeCode
  **/
  @Schema(description = "Reserved for future use.")
  public String getDiscountTypeCode() {
    return discountTypeCode;
  }

  public void setDiscountTypeCode(String discountTypeCode) {
    this.discountTypeCode = discountTypeCode;
  }

  public CustomerOrderForDeliveryBase priceTypeCode(PriceTypeCodeEnum priceTypeCode) {
    this.priceTypeCode = priceTypeCode;
    return this;
  }

   /**
   * Reserved for future use. Value 00 corresponds to \&quot;regular price\&quot;.
   * @return priceTypeCode
  **/
  @Schema(description = "Reserved for future use. Value 00 corresponds to \"regular price\".")
  public PriceTypeCodeEnum getPriceTypeCode() {
    return priceTypeCode;
  }

  public void setPriceTypeCode(PriceTypeCodeEnum priceTypeCode) {
    this.priceTypeCode = priceTypeCode;
  }

  public CustomerOrderForDeliveryBase notConsideredByPriceEngineFlag(Boolean notConsideredByPriceEngineFlag) {
    this.notConsideredByPriceEngineFlag = notConsideredByPriceEngineFlag;
    return this;
  }

   /**
   * Determines if the price calculation should consider the line item as a trigger. This information is needed if another process (e.g. a return regarding to an existing transaction) applies rules for the line item that should not be applied. Assuming that the line item is discountable in general, the line item does not trigger a line-item-releated price derivation rule, get discount or get bonus points, if you set this flag to true. However, it can do so for a transaction-related price derivation rule. 
   * @return notConsideredByPriceEngineFlag
  **/
  @Schema(description = "Determines if the price calculation should consider the line item as a trigger. This information is needed if another process (e.g. a return regarding to an existing transaction) applies rules for the line item that should not be applied. Assuming that the line item is discountable in general, the line item does not trigger a line-item-releated price derivation rule, get discount or get bonus points, if you set this flag to true. However, it can do so for a transaction-related price derivation rule. ")
  public Boolean isNotConsideredByPriceEngineFlag() {
    return notConsideredByPriceEngineFlag;
  }

  public void setNotConsideredByPriceEngineFlag(Boolean notConsideredByPriceEngineFlag) {
    this.notConsideredByPriceEngineFlag = notConsideredByPriceEngineFlag;
  }

  public CustomerOrderForDeliveryBase frequentShopperPointsModifier(List<FrequentShopperPointsModifierType> frequentShopperPointsModifier) {
    this.frequentShopperPointsModifier = frequentShopperPointsModifier;
    return this;
  }

  public CustomerOrderForDeliveryBase addFrequentShopperPointsModifierItem(FrequentShopperPointsModifierType frequentShopperPointsModifierItem) {
    if (this.frequentShopperPointsModifier == null) {
      this.frequentShopperPointsModifier = new ArrayList<FrequentShopperPointsModifierType>();
    }
    this.frequentShopperPointsModifier.add(frequentShopperPointsModifierItem);
    return this;
  }

   /**
   * The list of modifiers for loyalty points determined by applied price rules.
   * @return frequentShopperPointsModifier
  **/
  @Schema(description = "The list of modifiers for loyalty points determined by applied price rules.")
  public List<FrequentShopperPointsModifierType> getFrequentShopperPointsModifier() {
    return frequentShopperPointsModifier;
  }

  public void setFrequentShopperPointsModifier(List<FrequentShopperPointsModifierType> frequentShopperPointsModifier) {
    this.frequentShopperPointsModifier = frequentShopperPointsModifier;
  }

  public CustomerOrderForDeliveryBase promotionPriceDerivationRuleReference(List<PromotionPriceDerivationRuleReferenceType> promotionPriceDerivationRuleReference) {
    this.promotionPriceDerivationRuleReference = promotionPriceDerivationRuleReference;
    return this;
  }

  public CustomerOrderForDeliveryBase addPromotionPriceDerivationRuleReferenceItem(PromotionPriceDerivationRuleReferenceType promotionPriceDerivationRuleReferenceItem) {
    if (this.promotionPriceDerivationRuleReference == null) {
      this.promotionPriceDerivationRuleReference = new ArrayList<PromotionPriceDerivationRuleReferenceType>();
    }
    this.promotionPriceDerivationRuleReference.add(promotionPriceDerivationRuleReferenceItem);
    return this;
  }

   /**
   * The list of promotions for which the corresponding line item contributed as an eligibility.
   * @return promotionPriceDerivationRuleReference
  **/
  @Schema(description = "The list of promotions for which the corresponding line item contributed as an eligibility.")
  public List<PromotionPriceDerivationRuleReferenceType> getPromotionPriceDerivationRuleReference() {
    return promotionPriceDerivationRuleReference;
  }

  public void setPromotionPriceDerivationRuleReference(List<PromotionPriceDerivationRuleReferenceType> promotionPriceDerivationRuleReference) {
    this.promotionPriceDerivationRuleReference = promotionPriceDerivationRuleReference;
  }

  public CustomerOrderForDeliveryBase promotionManualTrigger(List<PromotionManualTriggerType> promotionManualTrigger) {
    this.promotionManualTrigger = promotionManualTrigger;
    return this;
  }

  public CustomerOrderForDeliveryBase addPromotionManualTriggerItem(PromotionManualTriggerType promotionManualTriggerItem) {
    if (this.promotionManualTrigger == null) {
      this.promotionManualTrigger = new ArrayList<PromotionManualTriggerType>();
    }
    this.promotionManualTrigger.add(promotionManualTriggerItem);
    return this;
  }

   /**
   * Manual triggers created by the client in order to make the transaction eligible for promotions that contain eligibilities of type manual trigger.
   * @return promotionManualTrigger
  **/
  @Schema(description = "Manual triggers created by the client in order to make the transaction eligible for promotions that contain eligibilities of type manual trigger.")
  public List<PromotionManualTriggerType> getPromotionManualTrigger() {
    return promotionManualTrigger;
  }

  public void setPromotionManualTrigger(List<PromotionManualTriggerType> promotionManualTrigger) {
    this.promotionManualTrigger = promotionManualTrigger;
  }

  public CustomerOrderForDeliveryBase lineItemAttribute(List<LineItemAttributeType> lineItemAttribute) {
    this.lineItemAttribute = lineItemAttribute;
    return this;
  }

  public CustomerOrderForDeliveryBase addLineItemAttributeItem(LineItemAttributeType lineItemAttributeItem) {
    if (this.lineItemAttribute == null) {
      this.lineItemAttribute = new ArrayList<LineItemAttributeType>();
    }
    this.lineItemAttribute.add(lineItemAttributeItem);
    return this;
  }

   /**
   * Attribute Value pairs on line item level. This information is  taken for checking eligibilities on item level.
   * @return lineItemAttribute
  **/
  @Schema(description = "Attribute Value pairs on line item level. This information is  taken for checking eligibilities on item level.")
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
    CustomerOrderForDeliveryBase customerOrderForDeliveryBase = (CustomerOrderForDeliveryBase) o;
    return Objects.equals(this.any, customerOrderForDeliveryBase.any) &&
        Objects.equals(this.itemID, customerOrderForDeliveryBase.itemID) &&
        Objects.equals(this.merchandiseHierarchy, customerOrderForDeliveryBase.merchandiseHierarchy) &&
        Objects.equals(this.regularSalesUnitPrice, customerOrderForDeliveryBase.regularSalesUnitPrice) &&
        Objects.equals(this.extendedAmount, customerOrderForDeliveryBase.extendedAmount) &&
        Objects.equals(this.extendedDiscountAmount, customerOrderForDeliveryBase.extendedDiscountAmount) &&
        Objects.equals(this.quantity, customerOrderForDeliveryBase.quantity) &&
        Objects.equals(this.retailPriceModifier, customerOrderForDeliveryBase.retailPriceModifier) &&
        Objects.equals(this.itemLink, customerOrderForDeliveryBase.itemLink) &&
        Objects.equals(this.itemType, customerOrderForDeliveryBase.itemType) &&
        Objects.equals(this.nonDiscountableFlag, customerOrderForDeliveryBase.nonDiscountableFlag) &&
        Objects.equals(this.fixedPriceFlag, customerOrderForDeliveryBase.fixedPriceFlag) &&
        Objects.equals(this.taxIncludedInPriceFlag, customerOrderForDeliveryBase.taxIncludedInPriceFlag) &&
        Objects.equals(this.nonPieceGoodFlag, customerOrderForDeliveryBase.nonPieceGoodFlag) &&
        Objects.equals(this.frequentShopperPointsEligibilityFlag, customerOrderForDeliveryBase.frequentShopperPointsEligibilityFlag) &&
        Objects.equals(this.discountTypeCode, customerOrderForDeliveryBase.discountTypeCode) &&
        Objects.equals(this.priceTypeCode, customerOrderForDeliveryBase.priceTypeCode) &&
        Objects.equals(this.notConsideredByPriceEngineFlag, customerOrderForDeliveryBase.notConsideredByPriceEngineFlag) &&
        Objects.equals(this.frequentShopperPointsModifier, customerOrderForDeliveryBase.frequentShopperPointsModifier) &&
        Objects.equals(this.promotionPriceDerivationRuleReference, customerOrderForDeliveryBase.promotionPriceDerivationRuleReference) &&
        Objects.equals(this.promotionManualTrigger, customerOrderForDeliveryBase.promotionManualTrigger) &&
        Objects.equals(this.lineItemAttribute, customerOrderForDeliveryBase.lineItemAttribute);
  }

  @Override
  public int hashCode() {
    return Objects.hash(any, itemID, merchandiseHierarchy, regularSalesUnitPrice, extendedAmount, extendedDiscountAmount, quantity, retailPriceModifier, itemLink, itemType, nonDiscountableFlag, fixedPriceFlag, taxIncludedInPriceFlag, nonPieceGoodFlag, frequentShopperPointsEligibilityFlag, discountTypeCode, priceTypeCode, notConsideredByPriceEngineFlag, frequentShopperPointsModifier, promotionPriceDerivationRuleReference, promotionManualTrigger, lineItemAttribute);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CustomerOrderForDeliveryBase {\n");
    
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
