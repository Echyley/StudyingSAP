import { ICatalogService, ModalWizard, ICMSPage } from 'smarteditcommons';
import { PageFacade } from '../../../facades';
export declare class ClonePageWizardService {
    private modalWizard;
    private catalogService;
    private pageFacade;
    constructor(modalWizard: ModalWizard, catalogService: ICatalogService, pageFacade: PageFacade);
    /**
     * When called, this method opens a modal window containing a wizard to clone an existing page.
     *
     * @param pageData An object containing the pageData when the clone page wizard is opened from the page list.
     * @returns A promise that will resolve when the modal wizard is closed or reject if it's canceled.
     *
     */
    openClonePageWizard(pageData?: ICMSPage): Promise<any>;
}
