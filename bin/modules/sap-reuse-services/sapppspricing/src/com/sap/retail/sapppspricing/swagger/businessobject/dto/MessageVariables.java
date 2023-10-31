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
 * Message variables for a business error 
 */
@Schema(description = "Message variables for a business error ")
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2022-09-26T19:16:22.658615400+05:30[Asia/Calcutta]")
public class MessageVariables {
  @JsonProperty("MessageVariable")
  private List<MessageVariableType> messageVariable = null;

  public MessageVariables messageVariable(List<MessageVariableType> messageVariable) {
    this.messageVariable = messageVariable;
    return this;
  }

  public MessageVariables addMessageVariableItem(MessageVariableType messageVariableItem) {
    if (this.messageVariable == null) {
      this.messageVariable = new ArrayList<MessageVariableType>();
    }
    this.messageVariable.add(messageVariableItem);
    return this;
  }

   /**
   * Get messageVariable
   * @return messageVariable
  **/
  @Schema(description = "")
  public List<MessageVariableType> getMessageVariable() {
    return messageVariable;
  }

  public void setMessageVariable(List<MessageVariableType> messageVariable) {
    this.messageVariable = messageVariable;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MessageVariables messageVariables = (MessageVariables) o;
    return Objects.equals(this.messageVariable, messageVariables.messageVariable);
  }

  @Override
  public int hashCode() {
    return Objects.hash(messageVariable);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MessageVariables {\n");
    
    sb.append("    messageVariable: ").append(toIndentedString(messageVariable)).append("\n");
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
