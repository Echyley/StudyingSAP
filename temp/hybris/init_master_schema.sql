
CREATE CACHED TABLE abstractcontact26sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_26Sn ON abstractcontact26sn (ITEMPK);


CREATE CACHED TABLE abstractcontactinfo
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_code NVARCHAR(255),
    p_userpos INTEGER,
    p_user BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    p_phonenumber NVARCHAR(255),
    p_type BIGINT,
    PRIMARY KEY (PK)
);

CREATE INDEX userRelIDX_26 ON abstractcontactinfo (p_user);

CREATE INDEX userposPosIDX_26 ON abstractcontactinfo (p_userpos);


CREATE CACHED TABLE abstrcfgproduct131sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_131Sn ON abstrcfgproduct131sn (ITEMPK);


CREATE CACHED TABLE abstrcfgproductinfo
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_productinfostatus BIGINT,
    p_configuratortype BIGINT,
    p_orderentrypos INTEGER,
    p_orderentry BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX orderentryRelIDX_131 ON abstrcfgproductinfo (p_orderentry);

CREATE INDEX orderentryposPosIDX_131 ON abstrcfgproductinfo (p_orderentrypos);


CREATE CACHED TABLE aclentries
(
    hjmpTS BIGINT,
    PermissionPK BIGINT NOT NULL,
    Negative TINYINT DEFAULT 0,
    PrincipalPK BIGINT NOT NULL,
    ItemPK BIGINT NOT NULL,
    PRIMARY KEY (PermissionPK, PrincipalPK, ItemPK)
);

CREATE INDEX aclcheckindex_aclentries ON aclentries (ItemPK, PrincipalPK);

CREATE INDEX aclupdateindex_aclentries ON aclentries (ItemPK);


CREATE CACHED TABLE addresses
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_original BIGINT,
    p_duplicate TINYINT,
    p_appartment NVARCHAR(255),
    p_building NVARCHAR(255),
    p_cellphone NVARCHAR(255),
    p_company NVARCHAR(255),
    p_country BIGINT,
    p_department NVARCHAR(255),
    p_district NVARCHAR(255),
    p_email NVARCHAR(255),
    p_fax NVARCHAR(255),
    p_firstname NVARCHAR(255),
    p_lastname NVARCHAR(255),
    p_middlename NVARCHAR(255),
    p_middlename2 NVARCHAR(255),
    p_phone1 NVARCHAR(255),
    p_phone2 NVARCHAR(255),
    p_pobox NVARCHAR(255),
    p_postalcode NVARCHAR(255),
    p_region BIGINT,
    p_streetname NVARCHAR(255),
    p_streetnumber NVARCHAR(255),
    p_title BIGINT,
    p_town NVARCHAR(255),
    p_gender BIGINT,
    p_dateofbirth TIMESTAMP,
    p_remarks NVARCHAR(255),
    p_url NVARCHAR(255),
    p_shippingaddress TINYINT,
    p_unloadingaddress TINYINT,
    p_billingaddress TINYINT,
    p_contactaddress TINYINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX testindex_23 ON addresses (p_email);

CREATE INDEX Address_Owner_23 ON addresses (OwnerPkString);


CREATE CACHED TABLE addresses23sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_23Sn ON addresses23sn (ITEMPK);


CREATE CACHED TABLE addressprops
(
    hjmpTS BIGINT,
    ITEMPK BIGINT NOT NULL,
    ITEMTYPEPK BIGINT,
    NAME NVARCHAR(255) NOT NULL,
    LANGPK BIGINT NOT NULL,
    REALNAME NVARCHAR(255),
    TYPE1 INTEGER DEFAULT 0,
    VALUESTRING1 LONGVARCHAR,
    VALUE1 LONGVARBINARY,
    PRIMARY KEY (ITEMPK, NAME, LANGPK)
);

CREATE INDEX nameidx_addressprops ON addressprops (NAME);

CREATE INDEX itempk_addressprops ON addressprops (ITEMPK);

CREATE INDEX realnameidx_addressprops ON addressprops (REALNAME);


CREATE CACHED TABLE agreements
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_id NVARCHAR(255),
    p_startdate TIMESTAMP,
    p_enddate TIMESTAMP,
    p_catalog BIGINT,
    p_buyer BIGINT,
    p_supplier BIGINT,
    p_buyercontact BIGINT,
    p_suppliercontact BIGINT,
    p_currency BIGINT,
    p_catalogversion BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX catalogversionRelIDX_603 ON agreements (p_catalogversion);


CREATE CACHED TABLE agreements603sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_603Sn ON agreements603sn (ITEMPK);


CREATE CACHED TABLE atomictypes
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_extensionname NVARCHAR(255),
    p_autocreate TINYINT,
    p_generate TINYINT,
    InternalCode NVARCHAR(255),
    p_defaultvalue LONGVARBINARY,
    InheritancePathString LONGVARCHAR,
    JavaClassName NVARCHAR(255),
    SuperTypePK BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    InternalCodeLowerCase NVARCHAR(255),
    PRIMARY KEY (PK)
);

CREATE INDEX inheritpsi_81 ON atomictypes (InheritancePathString);

CREATE INDEX typecodelowercase_81 ON atomictypes (InternalCodeLowerCase);

CREATE INDEX typecode_81 ON atomictypes (InternalCode);


CREATE CACHED TABLE atomictypes81sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_81Sn ON atomictypes81sn (ITEMPK);


CREATE CACHED TABLE atomictypeslp
(
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    LANGPK BIGINT,
    p_name NVARCHAR(255),
    p_description LONGVARCHAR,
    PRIMARY KEY (ITEMPK, LANGPK)
);


CREATE CACHED TABLE attr2valuerel
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_value BIGINT,
    p_attributeassignment BIGINT,
    p_attribute BIGINT,
    p_systemversion BIGINT,
    p_position INTEGER,
    p_externalid NVARCHAR(255),
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX sysVer_609 ON attr2valuerel (p_systemversion);

CREATE INDEX catRelIDX_609 ON attr2valuerel (p_attributeassignment);

CREATE INDEX idIDX_609 ON attr2valuerel (p_externalid);

CREATE INDEX valIDX_609 ON attr2valuerel (p_value);

CREATE INDEX attrIDX_609 ON attr2valuerel (p_attribute);


CREATE CACHED TABLE attr2valuerel609sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_609Sn ON attr2valuerel609sn (ITEMPK);


CREATE CACHED TABLE attributedescri87sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_87Sn ON attributedescri87sn (ITEMPK);


CREATE CACHED TABLE attributedescriptors
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_extensionname NVARCHAR(255),
    p_autocreate TINYINT,
    p_generate TINYINT,
    QualifierInternal NVARCHAR(255),
    AttributeTypePK BIGINT,
    columnName NVARCHAR(255),
    p_defaultvalue LONGVARBINARY,
    p_defaultvaluedefinitionstring NVARCHAR(255),
    EnclosingTypePK BIGINT,
    PersistenceQualifierInternal NVARCHAR(255),
    PersistenceTypePK BIGINT,
    p_attributehandler NVARCHAR(255),
    SelectionDescriptorPK BIGINT,
    modifiers INTEGER DEFAULT 0,
    p_unique TINYINT,
    p_hiddenforui TINYINT,
    p_readonlyforui TINYINT,
    p_dontcopy TINYINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    QualifierLowerCaseInternal NVARCHAR(255),
    isHidden TINYINT DEFAULT 0,
    isProperty TINYINT DEFAULT 0,
    SuperAttributeDescriptorPK BIGINT,
    InheritancePathString LONGVARCHAR,
    p_externalqualifier NVARCHAR(255),
    p_storeindatabase TINYINT,
    p_needrestart TINYINT,
    p_param TINYINT,
    p_position INTEGER,
    p_defaultvalueexpression NVARCHAR(255),
    p_issource TINYINT,
    p_ordered TINYINT,
    p_relationname NVARCHAR(255),
    p_relationtype BIGINT,
    PRIMARY KEY (PK)
);

CREATE INDEX lcqualifier_87 ON attributedescriptors (QualifierLowerCaseInternal);

CREATE INDEX enclosing_87 ON attributedescriptors (EnclosingTypePK);

CREATE UNIQUE INDEX qualifier_87 ON attributedescriptors (QualifierInternal, EnclosingTypePK);

CREATE INDEX inheritps_87 ON attributedescriptors (InheritancePathString);


CREATE CACHED TABLE attributedescriptorslp
(
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    LANGPK BIGINT,
    p_name NVARCHAR(255),
    p_description LONGVARCHAR,
    PRIMARY KEY (ITEMPK, LANGPK)
);


CREATE CACHED TABLE bruteforcelogin9450sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_9450Sn ON bruteforcelogin9450sn (ITEMPK);


CREATE CACHED TABLE bruteforceloginattempts
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_uid NVARCHAR(255),
    p_attempts INTEGER,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE UNIQUE INDEX uidX_9450 ON bruteforceloginattempts (p_uid);


CREATE CACHED TABLE cartentries
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_baseprice DECIMAL(30,8),
    p_calculated TINYINT,
    p_discountvaluesinternal LONGVARCHAR,
    p_entrynumber INTEGER,
    p_info LONGVARCHAR,
    p_product BIGINT,
    p_quantity DECIMAL(30,8),
    p_taxvaluesinternal NVARCHAR(255),
    p_totalprice DECIMAL(30,8),
    p_unit BIGINT,
    p_giveaway TINYINT,
    p_rejected TINYINT,
    p_entrygroupnumbers LONGVARBINARY,
    p_order BIGINT,
    p_europe1pricefactory_ppg BIGINT,
    p_europe1pricefactory_ptg BIGINT,
    p_europe1pricefactory_pdg BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX oeProd_44 ON cartentries (p_product);

CREATE INDEX oeOrd_44 ON cartentries (p_order);


CREATE CACHED TABLE cartentries44sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_44Sn ON cartentries44sn (ITEMPK);


CREATE CACHED TABLE carts
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_calculated TINYINT,
    p_code NVARCHAR(255),
    p_currency BIGINT,
    p_deliveryaddress BIGINT,
    p_deliverycost DECIMAL(30,8),
    p_deliverymode BIGINT,
    p_deliverystatus BIGINT,
    p_description NVARCHAR(255),
    p_expirationtime TIMESTAMP,
    p_globaldiscountvaluesinternal LONGVARCHAR,
    p_name NVARCHAR(255),
    p_net TINYINT,
    p_paymentaddress BIGINT,
    p_paymentcost DECIMAL(30,8),
    p_paymentinfo BIGINT,
    p_paymentmode BIGINT,
    p_paymentstatus BIGINT,
    p_status BIGINT,
    p_exportstatus BIGINT,
    p_statusinfo NVARCHAR(255),
    p_totalprice DECIMAL(30,8),
    p_totaldiscounts DECIMAL(30,8),
    p_totaltax DECIMAL(30,8),
    p_totaltaxvaluesinternal LONGVARCHAR,
    p_user BIGINT,
    p_subtotal DECIMAL(30,8),
    p_discountsincludedeliverycost TINYINT,
    p_discountsincludepaymentcost TINYINT,
    p_entrygroups LONGVARBINARY,
    p_europe1pricefactory_udg BIGINT,
    p_europe1pricefactory_upg BIGINT,
    p_europe1pricefactory_utg BIGINT,
    p_sessionid NVARCHAR(255),
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX OrderCode_43 ON carts (p_code);

CREATE INDEX OrderUser_43 ON carts (p_user);


CREATE CACHED TABLE carts43sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_43Sn ON carts43sn (ITEMPK);


CREATE CACHED TABLE cat2attrrel
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_classificationclass BIGINT,
    p_classificationattribute BIGINT,
    p_systemversion BIGINT,
    p_position INTEGER,
    p_externalid NVARCHAR(255),
    p_unit BIGINT,
    p_mandatory TINYINT,
    p_localized TINYINT,
    p_range TINYINT,
    p_multivalued TINYINT,
    p_searchable TINYINT,
    p_attributetype BIGINT,
    p_referencetype BIGINT,
    p_referenceincludessubtypes TINYINT,
    p_formatdefinition NVARCHAR(255),
    p_listable TINYINT,
    p_comparable TINYINT,
    p_visibility BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX sysVer_610 ON cat2attrrel (p_systemversion);

CREATE INDEX relSrcIDX_610 ON cat2attrrel (p_classificationclass);

CREATE INDEX idIDX_610 ON cat2attrrel (p_externalid);

CREATE INDEX relTgtIDX_610 ON cat2attrrel (p_classificationattribute);

CREATE INDEX posIdx_610 ON cat2attrrel (p_position);

CREATE UNIQUE INDEX classVerAttrIDX_610 ON cat2attrrel (p_classificationclass, p_systemversion, p_classificationattribute);


CREATE CACHED TABLE cat2attrrel610sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_610Sn ON cat2attrrel610sn (ITEMPK);


CREATE CACHED TABLE cat2attrrellp
(
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    LANGPK BIGINT,
    p_description NVARCHAR(255),
    PRIMARY KEY (ITEMPK, LANGPK)
);


CREATE CACHED TABLE cat2catrel
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    languagepk BIGINT,
    Qualifier NVARCHAR(255),
    SourcePK BIGINT,
    TargetPK BIGINT,
    SequenceNumber INTEGER DEFAULT 0,
    RSequenceNumber INTEGER DEFAULT 0,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX linktarget_144 ON cat2catrel (TargetPK);

CREATE INDEX qualifier_144 ON cat2catrel (Qualifier);

CREATE INDEX linksource_144 ON cat2catrel (SourcePK);

CREATE INDEX seqnr_144 ON cat2catrel (SequenceNumber);

CREATE INDEX rseqnr_144 ON cat2catrel (RSequenceNumber);


CREATE CACHED TABLE cat2catrel144sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    sourcePK BIGINT,
    targetPK BIGINT,
    languagePK BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_144Sn ON cat2catrel144sn (ITEMPK);

CREATE INDEX IdxsourcePK_144Sn ON cat2catrel144sn (sourcePK);

CREATE INDEX IdxtargetPK_144Sn ON cat2catrel144sn (targetPK);

CREATE INDEX IdxlanguagePK_144Sn ON cat2catrel144sn (languagePK);


CREATE CACHED TABLE cat2keywordrel
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    languagepk BIGINT,
    Qualifier NVARCHAR(255),
    SourcePK BIGINT,
    TargetPK BIGINT,
    SequenceNumber INTEGER DEFAULT 0,
    RSequenceNumber INTEGER DEFAULT 0,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX linktarget_605 ON cat2keywordrel (TargetPK);

CREATE INDEX qualifier_605 ON cat2keywordrel (Qualifier);

CREATE INDEX linksource_605 ON cat2keywordrel (SourcePK);

CREATE INDEX seqnr_605 ON cat2keywordrel (SequenceNumber);

CREATE INDEX rseqnr_605 ON cat2keywordrel (RSequenceNumber);


CREATE CACHED TABLE cat2keywordrel605sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    sourcePK BIGINT,
    targetPK BIGINT,
    languagePK BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_605Sn ON cat2keywordrel605sn (ITEMPK);

CREATE INDEX IdxsourcePK_605Sn ON cat2keywordrel605sn (sourcePK);

CREATE INDEX IdxtargetPK_605Sn ON cat2keywordrel605sn (targetPK);

CREATE INDEX IdxlanguagePK_605Sn ON cat2keywordrel605sn (languagePK);


CREATE CACHED TABLE cat2medrel
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    languagepk BIGINT,
    Qualifier NVARCHAR(255),
    SourcePK BIGINT,
    TargetPK BIGINT,
    SequenceNumber INTEGER DEFAULT 0,
    RSequenceNumber INTEGER DEFAULT 0,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX linktarget_145 ON cat2medrel (TargetPK);

CREATE INDEX qualifier_145 ON cat2medrel (Qualifier);

CREATE INDEX linksource_145 ON cat2medrel (SourcePK);

CREATE INDEX seqnr_145 ON cat2medrel (SequenceNumber);

CREATE INDEX rseqnr_145 ON cat2medrel (RSequenceNumber);


CREATE CACHED TABLE cat2medrel145sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    sourcePK BIGINT,
    targetPK BIGINT,
    languagePK BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_145Sn ON cat2medrel145sn (ITEMPK);

CREATE INDEX IdxsourcePK_145Sn ON cat2medrel145sn (sourcePK);

CREATE INDEX IdxtargetPK_145Sn ON cat2medrel145sn (targetPK);

CREATE INDEX IdxlanguagePK_145Sn ON cat2medrel145sn (languagePK);


CREATE CACHED TABLE cat2princrel
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    languagepk BIGINT,
    Qualifier NVARCHAR(255),
    SourcePK BIGINT,
    TargetPK BIGINT,
    SequenceNumber INTEGER DEFAULT 0,
    RSequenceNumber INTEGER DEFAULT 0,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX linktarget_613 ON cat2princrel (TargetPK);

CREATE INDEX qualifier_613 ON cat2princrel (Qualifier);

CREATE INDEX linksource_613 ON cat2princrel (SourcePK);

CREATE INDEX seqnr_613 ON cat2princrel (SequenceNumber);

CREATE INDEX rseqnr_613 ON cat2princrel (RSequenceNumber);


CREATE CACHED TABLE cat2princrel613sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    sourcePK BIGINT,
    targetPK BIGINT,
    languagePK BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_613Sn ON cat2princrel613sn (ITEMPK);

CREATE INDEX IdxsourcePK_613Sn ON cat2princrel613sn (sourcePK);

CREATE INDEX IdxtargetPK_613Sn ON cat2princrel613sn (targetPK);

CREATE INDEX IdxlanguagePK_613Sn ON cat2princrel613sn (languagePK);


CREATE CACHED TABLE cat2prodrel
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    languagepk BIGINT,
    Qualifier NVARCHAR(255),
    SourcePK BIGINT,
    TargetPK BIGINT,
    SequenceNumber INTEGER DEFAULT 0,
    RSequenceNumber INTEGER DEFAULT 0,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX linktarget_143 ON cat2prodrel (TargetPK);

CREATE INDEX qualifier_143 ON cat2prodrel (Qualifier);

CREATE INDEX linksource_143 ON cat2prodrel (SourcePK);

CREATE INDEX seqnr_143 ON cat2prodrel (SequenceNumber);

CREATE INDEX rseqnr_143 ON cat2prodrel (RSequenceNumber);


CREATE CACHED TABLE cat2prodrel143sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    sourcePK BIGINT,
    targetPK BIGINT,
    languagePK BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_143Sn ON cat2prodrel143sn (ITEMPK);

CREATE INDEX IdxsourcePK_143Sn ON cat2prodrel143sn (sourcePK);

CREATE INDEX IdxtargetPK_143Sn ON cat2prodrel143sn (targetPK);

CREATE INDEX IdxlanguagePK_143Sn ON cat2prodrel143sn (languagePK);


CREATE CACHED TABLE catalogs
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_id varchar(200),
    p_activecatalogversion BIGINT,
    p_defaultcatalog TINYINT,
    p_previewurltemplate NVARCHAR(255),
    p_urlpatterns LONGVARBINARY,
    p_supplier BIGINT,
    p_buyer BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX idIdx_600 ON catalogs (p_id);

