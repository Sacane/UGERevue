import {
    AfterViewInit,
    Component,
    Input,
    OnChanges,
    OnDestroy,
    SimpleChanges,
    ViewChild,
    ViewEncapsulation
} from '@angular/core';
import {FormControl} from '@angular/forms';
import {MatPaginator} from '@angular/material/paginator';
import {Router} from '@angular/router';
import {Subject, takeUntil} from 'rxjs';
import {SimpleQuestion} from "../../models/question";

@Component({
    selector: 'app-questions-displayer',
    templateUrl: './questions-displayer.component.html',
    styleUrl: './questions-displayer.component.scss',
    encapsulation: ViewEncapsulation.None
})
export class QuestionsDisplayerComponent implements AfterViewInit, OnDestroy, OnChanges {
    @ViewChild(MatPaginator) paginator: MatPaginator;

    @Input() questions: SimpleQuestion[];

    titleFilter = new FormControl('');
    displayedQuestions: SimpleQuestion[] = [] as SimpleQuestion[];
    private _onDestroy = new Subject<void>();

    constructor(private router: Router) { }

    ngAfterViewInit(): void {
        this.titleFilter.valueChanges.pipe(
            takeUntil(this._onDestroy)
        ).subscribe(value => {
            this.displayedQuestions = this.questions.filter(question => question.title.includes(value as string));
        });
    }

    ngOnDestroy(): void {
        this._onDestroy.next();
        this._onDestroy.complete();
    }

    ngOnChanges(changes: SimpleChanges): void {
        this.displayedQuestions = this.questions;
    }

    navigateTo(url: string): void {
        this.router.navigateByUrl(url).then();
    }
}
