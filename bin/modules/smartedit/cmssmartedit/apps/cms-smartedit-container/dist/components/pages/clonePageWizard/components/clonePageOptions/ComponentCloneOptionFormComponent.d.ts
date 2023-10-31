import { EventEmitter, OnInit } from '@angular/core';
export declare class ComponentCloneOptionFormComponent implements OnInit {
    onSelectionChange: EventEmitter<string>;
    componentInSlotOption: string;
    readonly CLONE_COMPONENTS_IN_CONTENT_SLOTS_OPTION: {
        REFERENCE_EXISTING: string;
        CLONE: string;
    };
    constructor();
    ngOnInit(): void;
    updateComponentInSlotOption(option: string): void;
}
