import { IWaitDialogService, ModalService } from 'smarteditcommons';
/** @internal */
export declare class WaitDialogService extends IWaitDialogService {
    private modalService;
    private modalRef;
    constructor(modalService: ModalService);
    showWaitModal(customLoadingMessageLocalizedKey?: string): void;
    hideWaitModal(): void;
}
