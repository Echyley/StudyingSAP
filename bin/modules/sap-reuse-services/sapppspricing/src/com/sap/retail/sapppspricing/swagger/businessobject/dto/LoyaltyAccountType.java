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
 * LoyaltyAccountType
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2022-09-26T19:16:22.658615400+05:30[Asia/Calcutta]")
public class LoyaltyAccountType {
  @JsonProperty("LoyaltyProgramID")
  private List<LoyaltyProgramIDType> loyaltyProgramID = null;

  @JsonProperty("any")
  private List<Object> any = null;

  public LoyaltyAccountType loyaltyProgramID(List<LoyaltyProgramIDType> loyaltyProgramID) {
    this.loyaltyProgramID = loyaltyProgramID;
    return this;
  }

  public LoyaltyAccountType addLoyaltyProgramIDItem(LoyaltyProgramIDType loyaltyProgramIDItem) {
    if (this.loyaltyProgramID == null) {
      this.loyaltyProgramID = new ArrayList<LoyaltyProgramIDType>();
    }
    this.loyaltyProgramID.add(loyaltyProgramIDItem);
    return this;
  }

   /**
   * List
   * @return loyaltyProgramID
  **/
  @Schema(description = "List")
  public List<LoyaltyProgramIDType> getLoyaltyProgramID() {
    return loyaltyProgramID;
  }

  public void setLoyaltyProgramID(List<LoyaltyProgramIDType> loyaltyProgramID) {
    this.loyaltyProgramID = loyaltyProgramID;
  }

  public LoyaltyAccountType any(List<Object> any) {
    this.any = any;
    return this;
  }

  public LoyaltyAccountType addAnyItem(Object anyItem) {
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
    LoyaltyAccountType loyaltyAccountType = (LoyaltyAccountType) o;
    return Objects.equals(this.loyaltyProgramID, loyaltyAccountType.loyaltyProgramID) &&
        Objects.equals(this.any, loyaltyAccountType.any);
  }

  @Override
  public int hashCode() {
    return Objects.hash(loyaltyProgramID, any);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LoyaltyAccountType {\n");
    
    sb.append("    loyaltyProgramID: ").append(toIndentedString(loyaltyProgramID)).append("\n");
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
