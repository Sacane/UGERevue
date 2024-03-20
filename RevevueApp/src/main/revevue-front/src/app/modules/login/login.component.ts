import {Component, inject, ViewEncapsulation} from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {LoginService} from "../../shared/HttpServices";
import {Router} from "@angular/router";
import {ToastrService} from "ngx-toastr";

@Component({
    selector: 'app-login',
    templateUrl: './login.component.html',
    styleUrl: './login.component.scss',
    encapsulation: ViewEncapsulation.None
})
export class LoginComponent {

    private toast = inject(ToastrService)
    loginForm = new FormGroup({
        accountName: new FormControl('', [Validators.required]),
        password: new FormControl('', [Validators.required])
    });

    constructor(private loginService: LoginService, private router: Router) {
    }

    login(): void {
        if (this.loginForm.valid) {
            this.loginService.login({
                login: this.loginForm.value.accountName as string,
                password: this.loginForm.value.password as string
            }, err => this.toast.error(err.error.message)).subscribe(result => {
                this.router.navigateByUrl("/home").then()
            })
        }
    }
}
