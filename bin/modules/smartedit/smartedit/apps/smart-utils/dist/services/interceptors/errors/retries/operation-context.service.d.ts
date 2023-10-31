/**
 * @ngdoc service
 * @name @smartutils.services:OperationContextService
 * @description
 * This service provides the functionality to register a url with its associated operation contexts and also finds operation context given an url.
 */
export declare class OperationContextService {
    private store;
    constructor();
    /**
     * @ngdoc method
     * @name @smartutils.services:OperationContextService#register
     * @methodOf @smartutils.services:OperationContextService
     *
     * @description
     * Register a new url with it's associated operationContext.
     *
     * @param {String} url The url that is associated to the operation context.
     * @param {String} operationContext The operation context name that is associated to the given url.
     *
     * @return {Object} operationContextService The operationContextService service
     */
    register(url: string, operationContext: string): this;
    /**
     * @ngdoc method
     * @name @smartutils.services:OperationContextService#findOperationContext
     * @methodOf @smartutils.services:OperationContextService
     *
     * @description
     * Find the first matching operation context for the given url.
     *
     * @param {String} url The request url.
     *
     * @return {String} operationContext
     */
    findOperationContext(url: string): string | null;
}
