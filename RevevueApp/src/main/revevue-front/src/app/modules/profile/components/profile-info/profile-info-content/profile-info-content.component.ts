import {Component, EventEmitter, Input, Output} from '@angular/core';
import {UserInfo} from "../../../models/UserInfo";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {MatButton} from "@angular/material/button";
import {RolePipe} from "../../../pipes/role.pipe";
import {FormControl, ReactiveFormsModule, Validators} from "@angular/forms";

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
    usernameControl = new FormControl('',
        [Validators.minLength(3), Validators.maxLength(20)]);
    editingUsername = false;

    editName() {
        this.usernameControl.setValue(this.userInfo.username);
        this.editingUsername = true;
    }

    saveName() {
        const newUsername = this.usernameControl.value;
        if (newUsername != null && this.usernameControl.valid) {
            this.userInfo.username = newUsername;
            this.editingUsername = false;
            this.usernameChanged.emit(newUsername);
        }
    }
}
