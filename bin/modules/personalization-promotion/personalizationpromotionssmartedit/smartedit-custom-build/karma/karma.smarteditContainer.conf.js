/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const base = require('../../smartedit-build/config/karma/karma.ext.smarteditContainer.conf');
const customPaths = require('../../jsTests/paths');

const {
    compose,
    merge,
    add
} = require('../../smartedit-build/builders');

const karma = compose(
    merge({
        singleRun: true,
        junitReporter: {
            outputDir: 'jsTarget/test/personalizationpromotionssmarteditContainer/junit/', // results will be saved as $outputDir/$browserName.xml
            outputFile: 'testReport.xml' // if included, results will be saved as $outputDir/$browserName/$outputFile
        },

        // list of files / patterns to load in the browser
        files: customPaths.getPersonalizationpromotionssmarteditContainerKarmaConfFiles(),

        proxies: {
            '/personalizationpromotionssmartedit/images/': '/base/images/'
        },

        webpack: require('../webpack/webpack.karma.smarteditContainer.config'),
    }),
    add('exclude', [
        '**/PersonalizationpromotionssmarteditContainerApp.ts',
        '*.d.ts'
    ], true)
)(base);

module.exports = function(config) {
    config.set(karma);
};
