/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const add = require('../add');

/**
 * @ngdoc service
 * @name ConfigurationBuilder.service:webpack.rule
 * @description
 * Adds a rule to a webpack config.
 *
 * @param {object} rule A test rule.
 * @returns {function(config)} A builder for a webpack configuration object.
 */
module.exports = (rule) => add('module.rules', rule);
