/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.jco.connection;

import java.util.List;

import org.apache.log4j.Logger;

import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoFieldIterator;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoRecord;
import com.sap.conn.jco.JCoTable;

import de.hybris.platform.sap.core.jco.connection.impl.JCoManagedFunction;

/**
 * Utility class for logging the content of a JCO function.
 */
public class JCoLogUtil {
    /**
     * Logger.
     */
    private static final Logger LOG = Logger.getLogger(JCoLogUtil.class
            .getName());

    /**
     * Marker for parameters which are excluded in the output.
     */
    private static final String HIDDEN = "---hidden---";
    
    private static final int HUNDREDTH_ROW = 100;
    
    private static final int TENTH_ROW = 10;

    /**
     * Marker for input or output parameter.
     */
    public enum LogType {
        /**
         * Maker for input parameter.
         */
        INPUT, /**
         * Marker for output parameter.
         */
        OUTPUT
    }

    /**
     * Log function before call. <br>
     * Uses standard logger for this class.
     * 
     * @param function
     *            function to be logged.
     */
    public void logBeforeCall(final JCoManagedFunction function) {
        logBeforeCall(function, LOG);
    }

    /**
     * Log function before call in given logger. <br>
     * 
     * @param function
     *            function to be logged.
     * @param log
     *            logger to be used.
     */
    public void logBeforeCall(final JCoManagedFunction function,
           final Logger log) {

        final JCoConnectionParameter connectionParameter = function
                .getConnectionParameter();
        logRecord(function.getName(), function.getImportParameterList(), log,
                connectionParameter.getTraceExcludeImportParametersList(),
                LogType.INPUT);
        logRecord(function.getName(), function.getExportParameterList(), log,
                connectionParameter.getTraceExcludeExportParametersList(),
                LogType.INPUT);
        logTables(function.getName(), function.getTableParameterList(), log,
                connectionParameter.getTraceExcludeTableParametersList(),
                LogType.INPUT);

    }

    /**
     * Log function after call. <br>
     * Uses standard logger for this class.
     * 
     * @param function
     *            function to be logged.
     */
    public void logAfterCall(final JCoManagedFunction function) {
        logAfterCall(function, LOG);
    }

    /**
     * Log function after call in given logger. <br>
     * 
     * @param function
     *            function to be logged.
     * @param logger
     *            logger to be used.
     */
    public void logAfterCall(final JCoManagedFunction function,
            final Logger log) {

        final JCoConnectionParameter connectionParameter = function
                .getConnectionParameter();
        logRecord(function.getName(), function.getImportParameterList(), log,
                connectionParameter.getTraceExcludeImportParametersList(),
                LogType.OUTPUT);
        logRecord(function.getName(), function.getExportParameterList(), log,
                connectionParameter.getTraceExcludeExportParametersList(),
                LogType.OUTPUT);
        logTables(function.getName(), function.getTableParameterList(), log,
                connectionParameter.getTraceExcludeTableParametersList(),
                LogType.OUTPUT);

    }

    /**
     * Logs content of a list of tables.
     * 
     * @param functionName
     *            name of the function.
     * @param tableParams
     *            tables to be logged.
     * @param log
     *            logger to be used.
     * @param excludeParam
     *            defines parameter names for excluding.
     * @param type
     *            either input or output.
     */
    private void logTables(final String functionName,
            final JCoParameterList tableParams,
            final Logger log, final List<String> excludeParam,
            final LogType type) {

        if (tableParams != null) {
            final int numTables = tableParams.getFieldCount();
            for (int i = 0; i < numTables; i++) {
                final JCoTable table = tableParams.getTable(i);
                logTable(functionName, table, log, excludeParam, type);
            }
        }
    }

    /**
     * Logs a record.
     * 
     * @param functionName
     *            name of the function.
     * @param record
     *            record to be logged.
     * @param log
     *            logger to be used.
     * @param excludeParam
     *            defines parameter names for excluding.
     * @param type
     *            either input or output.
     */
    private void logRecord(final String functionName, final JCoRecord record,
            final Logger log, final List<String> excludeParam,
            final LogType type) {

        if (record != null) {
            final StringBuilder in = new StringBuilder();

            in.append("::").append(functionName).append("::")
                    .append(type == LogType.INPUT ? " - IN: " : " - OUT: ")
                    .append(record.getMetaData().getName()).append(" * ");

            final JCoFieldIterator iterator = record.getFieldIterator();
            in.append(paramIterator(iterator, excludeParam));
            log.debug(in.toString());
        }

    }

    /**
     * Logs a table.
     * 
     * @param functionName
     *            name of the function.
     * @param table
     *            table to be logged.
     * @param log
     *            logger to be used.
     * @param excludeParam
     *            list of excluded table rows.
     * @param type
     *            either input or output.
     */
    private void logTable(final String functionName, final JCoTable table,
            final Logger log, final List<String> excludeParam,
            final LogType type) {

        if (table == null) {
            return;
        }

        final int rows = table.getNumRows();
        final int cols = table.getNumColumns();
        final String inputName = table.getMetaData().getName();

        for (int i = 0; i < rows; i++) {

            final StringBuilder sb = new StringBuilder();
            sb.append("::").append(functionName).append("::")
                    .append(type == LogType.INPUT ? " - IN: " : " - OUT: ")
                    .append(inputName).append("[").append(i).append("] ");

            // add an additional space
            if (i < TENTH_ROW) {
                sb.append("  ");
            } else if (i < HUNDREDTH_ROW) {
                sb.append(' ');
            }

            sb.append("* ");

            table.setRow(i);

            for (int k = 0; k < cols; k++) {
                if (excludeParam.contains(table.getMetaData().getName(k))) {
                    sb.append(table.getMetaData().getName(k)).append("='")
                            .append(HIDDEN).append("' ");
                } else {
                    sb.append(table.getMetaData().getName(k)).append("='")
                            .append(table.getString(k)).append("' ");
                }

            }
            log.debug(sb.toString());
        }
        table.firstRow();

    }

    /**
     * Provides parameter information. <br>
     * In case of a parameter is excluded a hidden marker is appended.
     * 
     * @param iterator
     *            iterator
     * @param excludeParam
     *            defines parameter names for excluding.
     * @return parameter information.
     */
    private String paramIterator(final JCoFieldIterator iterator,
            final List<String> excludeParam) {
        final StringBuilder sb = new StringBuilder();
        while (iterator.hasNextField()) {
            final JCoField field = iterator.nextField();
            final String name = field.getName();
            if (excludeParam.contains(name)) {
                sb.append(field.getName()).append("='").append(HIDDEN)
                        .append("' ");
            } else {
                sb.append(field.getName()).append("='")
                        .append(field.getString()).append("' ");
            }
        }
        return sb.toString();
    }

}
