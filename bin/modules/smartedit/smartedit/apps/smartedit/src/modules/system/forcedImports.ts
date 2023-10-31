/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
function importAll(requireContext: any): void {
    requireContext.keys().forEach(function (key: string) {
        requireContext(key);
    });
}

/* @internal */
export function doImport(): void {
    importAll(require.context('./', true, /\.js$/));
}
