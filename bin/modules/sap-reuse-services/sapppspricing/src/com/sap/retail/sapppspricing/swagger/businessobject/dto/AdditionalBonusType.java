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
 * A flavor of a line item whereby customers have a product for free in the  basket. This line item is used in the request and indicates products have already been added to the shopping cart. Supported starting with client API version 5.0 of the calculation request. 
 */
@Schema(description = "A flavor of a line item whereby customers have a product for free in the  basket. This line item is used in the request and indicates products have already been added to the shopping cart. Supported starting with client API version 5.0 of the calculation request. ")
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2022-09-26T19:16:22.658615400+05:30[Asia/Calcutta]")
public class AdditionalBonusType {
  @JsonProperty("any")
  private List<Object> any = null;

  @JsonProperty("AdditionalBonusID")
  private String additionalBonusID = null;

  @JsonProperty("ItemID")
  private ItemID itemID = null;

  @JsonProperty("MerchandiseSetID")
  private String merchandiseSetID = null;

  @JsonProperty("Quantity")
  private QuantityCommonData quantity = null;

  public AdditionalBonusType any(List<Object> any) {
    this.any = any;
    return this;
  }

  public AdditionalBonusType addAnyItem(Object anyItem) {
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

  public AdditionalBonusType additionalBonusID(String additionalBonusID) {
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

  public AdditionalBonusType itemID(ItemID itemID) {
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

  public AdditionalBonusType merchandiseSetID(String merchandiseSetID) {
    this.merchandiseSetID = merchandiseSetID;
    return this;
  }

   /**
   * Decimal representation of a 64 bit integer value
   * @return merchandiseSetID
  **/
  @Schema(description = "Decimal representation of a 64 bit integer value")
  public String getMerchandiseSetID() {
    return merchandiseSetID;
  }

  public void setMerchandiseSetID(String merchandiseSetID) {
    this.merchandiseSetID = merchandiseSetID;
  }

  public AdditionalBonusType quantity(QuantityCommonData quantity) {
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


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AdditionalBonusType additionalBonusType = (AdditionalBonusType) o;
    return Objects.equals(this.any, additionalBonusType.any) &&
        Objects.equals(this.additionalBonusID, additionalBonusType.additionalBonusID) &&
        Objects.equals(this.itemID, additionalBonusType.itemID) &&
        Objects.equals(this.merchandiseSetID, additionalBonusType.merchandiseSetID) &&
        Objects.equals(this.quantity, additionalBonusType.quantity);
  }

  @Override
  public int hashCode() {
    return Objects.hash(any, additionalBonusID, itemID, merchandiseSetID, quantity);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AdditionalBonusType {\n");
    
    sb.append("    any: ").append(toIndentedString(any)).append("\n");
    sb.append("    additionalBonusID: ").append(toIndentedString(additionalBonusID)).append("\n");
    sb.append("    itemID: ").append(toIndentedString(itemID)).append("\n");
    sb.append("    merchandiseSetID: ").append(toIndentedString(merchandiseSetID)).append("\n");
    sb.append("    quantity: ").append(toIndentedString(quantity)).append("\n");
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
