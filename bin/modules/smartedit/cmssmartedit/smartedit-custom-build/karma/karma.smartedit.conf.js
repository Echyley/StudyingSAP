/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const base = require('../../smartedit-build/config/karma/karma.ext.smartedit.conf');
const customPaths = require('../../jsTests/paths');

const { compose, merge, add } = require('../../smartedit-build/builders');

const karma = compose(
    merge({
        singleRun: true,
        junitReporter: {
            outputDir: 'jsTarget/test/cmssmartedit/junit/', // results will be saved as $outputDir/$browserName.xml
            outputFile: 'testReport.xml' // if included, results will be saved as $outputDir/$browserName/$outputFile
        },

        files: customPaths.cmssmarteditKarmaConfFiles,

        proxies: {
            '/cmssmartedit/images/': '/base/images/'
        }
    }),
    add('exclude', ['**/requireLegacyJsFiles.js'], true)
)(base);

module.exports = function (config) {
    config.set(karma);
};
