import { Injectable } from '@angular/core';
import {UserFollowInfo} from "./models-in";

@Injectable({
    providedIn: 'root'
})
export class FakeUserInfoService {
    getFakeUserInfos(): UserFollowInfo[] {
        return [
            { username: 'Mathis', isFollowing: true},
            { username: 'Johan', isFollowing: true},
            { username: 'Yohann', isFollowing: true},
            { username: 'Quentin', isFollowing: true},
            { username: 'Clement', isFollowing: true},
            { username: 'Arnaud', isFollowing: true},
            { username: 'Remi', isFollowing: true},
            { username: 'Drake', isFollowing: true},
            { username: 'Kylian', isFollowing: true},
        ];
    }
}
