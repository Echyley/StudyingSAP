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
 * Depending on the type of the line item, one of the contained elements like Sales, SaleForDelivery must be filled.
 */
@Schema(description = "Depending on the type of the line item, one of the contained elements like Sales, SaleForDelivery must be filled.")
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2022-09-26T19:16:22.658615400+05:30[Asia/Calcutta]")
public class LineItemDomainSpecific {
  @JsonProperty("any")
  private Object any = null;

  @JsonProperty("Sale")
  private SaleBase sale = null;

  @JsonProperty("SaleForDelivery")
  private SaleForDeliveryBase saleForDelivery = null;

  @JsonProperty("SaleForPickup")
  private SaleForPickupBase saleForPickup = null;

  @JsonProperty("Return")
  private ReturnBase _return = null;

  @JsonProperty("ReturnForDelivery")
  private ReturnForDeliveryBase returnForDelivery = null;

  @JsonProperty("ReturnForPickup")
  private ReturnForPickupBase returnForPickup = null;

  @JsonProperty("CustomerOrderForDelivery")
  private CustomerOrderForDeliveryBase customerOrderForDelivery = null;

  @JsonProperty("CustomerOrderForPickup")
  private CustomerOrderForPickupBase customerOrderForPickup = null;

  @JsonProperty("Discount")
  private DiscountBase discount = null;

  @JsonProperty("LoyaltyReward")
  private LoyaltyRewardBase loyaltyReward = null;

  @JsonProperty("Coupon")
  private TenderCouponBase coupon = null;

  @JsonProperty("PromotionManualTrigger")
  private PromotionManualTriggerType promotionManualTrigger = null;

  @JsonProperty("PromotionExternalTrigger")
  private PromotionExternalTriggerType promotionExternalTrigger = null;

  @JsonProperty("SequenceNumber")
  private List<Integer> sequenceNumber = null;

  @JsonProperty("MerchandiseHierarchy")
  private List<MerchandiseHierarchyCommonData> merchandiseHierarchy = null;

  @JsonProperty("AdditionalBonusLineItem")
  private AdditionalBonusType additionalBonusLineItem = null;

  @JsonProperty("AdditionalBonusDiscountLineItem")
  private AdditionalBonusDiscountType additionalBonusDiscountLineItem = null;

  public LineItemDomainSpecific any(Object any) {
    this.any = any;
    return this;
  }

   /**
   * This is currently not supported
   * @return any
  **/
  @Schema(description = "This is currently not supported")
  public Object getAny() {
    return any;
  }

  public void setAny(Object any) {
    this.any = any;
  }

  public LineItemDomainSpecific sale(SaleBase sale) {
    this.sale = sale;
    return this;
  }

   /**
   * Get sale
   * @return sale
  **/
  @Schema(description = "")
  public SaleBase getSale() {
    return sale;
  }

  public void setSale(SaleBase sale) {
    this.sale = sale;
  }

  public LineItemDomainSpecific saleForDelivery(SaleForDeliveryBase saleForDelivery) {
    this.saleForDelivery = saleForDelivery;
    return this;
  }

   /**
   * Get saleForDelivery
   * @return saleForDelivery
  **/
  @Schema(description = "")
  public SaleForDeliveryBase getSaleForDelivery() {
    return saleForDelivery;
  }

  public void setSaleForDelivery(SaleForDeliveryBase saleForDelivery) {
    this.saleForDelivery = saleForDelivery;
  }

  public LineItemDomainSpecific saleForPickup(SaleForPickupBase saleForPickup) {
    this.saleForPickup = saleForPickup;
    return this;
  }

   /**
   * Get saleForPickup
   * @return saleForPickup
  **/
  @Schema(description = "")
  public SaleForPickupBase getSaleForPickup() {
    return saleForPickup;
  }

  public void setSaleForPickup(SaleForPickupBase saleForPickup) {
    this.saleForPickup = saleForPickup;
  }

  public LineItemDomainSpecific _return(ReturnBase _return) {
    this._return = _return;
    return this;
  }

   /**
   * Get _return
   * @return _return
  **/
  @Schema(description = "")
  public ReturnBase getReturn() {
    return _return;
  }

