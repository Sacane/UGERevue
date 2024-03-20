import {Component, inject, ViewEncapsulation} from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {LoginService} from "../../shared/HttpServices";
import {Router} from "@angular/router";
import {ToastrService} from "ngx-toastr";

@Component({
    selector: 'app-signup',
    templateUrl: './signup.component.html',
    styleUrl: './signup.component.scss',
    encapsulation: ViewEncapsulation.None
})
export class SignupComponent{

    signupForm = new FormGroup({
        displayName: new FormControl('', [Validators.required]),
        email: new FormControl('', [Validators.required]),
        password: new FormControl('', [Validators.required]),
        accountName: new FormControl('', [Validators.required])
    });
    userService = inject(LoginService)
    private router = inject(Router)
    private toastService = inject(ToastrService)

    signup(): void {
        if (this.signupForm.valid) {
            this.userService.registerUser({
                username: this.signupForm.value.displayName as string,
                login: this.signupForm.value.accountName as string,
                password: this.signupForm.value.password as string,
                email: this.signupForm.value.email as string
            }, err => {
                this.toastService.error(err.error.message)
            }).subscribe(() => {
                this.router.navigateByUrl("/home").then();
            })
        }
    }
}
