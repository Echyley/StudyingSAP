/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.jco.rec.version000.impl;

import de.hybris.platform.sap.core.jco.rec.JCoRecRuntimeException;
import de.hybris.platform.sap.core.jco.rec.version000.Utils000;
import de.hybris.platform.sap.core.jco.rec.version000.JCoRecXMLParserException;

import java.util.HashSet;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sap.conn.jco.JCo;
import com.sap.conn.jco.JCoCustomRepository;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunctionTemplate;
import com.sap.conn.jco.JCoListMetaData;
import com.sap.conn.jco.JCoRecordMetaData;

/**
 * This class provides functionality to parse JCoMetaData from a DOM Document.
 */
class JCoRecMetaDataParser {
    private final Set<Node> parsedNodes = new HashSet<Node>();

    private JCoCustomRepository repository;

    /**
     * Parses metadata from a JCoRecorder repository file.
     * 
     * @param doc
     *            the parsed content will be added to this document.
     * @param repo
     *            the JCoRecorder repository that should be parsed.
     * @throws JCoRecXMLParserException
     * <br/>
     *             if<br/>
     *             <ul>
     *             <li>child elements can't be retrieved</li>
     *             <li>the repository contains unexpected content</li>
     *             <li>the parsing of the content raises an exception</li>
     *             </ul>
     */
    public void parseMetaDataRepository(final Document doc,
            final JCoCustomRepository repo) throws JCoRecXMLParserException {

        repository = repo;
        final Element repositoryRoot = doc.getDocumentElement();
        // get MetaData Root Tag
        final Element metaDataRoot = getChildElement(repositoryRoot,
                Utils000.TAGNAME_METADATA_ROOT, true);

        // get RecordMetaData Root Tag (need to be parsed first)
        final Element recordRoot = getChildElement(metaDataRoot,
                Utils000.TAGNAME_METADATA_RECORD_ROOT, false);

        // get FunctionTemplates Root Tag
        final Element templateRoot = getChildElement(metaDataRoot,
                Utils000.TAGNAME_METADATA_TEMPLATE_ROOT, false);

        if (recordRoot == null && templateRoot == null) {
            throw new JCoRecXMLParserException("Nothing to parse:"
                    + "neither <" + Utils000.TAGNAME_METADATA_RECORD_ROOT
                    + "> nor <" + Utils000.TAGNAME_METADATA_TEMPLATE_ROOT
                    + "> found in <" + metaDataRoot.getNodeName() + ">");
        }

        if (recordRoot != null) {
            parseRecordMetaData(recordRoot, repo, null);
        }
        if (templateRoot != null) {
            parseTemplates(templateRoot, repo);
        }
    }

    /**
     * Parses the {@link JCoFunctionTemplate} from the given DOM Element.
     * 
     * @param templateRoot
     *            the Element to parse
     * @param repo
     *            the {@link JCoRecRepository} to put the
     *            {@link JCoFunctionTemplate JCoFunctionTemplates}
     * @throws JCoRecXMLParserException
     *             if an attribute is missing, or if parsing of the metadata
     *             fails.
     */
    private void parseTemplates(final Element templateRoot,
            final JCoCustomRepository repo) throws JCoRecXMLParserException {
        Node currNode = templateRoot.getFirstChild();
        while (currNode != null) {
            if (currNode instanceof Element) {
                JCoFunctionTemplate template = null;
                final Node functionNameNode = currNode.getAttributes()
                        .getNamedItem(Utils000.ATTNAME_FUNCTION_NAME);

                if (functionNameNode == null) {
                    throw new JCoRecXMLParserException("Expected Attribute "
                            + Utils000.ATTNAME_FUNCTION_NAME + " in "
                            + currNode.getNodeName());
                }

                final String functionName = functionNameNode.getNodeValue();

                final JCoListMetaData imports = parseListMetaData(
                        (Element) currNode, Utils000.TAGNAME_METADATA_INPUT,
                        Utils000.TAGNAME_IMPORT_PARAM);
                final JCoListMetaData exports = parseListMetaData(
                        (Element) currNode, Utils000.TAGNAME_METADATA_OUTPUT,
                        Utils000.TAGNAME_EXPORT_PARAM);
                final JCoListMetaData changing = parseListMetaData(
                        (Element) currNode, Utils000.TAGNAME_METADATA_CHANGING,
                        Utils000.TAGNAME_CHANGING_PARAM);
                final JCoListMetaData tables = parseListMetaData(
                        (Element) currNode, Utils000.TAGNAME_METADATA_TABLE,
                        Utils000.TAGNAME_TABLE_PARAM);

                template = JCo.createFunctionTemplate(functionName, imports,
                        exports, changing, tables, null);
                if (template != null) {
                    repo.addFunctionTemplateToCache(template);
                }
            }
            currNode = currNode.getNextSibling();
        }

    }

