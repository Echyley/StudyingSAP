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
 * LoyaltyAccountCommonData
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2022-09-26T19:16:22.658615400+05:30[Asia/Calcutta]")
public class LoyaltyAccountCommonData {
  @JsonProperty("CustomerID")
  private String customerID = null;

  @JsonProperty("LoyaltyProgram")
  private List<LoyaltyAccountType> loyaltyProgram = null;

  @JsonProperty("CustomerIsEmployeeFlag")
  private Boolean customerIsEmployeeFlag = null;

  public LoyaltyAccountCommonData customerID(String customerID) {
    this.customerID = customerID;
    return this;
  }

   /**
   * Identifies the customer of the loyalty account. Must be set, but currently the value is not used.
   * @return customerID
  **/
  @Schema(description = "Identifies the customer of the loyalty account. Must be set, but currently the value is not used.")
  public String getCustomerID() {
    return customerID;
  }

  public void setCustomerID(String customerID) {
    this.customerID = customerID;
  }

  public LoyaltyAccountCommonData loyaltyProgram(List<LoyaltyAccountType> loyaltyProgram) {
    this.loyaltyProgram = loyaltyProgram;
    return this;
  }

  public LoyaltyAccountCommonData addLoyaltyProgramItem(LoyaltyAccountType loyaltyProgramItem) {
    if (this.loyaltyProgram == null) {
      this.loyaltyProgram = new ArrayList<LoyaltyAccountType>();
    }
    this.loyaltyProgram.add(loyaltyProgramItem);
    return this;
  }

   /**
   * List of loyalty programs to which this customer is assigned for this loyalty account.
   * @return loyaltyProgram
  **/
  @Schema(description = "List of loyalty programs to which this customer is assigned for this loyalty account.")
  public List<LoyaltyAccountType> getLoyaltyProgram() {
    return loyaltyProgram;
  }

  public void setLoyaltyProgram(List<LoyaltyAccountType> loyaltyProgram) {
    this.loyaltyProgram = loyaltyProgram;
  }

  public LoyaltyAccountCommonData customerIsEmployeeFlag(Boolean customerIsEmployeeFlag) {
    this.customerIsEmployeeFlag = customerIsEmployeeFlag;
    return this;
  }

   /**
   * Indicates if the specified customer is also an employee. Currently not used.
   * @return customerIsEmployeeFlag
  **/
  @Schema(description = "Indicates if the specified customer is also an employee. Currently not used.")
  public Boolean isCustomerIsEmployeeFlag() {
    return customerIsEmployeeFlag;
  }

  public void setCustomerIsEmployeeFlag(Boolean customerIsEmployeeFlag) {
    this.customerIsEmployeeFlag = customerIsEmployeeFlag;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LoyaltyAccountCommonData loyaltyAccountCommonData = (LoyaltyAccountCommonData) o;
    return Objects.equals(this.customerID, loyaltyAccountCommonData.customerID) &&
        Objects.equals(this.loyaltyProgram, loyaltyAccountCommonData.loyaltyProgram) &&
        Objects.equals(this.customerIsEmployeeFlag, loyaltyAccountCommonData.customerIsEmployeeFlag);
  }

  @Override
  public int hashCode() {
    return Objects.hash(customerID, loyaltyProgram, customerIsEmployeeFlag);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LoyaltyAccountCommonData {\n");
    
    sb.append("    customerID: ").append(toIndentedString(customerID)).append("\n");
    sb.append("    loyaltyProgram: ").append(toIndentedString(loyaltyProgram)).append("\n");
    sb.append("    customerIsEmployeeFlag: ").append(toIndentedString(customerIsEmployeeFlag)).append("\n");
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
