"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.resolvePath = exports.resolveBasePath = exports.setBasePath = void 0;
/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const path_1 = require("path");
let basePath = process.cwd();
function setBasePath(path) {
    basePath = path;
}
exports.setBasePath = setBasePath;
function resolveBasePath() {
    return resolvePath('');
}
exports.resolveBasePath = resolveBasePath;
function resolvePath(pathToResolve) {
    if (path_1.isAbsolute(pathToResolve)) {
        return pathToResolve;
    }
    return path_1.resolve(basePath, pathToResolve);
}
exports.resolvePath = resolvePath;
