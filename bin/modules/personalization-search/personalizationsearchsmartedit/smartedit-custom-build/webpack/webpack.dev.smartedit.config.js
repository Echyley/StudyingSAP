/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
const base = require('../../smartedit-build/config/webpack/webpack.ext.dev.smartedit.config');

const {
    compose
} = require('../../smartedit-build/builders');

const {
    ySmartedit
} = require('./webpack.shared.config');

module.exports = compose(
    ySmartedit()
)(base);
