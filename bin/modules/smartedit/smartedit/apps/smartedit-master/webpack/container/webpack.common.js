/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const path = require('path');
const ForkTsCheckerNotifierWebpackPlugin = require('fork-ts-checker-notifier-webpack-plugin');
const ForkTsCheckerWebpackPlugin = require('fork-ts-checker-webpack-plugin');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const webpack = require('webpack');

const extensionsLoader = require('../smartedit-extensions-loader');
const currentYear = new Date().getFullYear();

const configPromise = new Promise((resolve, reject) => {
    extensionsLoader
        .loadExtensions('container')
        .then((extensionsInfo) =>
            resolve({
                entry: {
                    smarteditcontainer: './src/container/master_index.ts'
                }, // This file is auto-generated.
                resolve: {
                    extensions: ['.ts', '.js'],
                    modules: ['node_modules'],
                    //  https://jira.tools.sap/browse/CXEC-15383
                    //  webpack5 remove node polyfill support. Need to add polyfill when call node core modules in browsers.
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
                                    loader: MiniCssExtractPlugin.loader,
                                    options: {
                                        publicPath: './'
                                    }
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
                            type: 'asset/resource'
                        }
                    ]
                },
                plugins: [
                    new ForkTsCheckerWebpackPlugin(),
                    new ForkTsCheckerNotifierWebpackPlugin({
                        title: 'TypeScript',
                        excludeWarnings: false
                    }),
                    new webpack.DefinePlugin({
                        // Creates global variables that will be used later to control SmartEdit bootstrapping
                        'window.__smartedit__.smartEditContainerAngularApps': JSON.stringify(
                            extensionsInfo.containerAngularAppsToLoad
                        ),
                        'window.__smartedit__.smartEditInnerAngularApps': JSON.stringify(
                            extensionsInfo.innerAngularAppsToLoad
                        ),
                        E2E_ENVIRONMENT: false
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
                        filename: '[name].css',
                        chunkFilename: '[id].css',
                        ignoreOrder: false // Enable to remove warnings about conflicting order
                    }),
                    new webpack.BannerPlugin(
                        `Copyright (c) ${currentYear} SAP SE or an SAP affiliate company. All rights reserved.`
                    ),
                    new HtmlWebpackPlugin({
                        title: 'SmartEdit',
                        filename: path.resolve(__dirname, '../../dist/index.jsp'),
                        inject: 'body',
                        scriptLoading: 'defer',
                        minify: false,
                        template: path.resolve(__dirname, './smarteditContainerTemplate.html')
                    })
                ],
                output: {
                    publicPath: 'static-resources/dist/smartedit-container-new/',
                    filename: '[name].js',
                    path: path.resolve(__dirname, '../../dist/smartedit-container'),
                    clean: true
                },
                optimization: {
                    splitChunks: {
                        cacheGroups: {
                            vendors: {
                                test(module) {
                                    return (
                                        module.resource && module.resource.includes('node_modules')
                                    );
                                },
                                name: 'vendors',
                                chunks: 'all'
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
