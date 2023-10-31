/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.sapppspricing.swagger.businessobject.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/**
 * A structure for modelling a merchandise set node which can have filter, but not includes or excludes 
 */
@Schema(description = "A structure for modelling a merchandise set node which can have filter, but not includes or excludes ")
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2022-09-26T19:16:22.658615400+05:30[Asia/Calcutta]")
public class MerchSetNodeTypeL1 {
  /**
   * Gets or Sets type
   */
  public enum TypeEnum {
    OPERATION("Operation"),
    ITEM("Item"),
    MERCHANDISEHIERARCHYNODE("MerchandiseHierarchyNode"),
    GENERICATTRIBUTE("GenericAttribute");

    private String value;

    TypeEnum(String value) {
      this.value = value;
    }
    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }
    @JsonCreator
    public static TypeEnum fromValue(String input) {
      for (TypeEnum b : TypeEnum.values()) {
        if (b.value.equals(input)) {
          return b;
        }
      }
      return null;
    }

  }  @JsonProperty("Type")
  private TypeEnum type = null;

  /**
   * Gets or Sets operation
   */
  public enum OperationEnum {
    DIFFERENCE("Difference");

    private String value;

    OperationEnum(String value) {
      this.value = value;
    }
    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }
    @JsonCreator
    public static OperationEnum fromValue(String input) {
      for (OperationEnum b : OperationEnum.values()) {
        if (b.value.equals(input)) {
          return b;
        }
      }
      return null;
    }

  }  @JsonProperty("Operation")
  private OperationEnum operation = null;

  @JsonProperty("ItemID")
  private ItemID itemID = null;

  @JsonProperty("MerchandiseHierarchy")
  private MerchandiseHierarchyCommonData merchandiseHierarchy = null;

  @JsonProperty("GenericAttribute")
  private LineItemAttributeType genericAttribute = null;

  @JsonProperty("Filter")
  private List<MerchSetNodeTypeL2> filter = null;

  public MerchSetNodeTypeL1 type(TypeEnum type) {
    this.type = type;
    return this;
  }

   /**
   * Get type
   * @return type
  **/
  @Schema(description = "")
  public TypeEnum getType() {
    return type;
  }

  public void setType(TypeEnum type) {
    this.type = type;
  }

  public MerchSetNodeTypeL1 operation(OperationEnum operation) {
    this.operation = operation;
    return this;
  }

   /**
   * Get operation
   * @return operation
  **/
  @Schema(description = "")
  public OperationEnum getOperation() {
    return operation;
  }

  public void setOperation(OperationEnum operation) {
    this.operation = operation;
  }

  public MerchSetNodeTypeL1 itemID(ItemID itemID) {
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

  public MerchSetNodeTypeL1 merchandiseHierarchy(MerchandiseHierarchyCommonData merchandiseHierarchy) {
    this.merchandiseHierarchy = merchandiseHierarchy;
    return this;
  }

   /**
   * Get merchandiseHierarchy
   * @return merchandiseHierarchy
  **/
  @Schema(description = "")
  public MerchandiseHierarchyCommonData getMerchandiseHierarchy() {
    return merchandiseHierarchy;
  }

  public void setMerchandiseHierarchy(MerchandiseHierarchyCommonData merchandiseHierarchy) {
    this.merchandiseHierarchy = merchandiseHierarchy;
  }

  public MerchSetNodeTypeL1 genericAttribute(LineItemAttributeType genericAttribute) {
    this.genericAttribute = genericAttribute;
    return this;
  }

   /**
   * Get genericAttribute
   * @return genericAttribute
  **/
  @Schema(description = "")
  public LineItemAttributeType getGenericAttribute() {
    return genericAttribute;
  }

  public void setGenericAttribute(LineItemAttributeType genericAttribute) {
    this.genericAttribute = genericAttribute;
  }

  public MerchSetNodeTypeL1 filter(List<MerchSetNodeTypeL2> filter) {
    this.filter = filter;
    return this;
  }

  public MerchSetNodeTypeL1 addFilterItem(MerchSetNodeTypeL2 filterItem) {
    if (this.filter == null) {
      this.filter = new ArrayList<MerchSetNodeTypeL2>();
    }
    this.filter.add(filterItem);
    return this;
  }

   /**
   * Get filter
   * @return filter
  **/
  @Schema(description = "")
  public List<MerchSetNodeTypeL2> getFilter() {
    return filter;
  }

  public void setFilter(List<MerchSetNodeTypeL2> filter) {
    this.filter = filter;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MerchSetNodeTypeL1 merchSetNodeTypeL1 = (MerchSetNodeTypeL1) o;
    return Objects.equals(this.type, merchSetNodeTypeL1.type) &&
        Objects.equals(this.operation, merchSetNodeTypeL1.operation) &&
        Objects.equals(this.itemID, merchSetNodeTypeL1.itemID) &&
        Objects.equals(this.merchandiseHierarchy, merchSetNodeTypeL1.merchandiseHierarchy) &&
        Objects.equals(this.genericAttribute, merchSetNodeTypeL1.genericAttribute) &&
        Objects.equals(this.filter, merchSetNodeTypeL1.filter);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, operation, itemID, merchandiseHierarchy, genericAttribute, filter);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MerchSetNodeTypeL1 {\n");
    
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    operation: ").append(toIndentedString(operation)).append("\n");
    sb.append("    itemID: ").append(toIndentedString(itemID)).append("\n");
    sb.append("    merchandiseHierarchy: ").append(toIndentedString(merchandiseHierarchy)).append("\n");
    sb.append("    genericAttribute: ").append(toIndentedString(genericAttribute)).append("\n");
    sb.append("    filter: ").append(toIndentedString(filter)).append("\n");
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
