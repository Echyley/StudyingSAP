# FormBuilder Module

Build forms dynamically in a data driven approach.

## Features

-   Build forms dynamically with any app specific look.
-   Create data driven components with SOLID principal in mind for:
    -   Easier testing
    -   Predictability
    -   Less flakiness
    -   Relies on the single source of truth of the data
-   Components, validators, and asynchronous validators can be registered at runtime or at DI time.
    Through module configuration.
-   Components can be represented in any fashion in their isolated environment.
-   Reactive to property changes.

## Usage

### Module Setup

The example below shows a dynamic component and is registered in the AppModule below.

#### Dynamic Inputs

This text component has dynamic inputs that is passed from the schema's `inputs` property to the component.
Those dynamic values that are set by the class will override the values for those inputs that are
initially `undefined` in the schema. The component's dynamic input values can be changed when
using the api `setInput(key: string)` on a form.

```typescript
@Component({
    selector: 'text-input',
    template: `
        <div class="form-group">
            <label>{{ label }}<span *ngIf="required">*</span></label>
            <input
                class="form-control"
                [type]="type"
                [formControl]="control"
                [ngClass]="{
                    'is-invalid': isInvalid
                }"
            />
            <span *ngIf="isPending">PENDING...</span>
        </div>
        <field-errors [form]="control"></field-errors>
    `
})
export class TextInputComponent implements DynamicInputChange {
    @DynamicForm()
    form: FormField;

    @DynamicInput()
    type = 'text';

    @DynamicInput()
    label: string;

    @DynamicInput()
    required = false;

    get isPending() {
        return this.control.pending;
    }

    get isInvalid() {
        return this.control.invalid && this.control.touched;
    }

    onDynamicInputChange() {
        // If any decorated property changes, this method will execute.
    }
}

// COMPONENTS ===============
export const TYPES: ComponentTypeMap = {
    text: TextInputComponent
};

// VALIDATORS ===============
export const VALIDATORS: ValidatorMap = {
    required: () => Validators.required,
    min: (min: number) => {
        return (control: AbstractControl) => {
            if (control.value > min) {
                return null;
            }
            return {
                min: `Value must be greater than ${min}.`
            };
        };
    }
};

@NgModule({
    declarations: [TextInputComponent],
    imports: [
        CommonModule,
        FormBuilderModule.forRoot({
            types: TYPES,
            validators: VALIDATORS
        }),
        ReactiveFormsModule
    ]
})
export class AppModule {}
```

### Usage for dynamically generating a form.

```typescript
interface Person {
    name: string;
    age: number;
    address: {
        number?: number;
        street?: string;
    };
}

@Component({
    selector: 'example',
    templateUrl: './example.component.html'
})
export class SimpleComponent {
    form: FormGrouping;
    value: Person = null;

    /**
     * Schema representing the form.
     */
    schema: FormGroupSchema = {
        type: 'group',
        schemas: {
            name: {
                type: 'field',
                component: 'text',
                disabled: false,
                validators: {
                    required: true
                },
                inputs: {
                    label: 'Name:',
                    required: true
                }
            },
            age: {
                type: 'field',
                component: 'text',
                validators: {
                    required: true,
                    min: 0
                },
                inputs: {
                    label: 'Age:',
                    type: 'number',
                    required: true
                }
            },
            address: {
                type: 'group',
                schemas: {
                    number: {
                        type: 'field',
                        component: 'text',
                        inputs: {
                            label: 'House Number:',
                            type: 'number'
                        }
                    },
                    street: {
                        type: 'field',
                        component: 'text',
                        inputs: {
                            label: 'Street Name:',
                            type: 'number'
                        }
                    }
                }
            }
        }
    };

    constructor(private builder: SchemaCompilerService) {
        this.form = this.builder.compileGroup(this.value, this.schema);
    }

    submit() {
        this.form.setNestedErrors([]);
    }
}
```

#### Component Template

```html
Enter information about yourself.
<ng-template [formRenderer]="form"></ng-template>

<button (click)="submit()" [disabled]="form.control.pending || form.control.invalid">Submit</button>
```

## Missing Features

-   [ ] Implement form arrays.
-   [ ] Add dirty form handler utility. It's called DirtyForm and it's already implemented in the SmartEdit POC repository.
