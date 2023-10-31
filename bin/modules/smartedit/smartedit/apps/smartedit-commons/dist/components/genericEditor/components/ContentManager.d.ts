import { EventEmitter } from '@angular/core';
import { Observable } from 'rxjs';
/**
 * Directive solely responsible for handling the submitting of its current data state to
 * an onSave input method and notifying of success and failure.
 *
 * ### Example
 *
 *      <form
 *          [contentManager]="{onSave: editor.submit$}"
 *          (onSuccess)="editor.onSuccess($event)"
 *          (onError)="editor.onFailure($event)"
 *      >
 *      </form>
 *
 * @param option object containing the onSave method of type (data: T) => Observable<T>
 * @param onSuccess outputs the successful result of onSave invocation
 * @param onError outputs the failing result of onSave invocation
 */
export declare class ContentManager<T> {
    set option(option: {
        onSave: () => Observable<T>;
    });
    /**
     * Called when a saving is a success.
     */
    onSuccess: EventEmitter<T>;
    /**
     * Called when there is an error after saving.
     */
    onError: EventEmitter<any>;
    /**
     * Submitting state of the manager.
     *
     * @type {boolean}
     */
    submitting: boolean;
    /**
     *  @internal
     */
    private _onSave;
    save(): Observable<T>;
}
