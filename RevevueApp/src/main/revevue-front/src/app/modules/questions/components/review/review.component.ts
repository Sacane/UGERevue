import {Component, EventEmitter, inject, Input, OnInit, Output} from '@angular/core';
import {Review} from "../../models/review";
import {LoginService} from '../../../../shared/HttpServices';
import {MatDialog} from '@angular/material/dialog';
import {ConfirmDialogComponent} from '../../../../shared/components/confirm-dialog/confirm-dialog.component';
import {catchError, concat, of, switchMap, tap, throwError} from 'rxjs';
import {ReviewDialogComponent, ReviewService} from '../../../../shared';
import {MatSnackBar} from '@angular/material/snack-bar';
import {Role} from '../../models/role.model';
import {Router} from '@angular/router';

import 'prismjs'; // Import Prism.js
import 'prismjs/components/prism-java.js';
import {ToastrService} from "ngx-toastr";

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

    private toastService = inject(ToastrService)
    canDelete: boolean = false;
    deleting: boolean = false;
    canUpdate: boolean = false;

    constructor(private userService: LoginService, private reviewService: ReviewService, private router: Router, private snackBar: MatSnackBar, protected dialog: MatDialog) { }

    ngOnInit(): void {
        this.canDelete = this.review.author === this.userService.getLogin() || this.userService.getRole() === Role.ADMIN;
        this.canUpdate = this.review.author === this.userService.getLogin()
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
                                this.toastService.error(err.error.message)
                                return of({ error: err });
                            })
                        )
                    );
                }
                return of();
            }), catchError(err => throwError(() => this.toastService.error(err.error.message)))
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

    updateReview(): void {
        this.dialog.open(ReviewDialogComponent, {data: {onQuestion: true, template: {
                    content: this.review.content,
                    tags: this.review.tags,
                    lineStart: this.review.lineStart,
                    lineEnd: this.review.lineEnd
                }}, disableClose: true}).afterClosed().pipe(
            switchMap(reviewValue => {
                console.log(reviewValue);
                if (reviewValue) {
                    return this.reviewService.updateById(this.review.id, reviewValue.content, reviewValue.lineStart, reviewValue.lineEnd, reviewValue.tags).pipe(
                        tap(response => {
                            this.review.content = response.content;
                            this.review.tags = response.tags;
                            this.review.lineStart = response.lineStart;
                            this.review.lineEnd = response.lineEnd;
                        }),
                        catchError(err => {
                            this.toastService.error(err.error.message)
                            return of(err);
                        })
                    );
                }
                return of();
            })
        ).subscribe()
    }
}
