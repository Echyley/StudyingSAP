import { ActivatedRoute } from '@angular/router';
import { ICatalogVersionPermissionService, IExperience, IExperienceParams, IExperienceService, ISharedDataService, LogService, SystemEventService } from 'smarteditcommons';
/**
 * Validates if user has permission on current catalog.
 *
 * Implemented by Route Guards.
 */
export declare class CatalogAwareRouteResolverHelper {
    private logService;
    private route;
    private systemEventService;
    private experienceService;
    private sharedDataService;
    private catalogVersionPermissionService;
    /**
     * @internal
     * @ignore
     *
     * Convert to instance method after 2105 deprecation period has been exceeded.
     */
    static executeAndCheckCatalogPermissions(catalogVersionPermissionService: ICatalogVersionPermissionService, logService: LogService, experienceService: IExperienceService, systemEventService: SystemEventService, operation: () => Promise<any>): Promise<boolean>;
    /**
     * @internal
     * @ignore
     */
    static checkExperienceIsSet(experienceService: IExperienceService, sharedDataService: ISharedDataService): Promise<IExperience>;
    /**
     * @internal
     * @ignore
     */
    static buildExperienceFromRoute(experienceService: IExperienceService, params: IExperienceParams): Promise<IExperience>;
    constructor(logService: LogService, route: ActivatedRoute, systemEventService: SystemEventService, experienceService: IExperienceService, sharedDataService: ISharedDataService, catalogVersionPermissionService: ICatalogVersionPermissionService);
    /**
     * Checks presence of a stored experience.
     *
     * It will reject if the user doesn't have a read permission to the current catalog version.
     * Consumer can redirect current user to the Landing Page by handling the rejection.
     *
     * If the user has read permission for the catalog version then EVENTS.EXPERIENCE_UPDATE is sent, but only when the experience has been changed.
     */
    storefrontResolve(): Promise<boolean>;
    /**
     * Initializes new experience based on route params.
     *
     * It will reject if the user doesn't have a read permission to the current catalog version.
     * Consumer can redirect current user to the Landing Page by handling the rejection.
     *
     * If the user has read permission for the catalog version then EVENTS.EXPERIENCE_UPDATE is sent, but only when the experience has been changed.
     */
    experienceFromPathResolve(params?: IExperienceParams): Promise<boolean>;
    /**
     * Runs operation that sets the experience and then resolves to true if the user has read permissions on current catalog.
     *
     * @internal
     * @ignore
     */
    private executeAndCheckCatalogPermissions;
    /**
     * Resolves with the existing experience if it is set, otherwise rejects.
     *
     * @internal
     * @ignore
     */
    private checkExperienceIsSet;
    /**
     * Creates and sets an experience based on active route params.
     *
     * @internal
     * @ignore
     */
    private buildExperienceFromRoute;
}
