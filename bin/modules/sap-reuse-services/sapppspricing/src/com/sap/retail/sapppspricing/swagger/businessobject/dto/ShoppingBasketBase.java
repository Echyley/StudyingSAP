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
 * The collection of the line items to be processed.
 */
@Schema(description = "The collection of the line items to be processed.")
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2022-09-26T19:16:22.658615400+05:30[Asia/Calcutta]")
public class ShoppingBasketBase {
  @JsonProperty("LineItem")
  private List<LineItemDomainSpecific> lineItem = new ArrayList<LineItemDomainSpecific>();

  @JsonProperty("any")
  private List<Object> any = null;

  public ShoppingBasketBase lineItem(List<LineItemDomainSpecific> lineItem) {
    this.lineItem = lineItem;
    return this;
  }

  public ShoppingBasketBase addLineItemItem(LineItemDomainSpecific lineItemItem) {
    this.lineItem.add(lineItemItem);
    return this;
  }

   /**
   * The response might contain more line items than the request due to generated transaction discounts.
   * @return lineItem
  **/
  @Schema(required = true, description = "The response might contain more line items than the request due to generated transaction discounts.")
  public List<LineItemDomainSpecific> getLineItem() {
    return lineItem;
  }

  public void setLineItem(List<LineItemDomainSpecific> lineItem) {
    this.lineItem = lineItem;
  }

  public ShoppingBasketBase any(List<Object> any) {
    this.any = any;
    return this;
  }

  public ShoppingBasketBase addAnyItem(Object anyItem) {
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
    ShoppingBasketBase shoppingBasketBase = (ShoppingBasketBase) o;
    return Objects.equals(this.lineItem, shoppingBasketBase.lineItem) &&
        Objects.equals(this.any, shoppingBasketBase.any);
  }

  @Override
  public int hashCode() {
    return Objects.hash(lineItem, any);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ShoppingBasketBase {\n");
    
    sb.append("    lineItem: ").append(toIndentedString(lineItem)).append("\n");
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
