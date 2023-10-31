/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const add = require('../add');

/**
 * @ngdoc service
 * @name ConfigurationBuilder.service:webpack.addModule
 * @description
 * Adds a path for webpack to {@link https://webpack.js.org/configuration/resolve/#resolve-modules resolve} modules in.
 *
 * @param {string} module A path.
 * @returns {function(config)} A builder for a webpack configuration object.
 */
module.exports = (module) => add('resolve.modules', module);
