const base = require('smartedit-build/config/karma/karma.ext.smartedit.conf');
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
