/**
 * @ngdoc service
 *
 * @description
 * Service to verify whether the item is editable in a particular context.
 */
export declare abstract class IContextAwareEditableItemService {
    /**
     * @ngdoc method
     *
     * @description
     * Verifies whether the item is editable in current context or not.
     *
     * @param {string} itemUid The item uid.
     *
     * @returns {Promise} A promise that resolves to a boolean. It will be true, if the item is editable, false otherwise.
     */
    isItemEditable(itemUid: string): Promise<boolean>;
}
