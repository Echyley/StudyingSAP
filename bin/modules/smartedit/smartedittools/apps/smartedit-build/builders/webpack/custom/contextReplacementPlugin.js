/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const webpack = require('webpack');
const plugin = require('../plugin');

/**
 * Adds a {@link https://webpack.js.org/plugins/context-replacement-plugin/ ContextReplacementPlugin} plugin to a webpack configuration.
 *
 * @param resourceRegExp
 * @returns {function(config)} A builder for a webpack configuration object.
 */
module.exports = (
    resourceRegExp,
    newContentResource = null,
    newContentRecursive = null,
    newContentRegExp = null
) =>
    plugin(
        new webpack.ContextReplacementPlugin(
            resourceRegExp,
            newContentResource,
            newContentRecursive,
            newContentRegExp
        )
    );
