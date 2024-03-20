import {Component, ViewEncapsulation} from '@angular/core';
import {UserService} from "../../services/user.service";
import {ProfileModelDTO} from "../../models/profile.model";
import {ActivatedRoute} from "@angular/router";
import {UserReview} from "../../models/UserReview";
import {SimpleQuestion} from "../../../../shared/models/question";

@Component({
    selector: 'app-profile-other',
    templateUrl: './profile-other.component.html',
    styleUrl: './profile-other.component.scss',
    encapsulation: ViewEncapsulation.None
})
export class ProfileOtherComponent {
    userId: number;

    profile: ProfileModelDTO;
    reviews: Array<UserReview>;
    questions: Array<SimpleQuestion>;

    constructor(private userService: UserService, private route: ActivatedRoute) {
    }

    ngOnInit(): void {
        this.userId = this.route.snapshot.params['id'];
        this.loadProfile();
        this.loadReviews();
        this.loadQuestions();
    }

    loadProfile() {
        this.userService.getUserInfo(this.userId).subscribe(next => this.profile = next);
    }

    loadReviews() {
        this.userService.getUserReviews(this.userId).subscribe(next => this.reviews = next);
    }

    loadQuestions() {
        this.userService.getUserQuestions(this.userId).subscribe(next => this.questions = next);
    }
}
