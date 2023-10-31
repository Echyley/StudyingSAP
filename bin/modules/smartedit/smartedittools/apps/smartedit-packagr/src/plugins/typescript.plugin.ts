/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { resolvePathOrThrow, smartEditTransformerFactory } from '../utils';
import * as os from 'os';
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
export const typescript = async (tsconfigPath: string, instrumentation = true) => {
    const tsconfig = await resolvePathOrThrow(tsconfigPath, 'Could not find tsconfig file.');
    return typescriptPlugin({
        tsconfig,
        abortOnError: false,
        clean: os.type() === 'Windows_NT', // default is false. Change to true in Windows since cache related rename operation will be stopped by antivirus scanner
        ...(instrumentation ? { transformers: [smartEditTransformerFactory()] } : {})
    });
};
