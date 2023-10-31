/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { SmarteditNamespace } from 'smartedit';
import { SmarteditBundle, SmarteditBundleJsFile } from 'smarteditcontainer';

import { Heartbeat } from './heartbeat';
import WebUtils from './web-utils';
import WhitelistingUtils from './whitelisting-utils';

interface SmartEditMessageEvent extends MessageEvent {
    data: {
        pk: string;
        gatewayId: string;
        eventId: string;
        data: {
            resources: SmarteditBundle;
        };
    };
}

const smartEditBootstrapGatewayId = 'smartEditBootstrap';

// Get the script element of webApplicationInjector.js
const webappScript = WebUtils.getWebappScriptElementFromDocument(document);
if (!webappScript) {
    throw new Error('Unable to location webappInjector script');
}

declare global {
    interface Window {
        smartedit: SmarteditNamespace;
    }
}

// Extract the smartedit location whitelistings
const whiteListedOrigins = WhitelistingUtils.getWhitelistFromScriptElement(webappScript, window);

// Convert whitelistings to regex that can be testing against event origins
const whiteListedOriginRegexes = WhitelistingUtils.convertWhitelistingToRegexp(whiteListedOrigins);

// Notify smarteditContainer that a smarteditable storefront is loading
parent.postMessage(
    {
        pk: Math.random(),
        gatewayId: smartEditBootstrapGatewayId,
        eventId: 'loading',
        data: {
            location: document.location.href
        }
    },
    '*'
);

window.addEventListener('load', loadEventListener);
window.addEventListener('message', messageEventListener, false);

// On exit, notify smartedit container
window.onbeforeunload = onbeforeunload;

Heartbeat.startSendingHeartBeatToIframe(webappScript);

// #################################################################################################
// Private functions
// #################################################################################################

function onbeforeunload(): void {
    parent.postMessage(
        {
            pk: Math.random(),
            gatewayId: smartEditBootstrapGatewayId,
            eventId: 'unloading',
            data: {
                location: document.location.href
            }
        },
        '*'
    );
}

function loadEventListener(): void {
    parent.postMessage(
        {
            pk: Math.random(),
            gatewayId: smartEditBootstrapGatewayId,
            eventId: 'bootstrapSmartEdit',
            data: {
                location: document.location.href
            }
        },
        '*'
    );
}

// Listen to message from child window
function messageEventListener(messageEvent: SmartEditMessageEvent): void {
    const handleEvent =
        messageEvent.data.gatewayId === smartEditBootstrapGatewayId &&
        messageEvent.data.eventId === 'bundle';
    if (!handleEvent) {
        return;
    }
    // Do not remove the line below as it will expose XSS vulnerabilities.
    if (!WhitelistingUtils.isAllowed(messageEvent.origin, window, whiteListedOriginRegexes)) {
        throw new Error(messageEvent.origin + ' is not allowed to override this storefront.');
    }
    injectResources(messageEvent.data.pk, messageEvent.data.data.resources);
}

// define inner functions
function handleReourceProperty(resources: SmarteditBundle): void {
    if (!resources.properties) {
        return;
    }

    for (const prop in resources.properties) {
        if (resources.properties.hasOwnProperty(prop)) {
            window.smartedit[prop as keyof SmarteditNamespace] = resources.properties[prop] as any;
        }
    }
}

function avoidJSLibConflict(resources: SmarteditBundle): void {
    // avoid conflict of js libraries - see smartedit contract for more details
    if (!(resources.js && resources.js.length > 0)) {
        return;
    }

    let sources: string[];
    if (typeof resources.js[0] === 'string') {
        // @deprecated 1905
        // adding legacy support for old JS type before we changed js array elements
        // to be objects with src and namespaceToCheck properties. This will make
        // for easier backporting and cross version maintenance
        sources = (resources.js as unknown) as string[];
    } else {
        sources = resources.js
            .filter(
                (el: SmarteditBundleJsFile) =>
                    !el.namespaceToCheck || !(window as any)[el.namespaceToCheck]
            )
            .map((el: SmarteditBundleJsFile) => el.src);
    }
    WebUtils.injectJS(sources);
}

function injectResources(pk: string, resources: SmarteditBundle): void {
    // code for this function 'injectResources'
    window.smartedit = window.smartedit || ({} as SmarteditNamespace);

    parent.postMessage(
        {
            gatewayId: smartEditBootstrapGatewayId,
            eventId: 'promiseAcknowledgement',
            data: {
                pk
            }
        },
        '*'
    );

    if (resources) {
        const body: HTMLBodyElement = document.getElementsByTagName('body')[0];
        handleReourceProperty(resources);
        avoidJSLibConflict(resources);
        if (resources.css && resources.css.length > 0) {
            WebUtils.removeThemeCSS();
            WebUtils.injectCSS(body, resources.css);
        }
    }

    parent.postMessage(
        {
            gatewayId: smartEditBootstrapGatewayId,
            eventId: 'promiseReturn',
            data: {
                pk,
                type: 'success'
            }
        },
        '*'
    );
}
