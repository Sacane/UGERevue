import { Component, EventEmitter, Input, Output, ViewEncapsulation } from '@angular/core';
import { UserInfo } from '../../../models/UserInfo';
import { UserPasswordUpdate } from '../../../models/UserPasswordUpdate';
import { FormControl, Validators } from '@angular/forms';

@Component({
    selector: 'app-profil-infos-content',
    templateUrl: './profil-infos-content.component.html',
    styleUrl: './profil-infos-content.component.scss',
    encapsulation: ViewEncapsulation.None
})
export class ProfilInfosContentComponent {
    @Input({ required: true }) userInfo: UserInfo;

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

        if (this.oldPasswordControl.valid && this.newPasswordControl.valid && oldPassword !== null && newPassword !== null) {
            this.oldPasswordControl.setValue('');
            this.newPasswordControl.setValue('');
            this.passwordChanged.emit({ oldPassword, newPassword });
        }
    }
}
