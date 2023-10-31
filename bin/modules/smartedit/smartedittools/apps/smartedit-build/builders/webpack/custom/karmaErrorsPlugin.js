/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const plugin = require('../plugin');

class KarmaErrorsPlugin {
    apply(compiler) {
        // check if the fork-ts-checker-webpack-plugin is used.
        if (compiler.hooks.forkTsCheckerEmit) {
            compiler.hooks.forkTsCheckerEmit.tap('KarmaErrorsPlugin', (diagnostics) => {
                if (
                    diagnostics.length &&
                    process.argv.indexOf('--watch') === -1 &&
                    process.argv.indexOf('--browser') === -1
                ) {
                    let err;
                    diagnostics.forEach((diagnostic) => {
                        console.error(
                            '\x1b[31m%s\x1b[0m',
                            `ERROR in ${diagnostic.file} (${diagnostic.line}, ${
                                diagnostic.character
                            }) \n
                        Error TS${diagnostic.code}: ${diagnostic.content}`
                        );
                    });
                    process.exit(1);
                }
            });
        } else {
            // hook on default compiler done (no Type Checking fork).
            compiler.hooks.done.tap('KarmaErrorsPlugin', (stats) => {
                if (
                    stats.compilation.errors.length &&
                    process.argv.indexOf('--watch') === -1 &&
                    process.argv.indexOf('--browser') === -1
                ) {
                    stats.compilation.errors.forEach(function(error) {
                        console.error(`${error.message || error}`);
                    });
                    process.exit(1);
                }
            });
        }
    }
}

/**
 * @ngdoc service
 * @name ConfigurationBuilder.service:webpack.karmaErrorsPlugin
 * @description
 * Throw errors for typescript compilation errors.
 *
 * @returns {function(config)} A builder for a webpack configuration object.
 */
module.exports = () => plugin(new KarmaErrorsPlugin());
