import { Component, computed, ElementRef, input, signal, ViewChild } from "@angular/core";
import { NgClass } from "@angular/common";

/**
 * Number input component with floating label pattern.
 * Supports decimals, negatives, and numeric filtering.
 * 
 * @example
 * ```html
 * <input-number-outline
 *   placeHolder="Cantidad"
 *   min="0"
 *   max="100"
 *   [allowDecimals]="false"
 *   [allownNegatives]="false"
 * />
 * ```
 */
@Component({
  selector: 'input-number-outline',
  templateUrl: './input-number-outline.component.html',
  imports: [NgClass]
})
export class InputNumberOutlineComponent {

  /** Reference to the native input element */
  @ViewChild('input') private input!: ElementRef<HTMLInputElement>;

  /** Placeholder text displayed when input is empty */
  placeHolder = input.required<string>();
  /** Minimum value allowed */
  min = input<string>('');
  /** Maximum value allowed */
  max = input<string>('');
  /** Whether to allow decimal numbers */
  allowDecimals = input<boolean>(true);
  /** Whether to allow negative numbers */
  allownNegatives = input<boolean>(true);
  /** Input name attribute */
  name = input<string>('');
  /** Autocomplete attribute */
  autocomplete = input<string>('off');

  /** Current input value as string */
  protected _value = signal<string>('');
  /** Error state */
  protected _isError = signal<boolean>(false);
  /** Disabled state */
  protected _isDisabled = signal<boolean>(false);
  /** Error message to display */
  protected _errorMessage = signal<string>('');
  /** Focus state for label floating */
  protected _isFocused = signal<boolean>(false);

  /**
   * Determines if the label should float (when focused or has value)
   */
  protected _isLabelFloating = (): boolean => {
    return this._isFocused() || this._value().length > 0;
  };

  /**
   * Determinates the styles of the label depending on the states
   */
  protected labelClasses = computed(() => {
    if (this._isLabelFloating()) {
      return this._isError()
        ? 'text-xs text-red-500 -translate-y-1/2'
        : 'text-xs text-gray-600 -translate-y-1/2';
    }
  
    return 'text-sm text-gray-500 translate-y-1/2';
  });

  /**
   * Called when input receives focus
   */
  onFocus(): void {
    this._isFocused.set(true);
  }

  /**
   * Called when input loses focus
   */
  onBlur(): void {
    this._isFocused.set(false);
  }

  /**
   * Sets the input value programmatically
   * @param value - Number value to set
   */
  setValue(value: number): void {
    this._value.set(String(value));
  }

  /**
   * Gets the current input value as number
   * @returns Number value or undefined if invalid
   */
  getValue(): number | null {
    const value = Number(this._value().trim());
    return Number.isNaN(value) ? null : value;
  }

  /**
   * Sets the disabled state
   * @param state - Whether to disable the input
   */
  setDisabled(state: boolean) {
    if (this.input?.nativeElement) {
      this.input.nativeElement.disabled = state;
    }
  }

  /**
   * Clears the input value
   */
  clearValue() {
    this._value.set('');
    if (this.input?.nativeElement) {
      this.input.nativeElement.value = '';
    }
  }

  /**
   * Shows an error message
   * @param errorMessage - Error message to display
   */
  showError(errorMessage: string): void {
    this._isError.set(true);
    this._errorMessage.set(errorMessage);
  }

  /**
   * Hides the error state
   */
  hideError(): void {
    this._isError.set(false);
    this._errorMessage.set('');
  }

  /**
   * Handles input change events
   * @param event - Input event
   */
  protected onInput(event: Event) {
    const input = event.target as HTMLInputElement;
    let filteredValue = this.filterValue(input.value);

    this._value.set(filteredValue);
    input.value = filteredValue;
  }

  /**
   * Filters value to allow only valid numeric input
   * @param value - Raw input value
   * @returns Filtered numeric value
   */
  private filterValue(value: string): string {
    let filteredValue = value.replace(/\s/g, '');

    if (filteredValue === '') {
      return '';
    }

    let isNegative = false;
    if (this.allownNegatives() && filteredValue.startsWith('-')) {
      isNegative = true;
      filteredValue = filteredValue.substring(1);
    }

    const decimalRegex = this.allowDecimals() ? /[^0-9.]/g : /[^0-9]/g;
    filteredValue = filteredValue.replace(decimalRegex, '');

    if (this.allowDecimals()) {
      const parts = filteredValue.split('.');
      if (parts.length > 2) {
        filteredValue = parts[0] + '.' + parts.slice(1).join('');
      }

      if (filteredValue.startsWith('.')) {
        filteredValue = '0' + filteredValue;
      }
    }

    if (isNegative) {
      filteredValue = '-' + filteredValue;
    }

    return filteredValue;
  }

}
