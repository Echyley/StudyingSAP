"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.generateBundle = exports.getConfiguredPlugins = void 0;
/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const rollup = require("rollup");
const utils_1 = require("./utils");
const plugins_1 = require("./plugins");
const progress = require('rollup-plugin-progress');
const getConfiguredPlugins = async (appConfig) => {
    var _a, _b;
    const stylesProcessor = await utils_1.createStyleProcessor((_a = appConfig.style) === null || _a === void 0 ? void 0 : _a.global);
    return [
        // Compiles css / sass / global styles
        plugins_1.styles(stylesProcessor, appConfig.style),
        // Resolves inner HTML and styling templates
        plugins_1.angular(stylesProcessor),
        // Compiles TS files into JS  files
        await plugins_1.typescript(appConfig.typescript.config, !((_b = appConfig.instrumentation) === null || _b === void 0 ? void 0 : _b.skipInstrumentation)),
        // Leaves external dependencies out of the main bundle.
        plugins_1.external(appConfig.dependencies),
        // Shows progress of the create bundle operation
        progress()
    ];
};
exports.getConfiguredPlugins = getConfiguredPlugins;
const generateBundle = async (appConfig, plugins, writeBundle = true) => {
    const input = await utils_1.resolvePathOrThrow(appConfig.typescript.entry, 'Could not find entry file.');
    const bundle = await rollup.rollup({
        input,
        plugins
    });
    const outputOptions = {
        file: utils_1.resolvePath(appConfig.typescript.dist),
        format: 'cjs',
        sourcemap: false // TODO: switch to true when fixing TypeScript source maps.
    };
    if (writeBundle) {
        return bundle.write(outputOptions);
    }
    else {
        return bundle.generate(outputOptions);
    }
};
exports.generateBundle = generateBundle;
