import { Component, ElementRef, input, signal, ViewChild, effect, computed } from "@angular/core";
import { NgClass } from "@angular/common";

/**
 * Text input component with floating label pattern.
 * Supports filtering, validation, and error states.
 * 
 * @example
 * ```html
 * <input-text-outline
 *   placeHolder="Nombre"
 *   min="0"
 *   max="50"
 * />
 * ```
 */ 
@Component({
  selector: 'input-text-outline',
  templateUrl: './input-text-outline.component.html',
  imports: [NgClass]
})
export class InputTextOutlineComponent {

  /** Reference to the native input element */
  @ViewChild('input') private input!: ElementRef<HTMLInputElement>;

  /** Placeholder text displayed when input is empty */
  placeHolder = input.required<string>();
  /** Minimum length for validation */
  min = input<string>('0');
  /** Maximum length allowed */
  max = input<string>('100');
  /** Whether to allow spaces in input */
  allowSpaces = input<boolean>(true);
  /** Input name attribute */
  name = input<string>('');
  /** Autocomplete attribute */
  autocomplete = input<string>('off');
  /** Disabled state */
  disabled = input<boolean>(false);  

  /** Current input value */
  protected value = signal<string>('');
  /** Error state */
  protected _isError = signal<boolean>(false);
  /** Error message to display */
  protected _errorMessage = signal<string>('');
  /** Focus state for label floating */
  protected _isFocused = signal<boolean>(false);

  /**
   * Determines if the label should float (when focused or has value)
   */
  protected _isLabelFloating = computed(() => {
    return this._isFocused() || this.value().length > 0;
  });

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
    this.value.set(value);
  }

  /**
   * Gets the current input value
   * @returns Trimmed value or undefined if empty
   */
  getValue(): string  {
    return this.value().trim();
  }

  /**
   * Clears the input value
   */
  clearValue() {
    this.value.set('');
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
   * Handles input change events
   * @param event - Input event
   */
  protected onInput(event: Event) {
    const val: any = (event.target as HTMLInputElement).value;
    const filteredValue = this.applyFilters(val);
    this.value.set(filteredValue);
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
