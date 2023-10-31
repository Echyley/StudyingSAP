/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
const {
    resolve
} = require('path');

const {
    group,
    webpack: {
        entry,
        alias
    }
} = require('../../smartedit-build/builders');


const smartedit = group(
    alias('personalizationsearchsmartedit', resolve('./jsTarget/web/features/personalizationsearchsmartedit'))
);
const smarteditContainer = group(
    alias('personalizationsearchsmarteditcontainer', resolve('./jsTarget/web/features/personalizationsearchsmarteditContainer')),
);

module.exports = {
    ySmarteditKarma: () => group(
        smartedit
    ),
    ySmarteditContainerKarma: () => group(
        smarteditContainer
    ),
    ySmartedit: () => group(
        smartedit,
        entry({
            personalizationsearchsmartedit: resolve('./jsTarget/web/features/personalizationsearchsmartedit/personalizationsearchsmarteditModule.ts')
        })
    ),
    ySmarteditContainer: () => group(
        smarteditContainer,
        entry({
            personalizationsearchsmarteditContainer: resolve('./jsTarget/web/features/personalizationsearchsmarteditContainer/personalizationsearchsmarteditcontainerModule.ts')
        })
    )
};
