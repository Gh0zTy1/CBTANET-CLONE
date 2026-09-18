import { Component, computed, ElementRef, input, signal, ViewChild } from "@angular/core";
import { NgClass } from "@angular/common";

/**
 * Insurance/police number input component with floating label pattern.
 * Supports formatted insurance number with mask XXX/XXX/XXXX/XX.
 * 
 * @example
 * ```html
 * <input-insurance-outline
 *   placeHolder="Póliza de seguro"
 *   min="0"
 *   max="12"
 * />
 * ```
 */
@Component({
  selector: 'input-insurance-outline',
  templateUrl: './input-insurance-outline.component.html',
  imports: [NgClass]
})
export class InputInsuranceOutlineComponent {

  /** Reference to the native input element */
  @ViewChild('input') private input!: ElementRef<HTMLInputElement>;

  /** Placeholder text displayed when input is empty */
  placeHolder = input.required<string>();
  /** Minimum length for validation */
  min = input<string>('0');
  /** Maximum length allowed */
  max = input<string>('100');
  /** Expected length of insurance number */
  trueLenght = input<string>('0');
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
   * @param value - Insurance number to set
   */
  setValue(value: number): void {
    this._value.set(this.applyInsuranceFormat(String(value)));
    if (this.input?.nativeElement) {
      this.input.nativeElement.value = this._value();
    }
  }

  /**
   * Gets the current input value
   * @returns Number value or undefined if invalid
   */
  getValue(): number | null{
    const raw = this._value().trim().replace(/\//g, '');
    const value = Number(raw);
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
    if(input.value.length > 12) return;
    let filteredValue = this.applyInsuranceFormat(input.value);
    this._value.set(filteredValue);
    input.value = filteredValue;
  }

  /**
   * Applies insurance format mask (XXX/XXX/XXXX/XX)
   * @param value - Raw input value
   * @returns Formatted insurance number
   */
  private applyInsuranceFormat(value: string): string {
    let filteredValue = value.replace(/\s/g, '');
    filteredValue = filteredValue.replace(/[^0-9]/g, '');
    if (filteredValue.length <= 3) { return `${filteredValue}`;}
    if (filteredValue.length <= 6) { return `${filteredValue.slice(0, 3)}/${filteredValue.slice(3, 6)}`}
    if (filteredValue.length <= 10) { return `${filteredValue.slice(0, 3)}/${filteredValue.slice(3, 6)}/${filteredValue.slice(6, 10)}`}
    return filteredValue;
  }

}
