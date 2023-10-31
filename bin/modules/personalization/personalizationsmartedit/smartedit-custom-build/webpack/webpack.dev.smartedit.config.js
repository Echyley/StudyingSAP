/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const base = require('../../smartedit-build/config/webpack/webpack.ext.dev.smartedit.config');

const {
    compose
} = require('../../smartedit-build/builders');

const {
    smartedit
} = require('./webpack.shared.config');

module.exports = compose(
    smartedit()
)(base);
