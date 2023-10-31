/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
module.exports = {
    compose: require('./compose'),
    group: require('./compose'),
    add: require('./add'),
    set: require('./set'),
    unset: require('./unset'),
    merge: require('./merge'),
    operate: require('./operate'),
    execute: require('./execute'),

    webpack: require('./webpack'),
    karma: require('./karma'),
    utils: require('./utils')
};
