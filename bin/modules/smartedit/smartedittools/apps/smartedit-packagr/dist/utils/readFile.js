"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.readFile = void 0;
/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const path_1 = require("./path");
const fs = require('fs');
const util = require('util');
const baseReadFile = util.promisify(fs.readFile);
function readFile(path) {
    const filePath = path_1.resolvePath(path);
    return baseReadFile(filePath, 'utf8');
}
exports.readFile = readFile;
