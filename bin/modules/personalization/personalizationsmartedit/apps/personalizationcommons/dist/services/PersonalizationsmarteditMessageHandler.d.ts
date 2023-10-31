import { IAlertService } from 'smarteditcommons';
export declare class PersonalizationsmarteditMessageHandler {
    private alertService;
    constructor(alertService: IAlertService);
    sendInformation(informationMessage: any): any;
    sendError(errorMessage: any): any;
    sendWarning(warningMessage: any): any;
    sendSuccess(successMessage: any): any;
}
