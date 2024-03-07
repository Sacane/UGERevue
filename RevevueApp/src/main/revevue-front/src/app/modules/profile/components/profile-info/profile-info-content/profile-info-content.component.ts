import {Component, Input} from '@angular/core';
import {UserInfo} from "../../../models/UserInfo";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {MatButton} from "@angular/material/button";
import {RolePipe} from "../../../pipes/role.pipe";

@Component({
    selector: 'app-profile-info-content',
    standalone: true,
    imports: [
        MatFormField,
        MatLabel,
        MatInput,
        MatButton,
        RolePipe
    ],
    templateUrl: './profile-info-content.component.html',
    styleUrl: './profile-info-content.component.scss'
})
export class ProfileInfoContentComponent {
    @Input() userInfo!: UserInfo;

}
