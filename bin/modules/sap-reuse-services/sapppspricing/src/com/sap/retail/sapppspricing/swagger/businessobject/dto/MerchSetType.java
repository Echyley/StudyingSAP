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
 * A structure for modelling a merchandise set  
 */
@Schema(description = "A structure for modelling a merchandise set  ")
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2022-09-26T19:16:22.658615400+05:30[Asia/Calcutta]")
public class MerchSetType {
  @JsonProperty("any")
  private List<Object> any = null;

  @JsonProperty("MerchandiseSetID")
  private String merchandiseSetID = null;

  @JsonProperty("RootNode")
  private MerchSetNodeTypeL0 rootNode = null;

  public MerchSetType any(List<Object> any) {
    this.any = any;
    return this;
  }

  public MerchSetType addAnyItem(Object anyItem) {
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

  public MerchSetType merchandiseSetID(String merchandiseSetID) {
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

  public MerchSetType rootNode(MerchSetNodeTypeL0 rootNode) {
    this.rootNode = rootNode;
    return this;
  }

   /**
   * Get rootNode
   * @return rootNode
  **/
  @Schema(description = "")
  public MerchSetNodeTypeL0 getRootNode() {
    return rootNode;
  }

  public void setRootNode(MerchSetNodeTypeL0 rootNode) {
    this.rootNode = rootNode;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MerchSetType merchSetType = (MerchSetType) o;
    return Objects.equals(this.any, merchSetType.any) &&
        Objects.equals(this.merchandiseSetID, merchSetType.merchandiseSetID) &&
        Objects.equals(this.rootNode, merchSetType.rootNode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(any, merchandiseSetID, rootNode);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MerchSetType {\n");
    
    sb.append("    any: ").append(toIndentedString(any)).append("\n");
    sb.append("    merchandiseSetID: ").append(toIndentedString(merchandiseSetID)).append("\n");
    sb.append("    rootNode: ").append(toIndentedString(rootNode)).append("\n");
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
