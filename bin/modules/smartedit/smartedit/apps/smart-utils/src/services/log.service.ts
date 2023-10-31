/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
export enum LogLevel {
    log,
    debug,
    info,
    warn,
    error
}
export class LogService {
    private logLevel = LogLevel.info;

    log(...msg: any[]): void {
        this._log(LogLevel.log, msg);
    }

    debug(...msg: any[]): void {
        this._log(LogLevel.debug, msg);
    }

    info(...msg: any[]): void {
        this._log(LogLevel.info, msg);
    }

    warn(...msg: any[]): void {
        this._log(LogLevel.warn, msg);
    }

    error(...msg: any[]): void {
        this._log(LogLevel.error, msg);
    }

    setLogLevel(logLevel: LogLevel): void {
        this.logLevel = logLevel;
    }

    private _log(requestLevel: LogLevel, msg: any[]): void {
        if (requestLevel >= this.logLevel) {
            const method = LogLevel[requestLevel] as keyof Console;
            if (this._console() && this._console()[method]) {
                this._console()[method](...msg);
            }
        }
    }

    private _console(): Console {
        return console;
    }
}
