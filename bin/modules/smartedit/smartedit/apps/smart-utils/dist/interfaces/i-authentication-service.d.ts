/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
/**
 * @ngdoc service
 * @name @smartutils.services:authenticationService
 *
 * @description
 * The authenticationService is used to authenticate and logout from SmartEdit.
 * It also allows the management of entry points used to authenticate the different resources in the application.
 *
 */
export declare abstract class IAuthenticationService {
    protected reauthInProgress: IReAuthInProgress;
    protected initialized: boolean;
    /**
     * @ngdoc method
     * @name @smartutils.services:authenticationService#authenticate
     * @methodOf @smartutils.services:authenticationService
     *
     * @description
     * Authenticates the current SmartEdit user against the entry point assigned to the requested resource. If no
     * suitable entry point is found, the resource will be authenticated against the
     * {@link resourceLocationsModule.object:DEFAULT_AUTHENTICATION_ENTRY_POINT DEFAULT_AUTHENTICATION_ENTRY_POINT}
     *
     * @param {String} resource The URI identifying the resource to access.
     * @returns {Promise} A promise that resolves if the authentication is successful.
     */
    authenticate(resource: string): Promise<void>;
    /**
     * @ngdoc method
     * @name @smartutils.services:authenticationService#logout
     * @methodOf @smartutils.services:authenticationService
     *
     * @description
     * The logout method removes all stored authentication tokens and redirects to the
     * landing page.
     *
     */
    logout(): Promise<void>;
    isReAuthInProgress(entryPoint: string): Promise<boolean>;
    /**
     * @ngdoc method
     * @name @smartutils.services:authenticationService#setReAuthInProgress
     * @methodOf @smartutils.services:authenticationService
     *
     * @description
     * Used to indicate that the user is currently within a re-authentication flow for the given entry point.
     * This flow is entered by default through authentication token expiry.
     *
     * @param {String} entryPoint The entry point which the user must be re-authenticated against.
     *
     */
    setReAuthInProgress(entryPoint: string): Promise<void>;
    /**
     * @ngdoc method
     * @name @smartutils.services:authenticationService#filterEntryPoints
     * @methodOf @smartutils.services:authenticationService
     *
     * @description
     * Will retrieve all relevant authentication entry points for a given resource.
     * A relevant entry point is an entry value of the authenticationMap found in {@link @smartutils.sharedDataService sharedDataService}.The key used in that map is a regular expression matching the resource.
     * When no entry point is found, the method returns the {@link resourceLocationsModule.object:DEFAULT_AUTHENTICATION_ENTRY_POINT DEFAULT_AUTHENTICATION_ENTRY_POINT}
     * @param {string} resource The URL for which a relevant authentication entry point must be found.
     */
    filterEntryPoints(resource: string): Promise<string[]>;
    /**
     * @ngdoc method
     * @name @smartutils.services:authenticationService##isAuthEntryPoint
     * @methodOf @smartutils.services:authenticationService
     *
     * @description
     * Indicates if the resource URI provided is one of the registered authentication entry points.
     *
     * @param {String} resource The URI to compare
     * @returns {Boolean} Flag that will be true if the resource URI provided is an authentication entry point.
     */
    isAuthEntryPoint(resource: string): Promise<boolean>;
    /**
     * @ngdoc method
     * @name @smartutils.services:authenticationService##isAuthenticated
     * @methodOf @smartutils.services:authenticationService
     *
     * @description
     * Indicates if the resource URI provided maps to a registered authentication entry point and the associated entry point has an authentication token.
     *
     * @param {String} resource The URI to compare
     * @returns {Boolean} Flag that will be true if the resource URI provided maps to an authentication entry point which has an authentication token.
     */
    isAuthenticated(url: string): Promise<boolean>;
}
export interface IReAuthInProgress {
    [endPoint: string]: boolean;
}
