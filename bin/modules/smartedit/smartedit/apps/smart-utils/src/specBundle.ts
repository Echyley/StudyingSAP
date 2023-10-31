/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/* eslint-disable */

/**
 * Entry file for unit tests.
 */
import 'zone.js';
import 'zone.js/testing';
import { getTestBed } from '@angular/core/testing';
import {
    BrowserDynamicTestingModule,
    platformBrowserDynamicTesting
} from '@angular/platform-browser-dynamic/testing';

import 'core-js/features/reflect';

// First, initialize the Angular testing environment.
getTestBed().initTestEnvironment(BrowserDynamicTestingModule, platformBrowserDynamicTesting());

function importAll(requireContext: any) {
    requireContext.keys().forEach(function (key: string) {
        requireContext(key);
    });
}

importAll(require.context('./', true, /spec.ts$/));
