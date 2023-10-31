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
 * A flavor of a line item whereby customers have a product for free in the  basket. This line item is created by the calculation service and used in the response. It indicates which products may be added to the shopping cart. Supported starting  with client API version 5.0 of the calculation request. 
 */
@Schema(description = "A flavor of a line item whereby customers have a product for free in the  basket. This line item is created by the calculation service and used in the response. It indicates which products may be added to the shopping cart. Supported starting  with client API version 5.0 of the calculation request. ")
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2022-09-26T19:16:22.658615400+05:30[Asia/Calcutta]")
public class AdditionalBonusDiscountType {
  @JsonProperty("any")
  private List<Object> any = null;

  @JsonProperty("AdditionalBonusID")
  private String additionalBonusID = null;

  @JsonProperty("ItemID")
  private ItemID itemID = null;

  @JsonProperty("TotalGrantedQuantity")
  private QuantityCommonData totalGrantedQuantity = null;

  @JsonProperty("QuantityDifference")
  private QuantityDifference quantityDifference = null;

  @JsonProperty("MerchandiseSet")
  private MerchSetType merchandiseSet = null;

  @JsonProperty("PriceDerivationRule")
  private PriceDerivationRuleBase priceDerivationRule = null;

  public AdditionalBonusDiscountType any(List<Object> any) {
    this.any = any;
    return this;
  }

  public AdditionalBonusDiscountType addAnyItem(Object anyItem) {
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

  public AdditionalBonusDiscountType additionalBonusID(String additionalBonusID) {
    this.additionalBonusID = additionalBonusID;
    return this;
  }

   /**
   * Get additionalBonusID
   * @return additionalBonusID
  **/
  @Schema(description = "")
  public String getAdditionalBonusID() {
    return additionalBonusID;
  }

  public void setAdditionalBonusID(String additionalBonusID) {
    this.additionalBonusID = additionalBonusID;
  }

  public AdditionalBonusDiscountType itemID(ItemID itemID) {
    this.itemID = itemID;
    return this;
  }

   /**
   * Get itemID
   * @return itemID
  **/
  @Schema(description = "")
  public ItemID getItemID() {
    return itemID;
  }

  public void setItemID(ItemID itemID) {
    this.itemID = itemID;
  }

  public AdditionalBonusDiscountType totalGrantedQuantity(QuantityCommonData totalGrantedQuantity) {
    this.totalGrantedQuantity = totalGrantedQuantity;
    return this;
  }

   /**
   * Get totalGrantedQuantity
   * @return totalGrantedQuantity
  **/
  @Schema(description = "")
  public QuantityCommonData getTotalGrantedQuantity() {
    return totalGrantedQuantity;
  }

  public void setTotalGrantedQuantity(QuantityCommonData totalGrantedQuantity) {
    this.totalGrantedQuantity = totalGrantedQuantity;
  }

  public AdditionalBonusDiscountType quantityDifference(QuantityDifference quantityDifference) {
    this.quantityDifference = quantityDifference;
    return this;
  }

   /**
   * Get quantityDifference
   * @return quantityDifference
  **/
  @Schema(description = "")
  public QuantityDifference getQuantityDifference() {
    return quantityDifference;
  }

  public void setQuantityDifference(QuantityDifference quantityDifference) {
    this.quantityDifference = quantityDifference;
  }

  public AdditionalBonusDiscountType merchandiseSet(MerchSetType merchandiseSet) {
    this.merchandiseSet = merchandiseSet;
    return this;
  }

   /**
   * Get merchandiseSet
   * @return merchandiseSet
  **/
  @Schema(description = "")
  public MerchSetType getMerchandiseSet() {
    return merchandiseSet;
  }

  public void setMerchandiseSet(MerchSetType merchandiseSet) {
    this.merchandiseSet = merchandiseSet;
  }

  public AdditionalBonusDiscountType priceDerivationRule(PriceDerivationRuleBase priceDerivationRule) {
    this.priceDerivationRule = priceDerivationRule;
    return this;
  }

   /**
   * Get priceDerivationRule
   * @return priceDerivationRule
  **/
  @Schema(description = "")
  public PriceDerivationRuleBase getPriceDerivationRule() {
    return priceDerivationRule;
  }

  public void setPriceDerivationRule(PriceDerivationRuleBase priceDerivationRule) {
    this.priceDerivationRule = priceDerivationRule;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AdditionalBonusDiscountType additionalBonusDiscountType = (AdditionalBonusDiscountType) o;
    return Objects.equals(this.any, additionalBonusDiscountType.any) &&
        Objects.equals(this.additionalBonusID, additionalBonusDiscountType.additionalBonusID) &&
        Objects.equals(this.itemID, additionalBonusDiscountType.itemID) &&
        Objects.equals(this.totalGrantedQuantity, additionalBonusDiscountType.totalGrantedQuantity) &&
        Objects.equals(this.quantityDifference, additionalBonusDiscountType.quantityDifference) &&
        Objects.equals(this.merchandiseSet, additionalBonusDiscountType.merchandiseSet) &&
        Objects.equals(this.priceDerivationRule, additionalBonusDiscountType.priceDerivationRule);
  }

  @Override
  public int hashCode() {
    return Objects.hash(any, additionalBonusID, itemID, totalGrantedQuantity, quantityDifference, merchandiseSet, priceDerivationRule);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AdditionalBonusDiscountType {\n");
    
    sb.append("    any: ").append(toIndentedString(any)).append("\n");
    sb.append("    additionalBonusID: ").append(toIndentedString(additionalBonusID)).append("\n");
    sb.append("    itemID: ").append(toIndentedString(itemID)).append("\n");
    sb.append("    totalGrantedQuantity: ").append(toIndentedString(totalGrantedQuantity)).append("\n");
    sb.append("    quantityDifference: ").append(toIndentedString(quantityDifference)).append("\n");
    sb.append("    merchandiseSet: ").append(toIndentedString(merchandiseSet)).append("\n");
    sb.append("    priceDerivationRule: ").append(toIndentedString(priceDerivationRule)).append("\n");
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
