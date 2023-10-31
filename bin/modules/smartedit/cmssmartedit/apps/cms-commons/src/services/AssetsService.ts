/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';

/**
 * Determines the root of the production and test assets
 */
@Injectable()
export class AssetsService {
    private readonly PROD_ASSETS_SRC = '/cmssmartedit';

    // eslint-disable-next-line @typescript-eslint/member-ordering
    public getAssetsRoot(): string {
        return this.PROD_ASSETS_SRC;
    }
}
