import {Component} from '@angular/core';
import {MatTab, MatTabContent, MatTabGroup} from "@angular/material/tabs";
import {ProfileInfoComponent} from "../info/profile-info.component";

@Component({
    selector: 'app-profile',
    standalone: true,
    imports: [
        MatTabGroup,
        MatTab,
        MatTabContent,
        ProfileInfoComponent
    ],
    templateUrl: './profile.component.html',
    styleUrl: './profile.component.scss'
})
export class ProfileComponent {

}
