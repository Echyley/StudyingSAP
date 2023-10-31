/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const set = require('../set');

/**
 * @ngdoc service
 * @name ConfigurationBuilder.service:karma.webpack
 * @description
 * Sets a the webpack object to a configuration object.
 *
 * @param {object} webpack The webpack configuration.
 * @returns {function(config)} A builder for a karma configuration object.
 */
module.exports = (webpack) => set('webpack', webpack);
