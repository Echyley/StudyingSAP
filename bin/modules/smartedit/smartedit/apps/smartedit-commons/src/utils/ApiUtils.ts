/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Collection of utility methods for handling responses from backend calls
 */
export class ApiUtils {
    /**
     * When provided with a response returned from a backend call, will filter the response
     * to retrieve the data of interest.
     *
     * @returns {Array} Returns the array from the response.
     */
    getDataFromResponse(response: any): any {
        const dataKey = Object.keys(response).filter(function (key) {
            return response[key] instanceof Array;
        })[0];

        return response[dataKey];
    }

    /**
     * When provided with a response returned from a backend call, will filter the response
     * to retrieve the key holding the data of interest.
     *
     * @returns Returns the name of the key holding the array from the response.
     */
    getKeyHoldingDataFromResponse(response: any): string {
        const dataKey = Object.keys(response).filter(function (key) {
            return response[key] instanceof Array;
        })[0];

        return dataKey;
    }
}
export const apiUtils = new ApiUtils();
