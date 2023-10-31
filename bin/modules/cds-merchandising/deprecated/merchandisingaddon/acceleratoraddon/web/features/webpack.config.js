/*
	Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
*/
const path = require('path');
const webpack = require('webpack');

module.exports = {
    mode: 'production',
    entry: [
        './../jsTarget/main.js'
    ],
    output: {
        path: path.resolve(__dirname, './../webroot/_ui/responsive/common/js'),
        filename: 'merchandisingaddon.js'
    },
    externals: {
        jquery: 'jQuery',
        'crypto-js': 'CryptoJS',
    },
    resolve: {
        extensions: ['.ts', '.js'],
        modules: [
            path.resolve(__dirname, './node_modules'),
        ],
    },
    resolveLoader: {
        modules: [
            path.resolve(__dirname, './node_modules'),
        ],
    },
    performance: {
        hints: false,
    },
    stats: {
        assets: false,
        colors: true,
        modules: false,
        reasons: true,
        errorDetails: true,
    },
    // Add the loader for .ts files.
	module: {
		rules: [
			{
				test: /\.ts?$/,
				loader: 'ts-loader',
				options: {
					configFile:path.resolve(__dirname, './tsconfig.json')
				}
			}
		]
	},
    plugins: [
        new webpack.ProvidePlugin({
            $: 'jquery',
            jQuery: 'jquery',
            'window.jQuery': 'jquery'
        })
    ]
};