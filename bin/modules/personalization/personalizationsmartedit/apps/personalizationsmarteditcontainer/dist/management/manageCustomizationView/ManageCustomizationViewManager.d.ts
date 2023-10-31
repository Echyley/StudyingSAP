import { IModalService } from 'smarteditcommons';
import { ManageCustomizationViewModalData } from './types';
export declare class ManageCustomizationViewManager {
    private modalService;
    private readonly className;
    private readonly modalTitle;
    constructor(modalService: IModalService);
    openCreateCustomizationModal(): void;
    openEditCustomizationModal(customizationCode: ManageCustomizationViewModalData['customizationCode'], variationCode: ManageCustomizationViewModalData['variationCode']): void;
}
