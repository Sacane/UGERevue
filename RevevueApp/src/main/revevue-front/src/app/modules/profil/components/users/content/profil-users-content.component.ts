import {Component, Input, ViewEncapsulation} from '@angular/core';
import {UserFollowing} from '../../../models/UserFollowing';
import {Router} from "@angular/router";

@Component({
    selector: 'app-profil-users-content',
    templateUrl: './profil-users-content.component.html',
    styleUrl: './profil-users-content.component.scss',
    encapsulation: ViewEncapsulation.None
})
export class ProfilUsersContentComponent {
    @Input({required: true}) userFollowing: UserFollowing[];

    constructor(private router: Router) {
    }

    gotoProfileUser(user: UserFollowing) {
        this.router.navigateByUrl('/profile/' + user.id).then()
    }
}
