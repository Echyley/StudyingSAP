import { LogService } from '@smart/utils';
export interface InterceptorHelperConfig {
    url: string;
    config?: InterceptorHelperConfig;
}
export declare class InterceptorHelper {
    private readonly logService;
    constructor(logService: LogService);
    handleRequest(config: InterceptorHelperConfig, callback: () => Promise<any>): InterceptorHelperConfig | Promise<any>;
    handleResponse(response: InterceptorHelperConfig, callback: () => Promise<any>): InterceptorHelperConfig | Promise<any>;
    handleResponseError(response: InterceptorHelperConfig, callback: () => Promise<any>): InterceptorHelperConfig | Promise<any>;
    private _isEligibleForInterceptors;
    private _handle;
}
