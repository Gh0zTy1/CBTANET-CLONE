import { Component, computed, ElementRef, input, signal, ViewChild } from "@angular/core";
import { NgClass } from "@angular/common";
import { ZardDatePickerIconComponent } from "@/shared/components/base/date-picker-icon";
import { ZardTooltipImports } from "@/shared/components/base/tooltip";

/**
 * Date input component with floating label pattern.
 * Supports date picker dialog and manual input with mask.
 * 
 * @example
 * ```html
 * <input-date-outline
 *   placeHolder="Fecha de nacimiento"
 *   [minDate]="minDate"
 *   [maxDate]="maxDate"
 * />
 * ```
 */
@Component({
  selector: 'input-date-outline',
  templateUrl: './input-date-outline.component.html',
  imports: [
    NgClass,
    ZardTooltipImports,
    ZardDatePickerIconComponent
  ]
})
export class InputDateOutlineComponent {

  /** Reference to the native input element */
  @ViewChild('dateInput') private dateInput!: ElementRef<HTMLInputElement>;

  /** Current formatted date value */
  value = signal<string>('');
  /** Placeholder text displayed when input is empty */
  placeHolder = input.required<string>();
  /** Minimum selectable date */
  minDate = input<Date>(new Date(1970, 1, 1));
  /** Maximum selectable date */
  maxDate = input<Date>(new Date());
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
  /** Touched state */
  protected _isTouched = signal<boolean>(false);
  /** Focus state for label floating */
  protected _isFocused = signal<boolean>(false);
  /** Minimum length for complete date */
  protected _min = signal<string>('0')
  /** Maximum length for complete date */
  protected _max = signal<string>('10')


  /** Selected date as Date object */
  thevalue = signal<Date | null>(null);

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
   * @param date - Date to set (Date object or string)
   */
  setValue(date: Date | undefined): void {

    if (!date) {
      this.clearValue();
      return;
    }

    if (isNaN(date.getTime())) {
      console.error('InputDateOutline: La fecha no es válida ->', date);
      return;
    }

    const day = date.getDate().toString().padStart(2, '0');
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const year = date.getFullYear().toString();
    const formattedDate = `${day}/${month}/${year}`;

    this.value.set(formattedDate);
    this._value.set(formattedDate);

    if (this.dateInput) {
      this.dateInput.nativeElement.value = formattedDate;
    }
  }

  /**
   * Gets the current input value as Date object
   * @returns Date object or undefined if invalid/incomplete
   */
  getValue(): Date | null {
    const value = this._value();
    if (!value) return null;
    if (this.isValueUncomplete()) return null;

    const [day, month, year] = value.trim().split('/');
    if (!day || !month || !year) {
      return null;
    }
    const dayNum = Number(day);
    const monthNum = Number(month) - 1;
    const yearNum = Number(year);

    return new Date(yearNum, monthNum, dayNum);
  }

  /**
   * Checks if the date value is incomplete
   */
  isValueUncomplete(): boolean {
    const value: number = this._value().length;
    const max: number = parseInt(this._max());
    if (!value) return true;
    return value < max;
  }

  /**
   * Clears the input value
   */
  clearValue() {
    this.value.set('');
    if (this.dateInput?.nativeElement) {
      this.dateInput.nativeElement.value = ''
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
   * Sets the disabled state
   * @param state - Whether to disable the input
   */
  setDisabled(state: boolean) {
    if (this.dateInput?.nativeElement) {
      this.dateInput.nativeElement.disabled = state;
    }
  }

  /**
   * Handles input change events
   * @param event - Input event
   */
  protected onInput(event: Event) {
    const value: any = (event.target as HTMLInputElement).value;
    const filteredValue = this.filterValue(value);
    const finalValue = this.applyMask(filteredValue);
    this._value.set(finalValue);
    if (this.dateInput?.nativeElement) {
      this.dateInput.nativeElement.value = finalValue;
    }
  }

  /**
   * Applies date mask to input value (DD/MM/YYYY)
   * @param value - Raw numeric value
   * @returns Formatted date string
   */
  private applyMask(value: string): string {
    if (value.length <= 2) {
      return `${value}`;
    }

    if (value.length <= 4) {
      return `${value.slice(0, 2)}/${value.slice(2, 4)}`;
    }

    if (value.length <= 8) {
      return `${value.slice(0, 2)}/${value.slice(2, 4)}/${value.slice(4, 8)}`;
    }

    return "";
  }

  /**
   * Filters value to allow only digits
   * @param value - Raw input value
   * @returns Filtered numeric value
   */
  private filterValue(value: string): string {
    let filteredValue = value.replace(/\s/g, '');
    if (filteredValue === '') {
      return '';
    }
    filteredValue = filteredValue.replace(/[^0-9]/g, '');
    return filteredValue;
  }

  /**
   * Handles date change from date picker
   * @param date - Selected date
   */
  protected onDateSelected(date: Date | null) {
    if (date) {
      const dateObj = date as Date;
      const year = dateObj.getFullYear();
      const month = String(dateObj.getMonth() + 1).padStart(2, '0');
      const day = String(dateObj.getDate()).padStart(2, '0');
      const dateString = `${day}/${month}/${year}`;

      this._value.set(dateString);

      if (this.dateInput) {
        this.dateInput.nativeElement.value = dateString;
      }
    }
  }

}
