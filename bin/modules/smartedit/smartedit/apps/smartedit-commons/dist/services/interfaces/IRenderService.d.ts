/// <reference types="jquery" />
/**
 * The renderService is responsible for rendering and resizing component overlays, and re-rendering components and slots
 * from the storefront.
 */
import { IModalService, LogService } from '@smart/utils';
import { WindowUtils } from 'smarteditcommons/utils/WindowUtils';
import { CrossFrameEventService, INotificationService, IPageInfoService, IPerspectiveService, SystemEventService } from '../';
export declare abstract class IRenderService {
    protected yjQuery: JQueryStatic;
    protected systemEventService: SystemEventService;
    private readonly notificationService;
    private readonly pageInfoService;
    private readonly perspectiveService;
    protected crossFrameEventService: CrossFrameEventService;
    private readonly windowUtils;
    private readonly modalService;
    protected logService: LogService;
    private readonly HOTKEY_NOTIFICATION_CONFIGURATION;
    private readonly KEY_CODES;
    constructor(yjQuery: JQueryStatic, systemEventService: SystemEventService, notificationService: INotificationService, pageInfoService: IPageInfoService, perspectiveService: IPerspectiveService, crossFrameEventService: CrossFrameEventService, windowUtils: WindowUtils, modalService: IModalService, logService: LogService);
    /**
     * Re-renders a slot in the page
     */
    renderSlots(_slotIds: string[] | string): Promise<any>;
    /**
     * Re-renders a component in the page.
     *
     * @param customContent The custom content to replace the component content with. If specified, the
     * component content will be rendered with it, instead of the accelerator's. Optional.
     *
     * @returns Promise that will resolve on render success or reject if there's an error. When rejected,
     * the promise returns an Object{message, stack}.
     */
    renderComponent(componentId: string, componentType: string): Promise<string | boolean>;
    /**
     * This method removes a component from a slot in the current page. Note that the component is only removed
     * on the frontend; the operation does not propagate to the backend.
     *
     * @param componentId The ID of the component to remove.
     *
     * @returns Object wrapping the removed component.
     */
    renderRemoval(componentId: string, componentType: string, slotId: string): JQuery;
    /**
     * Re-renders all components in the page.
     * this method first resets the HTML content all of components to the values saved by {@link /smartedit/injectables/DecoratorService.html#storePrecompiledComponent storePrecompiledComponent} at the last $compile time
     * then requires a new compilation.
     */
    renderPage(isRerender: boolean): void;
    /**
     * Toggles on/off the visibility of the page overlay (containing the decorators).
     *
     * @param isVisible Flag that indicates if the overlay must be displayed.
     */
    toggleOverlay(isVisible: boolean): void;
    /**
     * This method updates the position of the decorators in the overlay. Normally, this method must be executed every
     * time the original storefront content is updated to keep the decorators correctly positioned.
     */
    refreshOverlayDimensions(): void;
    /**
     * Toggles the rendering to be blocked or not which determines whether the overlay should be rendered or not.
     *
     * @param isBlocked Flag that indicates if the rendering should be blocked or not.
     */
    blockRendering(isBlocked: boolean): void;
    /**
     * This method returns a boolean that determines whether the rendering is blocked or not.
     *
     * @returns True if the rendering is blocked. Otherwise false.
     */
    isRenderingBlocked(): Promise<boolean>;
    createComponent(element: HTMLElement): void;
    destroyComponent(_component: HTMLElement, _parent?: HTMLElement, oldAttributes?: any): void;
    updateComponentSizeAndPosition(element: HTMLElement, componentOverlayElem?: HTMLElement): void;
    protected _getDocument(): Document;
    private _bindEvents;
    private _keyUpEventHandler;
    private _shouldEnableKeyPressEvent;
    private _keyPressEvent;
    private _clickEvent;
    private _areAllModalWindowsClosed;
}