  public void setReturn(ReturnBase _return) {
    this._return = _return;
  }

  public LineItemDomainSpecific returnForDelivery(ReturnForDeliveryBase returnForDelivery) {
    this.returnForDelivery = returnForDelivery;
    return this;
  }

   /**
   * Get returnForDelivery
   * @return returnForDelivery
  **/
  @Schema(description = "")
  public ReturnForDeliveryBase getReturnForDelivery() {
    return returnForDelivery;
  }

  public void setReturnForDelivery(ReturnForDeliveryBase returnForDelivery) {
    this.returnForDelivery = returnForDelivery;
  }

  public LineItemDomainSpecific returnForPickup(ReturnForPickupBase returnForPickup) {
    this.returnForPickup = returnForPickup;
    return this;
  }

   /**
   * Get returnForPickup
   * @return returnForPickup
  **/
  @Schema(description = "")
  public ReturnForPickupBase getReturnForPickup() {
    return returnForPickup;
  }

  public void setReturnForPickup(ReturnForPickupBase returnForPickup) {
    this.returnForPickup = returnForPickup;
  }

  public LineItemDomainSpecific customerOrderForDelivery(CustomerOrderForDeliveryBase customerOrderForDelivery) {
    this.customerOrderForDelivery = customerOrderForDelivery;
    return this;
  }

   /**
   * Get customerOrderForDelivery
   * @return customerOrderForDelivery
  **/
  @Schema(description = "")
  public CustomerOrderForDeliveryBase getCustomerOrderForDelivery() {
    return customerOrderForDelivery;
  }

  public void setCustomerOrderForDelivery(CustomerOrderForDeliveryBase customerOrderForDelivery) {
    this.customerOrderForDelivery = customerOrderForDelivery;
  }

  public LineItemDomainSpecific customerOrderForPickup(CustomerOrderForPickupBase customerOrderForPickup) {
    this.customerOrderForPickup = customerOrderForPickup;
    return this;
  }

   /**
   * Get customerOrderForPickup
   * @return customerOrderForPickup
  **/
  @Schema(description = "")
  public CustomerOrderForPickupBase getCustomerOrderForPickup() {
    return customerOrderForPickup;
  }

  public void setCustomerOrderForPickup(CustomerOrderForPickupBase customerOrderForPickup) {
    this.customerOrderForPickup = customerOrderForPickup;
  }

  public LineItemDomainSpecific discount(DiscountBase discount) {
    this.discount = discount;
    return this;
  }

   /**
   * Get discount
   * @return discount
  **/
  @Schema(description = "")
  public DiscountBase getDiscount() {
    return discount;
  }

  public void setDiscount(DiscountBase discount) {
    this.discount = discount;
  }

  public LineItemDomainSpecific loyaltyReward(LoyaltyRewardBase loyaltyReward) {
    this.loyaltyReward = loyaltyReward;
    return this;
  }

   /**
   * Get loyaltyReward
   * @return loyaltyReward
  **/
  @Schema(description = "")
  public LoyaltyRewardBase getLoyaltyReward() {
    return loyaltyReward;
  }

  public void setLoyaltyReward(LoyaltyRewardBase loyaltyReward) {
    this.loyaltyReward = loyaltyReward;
  }

  public LineItemDomainSpecific coupon(TenderCouponBase coupon) {
    this.coupon = coupon;
    return this;
  }

   /**
   * Get coupon
   * @return coupon
  **/
  @Schema(description = "")
  public TenderCouponBase getCoupon() {
    return coupon;
  }

  public void setCoupon(TenderCouponBase coupon) {
    this.coupon = coupon;
  }

  public LineItemDomainSpecific promotionManualTrigger(PromotionManualTriggerType promotionManualTrigger) {
    this.promotionManualTrigger = promotionManualTrigger;
    return this;
  }

   /**
   * Get promotionManualTrigger
   * @return promotionManualTrigger
  **/
  @Schema(description = "")
  public PromotionManualTriggerType getPromotionManualTrigger() {
    return promotionManualTrigger;
  }

  public void setPromotionManualTrigger(PromotionManualTriggerType promotionManualTrigger) {
    this.promotionManualTrigger = promotionManualTrigger;
  }

