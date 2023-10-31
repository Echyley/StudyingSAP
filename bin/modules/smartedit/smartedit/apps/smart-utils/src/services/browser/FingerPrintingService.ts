/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { stringUtils } from '../../utils';
/** @internal */
export class FingerPrintingService {
    private readonly _fingerprint: string = stringUtils.encode(
        this.getUserAgent() +
            this.getPlugins() +
            this.hasJava() +
            this.hasFlash() +
            this.hasLocalStorage() +
            this.hasSessionStorage() +
            this.hasCookie() +
            this.getTimeZone() +
            this.getLanguage() +
            this.getSystemLanguage() +
            this.hasCanvas()
    );

    /**
     * Get unique browser fingerprint information encoded in Base64.
     */
    getFingerprint(): string {
        return this._fingerprint;
    }

    private getUserAgent(): string {
        return navigator.userAgent;
    }

    private getPlugins(): string {
        const plugins = [];
        for (let i = 0, l = navigator.plugins.length; i < l; i++) {
            if (navigator.plugins[i]) {
                plugins.push(navigator.plugins[i].name);
            }
        }
        return plugins.join(',');
    }

    private hasJava(): boolean {
        return navigator.javaEnabled();
    }

    private hasFlash(): boolean {
        return !!navigator.plugins.namedItem('Shockwave Flash');
    }

    private hasLocalStorage(): boolean {
        try {
            const hasLs = 'test-has-ls';
            localStorage.setItem(hasLs, hasLs);
            localStorage.removeItem(hasLs);
            return true;
        } catch (exception) {
            return false;
        }
    }

    private hasSessionStorage(): boolean {
        try {
            const hasSs = 'test-has-ss';
            sessionStorage.setItem(hasSs, hasSs);
            sessionStorage.removeItem(hasSs);
            return true;
        } catch (exception) {
            return false;
        }
    }

    private hasCookie(): boolean {
        return navigator.cookieEnabled;
    }

    private getTimeZone(): string {
        return String(String(new Date()).split('(')[1]).split(')')[0];
    }

    private getLanguage(): string {
        return navigator.language;
    }

    private getSystemLanguage(): string {
        return window.navigator.language;
    }

    private hasCanvas(): boolean {
        try {
            const elem = document.createElement('canvas');
            return !!(elem.getContext && elem.getContext('2d'));
        } catch (e) {
            return false;
        }
    }
}
