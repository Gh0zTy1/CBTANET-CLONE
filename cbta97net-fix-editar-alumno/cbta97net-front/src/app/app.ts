import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { ZardToastComponent } from './shared/components/base/toast';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, ZardToastComponent],
  template: `
      <router-outlet></router-outlet>
      <z-toaster />
    `,
})
export class App { }
