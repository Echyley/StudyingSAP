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
 * Allows the entry of coupons.
 */
@Schema(description = "Allows the entry of coupons.")
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2022-09-26T19:16:22.658615400+05:30[Asia/Calcutta]")
public class TenderCouponBase {
  @JsonProperty("Quantity")
  private QuantityCommonData quantity = null;

  @JsonProperty("PrimaryLabel")
  private String primaryLabel = null;

  @JsonProperty("RewardValue")
  private BigDecimal rewardValue = null;

  @JsonProperty("any")
  private List<Object> any = null;

  @JsonProperty("AppliedQuantity")
  private BigDecimal appliedQuantity = null;

  @JsonProperty("RewardType")
  private String rewardType = null;

  public TenderCouponBase quantity(QuantityCommonData quantity) {
    this.quantity = quantity;
    return this;
  }

   /**
   * Get quantity
   * @return quantity
  **/
  @Schema(description = "")
  public QuantityCommonData getQuantity() {
    return quantity;
  }

  public void setQuantity(QuantityCommonData quantity) {
    this.quantity = quantity;
  }

  public TenderCouponBase primaryLabel(String primaryLabel) {
    this.primaryLabel = primaryLabel;
    return this;
  }

   /**
   * Must be unique within this request.
   * @return primaryLabel
  **/
  @Schema(required = true, description = "Must be unique within this request.")
  public String getPrimaryLabel() {
    return primaryLabel;
  }

  public void setPrimaryLabel(String primaryLabel) {
    this.primaryLabel = primaryLabel;
  }

  public TenderCouponBase rewardValue(BigDecimal rewardValue) {
    this.rewardValue = rewardValue;
    return this;
  }

   /**
   * Reserved for future use.
   * @return rewardValue
  **/
  @Schema(description = "Reserved for future use.")
  public BigDecimal getRewardValue() {
    return rewardValue;
  }

  public void setRewardValue(BigDecimal rewardValue) {
    this.rewardValue = rewardValue;
  }

  public TenderCouponBase any(List<Object> any) {
    this.any = any;
    return this;
  }

  public TenderCouponBase addAnyItem(Object anyItem) {
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

  public TenderCouponBase appliedQuantity(BigDecimal appliedQuantity) {
    this.appliedQuantity = appliedQuantity;
    return this;
  }

   /**
   * Quantity of items for which the provided coupons could be applied.
   * minimum: 0
   * @return appliedQuantity
  **/
  @Schema(description = "Quantity of items for which the provided coupons could be applied.")
  public BigDecimal getAppliedQuantity() {
    return appliedQuantity;
  }

  public void setAppliedQuantity(BigDecimal appliedQuantity) {
    this.appliedQuantity = appliedQuantity;
  }

  public TenderCouponBase rewardType(String rewardType) {
    this.rewardType = rewardType;
    return this;
  }

   /**
   * Reserved for future use.
   * @return rewardType
  **/
  @Schema(description = "Reserved for future use.")
  public String getRewardType() {
    return rewardType;
  }

  public void setRewardType(String rewardType) {
    this.rewardType = rewardType;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TenderCouponBase tenderCouponBase = (TenderCouponBase) o;
    return Objects.equals(this.quantity, tenderCouponBase.quantity) &&
        Objects.equals(this.primaryLabel, tenderCouponBase.primaryLabel) &&
        Objects.equals(this.rewardValue, tenderCouponBase.rewardValue) &&
        Objects.equals(this.any, tenderCouponBase.any) &&
        Objects.equals(this.appliedQuantity, tenderCouponBase.appliedQuantity) &&
        Objects.equals(this.rewardType, tenderCouponBase.rewardType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(quantity, primaryLabel, rewardValue, any, appliedQuantity, rewardType);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TenderCouponBase {\n");
    
    sb.append("    quantity: ").append(toIndentedString(quantity)).append("\n");
    sb.append("    primaryLabel: ").append(toIndentedString(primaryLabel)).append("\n");
    sb.append("    rewardValue: ").append(toIndentedString(rewardValue)).append("\n");
    sb.append("    any: ").append(toIndentedString(any)).append("\n");
    sb.append("    appliedQuantity: ").append(toIndentedString(appliedQuantity)).append("\n");
    sb.append("    rewardType: ").append(toIndentedString(rewardType)).append("\n");
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
