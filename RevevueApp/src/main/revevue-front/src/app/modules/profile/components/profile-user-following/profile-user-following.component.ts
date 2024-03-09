import {Component} from '@angular/core';
import {Observable} from "rxjs";
import {UserService} from "../../services/user.service";
import {AsyncPipe, NgIf} from "@angular/common";
import {
    ProfileUserQuestionsContentComponent
} from "../pofile-user-questions/profile-user-questions-content/profile-user-questions-content.component";
import {UserFollowing} from "../../models/UserFollowing";
import {
    ProfileUserFollowingContentComponent
} from "./profile-user-following-content/profile-user-following-content.component";

@Component({
    selector: 'app-profile-user-following',
    standalone: true,
    imports: [
        AsyncPipe,
        NgIf,
        ProfileUserQuestionsContentComponent,
        ProfileUserFollowingContentComponent
    ],
    templateUrl: './profile-user-following.component.html',
    styleUrl: './profile-user-following.component.scss'
})
export class ProfileUserFollowingComponent {
    userFollowing$!: Observable<Array<UserFollowing>>;

    constructor(private userService: UserService) {
    }

    ngOnInit() {
        this.userFollowing$ = this.userService.getCurrentUserFollowing();
    }
}
