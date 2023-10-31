import { ContentManager } from './ContentManager';
/**
 * Applied on a DOM element, this Directive will trigger a submit of the data stored in
 * the parent {@link ContentManager} upon cliking.
 *
 * ### Example
 *
 *      <form [contentManager]="{onSave: editor.someSubmit}">
 *          <button [seSubmitBtn]="editor.isSubmitDisabled">Submit </button>
 *      </form>
 *
 * @param seSubmitBtn The optional callback returning a boolean to add more cases for disablement
 */
export declare class SubmitBtnDirective {
    private cm;
    isDisabled: () => boolean;
    constructor(cm: ContentManager<any>);
    /**
     * Modifies the disabled attribute to be disabled when saving.
     */
    get disabled(): boolean;
    /**
     * When the element is clicked the save operation is called in the content manager direcitve.
     */
    save($event: Event): void;
}