    /**
     * Parses the {@link JCoListMetaData} from a given DOM Element.
     * 
     * @param templateNode
     *            the Node to parse
     * @param tagName
     *            the tagname to parse
     * @param paramName
     *            defines whether this is an importing, exporting, changing or
     *            table parameterlist
     * @return the parsed {@link JCoListMetaData}
     * @throws JCoRecXMLParserException
     *             if a child can't be retrieved, or if a metadata element is
     *             missing.
     */
    private JCoListMetaData parseListMetaData(final Element templateNode,
            final String tagName, final String paramName)
            throws JCoRecXMLParserException {
        if (templateNode == null) {
            return null;
        }
        final Element parameterListNode = getChildElement(templateNode,
                tagName, true);
        final NodeList list = parameterListNode.getChildNodes();
        if (list.getLength() == 0) {
            return null;
        }

        final JCoListMetaData listMetaData = JCo.createListMetaData(paramName);

        for (int i = 0; i < list.getLength(); i++) {
            addToListMetaDataByType(templateNode, list, listMetaData, i);
        }
        listMetaData.lock();
        return listMetaData;
    }

    private void addToListMetaDataByType(final Element templateNode,
            final NodeList list, final JCoListMetaData listMetaData, int i)
            throws JCoRecXMLParserException {
        final Node currField = list.item(i);

        final NamedNodeMap attributes = currField.getAttributes();

        /* required attributes */
        final Node fieldNameNode = attributes
                .getNamedItem(Utils000.ATTNAME_METADATA_FIELDNAME);
        final Node fieldRecordTypeNode = attributes
                .getNamedItem(Utils000.ATTNAME_METADATA_RECORDTYPE);
        final Node ucByteLengthNode = attributes
                .getNamedItem(Utils000.ATTNAME_METADATA_UC_BYTELENGTH);
        final Node nucByteLengthNode = attributes
                .getNamedItem(Utils000.ATTNAME_METADATA_NUC_BYTELENGTH);

        /* optional attributes */
        final Node fieldTypeNode = attributes
                .getNamedItem(Utils000.ATTNAME_METADATA_FIELDTYPE);
        final Node nDecimals = attributes
                .getNamedItem(Utils000.ATTNAME_METADATA_DECIMALS);
        final Node descriptionNode = attributes
                .getNamedItem(Utils000.ATTNAME_METADATA_DESCRIPTION);

        checkForMissingAttributes(currField, fieldNameNode,
                fieldRecordTypeNode, ucByteLengthNode, nucByteLengthNode);

        final String fieldName = fieldNameNode.getNodeValue();
        final String fieldRecordType = fieldRecordTypeNode.getNodeValue();
        final int ucByteLength = Integer.parseInt(ucByteLengthNode
                .getNodeValue());
        final int nucByteLength = Integer.parseInt(nucByteLengthNode
                .getNodeValue());

        final int fieldType = findDecimals(fieldTypeNode);
        final int decimals = findDecimals(nDecimals);
        final String description = findDescription(descriptionNode);

        switch (fieldType) {
        case JCoRecordMetaData.TYPE_STRUCTURE:
        case JCoRecordMetaData.TYPE_TABLE:
            JCoRecordMetaData obj1 = getRecordMetaData(fieldName,fieldRecordType, currField, templateNode);
            listMetaData.add(fieldName, fieldType, nucByteLength, ucByteLength,decimals, " ", description,JCoListMetaData.OPTIONAL_PARAMETER, obj1, null);
            break;
        default:
            listMetaData.add(fieldName, fieldType, nucByteLength, ucByteLength,
                    decimals, " ", description,
                    JCoListMetaData.OPTIONAL_PARAMETER, fieldRecordType, null);
            break;
        }
    }

