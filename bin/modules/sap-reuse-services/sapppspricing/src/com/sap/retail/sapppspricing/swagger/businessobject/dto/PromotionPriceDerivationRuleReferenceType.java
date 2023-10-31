/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.sapppspricing.swagger.businessobject.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/**
 * Specifies the price derivation rule to which a line item contributed to.
 */
@Schema(description = "Specifies the price derivation rule to which a line item contributed to.")
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2022-09-26T19:16:22.658615400+05:30[Asia/Calcutta]")
public class PromotionPriceDerivationRuleReferenceType {
  @JsonProperty("PromotionID")
  private String promotionID = null;

  @JsonProperty("PriceDerivationRuleID")
  private String priceDerivationRuleID = null;

  @JsonProperty("ReferenceQuantity")
  private BigDecimal referenceQuantity = null;

  @JsonProperty("any")
  private List<Object> any = null;

  public PromotionPriceDerivationRuleReferenceType promotionID(String promotionID) {
    this.promotionID = promotionID;
    return this;
  }

   /**
   * Identifies a promotion. Positive number.
   * @return promotionID
  **/
  @Schema(required = true, description = "Identifies a promotion. Positive number.")
  public String getPromotionID() {
    return promotionID;
  }

  public void setPromotionID(String promotionID) {
    this.promotionID = promotionID;
  }

  public PromotionPriceDerivationRuleReferenceType priceDerivationRuleID(String priceDerivationRuleID) {
    this.priceDerivationRuleID = priceDerivationRuleID;
    return this;
  }

   /**
   * Identifies a price derivation rule. Decimal representation of a 64 bit integer value.
   * @return priceDerivationRuleID
  **/
  @Schema(required = true, description = "Identifies a price derivation rule. Decimal representation of a 64 bit integer value.")
  public String getPriceDerivationRuleID() {
    return priceDerivationRuleID;
  }

  public void setPriceDerivationRuleID(String priceDerivationRuleID) {
    this.priceDerivationRuleID = priceDerivationRuleID;
  }

  public PromotionPriceDerivationRuleReferenceType referenceQuantity(BigDecimal referenceQuantity) {
    this.referenceQuantity = referenceQuantity;
    return this;
  }

   /**
   * The quantity relevant for the applied price derivation rule.
   * minimum: 0
   * @return referenceQuantity
  **/
  @Schema(required = true, description = "The quantity relevant for the applied price derivation rule.")
  public BigDecimal getReferenceQuantity() {
    return referenceQuantity;
  }

  public void setReferenceQuantity(BigDecimal referenceQuantity) {
    this.referenceQuantity = referenceQuantity;
  }

  public PromotionPriceDerivationRuleReferenceType any(List<Object> any) {
    this.any = any;
    return this;
  }

  public PromotionPriceDerivationRuleReferenceType addAnyItem(Object anyItem) {
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


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PromotionPriceDerivationRuleReferenceType promotionPriceDerivationRuleReferenceType = (PromotionPriceDerivationRuleReferenceType) o;
    return Objects.equals(this.promotionID, promotionPriceDerivationRuleReferenceType.promotionID) &&
        Objects.equals(this.priceDerivationRuleID, promotionPriceDerivationRuleReferenceType.priceDerivationRuleID) &&
        Objects.equals(this.referenceQuantity, promotionPriceDerivationRuleReferenceType.referenceQuantity) &&
        Objects.equals(this.any, promotionPriceDerivationRuleReferenceType.any);
  }

  @Override
  public int hashCode() {
    return Objects.hash(promotionID, priceDerivationRuleID, referenceQuantity, any);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PromotionPriceDerivationRuleReferenceType {\n");
    
    sb.append("    promotionID: ").append(toIndentedString(promotionID)).append("\n");
    sb.append("    priceDerivationRuleID: ").append(toIndentedString(priceDerivationRuleID)).append("\n");
    sb.append("    referenceQuantity: ").append(toIndentedString(referenceQuantity)).append("\n");
    sb.append("    any: ").append(toIndentedString(any)).append("\n");
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
