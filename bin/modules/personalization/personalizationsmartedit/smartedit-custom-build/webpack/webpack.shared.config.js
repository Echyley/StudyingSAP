/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
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

const commonsAlias = alias('personalizationcommons', resolve("./jsTarget/web/features/personalizationcommons"));

const smartedit = group(
    commonsAlias,
    alias('personalizationsmartedit', resolve("./jsTarget/web/features/personalizationsmartedit"))
);
const smarteditContainer = group(
    commonsAlias,
    alias('personalizationsmarteditcontainer', resolve("./jsTarget/web/features/personalizationsmarteditcontainer")),
);

module.exports = {
    smarteditKarma: () => group(
        smartedit
    ),
    smarteditContainerKarma: () => group(
        smarteditContainer
    ),
    smartedit: () => group(
        smartedit,
        entry({
            'personalizationsmartedit': resolve('./jsTarget/web/features/personalizationsmartedit/index.ts')
        })
    ),
    smarteditContainer: () => group(
        smarteditContainer,
        entry({
            'personalizationsmarteditcontainer': resolve('./jsTarget/web/features/personalizationsmarteditcontainer/index.ts')
        })
    )
};
