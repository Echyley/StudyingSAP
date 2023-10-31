/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import * as $script from 'scriptjs';

export default abstract class WebUtils {
    static readonly webappScriptId = 'smartedit-injector';
    static readonly webappScriptName = 'webApplicationInjector';
    static readonly smarteditMainCssId = 'smarteditMainCss';

    /**
     * Get script element of webApplicationInjector.js from storefront page
     */
    static getWebappScriptElementFromDocument(document: Document): HTMLScriptElement {
        if (document.currentScript) {
            if (!(document.currentScript instanceof HTMLScriptElement)) {
                throw new Error(
                    'getWebappScriptElementFromDocument() found non html script element'
                );
            }
            return document.currentScript;
        }

        const scriptElement = document.querySelector<HTMLScriptElement>(
            `script#${WebUtils.webappScriptId}`
        );
        if (scriptElement) {
            return scriptElement;
        }

        // This will be the case for IE (if no smartedit-injector attr used)
        const nodeListElements: NodeListOf<HTMLScriptElement> = document.querySelectorAll(
            `script[src*=${WebUtils.webappScriptName}]`
        );
        if (nodeListElements.length !== 1) {
            throw new Error(
                `SmartEdit unable to load - invalid ${WebUtils.webappScriptName} script tag`
            );
        }
        return nodeListElements.item(0);
    }

    static extractQueryParameter(url: string, queryParameterName: string): string {
        const queryParameters = {} as any;
        url.replace(
            /([?&])([^&=]+)=([^&]*)?/g,
            ($0: any, $1: any, parameterKey: any, parameterValue: any) => {
                queryParameters[parameterKey] = parameterValue;
                return '';
            }
        );
        return queryParameters[queryParameterName];
    }

    static injectJS(srcs: string[], index = 0): void {
        if (srcs.length && index < srcs.length) {
            WebUtils.getScriptJs()(srcs[index], function () {
                WebUtils.injectJS(srcs, index + 1);
            });
        }
    }

    static injectCSS(body: HTMLBodyElement, cssPaths: string[]): void {
        if (!cssPaths) {
            return;
        }
        for (let i = 0; i < cssPaths.length; i++) {
            if (WebUtils.isFioriWithoutDark(cssPaths[i])) {
                continue;
            }
            let id = `themeCss${i}`;
            const href = cssPaths[i];
            if (cssPaths[i].includes('main.css')) {
                id = WebUtils.smarteditMainCssId;
                if (document.getElementById(id)) {
                    continue;
                }
            }
            WebUtils.injectCSSHelper(id, href, body);
        }
    }

    static removeThemeCSS(): void {
        const cssThemeLink = document.getElementById('themeCss0');
        const cssCustomThemeLink = document.getElementById('themeCss1');
        if (cssThemeLink && cssCustomThemeLink) {
            cssThemeLink?.remove();
            cssCustomThemeLink?.remove();
        }
    }

    static getScriptJs(): any {
        return $script as any;
    }

    private static injectCSSHelper(id: string, href: string, body: HTMLBodyElement): void {
        const link = document.createElement('link');
        link.rel = 'stylesheet';
        link.type = 'text/css';
        link.id = id;
        link.href = href;
        body.appendChild(link);
    }

    private static isFioriWithoutDark(cssPath: string): boolean {
        return cssPath.includes('fiori') && !cssPath.includes('dark');
    }
}
