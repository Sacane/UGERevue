import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { Observable } from 'rxjs';
import { UserInfo } from '../../models/UserInfo';
import { UserService } from '../../services/user.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { UserPasswordUpdate } from '../../models/UserPasswordUpdate';

@Component({
    selector: 'app-profil-infos',
    templateUrl: './profil-infos.component.html',
    styleUrl: './profil-infos.component.scss',
    encapsulation: ViewEncapsulation.None
})
export class ProfilInfosComponent implements OnInit {
    userInfo$: Observable<UserInfo>;

    constructor(private userService: UserService, private snackBar: MatSnackBar) { }

    ngOnInit() {
        this.userInfo$ = this.userService.getCurrentUserInfo();
    }

    saveUsername(username: string) {
        this.userService.changeCurrentUserInfo({ username: username }).subscribe({
            next: response => {
                this.snackBar.open('Nom d\'utilisateur mis à jour', 'Fermer', { duration: 3000 });
            }, error: error => {
                this.snackBar.open('Erreur lors de mise à jour du nom d\'utilisateur', 'Fermer', { duration: 3000 })
            }
        });
    }

    savePassword(userPasswordUpdate: UserPasswordUpdate) {
        console.log(userPasswordUpdate);
        this.userService.changeCurrentUserPassword(userPasswordUpdate).subscribe({
            next: response => {
                this.snackBar.open('Mot de passe mis à jour', 'Fermer', { duration: 3000 });
            }, error: error => {
                this.snackBar.open('Erreur lors de mise à jour du mot de passe', 'Fermer', { duration: 3000 })
            }
        });
    }
}
