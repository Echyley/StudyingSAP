import { SystemEventService } from 'smarteditcommons';
/**
 * ContextualMenuDropdownService is an internal service that provides methods for interaction between
 * Drag and Drop Service and the Contextual Menu.
 *
 * Note: The contextualMenuDropdownService functions are as a glue between the Drag and Drop Service and the Contextual Menu.
 *  The service was created to solve the issue of closing any contextual menu that is open whenever a drag operation is started.
 *  It does so while keeping the DnD and Contextual Menu services decoupled.
 */
export declare class ContextualMenuDropdownService {
    private systemEventService;
    private unsubscribeFn;
    constructor(systemEventService: SystemEventService);
    registerIsOpenEvent(): void;
    private registerDragStarted;
    private triggerCloseOperation;
}