CREATE INDEX supplierRelIDX_600 ON catalogs (p_supplier);

CREATE INDEX buyerRelIDX_600 ON catalogs (p_buyer);


CREATE CACHED TABLE catalogs600sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_600Sn ON catalogs600sn (ITEMPK);


CREATE CACHED TABLE catalogslp
(
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    LANGPK BIGINT,
    p_name NVARCHAR(255),
    PRIMARY KEY (ITEMPK, LANGPK)
);


CREATE CACHED TABLE catalogversions
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_active TINYINT,
    p_version NVARCHAR(255),
    p_mimerootdirectory NVARCHAR(255),
    p_generationdate TIMESTAMP,
    p_defaultcurrency BIGINT,
    p_inclfreight TINYINT,
    p_inclpacking TINYINT,
    p_inclassurance TINYINT,
    p_inclduty TINYINT,
    p_territories LONGVARCHAR,
    p_languages LONGVARCHAR,
    p_generatorinfo NVARCHAR(255),
    p_categorysystemid NVARCHAR(255),
    p_previousupdateversion INTEGER,
    p_catalog BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX versionIDX_601 ON catalogversions (p_version);

CREATE INDEX catalogIDX_601 ON catalogversions (p_catalog);

CREATE INDEX visibleIDX_601 ON catalogversions (p_active);


CREATE CACHED TABLE catalogversions601sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_601Sn ON catalogversions601sn (ITEMPK);


CREATE CACHED TABLE catalogversions624sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    sourcePK BIGINT,
    targetPK BIGINT,
    languagePK BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_624Sn ON catalogversions624sn (ITEMPK);

CREATE INDEX IdxsourcePK_624Sn ON catalogversions624sn (sourcePK);

CREATE INDEX IdxtargetPK_624Sn ON catalogversions624sn (targetPK);

CREATE INDEX IdxlanguagePK_624Sn ON catalogversions624sn (languagePK);


CREATE CACHED TABLE catalogversionslp
(
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    LANGPK BIGINT,
    p_categorysystemname NVARCHAR(255),
    p_categorysystemdescription NVARCHAR(255),
    PRIMARY KEY (ITEMPK, LANGPK)
);


CREATE CACHED TABLE catalogversionsyncjob
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    languagepk BIGINT,
    Qualifier NVARCHAR(255),
    SourcePK BIGINT,
    TargetPK BIGINT,
    SequenceNumber INTEGER DEFAULT 0,
    RSequenceNumber INTEGER DEFAULT 0,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX linktarget_624 ON catalogversionsyncjob (TargetPK);

CREATE INDEX qualifier_624 ON catalogversionsyncjob (Qualifier);

CREATE INDEX linksource_624 ON catalogversionsyncjob (SourcePK);

CREATE INDEX seqnr_624 ON catalogversionsyncjob (SequenceNumber);

CREATE INDEX rseqnr_624 ON catalogversionsyncjob (RSequenceNumber);


CREATE CACHED TABLE categories
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_order INTEGER,
    p_catalog BIGINT,
    p_catalogversion BIGINT,
    p_normal LONGVARCHAR,
    p_thumbnails LONGVARCHAR,
    p_detail LONGVARCHAR,
    p_logo LONGVARCHAR,
    p_data_sheet LONGVARCHAR,
    p_others LONGVARCHAR,
    p_thumbnail BIGINT,
    p_picture BIGINT,
    p_code NVARCHAR(255),
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    p_externalid NVARCHAR(255),
    p_revision NVARCHAR(255),
    p_showemptyattributes TINYINT,
    PRIMARY KEY (PK)
);

CREATE INDEX codeIDX_142 ON categories (p_code);

CREATE INDEX versionIDX_142 ON categories (p_catalogversion);

CREATE UNIQUE INDEX codeVersionIDX_142 ON categories (p_code, p_catalogversion);

CREATE INDEX extID_142 ON categories (p_externalid);


CREATE CACHED TABLE categories142sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_142Sn ON categories142sn (ITEMPK);


CREATE CACHED TABLE categorieslp
(
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    LANGPK BIGINT,
    p_description LONGVARCHAR,
    p_name NVARCHAR(255),
    PRIMARY KEY (ITEMPK, LANGPK)
);


CREATE CACHED TABLE catverdiffs
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_sourceversion BIGINT,
    p_targetversion BIGINT,
    p_cronjob BIGINT,
    p_differencetext LONGVARCHAR,
    p_differencevalue DOUBLE,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    p_sourceproduct BIGINT,
    p_targetproduct BIGINT,
    p_mode BIGINT,
    p_sourcecategory BIGINT,
    p_targetcategory BIGINT,
    PRIMARY KEY (PK)
);


CREATE CACHED TABLE catverdiffs615sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_615Sn ON catverdiffs615sn (ITEMPK);


CREATE CACHED TABLE changedescripto505sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_505Sn ON changedescripto505sn (ITEMPK);


CREATE CACHED TABLE changedescriptors
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_cronjob BIGINT,
    p_step BIGINT,
    p_changeditem BIGINT,
    p_sequencenumber INTEGER,
    p_savetimestamp TIMESTAMP,
    p_changetype NVARCHAR(255),
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    p_targetitem BIGINT,
    p_done TINYINT,
    p_copiedimplicitely TINYINT,
    PRIMARY KEY (PK)
);

CREATE INDEX cronjobIDX_505 ON changedescriptors (p_cronjob);

CREATE INDEX stepIDX_505 ON changedescriptors (p_step);

CREATE INDEX changedItemIDX_505 ON changedescriptors (p_changeditem);

CREATE INDEX seqNrIDX_505 ON changedescriptors (p_sequencenumber);

CREATE INDEX doneIDX_505 ON changedescriptors (p_done);


CREATE CACHED TABLE classattrvalues
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_code NVARCHAR(255),
    p_externalid NVARCHAR(255),
    p_systemversion BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX sysVer_608 ON classattrvalues (p_systemversion);

CREATE INDEX code_608 ON classattrvalues (p_code);

CREATE INDEX idIDX_608 ON classattrvalues (p_externalid);

CREATE UNIQUE INDEX codeVersionIDX_608 ON classattrvalues (p_code, p_systemversion);


CREATE CACHED TABLE classattrvalues608sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_608Sn ON classattrvalues608sn (ITEMPK);


CREATE CACHED TABLE classattrvalueslp
(
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    LANGPK BIGINT,
    p_name NVARCHAR(255),
    PRIMARY KEY (ITEMPK, LANGPK)
);


CREATE CACHED TABLE classificationa607sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_607Sn ON classificationa607sn (ITEMPK);


CREATE CACHED TABLE classificationattrs
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_systemversion BIGINT,
    p_code NVARCHAR(255),
    p_externalid NVARCHAR(255),
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX sysVer_607 ON classificationattrs (p_systemversion);

CREATE INDEX code_607 ON classificationattrs (p_code);

CREATE INDEX idIDX_607 ON classificationattrs (p_externalid);


CREATE CACHED TABLE classificationattrslp
(
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    LANGPK BIGINT,
    p_name NVARCHAR(255),
    PRIMARY KEY (ITEMPK, LANGPK)
);


CREATE CACHED TABLE clattrunt
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_systemversion BIGINT,
    p_code NVARCHAR(255),
    p_externalid NVARCHAR(255),
    p_symbol NVARCHAR(255),
    p_unittype NVARCHAR(255),
    p_conversionfactor DOUBLE,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX sysVer_612 ON clattrunt (p_systemversion);

CREATE INDEX codeIdx_612 ON clattrunt (p_code);

CREATE INDEX extID_612 ON clattrunt (p_externalid);


CREATE CACHED TABLE clattrunt612sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_612Sn ON clattrunt612sn (ITEMPK);


CREATE CACHED TABLE clattruntlp
(
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    LANGPK BIGINT,
    p_name NVARCHAR(255),
    PRIMARY KEY (ITEMPK, LANGPK)
);


CREATE CACHED TABLE cmptype2covgrpr978sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    sourcePK BIGINT,
    targetPK BIGINT,
    languagePK BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_978Sn ON cmptype2covgrpr978sn (ITEMPK);

CREATE INDEX IdxsourcePK_978Sn ON cmptype2covgrpr978sn (sourcePK);

CREATE INDEX IdxtargetPK_978Sn ON cmptype2covgrpr978sn (targetPK);

CREATE INDEX IdxlanguagePK_978Sn ON cmptype2covgrpr978sn (languagePK);


CREATE CACHED TABLE cmptype2covgrprels
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    languagepk BIGINT,
    Qualifier NVARCHAR(255),
    SourcePK BIGINT,
    TargetPK BIGINT,
    SequenceNumber INTEGER DEFAULT 0,
    RSequenceNumber INTEGER DEFAULT 0,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX linktarget_978 ON cmptype2covgrprels (TargetPK);

CREATE INDEX qualifier_978 ON cmptype2covgrprels (Qualifier);

CREATE INDEX linksource_978 ON cmptype2covgrprels (SourcePK);

CREATE INDEX seqnr_978 ON cmptype2covgrprels (SequenceNumber);

CREATE INDEX rseqnr_978 ON cmptype2covgrprels (RSequenceNumber);


CREATE CACHED TABLE collectiontypes
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_extensionname NVARCHAR(255),
    p_autocreate TINYINT,
    p_generate TINYINT,
    InternalCode NVARCHAR(255),
    p_defaultvalue LONGVARBINARY,
    ElementTypePK BIGINT,
    typeOfCollection INTEGER DEFAULT 0,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    InternalCodeLowerCase NVARCHAR(255),
    PRIMARY KEY (PK)
);

CREATE INDEX typecodelowercase_83 ON collectiontypes (InternalCodeLowerCase);

CREATE INDEX typecode_83 ON collectiontypes (InternalCode);


CREATE CACHED TABLE collectiontypes83sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_83Sn ON collectiontypes83sn (ITEMPK);


CREATE CACHED TABLE collectiontypeslp
(
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    LANGPK BIGINT,
    p_name NVARCHAR(255),
    p_description LONGVARCHAR,
    PRIMARY KEY (ITEMPK, LANGPK)
);


CREATE CACHED TABLE commentassignre1148sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    sourcePK BIGINT,
    targetPK BIGINT,
    languagePK BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_1148Sn ON commentassignre1148sn (ITEMPK);

CREATE INDEX IdxsourcePK_1148Sn ON commentassignre1148sn (sourcePK);

CREATE INDEX IdxtargetPK_1148Sn ON commentassignre1148sn (targetPK);

CREATE INDEX IdxlanguagePK_1148Sn ON commentassignre1148sn (languagePK);


CREATE CACHED TABLE commentassignrelations
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    languagepk BIGINT,
    Qualifier NVARCHAR(255),
    SourcePK BIGINT,
    TargetPK BIGINT,
    SequenceNumber INTEGER DEFAULT 0,
    RSequenceNumber INTEGER DEFAULT 0,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX linktarget_1148 ON commentassignrelations (TargetPK);

CREATE INDEX qualifier_1148 ON commentassignrelations (Qualifier);

CREATE INDEX linksource_1148 ON commentassignrelations (SourcePK);

CREATE INDEX seqnr_1148 ON commentassignrelations (SequenceNumber);

CREATE INDEX rseqnr_1148 ON commentassignrelations (RSequenceNumber);


CREATE CACHED TABLE commentattachme1146sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_1146Sn ON commentattachme1146sn (ITEMPK);


CREATE CACHED TABLE commentattachments
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_item BIGINT,
    p_abstractcomment BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX comm_att_comment_1146 ON commentattachments (p_abstractcomment);


CREATE CACHED TABLE commentcompcrea1152sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    sourcePK BIGINT,
    targetPK BIGINT,
    languagePK BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_1152Sn ON commentcompcrea1152sn (ITEMPK);

CREATE INDEX IdxsourcePK_1152Sn ON commentcompcrea1152sn (sourcePK);

CREATE INDEX IdxtargetPK_1152Sn ON commentcompcrea1152sn (targetPK);

CREATE INDEX IdxlanguagePK_1152Sn ON commentcompcrea1152sn (languagePK);


CREATE CACHED TABLE commentcompcreaterels
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    languagepk BIGINT,
    Qualifier NVARCHAR(255),
    SourcePK BIGINT,
    TargetPK BIGINT,
    SequenceNumber INTEGER DEFAULT 0,
    RSequenceNumber INTEGER DEFAULT 0,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX linktarget_1152 ON commentcompcreaterels (TargetPK);

CREATE INDEX qualifier_1152 ON commentcompcreaterels (Qualifier);

CREATE INDEX linksource_1152 ON commentcompcreaterels (SourcePK);

CREATE INDEX seqnr_1152 ON commentcompcreaterels (SequenceNumber);

CREATE INDEX rseqnr_1152 ON commentcompcreaterels (RSequenceNumber);


CREATE CACHED TABLE commentcomponen1142sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_1142Sn ON commentcomponen1142sn (ITEMPK);


CREATE CACHED TABLE commentcomponents
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_code NVARCHAR(255),
    p_name NVARCHAR(255),
    p_domain BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX commComponent_code_1142 ON commentcomponents (p_code);

CREATE INDEX domainRelIDX_1142 ON commentcomponents (p_domain);


CREATE CACHED TABLE commentcompread1150sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    sourcePK BIGINT,
    targetPK BIGINT,
    languagePK BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_1150Sn ON commentcompread1150sn (ITEMPK);

CREATE INDEX IdxsourcePK_1150Sn ON commentcompread1150sn (sourcePK);

CREATE INDEX IdxtargetPK_1150Sn ON commentcompread1150sn (targetPK);

CREATE INDEX IdxlanguagePK_1150Sn ON commentcompread1150sn (languagePK);


CREATE CACHED TABLE commentcompreadrels
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    languagepk BIGINT,
    Qualifier NVARCHAR(255),
    SourcePK BIGINT,
    TargetPK BIGINT,
    SequenceNumber INTEGER DEFAULT 0,
    RSequenceNumber INTEGER DEFAULT 0,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX linktarget_1150 ON commentcompreadrels (TargetPK);

CREATE INDEX qualifier_1150 ON commentcompreadrels (Qualifier);

CREATE INDEX linksource_1150 ON commentcompreadrels (SourcePK);

CREATE INDEX seqnr_1150 ON commentcompreadrels (SequenceNumber);

CREATE INDEX rseqnr_1150 ON commentcompreadrels (RSequenceNumber);


CREATE CACHED TABLE commentcompremo1153sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    sourcePK BIGINT,
    targetPK BIGINT,
    languagePK BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_1153Sn ON commentcompremo1153sn (ITEMPK);

CREATE INDEX IdxsourcePK_1153Sn ON commentcompremo1153sn (sourcePK);

CREATE INDEX IdxtargetPK_1153Sn ON commentcompremo1153sn (targetPK);

CREATE INDEX IdxlanguagePK_1153Sn ON commentcompremo1153sn (languagePK);


CREATE CACHED TABLE commentcompremoverels
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    languagepk BIGINT,
    Qualifier NVARCHAR(255),
    SourcePK BIGINT,
    TargetPK BIGINT,
    SequenceNumber INTEGER DEFAULT 0,
    RSequenceNumber INTEGER DEFAULT 0,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX linktarget_1153 ON commentcompremoverels (TargetPK);

CREATE INDEX qualifier_1153 ON commentcompremoverels (Qualifier);

CREATE INDEX linksource_1153 ON commentcompremoverels (SourcePK);

CREATE INDEX seqnr_1153 ON commentcompremoverels (SequenceNumber);

CREATE INDEX rseqnr_1153 ON commentcompremoverels (RSequenceNumber);


CREATE CACHED TABLE commentcompwrit1151sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    sourcePK BIGINT,
    targetPK BIGINT,
    languagePK BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_1151Sn ON commentcompwrit1151sn (ITEMPK);

CREATE INDEX IdxsourcePK_1151Sn ON commentcompwrit1151sn (sourcePK);

CREATE INDEX IdxtargetPK_1151Sn ON commentcompwrit1151sn (targetPK);

CREATE INDEX IdxlanguagePK_1151Sn ON commentcompwrit1151sn (languagePK);


CREATE CACHED TABLE commentcompwriterels
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    languagepk BIGINT,
    Qualifier NVARCHAR(255),
    SourcePK BIGINT,
    TargetPK BIGINT,
    SequenceNumber INTEGER DEFAULT 0,
    RSequenceNumber INTEGER DEFAULT 0,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX linktarget_1151 ON commentcompwriterels (TargetPK);

CREATE INDEX qualifier_1151 ON commentcompwriterels (Qualifier);

CREATE INDEX linksource_1151 ON commentcompwriterels (SourcePK);

CREATE INDEX seqnr_1151 ON commentcompwriterels (SequenceNumber);

CREATE INDEX rseqnr_1151 ON commentcompwriterels (RSequenceNumber);


CREATE CACHED TABLE commentdomains
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_code NVARCHAR(255),
    p_name NVARCHAR(255),
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX commDomain_code_1141 ON commentdomains (p_code);


CREATE CACHED TABLE commentdomains1141sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_1141Sn ON commentdomains1141sn (ITEMPK);


CREATE CACHED TABLE commentitemrela1147sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    sourcePK BIGINT,
    targetPK BIGINT,
    languagePK BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_1147Sn ON commentitemrela1147sn (ITEMPK);

CREATE INDEX IdxsourcePK_1147Sn ON commentitemrela1147sn (sourcePK);

CREATE INDEX IdxtargetPK_1147Sn ON commentitemrela1147sn (targetPK);

CREATE INDEX IdxlanguagePK_1147Sn ON commentitemrela1147sn (languagePK);


CREATE CACHED TABLE commentitemrelations
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    languagepk BIGINT,
    Qualifier NVARCHAR(255),
    SourcePK BIGINT,
    TargetPK BIGINT,
    SequenceNumber INTEGER DEFAULT 0,
    RSequenceNumber INTEGER DEFAULT 0,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX linktarget_1147 ON commentitemrelations (TargetPK);

CREATE INDEX qualifier_1147 ON commentitemrelations (Qualifier);

CREATE INDEX linksource_1147 ON commentitemrelations (SourcePK);

CREATE INDEX seqnr_1147 ON commentitemrelations (SequenceNumber);

CREATE INDEX rseqnr_1147 ON commentitemrelations (RSequenceNumber);


CREATE CACHED TABLE comments
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_subject NVARCHAR(255),
    p_author BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    p_code NVARCHAR(255),
    p_priority INTEGER,
    p_component BIGINT,
    p_commenttype BIGINT,
    p_parentpos INTEGER,
    p_parent BIGINT,
    p_commentpos INTEGER,
    p_comment BIGINT,
    PRIMARY KEY (PK)
);

CREATE INDEX comment_code_1140 ON comments (p_code);

CREATE INDEX comment_component_1140 ON comments (p_component);

CREATE INDEX reply_parent_1140 ON comments (p_parent);

CREATE INDEX reply_comment_1140 ON comments (p_comment);

CREATE INDEX parentposPosIDX_1140 ON comments (p_parentpos);

