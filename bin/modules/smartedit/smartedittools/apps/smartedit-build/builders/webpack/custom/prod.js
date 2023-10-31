/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const group = require('../../compose');
const mode = require('../mode');
const devtool = require('../devtool');
const minify = require('./minify');

/**
 * @ngdoc service
 * @name ConfigurationBuilder.service:webpack.prod
 * @description
 * A preset group of builders for configuring a production webpack config.
 *
 * @returns {function(config)} A builder for a webpack configuration object.
 */
module.exports = () =>
    group(
        mode('production'),
        devtool('none'),

        minify()
    );
