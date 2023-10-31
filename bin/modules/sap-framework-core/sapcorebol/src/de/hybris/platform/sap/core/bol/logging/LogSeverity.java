/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.bol.logging;


/**
 * Log Severity.
 */
public final class LogSeverity
{
    public static final int ALL = 0;
    public static final int DEBUG = 100;
    public static final int PATH = 200;
    public static final int INFO = 300;
    public static final int WARNING = 400;
    public static final int ERROR = 500;
    public static final int FATAL = 600;
    public static final int MAX = 700;
    public static final int NONE = 701;
    public static final int GROUP = 800;


    private LogSeverity() {

    }

    public static String toString(int severity) {
        if (severity < DEBUG) {
            return "All";
        } else if (severity < PATH) {
            return "Debug";
        } else if (severity < INFO) {
            return "Path";
        } else if (severity < WARNING) {
            return "Info";
        } else if (severity < ERROR) {
            return "Warning";
        } else if (severity < FATAL) {
            return "Error";
        } else if (severity <= MAX) {
            return "Fatal";
        } else if (severity == NONE) {
            return "None";
        } else {
            return severity == GROUP ? "Group" : "Unknown";
        }
    }

}
