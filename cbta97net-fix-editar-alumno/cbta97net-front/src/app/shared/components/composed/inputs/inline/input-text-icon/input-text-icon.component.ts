import { Component, input, signal } from "@angular/core";
import { ZardInputDirective } from '@/shared/components/base/input/input.directive';
import { ZardInputGroupComponent } from '@/shared/components/base/input-group/input-group.component';

@Component({
  selector: 'input-text-icon',
  templateUrl: './input-text-icon.component.html',
  imports: [
    ZardInputDirective, 
    ZardInputGroupComponent
  ]
})
export class InputTextIconComponent {
  icon = input.required<string>();
  label = input<string>('');
  placeHolder = input<string>('');
  minLength = input<string>('0');
  maxLength = input<string>('100');
  name = input<string>('');
  autocomplete = input<string>('off');

  protected _value = signal<string>('');
  protected _isError = signal<boolean>(false);
  protected _errorMessage = signal<string>('');

  setValue(value: string): void {
    this._value.set(value);
  }

  getValue(): string {
    return this._value();
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
    this._value.set(value);
  }
}
