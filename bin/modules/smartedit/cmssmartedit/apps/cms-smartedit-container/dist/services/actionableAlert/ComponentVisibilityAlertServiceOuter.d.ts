import { IComponentVisibilityAlertService, SlotComponent } from 'cmscommons';
import { IAlertService, ISharedDataService, CrossFrameEventService } from 'smarteditcommons';
import { ActionableAlertService } from './ActionableAlertService';
export declare class ComponentVisibilityAlertService extends IComponentVisibilityAlertService {
    private readonly sharedDataService;
    private readonly alertService;
    private readonly actionableAlertService;
    private readonly crossFrameEventService;
    constructor(sharedDataService: ISharedDataService, alertService: IAlertService, actionableAlertService: ActionableAlertService, crossFrameEventService: CrossFrameEventService);
    checkAndAlertOnComponentVisibility(component: SlotComponent): Promise<void>;
}
