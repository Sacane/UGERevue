import {Component} from '@angular/core';
import {MatTab, MatTabContent, MatTabGroup} from "@angular/material/tabs";
import {ProfileInfoComponent} from "../profile-info/profile-info.component";
import {ProfileUserQuestionsComponent} from "../pofile-user-questions/profile-user-questions.component";
import {ProfileUserFollowingComponent} from "../profile-user-following/profile-user-following.component";

@Component({
    selector: 'app-profile',
    standalone: true,
    imports: [
        MatTabGroup,
        MatTab,
        MatTabContent,
        ProfileInfoComponent,
        ProfileUserQuestionsComponent,
        ProfileUserFollowingComponent
    ],
    templateUrl: './profile.component.html',
    styleUrl: './profile.component.scss'
})
export class ProfileComponent {

}
