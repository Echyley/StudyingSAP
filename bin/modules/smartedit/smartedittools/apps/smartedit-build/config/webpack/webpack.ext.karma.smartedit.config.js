/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const base = require('./shared/webpack.bare.config');

const {
    compose,
    webpack: { karma }
} = require('../../builders');

module.exports = compose(
    karma()
)(base);
