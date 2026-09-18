import { Component, input, signal } from "@angular/core";
import { ZardInputDirective } from '@/shared/components/base/input/input.directive';

@Component({
  selector: 'input-number',
  templateUrl: './input-number.component.html',
  imports: [ZardInputDirective]
})
export class InputNumberComponent {
  label = input<string>('');
  placeHolder = input<string>('');
  minLength = input<string>('0');
  maxLength = input<string>('100');
  allowDecimals = input<boolean>(true);
  allownNegatives = input<boolean>(true);
  name = input<string>('');
  autocomplete = input<string>('off');

  protected _value = signal<string>('');
  protected _isError = signal<boolean>(false);
  protected _errorMessage = signal<string>('');

  setValue(value: number): void {
    this._value.set(String(value));
  }

  getValue(): number | undefined {
    const value = Number(this._value());
    return Number.isNaN(value) ? undefined : value;
  }

  showError(errorMessage: string): void {
    this._isError.set(true);
    this._errorMessage.set(errorMessage);
  }

  hideError(): void {
    this._isError.set(false);
    this._errorMessage.set('');
  }

  protected onInput(event: Event): void {
    const value: string = (event.target as HTMLInputElement).value;
    const filteredValue: string = this.filterValue(value);
    this._value.set(filteredValue);
    (event.target as HTMLInputElement).value = filteredValue;
  }

  private filterValue(value: string): string {
    let filteredValue = value.replace(/\s/g, '');
    if (filteredValue === '') return '';

    let isNegative = false;
    if (this.allownNegatives() && filteredValue.startsWith('-')) {
      isNegative = true;
      filteredValue = filteredValue.substring(1);
    }

    const decimalRegex = this.allowDecimals() ? /[^0-9.]/g : /[^0-9]/g;
    filteredValue = filteredValue.replace(decimalRegex, '');

    if (this.allowDecimals()) {
      const parts = filteredValue.split('.');
      if (parts.length > 2) filteredValue = parts[0] + '.' + parts.slice(1).join('');
      if (filteredValue.startsWith('.')) filteredValue = '0' + filteredValue;
    }

    if (isNegative && filteredValue !== '' && filteredValue !== '0') {
      filteredValue = '-' + filteredValue;
    }

    return filteredValue;
  }
}
