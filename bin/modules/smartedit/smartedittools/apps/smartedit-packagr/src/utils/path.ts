/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { isAbsolute, resolve } from 'path';

let basePath = process.cwd();

export function setBasePath(path: string) {
    basePath = path;
}

export function resolveBasePath() {
    return resolvePath('');
}

export function resolvePath(pathToResolve: string) {
    if (isAbsolute(pathToResolve)) {
        return pathToResolve;
    }

    return resolve(basePath, pathToResolve);
}