CREATE INDEX commentposPosIDX_1140 ON comments (p_commentpos);

CREATE INDEX authorRelIDX_1140 ON comments (p_author);

CREATE INDEX commenttypeRelIDX_1140 ON comments (p_commenttype);


CREATE CACHED TABLE comments1140sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_1140Sn ON comments1140sn (ITEMPK);


CREATE CACHED TABLE commenttypes
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_code NVARCHAR(255),
    p_name NVARCHAR(255),
    p_metatype BIGINT,
    p_domain BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX comment_type_code_1145 ON commenttypes (p_code, p_domain);

CREATE INDEX domainRelIDX_1145 ON commenttypes (p_domain);


CREATE CACHED TABLE commenttypes1145sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_1145Sn ON commenttypes1145sn (ITEMPK);


CREATE CACHED TABLE commentusersett1144sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_1144Sn ON commentusersett1144sn (ITEMPK);


CREATE CACHED TABLE commentusersettings
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_read TINYINT,
    p_ignored TINYINT,
    p_priority INTEGER,
    p_comment BIGINT,
    p_user BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE UNIQUE INDEX cus_user_comment_1144 ON commentusersettings (p_user, p_comment);

CREATE INDEX commentRelIDX_1144 ON commentusersettings (p_comment);

CREATE INDEX userRelIDX_1144 ON commentusersettings (p_user);


CREATE CACHED TABLE commentwatchrel1149sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    sourcePK BIGINT,
    targetPK BIGINT,
    languagePK BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_1149Sn ON commentwatchrel1149sn (ITEMPK);

CREATE INDEX IdxsourcePK_1149Sn ON commentwatchrel1149sn (sourcePK);

CREATE INDEX IdxtargetPK_1149Sn ON commentwatchrel1149sn (targetPK);

CREATE INDEX IdxlanguagePK_1149Sn ON commentwatchrel1149sn (languagePK);


CREATE CACHED TABLE commentwatchrelations
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    languagepk BIGINT,
    Qualifier NVARCHAR(255),
    SourcePK BIGINT,
    TargetPK BIGINT,
    SequenceNumber INTEGER DEFAULT 0,
    RSequenceNumber INTEGER DEFAULT 0,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX linktarget_1149 ON commentwatchrelations (TargetPK);

CREATE INDEX qualifier_1149 ON commentwatchrelations (Qualifier);

CREATE INDEX linksource_1149 ON commentwatchrelations (SourcePK);

CREATE INDEX seqnr_1149 ON commentwatchrelations (SequenceNumber);

CREATE INDEX rseqnr_1149 ON commentwatchrelations (RSequenceNumber);


CREATE CACHED TABLE composedtypes
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_extensionname NVARCHAR(255),
    p_autocreate TINYINT,
    p_generate TINYINT,
    InternalCode NVARCHAR(255),
    p_defaultvalue LONGVARBINARY,
    InheritancePathString LONGVARCHAR,
    jaloClassName NVARCHAR(255),
    ItemJNDIName NVARCHAR(255),
    Singleton TINYINT DEFAULT 0,
    p_jaloonly TINYINT,
    p_dynamic TINYINT,
    SuperTypePK BIGINT,
    p_legacypersistence TINYINT,
    p_systemtype TINYINT,
    p_catalogitemtype TINYINT,
    p_catalogversionattributequali NVARCHAR(255),
    p_uniquekeyattributequalifier NVARCHAR(255),
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    InternalCodeLowerCase NVARCHAR(255),
    removable TINYINT DEFAULT 0,
    propertyTableStatus TINYINT DEFAULT 0,
    ItemTypeCode INTEGER DEFAULT 0,
    p_comparationattribute BIGINT,
    p_localized TINYINT,
    p_sourceattribute BIGINT,
    p_targetattribute BIGINT,
    p_sourcetype BIGINT,
    p_targettype BIGINT,
    p_sourcenavigable TINYINT,
    p_targetnavigable TINYINT,
    p_orderingattribute BIGINT,
    p_localizationattribute BIGINT,
    PRIMARY KEY (PK)
);

CREATE INDEX ComposedTypeSuperTypePKIDX_82 ON composedtypes (SuperTypePK);

CREATE INDEX inheritpsi_82 ON composedtypes (InheritancePathString);

CREATE INDEX typecodelowercase_82 ON composedtypes (InternalCodeLowerCase);

CREATE INDEX typecode_82 ON composedtypes (InternalCode);


CREATE CACHED TABLE composedtypes82sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_82Sn ON composedtypes82sn (ITEMPK);


CREATE CACHED TABLE composedtypeslp
(
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    LANGPK BIGINT,
    p_name NVARCHAR(255),
    p_description LONGVARCHAR,
    PRIMARY KEY (ITEMPK, LANGPK)
);


CREATE CACHED TABLE compositeentrie510sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_510Sn ON compositeentrie510sn (ITEMPK);


CREATE CACHED TABLE compositeentries
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_code NVARCHAR(255),
    p_executablecronjob BIGINT,
    p_triggerablejob BIGINT,
    p_compositecronjobpos INTEGER,
    p_compositecronjob BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX compositecronjobRelIDX_510 ON compositeentries (p_compositecronjob);

CREATE INDEX compositecronjobposPosIDX_510 ON compositeentries (p_compositecronjobpos);


CREATE CACHED TABLE configitems
(
    hjmpTS BIGINT,
    PK BIGINT NOT NULL,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    aCLTS BIGINT DEFAULT 0,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);


CREATE CACHED TABLE configuratorset130sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_130Sn ON configuratorset130sn (ITEMPK);


CREATE CACHED TABLE configuratorsettings
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_id NVARCHAR(255),
    p_catalogversion BIGINT,
    p_configuratortype BIGINT,
    p_qualifier NVARCHAR(255),
    p_configurationcategorypos INTEGER,
    p_configurationcategory BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX versionIdx_130 ON configuratorsettings (p_catalogversion);

CREATE UNIQUE INDEX idVersionIdx_130 ON configuratorsettings (p_id, p_catalogversion);

CREATE INDEX configurationcategoryRelIDX_130 ON configuratorsettings (p_configurationcategory);

CREATE INDEX configurationcategoryposPosIDX_130 ON configuratorsettings (p_configurationcategorypos);


CREATE CACHED TABLE constraintgroup
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_id NVARCHAR(255),
    p_interfacename NVARCHAR(255),
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    p_coveragedomainid NVARCHAR(255),
    PRIMARY KEY (PK)
);

CREATE INDEX CronstraintGroup_idx_982 ON constraintgroup (p_id);


CREATE CACHED TABLE constraintgroup982sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_982Sn ON constraintgroup982sn (ITEMPK);


CREATE CACHED TABLE corsconfigprope800sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_800Sn ON corsconfigprope800sn (ITEMPK);


CREATE CACHED TABLE corsconfigproperty
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_context NVARCHAR(255),
    p_key NVARCHAR(255),
    p_value NVARCHAR(255),
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE UNIQUE INDEX contextKey_800 ON corsconfigproperty (p_context, p_key);


CREATE CACHED TABLE countries
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_active TINYINT,
    p_isocode NVARCHAR(255),
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX ISOCode_34 ON countries (p_isocode);


CREATE CACHED TABLE countries34sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_34Sn ON countries34sn (ITEMPK);


CREATE CACHED TABLE countrieslp
(
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    LANGPK BIGINT,
    p_name NVARCHAR(255),
    PRIMARY KEY (ITEMPK, LANGPK)
);


CREATE CACHED TABLE cronjobhistorie522sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_522Sn ON cronjobhistorie522sn (ITEMPK);


CREATE CACHED TABLE cronjobhistories
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_cronjobcode NVARCHAR(255),
    p_jobcode NVARCHAR(255),
    p_starttime TIMESTAMP,
    p_endtime TIMESTAMP,
    p_nodeid INTEGER,
    p_scheduled TINYINT,
    p_useruid NVARCHAR(255),
    p_status BIGINT,
    p_result BIGINT,
    p_failuremessage NVARCHAR(255),
    p_progress DOUBLE,
    p_statusline NVARCHAR(255),
    p_cronjob BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX IdxCronJob_522 ON cronjobhistories (p_cronjob);


CREATE CACHED TABLE cronjobs
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_code NVARCHAR(255),
    p_errormode BIGINT,
    p_logtofile TINYINT,
    p_logtodatabase TINYINT,
    p_loglevelfile BIGINT,
    p_logleveldatabase BIGINT,
    p_sessionuser BIGINT,
    p_sessionlanguage BIGINT,
    p_sessioncurrency BIGINT,
    p_active TINYINT,
    p_retry TINYINT,
    p_singleexecutable TINYINT,
    p_emailaddress NVARCHAR(255),
    p_sendemail TINYINT,
    p_starttime TIMESTAMP,
    p_endtime TIMESTAMP,
    p_status BIGINT,
    p_result BIGINT,
    p_nodeid INTEGER,
    p_nodegroup NVARCHAR(255),
    p_runningonclusternode INTEGER,
    p_currentstep BIGINT,
    p_changerecordingenabled TINYINT,
    p_requestabort TINYINT,
    p_requestabortstep TINYINT,
    p_priority INTEGER,
    p_removeonexit TINYINT,
    p_emailnotificationtemplate BIGINT,
    p_alternativedatasourceid NVARCHAR(255),
    p_logsdaysold INTEGER,
    p_logscount INTEGER,
    p_logsoperator BIGINT,
    p_filesdaysold INTEGER,
    p_filescount INTEGER,
    p_filesoperator BIGINT,
    p_querycount INTEGER,
    p_activecronjobhistory BIGINT,
    p_usereadonlydatasource TINYINT,
    p_numberofretries INTEGER,
    p_currentretry INTEGER,
    p_job BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    p_jobmedia BIGINT,
    p_currentline INTEGER,
    p_lastsuccessfulline INTEGER,
    p_url NVARCHAR(255),
    p_query LONGVARCHAR,
    p_failonunknown TINYINT,
    p_dontneedtotal TINYINT,
    p_rangestart INTEGER,
    p_count INTEGER,
    p_itempks BIGINT,
    p_itemsfound INTEGER,
    p_itemsdeleted INTEGER,
    p_itemsrefused INTEGER,
    p_createsavedvalues TINYINT,
    p_medias LONGVARCHAR,
    p_targetfolder BIGINT,
    p_movedmediascount INTEGER,
    p_mediafolder BIGINT,
    p_timethreshold INTEGER,
    p_versionthreshold INTEGER,
    p_xdaysold INTEGER,
    p_excludecronjobs LONGVARCHAR,
    p_resultcoll LONGVARCHAR,
    p_statuscoll LONGVARCHAR,
    p_encoding BIGINT,
    p_mode BIGINT,
    p_dataexporttarget BIGINT,
    p_mediasexporttarget BIGINT,
    p_exporttemplate BIGINT,
    p_export BIGINT,
    p_itemsexported INTEGER,
    p_itemsmaxcount INTEGER,
    p_itemsskipped INTEGER,
    p_fieldseparator SMALLINT,
    p_quotecharacter SMALLINT,
    p_commentcharacter SMALLINT,
    p_dataexportmediacode NVARCHAR(255),
    p_mediasexportmediacode NVARCHAR(255),
    p_report BIGINT,
    p_converterclass BIGINT,
    p_singlefile TINYINT,
    p_workmedia BIGINT,
    p_mediasmedia BIGINT,
    p_externaldatacollection LONGVARCHAR,
    p_locale NVARCHAR(255),
    p_dumpfileencoding BIGINT,
    p_enablecodeexecution TINYINT,
    p_enableexternalcodeexecution TINYINT,
    p_enableexternalsyntaxparsing TINYINT,
    p_enablehmcsavedvalues TINYINT,
    p_mediastarget NVARCHAR(255),
    p_valuecount INTEGER,
    p_unresolveddatastore BIGINT,
    p_dumpingallowed TINYINT,
    p_unzipmediasmedia TINYINT,
    p_maxthreads INTEGER,
    p_legacymode TINYINT,
    p_processeditemscount INTEGER,
    p_sourceversion BIGINT,
    p_targetversion BIGINT,
    p_missingproducts INTEGER,
    p_newproducts INTEGER,
    p_maxpricetolerance DOUBLE,
    p_searchmissingproducts TINYINT,
    p_searchmissingcategories TINYINT,
    p_searchnewproducts TINYINT,
    p_searchnewcategories TINYINT,
    p_searchpricedifferences TINYINT,
    p_overwriteproductapprovalstat TINYINT,
    p_pricecomparecustomer BIGINT,
    p_catalog BIGINT,
    p_catalogversion BIGINT,
    p_dontremoveobjects TINYINT,
    p_notremoveditems BIGINT,
    p_totaldeleteitemcount INTEGER,
    p_currentprocessingitemcount INTEGER,
    p_forceupdate TINYINT,
    p_fullsync TINYINT,
    p_abortoncollidingsync TINYINT,
    p_statusmessage NVARCHAR(255),
    PRIMARY KEY (PK)
);

CREATE INDEX IdxJob_501 ON cronjobs (p_job);

CREATE INDEX IdxNode_501 ON cronjobs (p_nodeid);

CREATE INDEX IdxActive_501 ON cronjobs (p_active);


CREATE CACHED TABLE cronjobs501sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_501Sn ON cronjobs501sn (ITEMPK);


CREATE CACHED TABLE cronjobslp
(
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    LANGPK BIGINT,
    p_name NVARCHAR(255),
    p_description LONGVARCHAR,
    PRIMARY KEY (ITEMPK, LANGPK)
);


CREATE CACHED TABLE cstrgr2abscstrr979sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    sourcePK BIGINT,
    targetPK BIGINT,
    languagePK BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_979Sn ON cstrgr2abscstrr979sn (ITEMPK);

CREATE INDEX IdxsourcePK_979Sn ON cstrgr2abscstrr979sn (sourcePK);

CREATE INDEX IdxtargetPK_979Sn ON cstrgr2abscstrr979sn (targetPK);

CREATE INDEX IdxlanguagePK_979Sn ON cstrgr2abscstrr979sn (languagePK);


CREATE CACHED TABLE cstrgr2abscstrrel
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    languagepk BIGINT,
    Qualifier NVARCHAR(255),
    SourcePK BIGINT,
    TargetPK BIGINT,
    SequenceNumber INTEGER DEFAULT 0,
    RSequenceNumber INTEGER DEFAULT 0,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX linktarget_979 ON cstrgr2abscstrrel (TargetPK);

CREATE INDEX qualifier_979 ON cstrgr2abscstrrel (Qualifier);

CREATE INDEX linksource_979 ON cstrgr2abscstrrel (SourcePK);

CREATE INDEX seqnr_979 ON cstrgr2abscstrrel (SequenceNumber);

CREATE INDEX rseqnr_979 ON cstrgr2abscstrrel (RSequenceNumber);


CREATE CACHED TABLE currencies
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_active TINYINT,
    p_isocode NVARCHAR(255),
    p_base TINYINT,
    p_conversion DOUBLE,
    p_digits INTEGER,
    p_symbol NVARCHAR(255),
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX ISOCode_33 ON currencies (p_isocode);


CREATE CACHED TABLE currencies33sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_33Sn ON currencies33sn (ITEMPK);


CREATE CACHED TABLE currencieslp
(
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    LANGPK BIGINT,
    p_name NVARCHAR(255),
    PRIMARY KEY (ITEMPK, LANGPK)
);


CREATE CACHED TABLE cvsynccronjobhi523sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_523Sn ON cvsynccronjobhi523sn (ITEMPK);


CREATE CACHED TABLE cvsynccronjobhistories
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_cronjobcode NVARCHAR(255),
    p_jobcode NVARCHAR(255),
    p_starttime TIMESTAMP,
    p_endtime TIMESTAMP,
    p_nodeid INTEGER,
    p_scheduled TINYINT,
    p_useruid NVARCHAR(255),
    p_status BIGINT,
    p_result BIGINT,
    p_failuremessage NVARCHAR(255),
    p_progress DOUBLE,
    p_statusline NVARCHAR(255),
    p_cronjob BIGINT,
    p_processeditemscount INTEGER,
    p_scheduleditemscount INTEGER,
    p_dumpeditemscount INTEGER,
    p_fullsync TINYINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX IdxCronJob_523 ON cvsynccronjobhistories (p_cronjob);


CREATE CACHED TABLE deliverymodes
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_active TINYINT,
    p_code NVARCHAR(255),
    p_supportedpaymentmodesinterna NVARCHAR(255),
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    p_propertyname NVARCHAR(255),
    p_net TINYINT,
    PRIMARY KEY (PK)
);


CREATE CACHED TABLE deliverymodes40sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_40Sn ON deliverymodes40sn (ITEMPK);


CREATE CACHED TABLE deliverymodeslp
(
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    LANGPK BIGINT,
    p_description LONGVARCHAR,
    p_name NVARCHAR(255),
    PRIMARY KEY (ITEMPK, LANGPK)
);


CREATE CACHED TABLE derivedmedias
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_mime NVARCHAR(255),
    p_size BIGINT,
    p_datapk BIGINT,
    p_location LONGVARCHAR,
    p_locationhash NVARCHAR(255),
    p_realfilename NVARCHAR(255),
    p_version NVARCHAR(255),
    p_media BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX dataPK_idx_31 ON derivedmedias (p_datapk);

CREATE UNIQUE INDEX version_idx_31 ON derivedmedias (p_media, p_version);

CREATE INDEX mediaRelIDX_31 ON derivedmedias (p_media);


CREATE CACHED TABLE derivedmedias31sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_31Sn ON derivedmedias31sn (ITEMPK);


CREATE CACHED TABLE discountrows
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_product BIGINT,
    p_pg BIGINT,
    p_productmatchqualifier BIGINT,
    p_starttime TIMESTAMP,
    p_endtime TIMESTAMP,
    p_user BIGINT,
    p_ug BIGINT,
    p_usermatchqualifier BIGINT,
    p_productid NVARCHAR(255),
    p_currency BIGINT,
    p_discount BIGINT,
    p_value DOUBLE,
    p_catalogversion BIGINT,
    p_astargetprice TINYINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX MatchIndexP_1052 ON discountrows (p_productmatchqualifier);

CREATE INDEX MatchIndexU_1052 ON discountrows (p_usermatchqualifier);

CREATE INDEX PIdx_1052 ON discountrows (p_product);

CREATE INDEX UIdx_1052 ON discountrows (p_user);

CREATE INDEX PGIdx_1052 ON discountrows (p_pg);

CREATE INDEX UGIdx_1052 ON discountrows (p_ug);

CREATE INDEX ProductIdIdx_1052 ON discountrows (p_productid);

CREATE INDEX versionIDX_1052 ON discountrows (p_catalogversion);


CREATE CACHED TABLE discountrows1052sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_1052Sn ON discountrows1052sn (ITEMPK);


CREATE CACHED TABLE discounts
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_code NVARCHAR(255),
    p_currency BIGINT,
    p_global TINYINT,
    p_priority INTEGER,
    p_value DOUBLE,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);


CREATE CACHED TABLE discounts48sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_48Sn ON discounts48sn (ITEMPK);


