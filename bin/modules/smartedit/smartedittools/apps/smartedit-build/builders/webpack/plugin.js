/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const add = require('../add');

/**
 * @ngdoc service
 * @name ConfigurationBuilder.service:webpack.plugin
 * @description
 * Adds a plugin to a webpack plugins' list.
 *
 * @param {object} plugin A webpack plugin.
 * @returns {function(config)} A builder for a webpack configuration object.
 */
module.exports = (plugin) => add('plugins', plugin);
