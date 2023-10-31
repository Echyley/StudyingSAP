/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { StringUtils as ParentStringUtils } from '@smart/utils';

/**
 * A collection of utility methods for windows.
 */
export class StringUtils extends ParentStringUtils {
    /**
     * Remove breaks and space.
     */
    sanitizeHTML(text: string): string {
        if (stringUtils.isBlank(text)) {
            return text;
        }

        return text
            .replace(/(\r\n|\n|\r)/gm, '')
            .replace(/>\s+</g, '><')
            .replace(/<\/br\>/g, '');
    }

    /**
     * Generates a unique string based on system time and a random generator.
     */
    generateIdentifier(): string {
        let d = new Date().getTime();
        if (window.performance && typeof window.performance.now === 'function') {
            d += window.performance.now(); // use high-precision timer if available
        }
        const uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
            const r = (d + Math.random() * 16) % 16 | 0;
            d = Math.floor(d / 16);
            return (c === 'x' ? r : (r & 0x3) | 0x8).toString(16);
        });
        return uuid;
    }

    /**
     * Creates a base-64 encoded ASCII string from the object or string.
     */
    getEncodedString(input: any): string {
        return this.encode(input);
    }

    /**
     * **Deprecated since 2005, use {@link https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/String/Trim API}.**
     *
     * Removes spaces at the beginning and end of a given string.
     *
     * @returns Modified string without spaces at the beginning and the end
     *
     * @deprecated
     */
    trim(aString: string): string {
        return aString.trim();
    }

    /**
     * Escapes &, <, >, " and ' characters.
     */
    escapeHtml(str: string | number): string | number {
        if (typeof str === 'string') {
            return str
                .replace(/&/g, '&amp;')
                .replace(/>/g, '&gt;')
                .replace(/</g, '&lt;')
                .replace(/"/g, '&quot;')
                .replace(/'/g, '&apos;');
        }
        return str;
    }

    resourceLocationToRegex(str: string): RegExp {
        return new RegExp(str.replace(/\/:[^\/]*/g, '/.*'));
    }
}

export const stringUtils = new StringUtils();
