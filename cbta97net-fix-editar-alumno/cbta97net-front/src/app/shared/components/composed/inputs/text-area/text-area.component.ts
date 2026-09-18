import { Component, input, signal,} from "@angular/core";
import { ZardInputDirective } from '@/shared/components/base/input/input.directive';


@Component({
  selector: 'text-area',
  template: `
    <textarea
        z-input
        rows="8" 
        cols="12"
        placeholder="{{ placeHolder() }}"
        [value]="_value()"
        [attr.minlength]="min()"
        [attr.maxlength]="max()"
        (input)="onInput($event)"
        class="resize-none"
    ></textarea>
  `,
  imports: [
    ZardInputDirective
  ]
})
/**
 * InputUploadComponent 
 * 
 * Reusable component that provides an inline file selector that allows file selection and 
 * drag and drop features.
 */
export class TextAreaComponent {
  
  placeHolder = input.required<string>();
  min = input<number>(0);
  max = input<number>(0);

  protected _value = signal<string>('');
  protected _isError = signal<boolean>(false);
  protected _isDisabled = signal<boolean>(false);
  protected _errorMessage = signal<string>('');
  
  /**
   * Sets the internal value of the input.
   * @param value The string value to set.
   */
  setValue(value: any): void{
    this._value.set(value);
  }

  /**
   * Retrieves the current value of the input.
   * @returns Current value of the component.
   */
  getValue(): string{
    return this._value().trim();
  }

  /**
   * Resets the current value of the component.
   */
  clearValue(){
    this._value.set(''); 
  }

  /**
   * Displays an error message.
   * @param errorMessage The message to display.
   */
  showError(errorMessage: string): void {
    this._isError.set(true);
    this._errorMessage.set(errorMessage);
  }

  /**
   * Hides any displayed error message.
   */
  hideError(): void {
    this._isError.set(false);
    this._errorMessage.set('');
  }

  /**
   * Sets the disabled state of the input.
   * @param state True to disable, false to enable.
   */
  setDisabled(state: boolean){
    this._isDisabled.set(state);
  }

  /**
   * Handles the input event, applying filters to the value.
   * @param event The input event.
   */
  protected onInput(event: Event){
    const value: any = (event.target as HTMLTextAreaElement).value;
    this._value.set(value);
  }
  
}