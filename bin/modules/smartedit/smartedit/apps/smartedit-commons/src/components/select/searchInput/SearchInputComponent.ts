/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    ChangeDetectionStrategy,
    Component,
    ElementRef,
    EventEmitter,
    Input,
    OnDestroy,
    OnInit,
    Output,
    ViewChild
} from '@angular/core';
import { ISharedDataService } from '@smart/utils';
import { Observable, Subject, Subscription } from 'rxjs';
import { debounceTime, distinctUntilChanged, filter } from 'rxjs/operators';
import { IConfiguration } from 'smarteditcommons';

/**
 * Represents Search Input for Select Component.
 *
 * @internal
 */
@Component({
    selector: 'se-select-search-input',
    changeDetection: ChangeDetectionStrategy.OnPush,
    host: {
        '[class.se-select-search-input]': 'true'
    },
    templateUrl: './SearchInputComponent.html'
})
export class SearchInputComponent implements OnInit, OnDestroy {
    @Input() isDisabled: boolean;
    @Input() isReadOnly: boolean;
    @Input() isTypeAheadEnabled: boolean;
    @Input() placeholder: string;
    @Input() search: string;

    @Output() searchKeyup = new EventEmitter<{ event: Event; value: string }>();
    @Output() searchChange = new EventEmitter<string>();

    @ViewChild('searchInput', { static: false }) searchInput: ElementRef;

    private keyUpEvent: KeyboardEvent;

    private searchTerm$: Observable<string>;
    private searchTermSubject: Subject<string>;
    private searchTermSubscription: Subscription;
    private configurations: IConfiguration;

    constructor(private elementRef: ElementRef, private sharedDataService: ISharedDataService) {}

    async ngOnInit(): Promise<void> {
        this.configurations = (await this.sharedDataService.get('configuration')) as IConfiguration;
        this.initSearchInputFilter();
    }

    ngOnDestroy(): void {
        this.searchTermSubscription.unsubscribe();
    }

    get nativeElement(): any {
        return this.elementRef.nativeElement;
    }

    get inputElement(): any {
        return this.searchInput.nativeElement;
    }

    public focus(): void {
        this.inputElement.focus();
    }

    public onChange(value: string): void {
        this.isTypeAheadEnabled
            ? this.searchTermSubject.next(value)
            : this.searchChange.emit(value);
    }

    public onKeyup(event: KeyboardEvent): void {
        this.keyUpEvent = event;
        const value = (event.target as HTMLInputElement).value;
        this.isTypeAheadEnabled
            ? this.searchTermSubject.next(value)
            : this.searchKeyup.emit({ event: this.keyUpEvent, value });
    }

    private initSearchInputFilter(): void {
        this.searchTermSubject = new Subject<string>();
        this.searchTerm$ = this.searchTermSubject.asObservable().pipe(
            filter((text: string) =>
                this.configurations && this.configurations.typeAheadMiniSearchTermLength
                    ? text.length > 0 &&
                      text.length > this.configurations.typeAheadMiniSearchTermLength
                    : true
            ),
            debounceTime((this.configurations && this.configurations.typeAheadDebounce) || 500),
            distinctUntilChanged()
        );
        this.searchTermSubscription = this.searchTerm$.subscribe((value) => {
            this.searchKeyup.emit({ event: this.keyUpEvent, value });
            this.searchChange.emit(value);
        });
    }
}
