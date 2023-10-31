import { OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { SmarteditRoutingService } from 'smarteditcommons';
export declare class PagesLinkComponent implements OnInit {
    private route;
    private seRouting;
    private siteId;
    private catalogId;
    private catalogVersion;
    constructor(route: ActivatedRoute, seRouting: SmarteditRoutingService);
    ngOnInit(): void;
    backToPagelist(): void;
}
