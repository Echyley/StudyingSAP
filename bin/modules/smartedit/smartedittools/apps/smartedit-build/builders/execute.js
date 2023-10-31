/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * @ngdoc service
 * @name ConfigurationBuilder.service:execute
 * @description
 * Executes a builder only if it is a function. In some cases where a builder is 'false', because of conditional options,
 * it will return an untouched configuration object.
 *
 * @param {function(config)} builder A builder.
 * @returns {function(config)} A builder for a configuration object.
 */
module.exports = (builder) => (config) => {
    if (builder instanceof Function) {
        return builder(config);
    }
    return config;
};
