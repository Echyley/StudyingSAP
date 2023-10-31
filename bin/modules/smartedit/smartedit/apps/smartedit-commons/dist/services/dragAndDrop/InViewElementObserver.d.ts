/// <reference types="jquery" />
import { LogService } from '@smart/utils';
import { IMousePosition } from './types';
export interface QueueElement {
    component: HTMLElement;
    isIntersecting: boolean;
}
/**
 * InViewElementObserver maintains a collection of eligible DOM elements considered "in view".
 * An element is considered eligible if matches at least one of the selectors passed to the service.
 * An eligible element is in view when and only when it intersects with the view port of the window frame.
 * This services provides as well convenience methods around "in view" components:
 */
export declare class InViewElementObserver {
    private readonly logService;
    private readonly document;
    private readonly yjQuery;
    private mutationObserver;
    private intersectionObserver;
    private componentsQueue;
    private readonly selectors;
    private hasClassBasedSelectors;
    constructor(logService: LogService, document: Document, yjQuery: JQueryStatic);
    /**
     * Retrieves the element targeted by the given mousePosition.
     * On some browsers, the native Javascript API will not work when targeting
     * an element inside an iframe from the container if a container overlay blocks it.
     * In such case we resort to returning the targeted element amongst the list of "in view" elements
     */
    elementFromPoint(mousePosition: IMousePosition): Element;
    /**
     * Declares a new yjQuery selector in order to observe more elements.
     */
    addSelector(selector: string, callback?: () => void): () => void;
    /**
     * Retrieves the full list of eligible DOM elements even if they are not "in view".
     */
    getAllElements(): Element[];
    /**
     * Retrieves the list of currently "in view" DOM elements.
     */
    getInViewElements(): Element[];
    private restart;
    private stopListener;
    private initListener;
    private _aggregateAddedOrRemovedNodes;
    private _aggregateMutationsOnClass;
    private _mutationObserverCallback;
    private _updateQueue;
    private _newMutationObserver;
    private _newIntersectionObserver;
    private _getJQuerySelector;
    private _isEligibleComponent;
    private _getEligibleElements;
    private _getAllEligibleChildren;
    private _getComponentIndexInQueue;
    private _isInDOM;
}
