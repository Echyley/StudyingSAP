/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import 'testhelpers';
import 'smarteditcontainer';

function importAll(requireContext: __WebpackModuleApi.RequireContext): void {
    requireContext.keys().forEach(function (key: string) {
        requireContext(key);
    });
}
importAll(require.context('./features', true, /(Test|spec)\.(js|ts)$/));
importAll(require.context('../src', true, /Module\.ts$/));
