import { ICatalogService, ICMSPage } from 'smarteditcommons';
import { ActionableAlertService } from './ActionableAlertService';
export declare class ClonePageAlertService {
    private actionableAlertService;
    private catalogService;
    constructor(actionableAlertService: ActionableAlertService, catalogService: ICatalogService);
    /**
     * Displays an alert containing an hyperlink allowing for the user
     * to navigate to the newly cloned page.
     */
    displayClonePageAlert(clonedPageInfo: ICMSPage): Promise<void>;
}
