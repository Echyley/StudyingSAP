/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const set = require('../set');

/**
 * @ngdoc service
 * @name ConfigurationBuilder.service:webpack.mode
 * @description
 * Sets the mode of a webpack config.
 *
 * @param {string} mode The mode type either 'development' or 'production'.
 * @returns {function(config)} A builder for a webpack configuration object.
 */
module.exports = (mode) => set('mode', mode);