  public LineItemDomainSpecific promotionExternalTrigger(PromotionExternalTriggerType promotionExternalTrigger) {
    this.promotionExternalTrigger = promotionExternalTrigger;
    return this;
  }

   /**
   * Get promotionExternalTrigger
   * @return promotionExternalTrigger
  **/
  @Schema(description = "")
  public PromotionExternalTriggerType getPromotionExternalTrigger() {
    return promotionExternalTrigger;
  }

  public void setPromotionExternalTrigger(PromotionExternalTriggerType promotionExternalTrigger) {
    this.promotionExternalTrigger = promotionExternalTrigger;
  }

  public LineItemDomainSpecific sequenceNumber(List<Integer> sequenceNumber) {
    this.sequenceNumber = sequenceNumber;
    return this;
  }

  public LineItemDomainSpecific addSequenceNumberItem(Integer sequenceNumberItem) {
    if (this.sequenceNumber == null) {
      this.sequenceNumber = new ArrayList<Integer>();
    }
    this.sequenceNumber.add(sequenceNumberItem);
    return this;
  }

   /**
   * Identifies the line item. Although this is an array, exactly 1 entry is required.
   * @return sequenceNumber
  **/
  @Schema(description = "Identifies the line item. Although this is an array, exactly 1 entry is required.")
  public List<Integer> getSequenceNumber() {
    return sequenceNumber;
  }

  public void setSequenceNumber(List<Integer> sequenceNumber) {
    this.sequenceNumber = sequenceNumber;
  }

  public LineItemDomainSpecific merchandiseHierarchy(List<MerchandiseHierarchyCommonData> merchandiseHierarchy) {
    this.merchandiseHierarchy = merchandiseHierarchy;
    return this;
  }

  public LineItemDomainSpecific addMerchandiseHierarchyItem(MerchandiseHierarchyCommonData merchandiseHierarchyItem) {
    if (this.merchandiseHierarchy == null) {
      this.merchandiseHierarchy = new ArrayList<MerchandiseHierarchyCommonData>();
    }
    this.merchandiseHierarchy.add(merchandiseHierarchyItem);
    return this;
  }

   /**
   * This information is taken for checking eligibilities on item hierarchy level.
   * @return merchandiseHierarchy
  **/
  @Schema(description = "This information is taken for checking eligibilities on item hierarchy level.")
  public List<MerchandiseHierarchyCommonData> getMerchandiseHierarchy() {
    return merchandiseHierarchy;
  }

  public void setMerchandiseHierarchy(List<MerchandiseHierarchyCommonData> merchandiseHierarchy) {
    this.merchandiseHierarchy = merchandiseHierarchy;
  }

  public LineItemDomainSpecific additionalBonusLineItem(AdditionalBonusType additionalBonusLineItem) {
    this.additionalBonusLineItem = additionalBonusLineItem;
    return this;
  }

   /**
   * Get additionalBonusLineItem
   * @return additionalBonusLineItem
  **/
  @Schema(description = "")
  public AdditionalBonusType getAdditionalBonusLineItem() {
    return additionalBonusLineItem;
  }

  public void setAdditionalBonusLineItem(AdditionalBonusType additionalBonusLineItem) {
    this.additionalBonusLineItem = additionalBonusLineItem;
  }

  public LineItemDomainSpecific additionalBonusDiscountLineItem(AdditionalBonusDiscountType additionalBonusDiscountLineItem) {
    this.additionalBonusDiscountLineItem = additionalBonusDiscountLineItem;
    return this;
  }

   /**
   * Get additionalBonusDiscountLineItem
   * @return additionalBonusDiscountLineItem
  **/
  @Schema(description = "")
  public AdditionalBonusDiscountType getAdditionalBonusDiscountLineItem() {
    return additionalBonusDiscountLineItem;
  }

