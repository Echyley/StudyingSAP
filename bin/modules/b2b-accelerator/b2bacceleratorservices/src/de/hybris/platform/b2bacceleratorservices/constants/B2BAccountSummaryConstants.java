/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bacceleratorservices.constants;

import de.hybris.platform.b2bacceleratorservices.model.B2BDocumentModel;
import de.hybris.platform.b2bacceleratorservices.model.B2BDocumentTypeModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.util.Config;

public final class B2BAccountSummaryConstants
{
    public static final String FIND_ALL_DOCUMENTS_FOR_UNIT_DOCUMENT_TYPE = "SELECT {" + B2BDocumentModel._TYPECODE + ":"
            + B2BDocumentModel.PK + "} FROM { " + B2BDocumentModel._TYPECODE + " as " + B2BDocumentModel._TYPECODE + " join "
            + B2BDocumentTypeModel._TYPECODE + " as " + B2BDocumentTypeModel._TYPECODE + " on {" + B2BDocumentModel._TYPECODE + ":"
            + B2BDocumentModel.DOCUMENTTYPE + "} = {" + B2BDocumentTypeModel._TYPECODE + ":" + B2BDocumentTypeModel.PK + "} join "
            + B2BUnitModel._TYPECODE + " as " + B2BUnitModel._TYPECODE + " on {" + B2BDocumentModel._TYPECODE + ":"
            + B2BDocumentModel.UNIT + "} = {" + B2BUnitModel._TYPECODE + ":" + B2BUnitModel.PK + "} }";

    public static final String DISPLAY_IN_ALL_LIST = "{" + B2BDocumentTypeModel._TYPECODE + ":"
            + B2BDocumentTypeModel.DISPLAYINALLLIST + " } = ?displayInAllList";

    public static final String AND_STATEMENT = " AND ";
    public static final String WHERE_STATEMENT = " WHERE ";
    public static final String DEFAULT_SORT_CODE_PROP = "accountsummary.documents.defaultSortCode";


    public static final String B2B_UNIT_CRITERIA = "{" + B2BUnitModel._TYPECODE + ":" + B2BUnitModel.UID + "} = ?b2bUnitCode";
    public static final String B2B_DOCUMENT_NUMBER_CRITERIA = "{" + B2BDocumentModel._TYPECODE + ":" + B2BDocumentModel.DOCUMENTNUMBER + "} = ?b2bDocumentNumber";
    public static final String B2B_STATUS_CRITERIA = "{" + B2BDocumentModel._TYPECODE + ":" + B2BDocumentModel.STATUS + "} = ?b2bDocumentStatus";
    public static final String ORDER_BY_QUERY = " ORDER BY { %s }";
    public static final String DESC = "DESC";
    public static final String DEFAULT_SORT_CODE = Config.getString(DEFAULT_SORT_CODE_PROP, "byDocumentDateAsc");

    public static final String B2B_DOCUMENT_STATUS = "b2bDocumentStatus";
    public static final String B2B_DOCUMENT_NUMBER = "b2bDocumentNumber";
    public static final String B2B_UNIT_CODE = "b2bUnitCode";

    public static final String PAYORUSEINTERFACENAME = "PayableOrUsableInterfaceActive";

    public static final String DATE_FORMAT_MM_DD_YYYY = "MM/dd/yyyy";

    public static final String DEFAULT_SORT = "asc";
    public static final String DEFAULT_PAGE_SIZE = "10";
    public static final String DEFAULT_PAGE = "0";
    public static final String ERROR_ATTRIBUTE = "error";
    public static final String ACCOUNT_STATUS_CMS_PAGE = "accountstatus";
    public static final String ACCOUNT_STATUS_DETAIL_CMS_PAGE = "accountstatusdetail";
    public static final String SEARCH_STATUS_OPEN = "status_open";
    public static final String SEARCH_STATUS_CLOSED = "status_closed";
    public static final String SEARCH_BY_DOCUMENTNUMBER = "documentNumber";

    public static final String ACCOUNT_SUMMARY_UNIT_CMS_PAGE = "accountsummaryunittree";
    public static final String ACCOUNT_SUMMARY_UNIT_DETAILS_LIST_CMS_PAGE = "accountsummaryunitdetailslist";


    private B2BAccountSummaryConstants() { }
}
