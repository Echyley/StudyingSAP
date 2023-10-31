import { StringUtils as ParentStringUtils } from '@smart/utils';
/**
 * A collection of utility methods for windows.
 */
export declare class StringUtils extends ParentStringUtils {
    /**
     * Remove breaks and space.
     */
    sanitizeHTML(text: string): string;
    /**
     * Generates a unique string based on system time and a random generator.
     */
    generateIdentifier(): string;
    /**
     * Creates a base-64 encoded ASCII string from the object or string.
     */
    getEncodedString(input: any): string;
    /**
     * **Deprecated since 2005, use {@link https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/String/Trim API}.**
     *
     * Removes spaces at the beginning and end of a given string.
     *
     * @returns Modified string without spaces at the beginning and the end
     *
     * @deprecated
     */
    trim(aString: string): string;
    /**
     * Escapes &, <, >, " and ' characters.
     */
    escapeHtml(str: string | number): string | number;
    resourceLocationToRegex(str: string): RegExp;
}
export declare const stringUtils: StringUtils;
