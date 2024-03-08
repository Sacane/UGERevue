import {Component, EventEmitter, Input, Output} from '@angular/core';
import {UserInfo} from "../../../models/UserInfo";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {MatButton} from "@angular/material/button";
import {RolePipe} from "../../../pipes/role.pipe";
import {FormControl, ReactiveFormsModule, Validators} from "@angular/forms";
import {UserPasswordUpdate} from "../../../models/UserPasswordUpdate"

@Component({
    selector: 'app-profile-info-content',
    standalone: true,
    imports: [
        MatFormField,
        MatLabel,
        MatInput,
        MatButton,
        RolePipe,
        ReactiveFormsModule
    ],
    templateUrl: './profile-info-content.component.html',
    styleUrl: './profile-info-content.component.scss'
})
export class ProfileInfoContentComponent {
    @Input({required: true}) userInfo!: UserInfo;
    @Output() usernameChanged = new EventEmitter<string>();
    @Output() passwordChanged = new EventEmitter<UserPasswordUpdate>();
    usernameControl = new FormControl('',
        [Validators.minLength(3), Validators.maxLength(20)]);
    editingUsername = false;

    oldPasswordControl = new FormControl('',
        [Validators.minLength(3), Validators.maxLength(20)]);
    newPasswordControl = new FormControl('',
        [Validators.minLength(3), Validators.maxLength(20)]);

    editName() {
        this.usernameControl.setValue(this.userInfo.username);
        this.editingUsername = true;
    }

    saveName() {
        const newUsername = this.usernameControl.value;
        if (newUsername != null && this.usernameControl.valid) {
            if (newUsername !== this.userInfo.username) {
                this.userInfo.username = newUsername;
                this.usernameChanged.emit(newUsername);
            }
            this.editingUsername = false;
        }
    }

    savePassword() {
        const oldPassword = this.oldPasswordControl.value;
        const newPassword = this.newPasswordControl.value;
        if (this.oldPasswordControl.valid && this.newPasswordControl.valid
            && oldPassword !== null && newPassword !== null) {
            this.oldPasswordControl.setValue('');
            this.newPasswordControl.setValue('');
            this.passwordChanged.emit({oldPassword, newPassword});
        }
    }
}
