import { Component, ViewEncapsulation } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

@Component({
    selector: 'app-signup',
    templateUrl: './signup.component.html',
    styleUrl: './signup.component.scss',
    encapsulation: ViewEncapsulation.None
})
export class SignupComponent {
    signupForm = new FormGroup({
        displayName: new FormControl('', [Validators.required]),
        email: new FormControl('', [Validators.required]),
        password: new FormControl('', [Validators.required])
    });

    signup(): void {
        if (this.signupForm.valid) {
            console.log("SIGNUP");
        }
    }
}
