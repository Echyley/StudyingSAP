import { ElementRef, OnChanges, Renderer2 } from '@angular/core';
/**
 * @ignore
 *
 * Used for rendering dynamic components decorated with {@link SeCustomComponent}.
 * It is meant for configurations that requires component to be sent through {@link MessageGateway} in postMessage payload.
 * Instead `component` use `componentName` as a configuration parameter.
 *
 * Due to {@link https://developer.mozilla.org/en-US/docs/Web/API/Window/postMessage postMessageApi},
 * component class / constructor function, cannot be sent through postMessage (it is removed).
 *
 * Note: Component must be also registered in @NgModule entryComponents array.
 *
 * ### Example
 *
 *
 *      \@SeCustomComponent()
 *      \@Component({
 *          selector: 'se-my-custom-component',
 *          templateUrl: './SeMyComponent.html'
 *      })
 *      export class MyCustomComponent {}
 *
 *      \@Component({
 *          selector: 'se-my-container',
 *          template: `<div [seCustomComponentOutlet]="'MyCustomComponent'"></div>`
 *      })
 *      export class MyContainer {}
 *
 *      \@NgModule({
 *          imports: [CustomComponentOutletDirectiveModule]
 *          declarations: [MyContainer, MyCustomComponent],
 *          entryComponents: [MyContainer, MyCustomComponent]
 *      })
 *      export class MyModule {}
 *
 */
export declare class CustomComponentOutletDirective implements OnChanges {
    private elementRef;
    private renderer;
    /** Component Name corresponding to smarteditDecoratorPayloads registry */
    componentName: string;
    private component;
    private hasView;
    constructor(elementRef: ElementRef<HTMLElement>, renderer: Renderer2);
    ngOnChanges(): void;
    private updateView;
    private createView;
    private removeView;
}
