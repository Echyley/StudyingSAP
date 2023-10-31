/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import './HelpComponent.scss';
import { Component, Input } from '@angular/core';

/**
 * This component represents a help button that will show a customizable popover on top of it when hovering.
 *
 * Either one of the template, templateUrl or Content Projection can be used to render the popover body.
 *
 *  ### Example using Content Projection
 *
 *      <se-help title="Title">
 *          <span>Some help text</span>
 *      </se-help>
 */
@Component({
    selector: 'se-help',
    templateUrl: './HelpComponent.html'
})
export class HelpComponent {
    @Input() title: string;
}