    private JCoRecordMetaData getRecordMetaData(final String fieldName,
            final String fieldRecordType, Node currField, Element templateNode)
            throws JCoRecXMLParserException {
        JCoRecordMetaData obj1 = null;
        try {

            final JCoRecordMetaData recordMetaData = repository
                    .getRecordMetaData(fieldRecordType);

            if (recordMetaData == null) {
                throw new JCoRecXMLParserException(
                        "No RecordMetaData found for Table " + fieldName);
            }

            obj1 = JCo.createRecordMetaData(recordMetaData);
        } catch (final JCoException e) {
            throw new JCoRecXMLParserException("Cannot parse <"
                    + currField.getNodeName() + "> in <"
                    + templateNode.getNodeName() + ">", e);
        }
        return obj1;
    }

    private void checkForMissingAttributes(final Node currField,
            final Node fieldNameNode, final Node fieldRecordTypeNode,
            final Node ucByteLengthNode, final Node nucByteLengthNode)
            throws JCoRecXMLParserException {
        if (fieldNameNode == null) {
            throw new JCoRecXMLParserException("Missing Attribute "
                    + Utils000.ATTNAME_METADATA_FIELDNAME + " in "
                    + currField.getNodeName());
        }
        if (fieldRecordTypeNode == null) {
            throw new JCoRecXMLParserException("Missing Attribute "
                    + Utils000.ATTNAME_METADATA_RECORDTYPE + " in "
                    + currField.getNodeName());
        }
        if (ucByteLengthNode == null) {
            throw new JCoRecXMLParserException("Missing Attribute "
                    + Utils000.ATTNAME_METADATA_UC_BYTELENGTH + " in "
                    + currField.getNodeName());
        }
        if (nucByteLengthNode == null) {
            throw new JCoRecXMLParserException("Missing Attribute "
                    + Utils000.ATTNAME_METADATA_NUC_BYTELENGTH + " in "
                    + currField.getNodeName());
        }
    }

    /**
     * Parses single Records from a DOM Element.
     * 
     * @param recordRoot
     *            the Element to parse from
     * @param repo
     *            the {@link JCoRecRepository} to append the records
     * @param recordName
     *            the name of the record to parse. If this is null all Records
     *            get parsed
     * @throws JCoRecXMLParserException
     *             if an unexpected attribute is found, or the parsing of the
     *             metadata fails.
     */
    private void parseRecordMetaData(final Element recordRoot,
            final JCoCustomRepository repo, final String recordName)
            throws JCoRecXMLParserException {
        Node currMRecNode = recordRoot.getFirstChild();
        while (currMRecNode != null) {
            if (currMRecNode instanceof Element
                    && !parsedNodes.contains(currMRecNode)) {
                final Node mFNAttributeNode = currMRecNode.getAttributes()
                        .getNamedItem(Utils000.ATTNAME_METADATA_FIELDNAME);
                if (mFNAttributeNode == null) {
                    throw new JCoRecXMLParserException("Expected Attribute "
                            + Utils000.ATTNAME_METADATA_FIELDNAME + " in <"
                            + currMRecNode.getNodeName() + ">");
                }

                final String mFNValue = mFNAttributeNode.getNodeValue();
                if (recordName == null || mFNValue.equals(recordName)) {
                    final JCoRecordMetaData recordMetaData = JCo
                            .createRecordMetaData(mFNValue);
                    parseRecordMetaDataChildren((Element) currMRecNode,
                            recordMetaData);
                    repo.addRecordMetaDataToCache(recordMetaData);
                    parsedNodes.add(currMRecNode);
                }
            }
            currMRecNode = currMRecNode.getNextSibling();
        }
    }

    /**
     * Retrieves the child element by its tag name.
     * 
     * @param parent
     *            in this node will be searched.
     * @param childName
     *            the name of the searched child.
     * @param raiseException
     *            whether an exception should be thrown as soon as no child with
     *            the given name is found.
     * 
     * @return the child element or null if there is no such child element
     * @throws JCoRecXMLParserException
     *             if no child is found.
     */
    private Element getChildElement(final Element parent,
            final String childName, final boolean raiseException)
            throws JCoRecXMLParserException {
        Node currChild = parent.getFirstChild();
        while (currChild != null) {
            if (currChild.getNodeName().equals(childName)
                    && currChild instanceof Element) {
                return (Element) currChild;
            }
            currChild = currChild.getNextSibling();
        }
        if (raiseException) {
            throw new JCoRecXMLParserException("Expected <" + childName
                    + "> in <" + parent.getNodeName() + ">");
        } else {
            return null;
        }
    }

