/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const ForkTsCheckerWebpackPlugin = require('fork-ts-checker-webpack-plugin');
const group = require('../../compose');
const rule = require('../rule');
const plugin = require('../plugin');

/**
 * @ngdoc service
 * @name ConfigurationBuilder.service:webpack.tsLoader
 * @description
 * A preset group of builders for configuring typescript support for a webpack config.
 *
 * @param {string} tsconfig The typescript configuration.
 * @param {boolean} transpileOnly To check for typescript syntax.
 * There is a limitation when ts-loader has "transpileOnly: true" in combination with fork-ts-checker-plugin:
 * the d.ts files are not produced, see https://github.com/Realytics/fork-ts-checker-webpack-plugin/issues/49
 * To have d.ts files emitted, transpileOnly must be set to 'false'.
 * @returns {function(config)} A builder for a webpack configuration object.
 */
module.exports = (
    tsconfig,
    transpileOnly = true,
    cache = false,
    enableHappyPack = true,
    instrument = false
) =>
    group(
        rule({
            test: /\.ts$/,
            sideEffects: true,
            use: [
                enableHappyPack && {
                    loader: 'thread-loader',
                    /**
                     * thread-loader: https://webpack.js.org/loaders/thread-loader/#root
                     * Put this loader in front of other loaders. The following loaders run in a worker pool.
                     * It's an alternative to happyPack to speed up the process of ts-loader.
                     * Remove this loader will nearly double the unit testing time of smartedit.
                     */
                    options: {
                        workers: 2,
                        workerParallelJobs: 50,
                        poolTimeout: 2000
                    }
                },
                {
                    loader: 'ts-loader',
                    /**
                     * ts-loader: https://github.com/TypeStrong/ts-loader
                     * Using ts-loader in combination with fork-ts-checker-webpack-plugin.
                     * To have a fast build, ts-loader is only transpiling TypeScript by using `transpileOnly: true` option.
                     * Type checking is performed by fork-ts-checker-webpack-plugin on a separate process.
                     *
                     * *** Note about emit of declaration files (d.ts): ***
                     * To have d.ts files emitted, "transpileOnly" values must be set to 'false' due to an
                     * issue in fork-ts-checker-plugin: https://github.com/Realytics/fork-ts-checker-webpack-plugin/issues/49
                     *
                     * If you need declatation file emit, you can use `webpackUtil.disableTsLoaderTranspileOnly(conf)`.
                     *
                     * Another way to produce d.ts files is to just run `tsc -p path/to/tsconfig.json` on command line.
                     */
                    options: {
                        transpileOnly: transpileOnly,
                        happyPackMode: transpileOnly,
                        configFile: tsconfig
                    }
                },
                ...(instrument
                    ? [
                          {
                              loader: 'instrument-se-loader'
                          }
                      ]
                    : [])
            ]
        }),
        rule({
            test: /\.(html)$/,
            use: [
                {
                    loader: 'raw-loader'
                }
            ]
        }),
        rule({
            // https://github.com/angular/angular/issues/21560
            // Mark files inside `@angular/core` as using SystemJS style dynamic imports.
            // Removing this will cause deprecation warnings to appear.
            test: /\/node_modules\/(@angular|core-js|rxjs|zone.js)\//,
            // test: /[\/\\]@angular[\/\\]core[\/\\].+\.js$/,
            parser: {
                system: true
            } // enable SystemJS
        }),
        transpileOnly &&
            plugin(
                new ForkTsCheckerWebpackPlugin({
                    async: false, // async: false block webpack's emit to wait for type checker/linter and to add errors to the webpack's compilation.
                    typescript: {
                        configFile: tsconfig,
                        diagnosticOptions: {
                            syntactic: false
                        }
                    }
                })
            )
    );
