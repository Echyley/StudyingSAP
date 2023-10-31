///
/// Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
///

export const LOG = false;

export function log(...params) {
    if (LOG) {
        console.log(...params);
    }
}


const invalidProtocolRegex = /^(%20|\s)*(javascript|data|vbscript)/im;
const ctrlCharactersRegex = /[^\x20-\x7EÀ-ž]/gim;
const urlSchemeRegex = /^([^:]+):/gm;
const relativeFirstCharacters = [".", "/"];

function isRelativeUrlWithoutProtocol(url: string): boolean {
    return relativeFirstCharacters.indexOf(url[0]) > -1;
}

export function getCookie(name: string): string {
    const searchRegex = new RegExp(getCookieRegex(name));
    const match = searchRegex.exec(document.cookie);
    const cookieValue = match[1];
    return cookieValue ? decodeURI(cookieValue) : cookieValue;
}

function getCookieRegex(name: string): string {
    return `(?:(?:^|.*;\\s*)${name}\\s*\\=\\s*([^;]*).*$)|^.*$`;
}

/**
 * Sanitizing function based on braintree/sanitize-url sanitizer
 * @param {string} string
 */
export function sanitizeUrl(url?: string): string {
    if (!url) {
        return "#";
    }

    const sanitizedUrl = url.replace(ctrlCharactersRegex, "").trim();

    if (isRelativeUrlWithoutProtocol(sanitizedUrl)) {
        return sanitizedUrl;
    }

    const urlSchemeParseResults = sanitizedUrl.match(urlSchemeRegex);

    if (!urlSchemeParseResults) {
        return sanitizedUrl;
    }

    const urlScheme = urlSchemeParseResults[0];

    if (invalidProtocolRegex.test(urlScheme)) {
        return "#";
    }

    return sanitizedUrl;
}