CREATE CACHED TABLE discountslp
(
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    LANGPK BIGINT,
    p_name NVARCHAR(255),
    PRIMARY KEY (ITEMPK, LANGPK)
);


CREATE CACHED TABLE distributedbatc112sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_112Sn ON distributedbatc112sn (ITEMPK);


CREATE CACHED TABLE distributedbatches
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_id NVARCHAR(255),
    p_executionid NVARCHAR(255),
    p_type BIGINT,
    p_remainingworkload BIGINT,
    p_process BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    p_resultbatchid NVARCHAR(255),
    p_retries INTEGER,
    p_scriptcode NVARCHAR(255),
    p_context LONGVARBINARY,
    p_group INTEGER,
    p_metadata NVARCHAR(255),
    p_importcontentcode NVARCHAR(255),
    PRIMARY KEY (PK)
);

CREATE UNIQUE INDEX BatchUnqIdx_112 ON distributedbatches (p_executionid, p_process, p_id, p_type);

CREATE INDEX processRelIDX_112 ON distributedbatches (p_process);


CREATE CACHED TABLE distributedproc111sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_111Sn ON distributedproc111sn (ITEMPK);


CREATE CACHED TABLE distributedprocesses
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_code NVARCHAR(255),
    p_handlerbeanid NVARCHAR(255),
    p_currentexecutionid NVARCHAR(255),
    p_state BIGINT,
    p_stoprequested TINYINT,
    p_nodegroup NVARCHAR(255),
    p_status NVARCHAR(255),
    p_extendedstatus NVARCHAR(255),
    p_progress DOUBLE,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    p_batchsize INTEGER,
    p_impeximportcronjob BIGINT,
    p_metadata NVARCHAR(255),
    PRIMARY KEY (PK)
);


CREATE CACHED TABLE dynamiccontent
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_code NVARCHAR(255),
    p_active TINYINT,
    p_checksum NVARCHAR(255),
    p_content LONGVARCHAR,
    p_version BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE UNIQUE INDEX codeVersionActiveIDX_101 ON dynamiccontent (p_code, p_version, p_active);


CREATE CACHED TABLE dynamiccontent101sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_101Sn ON dynamiccontent101sn (ITEMPK);


CREATE CACHED TABLE enumerationvalu91sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_91Sn ON enumerationvalu91sn (ITEMPK);


CREATE CACHED TABLE enumerationvalues
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    Code NVARCHAR(255),
    codeLowerCase NVARCHAR(255),
    SequenceNumber INTEGER DEFAULT 0,
    p_extensionname NVARCHAR(255),
    p_icon BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    Editable TINYINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE UNIQUE INDEX uniqueCodeIdx_91 ON enumerationvalues (TypePkString, codeLowerCase);

CREATE INDEX codeidx_91 ON enumerationvalues (Code);

CREATE INDEX seqnridx_91 ON enumerationvalues (SequenceNumber);

CREATE INDEX code2idx_91 ON enumerationvalues (codeLowerCase);


CREATE CACHED TABLE enumerationvalueslp
(
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    LANGPK BIGINT,
    p_name NVARCHAR(255),
    PRIMARY KEY (ITEMPK, LANGPK)
);


CREATE CACHED TABLE exports
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_code NVARCHAR(255),
    p_exportedmedias BIGINT,
    p_exporteddata BIGINT,
    p_exportscript BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);


CREATE CACHED TABLE exports151sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_151Sn ON exports151sn (ITEMPK);


CREATE CACHED TABLE exportslp
(
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    LANGPK BIGINT,
    p_description NVARCHAR(255),
    PRIMARY KEY (ITEMPK, LANGPK)
);


CREATE CACHED TABLE externalimportk110sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_110Sn ON externalimportk110sn (ITEMPK);


CREATE CACHED TABLE externalimportkey
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_sourcesystemid NVARCHAR(255),
    p_sourcekey NVARCHAR(255),
    p_targetpk BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE UNIQUE INDEX sourceSystemIDSourceKeyIDX_110 ON externalimportkey (p_sourcesystemid, p_sourcekey);


CREATE CACHED TABLE format
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_code NVARCHAR(255),
    p_initial BIGINT,
    p_documenttype BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);


CREATE CACHED TABLE format13113sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_13113Sn ON format13113sn (ITEMPK);


CREATE CACHED TABLE format2comtypre13102sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    sourcePK BIGINT,
    targetPK BIGINT,
    languagePK BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_13102Sn ON format2comtypre13102sn (ITEMPK);

CREATE INDEX IdxsourcePK_13102Sn ON format2comtypre13102sn (sourcePK);

CREATE INDEX IdxtargetPK_13102Sn ON format2comtypre13102sn (targetPK);

CREATE INDEX IdxlanguagePK_13102Sn ON format2comtypre13102sn (languagePK);


CREATE CACHED TABLE format2comtyprel
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    languagepk BIGINT,
    Qualifier NVARCHAR(255),
    SourcePK BIGINT,
    TargetPK BIGINT,
    SequenceNumber INTEGER DEFAULT 0,
    RSequenceNumber INTEGER DEFAULT 0,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX linktarget_13102 ON format2comtyprel (TargetPK);

CREATE INDEX qualifier_13102 ON format2comtyprel (Qualifier);

CREATE INDEX linksource_13102 ON format2comtyprel (SourcePK);

CREATE INDEX seqnr_13102 ON format2comtyprel (SequenceNumber);

CREATE INDEX rseqnr_13102 ON format2comtyprel (RSequenceNumber);


CREATE CACHED TABLE format2medforre13101sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    sourcePK BIGINT,
    targetPK BIGINT,
    languagePK BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_13101Sn ON format2medforre13101sn (ITEMPK);

CREATE INDEX IdxsourcePK_13101Sn ON format2medforre13101sn (sourcePK);

CREATE INDEX IdxtargetPK_13101Sn ON format2medforre13101sn (targetPK);

CREATE INDEX IdxlanguagePK_13101Sn ON format2medforre13101sn (languagePK);


CREATE CACHED TABLE format2medforrel
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    languagepk BIGINT,
    Qualifier NVARCHAR(255),
    SourcePK BIGINT,
    TargetPK BIGINT,
    SequenceNumber INTEGER DEFAULT 0,
    RSequenceNumber INTEGER DEFAULT 0,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX linktarget_13101 ON format2medforrel (TargetPK);

CREATE INDEX qualifier_13101 ON format2medforrel (Qualifier);

CREATE INDEX linksource_13101 ON format2medforrel (SourcePK);

CREATE INDEX seqnr_13101 ON format2medforrel (SequenceNumber);

CREATE INDEX rseqnr_13101 ON format2medforrel (RSequenceNumber);


CREATE CACHED TABLE formatlp
(
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    LANGPK BIGINT,
    p_name NVARCHAR(255),
    PRIMARY KEY (ITEMPK, LANGPK)
);


CREATE CACHED TABLE genericitems
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    p_comparator BIGINT,
    p_emptyhandling BIGINT,
    p_valuetype BIGINT,
    p_searchparametername NVARCHAR(255),
    p_joinalias NVARCHAR(255),
    p_lower TINYINT,
    p_wherepart BIGINT,
    p_typedsearchparameter BIGINT,
    p_enclosingtype BIGINT,
    p_code NVARCHAR(255),
    p_actiontemplate BIGINT,
    p_action BIGINT,
    PRIMARY KEY (PK)
);

CREATE INDEX wherepartRelIDX_99 ON genericitems (p_wherepart);

CREATE INDEX actiontemplateRelIDX_99 ON genericitems (p_actiontemplate);

CREATE INDEX actionRelIDX_99 ON genericitems (p_action);


CREATE CACHED TABLE genericitems99sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_99Sn ON genericitems99sn (ITEMPK);


CREATE CACHED TABLE genericitemslp
(
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    LANGPK BIGINT,
    p_name NVARCHAR(255),
    p_description NVARCHAR(255),
    PRIMARY KEY (ITEMPK, LANGPK)
);


CREATE CACHED TABLE gentestitems
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);


CREATE CACHED TABLE gentestitems98sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_98Sn ON gentestitems98sn (ITEMPK);


CREATE CACHED TABLE globaldiscountr1053sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_1053Sn ON globaldiscountr1053sn (ITEMPK);


CREATE CACHED TABLE globaldiscountrows
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_product BIGINT,
    p_pg BIGINT,
    p_productmatchqualifier BIGINT,
    p_starttime TIMESTAMP,
    p_endtime TIMESTAMP,
    p_user BIGINT,
    p_ug BIGINT,
    p_usermatchqualifier BIGINT,
    p_productid NVARCHAR(255),
    p_currency BIGINT,
    p_discount BIGINT,
    p_value DOUBLE,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX MatchIndexP_1053 ON globaldiscountrows (p_productmatchqualifier);

CREATE INDEX MatchIndexU_1053 ON globaldiscountrows (p_usermatchqualifier);

CREATE INDEX PIdx_1053 ON globaldiscountrows (p_product);

CREATE INDEX UIdx_1053 ON globaldiscountrows (p_user);

CREATE INDEX PGIdx_1053 ON globaldiscountrows (p_pg);

CREATE INDEX UGIdx_1053 ON globaldiscountrows (p_ug);

CREATE INDEX ProductIdIdx_1053 ON globaldiscountrows (p_productid);


CREATE CACHED TABLE impbatchcontent
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_code NVARCHAR(255),
    p_content LONGVARCHAR,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    p_processcode NVARCHAR(255),
    PRIMARY KEY (PK)
);

CREATE UNIQUE INDEX codeIDX_113 ON impbatchcontent (p_code);


CREATE CACHED TABLE impbatchcontent113sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_113Sn ON impbatchcontent113sn (ITEMPK);


CREATE CACHED TABLE impexdocumentid114sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_114Sn ON impexdocumentid114sn (ITEMPK);


CREATE CACHED TABLE impexdocumentids
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_processcode NVARCHAR(255),
    p_docid NVARCHAR(255),
    p_itemqualifier NVARCHAR(255),
    p_itempk BIGINT,
    p_resolved TINYINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE UNIQUE INDEX ImpexDocumentIdsIdx_114 ON impexdocumentids (p_processcode, p_docid, p_itemqualifier, p_itempk, p_resolved);


CREATE CACHED TABLE indextestitem
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_column1 SMALLINT,
    p_column2 SMALLINT,
    p_column3 SMALLINT,
    p_column4 SMALLINT,
    p_column5 SMALLINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX OrderIndex_7777 ON indextestitem (p_column3, p_column4, p_column1, p_column2, p_column5);


CREATE CACHED TABLE indextestitem7777sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_7777Sn ON indextestitem7777sn (ITEMPK);


CREATE CACHED TABLE itemsynctimesta619sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_619Sn ON itemsynctimesta619sn (ITEMPK);


CREATE CACHED TABLE itemsynctimestamps
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_syncjob BIGINT,
    p_sourceitem BIGINT,
    p_targetitem BIGINT,
    p_sourceversion BIGINT,
    p_targetversion BIGINT,
    p_lastsyncsourcemodifiedtime TIMESTAMP,
    p_lastsynctime TIMESTAMP,
    p_pendingattributesownerjob BIGINT,
    p_pendingattributesscheduledtu INTEGER,
    p_pendingattributequalifiers LONGVARCHAR,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE UNIQUE INDEX syncIDX_619 ON itemsynctimestamps (p_sourceitem, p_targetversion, p_syncjob);

CREATE INDEX srcIDX_619 ON itemsynctimestamps (p_sourceitem);

CREATE INDEX tgtIDX_619 ON itemsynctimestamps (p_targetitem);

CREATE INDEX jobIDX_619 ON itemsynctimestamps (p_syncjob);

CREATE INDEX srcVerIDX_619 ON itemsynctimestamps (p_sourceversion);

CREATE INDEX tgtVerIDX_619 ON itemsynctimestamps (p_targetversion);


CREATE CACHED TABLE jalotranslatorc13214sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_13214Sn ON jalotranslatorc13214sn (ITEMPK);


CREATE CACHED TABLE jalotranslatorconfig
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_code NVARCHAR(255),
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);


CREATE CACHED TABLE jalovelocityren13211sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_13211Sn ON jalovelocityren13211sn (ITEMPK);


CREATE CACHED TABLE jalovelocityrenderer
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_name NVARCHAR(255),
    p_template LONGVARCHAR,
    p_translatorconfigurationpos INTEGER,
    p_translatorconfiguration BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX translatorconfigurationRelIDX_13211 ON jalovelocityrenderer (p_translatorconfiguration);

CREATE INDEX translatorconfigurationposPosIDX_13211 ON jalovelocityrenderer (p_translatorconfigurationpos);


CREATE CACHED TABLE joblogs
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_step BIGINT,
    p_level BIGINT,
    p_cronjob BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX cronjobIDX_504 ON joblogs (p_cronjob);


CREATE CACHED TABLE joblogs504sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_504Sn ON joblogs504sn (ITEMPK);


CREATE CACHED TABLE jobs
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_code NVARCHAR(255),
    p_nodeid INTEGER,
    p_nodegroup NVARCHAR(255),
    p_errormode BIGINT,
    p_logtofile TINYINT,
    p_logtodatabase TINYINT,
    p_loglevelfile BIGINT,
    p_logleveldatabase BIGINT,
    p_sessionuser BIGINT,
    p_sessionlanguage BIGINT,
    p_sessioncurrency BIGINT,
    p_active TINYINT,
    p_retry TINYINT,
    p_singleexecutable TINYINT,
    p_emailaddress NVARCHAR(255),
    p_sendemail TINYINT,
    p_changerecordingenabled TINYINT,
    p_requestabort TINYINT,
    p_requestabortstep TINYINT,
    p_priority INTEGER,
    p_removeonexit TINYINT,
    p_emailnotificationtemplate BIGINT,
    p_alternativedatasourceid NVARCHAR(255),
    p_usereadonlydatasource TINYINT,
    p_numberofretries INTEGER,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    p_springid NVARCHAR(255),
    p_springidcronjobfactory NVARCHAR(255),
    p_scripturi NVARCHAR(255),
    p_age INTEGER,
    p_numberoflogs INTEGER,
    p_querycount INTEGER,
    p_threshold INTEGER,
    p_searchtype BIGINT,
    p_searchscript NVARCHAR(255),
    p_processscript NVARCHAR(255),
    p_retentionrule BIGINT,
    p_batchsize INTEGER,
    p_maxthreads INTEGER,
    p_exclusivemode TINYINT,
    p_syncprincipalsonly TINYINT,
    p_createnewitems TINYINT,
    p_removemissingitems TINYINT,
    p_syncorder INTEGER,
    p_sourceversion BIGINT,
    p_targetversion BIGINT,
    p_copycachesize INTEGER,
    p_enabletransactions TINYINT,
    p_maxschedulerthreads INTEGER,
    p_activationscript LONGVARCHAR,
    PRIMARY KEY (PK)
);

CREATE INDEX IdxCode_500 ON jobs (p_code);

CREATE INDEX sourceversionRelIDX_500 ON jobs (p_sourceversion);

CREATE INDEX targetversionRelIDX_500 ON jobs (p_targetversion);


CREATE CACHED TABLE jobs500sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_500Sn ON jobs500sn (ITEMPK);


CREATE CACHED TABLE jobsearchrestri508sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_508Sn ON jobsearchrestri508sn (ITEMPK);


CREATE CACHED TABLE jobsearchrestriction
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_code NVARCHAR(255),
    p_type BIGINT,
    p_jobpos INTEGER,
    p_job BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX jobRelIDX_508 ON jobsearchrestriction (p_job);

CREATE INDEX jobposPosIDX_508 ON jobsearchrestriction (p_jobpos);


CREATE CACHED TABLE jobslp
(
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    LANGPK BIGINT,
    p_name NVARCHAR(255),
    p_description LONGVARCHAR,
    PRIMARY KEY (ITEMPK, LANGPK)
);


CREATE CACHED TABLE keywords
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_keyword NVARCHAR(255),
    p_language BIGINT,
    p_catalog BIGINT,
    p_catalogversion BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    p_externalid NVARCHAR(255),
    PRIMARY KEY (PK)
);

CREATE INDEX keywordIDX_602 ON keywords (p_keyword);

CREATE INDEX versionIDX_602 ON keywords (p_catalogversion);

CREATE UNIQUE INDEX codeVersionIDX_602 ON keywords (p_keyword, p_catalogversion);

CREATE INDEX extIDX_602 ON keywords (p_externalid);


CREATE CACHED TABLE keywords602sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_602Sn ON keywords602sn (ITEMPK);


CREATE CACHED TABLE languages
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_active TINYINT,
    p_isocode NVARCHAR(255),
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX ISOCode_32 ON languages (p_isocode);


CREATE CACHED TABLE languages32sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_32Sn ON languages32sn (ITEMPK);


CREATE CACHED TABLE languageslp
(
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    LANGPK BIGINT,
    p_name NVARCHAR(255),
    PRIMARY KEY (ITEMPK, LANGPK)
);


CREATE CACHED TABLE links
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    languagepk BIGINT,
    Qualifier NVARCHAR(255),
    SourcePK BIGINT,
    TargetPK BIGINT,
    SequenceNumber INTEGER DEFAULT 0,
    RSequenceNumber INTEGER DEFAULT 0,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX linktarget_7 ON links (TargetPK);

CREATE INDEX qualifier_7 ON links (Qualifier);

CREATE INDEX linksource_7 ON links (SourcePK);

CREATE INDEX seqnr_7 ON links (SequenceNumber);

CREATE INDEX rseqnr_7 ON links (RSequenceNumber);


CREATE CACHED TABLE links7sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    sourcePK BIGINT,
    targetPK BIGINT,
    languagePK BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_7Sn ON links7sn (ITEMPK);

CREATE INDEX IdxsourcePK_7Sn ON links7sn (sourcePK);

CREATE INDEX IdxtargetPK_7Sn ON links7sn (targetPK);

CREATE INDEX IdxlanguagePK_7Sn ON links7sn (languagePK);


CREATE CACHED TABLE maptypes
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_extensionname NVARCHAR(255),
    p_autocreate TINYINT,
    p_generate TINYINT,
    InternalCode NVARCHAR(255),
    p_defaultvalue LONGVARBINARY,
    ArgumentTypePK BIGINT,
    ReturnTypePK BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    InternalCodeLowerCase NVARCHAR(255),
    PRIMARY KEY (PK)
);

CREATE INDEX typecodelowercase_84 ON maptypes (InternalCodeLowerCase);

CREATE INDEX typecode_84 ON maptypes (InternalCode);


CREATE CACHED TABLE maptypes84sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_84Sn ON maptypes84sn (ITEMPK);


CREATE CACHED TABLE maptypeslp
(
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    LANGPK BIGINT,
    p_name NVARCHAR(255),
    p_description LONGVARCHAR,
    PRIMARY KEY (ITEMPK, LANGPK)
);


CREATE CACHED TABLE mediacontainer
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_qualifier NVARCHAR(255),
    p_catalogversion BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX versionIDX_50 ON mediacontainer (p_catalogversion);

