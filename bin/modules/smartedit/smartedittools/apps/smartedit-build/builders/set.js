/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const { keysConfigLoop } = require('./utils');

/**
 * @ngdoc service
 * @name ConfigurationBuilder.service:set
 * @description
 * Sets a value of the destined nested object.
 *
 * @param {string} keys A path of keys separated by a period. The last key being to one to set a value to the object.
 * @param {*} value The value to set to a key.
 * @returns {function(config)} A builder for a configuration object.
 */
module.exports = (keys, value) => (config) => {
    return keysConfigLoop(keys, config, (current, key) => {
        current[key] = value;
    });
};
