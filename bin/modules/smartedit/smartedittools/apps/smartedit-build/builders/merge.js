/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const { merge } = require('lodash');

/**
 * @ngdoc service
 * @name ConfigurationBuilder.service:merge
 * @description
 * Uses the lodash @{link https://lodash.com/docs/4.17.11#merge merge} operation to merge a configuration with the source.
 *
 * @param {object} config A configuration to merge with.
 * @returns {function(config)} A builder for a configuration object.
 */
module.exports = (config) => (source) => merge({}, source, config);
