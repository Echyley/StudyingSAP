import { IModalService } from '@smart/utils';
import '../components/modalWizardNavBar.scss';
import { WizardAction } from './types';
/**
 * Used to create wizards that are embedded into the {@link ModalService}.
 */
export declare class ModalWizard {
    private readonly modalService;
    constructor(modalService: IModalService);
    /**
     * Open provides a simple way to create modal wizards, with much of the boilerplate taken care of for you
     * such as look, feel and wizard navigation.
     *
     * @returns Promise that will either be resolved (wizard finished) or
     * rejected (wizard cancelled).
     */
    open(config: WizardAction): Promise<any>;
    private validateConfig;
}
