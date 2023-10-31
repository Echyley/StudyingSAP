import { ICatalogService, ModalWizard } from 'smarteditcommons';
export declare class AddPageWizardService {
    private modalWizard;
    private catalogService;
    constructor(modalWizard: ModalWizard, catalogService: ICatalogService);
    openAddPageWizard(): Promise<any>;
}
