import { Component, Input, ViewEncapsulation } from '@angular/core';
import { UserFollowing } from '../../../../profile/models/UserFollowing';

@Component({
    selector: 'app-profil-users-content',
    templateUrl: './profil-users-content.component.html',
    styleUrl: './profil-users-content.component.scss',
    encapsulation: ViewEncapsulation.None
})
export class ProfilUsersContentComponent {
    @Input({required: true}) userFollowing: UserFollowing[];
}
