/**
 * @ignore
 * Model containing truncated text properties.
 */
export declare class TruncatedText {
    private text;
    private truncatedText;
    private truncated;
    private ellipsis;
    constructor(text: string, truncatedText: string, truncated: boolean, ellipsis?: string);
    getUntruncatedText(): string;
    getTruncatedText(): string;
    isTruncated(): boolean;
}
