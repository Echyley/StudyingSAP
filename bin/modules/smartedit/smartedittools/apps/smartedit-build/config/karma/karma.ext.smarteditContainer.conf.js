/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const base = require('./shared/karma.base.conf');

const {
    compose,
    set,
    karma: { headless }
} = require('../../builders');

module.exports = compose(
    headless(),
    set('exclude', ['**/index.ts', '**/*.d.ts'])
)(base);
