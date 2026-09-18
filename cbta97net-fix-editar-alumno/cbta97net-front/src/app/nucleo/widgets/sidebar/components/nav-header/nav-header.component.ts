import { Component, input, Input } from '@angular/core';

@Component({
  selector: 'sidebar-header',
  standalone: true,
  templateUrl: './nav-header.component.html',
})
export class SidebarHeaderComponent {
  title = input<string>('CBTA97Net');
  logoSrc = input<string>('cbtalogo.png');
  logoAlt = input<string>('Escudo CBTA 97');
}

