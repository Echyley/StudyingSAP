/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const set = require('../set');
const encodeDot = require('../utils').encodeDot;

/**
 * @ngdoc service
 * @name ConfigurationBuilder.service:webpack.alias
 * @description
 * Adds an alias to webpack's {@link https://webpack.js.org/configuration/resolve/#resolve-modules resolve.alias}.
 *
 * @param {string} alias The alias.
 * @param {string} path The module resolving the alias.
 * @returns {function(config)} A builder for a webpack configuration object.
 */
module.exports = (alias, path) => set(`resolve.alias.${encodeDot(alias)}`, path);