CREATE UNIQUE INDEX codeVersionIDX_50 ON mediacontainer (p_qualifier, p_catalogversion);


CREATE CACHED TABLE mediacontainer50sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_50Sn ON mediacontainer50sn (ITEMPK);


CREATE CACHED TABLE mediacontainerlp
(
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    LANGPK BIGINT,
    p_name NVARCHAR(255),
    PRIMARY KEY (ITEMPK, LANGPK)
);


CREATE CACHED TABLE mediacontext
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_qualifier NVARCHAR(255),
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE UNIQUE INDEX qualifierIDX_52 ON mediacontext (p_qualifier);


CREATE CACHED TABLE mediacontext52sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_52Sn ON mediacontext52sn (ITEMPK);


CREATE CACHED TABLE mediacontextlp
(
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    LANGPK BIGINT,
    p_name NVARCHAR(255),
    PRIMARY KEY (ITEMPK, LANGPK)
);


CREATE CACHED TABLE mediafolders
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_qualifier NVARCHAR(255),
    p_path NVARCHAR(255),
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE UNIQUE INDEX qualifierIdx_54 ON mediafolders (p_qualifier);


CREATE CACHED TABLE mediafolders54sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_54Sn ON mediafolders54sn (ITEMPK);


CREATE CACHED TABLE mediaformat
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_qualifier NVARCHAR(255),
    p_externalid NVARCHAR(255),
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE UNIQUE INDEX qualifierIDX_51 ON mediaformat (p_qualifier);


CREATE CACHED TABLE mediaformat51sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_51Sn ON mediaformat51sn (ITEMPK);


CREATE CACHED TABLE mediaformatlp
(
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    LANGPK BIGINT,
    p_name NVARCHAR(255),
    PRIMARY KEY (ITEMPK, LANGPK)
);


CREATE CACHED TABLE mediaformatmapp53sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_53Sn ON mediaformatmapp53sn (ITEMPK);


CREATE CACHED TABLE mediaformatmapping
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_source BIGINT,
    p_target BIGINT,
    p_mediacontext BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX mediacontextRelIDX_53 ON mediaformatmapping (p_mediacontext);


CREATE CACHED TABLE mediaprops
(
    hjmpTS BIGINT,
    ITEMPK BIGINT NOT NULL,
    ITEMTYPEPK BIGINT,
    NAME NVARCHAR(255) NOT NULL,
    LANGPK BIGINT NOT NULL,
    REALNAME NVARCHAR(255),
    TYPE1 INTEGER DEFAULT 0,
    VALUESTRING1 LONGVARCHAR,
    VALUE1 LONGVARBINARY,
    PRIMARY KEY (ITEMPK, NAME, LANGPK)
);

CREATE INDEX realnameidx_mediaprops ON mediaprops (REALNAME);

CREATE INDEX itempk_mediaprops ON mediaprops (ITEMPK);

CREATE INDEX nameidx_mediaprops ON mediaprops (NAME);


CREATE CACHED TABLE medias
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_mime NVARCHAR(255),
    p_size BIGINT,
    p_datapk BIGINT,
    p_location LONGVARCHAR,
    p_locationhash NVARCHAR(255),
    p_realfilename NVARCHAR(255),
    p_code NVARCHAR(255),
    p_internalurl LONGVARCHAR,
    p_description NVARCHAR(255),
    p_alttext NVARCHAR(255),
    p_removable TINYINT,
    p_mediaformat BIGINT,
    p_folder BIGINT,
    p_subfolderpath NVARCHAR(255),
    p_mediacontainer BIGINT,
    p_catalog BIGINT,
    p_catalogversion BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    p_outputmimetype NVARCHAR(255),
    p_inputmimetype NVARCHAR(255),
    p_itemtimestamp TIMESTAMP,
    p_format BIGINT,
    p_sourceitem BIGINT,
    p_fieldseparator SMALLINT,
    p_quotecharacter SMALLINT,
    p_commentcharacter SMALLINT,
    p_encoding BIGINT,
    p_linestoskip INTEGER,
    p_removeonsuccess TINYINT,
    p_zipentry NVARCHAR(255),
    p_extractionid NVARCHAR(255),
    p_scheduledcount INTEGER,
    p_cronjobpos INTEGER,
    p_cronjob BIGINT,
    PRIMARY KEY (PK)
);

CREATE INDEX dataPK_idx_30 ON medias (p_datapk);

CREATE INDEX Media_Code_30 ON medias (p_code);

CREATE INDEX versionIDX_30 ON medias (p_catalogversion);

CREATE UNIQUE INDEX codeVersionIDX_30 ON medias (p_code, p_catalogversion);

CREATE INDEX mediacontainerRelIDX_30 ON medias (p_mediacontainer);

CREATE INDEX sourceitemRelIDX_30 ON medias (p_sourceitem);

CREATE INDEX cronjobRelIDX_30 ON medias (p_cronjob);

CREATE INDEX cronjobposPosIDX_30 ON medias (p_cronjobpos);


CREATE CACHED TABLE medias30sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_30Sn ON medias30sn (ITEMPK);


CREATE CACHED TABLE metainformations
(
    hjmpTS BIGINT,
    PK BIGINT NOT NULL,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    aCLTS BIGINT DEFAULT 0,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    propTS BIGINT DEFAULT 0,
    SystemPK NVARCHAR(255),
    SystemName NVARCHAR(255),
    isInitialized TINYINT DEFAULT 0,
    LicenceID NVARCHAR(255),
    LicenceName NVARCHAR(255),
    LicenceEdition NVARCHAR(255),
    AdminFactor INTEGER DEFAULT 0,
    LicenceExpirationDate TIMESTAMP,
    LicenceSignature NVARCHAR(255),
    PRIMARY KEY (PK)
);


CREATE CACHED TABLE numberseries
(
    hjmpTS BIGINT,
    serieskey NVARCHAR(255) NOT NULL,
    seriestype INTEGER DEFAULT 0,
    currentValue BIGINT,
    template NVARCHAR(255),
    PRIMARY KEY (serieskey)
);


CREATE CACHED TABLE oauthaccesstoke6228sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_6228Sn ON oauthaccesstoke6228sn (ITEMPK);


CREATE CACHED TABLE oauthaccesstoken
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_tokenid NVARCHAR(255),
    p_token LONGVARBINARY,
    p_authenticationid NVARCHAR(255),
    p_client BIGINT,
    p_authentication LONGVARBINARY,
    p_refreshtoken BIGINT,
    p_user BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE UNIQUE INDEX tokenIdIdx_6228 ON oauthaccesstoken (p_tokenid);

CREATE UNIQUE INDEX authenticationIdIdx_6228 ON oauthaccesstoken (p_authenticationid);

CREATE INDEX refreshTokenIdx_6228 ON oauthaccesstoken (p_refreshtoken);

CREATE INDEX userRelIDX_6228 ON oauthaccesstoken (p_user);


CREATE CACHED TABLE oauthauthorizat6231sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_6231Sn ON oauthauthorizat6231sn (ITEMPK);


CREATE CACHED TABLE oauthauthorizationcode
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_code NVARCHAR(255),
    p_authentication LONGVARBINARY,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE UNIQUE INDEX codeIdx_6231 ON oauthauthorizationcode (p_code);


CREATE CACHED TABLE oauthclientdeta6500sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_6500Sn ON oauthclientdeta6500sn (ITEMPK);


CREATE CACHED TABLE oauthclientdetails
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_clientid NVARCHAR(255),
    p_resourceids LONGVARBINARY,
    p_clientsecret NVARCHAR(255),
    p_scope LONGVARBINARY,
    p_authorizedgranttypes LONGVARBINARY,
    p_registeredredirecturi LONGVARBINARY,
    p_authorities LONGVARBINARY,
    p_accesstokenvalidityseconds INTEGER,
    p_refreshtokenvalidityseconds INTEGER,
    p_autoapprove LONGVARBINARY,
    p_disabled TINYINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    p_externalscopeclaimname NVARCHAR(255),
    p_issuer NVARCHAR(255),
    PRIMARY KEY (PK)
);

CREATE UNIQUE INDEX clientIdIdx_6500 ON oauthclientdetails (p_clientid);


CREATE CACHED TABLE oauthrefreshtok6229sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_6229Sn ON oauthrefreshtok6229sn (ITEMPK);


CREATE CACHED TABLE oauthrefreshtoken
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_tokenid NVARCHAR(255),
    p_token LONGVARBINARY,
    p_authentication LONGVARBINARY,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE UNIQUE INDEX tokenIdIdx_6229 ON oauthrefreshtoken (p_tokenid);


CREATE CACHED TABLE openidexternals6520sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_6520Sn ON openidexternals6520sn (ITEMPK);


CREATE CACHED TABLE openidexternalscopes
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_code NVARCHAR(255),
    p_clientdetailsid BIGINT,
    p_permittedprincipals LONGVARCHAR,
    p_scope LONGVARBINARY,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX exScopesClIdIdx_6520 ON openidexternalscopes (p_clientdetailsid);


CREATE CACHED TABLE orderdiscrels
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    languagepk BIGINT,
    Qualifier NVARCHAR(255),
    SourcePK BIGINT,
    TargetPK BIGINT,
    SequenceNumber INTEGER DEFAULT 0,
    RSequenceNumber INTEGER DEFAULT 0,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX linktarget_202 ON orderdiscrels (TargetPK);

CREATE INDEX qualifier_202 ON orderdiscrels (Qualifier);

CREATE INDEX linksource_202 ON orderdiscrels (SourcePK);

CREATE INDEX seqnr_202 ON orderdiscrels (SequenceNumber);

CREATE INDEX rseqnr_202 ON orderdiscrels (RSequenceNumber);


CREATE CACHED TABLE orderdiscrels202sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    sourcePK BIGINT,
    targetPK BIGINT,
    languagePK BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_202Sn ON orderdiscrels202sn (ITEMPK);

CREATE INDEX IdxsourcePK_202Sn ON orderdiscrels202sn (sourcePK);

CREATE INDEX IdxtargetPK_202Sn ON orderdiscrels202sn (targetPK);

CREATE INDEX IdxlanguagePK_202Sn ON orderdiscrels202sn (languagePK);


CREATE CACHED TABLE orderentries
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_baseprice DECIMAL(30,8),
    p_calculated TINYINT,
    p_discountvaluesinternal LONGVARCHAR,
    p_entrynumber INTEGER,
    p_info LONGVARCHAR,
    p_product BIGINT,
    p_quantity DECIMAL(30,8),
    p_taxvaluesinternal NVARCHAR(255),
    p_totalprice DECIMAL(30,8),
    p_unit BIGINT,
    p_giveaway TINYINT,
    p_rejected TINYINT,
    p_entrygroupnumbers LONGVARBINARY,
    p_order BIGINT,
    p_europe1pricefactory_ppg BIGINT,
    p_europe1pricefactory_ptg BIGINT,
    p_europe1pricefactory_pdg BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX oeProd_46 ON orderentries (p_product);

CREATE INDEX oeOrd_46 ON orderentries (p_order);


CREATE CACHED TABLE orderentries46sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_46Sn ON orderentries46sn (ITEMPK);


CREATE CACHED TABLE orderentryprops
(
    hjmpTS BIGINT,
    ITEMPK BIGINT NOT NULL,
    ITEMTYPEPK BIGINT,
    NAME NVARCHAR(255) NOT NULL,
    LANGPK BIGINT NOT NULL,
    REALNAME NVARCHAR(255),
    TYPE1 INTEGER DEFAULT 0,
    VALUESTRING1 LONGVARCHAR,
    VALUE1 LONGVARBINARY,
    PRIMARY KEY (ITEMPK, NAME, LANGPK)
);

CREATE INDEX itempk_orderentryprops ON orderentryprops (ITEMPK);

CREATE INDEX nameidx_orderentryprops ON orderentryprops (NAME);

CREATE INDEX realnameidx_orderentryprops ON orderentryprops (REALNAME);


CREATE CACHED TABLE orderprops
(
    hjmpTS BIGINT,
    ITEMPK BIGINT NOT NULL,
    ITEMTYPEPK BIGINT,
    NAME NVARCHAR(255) NOT NULL,
    LANGPK BIGINT NOT NULL,
    REALNAME NVARCHAR(255),
    TYPE1 INTEGER DEFAULT 0,
    VALUESTRING1 LONGVARCHAR,
    VALUE1 LONGVARBINARY,
    PRIMARY KEY (ITEMPK, NAME, LANGPK)
);

CREATE INDEX itempk_orderprops ON orderprops (ITEMPK);

CREATE INDEX realnameidx_orderprops ON orderprops (REALNAME);

CREATE INDEX nameidx_orderprops ON orderprops (NAME);


CREATE CACHED TABLE orders
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_calculated TINYINT,
    p_code NVARCHAR(255),
    p_currency BIGINT,
    p_deliveryaddress BIGINT,
    p_deliverycost DECIMAL(30,8),
    p_deliverymode BIGINT,
    p_deliverystatus BIGINT,
    p_description NVARCHAR(255),
    p_expirationtime TIMESTAMP,
    p_globaldiscountvaluesinternal LONGVARCHAR,
    p_name NVARCHAR(255),
    p_net TINYINT,
    p_paymentaddress BIGINT,
    p_paymentcost DECIMAL(30,8),
    p_paymentinfo BIGINT,
    p_paymentmode BIGINT,
    p_paymentstatus BIGINT,
    p_status BIGINT,
    p_exportstatus BIGINT,
    p_statusinfo NVARCHAR(255),
    p_totalprice DECIMAL(30,8),
    p_totaldiscounts DECIMAL(30,8),
    p_totaltax DECIMAL(30,8),
    p_totaltaxvaluesinternal LONGVARCHAR,
    p_user BIGINT,
    p_subtotal DECIMAL(30,8),
    p_discountsincludedeliverycost TINYINT,
    p_discountsincludepaymentcost TINYINT,
    p_entrygroups LONGVARBINARY,
    p_europe1pricefactory_udg BIGINT,
    p_europe1pricefactory_upg BIGINT,
    p_europe1pricefactory_utg BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX OrderCode_45 ON orders (p_code);

CREATE INDEX OrderUser_45 ON orders (p_user);


CREATE CACHED TABLE orders45sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_45Sn ON orders45sn (ITEMPK);


CREATE CACHED TABLE parserproperty
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_name NVARCHAR(255),
    p_startexp NVARCHAR(255),
    p_endexp NVARCHAR(255),
    p_parserclass NVARCHAR(255),
    p_translatorconfigurationpos INTEGER,
    p_translatorconfiguration BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX translatorconfigurationRelIDX_13213 ON parserproperty (p_translatorconfiguration);

CREATE INDEX translatorconfigurationposPosIDX_13213 ON parserproperty (p_translatorconfigurationpos);


CREATE CACHED TABLE parserproperty13213sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_13213Sn ON parserproperty13213sn (ITEMPK);


CREATE CACHED TABLE paymentinfos
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_original BIGINT,
    p_code NVARCHAR(255),
    p_duplicate TINYINT,
    p_user BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    p_ccowner NVARCHAR(255),
    p_number NVARCHAR(255),
    p_type BIGINT,
    p_validtomonth NVARCHAR(255),
    p_validtoyear NVARCHAR(255),
    p_validfrommonth NVARCHAR(255),
    p_validfromyear NVARCHAR(255),
    p_bankidnumber NVARCHAR(255),
    p_bank NVARCHAR(255),
    p_accountnumber NVARCHAR(255),
    p_baowner NVARCHAR(255),
    PRIMARY KEY (PK)
);

CREATE INDEX PaymentInfo_User_42 ON paymentinfos (p_user);


CREATE CACHED TABLE paymentinfos42sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_42Sn ON paymentinfos42sn (ITEMPK);


CREATE CACHED TABLE paymentmodes
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_active TINYINT,
    p_code NVARCHAR(255),
    p_paymentinfotype BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    p_net TINYINT,
    PRIMARY KEY (PK)
);


CREATE CACHED TABLE paymentmodes41sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_41Sn ON paymentmodes41sn (ITEMPK);


CREATE CACHED TABLE paymentmodeslp
(
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    LANGPK BIGINT,
    p_description LONGVARCHAR,
    p_name NVARCHAR(255),
    PRIMARY KEY (ITEMPK, LANGPK)
);


CREATE CACHED TABLE pcp2wrtblecvrel
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    languagepk BIGINT,
    Qualifier NVARCHAR(255),
    SourcePK BIGINT,
    TargetPK BIGINT,
    SequenceNumber INTEGER DEFAULT 0,
    RSequenceNumber INTEGER DEFAULT 0,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX linktarget_617 ON pcp2wrtblecvrel (TargetPK);

CREATE INDEX qualifier_617 ON pcp2wrtblecvrel (Qualifier);

CREATE INDEX linksource_617 ON pcp2wrtblecvrel (SourcePK);

CREATE INDEX seqnr_617 ON pcp2wrtblecvrel (SequenceNumber);

CREATE INDEX rseqnr_617 ON pcp2wrtblecvrel (RSequenceNumber);


CREATE CACHED TABLE pcp2wrtblecvrel617sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    sourcePK BIGINT,
    targetPK BIGINT,
    languagePK BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_617Sn ON pcp2wrtblecvrel617sn (ITEMPK);

CREATE INDEX IdxsourcePK_617Sn ON pcp2wrtblecvrel617sn (sourcePK);

CREATE INDEX IdxtargetPK_617Sn ON pcp2wrtblecvrel617sn (targetPK);

CREATE INDEX IdxlanguagePK_617Sn ON pcp2wrtblecvrel617sn (languagePK);


CREATE CACHED TABLE pcpl2rdblecvrel
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    languagepk BIGINT,
    Qualifier NVARCHAR(255),
    SourcePK BIGINT,
    TargetPK BIGINT,
    SequenceNumber INTEGER DEFAULT 0,
    RSequenceNumber INTEGER DEFAULT 0,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX linktarget_618 ON pcpl2rdblecvrel (TargetPK);

CREATE INDEX qualifier_618 ON pcpl2rdblecvrel (Qualifier);

CREATE INDEX linksource_618 ON pcpl2rdblecvrel (SourcePK);

CREATE INDEX seqnr_618 ON pcpl2rdblecvrel (SequenceNumber);

CREATE INDEX rseqnr_618 ON pcpl2rdblecvrel (RSequenceNumber);


CREATE CACHED TABLE pcpl2rdblecvrel618sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    sourcePK BIGINT,
    targetPK BIGINT,
    languagePK BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_618Sn ON pcpl2rdblecvrel618sn (ITEMPK);

CREATE INDEX IdxsourcePK_618Sn ON pcpl2rdblecvrel618sn (sourcePK);

CREATE INDEX IdxtargetPK_618Sn ON pcpl2rdblecvrel618sn (targetPK);

CREATE INDEX IdxlanguagePK_618Sn ON pcpl2rdblecvrel618sn (languagePK);


CREATE CACHED TABLE pendingstepsrel507sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    sourcePK BIGINT,
    targetPK BIGINT,
    languagePK BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_507Sn ON pendingstepsrel507sn (ITEMPK);

