/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.jco.rec.version100.jaxb;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;
import de.hybris.platform.sap.core.jco.rec.version100.SuperToString;


/**
 * <p>Java class for Row complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Row">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded">
 *         &lt;element name="RowElement" type="{}DataElement"/>
 *         &lt;element name="RowStructure" type="{}DataStructure"/>
 *         &lt;element name="RowTable" type="{}DataTable"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Row", propOrder = {
    "rowElementOrRowStructureOrRowTable"
})
public class Row
    extends SuperToString
{

    @XmlElements({
        @XmlElement(name = "RowElement", type = DataElement.class, nillable = true),
        @XmlElement(name = "RowStructure", type = DataStructure.class),
        @XmlElement(name = "RowTable", type = DataTable.class)
    })
    protected List<SuperToString> rowElementOrRowStructureOrRowTable;

    /**
     * Gets the value of the rowElementOrRowStructureOrRowTable property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the rowElementOrRowStructureOrRowTable property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRowElementOrRowStructureOrRowTable().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DataElement }
     * {@link DataStructure }
     * {@link DataTable }
     * 
     * 
     */
    public List<SuperToString> getRowElementOrRowStructureOrRowTable() {
        if (rowElementOrRowStructureOrRowTable == null) {
            rowElementOrRowStructureOrRowTable = new ArrayList<SuperToString>();
        }
        return this.rowElementOrRowStructureOrRowTable;
    }

}
