"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.typescript = void 0;
/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const utils_1 = require("../utils");
const os = require("os");
const typescriptPlugin = require('rollup-plugin-typescript2');
/**
 * The TypeScript plugin specifies some default TypeScript configuration that any SmartEdit library must have and then delegates
 * to the rollup-plugin-typescript2 to perform the TypeScript compilation.
 * As part of the configuration process, this plugin also specifies a TypeScript transformer that will add instrumentation code
 * during the compilation process.
 *
 * @param tsconfigPath The path (relative to the library root) pointing to the TypeScript configuration file.
 * @param {boolean} [instrumentation] Boolean flag specifying whether to instrument SmartEdit code or not. Defaults to true.
 */
const typescript = async (tsconfigPath, instrumentation = true) => {
    const tsconfig = await utils_1.resolvePathOrThrow(tsconfigPath, 'Could not find tsconfig file.');
    return typescriptPlugin(Object.assign({ tsconfig, abortOnError: false, clean: os.type() === 'Windows_NT' }, (instrumentation ? { transformers: [utils_1.smartEditTransformerFactory()] } : {})));
};
exports.typescript = typescript;
