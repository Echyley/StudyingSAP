import { CdkDragMove } from '@angular/cdk/drag-drop';
export declare class SegmentDragAndDropService {
    private document;
    currentHoverDropListId?: string;
    constructor(document: Document);
    dragMoved(event: CdkDragMove<HTMLElement>): void;
    dragReleased(): void;
}
