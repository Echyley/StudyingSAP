import { CryptographicUtils, ISessionService, IStorageService, LogService, RestServiceFactory, User } from 'smarteditcommons';
/** @internal */
export declare class SessionService extends ISessionService {
    private $log;
    private storageService;
    private cryptographicUtils;
    private USER_DATA_URI;
    private cachedUserHash;
    private whoAmIService;
    private userRestService;
    constructor($log: LogService, restServiceFactory: RestServiceFactory, storageService: IStorageService, cryptographicUtils: CryptographicUtils);
    getCurrentUserDisplayName(): Promise<string>;
    getCurrentUsername(): Promise<string>;
    getCurrentUser(): Promise<User>;
    hasUserChanged(): Promise<boolean>;
    setCurrentUsername(): Promise<void>;
    private getCurrentUserData;
}
