import { IDecoratorDisplayCondition, IDecoratorService, PromiseUtils, StringUtils } from 'smarteditcommons';
export interface DecoratorMapping {
    [index: string]: string[];
}
/**
 * This service enables and disables decorators. It also maps decorators to SmartEdit component types–regardless if they are enabled or disabled.
 *
 */
export declare class DecoratorService implements IDecoratorService {
    private readonly promiseUtils;
    private readonly stringUtils;
    private _activeDecorators;
    private componentDecoratorsMap;
    constructor(promiseUtils: PromiseUtils, stringUtils: StringUtils);
    /**
     * This method enables a list of decorators for a group of component types.
     * The list to be [enable]{@link DecoratorService#enable} is identified by a matching pattern.
     * The list is enabled when a perspective or referenced perspective that it is bound to is activated/enabled.
     *
     *
     *
     *      decoratorService.addMappings({
     *          '*Suffix': ['decorator1', 'decorator2'],
     *          '.*Suffix': ['decorator2', 'decorator3'],
     *          'MyExactType': ['decorator3', 'decorator4'],
     *          '^((?!Middle).)*$': ['decorator4', 'decorator5']
     *      });
     *
     *
     * @param  map A key-map value; the key is the matching pattern and the value is an array of decorator keys. The key can be an exact type, an ant-like wild card, or a full regular expression:
     */
    addMappings(map: DecoratorMapping): void;
    /**
     * Enables a decorator
     *
     * @param decoratorKey The key that uniquely identifies the decorator.
     * @param displayCondition Returns a promise that will resolve to a boolean that determines whether the decorator will be displayed.
     */
    enable(decoratorKey: string, displayCondition?: IDecoratorDisplayCondition): void;
    /**
     * Disables a decorator
     *
     * @param decoratorKey the decorator key
     */
    disable(decoratorKey: string): void;
    /**
     * This method retrieves a list of decorator keys that is eligible for the specified component type.
     * The list retrieved depends on which perspective is active.
     *
     * This method uses the list of decorators enabled by the [addMappings]{@link DecoratorService#addMappings} method.
     *
     * @param componentType The type of the component to be decorated.
     * @param componentId The id of the component to be decorated.
     * @returns A promise that resolves to a list of decorator keys.
     *
     */
    getDecoratorsForComponent(componentType: string, componentId?: string): Promise<string[]>;
    private getDecoratorPromise;
}