    /**
     * Parses the JCoMetaData of a Record.
     * 
     * @param currentMRec
     *            the metadata that should be parsed.
     * @param jcoRecordMetaData
     *            th metadata that should contain the parsed data afterwards.
     * @throws JCoRecXMLParserException
     *             if the content of the parsed metadata contains incomplete
     *             data.
     */
    private void parseRecordMetaDataChildren(final Element currentMRec,
            final JCoRecordMetaData jcoRecordMetaData)
            throws JCoRecXMLParserException {
        if (currentMRec == null) {
            return;
        }
        final NodeList fields = currentMRec.getChildNodes();
        int ucByteOffset = 0;
        int nucByteOffset = 0;

        for (int i = 0; i < fields.getLength(); i++) {
            final Node currField = fields.item(i);
            checkCurrFieldInstanceOfElement(currentMRec, currField);

            final NamedNodeMap attributes = currField.getAttributes();

            /* necessary attributes */
            final Node fieldNameNode = attributes
                    .getNamedItem(Utils000.ATTNAME_METADATA_FIELDNAME);
            final Node fieldRecordTypeNode = attributes
                    .getNamedItem(Utils000.ATTNAME_METADATA_RECORDTYPE);
            final Node ucByteLengthNode = attributes
                    .getNamedItem(Utils000.ATTNAME_METADATA_UC_BYTELENGTH);
            final Node nucByteLengthNode = attributes
                    .getNamedItem(Utils000.ATTNAME_METADATA_NUC_BYTELENGTH);

            /* optional attributes */
            final Node descriptionNode = attributes
                    .getNamedItem(Utils000.ATTNAME_METADATA_DESCRIPTION);
            final Node nDecimals = attributes
                    .getNamedItem(Utils000.ATTNAME_METADATA_DECIMALS);
            final Node fieldTypeNode = attributes
                    .getNamedItem(Utils000.ATTNAME_METADATA_FIELDTYPE);
            final Node ucByteOffsetNode = attributes
                    .getNamedItem(Utils000.ATTNAME_METADATA_UC_BYTELOFFSET);
            final Node nucByteOffsetNode = attributes
                    .getNamedItem(Utils000.ATTNAME_METADATA_NUC_BYTEOFFSET);

            checkMissingAttributesBeforeParsing(currentMRec, fieldNameNode,
                    fieldRecordTypeNode, ucByteLengthNode, nucByteLengthNode);

            final int nucByteLength = Integer.parseInt(nucByteLengthNode
                    .getNodeValue());
            final int ucByteLength = Integer.parseInt(ucByteLengthNode
                    .getNodeValue());

            final String fieldName = fieldNameNode.getNodeValue();
            final String fieldRecordType = fieldRecordTypeNode.getNodeValue();

            final String description = findDescription(descriptionNode);
            final int decimals = findDecimals(nDecimals);

            final int fieldType = findFieldType(fieldTypeNode);

            if (nucByteOffsetNode != null && ucByteOffsetNode != null) {
                nucByteOffset = Integer.parseInt(nucByteOffsetNode
                        .getNodeValue());
                ucByteOffset = Integer
                        .parseInt(ucByteOffsetNode.getNodeValue());
            }

            if (fieldType == JCoRecordMetaData.TYPE_STRUCTURE
                    || fieldType == JCoRecordMetaData.TYPE_TABLE) {
                try {
                    // get the nested RecordMetaData
                    JCoRecordMetaData inner = checkIfRecordMetaDataIsNull(
                            currentMRec, fieldName, fieldRecordType);
                    // Nested Table or Structure
                    final JCoRecordMetaData obj = JCo
                            .createRecordMetaData(inner);
                    jcoRecordMetaData.add(fieldName, fieldType, nucByteLength,
                            nucByteOffset, ucByteLength, ucByteOffset,
                            decimals, description, obj, null);
                } catch (final JCoException e) {
                    throw new JCoRecXMLParserException("Cannot parse Node <"
                            + currField.getNodeName() + "> in <"
                            + currentMRec.getNodeName() + ">", e);
                }

            } else {
                jcoRecordMetaData.add(fieldName, fieldType, nucByteLength,
                        nucByteOffset, ucByteLength, ucByteOffset, decimals,
                        description, fieldRecordType, null);
            }
            ucByteOffset += ucByteLength;
            nucByteOffset += nucByteLength;
        }

        findRecordLengthFromCurrentMRec(currentMRec, jcoRecordMetaData);
    }

