/// <reference types="jquery" />
import { HttpClient } from '@angular/common/http';
import { SeNamespaceService } from 'smartedit/services/SeNamespaceService';
import { CrossFrameEventService, IAlertService, IExperienceService, INotificationService, IPageInfoService, IPerspectiveService, IRenderService, JQueryUtilsService, LogService, ModalService, SmarteditBootstrapGateway, SystemEventService, WindowUtils, IComponentHandlerService } from 'smarteditcommons';
/** @internal */
export declare class RenderService extends IRenderService {
    private readonly smarteditBootstrapGateway;
    private readonly httpClient;
    protected logService: LogService;
    protected yjQuery: JQueryStatic;
    private readonly alertService;
    private readonly componentHandlerService;
    protected crossFrameEventService: CrossFrameEventService;
    private readonly jQueryUtilsService;
    private readonly experienceService;
    private readonly seNamespaceService;
    protected systemEventService: SystemEventService;
    constructor(smarteditBootstrapGateway: SmarteditBootstrapGateway, httpClient: HttpClient, logService: LogService, yjQuery: JQueryStatic, alertService: IAlertService, componentHandlerService: IComponentHandlerService, crossFrameEventService: CrossFrameEventService, jQueryUtilsService: JQueryUtilsService, experienceService: IExperienceService, seNamespaceService: SeNamespaceService, systemEventService: SystemEventService, notificationService: INotificationService, pageInfoService: IPageInfoService, perspectiveService: IPerspectiveService, windowUtils: WindowUtils, modalService: ModalService);
    toggleOverlay(isVisible: boolean): void;
    refreshOverlayDimensions(element?: JQuery): void;
    /**
     * Updates the dimensions of the overlay component element given the original component element and the overlay component itself.
     * If no overlay component is provided, it will be fetched through {@link componentHandlerService.getOverlayComponent}
     *
     * The overlay component is resized to be the same dimensions of the component for which it overlays, and positioned absolutely
     * on the page. Additionally, it is provided with a minimum height and width. The resizing takes into account both
     * the size of the component element, and the position based on iframe scrolling.
     *
     * @param {HTMLElement} element The original CMS component element from the storefront.
     * @param {HTMLElement =} componentOverlayElem The overlay component. If none is provided
     */
    updateComponentSizeAndPosition(element: HTMLElement, componentOverlayElem?: HTMLElement): void;
    reBootstrapPage(): void;
    renderPage(isRerender: boolean): void;
    renderSlots(_slotIds?: string[] | string): Promise<any>;
    renderComponent(componentId: string, componentType: string): Promise<string | boolean>;
    renderRemoval(componentId: string, componentType: string, slotId: string): JQuery;
    /**
     * Given a smartEdit component in the storefront layer, its clone in the smartEdit overlay is removed and the pertaining decorators destroyed.
     *
     * @param {Element} element The original CMS component element from the storefront.
     * @param {Element=} parent the closest smartEditComponent parent, expected to be null for the highest elements
     * @param {Object=} oldAttributes The map of former attributes of the element. necessary when the element has mutated since the last creation
     */
    destroyComponent(_component: HTMLElement, _parent?: HTMLElement, oldAttributes?: any): void;
    /**
     * Given a smartEdit component in the storefront layer. An empty clone of it will be created, sized and positioned in the smartEdit overlay
     * then compiled with all eligible decorators for the given perspective (see {@link smarteditServicesModule.interface:IPerspectiveService perspectiveService})
     * @param {Element} element The original CMS component element from the storefront.
     */
    createComponent(element: HTMLElement): void;
    /**
     * Resizes the height of all slots on the page based on the sizes of the components. The new height of the
     * slot is set to the minimum height encompassing its sub-components, calculated by comparing each of the
     * sub-components' top and bottom bounding rectangle values.
     *
     * Slots that do not have components inside still appear in the DOM. If the CMS manager is in a perspective in which
     * slot contextual menus are displayed, slots must have a height. Otherwise, overlays will overlap. Thus, empty slots
     * are given a minimum size so that overlays match.
     *
     * fix CXEC-7224: If we set height of component slot 'auto', the height will be calculated automatically with proper size.
     * So it's not neccessary to call resizeSlots in smartedit.ts for different event.
     */
    private _resizeSlots;
    private _getParentInOverlay;
    private _buildShallowCloneId;
    private _cloneComponent;
    private _createOverlay;
    private _validateComponentAttributesContract;
    private _markSmartEditAsReady;
    private _markSmartEditBootstrap;
    private _isComponentVisible;
    private _reprocessPage;
}
