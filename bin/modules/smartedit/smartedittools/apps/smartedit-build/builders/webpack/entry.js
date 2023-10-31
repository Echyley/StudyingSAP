/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const set = require('../set');

/**
 * @ngdoc service
 * @name ConfigurationBuilder.service:webpack.entry
 * @description
 * Sets an entry configuration to a webpack config.
 *
 * @param {object} entry The entry configuration object.
 * @returns {function(config)} A builder for a webpack configuration object.
 */
module.exports = (entry) => set('entry', entry);
