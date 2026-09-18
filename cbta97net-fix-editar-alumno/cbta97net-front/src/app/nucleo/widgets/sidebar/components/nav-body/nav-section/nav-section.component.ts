import { Component, Input } from '@angular/core';

@Component({
    selector: 'sidebar-nav-section',
    standalone: true,
    templateUrl: './nav-section.component.html',
})
export class SidebarNavSectionComponent {
    /** Label shown in the dropdown header (e.g. "Grupos"). */
    @Input({ required: true }) label!: string;

    /** Material icon name shown on the left of the label. */
    @Input({ required: true }) icon!: string;

    /** Open/closed state of the dropdown. */
    open = false;

    toggle(event: Event) {
        event.preventDefault();
        this.open = !this.open;
    }
}
