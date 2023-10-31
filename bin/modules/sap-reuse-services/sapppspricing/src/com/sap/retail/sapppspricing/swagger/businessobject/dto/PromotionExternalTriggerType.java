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
 * Reserved for future use.
 */
@Schema(description = "Reserved for future use.")
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2022-09-26T19:16:22.658615400+05:30[Asia/Calcutta]")
public class PromotionExternalTriggerType {
  @JsonProperty("ExternalTriggerType")
  private String externalTriggerType = null;

  @JsonProperty("ExternalTriggerAmount")
  private AmountCommonData externalTriggerAmount = null;

  @JsonProperty("any")
  private List<Object> any = null;

  public PromotionExternalTriggerType externalTriggerType(String externalTriggerType) {
    this.externalTriggerType = externalTriggerType;
    return this;
  }

   /**
   * Get externalTriggerType
   * @return externalTriggerType
  **/
  @Schema(required = true, description = "")
  public String getExternalTriggerType() {
    return externalTriggerType;
  }

  public void setExternalTriggerType(String externalTriggerType) {
    this.externalTriggerType = externalTriggerType;
  }

  public PromotionExternalTriggerType externalTriggerAmount(AmountCommonData externalTriggerAmount) {
    this.externalTriggerAmount = externalTriggerAmount;
    return this;
  }

   /**
   * Get externalTriggerAmount
   * @return externalTriggerAmount
  **/
  @Schema(required = true, description = "")
  public AmountCommonData getExternalTriggerAmount() {
    return externalTriggerAmount;
  }

  public void setExternalTriggerAmount(AmountCommonData externalTriggerAmount) {
    this.externalTriggerAmount = externalTriggerAmount;
  }

  public PromotionExternalTriggerType any(List<Object> any) {
    this.any = any;
    return this;
  }

  public PromotionExternalTriggerType addAnyItem(Object anyItem) {
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
    PromotionExternalTriggerType promotionExternalTriggerType = (PromotionExternalTriggerType) o;
    return Objects.equals(this.externalTriggerType, promotionExternalTriggerType.externalTriggerType) &&
        Objects.equals(this.externalTriggerAmount, promotionExternalTriggerType.externalTriggerAmount) &&
        Objects.equals(this.any, promotionExternalTriggerType.any);
  }

  @Override
  public int hashCode() {
    return Objects.hash(externalTriggerType, externalTriggerAmount, any);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PromotionExternalTriggerType {\n");
    
    sb.append("    externalTriggerType: ").append(toIndentedString(externalTriggerType)).append("\n");
    sb.append("    externalTriggerAmount: ").append(toIndentedString(externalTriggerAmount)).append("\n");
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
