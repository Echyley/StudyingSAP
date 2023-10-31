/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const { resolve } = require('path');

module.exports = {
    devtool: 'inline-source-map',
    output: {
        path: resolve(process.cwd(), '.temp'),
        sourceMapFilename: '[file].map'
    },
    externals: {
        angular: 'angular',
        Reflect: 'Reflect',
        moment: 'moment',
        'crypto-js': 'CryptoJS'
    },
    resolve: {
        extensions: ['.ts', '.js']
    },
    resolveLoader: {
        modules: [
            resolve(__dirname, '../../../builders/webpack/loaders'),
            resolve(__dirname, '../../../node_modules')
        ]
    },
    performance: {
        hints: false
    },
    stats: {
        assets: false,
        colors: true,
        modules: false,
        reasons: true,
        errorDetails: true
    },
    plugins: [
        {
            apply: (compiler) => {
                return new (require('webpack').DefinePlugin)({
                    // Creates global variables that will be used later to control SmartEdit bootstrapping
                    'window.__smartedit__.smartEditContainerAngularApps': '[]',
                    'window.__smartedit__.smartEditInnerAngularApps': '[]',
                    E2E_ENVIRONMENT: false
                }).apply(compiler);
            }
        },
        {
            apply: (compiler) => {
                return new (require('webpack').ProvidePlugin)({
                    // This assumes that in the container there's no conflict between libraries.
                    Buffer: ['buffer', 'Buffer']
                }).apply(compiler);
            }
        }
    ],
    ignoreWarnings: [
        {
            message: /export '.*'( \(imported as '.*'\))? was not found in/
        },
        {
            message: /System.import/
        },
        {
            message: /the request of a dependency is an expression/
        },
        {
            message: /export '.*'( \(reexported as '.*'\))? was not found in/
        }
    ],
    bail: true,
    mode: 'development'
};
