import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {UserFollowInfo} from "../../shared/models-in";
import {FakeUserInfoService} from "../../shared/FakeUserFollowInfoService";
import {MatPaginator} from "@angular/material/paginator";

@Component({
    selector: 'app-users',
    templateUrl: './users.component.html',
    styleUrl: './users.component.scss'
})
export class UsersComponent implements OnInit {
    @Input() inputDatas: UserFollowInfo[] = [];
    usersFiltered: UserFollowInfo[] = [];
    constructor(private fakesService: FakeUserInfoService){}

    @ViewChild(MatPaginator) paginator!: MatPaginator;

    readonly pageSize = 9;
    private currentPage = 0;

    ngOnInit(): void {
        //this.inputDatas = this.userFollowedInfos.slice();
        this.inputDatas = this.fakesService.getALotFakeUserInfos();//Using fake data
        this.usersFiltered = this.inputDatas;
        this.paginator.pageSize = this.pageSize;
    }

    filter(event: Event): void {
        const query = (event.target as HTMLInputElement).value;
        this.usersFiltered = this.inputDatas.filter(infos => infos.username.toLowerCase().includes(query.toLowerCase()));
        this.paginator.firstPage();
    }

    getPagedUserFollowedInfos(): UserFollowInfo[] {
        const startIndex = this.currentPage * this.pageSize;
        return this.usersFiltered.slice(startIndex, startIndex + this.pageSize);
    }

    onPageChange(event: any) : void {
        this.currentPage = event.pageIndex;
    }

    onFollow(user: UserFollowInfo): void {
        console.log('Followed:', user.username);
        user.isFollowing = true;
    }

    onUnfollow(user: UserFollowInfo): void {
        console.log('Unfollowed:', user.username);
        user.isFollowing = false;
    }
}
