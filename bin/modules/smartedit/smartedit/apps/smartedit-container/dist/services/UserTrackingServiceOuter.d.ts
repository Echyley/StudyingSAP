import { ISettingsService, LogService, WindowUtils, IUserTrackingService } from 'smarteditcommons';
/** @internal */
export declare class UserTrackingService extends IUserTrackingService {
    private readonly windowUtils;
    private readonly settingService;
    private readonly logService;
    readonly CUSTOMER_KEY = "modelt.customer.code";
    readonly PROJECT_KEY = "modelt.project.code";
    readonly ENVIRONMENT_KEY = "modelt.environment.code";
    readonly TRACKING_URL_KEY = "smartedit.click.tracking.server.url";
    readonly DEFAULT_CUSTOMER = "localCustomer";
    readonly DEFAULT_PROJECT = "localProject";
    readonly DEFAULT_ENVIRONMENT = "localEnvironment";
    readonly DEFAULT_TRACKING_URL = "https://license.hybris.com/collect";
    isInitialized: boolean;
    private _paq;
    constructor(windowUtils: WindowUtils, settingService: ISettingsService, logService: LogService);
    initConfiguration(): Promise<void>;
    trackingUserAction(functionality: string, key: string): void;
    private isEnvInitialized;
    private getSiteId;
}
