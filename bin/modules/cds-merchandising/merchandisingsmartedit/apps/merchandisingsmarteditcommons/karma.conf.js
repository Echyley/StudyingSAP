const base = require('smartedit-build/config/karma/shared/karma.base.conf');
const lodash = require('lodash');

const bundlePaths = require('smartedit-build/bundlePaths');
const { compose, merge } = require('smartedit-build/builders');

module.exports = compose(
    merge({
        files: lodash.concat(
            bundlePaths.test.unit.commonUtilModules,

            '.temp/templates.js',
            'tests/features/*.ts'
        ),

        webpack: require('./webpack.config.spec'),

        failOnEmptyTestSuite: false
    })
)(base);
