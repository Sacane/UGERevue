import {Component, inject, OnInit, ViewEncapsulation} from '@angular/core';
import {QuestionService} from "../../../../shared/question.service";
import {ActivatedRoute} from "@angular/router";
import {catchError, Observable, of} from 'rxjs';

@Component({
  selector: 'app-search-questions',
  templateUrl: './search-questions.component.html',
  styleUrl: './search-questions.component.scss',
    encapsulation: ViewEncapsulation.None
})
export class SearchQuestionsComponent implements OnInit {
    private questionService = inject(QuestionService);
    private activatedRoute = inject(ActivatedRoute);
    questions$: Observable<any[]>;

    ngOnInit(): void {
        const label = localStorage.getItem('labelSearch')
        const username = localStorage.getItem('usernameSearch')
        this.questions$ = this.questionService.searchQuestion(label ?? '', username ?? '').pipe(
            catchError(() => of([]))
        );

    }
}
