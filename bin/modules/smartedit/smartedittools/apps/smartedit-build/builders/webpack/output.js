/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const set = require('../set');

/**
 * @ngdoc service
 * @name ConfigurationBuilder.service:webpack.output
 * @description
 * Sets the output configuration of a webpack config.
 *
 * @param {object} output The output configuration object.
 * @returns {function(config)} A builder for a webpack configuration object.
 */
module.exports = (output) => set('output', output);
