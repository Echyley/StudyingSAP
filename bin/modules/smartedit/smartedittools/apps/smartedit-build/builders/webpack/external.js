/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const set = require('../set');
const encodeDot = require('../utils').encodeDot;

/**
 * @ngdoc service
 * @name ConfigurationBuilder.service:webpack.external
 * @description
 * Adds an external module to a webpack config.
 *
 * @param {string} external The external module.
 * @param {*} value The external configuration module.
 * @returns {function(config)} A builder for a webpack configuration object.
 */
module.exports = (external, value) => set(`externals.${encodeDot(external)}`, value);
