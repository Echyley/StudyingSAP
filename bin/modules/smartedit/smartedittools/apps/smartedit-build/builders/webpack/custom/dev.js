/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const group = require('../../compose');
const external = require('../external');
const devtool = require('../devtool');

/**
 * @ngdoc service
 * @name ConfigurationBuilder.service:webpack.dev
 * @description
 * A preset group of builders for configuring a development webpack config.
 *
 * @returns {function(config)} A builder for a webpack configuration object.
 */
module.exports = () =>
    group(
        devtool('source-map'),
        external('jasmine', 'jasmine'),
        external('angular-mocks', 'angular-mocks')
    );
