import { BrowserService } from '@smart/utils';
export declare class PolyfillService {
    private readonly browserService;
    constructor(browserService: BrowserService);
    isEligibleForEconomyMode(): boolean;
    isEligibleForExtendedView(): boolean;
    isEligibleForThrottledScrolling(): boolean;
}
