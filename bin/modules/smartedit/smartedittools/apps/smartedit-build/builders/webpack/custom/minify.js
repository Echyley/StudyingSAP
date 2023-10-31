/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const set = require('../../set');
const add = require('../../add');
const group = require('../../compose');

const currentYear = new Date().getFullYear();
/**
 * @ngdoc service
 * @name ConfigurationBuilder.service:webpack.minify
 * @description
 * Adds minification support to a webpack configuration.
 *
 * @param {boolean} minify A boolean for activating minification.
 * @returns {function(config)} A builder for a webpack configuration object.
 */
module.exports = (minify = true) =>
    group(
        set('optimization.minimize', minify),
        add('optimization.minimizer', {
            apply: function (compiler) {
                const TerserPlugin = require('terser-webpack-plugin');
                return new TerserPlugin({
                    terserOptions: {
                        keep_classnames: true,
                        keep_fnames: true,

                        // Note: Compression is disabled for two reasons:
                        // - First, compressing files increase compilation time and do not reduce the bundle size significantly.
                        //   (more information found in this link: https://terser.org/docs/api-reference.html#terser-fast-minify-mode)
                        // - Second, if compression is enabled with all the default values, the compression will mess up some SmartEdit
                        //   features. In particular, the proxied functions (marked with 'proxyFunction') won't work, as compression
                        //   strips all literal strings.
                        compress: false
                    }
                }).apply(compiler);
            }
        })
    );
