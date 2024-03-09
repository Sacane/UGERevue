import {Component, Input} from '@angular/core';
import {UserFollowing} from "../../../models/UserFollowing";

@Component({
    selector: 'app-profile-user-following-content',
    standalone: true,
    imports: [],
    templateUrl: './profile-user-following-content.component.html',
    styleUrl: './profile-user-following-content.component.scss'
})
export class ProfileUserFollowingContentComponent {
    @Input({required: true}) userFollowing!: Array<UserFollowing>;

}
