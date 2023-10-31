/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const set = require('../set');

/**
 * @ngdoc service
 * @name ConfigurationBuilder.service:webpack.devtool
 * @description
 * Sets the {@link https://webpack.js.org/configuration/devtool/ devtool} for source maps.
 *
 * @param {string} devtool The sourcemap mode.
 * @returns {function(config)} A builder for a webpack configuration object.
 */
module.exports = (devtool) => set('devtool', devtool);