CREATE INDEX IdxsourcePK_507Sn ON pendingstepsrel507sn (sourcePK);

CREATE INDEX IdxtargetPK_507Sn ON pendingstepsrel507sn (targetPK);

CREATE INDEX IdxlanguagePK_507Sn ON pendingstepsrel507sn (languagePK);


CREATE CACHED TABLE pendingstepsrelation
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    languagepk BIGINT,
    Qualifier NVARCHAR(255),
    SourcePK BIGINT,
    TargetPK BIGINT,
    SequenceNumber INTEGER DEFAULT 0,
    RSequenceNumber INTEGER DEFAULT 0,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX linktarget_507 ON pendingstepsrelation (TargetPK);

CREATE INDEX qualifier_507 ON pendingstepsrelation (Qualifier);

CREATE INDEX linksource_507 ON pendingstepsrelation (SourcePK);

CREATE INDEX seqnr_507 ON pendingstepsrelation (SequenceNumber);

CREATE INDEX rseqnr_507 ON pendingstepsrelation (RSequenceNumber);


CREATE CACHED TABLE pgrels
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    languagepk BIGINT,
    Qualifier NVARCHAR(255),
    SourcePK BIGINT,
    TargetPK BIGINT,
    SequenceNumber INTEGER DEFAULT 0,
    RSequenceNumber INTEGER DEFAULT 0,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX linktarget_201 ON pgrels (TargetPK);

CREATE INDEX qualifier_201 ON pgrels (Qualifier);

CREATE INDEX linksource_201 ON pgrels (SourcePK);

CREATE INDEX seqnr_201 ON pgrels (SequenceNumber);

CREATE INDEX rseqnr_201 ON pgrels (RSequenceNumber);


CREATE CACHED TABLE pgrels201sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    sourcePK BIGINT,
    targetPK BIGINT,
    languagePK BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_201Sn ON pgrels201sn (ITEMPK);

CREATE INDEX IdxsourcePK_201Sn ON pgrels201sn (sourcePK);

CREATE INDEX IdxtargetPK_201Sn ON pgrels201sn (targetPK);

CREATE INDEX IdxlanguagePK_201Sn ON pgrels201sn (languagePK);


CREATE CACHED TABLE previewtickets
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_previewcatalogversion BIGINT,
    p_validto TIMESTAMP,
    p_createdby BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);


CREATE CACHED TABLE previewtickets667sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_667Sn ON previewtickets667sn (ITEMPK);


CREATE CACHED TABLE pricerows
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_product BIGINT,
    p_pg BIGINT,
    p_productmatchqualifier BIGINT,
    p_starttime TIMESTAMP,
    p_endtime TIMESTAMP,
    p_user BIGINT,
    p_ug BIGINT,
    p_usermatchqualifier BIGINT,
    p_productid NVARCHAR(255),
    p_catalogversion BIGINT,
    p_matchvalue INTEGER,
    p_currency BIGINT,
    p_minqtd BIGINT,
    p_net TINYINT,
    p_price DOUBLE,
    p_unit BIGINT,
    p_unitfactor INTEGER,
    p_giveawayprice TINYINT,
    p_channel BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX MatchIndexP_1055 ON pricerows (p_productmatchqualifier);

CREATE INDEX MatchIndexU_1055 ON pricerows (p_usermatchqualifier);

CREATE INDEX PIdx_1055 ON pricerows (p_product);

CREATE INDEX UIdx_1055 ON pricerows (p_user);

CREATE INDEX PGIdx_1055 ON pricerows (p_pg);

CREATE INDEX UGIdx_1055 ON pricerows (p_ug);

CREATE INDEX ProductIdIdx_1055 ON pricerows (p_productid);

CREATE INDEX versionIDX_1055 ON pricerows (p_catalogversion);


CREATE CACHED TABLE pricerows1055sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_1055Sn ON pricerows1055sn (ITEMPK);


CREATE CACHED TABLE processedstepsr506sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    sourcePK BIGINT,
    targetPK BIGINT,
    languagePK BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_506Sn ON processedstepsr506sn (ITEMPK);

CREATE INDEX IdxsourcePK_506Sn ON processedstepsr506sn (sourcePK);

CREATE INDEX IdxtargetPK_506Sn ON processedstepsr506sn (targetPK);

CREATE INDEX IdxlanguagePK_506Sn ON processedstepsr506sn (languagePK);


CREATE CACHED TABLE processedstepsrelation
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    languagepk BIGINT,
    Qualifier NVARCHAR(255),
    SourcePK BIGINT,
    TargetPK BIGINT,
    SequenceNumber INTEGER DEFAULT 0,
    RSequenceNumber INTEGER DEFAULT 0,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX linktarget_506 ON processedstepsrelation (TargetPK);

CREATE INDEX qualifier_506 ON processedstepsrelation (Qualifier);

CREATE INDEX linksource_506 ON processedstepsrelation (SourcePK);

CREATE INDEX seqnr_506 ON processedstepsrelation (SequenceNumber);

CREATE INDEX rseqnr_506 ON processedstepsrelation (RSequenceNumber);


CREATE CACHED TABLE processes
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_code NVARCHAR(255),
    p_processdefinitionname NVARCHAR(255),
    p_processdefinitionversion NVARCHAR(255),
    p_state BIGINT,
    p_endmessage LONGVARCHAR,
    p_user BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE UNIQUE INDEX ProcessengineProcess_name_idx_32766 ON processes (p_code);


CREATE CACHED TABLE processes32766sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_32766Sn ON processes32766sn (ITEMPK);


CREATE CACHED TABLE processparamete32764sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_32764Sn ON processparamete32764sn (ITEMPK);


CREATE CACHED TABLE processparameters
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_name NVARCHAR(255),
    p_value LONGVARBINARY,
    p_process BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE UNIQUE INDEX BusinessProcessParameter_idx_32764 ON processparameters (p_process, p_name);

CREATE INDEX processRelIDX_32764 ON processparameters (p_process);


CREATE CACHED TABLE prod2keywordrel
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    languagepk BIGINT,
    Qualifier NVARCHAR(255),
    SourcePK BIGINT,
    TargetPK BIGINT,
    SequenceNumber INTEGER DEFAULT 0,
    RSequenceNumber INTEGER DEFAULT 0,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX linktarget_604 ON prod2keywordrel (TargetPK);

CREATE INDEX qualifier_604 ON prod2keywordrel (Qualifier);

CREATE INDEX linksource_604 ON prod2keywordrel (SourcePK);

CREATE INDEX seqnr_604 ON prod2keywordrel (SequenceNumber);

CREATE INDEX rseqnr_604 ON prod2keywordrel (RSequenceNumber);


CREATE CACHED TABLE prod2keywordrel604sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    sourcePK BIGINT,
    targetPK BIGINT,
    languagePK BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_604Sn ON prod2keywordrel604sn (ITEMPK);

CREATE INDEX IdxsourcePK_604Sn ON prod2keywordrel604sn (sourcePK);

CREATE INDEX IdxtargetPK_604Sn ON prod2keywordrel604sn (targetPK);

CREATE INDEX IdxlanguagePK_604Sn ON prod2keywordrel604sn (languagePK);


CREATE CACHED TABLE productfeatures
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_qualifier NVARCHAR(255),
    p_classificationattributeassig BIGINT,
    p_language BIGINT,
    p_valueposition INTEGER,
    p_featureposition INTEGER,
    p_valuetype INTEGER,
    p_stringvalue LONGVARCHAR,
    p_booleanvalue TINYINT,
    p_numbervalue DECIMAL(30,8),
    p_rawvalue LONGVARBINARY,
    p_unit BIGINT,
    p_valuedetails NVARCHAR(255),
    p_description NVARCHAR(255),
    p_productpos INTEGER,
    p_product BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX featureIDX_611 ON productfeatures (p_product);

CREATE INDEX featureIDX2_611 ON productfeatures (p_qualifier);

CREATE INDEX featureIDX3_611 ON productfeatures (p_classificationattributeassig);

CREATE INDEX productposPosIDX_611 ON productfeatures (p_productpos);


CREATE CACHED TABLE productfeatures611sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_611Sn ON productfeatures611sn (ITEMPK);


CREATE CACHED TABLE productprops
(
    hjmpTS BIGINT,
    ITEMPK BIGINT NOT NULL,
    ITEMTYPEPK BIGINT,
    NAME NVARCHAR(255) NOT NULL,
    LANGPK BIGINT NOT NULL,
    REALNAME NVARCHAR(255),
    TYPE1 INTEGER DEFAULT 0,
    VALUESTRING1 LONGVARCHAR,
    VALUE1 LONGVARBINARY,
    PRIMARY KEY (ITEMPK, NAME, LANGPK)
);

CREATE INDEX itempk_productprops ON productprops (ITEMPK);

CREATE INDEX realnameidx_productprops ON productprops (REALNAME);

CREATE INDEX nameidx_productprops ON productprops (NAME);


CREATE CACHED TABLE productreferenc606sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_606Sn ON productreferenc606sn (ITEMPK);


CREATE CACHED TABLE productreferences
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_qualifier NVARCHAR(255),
    p_target BIGINT,
    p_quantity INTEGER,
    p_referencetype BIGINT,
    p_icon BIGINT,
    p_preselected TINYINT,
    p_active TINYINT,
    p_sourcepos INTEGER,
    p_source BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX targetIDX_606 ON productreferences (p_target);

CREATE INDEX qualifierIDX_606 ON productreferences (p_qualifier);

CREATE INDEX sourceRelIDX_606 ON productreferences (p_source);

CREATE INDEX sourceposPosIDX_606 ON productreferences (p_sourcepos);


CREATE CACHED TABLE productreferenceslp
(
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    LANGPK BIGINT,
    p_description NVARCHAR(255),
    PRIMARY KEY (ITEMPK, LANGPK)
);


CREATE CACHED TABLE products
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_code NVARCHAR(255),
    p_unit BIGINT,
    p_thumbnail BIGINT,
    p_picture BIGINT,
    p_catalog BIGINT,
    p_catalogversion BIGINT,
    p_onlinedate TIMESTAMP,
    p_offlinedate TIMESTAMP,
    p_ean NVARCHAR(255),
    p_supplieralternativeaid NVARCHAR(255),
    p_buyerids LONGVARBINARY,
    p_manufactureraid NVARCHAR(255),
    p_manufacturername NVARCHAR(255),
    p_erpgroupbuyer NVARCHAR(255),
    p_erpgroupsupplier NVARCHAR(255),
    p_deliverytime DOUBLE,
    p_specialtreatmentclasses LONGVARBINARY,
    p_order INTEGER,
    p_approvalstatus BIGINT,
    p_contentunit BIGINT,
    p_numbercontentunits DOUBLE,
    p_minorderquantity INTEGER,
    p_maxorderquantity INTEGER,
    p_orderquantityinterval INTEGER,
    p_pricequantity DOUBLE,
    p_normal LONGVARCHAR,
    p_thumbnails LONGVARCHAR,
    p_detail LONGVARCHAR,
    p_logo LONGVARCHAR,
    p_data_sheet LONGVARCHAR,
    p_others LONGVARCHAR,
    p_startlinenumber INTEGER,
    p_endlinenumber INTEGER,
    p_varianttype BIGINT,
    p_europe1pricefactory_ppg BIGINT,
    p_europe1pricefactory_ptg BIGINT,
    p_europe1pricefactory_pdg BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    p_baseproduct BIGINT,
    PRIMARY KEY (PK)
);

CREATE INDEX Product_Code_1 ON products (p_code);

CREATE INDEX catalogIDX_1 ON products (p_catalog);

CREATE INDEX visibilityIDX_1 ON products (p_approvalstatus, p_onlinedate, p_offlinedate);

CREATE UNIQUE INDEX codeVersionIDX_1 ON products (p_code, p_catalogversion);

CREATE INDEX versionIDX_1 ON products (p_catalogversion);

CREATE INDEX baseproductRelIDX_1 ON products (p_baseproduct);


CREATE CACHED TABLE products1sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_1Sn ON products1sn (ITEMPK);


CREATE CACHED TABLE productslp
(
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    LANGPK BIGINT,
    p_name NVARCHAR(255),
    p_description LONGVARCHAR,
    p_manufacturertypedescription NVARCHAR(255),
    p_segment NVARCHAR(255),
    p_articlestatus LONGVARBINARY,
    PRIMARY KEY (ITEMPK, LANGPK)
);


CREATE CACHED TABLE props
(
    hjmpTS BIGINT,
    ITEMPK BIGINT NOT NULL,
    ITEMTYPEPK BIGINT,
    NAME NVARCHAR(255) NOT NULL,
    LANGPK BIGINT NOT NULL,
    REALNAME NVARCHAR(255),
    TYPE1 INTEGER DEFAULT 0,
    VALUESTRING1 LONGVARCHAR,
    VALUE1 LONGVARBINARY,
    PRIMARY KEY (ITEMPK, NAME, LANGPK)
);

CREATE INDEX nameidx_props ON props (NAME);

CREATE INDEX realnameidx_props ON props (REALNAME);

CREATE INDEX itempk_props ON props (ITEMPK);


CREATE CACHED TABLE quoteentries
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_baseprice DECIMAL(30,8),
    p_calculated TINYINT,
    p_discountvaluesinternal LONGVARCHAR,
    p_entrynumber INTEGER,
    p_info LONGVARCHAR,
    p_product BIGINT,
    p_quantity DECIMAL(30,8),
    p_taxvaluesinternal NVARCHAR(255),
    p_totalprice DECIMAL(30,8),
    p_unit BIGINT,
    p_giveaway TINYINT,
    p_rejected TINYINT,
    p_entrygroupnumbers LONGVARBINARY,
    p_order BIGINT,
    p_europe1pricefactory_ppg BIGINT,
    p_europe1pricefactory_ptg BIGINT,
    p_europe1pricefactory_pdg BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX oeProd_61 ON quoteentries (p_product);

CREATE INDEX oeOrd_61 ON quoteentries (p_order);


CREATE CACHED TABLE quoteentries61sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_61Sn ON quoteentries61sn (ITEMPK);


CREATE CACHED TABLE quoteentryprops
(
    hjmpTS BIGINT,
    ITEMPK BIGINT NOT NULL,
    ITEMTYPEPK BIGINT,
    NAME NVARCHAR(255) NOT NULL,
    LANGPK BIGINT NOT NULL,
    REALNAME NVARCHAR(255),
    TYPE1 INTEGER DEFAULT 0,
    VALUESTRING1 LONGVARCHAR,
    VALUE1 LONGVARBINARY,
    PRIMARY KEY (ITEMPK, NAME, LANGPK)
);

CREATE INDEX nameidx_quoteentryprops ON quoteentryprops (NAME);

CREATE INDEX itempk_quoteentryprops ON quoteentryprops (ITEMPK);

CREATE INDEX realnameidx_quoteentryprops ON quoteentryprops (REALNAME);


CREATE CACHED TABLE quoteprops
(
    hjmpTS BIGINT,
    ITEMPK BIGINT NOT NULL,
    ITEMTYPEPK BIGINT,
    NAME NVARCHAR(255) NOT NULL,
    LANGPK BIGINT NOT NULL,
    REALNAME NVARCHAR(255),
    TYPE1 INTEGER DEFAULT 0,
    VALUESTRING1 LONGVARCHAR,
    VALUE1 LONGVARBINARY,
    PRIMARY KEY (ITEMPK, NAME, LANGPK)
);

CREATE INDEX nameidx_quoteprops ON quoteprops (NAME);

CREATE INDEX itempk_quoteprops ON quoteprops (ITEMPK);

CREATE INDEX realnameidx_quoteprops ON quoteprops (REALNAME);


CREATE CACHED TABLE quotes
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_calculated TINYINT,
    p_code NVARCHAR(255),
    p_currency BIGINT,
    p_deliveryaddress BIGINT,
    p_deliverycost DECIMAL(30,8),
    p_deliverymode BIGINT,
    p_deliverystatus BIGINT,
    p_description NVARCHAR(255),
    p_expirationtime TIMESTAMP,
    p_globaldiscountvaluesinternal LONGVARCHAR,
    p_name NVARCHAR(255),
    p_net TINYINT,
    p_paymentaddress BIGINT,
    p_paymentcost DECIMAL(30,8),
    p_paymentinfo BIGINT,
    p_paymentmode BIGINT,
    p_paymentstatus BIGINT,
    p_status BIGINT,
    p_exportstatus BIGINT,
    p_statusinfo NVARCHAR(255),
    p_totalprice DECIMAL(30,8),
    p_totaldiscounts DECIMAL(30,8),
    p_totaltax DECIMAL(30,8),
    p_totaltaxvaluesinternal LONGVARCHAR,
    p_user BIGINT,
    p_subtotal DECIMAL(30,8),
    p_discountsincludedeliverycost TINYINT,
    p_discountsincludepaymentcost TINYINT,
    p_entrygroups LONGVARBINARY,
    p_europe1pricefactory_udg BIGINT,
    p_europe1pricefactory_upg BIGINT,
    p_europe1pricefactory_utg BIGINT,
    p_version INTEGER,
    p_state BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX OrderCode_60 ON quotes (p_code);

CREATE INDEX OrderUser_60 ON quotes (p_user);

CREATE UNIQUE INDEX version_idx_60 ON quotes (p_code, p_version);


CREATE CACHED TABLE quotes60sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_60Sn ON quotes60sn (ITEMPK);


CREATE CACHED TABLE regions
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_active TINYINT,
    p_isocode NVARCHAR(255),
    p_country BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX ISOCode_35 ON regions (p_isocode);

CREATE INDEX Region_Country_35 ON regions (p_country);


CREATE CACHED TABLE regions35sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_35Sn ON regions35sn (ITEMPK);


CREATE CACHED TABLE regionslp
(
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    LANGPK BIGINT,
    p_name NVARCHAR(255),
    PRIMARY KEY (ITEMPK, LANGPK)
);


CREATE CACHED TABLE renderersproper13212sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_13212Sn ON renderersproper13212sn (ITEMPK);


CREATE CACHED TABLE renderersproperty
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_key NVARCHAR(255),
    p_value NVARCHAR(255),
    p_translatorconfigurationpos INTEGER,
    p_translatorconfiguration BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX translatorconfigurationRelIDX_13212 ON renderersproperty (p_translatorconfiguration);

CREATE INDEX translatorconfigurationposPosIDX_13212 ON renderersproperty (p_translatorconfigurationpos);


CREATE CACHED TABLE renderertemplat13215sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_13215Sn ON renderertemplat13215sn (ITEMPK);


CREATE CACHED TABLE renderertemplate
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_code NVARCHAR(255),
    p_defaultcontent BIGINT,
    p_contextclass NVARCHAR(255),
    p_outputmimetype NVARCHAR(255),
    p_renderertype BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);


CREATE CACHED TABLE renderertemplatelp
(
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    LANGPK BIGINT,
    p_description NVARCHAR(255),
    p_content BIGINT,
    PRIMARY KEY (ITEMPK, LANGPK)
);


