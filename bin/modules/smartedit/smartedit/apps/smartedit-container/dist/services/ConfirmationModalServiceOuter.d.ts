import { ConfirmationModalConfig, IConfirmationModalService, IModalService } from 'smarteditcommons';
/**
 * Service used to open a confirmation modal in which an end-user can confirm or cancel an action.
 * A confirmation modal consists of a title, content, and an OK and cancel button. This modal may be used in any context in which a
 * confirmation is required.
 */
export declare class ConfirmationModalService extends IConfirmationModalService {
    private modalService;
    constructor(modalService: IModalService);
    /**
     * Uses the [ModalService]{@link IModalService} to open a confirmation modal.
     *
     * The confirmation modal is initialized by a default i18N key as a title or by an override title passed in configuration.
     *
     * @returns A promise that is resolved when the OK button is actioned or is rejected when the Cancel
     * button is actioned.
     */
    confirm(configuration: ConfirmationModalConfig): Promise<any>;
    private getButtons;
}
