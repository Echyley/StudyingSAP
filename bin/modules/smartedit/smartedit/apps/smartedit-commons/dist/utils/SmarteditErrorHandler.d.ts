import { ErrorHandler } from '@angular/core';
export declare class SmarteditErrorHandler extends ErrorHandler {
    private ignorePatterns;
    constructor();
    handleError(error: {
        message: string;
    }): void;
}
