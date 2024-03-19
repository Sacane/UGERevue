import {AfterViewInit, Component, EventEmitter, Input, Output, ViewEncapsulation} from '@angular/core';
import {UserInfo} from '../../../models/UserInfo';
import {UserPasswordUpdate} from '../../../models/UserPasswordUpdate';
import {FormControl, Validators} from '@angular/forms';
import { UserService } from '../../../services/user.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
    selector: 'app-profil-infos-content',
    templateUrl: './profil-infos-content.component.html',
    styleUrl: './profil-infos-content.component.scss',
    encapsulation: ViewEncapsulation.None
})
export class ProfilInfosContentComponent implements AfterViewInit {
    @Input({ required: true }) userInfo: UserInfo;

    @Output() passwordChanged = new EventEmitter<UserPasswordUpdate>();

    editingUsername = false;

    oldUsername: string;
    usernameControl = new FormControl('', [Validators.minLength(3), Validators.maxLength(20)]);
    oldPasswordControl = new FormControl('', [Validators.minLength(3), Validators.maxLength(20)]);
    newPasswordControl = new FormControl('', [Validators.minLength(3), Validators.maxLength(20)]);

    constructor(private userService: UserService, private snackBar: MatSnackBar) { }

    ngAfterViewInit(): void {
        this.oldUsername = this.userInfo.username;
    }

    editName() {
        this.usernameControl.setValue(this.userInfo.username);
        this.editingUsername = true;
    }

    saveName() {
        const newUsername = this.usernameControl.value;
        if (newUsername != null && this.usernameControl.valid) {
            if (newUsername !== this.userInfo.username) {
                this.userInfo.username = newUsername;

                this.userService.changeCurrentUserInfo({ username: newUsername }).subscribe({
                    next: response => {
                        this.oldUsername = newUsername
                        this.snackBar.open('Nom d\'utilisateur mis à jour', 'Fermer', { duration: 3000 });
                    }, error: error => {
                        this.userInfo.username = this.oldUsername;
                        this.snackBar.open('Erreur lors de mise à jour du nom d\'utilisateur', 'Fermer', { duration: 3000 });
                    }
                });
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
