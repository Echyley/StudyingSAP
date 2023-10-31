/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { User } from '../dtos';
/**
 * @ngdoc interface
 * @name smarteditServicesModule.interface:ISessionService
 * @description
 * The ISessionService provides information related to the current session
 * and the authenticated user (including a user readable and writeable languages).
 */
export declare abstract class ISessionService {
    /**
     * @ngdoc method
     * @name smarteditServicesModule.interface:ISessionService#getCurrentUsername
     * @methodOf smarteditServicesModule.interface:ISessionService
     *
     * @description
     * Returns the username, previously mentioned as "principalUID",
     * associated to the authenticated user.
     *
     * @returns {Promise<string>} A promise resolving to the username,
     * previously mentioned as "principalUID", associated to the
     * authenticated user.
     */
    getCurrentUsername(): Promise<string>;
    /**
     * @ngdoc method
     * @name smarteditServicesModule.interface:ISessionService#getCurrentUserDisplayName
     * @methodOf smarteditServicesModule.interface:ISessionService
     *
     * @description
     * Returns the displayed name associated to the authenticated user.
     *
     * @returns {Promise<string>} A promise resolving to the displayed name
     * associated to the authenticated user.
     */
    getCurrentUserDisplayName(): Promise<string>;
    /**
     * @ngdoc method
     * @name smarteditServicesModule.interface:ISessionService#getCurrentUser
     * @methodOf smarteditServicesModule.interface:ISessionService
     *
     * @description
     * Returns the data of the current authenticated user.
     * Also note that as part of the User object returned by this method contains
     * the list of readable and writeable languages available to the user.
     *
     * @returns {Promise<User>} A promise resolving to the data of the current
     * authenticated user.
     */
    getCurrentUser(): Promise<User>;
    /**
     * @ngdoc method
     * @name smarteditServicesModule.interface:ISessionService#hasUserChanged
     * @methodOf smarteditServicesModule.interface:ISessionService
     *
     * @description
     * Returns boolean indicating whether the current user is different from
     * the last authenticated one.
     *
     * @returns {Promise<boolean>} Boolean indicating whether the current user is
     * different from the last authenticated one.
     */
    hasUserChanged(): Promise<boolean>;
    /**
     * @ngdoc method
     * @name smarteditServicesModule.interface:ISessionService#resetCurrentUserData
     * @methodOf smarteditServicesModule.interface:ISessionService
     *
     * @description
     * Reset all data associated to the authenticated user.
     * to the authenticated user.
     *
     * @return {Promise<void>} returns an empty promise.
     */
    resetCurrentUserData(): Promise<void>;
    /**
     * @ngdoc method
     * @name smarteditServicesModule.interface:ISessionService#setCurrentUsername
     * @methodOf smarteditServicesModule.interface:ISessionService
     *
     * @description
     * Set the username, previously mentioned as "principalUID", associated
     * to the authenticated user.
     *
     * @return {Promise<void>} returns an empty promise.
     */
    setCurrentUsername(): Promise<void>;
}
