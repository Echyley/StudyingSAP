/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const { resolve } = require('path');

const base = require('../../smartedit-build/config/webpack/shared/webpack.bare.config');

const {
    compose,
    webpack: {
        addModule,
        alias,
        entry,
        external,
        output,
        plugin,
        tsLoader,
        dev,
        sassLoader
    }
} = require('../../smartedit-build/builders');

const bundlePaths = require('../../smartedit-build/bundlePaths');
const customPaths = require('../paths');
const webpack = require('webpack');

module.exports = compose(
    entry({
        index: [resolve(customPaths.entrypoints.smarteditcommons)]
    }),
    output({
        path: resolve(bundlePaths.external.generated.commons.lib),
        filename: '[name].js',
        sourceMapFilename: '[file].map',
        library: '[name]',
        libraryTarget: 'umd',
        umdNamedDefine: true
    }),
    dev(),
    external('crypto-js', {
        commonjs: 'crypto-js',
        amd: 'crypto-js',
        root: 'CryptoJS',
        commonjs2: 'crypto-js'
    }),

    alias('smarteditcommons', resolve(customPaths.smarteditcommons.jsTarget)),
    addModule(resolve(customPaths.smarteditcommons.jsTarget)),

    sassLoader(false, true, true),
    tsLoader(resolve(customPaths.tsconfig.libSmarteditCommons), false, true),

    plugin(
        new webpack.DllReferencePlugin({
            context: resolve('/'),
            manifest: resolve(bundlePaths.external.generated.vendor.manifest)
        })
    )
)(base);
