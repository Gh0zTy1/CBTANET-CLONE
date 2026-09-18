import { Component, Output, EventEmitter, signal } from '@angular/core';
import { AppButtonComponent } from '../button/app-button.component';

@Component({
    selector: 'loading-screen-primary',
    templateUrl: 'loading-screen-primary.component.html',
    styleUrl: 'loading-screen-primary.component.css',
    imports: [AppButtonComponent] 
})
export class LoadingScreenPrimary {

    @Output() returnAction = new EventEmitter<Event>();

    protected _isError = signal<boolean>(false)
    protected _errorMessage = signal<string>('');

    isError(state: boolean) {
        this._isError.set(state);
    }

    setErrorMessage(message: string) {
        this._errorMessage.set(message);
    }

    onReturnClick(event: Event) {
        this.returnAction.emit(event);
    }

}