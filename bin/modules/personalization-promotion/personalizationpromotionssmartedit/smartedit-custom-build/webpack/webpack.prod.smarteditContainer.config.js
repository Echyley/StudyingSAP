/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const base = require('../../smartedit-build/config/webpack/webpack.ext.prod.smarteditContainer.config');

const {
    compose
} = require('../../smartedit-build/builders');

const {
    smarteditContainer
} = require('./webpack.shared.config');

module.exports = compose(
    smarteditContainer()
)(base);
