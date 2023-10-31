/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const execute = require('../../execute');
const rule = require('../rule');

const isAnalyzingCoverage = process.argv.indexOf('coverage') !== -1;

/**
 * @ngdoc service
 * @name ConfigurationBuilder.service:webpack.coveragePlugin
 * @description
 * Adds the 'istanbul-instrumenter-loader' rule to emit coverage information for a webpack configuration.
 *
 * @returns {function(config)} A builder for a webpack configuration object.
 */
module.exports = (activate = isAnalyzingCoverage) => execute(activate);