CREATE CACHED TABLE retentionrule
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_code NVARCHAR(255),
    p_actionreference NVARCHAR(255),
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    p_retirementitemtype BIGINT,
    p_retirementdateattribute BIGINT,
    p_retentiontimeseconds BIGINT,
    p_itemfilterexpression NVARCHAR(255),
    p_retirementdateexpression NVARCHAR(255),
    p_searchquery LONGVARCHAR,
    p_queryparameters LONGVARBINARY,
    PRIMARY KEY (PK)
);


CREATE CACHED TABLE retentionrule983sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_983Sn ON retentionrule983sn (ITEMPK);


CREATE CACHED TABLE savedqueries
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_code NVARCHAR(255),
    p_paramtypes LONGVARCHAR,
    p_params LONGVARBINARY,
    p_query LONGVARCHAR,
    p_resulttype BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);


CREATE CACHED TABLE savedqueries150sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_150Sn ON savedqueries150sn (ITEMPK);


CREATE CACHED TABLE savedquerieslp
(
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    LANGPK BIGINT,
    p_name NVARCHAR(255),
    p_description NVARCHAR(255),
    PRIMARY KEY (ITEMPK, LANGPK)
);


CREATE CACHED TABLE savedvalueentry
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_modifiedattribute NVARCHAR(255),
    p_oldvalueattributedescriptor BIGINT,
    p_parent BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX parentRelIDX_335 ON savedvalueentry (p_parent);


CREATE CACHED TABLE savedvalueentry335sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_335Sn ON savedvalueentry335sn (ITEMPK);


CREATE CACHED TABLE savedvalues
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_modifieditemtype BIGINT,
    p_timestamp TIMESTAMP,
    p_user BIGINT,
    p_modificationtype BIGINT,
    p_modifieditempos INTEGER,
    p_modifieditem BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX savedvalmoditem_334 ON savedvalues (p_modifieditem);

CREATE INDEX modifieditemposPosIDX_334 ON savedvalues (p_modifieditempos);


CREATE CACHED TABLE savedvalues334sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_334Sn ON savedvalues334sn (ITEMPK);


CREATE CACHED TABLE scripts
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_code NVARCHAR(255),
    p_active TINYINT,
    p_checksum NVARCHAR(255),
    p_content LONGVARCHAR,
    p_version BIGINT,
    p_scripttype BIGINT,
    p_autodisabling TINYINT,
    p_disabled TINYINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE UNIQUE INDEX codeVersionActiveIDX_100 ON scripts (p_code, p_version, p_active);


CREATE CACHED TABLE scripts100sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_100Sn ON scripts100sn (ITEMPK);


CREATE CACHED TABLE scriptslp
(
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    LANGPK BIGINT,
    p_description LONGVARCHAR,
    PRIMARY KEY (ITEMPK, LANGPK)
);


CREATE CACHED TABLE searchrestricti90sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_90Sn ON searchrestricti90sn (ITEMPK);


CREATE CACHED TABLE searchrestrictions
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_extensionname NVARCHAR(255),
    p_autocreate TINYINT,
    p_generate TINYINT,
    p_code NVARCHAR(255),
    p_active TINYINT,
    principal BIGINT,
    query LONGVARCHAR,
    RestrictedType BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX restrtype_90 ON searchrestrictions (RestrictedType);

CREATE INDEX principal_90 ON searchrestrictions (principal);


CREATE CACHED TABLE searchrestrictionslp
(
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    LANGPK BIGINT,
    p_name NVARCHAR(255),
    PRIMARY KEY (ITEMPK, LANGPK)
);


CREATE CACHED TABLE slactions
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_code NVARCHAR(255),
    p_type BIGINT,
    p_target NVARCHAR(255),
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);


CREATE CACHED TABLE slactions1000sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_1000Sn ON slactions1000sn (ITEMPK);


CREATE CACHED TABLE stdpaymmodevals
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_currency BIGINT,
    p_value DOUBLE,
    p_paymentmode BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX paymentmodeRelIDX_1022 ON stdpaymmodevals (p_paymentmode);


CREATE CACHED TABLE stdpaymmodevals1022sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_1022Sn ON stdpaymmodevals1022sn (ITEMPK);


CREATE CACHED TABLE steps
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_batchjob BIGINT,
    p_code NVARCHAR(255),
    p_sequencenumber INTEGER,
    p_synchronous TINYINT,
    p_errormode BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX IdxBatchJob_503 ON steps (p_batchjob);

CREATE INDEX seqNrIDX_503 ON steps (p_sequencenumber);


CREATE CACHED TABLE steps503sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_503Sn ON steps503sn (ITEMPK);


CREATE CACHED TABLE storedhttpsessi121sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_121Sn ON storedhttpsessi121sn (ITEMPK);


CREATE CACHED TABLE storedhttpsessions
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_sessionid NVARCHAR(255),
    p_clusterid INTEGER,
    p_extension NVARCHAR(255),
    p_contextroot NVARCHAR(255),
    p_serializedsession LONGVARBINARY,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE UNIQUE INDEX sessionIdIdx_121 ON storedhttpsessions (p_sessionid);


CREATE CACHED TABLE synattcfg
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_syncjob BIGINT,
    p_attributedescriptor BIGINT,
    p_includedinsync TINYINT,
    p_copybyvalue TINYINT,
    p_untranslatable TINYINT,
    p_translatevalue TINYINT,
    p_presetvalue LONGVARBINARY,
    p_partiallytranslatable TINYINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX jobIdx_620 ON synattcfg (p_syncjob);

CREATE INDEX attrIdx_620 ON synattcfg (p_attributedescriptor);


CREATE CACHED TABLE synattcfg620sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_620Sn ON synattcfg620sn (ITEMPK);


CREATE CACHED TABLE syncjob2langrel
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    languagepk BIGINT,
    Qualifier NVARCHAR(255),
    SourcePK BIGINT,
    TargetPK BIGINT,
    SequenceNumber INTEGER DEFAULT 0,
    RSequenceNumber INTEGER DEFAULT 0,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX linktarget_622 ON syncjob2langrel (TargetPK);

CREATE INDEX qualifier_622 ON syncjob2langrel (Qualifier);

CREATE INDEX linksource_622 ON syncjob2langrel (SourcePK);

CREATE INDEX seqnr_622 ON syncjob2langrel (SequenceNumber);

CREATE INDEX rseqnr_622 ON syncjob2langrel (RSequenceNumber);


CREATE CACHED TABLE syncjob2langrel622sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    sourcePK BIGINT,
    targetPK BIGINT,
    languagePK BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_622Sn ON syncjob2langrel622sn (ITEMPK);

CREATE INDEX IdxsourcePK_622Sn ON syncjob2langrel622sn (sourcePK);

CREATE INDEX IdxtargetPK_622Sn ON syncjob2langrel622sn (targetPK);

CREATE INDEX IdxlanguagePK_622Sn ON syncjob2langrel622sn (languagePK);


CREATE CACHED TABLE syncjob2pcplrel
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    languagepk BIGINT,
    Qualifier NVARCHAR(255),
    SourcePK BIGINT,
    TargetPK BIGINT,
    SequenceNumber INTEGER DEFAULT 0,
    RSequenceNumber INTEGER DEFAULT 0,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX linktarget_623 ON syncjob2pcplrel (TargetPK);

CREATE INDEX qualifier_623 ON syncjob2pcplrel (Qualifier);

CREATE INDEX linksource_623 ON syncjob2pcplrel (SourcePK);

CREATE INDEX seqnr_623 ON syncjob2pcplrel (SequenceNumber);

CREATE INDEX rseqnr_623 ON syncjob2pcplrel (RSequenceNumber);


CREATE CACHED TABLE syncjob2pcplrel623sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    sourcePK BIGINT,
    targetPK BIGINT,
    languagePK BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_623Sn ON syncjob2pcplrel623sn (ITEMPK);

CREATE INDEX IdxsourcePK_623Sn ON syncjob2pcplrel623sn (sourcePK);

CREATE INDEX IdxtargetPK_623Sn ON syncjob2pcplrel623sn (targetPK);

CREATE INDEX IdxlanguagePK_623Sn ON syncjob2pcplrel623sn (languagePK);


CREATE CACHED TABLE syncjob2typerel
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    languagepk BIGINT,
    Qualifier NVARCHAR(255),
    SourcePK BIGINT,
    TargetPK BIGINT,
    SequenceNumber INTEGER DEFAULT 0,
    RSequenceNumber INTEGER DEFAULT 0,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX linktarget_621 ON syncjob2typerel (TargetPK);

CREATE INDEX qualifier_621 ON syncjob2typerel (Qualifier);

CREATE INDEX linksource_621 ON syncjob2typerel (SourcePK);

CREATE INDEX seqnr_621 ON syncjob2typerel (SequenceNumber);

CREATE INDEX rseqnr_621 ON syncjob2typerel (RSequenceNumber);


CREATE CACHED TABLE syncjob2typerel621sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    sourcePK BIGINT,
    targetPK BIGINT,
    languagePK BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_621Sn ON syncjob2typerel621sn (ITEMPK);

CREATE INDEX IdxsourcePK_621Sn ON syncjob2typerel621sn (sourcePK);

CREATE INDEX IdxtargetPK_621Sn ON syncjob2typerel621sn (targetPK);

CREATE INDEX IdxlanguagePK_621Sn ON syncjob2typerel621sn (languagePK);


CREATE CACHED TABLE systemsetupaudi120sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_120Sn ON systemsetupaudi120sn (ITEMPK);


CREATE CACHED TABLE systemsetupaudit
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_hash NVARCHAR(255),
    p_extensionname NVARCHAR(255),
    p_required TINYINT,
    p_patch TINYINT,
    p_user BIGINT,
    p_name NVARCHAR(255),
    p_classname NVARCHAR(255),
    p_methodname NVARCHAR(255),
    p_description NVARCHAR(255),
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE UNIQUE INDEX codeIdx_120 ON systemsetupaudit (p_hash);


CREATE CACHED TABLE taskconditions
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_uniqueid NVARCHAR(255),
    p_expirationtimemillis BIGINT,
    p_processeddate TIMESTAMP,
    p_fulfilled TINYINT,
    p_consumed TINYINT,
    p_choice NVARCHAR(255),
    p_counter INTEGER,
    p_task BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE UNIQUE INDEX Cond_idx_951 ON taskconditions (p_uniqueid, p_consumed);

CREATE INDEX Cond_match_idx_951 ON taskconditions (p_task, p_fulfilled, TypePkString);

CREATE INDEX taskRelIDX_951 ON taskconditions (p_task);


CREATE CACHED TABLE taskconditions951sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_951Sn ON taskconditions951sn (ITEMPK);


CREATE CACHED TABLE tasklogs
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_returncode NVARCHAR(255),
    p_startdate TIMESTAMP,
    p_enddate TIMESTAMP,
    p_actionid NVARCHAR(255),
    p_clusterid INTEGER,
    p_logmessages LONGVARCHAR,
    p_process BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX processRelIDX_32767 ON tasklogs (p_process);


CREATE CACHED TABLE tasklogs32767sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_32767Sn ON tasklogs32767sn (ITEMPK);


CREATE CACHED TABLE tasks
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_runnerbean NVARCHAR(255),
    p_executiontimemillis BIGINT,
    p_executionhourmillis BIGINT,
    p_failed TINYINT,
    p_expirationtimemillis BIGINT,
    p_context LONGVARBINARY,
    p_contextitem BIGINT,
    p_nodeid INTEGER,
    p_nodegroup NVARCHAR(255),
    p_retry INTEGER,
    p_runningonclusternode INTEGER,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    p_action NVARCHAR(255),
    p_process BIGINT,
    p_scripturi NVARCHAR(255),
    p_trigger BIGINT,
    p_state BIGINT,
    p_conditionid NVARCHAR(255),
    PRIMARY KEY (PK)
);

CREATE INDEX Task_dr_idx_950 ON tasks (p_runningonclusternode, p_expirationtimemillis, p_nodeid);

CREATE INDEX Task_dr2_idx_950 ON tasks (p_failed, p_runningonclusternode, p_executionhourmillis, TypePkString, p_nodeid);

CREATE INDEX processRelIDX_950 ON tasks (p_process);


CREATE CACHED TABLE tasks950sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_950Sn ON tasks950sn (ITEMPK);


CREATE CACHED TABLE taxes
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_code NVARCHAR(255),
    p_value DOUBLE,
    p_currency BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);


CREATE CACHED TABLE taxes47sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_47Sn ON taxes47sn (ITEMPK);


CREATE CACHED TABLE taxeslp
(
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    LANGPK BIGINT,
    p_name NVARCHAR(255),
    PRIMARY KEY (ITEMPK, LANGPK)
);


CREATE CACHED TABLE taxrows
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_product BIGINT,
    p_pg BIGINT,
    p_productmatchqualifier BIGINT,
    p_starttime TIMESTAMP,
    p_endtime TIMESTAMP,
    p_user BIGINT,
    p_ug BIGINT,
    p_usermatchqualifier BIGINT,
    p_productid NVARCHAR(255),
    p_catalogversion BIGINT,
    p_currency BIGINT,
    p_tax BIGINT,
    p_value DOUBLE,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX MatchIndexP_1054 ON taxrows (p_productmatchqualifier);

CREATE INDEX MatchIndexU_1054 ON taxrows (p_usermatchqualifier);

CREATE INDEX PIdx_1054 ON taxrows (p_product);

CREATE INDEX UIdx_1054 ON taxrows (p_user);

CREATE INDEX PGIdx_1054 ON taxrows (p_pg);

CREATE INDEX UGIdx_1054 ON taxrows (p_ug);

CREATE INDEX ProductIdIdx_1054 ON taxrows (p_productid);

CREATE INDEX versionIDX_1054 ON taxrows (p_catalogversion);


CREATE CACHED TABLE taxrows1054sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_1054Sn ON taxrows1054sn (ITEMPK);


CREATE CACHED TABLE testitem
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    fieldA NVARCHAR(255),
    fieldB NVARCHAR(255),
    fieldBoolean TINYINT,
    fieldByte SMALLINT,
    fieldCharacter SMALLINT,
    fieldDate TIMESTAMP,
    fieldDouble DOUBLE,
    fieldFloat FLOAT,
    fieldInteger INTEGER,
    fieldLong BIGINT,
    fieldPrimitiveBoolean TINYINT DEFAULT 0,
    fieldPrimitiveByte SMALLINT DEFAULT 0,
    fieldPrimitiveChar SMALLINT,
    fieldPrimitiveDouble DOUBLE DEFAULT 0,
    fieldPrimitiveFloat FLOAT DEFAULT 0,
    fieldPrimitiveInteger INTEGER DEFAULT 0,
    fieldPrimitiveLong BIGINT DEFAULT 0,
    fieldPrimitiveShort SMALLINT,
    fieldSerializable LONGVARBINARY,
    fieldString NVARCHAR(255),
    fieldLongString LONGVARCHAR,
    p_testproperty0 NVARCHAR(255),
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    fieldJson LONGVARCHAR,
    fieldBigDecimal DECIMAL(30,8),
    p_testproperty1 INTEGER,
    p_foo NVARCHAR(255),
    p_bar NVARCHAR(255),
    p_xxx NVARCHAR(255),
    p_itemtypetwo BIGINT,
    p_itemstypetwo LONGVARCHAR,
    PRIMARY KEY (PK)
);


CREATE CACHED TABLE testitem25sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_25Sn ON testitem25sn (ITEMPK);


CREATE CACHED TABLE testitemlp
(
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    LANGPK BIGINT,
    p_testproperty2 NVARCHAR(255),
    PRIMARY KEY (ITEMPK, LANGPK)
);


CREATE CACHED TABLE titles
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_code NVARCHAR(255),
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX codeIdx_24 ON titles (p_code);


CREATE CACHED TABLE titles24sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_24Sn ON titles24sn (ITEMPK);


CREATE CACHED TABLE titleslp
(
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    LANGPK BIGINT,
    p_name NVARCHAR(255),
    PRIMARY KEY (ITEMPK, LANGPK)
);


CREATE CACHED TABLE triggerscj
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_active TINYINT,
    p_second INTEGER,
    p_minute INTEGER,
    p_hour INTEGER,
    p_day INTEGER,
    p_month INTEGER,
    p_year INTEGER,
    p_relative TINYINT,
    p_daysofweek LONGVARCHAR,
    p_weekinterval INTEGER,
    p_daterange LONGVARBINARY,
    p_activationtime TIMESTAMP,
    p_cronexpression NVARCHAR(255),
    p_maxacceptabledelay INTEGER,
    p_job BIGINT,
    p_cronjob BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX IdxCronJob_502 ON triggerscj (p_cronjob);

CREATE INDEX IdxActive_502 ON triggerscj (p_active);

CREATE INDEX jobRelIDX_502 ON triggerscj (p_job);


CREATE CACHED TABLE triggerscj502sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_502Sn ON triggerscj502sn (ITEMPK);


CREATE CACHED TABLE typesystemprops
(
    hjmpTS BIGINT,
    ITEMPK BIGINT NOT NULL,
    ITEMTYPEPK BIGINT,
    NAME NVARCHAR(255) NOT NULL,
    LANGPK BIGINT NOT NULL,
    REALNAME NVARCHAR(255),
    TYPE1 INTEGER DEFAULT 0,
    VALUESTRING1 LONGVARCHAR,
    VALUE1 LONGVARBINARY,
    PRIMARY KEY (ITEMPK, NAME, LANGPK)
);

CREATE INDEX itempk_typesystemprops ON typesystemprops (ITEMPK);

CREATE INDEX realnameidx_typesystemprops ON typesystemprops (REALNAME);

CREATE INDEX nameidx_typesystemprops ON typesystemprops (NAME);


CREATE CACHED TABLE units
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_code NVARCHAR(255),
    p_conversion DOUBLE,
    p_unittype NVARCHAR(255),
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);


CREATE CACHED TABLE units10sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_10Sn ON units10sn (ITEMPK);


CREATE CACHED TABLE unitslp
(
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    LANGPK BIGINT,
    p_name NVARCHAR(255),
    PRIMARY KEY (ITEMPK, LANGPK)
);


CREATE CACHED TABLE useraudit
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_uid NVARCHAR(255),
    p_userpk BIGINT,
    p_changinguser NVARCHAR(255),
    p_changingapplication NVARCHAR(255),
    p_ipaddress NVARCHAR(255),
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    passwd LONGVARCHAR,
    p_passwordencoding NVARCHAR(255),
    p_failedlogins INTEGER,
    p_failedoauthauthorizations INTEGER,
    PRIMARY KEY (PK)
);

CREATE INDEX UID_6 ON useraudit (p_uid);

CREATE INDEX userPK_6 ON useraudit (p_userpk);


CREATE CACHED TABLE useraudit6sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_6Sn ON useraudit6sn (ITEMPK);


CREATE CACHED TABLE usergroupprops
(
    hjmpTS BIGINT,
    ITEMPK BIGINT NOT NULL,
    ITEMTYPEPK BIGINT,
    NAME NVARCHAR(255) NOT NULL,
    LANGPK BIGINT NOT NULL,
    REALNAME NVARCHAR(255),
    TYPE1 INTEGER DEFAULT 0,
    VALUESTRING1 LONGVARCHAR,
    VALUE1 LONGVARBINARY,
    PRIMARY KEY (ITEMPK, NAME, LANGPK)
);

