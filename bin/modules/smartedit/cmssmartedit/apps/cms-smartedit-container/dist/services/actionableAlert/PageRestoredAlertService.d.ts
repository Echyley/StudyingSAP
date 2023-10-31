import { ICatalogService, ICMSPage } from 'smarteditcommons';
import { ActionableAlertService } from './ActionableAlertService';
export declare class PageRestoredAlertService {
    private catalogService;
    private actionableAlertService;
    constructor(catalogService: ICatalogService, actionableAlertService: ActionableAlertService);
    displayPageRestoredSuccessAlert(pageInfo: ICMSPage): Promise<void>;
}
