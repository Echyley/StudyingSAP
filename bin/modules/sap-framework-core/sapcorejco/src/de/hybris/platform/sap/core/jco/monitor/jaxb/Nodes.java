/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.jco.monitor.jaxb;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="node" type="{}nodeType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="singleNodeResult" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "node"
})
@XmlRootElement(name = "nodes")
public class Nodes {

    @XmlElement(required = true)
    protected List<NodeType> node;
    @XmlAttribute(name = "singleNodeResult")
    protected Boolean singleNodeResult;

    /**
     * Gets the value of the node property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the node property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNode().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link NodeType }
     * 
     * 
     */
    public List<NodeType> getNode() {
        if (node == null) {
            node = new ArrayList<NodeType>();
        }
        return this.node;
    }

    /**
     * Gets the value of the singleNodeResult property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSingleNodeResult() {
        return singleNodeResult;
    }

    /**
     * Sets the value of the singleNodeResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSingleNodeResult(Boolean value) {
        this.singleNodeResult = value;
    }

}
