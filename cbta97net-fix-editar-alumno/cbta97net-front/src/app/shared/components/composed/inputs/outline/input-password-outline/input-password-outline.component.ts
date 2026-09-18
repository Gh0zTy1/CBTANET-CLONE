import { Component, computed, ElementRef, input, signal, ViewChild } from "@angular/core";
import { NgClass } from "@angular/common";
import { ZardTooltipImports } from "@/shared/components/base/tooltip";

/**
 * Password input component with floating label pattern.
 * Supports password visibility toggle and input filtering.
 * 
 * @example
 * ```html
 * <input-password-outline
 *   placeHolder="Contraseña"
 *   min="8"
 *   max="32"
 *   [allowSpaces]="false"
 * />
 * ```
 */
@Component({
  selector: 'input-password-outline',
  templateUrl: './input-password-outline.component.html',
  imports: [
    NgClass,
    ZardTooltipImports
  ]
})
export class InputPasswordOutlineComponent {

  /** Reference to the native input element */
  @ViewChild('input') private input!: ElementRef<HTMLInputElement>;

  /** Placeholder text displayed when input is empty */
  placeHolder = input.required<string>();
  /** Minimum length for validation */
  min = input<string>('0');
  /** Maximum length allowed */
  max = input<string>('100');
  /** Whether to allow spaces in password */
  allowSpaces = input<boolean>(true);
  /** Input name attribute */
  name = input<string>('');
  /** Autocomplete attribute */
  autocomplete = input<string>('off');

  /** Current input value */
  protected _value = signal<string>('');
  /** Error state */
  protected _isError = signal<boolean>(false);
  /** Disabled state */
  protected _isDisabled = signal<boolean>(false);
  /** Whether password is visible */
  protected _showPassword = signal<boolean>(false);
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
   * @param value - Value to set
   */
  setValue(value: string): void {
    this._value.set(value);
  }

  /**
   * Gets the current input value
   * @returns Trimmed value or undefined if empty
   */
  getValue(): string {
    return this._value().trim();
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
   * Sets the disabled state
   * @param state - Whether to disable the input
   */
  setDisabled(state: boolean) {
    if (this.input?.nativeElement) {
      this.input.nativeElement.disabled = state;
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
   * Toggles password visibility
   * @param event - Click event
   */
  togglePassword(event: Event): void {
    event.preventDefault();
    this._showPassword() 
      ? this._showPassword.set(false)
      : this._showPassword.set(true)
  }

  /**
   * Handles input change events
   * @param event - Input event
   */
  protected onInput(event: Event) {
    const value: any = (event.target as HTMLInputElement).value;
    const filteredValue = this.applyFilters(value);
    this._value.set(filteredValue);
    if (this.input?.nativeElement) {
      this.input.nativeElement.value = filteredValue;
    }
  }

  /**
   * Applies filters to input value based on component settings
   * @param value - Raw input value
   * @returns Filtered value
   */
  private applyFilters(value: string): string {
    let filteredValue = value;

    if (!this.allowSpaces()) {
      filteredValue = filteredValue.replace(/\s+/g, '');
    }

    return filteredValue;
  }

}
