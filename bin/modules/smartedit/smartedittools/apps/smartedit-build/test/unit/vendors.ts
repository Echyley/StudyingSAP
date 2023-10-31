/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import 'core-js/es/symbol';
import 'core-js/es/object';
import 'core-js/es/function';
import 'core-js/es/parse-int';
import 'core-js/es/parse-float';
import 'core-js/es/number';
import 'core-js/es/math';
import 'core-js/es/string';
import 'core-js/es/date';
import 'core-js/es/array';
import 'core-js/es/regexp';
import 'core-js/es/map';
import 'core-js/es/weak-set';
import 'core-js/es/weak-map';
import 'core-js/es/set';
import 'core-js/es/typed-array';
import 'core-js/es/promise';
import 'core-js/features/reflect';
import 'intersection-observer';

import 'zone.js';
import 'zone.js/testing';

import '@webcomponents/webcomponentsjs/custom-elements-es5-adapter.js';
import '@webcomponents/webcomponentsjs/bundles/webcomponents-sd-ce.js';

import * as lodash from 'lodash';

declare global {
    interface Window {
        smarteditLodash: lodash.LoDashStatic;
    }
}

const $: any = (window as any).$;

if (!(window as any).smarteditJQuery && $ && $.noConflict) {
    (window as any).smarteditJQuery = $.noConflict();
}

window.smarteditLodash = lodash.noConflict();
