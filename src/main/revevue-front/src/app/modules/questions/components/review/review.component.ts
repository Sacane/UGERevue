import { Component, Input, OnInit } from '@angular/core';
import { Review } from "../../models/review";
import { UserService } from '../../../../shared/HttpServices';
import { MatDialog } from '@angular/material/dialog';
import { ConfirmDialogComponent } from '../../../../shared/components/confirm-dialog/confirm-dialog.component';
import { catchError, concat, of, switchMap } from 'rxjs';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ReviewService } from '../../../../shared';

@Component({
    selector: 'app-review',
    templateUrl: './review.component.html',
    styleUrl: './review.component.scss'
})
export class ReviewComponent implements OnInit {
    @Input() review: Review = {
        id: "", author: "", creationDate: new Date(0), content: "", downvotes: 0, reviews: [], upvotes: 0
    };
    canDelete: boolean = false;
    deleting: boolean = false;

    constructor(private userService: UserService, private reviewService: ReviewService, private router: Router, private snackBar: MatSnackBar, protected dialog: MatDialog) { }

    ngOnInit(): void {
        this.canDelete = this.userService.getLogin() === this.review.author;
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
                        this.reviewService.deleteReview("").pipe(
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
            console.log(response);
            if (response.deleting) {
                this.deleting = true;
            }
            else if (!response.error) {
                this.snackBar.open('La review a été suprimée', 'OK');
            }
            else {
                this.deleting = false;
            }
        });
    }
}
