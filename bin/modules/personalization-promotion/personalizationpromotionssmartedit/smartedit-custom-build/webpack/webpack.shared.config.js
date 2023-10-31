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

const commonsAlias = alias('personalizationpromotionssmarteditcommons', resolve("./jsTarget/web/features/personalizationpromotionssmarteditcommons"));

const smartedit = group(
    commonsAlias,
    alias('personalizationpromotionssmartedit', resolve("./jsTarget/web/features/personalizationpromotionssmartedit"))
);
const smarteditContainer = group(
    commonsAlias,
    alias('personalizationpromotionssmarteditContainer', resolve("./jsTarget/web/features/personalizationpromotionssmarteditContainer")),
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
            'personalizationpromotionssmartedit': resolve('./jsTarget/web/features/personalizationpromotionssmartedit/PersonalizationpromotionssmarteditApp.ts')
        })
    ),
    smarteditContainer: () => group(
        smarteditContainer,
        entry({
            'personalizationpromotionssmarteditContainer': resolve('./jsTarget/web/features/personalizationpromotionssmarteditContainer/PersonalizationpromotionssmarteditContainerApp.ts')
        })
    )
};
