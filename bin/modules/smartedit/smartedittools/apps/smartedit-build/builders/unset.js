/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const { keysConfigLoop } = require('./utils');

/**
 * @ngdoc service
 * @name ConfigurationBuilder.service:unset
 * @description
 * Removes a nested object provided by the keys.
 *
 * @param {string} keys A path of keys separated by a period. The last key being to one to remove from the object.
 * @returns {function(config)} A builder for a configuration object.
 */
module.exports = (keys) => (config) => {
    return keysConfigLoop(keys, config, (current, key) => {
        delete current[key];
    });
};
