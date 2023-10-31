/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/* jshint esversion: 6 */
const group = require('../../compose');
const rule = require('../rule');

const { resolve } = require('path');

/**
 * @ngdoc service
 * @name ConfigurationBuilder.service:webpack.sassLoader
 * @description
 * Adds scss, sass, and css support to a webpack configuration. The loader being used is 'fast-sass-loader' which has a
 * slight limitation to duplicate variables. Please consult their doc for more information: https://github.com/yibn2008/fast-sass-loader#readme.
 *
 * @param {boolean} sourceMap Generates source-maps.
 * Please do not use source-maps with Headless Chrome E2E environments with versions 71 or below are affected as the browser
 * is having issues with blobs. The styles-loader use blobs to handle its source maps. Please see the links below for
 * more information.
 * https://github.com/webpack-contrib/style-loader/issues/355
 * https://bugs.chromium.org/p/chromium/issues/detail?id=902918&desc=2
 *
 * @param {boolean} url Enable/Disable url() handling (css-loader).
 * @param {boolean} cache Enable/Disable caching of loaders with the use of.
 * @returns {function(config)} A builder for a webpack configuration object.
 */
module.exports = (sourceMap = false, url = true, cache = false) =>
    group(
        // For cross-origin support (inner layer), we must use url-loader which converts assets required from css to base64 string.
        rule({
            test: /\.(jpg|png|woff|woff2|eot|ttf|svg)$/,
            use: [
                {
                    loader: 'url-loader',
                    options: {
                        limit: 500000 // we use a high limit to prevent fallback to file-loader (enforcing base64 bunling of assets)
                    }
                }
            ]
        }),
        rule({
            test: /\.(sc|c|sa)ss$/,
            /**
             * To prevent tree shaking when in production.
             */
            sideEffects: true,
            use: [
                {
                    loader: 'thread-loader',
                    options: {
                        workers: 2,
                        workerParallelJobs: 50,
                        poolTimeout: 2000
                    }
                },
                'style-loader',
                {
                    loader: 'css-loader',
                    options: {
                        sourceMap,
                        url
                    }
                },
                {
                    loader: 'postcss-loader',
                    options: {
                        config: {
                            /**
                             * Cannot inline postcss configuration because of https://github.com/postcss/postcss-loader/issues/179,
                             * thus it resides in smartedit-build/config/postcss.config.js.
                             */
                            path: resolve(__dirname, '../../../config')
                        }
                    }
                },
                {
                    loader: 'sass-loader',
                    options: {
                        additionalData: '@import "se-variables";',
                        includePaths: [resolve(__dirname, '../../../styles')],
                        sassOptions: { fiber: false }
                    }
                }
            ]
        })
    );
