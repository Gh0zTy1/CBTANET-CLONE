import { Component, computed, ElementRef, input, signal, ViewChild } from "@angular/core";
import { NgClass } from "@angular/common";

/**
 * Cellphone input component with floating label pattern.
 * Supports phone number formatting with mask (XXX) XXX XXXX.
 * 
 * @example
 * ```html
 * <input-cellphone-outline
 *   placeHolder="Teléfono"
 *   [cellphoneNumberLength]="10"
 * />
 * ```
 */
@Component({
  selector: 'input-cellphone-outline',
  templateUrl: './input-cellphone-outline.component.html',
  imports: [NgClass]
})
export class InputCellphoneOutlineComponent {

  /** Reference to the native input element */
  @ViewChild('cellphoneInput') private input!: ElementRef<HTMLInputElement>;

  /** Placeholder text displayed when input is empty */
  placeHolder = input.required<string>();
  /** Minimum length for validation */
  min = input<string>('');
  /** Maximum length allowed */
  max = input<string>('');
  /** Input name attribute */
  name = input<string>('');
  /** Autocomplete attribute */
  autocomplete = input<string>('off');
  /** Expected phone number length */
  cellphoneNumberLength = input.required<number>();
  
  /** Current formatted value with mask */
  protected _value = signal<string>('');
  /** Raw value without mask (digits only) */
  protected _rawValue = signal<string>('');
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
   * @param value - Phone number to set
   */
  setValue(value: string): void {
    this._rawValue.set(value);
    this._value.set(this.applyMask(value));
  }

  /**
   * Gets the current input value (raw digits only)
   * @returns Raw phone number or undefined if empty
   */
  getValue(): string {
    const value: string = this._rawValue().trim();
    return value;
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
    this._rawValue.set('');
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

    const digits = input.value.replace(/\D+/g, '').slice(0, 10);

    this._rawValue.set(digits);

    input.value = this.applyMask(digits);
    this._value.set(input.value);
  }

  /**
   * Applies phone mask to digits (XXX) XXX XXXX
   * @param digits - Raw digit string
   * @returns Formatted phone number
   */
  private applyMask(digits: string): string {
    if (digits.length <= 3) {
      return `${digits}`;
    }

    if (digits.length <= 6) {
      return `(${digits.slice(0, 3)}) ${digits.slice(3)}`;
    }

    return `(${digits.slice(0, 3)}) ${digits.slice(3, 6)} ${digits.slice(6, 10)}`;
  }

}