CREATE INDEX nameidx_usergroupprops ON usergroupprops (NAME);

CREATE INDEX itempk_usergroupprops ON usergroupprops (ITEMPK);

CREATE INDEX realnameidx_usergroupprops ON usergroupprops (REALNAME);


CREATE CACHED TABLE usergroups
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_description NVARCHAR(255),
    p_name NVARCHAR(255),
    p_uid NVARCHAR(255),
    p_profilepicture BIGINT,
    p_maxbruteforceloginattempts INTEGER,
    p_writeablelanguages LONGVARCHAR,
    p_readablelanguages LONGVARCHAR,
    p_userdiscountgroup BIGINT,
    p_userpricegroup BIGINT,
    p_usertaxgroup BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    p_dunsid NVARCHAR(255),
    p_ilnid NVARCHAR(255),
    p_buyerspecificid NVARCHAR(255),
    p_id NVARCHAR(255),
    p_supplierspecificid NVARCHAR(255),
    p_medias LONGVARCHAR,
    p_shippingaddress BIGINT,
    p_unloadingaddress BIGINT,
    p_billingaddress BIGINT,
    p_contactaddress BIGINT,
    p_contact BIGINT,
    p_vatid NVARCHAR(255),
    p_responsiblecompany BIGINT,
    p_country BIGINT,
    p_lineofbuisness BIGINT,
    p_buyer TINYINT,
    p_supplier TINYINT,
    p_manufacturer TINYINT,
    p_carrier TINYINT,
    PRIMARY KEY (PK)
);

CREATE UNIQUE INDEX UID_5 ON usergroups (p_uid);


CREATE CACHED TABLE usergroups5sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_5Sn ON usergroups5sn (ITEMPK);


CREATE CACHED TABLE usergroupslp
(
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    LANGPK BIGINT,
    p_locname NVARCHAR(255),
    PRIMARY KEY (ITEMPK, LANGPK)
);


CREATE CACHED TABLE userprofiles
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_readablelanguages LONGVARCHAR,
    p_writablelanguages LONGVARCHAR,
    p_expandinitial TINYINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);


CREATE CACHED TABLE userprofiles1119sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_1119Sn ON userprofiles1119sn (ITEMPK);


CREATE CACHED TABLE userprops
(
    hjmpTS BIGINT,
    ITEMPK BIGINT NOT NULL,
    ITEMTYPEPK BIGINT,
    NAME NVARCHAR(255) NOT NULL,
    LANGPK BIGINT NOT NULL,
    REALNAME NVARCHAR(255),
    TYPE1 INTEGER DEFAULT 0,
    VALUESTRING1 LONGVARCHAR,
    VALUE1 LONGVARBINARY,
    PRIMARY KEY (ITEMPK, NAME, LANGPK)
);

CREATE INDEX realnameidx_userprops ON userprops (REALNAME);

CREATE INDEX nameidx_userprops ON userprops (NAME);

CREATE INDEX itempk_userprops ON userprops (ITEMPK);


CREATE CACHED TABLE userrights
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_code NVARCHAR(255),
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);


CREATE CACHED TABLE userrights29sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_29Sn ON userrights29sn (ITEMPK);


CREATE CACHED TABLE userrightslp
(
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    LANGPK BIGINT,
    p_name NVARCHAR(255),
    PRIMARY KEY (ITEMPK, LANGPK)
);


CREATE CACHED TABLE users
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_description NVARCHAR(255),
    p_name NVARCHAR(255),
    p_uid NVARCHAR(255),
    p_profilepicture BIGINT,
    p_defaultpaymentaddress BIGINT,
    p_defaultshipmentaddress BIGINT,
    p_passwordencoding NVARCHAR(255),
    passwd LONGVARCHAR,
    p_passwordanswer LONGVARCHAR,
    p_passwordquestion LONGVARCHAR,
    p_sessionlanguage BIGINT,
    p_sessioncurrency BIGINT,
    p_logindisabled TINYINT,
    p_lastlogin TIMESTAMP,
    p_hmclogindisabled TINYINT,
    p_retentionstate BIGINT,
    p_userprofile BIGINT,
    p_deactivationdate TIMESTAMP,
    p_randomtoken NVARCHAR(255),
    p_europe1pricefactory_udg BIGINT,
    p_europe1pricefactory_upg BIGINT,
    p_europe1pricefactory_utg BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    p_customerid NVARCHAR(255),
    p_previewcatalogversions LONGVARCHAR,
    PRIMARY KEY (PK)
);

CREATE UNIQUE INDEX UID_4 ON users (p_uid);


CREATE CACHED TABLE users4sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_4Sn ON users4sn (ITEMPK);


CREATE CACHED TABLE validationconst980sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_980Sn ON validationconst980sn (ITEMPK);


CREATE CACHED TABLE validationconstraints
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_id NVARCHAR(255),
    p_active TINYINT,
    p_annotation LONGVARCHAR,
    p_severity BIGINT,
    p_target LONGVARCHAR,
    p_type BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    p_language BIGINT,
    p_expression LONGVARCHAR,
    p_firstfieldname NVARCHAR(255),
    p_secondfieldname NVARCHAR(255),
    p_qualifier NVARCHAR(255),
    p_languages LONGVARCHAR,
    p_descriptor BIGINT,
    p_regexp NVARCHAR(255),
    p_flags LONGVARCHAR,
    p_integer INTEGER,
    p_fraction INTEGER,
    p_value BIGINT,
    p_valu0 DECIMAL(30,8),
    p_inclusive TINYINT,
    p_min BIGINT,
    p_max BIGINT,
    PRIMARY KEY (PK)
);

CREATE UNIQUE INDEX AbstractConstraint_idx_980 ON validationconstraints (p_id);

CREATE INDEX typeRelIDX_980 ON validationconstraints (p_type);

CREATE INDEX descriptorRelIDX_980 ON validationconstraints (p_descriptor);


CREATE CACHED TABLE validationconstraintslp
(
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    LANGPK BIGINT,
    p_message LONGVARCHAR,
    PRIMARY KEY (ITEMPK, LANGPK)
);


CREATE CACHED TABLE whereparts
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_and TINYINT,
    p_replacepattern NVARCHAR(255),
    p_savedquery BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX savedqueryRelIDX_1300 ON whereparts (p_savedquery);


CREATE CACHED TABLE whereparts1300sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_1300Sn ON whereparts1300sn (ITEMPK);


CREATE CACHED TABLE workflowactionc1118sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_1118Sn ON workflowactionc1118sn (ITEMPK);


CREATE CACHED TABLE workflowactioncomments
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_comment NVARCHAR(255),
    p_user BIGINT,
    p_workflowaction BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX workflowactionRelIDX_1118 ON workflowactioncomments (p_workflowaction);


CREATE CACHED TABLE workflowactioni1116sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    sourcePK BIGINT,
    targetPK BIGINT,
    languagePK BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_1116Sn ON workflowactioni1116sn (ITEMPK);

CREATE INDEX IdxsourcePK_1116Sn ON workflowactioni1116sn (sourcePK);

CREATE INDEX IdxtargetPK_1116Sn ON workflowactioni1116sn (targetPK);

CREATE INDEX IdxlanguagePK_1116Sn ON workflowactioni1116sn (languagePK);


CREATE CACHED TABLE workflowactionitemsrel
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    languagepk BIGINT,
    Qualifier NVARCHAR(255),
    SourcePK BIGINT,
    TargetPK BIGINT,
    SequenceNumber INTEGER DEFAULT 0,
    RSequenceNumber INTEGER DEFAULT 0,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX linktarget_1116 ON workflowactionitemsrel (TargetPK);

CREATE INDEX qualifier_1116 ON workflowactionitemsrel (Qualifier);

CREATE INDEX linksource_1116 ON workflowactionitemsrel (SourcePK);

CREATE INDEX seqnr_1116 ON workflowactionitemsrel (SequenceNumber);

CREATE INDEX rseqnr_1116 ON workflowactionitemsrel (RSequenceNumber);


CREATE CACHED TABLE workflowactionl1124sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    sourcePK BIGINT,
    targetPK BIGINT,
    languagePK BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_1124Sn ON workflowactionl1124sn (ITEMPK);

CREATE INDEX IdxsourcePK_1124Sn ON workflowactionl1124sn (sourcePK);

CREATE INDEX IdxtargetPK_1124Sn ON workflowactionl1124sn (targetPK);

CREATE INDEX IdxlanguagePK_1124Sn ON workflowactionl1124sn (languagePK);


CREATE CACHED TABLE workflowactionlinkrel
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    languagepk BIGINT,
    Qualifier NVARCHAR(255),
    SourcePK BIGINT,
    TargetPK BIGINT,
    SequenceNumber INTEGER DEFAULT 0,
    RSequenceNumber INTEGER DEFAULT 0,
    p_active TINYINT,
    p_andconnection TINYINT,
    p_template BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX linktarget_1124 ON workflowactionlinkrel (TargetPK);

CREATE INDEX qualifier_1124 ON workflowactionlinkrel (Qualifier);

CREATE INDEX linksource_1124 ON workflowactionlinkrel (SourcePK);

CREATE INDEX seqnr_1124 ON workflowactionlinkrel (SequenceNumber);

CREATE INDEX rseqnr_1124 ON workflowactionlinkrel (RSequenceNumber);


CREATE CACHED TABLE workflowactions
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_actiontype BIGINT,
    p_code NVARCHAR(255),
    p_principalassigned BIGINT,
    p_sendemail TINYINT,
    p_emailaddress NVARCHAR(255),
    p_renderertemplate BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    p_workflowpos INTEGER,
    p_workflow BIGINT,
    p_jobclass LONGVARCHAR,
    p_jobhandler NVARCHAR(255),
    p_selecteddecision BIGINT,
    p_firstactivated TIMESTAMP,
    p_activated TIMESTAMP,
    p_comment NVARCHAR(255),
    p_status BIGINT,
    p_template BIGINT,
    PRIMARY KEY (PK)
);

CREATE INDEX codeIDX_1113 ON workflowactions (p_code);

CREATE INDEX workflowRelIDX_1113 ON workflowactions (p_workflow);

CREATE INDEX workflowposPosIDX_1113 ON workflowactions (p_workflowpos);


CREATE CACHED TABLE workflowactions1113sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_1113Sn ON workflowactions1113sn (ITEMPK);


CREATE CACHED TABLE workflowactions1115sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    sourcePK BIGINT,
    targetPK BIGINT,
    languagePK BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_1115Sn ON workflowactions1115sn (ITEMPK);

CREATE INDEX IdxsourcePK_1115Sn ON workflowactions1115sn (sourcePK);

CREATE INDEX IdxtargetPK_1115Sn ON workflowactions1115sn (targetPK);

CREATE INDEX IdxlanguagePK_1115Sn ON workflowactions1115sn (languagePK);


CREATE CACHED TABLE workflowactionslp
(
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    LANGPK BIGINT,
    p_name NVARCHAR(255),
    p_description LONGVARCHAR,
    PRIMARY KEY (ITEMPK, LANGPK)
);


CREATE CACHED TABLE workflowactionsrel
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    languagepk BIGINT,
    Qualifier NVARCHAR(255),
    SourcePK BIGINT,
    TargetPK BIGINT,
    SequenceNumber INTEGER DEFAULT 0,
    RSequenceNumber INTEGER DEFAULT 0,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX linktarget_1115 ON workflowactionsrel (TargetPK);

CREATE INDEX qualifier_1115 ON workflowactionsrel (Qualifier);

CREATE INDEX linksource_1115 ON workflowactionsrel (SourcePK);

CREATE INDEX seqnr_1115 ON workflowactionsrel (SequenceNumber);

CREATE INDEX rseqnr_1115 ON workflowactionsrel (RSequenceNumber);


CREATE CACHED TABLE workflowitematt1114sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_1114Sn ON workflowitematt1114sn (ITEMPK);


CREATE CACHED TABLE workflowitematts
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_code NVARCHAR(255),
    p_comment NVARCHAR(255),
    p_item BIGINT,
    p_typeofitem BIGINT,
    p_workflowpos INTEGER,
    p_workflow BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX codeIDX_1114 ON workflowitematts (p_code);

CREATE INDEX workflowRelIDX_1114 ON workflowitematts (p_workflow);

CREATE INDEX workflowposPosIDX_1114 ON workflowitematts (p_workflowpos);


CREATE CACHED TABLE workflowitemattslp
(
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    LANGPK BIGINT,
    p_name NVARCHAR(255),
    PRIMARY KEY (ITEMPK, LANGPK)
);


CREATE CACHED TABLE workflowtemplat1125sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    sourcePK BIGINT,
    targetPK BIGINT,
    languagePK BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_1125Sn ON workflowtemplat1125sn (ITEMPK);

CREATE INDEX IdxsourcePK_1125Sn ON workflowtemplat1125sn (sourcePK);

CREATE INDEX IdxtargetPK_1125Sn ON workflowtemplat1125sn (targetPK);

CREATE INDEX IdxlanguagePK_1125Sn ON workflowtemplat1125sn (languagePK);


CREATE CACHED TABLE workflowtemplatelinkrel
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    languagepk BIGINT,
    Qualifier NVARCHAR(255),
    SourcePK BIGINT,
    TargetPK BIGINT,
    SequenceNumber INTEGER DEFAULT 0,
    RSequenceNumber INTEGER DEFAULT 0,
    p_andconnectiontemplate TINYINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX linktarget_1125 ON workflowtemplatelinkrel (TargetPK);

CREATE INDEX qualifier_1125 ON workflowtemplatelinkrel (Qualifier);

CREATE INDEX linksource_1125 ON workflowtemplatelinkrel (SourcePK);

CREATE INDEX seqnr_1125 ON workflowtemplatelinkrel (SequenceNumber);

CREATE INDEX rseqnr_1125 ON workflowtemplatelinkrel (RSequenceNumber);


CREATE CACHED TABLE workflowtemplpr1117sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    sourcePK BIGINT,
    targetPK BIGINT,
    languagePK BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_1117Sn ON workflowtemplpr1117sn (ITEMPK);

CREATE INDEX IdxsourcePK_1117Sn ON workflowtemplpr1117sn (sourcePK);

CREATE INDEX IdxtargetPK_1117Sn ON workflowtemplpr1117sn (targetPK);

CREATE INDEX IdxlanguagePK_1117Sn ON workflowtemplpr1117sn (languagePK);


CREATE CACHED TABLE workflowtemplprincrel
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    languagepk BIGINT,
    Qualifier NVARCHAR(255),
    SourcePK BIGINT,
    TargetPK BIGINT,
    SequenceNumber INTEGER DEFAULT 0,
    RSequenceNumber INTEGER DEFAULT 0,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX linktarget_1117 ON workflowtemplprincrel (TargetPK);

CREATE INDEX qualifier_1117 ON workflowtemplprincrel (Qualifier);

CREATE INDEX linksource_1117 ON workflowtemplprincrel (SourcePK);

CREATE INDEX seqnr_1117 ON workflowtemplprincrel (SequenceNumber);

CREATE INDEX rseqnr_1117 ON workflowtemplprincrel (RSequenceNumber);


CREATE CACHED TABLE ydeployments
(
    hjmpTS BIGINT,
    Typecode INTEGER DEFAULT 0,
    TableName NVARCHAR(255),
    PropsTableName NVARCHAR(255),
    Name NVARCHAR(255) NOT NULL,
    PackageName NVARCHAR(255),
    SuperName NVARCHAR(255),
    ExtensionName NVARCHAR(255),
    Modifiers INTEGER DEFAULT 0,
    TypeSystemName NVARCHAR(255) NOT NULL,
    AuditTableName NVARCHAR(255),
    PRIMARY KEY (Name, TypeSystemName)
);

CREATE INDEX deplselect2_ydeployments ON ydeployments (Typecode);

CREATE INDEX tsnameidx_ydeployments ON ydeployments (TypeSystemName);

CREATE INDEX deplselect_ydeployments ON ydeployments (ExtensionName);


CREATE CACHED TABLE zone2country
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    languagepk BIGINT,
    Qualifier NVARCHAR(255),
    SourcePK BIGINT,
    TargetPK BIGINT,
    SequenceNumber INTEGER DEFAULT 0,
    RSequenceNumber INTEGER DEFAULT 0,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE INDEX linktarget_1204 ON zone2country (TargetPK);

CREATE INDEX qualifier_1204 ON zone2country (Qualifier);

CREATE INDEX linksource_1204 ON zone2country (SourcePK);

CREATE INDEX seqnr_1204 ON zone2country (SequenceNumber);

CREATE INDEX rseqnr_1204 ON zone2country (RSequenceNumber);


CREATE CACHED TABLE zone2country1204sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    sourcePK BIGINT,
    targetPK BIGINT,
    languagePK BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_1204Sn ON zone2country1204sn (ITEMPK);

CREATE INDEX IdxsourcePK_1204Sn ON zone2country1204sn (sourcePK);

CREATE INDEX IdxtargetPK_1204Sn ON zone2country1204sn (targetPK);

CREATE INDEX IdxlanguagePK_1204Sn ON zone2country1204sn (languagePK);


CREATE CACHED TABLE zonedeliverymod1202sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_1202Sn ON zonedeliverymod1202sn (ITEMPK);


CREATE CACHED TABLE zonedeliverymodevalues
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_currency BIGINT,
    p_minimum DOUBLE,
    p_value DOUBLE,
    p_zone BIGINT,
    p_deliverymode BIGINT,
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE UNIQUE INDEX IdentityIDX_1202 ON zonedeliverymodevalues (p_deliverymode, p_zone, p_currency, p_minimum);

CREATE INDEX ModeIDX_1202 ON zonedeliverymodevalues (p_deliverymode);

CREATE INDEX ZoneIDX_1202 ON zonedeliverymodevalues (p_zone);


CREATE CACHED TABLE zones
(
    hjmpTS BIGINT,
    createdTS TIMESTAMP,
    modifiedTS TIMESTAMP,
    TypePkString BIGINT,
    OwnerPkString BIGINT,
    PK BIGINT NOT NULL,
    sealed TINYINT,
    p_code NVARCHAR(255),
    aCLTS BIGINT DEFAULT 0,
    propTS BIGINT DEFAULT 0,
    PRIMARY KEY (PK)
);

CREATE UNIQUE INDEX IdentityIDX_1203 ON zones (p_code);


CREATE CACHED TABLE zones1203sn
(
    ID BIGINT IDENTITY,
    ITEMPK BIGINT,
    ITEMTYPEPK BIGINT,
    timestamp TIMESTAMP,
    currenttimestamp TIMESTAMP,
    changinguser NVARCHAR(255),
    context LONGVARCHAR,
    payloadbefore LONGVARCHAR,
    payloadafter LONGVARCHAR,
    operationtype BIGINT,
    PRIMARY KEY (ID)
);

CREATE INDEX IdxITEMPK_1203Sn ON zones1203sn (ITEMPK);

