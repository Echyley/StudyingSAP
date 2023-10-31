/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import * as rollup from 'rollup';
import { SeAppConfiguration } from './configuration';
import { resolvePath, resolvePathOrThrow, createStyleProcessor } from './utils';
import { external, styles, angular, typescript } from './plugins';

const progress = require('rollup-plugin-progress');

export const getConfiguredPlugins = async (
    appConfig: SeAppConfiguration
): Promise<rollup.Plugin[]> => {
    const stylesProcessor = await createStyleProcessor(appConfig.style?.global);

    return [
        // Compiles css / sass / global styles
        styles(stylesProcessor, appConfig.style),

        // Resolves inner HTML and styling templates
        angular(stylesProcessor),

        // Compiles TS files into JS  files
        await typescript(
            appConfig.typescript.config,
            !appConfig.instrumentation?.skipInstrumentation
        ),

        // Leaves external dependencies out of the main bundle.
        external(appConfig.dependencies),

        // Shows progress of the create bundle operation
        progress()
    ];
};

export const generateBundle = async (
    appConfig: SeAppConfiguration,
    plugins: rollup.Plugin[],
    writeBundle: boolean = true
) => {
    const input = await resolvePathOrThrow(
        appConfig.typescript.entry,
        'Could not find entry file.'
    );
    const bundle = await rollup.rollup({
        input,
        plugins
    });

    const outputOptions: rollup.OutputOptions = {
        file: resolvePath(appConfig.typescript.dist),
        format: 'cjs',
        sourcemap: false // TODO: switch to true when fixing TypeScript source maps.
    };

    if (writeBundle) {
        return bundle.write(outputOptions);
    } else {
        return bundle.generate(outputOptions);
    }
};