    private void findRecordLengthFromCurrentMRec(final Element currentMRec,
            final JCoRecordMetaData jcoRecordMetaData) {
        final NamedNodeMap attributes = currentMRec.getAttributes();
        final Node ucByteLengthNode = attributes
                .getNamedItem(Utils000.ATTNAME_METADATA_UC_BYTELENGTH);
        final Node nucByteLengthNode = attributes
                .getNamedItem(Utils000.ATTNAME_METADATA_NUC_BYTELENGTH);
        if (ucByteLengthNode != null && nucByteLengthNode != null) {
            final int ucByteLength = Integer.parseInt(ucByteLengthNode
                    .getNodeValue());
            final int nucByteLength = Integer.parseInt(nucByteLengthNode
                    .getNodeValue());
            jcoRecordMetaData.setRecordLength(nucByteLength, ucByteLength);
        }
    }

    private int findFieldType(final Node fieldTypeNode) {
        final int fieldType = fieldTypeNode == null ? 0 : Integer
                .parseInt(fieldTypeNode.getNodeValue());
        return fieldType;
    }

    private int findDecimals(final Node nDecimals) {
        final int decimals = findFieldType(nDecimals);
        return decimals;
    }

    private String findDescription(final Node descriptionNode) {
        final String description = descriptionNode == null ? null
                : descriptionNode.getNodeValue();
        return description;
    }

    private void checkCurrFieldInstanceOfElement(final Element currentMRec,
            final Node currField) {
        if (!(currField instanceof Element)) {
            throw new JCoRecRuntimeException(
                    "Expected an Element with Attributes but was: <"
                            + currField.getNodeName() + "> in <"
                            + currentMRec.getNodeName() + ">");
        }
    }

    private JCoRecordMetaData checkIfRecordMetaDataIsNull(
            final Element currentMRec, final String fieldName,
            final String fieldRecordType) throws JCoException,
            JCoRecXMLParserException {
        JCoRecordMetaData inner = repository.getRecordMetaData(fieldRecordType);
        if (inner == null) {
            // nested RecordMetaData have not been parsed yet
            // parse the nested RecordMetaData first
            parseRecordMetaData((Element) currentMRec.getParentNode(),
                    repository, fieldRecordType);

            // retrieve the nested RecordMetaData (that have just been parsed)
            inner = repository.getRecordMetaData(fieldRecordType);

            if (inner == null) {
                // nested RecordMetaData is not available
                throw new JCoRecXMLParserException("No MetaData for Record "
                        + fieldRecordType + " found (nested Record in "
                        + fieldName + ")");
            }
        }
        return inner;
    }

    private void checkMissingAttributesBeforeParsing(final Element currentMRec,
            final Node fieldNameNode, final Node fieldRecordTypeNode,
            final Node ucByteLengthNode, final Node nucByteLengthNode)
            throws JCoRecXMLParserException {
        if (fieldRecordTypeNode == null) {
            throw new JCoRecXMLParserException("Missing attribute in "
                    + Utils000.ATTNAME_METADATA_RECORDTYPE + " <"
                    + currentMRec.getNodeName() + ">");
        }
        if (nucByteLengthNode == null) {
            throw new JCoRecXMLParserException("Missing attribute in "
                    + Utils000.ATTNAME_METADATA_NUC_BYTELENGTH + " <"
                    + currentMRec.getNodeName() + ">");
        }
        if (ucByteLengthNode == null) {
            throw new JCoRecXMLParserException("Missing attribute in "
                    + Utils000.ATTNAME_METADATA_UC_BYTELENGTH + " <"
                    + currentMRec.getNodeName() + ">");
        }
        if (fieldNameNode == null) {
            throw new JCoRecXMLParserException("Missing attribute in "
                    + Utils000.ATTNAME_METADATA_FIELDNAME + " <"
                    + currentMRec.getNodeName() + ">");
        }
    }
}
