import { BaseAlertService, IAlertConfig, IAlertService } from 'smarteditcommons';
import { AlertFactory } from './AlertFactory';
export declare class AlertService extends BaseAlertService implements IAlertService {
    private _alertFactory;
    constructor(_alertFactory: AlertFactory);
    showAlert(alertConf: string | IAlertConfig): void;
    showInfo(alertConf: string | IAlertConfig): void;
    showDanger(alertConf: string | IAlertConfig): void;
    showWarning(alertConf: string | IAlertConfig): void;
    showSuccess(alertConf: string | IAlertConfig): void;
}
