import { IPositionRegistry, YJQuery } from 'smarteditcommons';
/**
 * Service aimed at determining the list of registered DOM elements that have been repositioned, regardless of how, since it was last queried
 */
export declare class PositionRegistry implements IPositionRegistry {
    private yjQuery;
    private positionRegistry;
    constructor(yjQuery: YJQuery);
    /**
     * registers a given node in the repositioning registry
     */
    register(element: HTMLElement): void;
    /**
     * unregisters a given node from the repositioning registry
     */
    unregister(element: HTMLElement): void;
    /**
     * Method returning the list of nodes having been repositioned since last query
     */
    getRepositionedComponents(): HTMLElement[];
    /**
     * unregisters all nodes and cleans up
     */
    dispose(): void;
    /**
     * for e2e test purposes
     */
    _listenerCount(): number;
    private floor;
    private calculatePositionHash;
}
