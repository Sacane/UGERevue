import {Component, Input, OnInit} from '@angular/core';
import {UserFollowInfo} from "../../shared/models-in";
import {FakeUserInfoService} from "../../shared/FakeUserFollowInfoService";

@Component({
    selector: 'app-users',
    templateUrl: './users.component.html',
    styleUrl: './users.component.scss'
})
export class UsersComponent implements OnInit {
    @Input() userFollowedInfos: UserFollowInfo[] = [];
    usersFiltered: UserFollowInfo[] = [];
    constructor(private fakesService: FakeUserInfoService){}

    ngOnInit() {
        //this.userFollowedInfos = this.userFollowedInfos.slice();
        //Using fake data
        this.userFollowedInfos = this.fakesService.getFakeUserInfos();
    }

    filter(event: Event) {
        const query = (event.target as HTMLInputElement).value;
        this.usersFiltered = this.userFollowedInfos.filter(infos => infos.username.toLowerCase().includes(query.toLowerCase()));
    }

}
