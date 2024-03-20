import {Component, ViewEncapsulation, inject} from '@angular/core';
import {UserService} from "../../services/user.service";

@Component({
  selector: 'app-profile-other',
  templateUrl: './profile-other.component.html',
  styleUrl: './profile-other.component.scss',
    encapsulation: ViewEncapsulation.None
})
export class ProfileOtherComponent {
    userService = inject(UserService)


}
