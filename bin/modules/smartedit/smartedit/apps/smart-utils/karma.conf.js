/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const base = require('smartedit-build/config/karma/shared/karma.base.conf');
const lodash = require('lodash');

const bundlePaths = require('smartedit-build/bundlePaths');

const {
    compose,
    merge,
    karma: { headless }
} = require('smartedit-build/builders');

module.exports = compose(
    headless(),
    merge({
        // list of files / patterns to load in the browser
        // each file acts as entry point for the webpack configuration
        files: lodash.concat(bundlePaths.test.unit.commonUtilModules, 'src/specBundle.ts'),

        exclude: ['**/index.ts', '**/*.d.ts'],

        webpack: require('./webpack.config.spec')
    })
)(base);
