/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.sapppspricing.swagger.businessobject.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;
/**
 * A structure for modelling a merchandise set node on the lowest level (can not have includes, excludes or filter)  
 */
@Schema(description = "A structure for modelling a merchandise set node on the lowest level (can not have includes, excludes or filter)  ")
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2022-09-26T19:16:22.658615400+05:30[Asia/Calcutta]")
public class MerchSetNodeTypeL2 {
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

  public MerchSetNodeTypeL2 type(TypeEnum type) {
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

  public MerchSetNodeTypeL2 operation(OperationEnum operation) {
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

  public MerchSetNodeTypeL2 itemID(ItemID itemID) {
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

  public MerchSetNodeTypeL2 merchandiseHierarchy(MerchandiseHierarchyCommonData merchandiseHierarchy) {
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

  public MerchSetNodeTypeL2 genericAttribute(LineItemAttributeType genericAttribute) {
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


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MerchSetNodeTypeL2 merchSetNodeTypeL2 = (MerchSetNodeTypeL2) o;
    return Objects.equals(this.type, merchSetNodeTypeL2.type) &&
        Objects.equals(this.operation, merchSetNodeTypeL2.operation) &&
        Objects.equals(this.itemID, merchSetNodeTypeL2.itemID) &&
        Objects.equals(this.merchandiseHierarchy, merchSetNodeTypeL2.merchandiseHierarchy) &&
        Objects.equals(this.genericAttribute, merchSetNodeTypeL2.genericAttribute);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, operation, itemID, merchandiseHierarchy, genericAttribute);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MerchSetNodeTypeL2 {\n");
    
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    operation: ").append(toIndentedString(operation)).append("\n");
    sb.append("    itemID: ").append(toIndentedString(itemID)).append("\n");
    sb.append("    merchandiseHierarchy: ").append(toIndentedString(merchandiseHierarchy)).append("\n");
    sb.append("    genericAttribute: ").append(toIndentedString(genericAttribute)).append("\n");
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
