import { GenericEditorMediaType } from 'smarteditcommons';
/**
 * This service provides functionality for Media Util.
 */
export declare class MediaUtilService {
    /**
     * Limitation the specified file format against custom field and its allowMediaType.
     *
     * @param fieldAllowMediaType The file format to be validated.
     * @returns string[] list of accepted file types.
     */
    getAcceptedFileTypes(fieldAllowMediaType: GenericEditorMediaType): string[];
}