  public void setAdditionalBonusDiscountLineItem(AdditionalBonusDiscountType additionalBonusDiscountLineItem) {
    this.additionalBonusDiscountLineItem = additionalBonusDiscountLineItem;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LineItemDomainSpecific lineItemDomainSpecific = (LineItemDomainSpecific) o;
    return Objects.equals(this.any, lineItemDomainSpecific.any) &&
        Objects.equals(this.sale, lineItemDomainSpecific.sale) &&
        Objects.equals(this.saleForDelivery, lineItemDomainSpecific.saleForDelivery) &&
        Objects.equals(this.saleForPickup, lineItemDomainSpecific.saleForPickup) &&
        Objects.equals(this._return, lineItemDomainSpecific._return) &&
        Objects.equals(this.returnForDelivery, lineItemDomainSpecific.returnForDelivery) &&
        Objects.equals(this.returnForPickup, lineItemDomainSpecific.returnForPickup) &&
        Objects.equals(this.customerOrderForDelivery, lineItemDomainSpecific.customerOrderForDelivery) &&
        Objects.equals(this.customerOrderForPickup, lineItemDomainSpecific.customerOrderForPickup) &&
        Objects.equals(this.discount, lineItemDomainSpecific.discount) &&
        Objects.equals(this.loyaltyReward, lineItemDomainSpecific.loyaltyReward) &&
        Objects.equals(this.coupon, lineItemDomainSpecific.coupon) &&
        Objects.equals(this.promotionManualTrigger, lineItemDomainSpecific.promotionManualTrigger) &&
        Objects.equals(this.promotionExternalTrigger, lineItemDomainSpecific.promotionExternalTrigger) &&
        Objects.equals(this.sequenceNumber, lineItemDomainSpecific.sequenceNumber) &&
        Objects.equals(this.merchandiseHierarchy, lineItemDomainSpecific.merchandiseHierarchy) &&
        Objects.equals(this.additionalBonusLineItem, lineItemDomainSpecific.additionalBonusLineItem) &&
        Objects.equals(this.additionalBonusDiscountLineItem, lineItemDomainSpecific.additionalBonusDiscountLineItem);
  }

  @Override
  public int hashCode() {
    return Objects.hash(any, sale, saleForDelivery, saleForPickup, _return, returnForDelivery, returnForPickup, customerOrderForDelivery, customerOrderForPickup, discount, loyaltyReward, coupon, promotionManualTrigger, promotionExternalTrigger, sequenceNumber, merchandiseHierarchy, additionalBonusLineItem, additionalBonusDiscountLineItem);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LineItemDomainSpecific {\n");
    
    sb.append("    any: ").append(toIndentedString(any)).append("\n");
    sb.append("    sale: ").append(toIndentedString(sale)).append("\n");
    sb.append("    saleForDelivery: ").append(toIndentedString(saleForDelivery)).append("\n");
    sb.append("    saleForPickup: ").append(toIndentedString(saleForPickup)).append("\n");
    sb.append("    _return: ").append(toIndentedString(_return)).append("\n");
    sb.append("    returnForDelivery: ").append(toIndentedString(returnForDelivery)).append("\n");
    sb.append("    returnForPickup: ").append(toIndentedString(returnForPickup)).append("\n");
    sb.append("    customerOrderForDelivery: ").append(toIndentedString(customerOrderForDelivery)).append("\n");
    sb.append("    customerOrderForPickup: ").append(toIndentedString(customerOrderForPickup)).append("\n");
    sb.append("    discount: ").append(toIndentedString(discount)).append("\n");
    sb.append("    loyaltyReward: ").append(toIndentedString(loyaltyReward)).append("\n");
    sb.append("    coupon: ").append(toIndentedString(coupon)).append("\n");
    sb.append("    promotionManualTrigger: ").append(toIndentedString(promotionManualTrigger)).append("\n");
    sb.append("    promotionExternalTrigger: ").append(toIndentedString(promotionExternalTrigger)).append("\n");
    sb.append("    sequenceNumber: ").append(toIndentedString(sequenceNumber)).append("\n");
    sb.append("    merchandiseHierarchy: ").append(toIndentedString(merchandiseHierarchy)).append("\n");
    sb.append("    additionalBonusLineItem: ").append(toIndentedString(additionalBonusLineItem)).append("\n");
    sb.append("    additionalBonusDiscountLineItem: ").append(toIndentedString(additionalBonusDiscountLineItem)).append("\n");
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
