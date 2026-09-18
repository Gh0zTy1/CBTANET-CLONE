import { Component, input, Input } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';

@Component({
    selector: 'sidebar-nav-item',
    standalone: true,
    templateUrl: './nav-item.component.html',
    imports: [RouterLink, RouterLinkActive],
})
export class SidebarNavItemComponent {
    label =  input<string>();
    icon = input<string>();
    link = input<string>();
}

