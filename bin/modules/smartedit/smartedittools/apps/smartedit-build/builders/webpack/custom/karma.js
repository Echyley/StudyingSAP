/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const { resolve } = require('path');
const group = require('../../compose');

const dev = require('./dev');
const coverageLoader = require('./coveragePlugin');
const tsLoader = require('./ts');

const devtool = require('../devtool');
const alias = require('../alias');
const addModule = require('../addModule');
const rule = require('../rule');
const karmaErrorsPlugin = require('./karmaErrorsPlugin');

const bundlePaths = require('../../../bundlePaths');

/**
 * @ngdoc service
 * @name ConfigurationBuilder.service:webpack.karma
 * @description
 * A preset group of builders for configuring a karma webpack config.
 *
 * @returns {function(config)} A builder for a webpack configuration object.
 */
module.exports = ({ instrument = true } = {}) =>
    group(
        dev(),

        devtool('inline-source-map'),

        alias('testhelpers', bundlePaths.test.unit.root),
        addModule(resolve(bundlePaths.bundleRoot, 'node_modules')),
        addModule(resolve(bundlePaths.bundleRoot, '../../common/temp/node_modules')),

        coverageLoader(),
        karmaErrorsPlugin(),

        tsLoader(resolve('tsconfig.spec.json'), true, true, true, instrument),
        rule({
            test: /\.(less|sass|scss|css)$/,
            use: [
                {
                    loader: 'ignore-loader'
                }
            ]
        }),

        addModule(resolve('src')),
        addModule(resolve('node_modules')),
        addModule(resolve('node_modules/smarteditcommons/node_modules'))
    );
