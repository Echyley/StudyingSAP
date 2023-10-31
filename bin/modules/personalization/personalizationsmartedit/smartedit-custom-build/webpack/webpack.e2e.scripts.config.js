/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const {
    resolve
} = require('path');

const {
    compose,
    set,
    webpack: {
        multiEntry
    }
} = require('../../smartedit-build/builders');

const {
    scripts
} = require('../../smartedit-build/config/webpack/webpack.e2e.shared.config');
