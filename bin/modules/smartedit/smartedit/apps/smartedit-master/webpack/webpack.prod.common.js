/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const CssMinimizerPlugin = require('css-minimizer-webpack-plugin');
const TerserPlugin = require('terser-webpack-plugin');

module.exports = {
    mode: 'production',
    devtool: false,
    optimization: {
        minimize: true,
        minimizer: [
            new TerserPlugin({
                test: /(smartedit|smarteditcontainer)\.js$/i,
                terserOptions: {
                    keep_classnames: true,
                    keep_fnames: true,

                    // Note: Compression is disabled for two reasons:
                    // - First, compressing files increase compilation time and do not reduce the bundle size significantly.
                    //   (more information found in this link: https://terser.org/docs/api-reference.html#terser-fast-minify-mode)
                    // - Second, if compression is enabled with all the default values, the compression will mess up some SmartEdit
                    //   features. In particular, the proxied functions (marked with 'proxyFunction') won't work, as compression
                    //   strips all literal strings.
                    compress: false,
                    format: {
                        comments: /Copyright (c)/i
                    }
                }
            }),
            new TerserPlugin({
                parallel: true,
                exclude: /(smartedit|smarteditcontainer)\.js$/i,
                terserOptions: {
                    format: {
                        comments: false
                    }
                }
            }),
            new CssMinimizerPlugin({
                minimizerOptions: {
                    preset: [
                        'default',
                        {
                            discardComments: { removeAll: true }
                        }
                    ]
                }
            })
        ]
    }
};
