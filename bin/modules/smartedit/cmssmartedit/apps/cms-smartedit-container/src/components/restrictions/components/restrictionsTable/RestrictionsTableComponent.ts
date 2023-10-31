/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, EventEmitter, Input, OnChanges, OnInit, Output } from '@angular/core';
import { isEqual } from 'lodash';
import { GenericEditorFieldMessage, IDropdownMenuItem } from 'smarteditcommons';
import {
    IPageRestrictionCriteria,
    PageRestrictionsCriteriaService
} from '../../../../services/pageRestrictions/PageRestrictionsCriteriaService';
import { RestrictionCMSItem } from '../../types';

@Component({
    selector: 'se-restrictions-table',
    templateUrl: './RestrictionsTableComponent.html',
    styleUrls: ['./RestrictionsTableComponent.scss']
})
export class RestrictionsTableComponent implements OnInit, OnChanges {
    @Input() restrictions: RestrictionCMSItem[];
    @Input() customClass: string;
    @Input() editable: boolean;
    @Input() restrictionCriteria?: IPageRestrictionCriteria;
    @Input() errors?: GenericEditorFieldMessage[];

    @Output() onEdit?: EventEmitter<RestrictionCMSItem>;
    @Output() onRemove: EventEmitter<number>;

    public criteriaOptions: IPageRestrictionCriteria[];
    public defaultActions: IDropdownMenuItem[];

    private readonly REMOVE_RESTRICTION_KEY = 'se.cms.restrictions.item.remove';
    private readonly EDIT_RESTRICTION_KEY = 'se.cms.restrictions.item.edit';

    private oldRestrictionsEditability: boolean[];

    constructor(private pageRestrictionsCriteriaService: PageRestrictionsCriteriaService) {
        this.onEdit = new EventEmitter();
        this.onRemove = new EventEmitter();
        this.errors = [];
        this.criteriaOptions = [];
        this.defaultActions = [];
        this.oldRestrictionsEditability = [];
    }

    ngOnInit(): void {
        this.oldRestrictionsEditability = this.getRestrictionsEditability(this.restrictions);
        this.defaultActions = this.getDefaultActions();
        this.provideActionsForRestrictions();
        this.criteriaOptions = this.pageRestrictionsCriteriaService.getRestrictionCriteriaOptions();
        this.resetRestrictionCriteria();
    }

    ngOnChanges(): void {
        if (this.restrictionsEditabilityChanged(this.restrictions)) {
            this.provideActionsForRestrictions();
            this.oldRestrictionsEditability = this.getRestrictionsEditability(this.restrictions);
        }
        this.resetRestrictionCriteria();
    }

    public resetRestrictionCriteria(): void {
        if (!this.restrictions || this.restrictions.length < 2) {
            // default if none is provided or restrictions less than 2
            this.restrictionCriteria = this.criteriaOptions[0];
        }
    }

    public removeRestriction(restrictionToRemove: RestrictionCMSItem): void {
        const restrictionIndex = this.restrictions.findIndex(
            (restriction) => restriction.uuid === restrictionToRemove.uuid
        );
        this.onRemove.emit(restrictionIndex);
        this.removeUnnecessaryError(restrictionIndex);
        this.modifyErrorPositions(restrictionIndex);
    }

    public editRestriction(restriction: RestrictionCMSItem): void {
        this.onEdit.emit(restriction);
    }

    public isInError(index: number): boolean {
        return !!this.errors?.some((error) => error.position === index);
    }

    /**
     * Returns array of booleans where each element represents whether the restriction is editable or not.
     */
    public getRestrictionsEditability(restrictions: RestrictionCMSItem[]): boolean[] {
        return (restrictions || []).map((restriction) => restriction.canBeEdited);
    }

    private modifyErrorPositions(removedRestrictionIndex: number): void {
        this.errors = this.errors.map((error) => {
            if (error.position >= removedRestrictionIndex) {
                error.position = error.position - 1;
            }
            return error;
        });
    }

    private removeUnnecessaryError(removedRestrictionIndex: number): void {
        const errorIndex = this.errors.findIndex(
            (error) => error.position === removedRestrictionIndex
        );

        if (errorIndex > -1) {
            this.errors = this.errors.filter((_, index) => index !== errorIndex);
        }
    }

    /**
     * Returns the list of all default actions for a restriction.
     */
    private getDefaultActions(): IDropdownMenuItem[] {
        return [
            {
                key: this.EDIT_RESTRICTION_KEY,
                callback: (restriction: RestrictionCMSItem): void =>
                    this.editRestriction(restriction)
            },
            {
                key: this.REMOVE_RESTRICTION_KEY,
                callback: (restrictionToRemove: RestrictionCMSItem): void =>
                    this.removeRestriction(restrictionToRemove),
                customCss: 'se-dropdown-item__delete'
            }
        ];
    }

    /**
     * Returns actions for a restriction. It verifies whether the restriction is editable or not.
     * If the restriction is not editable only remove restriction action is returned.
     */
    private getRestrictionActions(restriction: RestrictionCMSItem): IDropdownMenuItem[] {
        const actions = this.getDefaultActions();
        if (!restriction.canBeEdited) {
            return actions.filter((action) => action.key !== this.EDIT_RESTRICTION_KEY);
        }
        return actions;
    }

    /**
     * Provides the list of actions for each restriction.
     */
    private provideActionsForRestrictions(): void {
        (this.restrictions || []).forEach((restriction) => {
            restriction.actions = this.getRestrictionActions(restriction);
        });
    }

    /**
     * Verifies whether the restrictions editability is changed.
     */
    private restrictionsEditabilityChanged(restrictions: RestrictionCMSItem[]): boolean {
        const restrictionsEditability = this.getRestrictionsEditability(restrictions);
        return !isEqual(restrictionsEditability, this.oldRestrictionsEditability);
    }
}
