import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Review } from "../../models/review";
import { UserService } from '../../../../shared/HttpServices';
import { MatDialog } from '@angular/material/dialog';
import { ConfirmDialogComponent } from '../../../../shared/components/confirm-dialog/confirm-dialog.component';
import { catchError, concat, of, switchMap } from 'rxjs';
import { ReviewService } from '../../../../shared';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Role } from '../../models/role.model';


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

    constructor(private userService: UserService, private reviewService: ReviewService, private snackBar: MatSnackBar, protected dialog: MatDialog) { }

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
}
