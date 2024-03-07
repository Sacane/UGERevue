import {Component} from '@angular/core';
import {UserService} from "../../services/user.service";
import {UserInfo} from "../../models/UserInfo";
import {Observable} from "rxjs";
import {AsyncPipe, NgIf} from "@angular/common";
import {ProfileInfoContentComponent} from "./profile-info-content/profile-info-content.component";
import {MatSnackBar} from "@angular/material/snack-bar";

@Component({
    selector: 'app-profile-info',
    standalone: true,
    imports: [
        AsyncPipe,
        NgIf,
        ProfileInfoContentComponent
    ],
    templateUrl: './profile-info.component.html',
    styleUrl: './profile-info.component.scss'
})
export class ProfileInfoComponent {
    userInfo$!: Observable<UserInfo>;

    constructor(private userService: UserService, private _snackBar: MatSnackBar) {
    }

    ngOnInit() {
        this.userInfo$ = this.userService.getCurrentUserInfo();
    }

    saveUsername(username: string) {
        this.userService.changeCurrentUserInfo({username: username}).subscribe({
            next: response => {
                this._snackBar.open('Nom d\'utilisateur mis à jour', 'Fermer', {duration: 3000});
            }, error: error => {
                this._snackBar.open('Erreur lors de mise à jour du nom d\'utilisateur', 'Fermer', {duration: 3000})
            }
        })
    }
}
