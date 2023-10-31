/// <reference types="jquery" />
import { IComponentHandlerService } from 'smarteditcommons';
/**
 * Internal service
 *
 * Service that resizes slots and components in the Inner Frame when the overlay is enabled or disabled.
 */
export declare class ResizeComponentService {
    private readonly componentHandlerService;
    private readonly yjQuery;
    constructor(componentHandlerService: IComponentHandlerService, yjQuery: JQueryStatic);
    /**
     * This methods appends CSS classes to inner frame slots and components. Passing a boolean true to showResizing
     * enables the resizing, and false vice versa.
     */
    resizeComponents(showResizing: boolean): void;
}
