import { Component, input, output, signal } from '@angular/core';
 
import { ZardComboboxComponent, type ZardComboboxOption } from '@/shared/components/base/combobox/combobox.component';
import { ZardButtonTypeVariants } from '../../base/button/button.variants';
import { ZardButtonSizeVariants } from '../../base/button/button.variants';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
 
@Component({
  selector: 'app-combobox',
  imports: [
    ZardComboboxComponent,
    ReactiveFormsModule
  ],
  standalone: true,
  template: `
      <z-combobox
          zWidth="full"
          [buttonSize]="buttonSize()"
          [value]="_value()"
          [disabled]="_disabled()"
          [options]="options()"
          [placeholder]="placeholder()"
          [searchPlaceholder]="searchPlaceholder()"
          [emptyText]="emptyText()"
          [buttonVariant]="buttonVariant()"
          [searchable]="searchable()"
          [ariaLabel]="ariaLabel()"
          [formControl]="optionsControl"
      />
  `,
})
export class AppComboboxComponent {

  /** Selected value of the component */
  protected _value = signal<string | null>(null);
  /** Disabled state */
  protected _disabled = signal<boolean>(false);
  /** Options to set in the combobox */
  options = input<ZardComboboxOption[]>([]);
  /** Placeholder to display */
  placeholder = input<string>('Selecciona una opción');
  /** Placeholder to display in the search bar of the options */
  searchPlaceholder = input<string>('Buscar opción');
  /** Text to display when no searched options match */
  emptyText = input<string>('Sin resultados.');
  /** If wether to display or not the search input */
  searchable = input<boolean>(true);
  /** Button type to display the component */
  buttonVariant = input<ZardButtonTypeVariants>('outline');
  /** Button size varian of the component, it defines the height */
  buttonSize = input<ZardButtonSizeVariants>('default');
  /** Aria label (tooltip of the component) */
  ariaLabel = input<string>('Selecciona una opción');
  /** On selected event that emits the selected option */
  onSelected = output<ZardComboboxOption>();


  optionsControl = new FormControl<string | null>(null);
  

  /**
   * Gets the value for the combobox.
   * @returns The value of the combobox.
   */
  getValue(): any | null {
    return this.optionsControl.value;
  }
  /**
   * Sets the value for the combobox.
   * @param value - The value to set.
   */
  setValue(value: string) {
    this.optionsControl.setValue(value);
  }
 
  /**
   * Resets the current value to null
   */
  clearValue() {
    this.optionsControl.setValue(null);
  }

  /**
   * Sets the component disabled state
   * @param state true for disabled, false otherwise.
   */
  setDisabled(state: boolean): void {
    this._disabled.set(state);
  }

}
 