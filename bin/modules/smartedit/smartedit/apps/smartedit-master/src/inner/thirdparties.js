/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
if (!window.Zone) {
    /* tslint:disable-next-line */
    require('zone.js/dist/zone.js');
}

if (!window.smarteditJQuery && window.$ && window.$.noConflict) {
    window.smarteditJQuery = window.$.noConflict();
}
window.smarteditLodash = lodash.noConflict();

require('../../../../web/webroot/static-resources/thirdparties/blockumd/unblockumd');
