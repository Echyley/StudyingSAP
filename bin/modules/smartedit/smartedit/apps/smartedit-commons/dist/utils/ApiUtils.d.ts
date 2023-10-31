/**
 * Collection of utility methods for handling responses from backend calls
 */
export declare class ApiUtils {
    /**
     * When provided with a response returned from a backend call, will filter the response
     * to retrieve the data of interest.
     *
     * @returns {Array} Returns the array from the response.
     */
    getDataFromResponse(response: any): any;
    /**
     * When provided with a response returned from a backend call, will filter the response
     * to retrieve the key holding the data of interest.
     *
     * @returns Returns the name of the key holding the array from the response.
     */
    getKeyHoldingDataFromResponse(response: any): string;
}
export declare const apiUtils: ApiUtils;
