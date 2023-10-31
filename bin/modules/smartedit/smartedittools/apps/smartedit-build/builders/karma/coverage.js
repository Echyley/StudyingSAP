/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const set = require('../set');

/**
 * @ngdoc service
 * @name ConfigurationBuilder.service:karma.coverage
 * @description
 * Adds coverage support to a karma configuration.
 *
 * @param {string} dir The base output directory.
 * @param {string} subdir The sub directory to output the coverage information.
 * @returns {function(config)} A builder for a karma configuration object.
 */
module.exports = (dir, subdir) =>
    set('coverageIstanbulReporter', {
        reports: ['html', 'lcovonly', 'text-summary'],
        dir,
        fixWebpackSourcePaths: true,
        skipFilesWithNoCoverage: true,
        'report-config': {
            html: {
                subdir
            }
        }
    });
