/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const {
    compose,
    webpack: {
        multiEntry
    }
} = require('../../smartedit-build/builders');

const {
    smarteditcontainer
} = require('../../smartedit-build/config/webpack/webpack.e2e.shared.config');
