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
import 'core-js/es/reflect';
import 'core-js/proposals/reflect-metadata';
import '@juggle/resize-observer';
import 'whatwg-fetch';

require('zone.js/dist/zone.js');

import '@webcomponents/webcomponentsjs/custom-elements-es5-adapter.js';
import '@webcomponents/webcomponentsjs/bundles/webcomponents-sd-ce.js';

(window as any).jQuery = require('jquery');
import './thirdparties';

// since styles.css of smarteditloader is empty to avoid unexpected css overwritten, import smarteditcommons here
import '../../node_modules/smarteditcommons/dist/styles.css';
// SmartEdit extensions' files (index and styles) will be added dynamically here.
