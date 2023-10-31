/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { IRestService } from './i-rest-service';
/**
 * @ngdoc interface
 * @name @smartutils.interfaces:restServiceFactory
 *
 * @description
 * A factory used to generate a REST service wrapper for a given resource URL, providing a means to perform HTTP
 * operations (GET, POST, etc) for the given resource.
 */
export interface IRestServiceFactory {
    /**
     * @ngdoc method
     * @name @smartutils.interfaces:restServiceFactory#get<T>
     * @methodOf @smartutils.interfaces:restServiceFactory
     *
     * @description
     * A factory method used to create a REST service of type {@link @smartutils.interfaces:IRestService IRestService<T>}
     * that points to the given resource URI.
     * The returned service wraps an httpClient object. As opposed to a httpClient, the REST services retrieved from the
     * restServiceFactory can only take one object argument. The object argument will automatically be split
     * into a parameter object and a payload object before they are delegated to the wrapped httpClient object.
     * If the domain is set, the domain is prepended to the given URI.
     *
     * @param {String} uri The URI of the REST service to be retrieved.
     * @param {String=} identifier An optional parameter. The name of the placeholder that is appended to the end
     * of the URI if the name is not already provided as part of the URI. The default value is "identifier".
     * <pre>
     * 	if identifier is "resourceId" and uri is "resource/:resourceId/someValue", the target URI will remain the same.
     * 	if identifier is "resourceId" and uri is "resource", the target URI will be "resource/:resourceId".
     * </pre>
     *
     * @returns {IRestService} A {@link @smartutils.interfaces:IRestService IRestService} around a {@link https://angular.io/guide/http HttpClient}
     */
    get<T>(uri: string, identifier?: string): IRestService<T>;
    /**
     * @ngdoc method
     * @name @smartutils.interfaces:restServiceFactory#setDomain
     * @methodOf @smartutils.interfaces:restServiceFactory
     *
     * @deprecated since 2005
     *
     * @description
     * When working with multiple services that reference the same domain, it is best to only specify relative
     * paths for the services and specify the context-wide domain in a separate location. The {@link
     * @smartutils.interfaces:restServiceFactory#get get} method of the {@link
     * @smartutils.interfaces:restServiceFactory restServiceFactory} will then prefix the specified service
     * URIs with the domain and a forward slash.
     *
     * @param {String} domain The context-wide domain that all URIs will be prefixed with when services are
     * created/when a service is created
     *
     */
    setDomain?(domain: string): void;
    /**
     * @ngdoc method
     * @name @smartutils.interfaces:restServiceFactory#setBasePath
     * @methodOf @smartutils.interfaces:restServiceFactory
     *
     * @description
     * Set base path for a particular service and every request that consists of the absolute path
     * will be automatically prefixed with the provided base path.
     * Base path prefixes only absolute URIs and will not override requests that contain protocols.
     *
     * @param {String} basePath The service-wide path that will prefix all absolute URIs for the particular service.
     */
    setBasePath?(basePath: string): void;
    /**
     * @ngdoc method
     * @name @smartutils.interfaces:restServiceFactory#setGlobalBasePath
     * @methodOf @smartutils.interfaces:restServiceFactory
     *
     * @description
     * The global base path will prefix all services` URIs, unless the {@link
     * @smartutils.interfaces:restServiceFactory#setBasePath setBasePath} method of the {@link
     * @smartutils.interfaces:restServiceFactory restServiceFactory} was invoked for any
     * particular service.
     * Once set, the global base path cannot be overridden.
     * Global base path prefixes only absolute URIs and will not override requests that contain protocols.
     *
     * @param {String} globalDomain The top-level domain that all URIs will be prefixed with.
     */
    setGlobalBasePath?(globalDomain: string): void;
}
