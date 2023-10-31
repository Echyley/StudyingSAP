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
 * A structure for modelling a merchandise set node which can have includes, excludes and filter 
 */
@Schema(description = "A structure for modelling a merchandise set node which can have includes, excludes and filter ")
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2022-09-26T19:16:22.658615400+05:30[Asia/Calcutta]")
public class MerchSetNodeTypeL0 {
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

  @JsonProperty("Include")
  private List<MerchSetNodeTypeL1> include = null;

  @JsonProperty("Exclude")
  private List<MerchSetNodeTypeL1> exclude = null;

  @JsonProperty("Filter")
  private List<MerchSetNodeTypeL1> filter = null;

  public MerchSetNodeTypeL0 type(TypeEnum type) {
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

  public MerchSetNodeTypeL0 operation(OperationEnum operation) {
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

  public MerchSetNodeTypeL0 itemID(ItemID itemID) {
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

  public MerchSetNodeTypeL0 merchandiseHierarchy(MerchandiseHierarchyCommonData merchandiseHierarchy) {
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

  public MerchSetNodeTypeL0 genericAttribute(LineItemAttributeType genericAttribute) {
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

  public MerchSetNodeTypeL0 include(List<MerchSetNodeTypeL1> include) {
    this.include = include;
    return this;
  }

  public MerchSetNodeTypeL0 addIncludeItem(MerchSetNodeTypeL1 includeItem) {
    if (this.include == null) {
      this.include = new ArrayList<MerchSetNodeTypeL1>();
    }
    this.include.add(includeItem);
    return this;
  }

   /**
   * Get include
   * @return include
  **/
  @Schema(description = "")
  public List<MerchSetNodeTypeL1> getInclude() {
    return include;
  }

  public void setInclude(List<MerchSetNodeTypeL1> include) {
    this.include = include;
  }

  public MerchSetNodeTypeL0 exclude(List<MerchSetNodeTypeL1> exclude) {
    this.exclude = exclude;
    return this;
  }

  public MerchSetNodeTypeL0 addExcludeItem(MerchSetNodeTypeL1 excludeItem) {
    if (this.exclude == null) {
      this.exclude = new ArrayList<MerchSetNodeTypeL1>();
    }
    this.exclude.add(excludeItem);
    return this;
  }

   /**
   * Get exclude
   * @return exclude
  **/
  @Schema(description = "")
  public List<MerchSetNodeTypeL1> getExclude() {
    return exclude;
  }

  public void setExclude(List<MerchSetNodeTypeL1> exclude) {
    this.exclude = exclude;
  }

  public MerchSetNodeTypeL0 filter(List<MerchSetNodeTypeL1> filter) {
    this.filter = filter;
    return this;
  }

  public MerchSetNodeTypeL0 addFilterItem(MerchSetNodeTypeL1 filterItem) {
    if (this.filter == null) {
      this.filter = new ArrayList<MerchSetNodeTypeL1>();
    }
    this.filter.add(filterItem);
    return this;
  }

   /**
   * Get filter
   * @return filter
  **/
  @Schema(description = "")
  public List<MerchSetNodeTypeL1> getFilter() {
    return filter;
  }

  public void setFilter(List<MerchSetNodeTypeL1> filter) {
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
    MerchSetNodeTypeL0 merchSetNodeTypeL0 = (MerchSetNodeTypeL0) o;
    return Objects.equals(this.type, merchSetNodeTypeL0.type) &&
        Objects.equals(this.operation, merchSetNodeTypeL0.operation) &&
        Objects.equals(this.itemID, merchSetNodeTypeL0.itemID) &&
        Objects.equals(this.merchandiseHierarchy, merchSetNodeTypeL0.merchandiseHierarchy) &&
        Objects.equals(this.genericAttribute, merchSetNodeTypeL0.genericAttribute) &&
        Objects.equals(this.include, merchSetNodeTypeL0.include) &&
        Objects.equals(this.exclude, merchSetNodeTypeL0.exclude) &&
        Objects.equals(this.filter, merchSetNodeTypeL0.filter);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, operation, itemID, merchandiseHierarchy, genericAttribute, include, exclude, filter);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MerchSetNodeTypeL0 {\n");
    
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    operation: ").append(toIndentedString(operation)).append("\n");
    sb.append("    itemID: ").append(toIndentedString(itemID)).append("\n");
    sb.append("    merchandiseHierarchy: ").append(toIndentedString(merchandiseHierarchy)).append("\n");
    sb.append("    genericAttribute: ").append(toIndentedString(genericAttribute)).append("\n");
    sb.append("    include: ").append(toIndentedString(include)).append("\n");
    sb.append("    exclude: ").append(toIndentedString(exclude)).append("\n");
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
