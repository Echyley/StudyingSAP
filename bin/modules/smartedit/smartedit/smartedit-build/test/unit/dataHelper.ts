/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { cloneDeep } from 'lodash';

class DataHelper {
    public getFetchPageResults(data: any[], currentPage: number, pageSize: number): any[] {
        return cloneDeep(data)
            .slice(currentPage * pageSize)
            .slice(0, pageSize);
    }
}

export const dataHelper = new DataHelper();
