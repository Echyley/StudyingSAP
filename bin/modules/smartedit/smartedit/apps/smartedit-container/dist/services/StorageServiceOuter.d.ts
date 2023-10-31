import { CryptographicUtils, FingerPrintingService, IAuthToken, IStorageService, LogService, WindowUtils } from 'smarteditcommons';
export interface ISessionAuth {
    [index: string]: IAuthToken | any;
}
/** @internal */
export declare class StorageService extends IStorageService {
    private logService;
    private windowUtils;
    private cryptographicUtils;
    private fingerPrintingService;
    private SMARTEDIT_SESSIONS;
    private CUSTOM_PROPERTIES;
    constructor(logService: LogService, windowUtils: WindowUtils, cryptographicUtils: CryptographicUtils, fingerPrintingService: FingerPrintingService);
    isInitialized(): Promise<boolean>;
    storeAuthToken(authURI: string, auth: IAuthToken): Promise<void>;
    getAuthToken(authURI: string): Promise<IAuthToken>;
    removeAuthToken(authURI: string): Promise<void>;
    removeAllAuthTokens(): Promise<void>;
    getValueFromCookie(cookieName: string, isEncoded: boolean): Promise<any>;
    getValueFromLocalStorage(cookieName: string, isEncoded: boolean): Promise<any>;
    getAuthTokens(): ISessionAuth;
    putValueInCookie(cookieName: string, value: any, encode: boolean): void;
    setValueInLocalStorage(cookieName: string, value: any, encode: boolean): Promise<void>;
    setItem(key: string, value: any): Promise<void>;
    getItem(key: string): Promise<any>;
    private _removeAllAuthTokens;
    private _getValueFromLocalStorage;
    private _setSmarteditSessions;
    private _setValueInLocalStorage;
}
