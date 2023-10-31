"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.resolvePathOrThrow = void 0;
/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const path_1 = require("./path");
const fs_1 = require("fs");
const util_1 = require("util");
const accessPromise = util_1.promisify(fs_1.access);
/**
 * Checks if path exists or throws error message.
 *
 * @param file
 * @param errorMessage
 */
const resolvePathOrThrow = async (file, errorMessage) => {
    const absolutePath = path_1.resolvePath(file);
    try {
        await accessPromise(absolutePath);
    }
    catch (err) {
        throw new Error(`The file does not exists at: ${absolutePath}. ${errorMessage || ''}`);
    }
    return absolutePath;
};
exports.resolvePathOrThrow = resolvePathOrThrow;
