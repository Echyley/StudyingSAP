/**
 * @internal
 * @ignore
 */
export declare abstract class StorageNamespaceConverter {
    /**
     * Given:
     *  namespace = nmsp
     *  storageId = stoid
     *
     * Produces:
     *  newStorageId = nmsp<ns:id>stoid
     *
     * Fastest implementation I could think of that (most likely) will not clash with weird storageIds
     *
     * This algorithm is a bit overly simple, and assumes that neither storageId nor namespace contains "<ns:id>"
     * I think this is a fairly safe assumption, but if we have time in the future, we should escape any existing
     * matches of the string.
     */
    private static readonly separator;
    private static readonly namespaceDecoderRegexStr;
    static ERR_INVALID_NAMESPACED_ID(id: string): Error;
    static getNamespacedStorageId(namespace: string, storageId: string): string;
    static getStorageIdFromNamespacedId(namespacedId: string): string;
    static getNamespaceFromNamespacedId(namespacedId: string): string;
}
