import { Component, EventEmitter, input, Output, signal } from "@angular/core";
import { NgClass } from "@angular/common";
import { ZardButtonComponent } from "@/shared/components/base/button/button.component";
import type { ZardButtonShapeVariants, ZardButtonSizeVariants, ZardButtonTypeVariants } from "@/shared/components/base/button/button.variants";

type iconSizeVariants = 'xs' | 'sm' | 'md' | 'lg' | 'xl';

@Component({
  selector: 'app-button',
  templateUrl: './app-button.component.html',
  imports: [
    NgClass,
    ZardButtonComponent
  ]
})
export class AppButtonComponent {

  @Output() clicked = new EventEmitter<Event>();

  /**
   * Wrapper props for `z-button`.
   * - `variant` maps to `zType`
   * - `size` maps to `zSize`
   */
  variant = input<ZardButtonTypeVariants>('default');
  size = input<ZardButtonSizeVariants>('lg');
  type = input<ZardButtonTypeVariants>('default');
  shape = input<ZardButtonShapeVariants>('default')
  placeHolder = input<string>('');
  icon = input<string>('');
  iconSize = input<iconSizeVariants>('xs');

  protected _isDisabled = signal<boolean>(false);
  protected _isLoading = signal<boolean>(false);

  isLoading(state: boolean) {
    this._isLoading.set(state);
    this._isDisabled.set(state);
  }

  isDisabled(state: boolean) {
    this._isDisabled.set(state);
  }

  protected onClick(event: Event) {
    if (this._isDisabled() || this._isLoading()) return
    this.clicked.emit(event);
  }

}