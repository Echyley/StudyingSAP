/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const base = require('smartedit-build/config/karma/karma.ext.smartedit.conf');
const lodash = require('lodash');

const bundlePaths = require('smartedit-build/bundlePaths');

const { compose, merge } = require('smartedit-build/builders');

module.exports = compose(
    merge({
        // list of files / patterns to load in the browser
        // each file acts as entry point for the webpack configuration
        files: lodash.concat(
            bundlePaths.test.unit.commonUtilModules,
            'tests/specBundle.ts'
        ),

        port: 9880,

        webpack: require('./webpack.config.spec')
    })
)(base);
