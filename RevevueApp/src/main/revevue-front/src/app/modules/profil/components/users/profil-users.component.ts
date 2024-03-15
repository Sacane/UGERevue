import { Component, ViewEncapsulation } from '@angular/core';
import { Observable } from 'rxjs';
import { UserFollowing } from '../../models/UserFollowing';
import { UserService } from '../../services/user.service';

@Component({
    selector: 'app-profil-users',
    templateUrl: './profil-users.component.html',
    styleUrl: './profil-users.component.scss',
    encapsulation: ViewEncapsulation.None
})
export class ProfilUsersComponent {
    userFollowing$: Observable<UserFollowing[]>;

    constructor(private userService: UserService) { }

    ngOnInit() {
        this.userFollowing$ = this.userService.getCurrentUserFollowing();
    }
}
