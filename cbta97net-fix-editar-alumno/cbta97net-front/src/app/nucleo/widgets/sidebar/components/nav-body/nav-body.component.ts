import { Component, OnInit } from '@angular/core';
import { SidebarNavItemComponent } from './nav-item/nav-item.component';
import { SidebarNavSectionComponent } from './nav-section/nav-section.component';
import { RouterLink, RouterLinkActive } from '@angular/router';

@Component({
    standalone: true,
    selector: 'sidebar-body',
    templateUrl: './nav-body.component.html',
    styles: [`
        :host {
            display: flex;
            flex: 1 1 auto;
            min-height: 0;
        }
    `],
    imports: [
        SidebarNavItemComponent,
        SidebarNavSectionComponent,
        RouterLink,
        RouterLinkActive
    ]
})
export class SidebarBodyComponent implements OnInit {
    constructor() { }

    ngOnInit() { }
}