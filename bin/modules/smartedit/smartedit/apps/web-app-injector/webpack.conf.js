/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/* jshint esversion: 6 */
const path = require('path');

module.exports = {
    // ########################################
    // ##### Configured through CLI with ######
    // "mode": "production",
    // "devtool": "source-map",
    // ########################################
    entry: './src/webapp-injector.ts',
    output: {
        filename: 'webApplicationInjector.js',
        path: path.resolve(__dirname, './dist')
    },
    resolve: {
        extensions: ['.ts', '.js'],
        modules: ['node_modules']
    },
    stats: {
        assets: true,
        colors: true,
        modules: true,
        reasons: true,
        errorDetails: true
    },
    module: {
        rules: [
            {
                test: /\.ts$/,
                loader: 'ts-loader',
                exclude: /node_modules/
            }
        ]
    }
};
