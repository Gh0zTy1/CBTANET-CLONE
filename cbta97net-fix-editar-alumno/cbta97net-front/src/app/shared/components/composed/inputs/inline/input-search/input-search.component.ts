import { Component, ElementRef, input, output, signal, ViewChild } from "@angular/core";
import { ZardDividerComponent } from "@/shared/components/base/divider";

@Component({
  selector: 'input-search',
  templateUrl: './input-search.component.html',
  imports: [ ZardDividerComponent ]
})

export class InputSearch {

  @ViewChild('input') private input!: ElementRef<HTMLInputElement>;

  placeHolder = input.required<string>();
  min = input<string>('0');
  max = input<string>('100');
  allowSpaces = input<boolean>(true);
  name = input<string>('');
  autocomplete = input<string>('off');
  disabled = input<boolean>(false);  

  readonly inputChange = output<string>();

  value = signal<string>('');
  protected _isError = signal<boolean>(false);
  protected _errorMessage = signal<string>('');
  protected _isFocused = signal<boolean>(false);

  protected _isLabelFloating = (): boolean => {
    return this._isFocused() || this.value().length > 0;
  };

  onFocus(): void {
    this._isFocused.set(true);
  }

  onBlur(): void {
    this._isFocused.set(false);
  }

  setValue(value: string): void {
    this.value.set(value);
  }

  getValue(): string | undefined {
    const val: string = this.value() ?? undefined;
    if(this.value()) val.trim();
    return val;
  }

  clearValue() {
    this.value.set('');
    if (this.input?.nativeElement) {
      this.input.nativeElement.value = '';
    }
  }

  setDisabled(state: boolean) {
    if (this.input?.nativeElement) {
      this.input.nativeElement.disabled = state;
    }
  }

  getMax(): string {
    return this.max();
  }

  isUnderLenght() {
    const val: string = this.value();
    const max: number = parseInt(this.max());
    return val.length < max;
  }

  showError(errorMessage: string): void {
    this._isError.set(true);
    this._errorMessage.set(errorMessage);
  }

  hideError(): void {
    this._isError.set(false);
    this._errorMessage.set('');
  }

  protected onInput(event: Event) {
    const val: any = (event.target as HTMLInputElement).value;
    const filteredValue = this.applyFilters(val);
    this.value.set(filteredValue);
    if (this.input?.nativeElement) {
      this.input.nativeElement.value = filteredValue;
    }
    this.inputChange.emit(filteredValue);
  }

  private applyFilters(value: string): string {
    let filteredValue = value;

    if (!this.allowSpaces()) {
      filteredValue = filteredValue.replace(/\s+/g, '');
    }

    return filteredValue;
  }

}
