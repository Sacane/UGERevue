import { Injectable } from '@angular/core';
import {UserFollowInfo} from "./models-in";

@Injectable({
    providedIn: 'root'
})
export class FakeUserInfoService {
    getALotFakeUserInfos(): UserFollowInfo[] {
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
            { username: 'Kylian', isFollowing: true},
            { username: 'Kylian', isFollowing: true},
            { username: 'Kylian', isFollowing: true},
            { username: 'Kylian', isFollowing: true},
            { username: 'Kylian', isFollowing: true},
            { username: 'Yohann', isFollowing: true},
            { username: 'Yohann', isFollowing: true},
            { username: 'Yohann', isFollowing: true},
            { username: 'Yohann', isFollowing: true},
            { username: 'Yohann', isFollowing: true},
        ];
    }
    getFewFakeUserInfos(): UserFollowInfo[] {
        return [
            { username: 'Mathis', isFollowing: true},
            { username: 'Johan', isFollowing: true},
            { username: 'Yohann', isFollowing: true},
            { username: 'Quentin', isFollowing: true},
            { username: 'Clement', isFollowing: true},
        ];
    }
}
