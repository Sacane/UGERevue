import {AfterViewInit, Component, inject, OnInit, ViewChild, ViewEncapsulation} from '@angular/core';
import {UserFollowInfo} from "../../shared/models-in";
import {MatPaginator} from "@angular/material/paginator";
import {LoginService} from "../../shared/HttpServices";
import {Router} from "@angular/router";

@Component({
    selector: 'app-users',
    templateUrl: './users.component.html',
    styleUrl: './users.component.scss',
    encapsulation: ViewEncapsulation.None
})
export class UsersComponent implements OnInit, AfterViewInit {
    userList: UserFollowInfo[] = [];
    usersFiltered: UserFollowInfo[] = [];
    @ViewChild(MatPaginator) paginator!: MatPaginator;
    readonly pageSize = 9;
    private readonly userService: LoginService = inject(LoginService)
    private currentPage = 0;
    private route = inject(Router)

    ngAfterViewInit() {
        this.paginator.pageSize = this.pageSize;
    }

    ngOnInit(): void {
        /*this.userService.getALotFakeUserInfos().subscribe(  Fake data
            (data: UserFollowInfo[]) => this.userList = data.slice()
        );*/
        this.userService.getAllRegisteredUsers().subscribe(
            (data: UserFollowInfo[]) => {
                this.userList = data.slice();
                this.usersFiltered = this.userList.slice();
            }
        );
    }

    filter(event: Event): void {
        const query = (event.target as HTMLInputElement).value;
        if (query.length == 0) {
            this.usersFiltered = this.userList;
        } else {
            this.usersFiltered = this.usersFiltered.filter(infos => infos.username.toLowerCase().includes(query.toLowerCase()));
        }
        this.paginator.firstPage();
    }

    getPagedUserFollowedInfos(): UserFollowInfo[] {
        const startIndex = this.currentPage * this.pageSize;
        return this.usersFiltered.slice(startIndex, startIndex + this.pageSize);
    }

    onPageChange(event: any): void {
        this.currentPage = event.pageIndex;
    }

    onFollow(user: UserFollowInfo): void {
        user.isFollowing = true;
        this.userService.follow(user.id).subscribe();
    }

    onUnfollow(user: UserFollowInfo): void {
        user.isFollowing = false;
        this.userService.unfollow(user.id).subscribe();
    }

    isLogged() {
        return this.userService.isLogin();
    }

    gotoProfileUser(user: UserFollowInfo) {
        this.route.navigateByUrl('/profile/' + user.id).then()
    }
}
