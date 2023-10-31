/** @internal */
export declare class FingerPrintingService {
    private readonly _fingerprint;
    /**
     * Get unique browser fingerprint information encoded in Base64.
     */
    getFingerprint(): string;
    private getUserAgent;
    private getPlugins;
    private hasJava;
    private hasFlash;
    private hasLocalStorage;
    private hasSessionStorage;
    private hasCookie;
    private getTimeZone;
    private getLanguage;
    private getSystemLanguage;
    private hasCanvas;
}
