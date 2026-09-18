import { Component, input, signal } from "@angular/core";
import { ZardInputDirective } from '@/shared/components/base/input/input.directive';
import { ZardInputGroupComponent } from '@/shared/components/base/input-group/input-group.component';
import { NgClass } from "@angular/common";

@Component({
  selector: 'input-password-icon-outline',
  templateUrl: './input-password-icon.component.html',
  imports: [
    ZardInputDirective, 
    ZardInputGroupComponent,
    NgClass
  ]
})
export class InputPasswordIconComponent {
  label = input<string>('');
  placeHolder = input<string>('');
  minLength = input<string>('0');
  maxLength = input<string>('100');
  name = input<string>('');
  autocomplete = input<string>('off');

  protected _value = signal<string>('');
  protected _isError = signal<boolean>(false);
  protected _showPassword = signal<boolean>(false);
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

  togglePassword(event: Event): void {
    event.preventDefault();
    this._showPassword.set(!this._showPassword());
  }

  protected onInput(event: Event): void {
    const value: string = (event.target as HTMLInputElement).value;
    this._value.set(value);
  }
}
