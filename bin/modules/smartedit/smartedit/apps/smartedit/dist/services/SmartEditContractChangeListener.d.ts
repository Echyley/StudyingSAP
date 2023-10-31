/// <reference types="jquery" />
import { ComponentEntry, IPageInfoService, IPositionRegistry, IResizeListener, ISmartEditContractChangeListener, JQueryUtilsService, LogService, PolyfillService, RemoveComponentObject, SystemEventService, TypedMap, IComponentHandlerService, IPageTreeNodeService } from 'smarteditcommons';
export declare const DEFAULT_REPROCESS_TIMEOUT = 100;
export declare const DEFAULT_PROCESS_QUEUE_POLYFILL_INTERVAL = 250;
export declare const DEFAULT_CONTRACT_CHANGE_LISTENER_INTERSECTION_OBSERVER_OPTIONS: {
    root: HTMLElement;
    rootMargin: string;
    threshold: number;
};
export declare const DEFAULT_CONTRACT_CHANGE_LISTENER_PROCESS_QUEUE_THROTTLE = 500;
export declare class SmartEditContractChangeListener implements ISmartEditContractChangeListener {
    private readonly yjQueryUtilsService;
    private readonly componentHandlerService;
    private readonly pageInfoService;
    private readonly resizeListener;
    private readonly positionRegistry;
    private readonly logService;
    private readonly yjQuery;
    private readonly systemEventService;
    private readonly polyfillService;
    private readonly pageTreeNodeService;
    private readonly smartEditAttributeNames;
    private readonly MUTATION_OBSERVER_OPTIONS;
    private mutationObserver;
    private intersectionObserver;
    private repositionListener;
    private currentPage;
    private enableExtendedView;
    private _componentsAddedCallback;
    private _componentsRemovedCallback;
    private _componentResizedCallback;
    private _componentRepositionedCallback;
    private _onComponentChangedCallback;
    private _pageChangedCallback;
    private readonly _throttledProcessQueue;
    private readonly componentsQueue;
    private economyMode;
    private resizeObserver;
    private prevBodyWidth;
    constructor(yjQueryUtilsService: JQueryUtilsService, componentHandlerService: IComponentHandlerService, pageInfoService: IPageInfoService, resizeListener: IResizeListener, positionRegistry: IPositionRegistry, logService: LogService, yjQuery: JQueryStatic, systemEventService: SystemEventService, polyfillService: PolyfillService, pageTreeNodeService: IPageTreeNodeService);
    _newMutationObserver(callback: MutationCallback): MutationObserver;
    _newIntersectionObserver(callback: IntersectionObserverCallback): IntersectionObserver;
    _addToComponentQueue(entry: IntersectionObserverEntry): void;
    _componentsQueueLength(): number;
    isExtendedViewEnabled(): boolean;
    /**
     * Set the 'economyMode' to true for better performance.
     * In economyMode, resize/position listeners are not present, and the current economyMode value is passed to the add /remove callbacks.
     */
    setEconomyMode(_mode: boolean): void;
    initListener(): void;
    _processQueue(): void;
    isIntersecting(obj: ComponentEntry): boolean;
    _rawProcessQueue(): void;
    _addComponents(componentsObj: ComponentEntry[]): void;
    _removeComponents(componentsObj: RemoveComponentObject[], forceRemoval?: boolean): void;
    _registerSizeAndPositionListeners(component: HTMLElement): void;
    _unregisterSizeAndPositionListeners(component: HTMLElement): void;
    stopListener(): void;
    _stopExpendableListeners(): void;
    _startExpendableListeners(): void;
    onComponentsAdded(callback: (components: HTMLElement[], isEconomyMode: boolean) => void): void;
    onComponentsRemoved(callback: (components: {
        component: HTMLElement;
        parent: HTMLElement;
    }[], isEconomyMode: boolean) => void): void;
    onComponentChanged(callback: (component: HTMLElement, oldAttributes: TypedMap<string>) => void): void;
    onComponentResized(callback: (component: HTMLElement) => void): void;
    onComponentRepositioned(callback: (component: HTMLElement) => void): void;
    onPageChanged(callback: (pageUUID: string) => void): void;
    private loopObservedQueue;
    private publishSuccess;
    private aggregateAddedOrRemovedNodesAndTheirParents;
    private aggregateMutationsOnChangedAttributes;
    /**
     * Verifies whether the entry is a smartedit complient element.
     */
    private isSmarteditNode;
    /**
     * Verifies whether at least one of the changed attributes is a smartedit attribute.
     */
    private isSmarteditAttributeChanged;
    /**
     * Verifies whether the entry is not a smartedit element anymore.
     * It checks that all smartedit related attributes were removed and the
     * entry.node is still in the componentsQueue.
     */
    private wasSmarteditNode;
    private mutationsHasPageChange;
    private applyToSelfAndAllChildren;
    private repairParentResizeListener;
    private executeCallback;
    private handleSmartEditNode;
    private handleNoSmarteditNode;
    private loopAggregateNodes;
    private loopMutationsOnChangedAttributes;
    private mutationObserverCallback;
    private resizeObserverCallback;
}
