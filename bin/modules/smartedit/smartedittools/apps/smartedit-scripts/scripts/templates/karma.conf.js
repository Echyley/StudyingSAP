/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const base = require('%KARMA_PATH%');
const lodash = require('lodash');

const bundlePaths = require('smartedit-build/bundlePaths');
const { compose, merge } = require('smartedit-build/builders');

module.exports = compose(
    merge({
        files: lodash.concat(
            bundlePaths.test.unit.commonUtilModules,

            'src/**/*.js',
            '.temp/templates.js',
            'tests/specBundle.ts'
        ),

        webpack: require('./webpack.config.spec')
    })
)(base);
