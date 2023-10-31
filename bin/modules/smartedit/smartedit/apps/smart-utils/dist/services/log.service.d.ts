/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
export declare enum LogLevel {
    log = 0,
    debug = 1,
    info = 2,
    warn = 3,
    error = 4
}
export declare class LogService {
    private logLevel;
    log(...msg: any[]): void;
    debug(...msg: any[]): void;
    info(...msg: any[]): void;
    warn(...msg: any[]): void;
    error(...msg: any[]): void;
    setLogLevel(logLevel: LogLevel): void;
    private _log;
    private _console;
}
