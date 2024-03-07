import {Component} from '@angular/core';
import {UserService} from "../../services/user.service";
import {UserInfo} from "../../models/UserInfo";
import {Observable} from "rxjs";
import {AsyncPipe, NgIf} from "@angular/common";
import {ProfileInfoContentComponent} from "./profile-info-content/profile-info-content.component";

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

    constructor(private userService: UserService) {
    }

    ngOnInit(): void {
        this.userInfo$ = this.userService.getCurrentUserInfo();
    }
}
