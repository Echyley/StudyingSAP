import { TruncatedText } from '../../dtos/TruncatedText';
/**
 * Service containing truncate string functions.
 * @internal
 */
export declare class TextTruncateService {
    /**
     * Truncates text to the nearest word depending on character length.
     * Truncates below character length.
     *
     * @param limit index in text to truncate to
     */
    truncateToNearestWord(limit: number, text: string, ellipsis?: string): TruncatedText;
    private getPositionOfCharacters;
}
