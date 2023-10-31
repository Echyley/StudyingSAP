/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const path = require('path');
const ForkTsCheckerNotifierWebpackPlugin = require('fork-ts-checker-notifier-webpack-plugin');
const ForkTsCheckerWebpackPlugin = require('fork-ts-checker-webpack-plugin');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const webpack = require('webpack');

const extensionsLoader = require('../smartedit-extensions-loader');
const currentYear = new Date().getFullYear();

const configPromise = new Promise((resolve, reject) => {
    extensionsLoader
        .loadExtensions('inner')
        .then(() =>
            resolve({
                entry: './src/inner/master_index.ts', // This file is auto-generated.
                resolve: {
                    extensions: ['.ts', '.js'],
                    modules: ['node_modules'],
                    fallback: {
                        buffer: require.resolve('buffer/'),
                        stream: require.resolve('stream-browserify')
                    }
                },
                module: {
                    rules: [
                        {
                            test: /.ts$/,
                            use: {
                                loader: 'ts-loader',
                                options: {
                                    transpileOnly: true
                                }
                            },
                            exclude: /node_modules/
                        },
                        {
                            test: /\.css$/i,
                            use: [
                                {
                                    loader: MiniCssExtractPlugin.loader
                                },
                                {
                                    loader: 'css-loader',
                                    options: {
                                        url: true
                                    }
                                }
                            ]
                        },
                        {
                            test: /\.(png|jpe?g|gif|svg|eot|ttf|woff|woff2)$/i,
                            type: 'asset/inline'
                        }
                    ]
                },
                plugins: [
                    new ForkTsCheckerWebpackPlugin(),
                    new ForkTsCheckerNotifierWebpackPlugin({
                        title: 'TypeScript',
                        excludeWarnings: false
                    }),
                    new webpack.ProvidePlugin({
                        // This assumes that in the container there's no conflict between libraries.
                        // This same approach cannot be used in the inner frame.
                        Buffer: ['buffer', 'Buffer'],
                        $: 'jquery',
                        'window.$': 'jquery',
                        lodash: 'lodash'
                    }),
                    new MiniCssExtractPlugin({
                        // Options similar to the same options in webpackOptions.output
                        // all options are optional
                        filename: 'main.css',
                        chunkFilename: '[id].css',
                        ignoreOrder: false // Enable to remove warnings about conflicting order
                    }),
                    new webpack.BannerPlugin(
                        `Copyright (c) ${currentYear} SAP SE or an SAP affiliate company. All rights reserved.`
                    )
                ],
                output: {
                    filename: 'smartedit.js',
                    path: path.resolve(__dirname, '../../dist/smartedit'),
                    clean: true
                },
                optimization: {
                    splitChunks: {
                        cacheGroups: {
                            defaultVendors: {
                                test(module) {
                                    return (
                                        module.resource && module.resource.includes('node_modules')
                                    );
                                },
                                filename: 'vendors.js',
                                chunks: 'all',
                                minSize: 0
                            }
                        }
                    }
                }
            })
        )
        .catch((err) => {
            reject('Cannot retrieve webpack configuration. ', err);
        });
});

module.exports = configPromise;
