import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Review} from "../../models/review";
import {LoginService} from '../../../../shared/HttpServices';
import {MatDialog} from '@angular/material/dialog';
import {ConfirmDialogComponent} from '../../../../shared/components/confirm-dialog/confirm-dialog.component';
import {catchError, concat, of, switchMap} from 'rxjs';
import {ReviewService} from '../../../../shared';
import {MatSnackBar} from '@angular/material/snack-bar';
import {Role} from '../../models/role.model';
import {Router} from '@angular/router';

import 'prismjs'; // Import Prism.js
import 'prismjs/components/prism-java.js'; // Import the Java language syntax for Prism.js
@Component({
    selector: 'app-review',
    templateUrl: './review.component.html',
    styleUrl: './review.component.scss'
})
export class ReviewComponent implements OnInit {
    @Input() review: Review = {
        id: "", author: "", creationDate: '', content: "", downvotes: 0, reviews: [], upvotes: 0
    };
    @Output() onDelete: EventEmitter<void> = new EventEmitter();

    canDelete: boolean = false;
    deleting: boolean = false;

    codeTest = "public record Point(int x, int y){\n}"

    constructor(private userService: LoginService, private reviewService: ReviewService, private router: Router, private snackBar: MatSnackBar, protected dialog: MatDialog) { }

    ngOnInit(): void {
        this.canDelete = this.review.author === this.userService.getLogin() || this.userService.getRole() === Role.ADMIN;
    }

    deleteReview(): void {
        this.dialog.open(ConfirmDialogComponent, {
            data: {
                title: 'Delete a review',
                message: 'Confirm that you want to delete this review ?'
            },
            disableClose: true
        }).afterClosed().pipe(
            switchMap(confirm => {
                if (confirm) {
                    return concat(
                        of({ deleting: true }),
                        this.reviewService.deleteReview(this.review.id).pipe(
                            catchError(err => {
                                console.log(err);
                                return of({ error: err });
                            })
                        )
                    );
                }

                return of();
            })
        ).subscribe(response => {
            if (response && response.deleting) {
                this.deleting = true;
            }
            else if (response && response.error) {
                this.deleting = false;
                this.snackBar.open('Error occurs while deleting the review', 'OK');
            }
            else {
                this.onDelete.emit();
                this.snackBar.open('The review have been deleted', 'OK');
            }
        });
    }

    detailsReview(): void {
        this.router.navigateByUrl(`/reviews/${this.review.id}`).then();
    }
}
