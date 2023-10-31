import { CrossFrameEventService, IFeatureService, IPermissionService, IPerspective, IPerspectiveService, IStorageService, IWaitDialogService, LogService, SmarteditRoutingService, SystemEventService } from 'smarteditcommons';
/** @internal */
export declare class PerspectiveService extends IPerspectiveService {
    private routingService;
    private logService;
    private systemEventService;
    private featureService;
    private waitDialogService;
    private storageService;
    private crossFrameEventService;
    private permissionService;
    private readonly PERSPECTIVE_COOKIE_NAME;
    private readonly INITIAL_SWITCHTO_ARG;
    private data;
    private immutablePerspectives;
    private perspectives;
    private NON_PERSPECTIVE_OBJECT;
    constructor(routingService: SmarteditRoutingService, logService: LogService, systemEventService: SystemEventService, featureService: IFeatureService, waitDialogService: IWaitDialogService, storageService: IStorageService, crossFrameEventService: CrossFrameEventService, permissionService: IPermissionService);
    register(configuration: IPerspective): Promise<void>;
    getPerspectives(): Promise<IPerspective[]>;
    hasActivePerspective(): Promise<boolean>;
    getActivePerspective(): IPerspective;
    isEmptyPerspectiveActive(): Promise<boolean>;
    switchTo(key: string): Promise<void>;
    selectDefault(): Promise<void>;
    refreshPerspective(): Promise<void>;
    getActivePerspectiveKey(): Promise<string>;
    isHotkeyEnabledForActivePerspective(): Promise<boolean>;
    private _addPerspectiveSelectorWidget;
    private _addDefaultPerspectives;
    private _registerEventHandlers;
    private _validate;
    private _findByKey;
    private _fetchAllFeatures;
    private _enableOrDisableFeature;
    /**
     * Takes care of sending EVENT_PERSPECTIVE_UNLOADING when perspectives change.
     *
     * This function tracks the "key" argument in calls to switchTo(..) function in order to detect when a
     * perspective is being switched.
     */
    private _handleUnloadEvent;
    private _retrievePerspective;
    private _changeActivePerspective;
    private _disableAllFeaturesForPerspective;
    private _clearPerspectiveFeatures;
}
