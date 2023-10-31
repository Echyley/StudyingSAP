/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
/**
 * @ngdoc interface
 * @name @smartutils.interfaces:IAuthToken
 * @description
 * Interface for Auth token
 */
export interface IAuthToken {
    access_token: string;
    expires_in?: number;
    refresh_token?: string;
    scope?: string;
    token_type: string;
}
/**
 * @ngdoc interface
 * @name @smartutils.interfaces:IStorageService
 * @description
 * Interface for StorageService
 */
export declare abstract class IStorageService {
    /**
     * @ngdoc method
     * @name @smartutils.interfaces:IStorageService#isInitialized
     * @methodOf @smartutils.interfaces:IStorageService
     *
     * @description
     * This method is used to determine if the storage service has been initialized properly. It
     * makes sure that the smartedit-sessions cookie is available in the browser.
     *
     * @returns {Boolean} Indicates if the storage service was properly initialized.
     */
    isInitialized(): Promise<boolean>;
    /**
     * @ngdoc method
     * @name @smartutils.interfaces:IStorageService#storeAuthToken
     * @methodOf @smartutils.interfaces:IStorageService
     *
     * @description
     * This method creates and stores a new key/value entry. It associates an authentication token with a
     * URI.
     *
     * @param {String} authURI The URI that identifies the resource(s) to be authenticated with the authToken. Will be used as a key.
     * @param {String} auth The token to be used to authenticate the user in the provided URI.
     */
    storeAuthToken(authURI: string, auth: IAuthToken): Promise<void>;
    /**
     * @ngdoc method
     * @name @smartutils.interfaces:IStorageService#getAuthToken
     * @methodOf @smartutils.interfaces:IStorageService
     *
     * @description
     * This method is used to retrieve the authToken associated with the provided URI.
     *
     * @param {String} authURI The URI for which the associated authToken is to be retrieved.
     * @returns {String} The authToken used to authenticate the current user in the provided URI.
     */
    getAuthToken(authURI: string): Promise<IAuthToken | undefined>;
    /**
     * @ngdoc method
     * @name @smartutils.interfaces:IStorageService#removeAuthToken
     * @methodOf @smartutils.interfaces:IStorageService
     *
     * @description
     * Removes the authToken associated with the provided URI.
     *
     * @param {String} authURI The URI for which its authToken is to be removed.
     */
    removeAuthToken(authURI: string): Promise<void>;
    /**
     * @ngdoc method
     * @name @smartutils.interfaces:IStorageService#removeAllAuthTokens
     * @methodOf @smartutils.interfaces:IStorageService
     *
     * @description
     * This method removes all authURI/authToken key/pairs from the storage service.
     */
    removeAllAuthTokens(): Promise<void>;
    /**
     * @ngdoc method
     * @name @smartutils.interfaces:IStorageService#getValueFromLocalStorage
     * @methodOf @smartutils.interfaces:IStorageService
     *
     * @description
     * Retrieves the value stored in the cookie identified by the provided name.
     */
    getValueFromLocalStorage(cookieName: string, isEncoded: boolean): Promise<any>;
    setValueInLocalStorage(cookieName: string, value: any, encode: boolean): Promise<void>;
    /**
     * @ngdoc method
     * @name @smartutils.interfaces:IStorageService#setItem
     * @methodOf @smartutils.interfaces:IStorageService
     *
     * @description
     * This method is used to store the item.
     *
     * @param {String} key The key of the item.
     * @param {any} value The value of the item.
     */
    setItem(key: string, value: any): Promise<void>;
    /**
     * @ngdoc method
     * @name @smartutils.interfaces:IStorageService#getItem
     * @methodOf @smartutils.interfaces:IStorageService
     *
     * @description
     * Retrieves the value for a given key.
     *
     * @param {String} key The key of the item.
     *
     * @returns {Promise<any>} A promise that resolves to the item value.
     */
    getItem(key: string): Promise<any>;
}
