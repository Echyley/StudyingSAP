/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const base = require('../../smartedit-build/config/webpack/webpack.ext.karma.smarteditContainer.config');

const {
    compose
} = require('../../smartedit-build/builders');

const {
    smarteditContainerKarma
} = require('./webpack.shared.config');

module.exports = compose(
    smarteditContainerKarma()
)(base);
