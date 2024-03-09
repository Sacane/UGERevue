import {AfterViewInit, Component, inject, OnInit, signal, ViewChild, ViewEncapsulation} from '@angular/core';
import {QuestionService} from "../../../../shared/question.service";
import {ActivatedRoute} from "@angular/router";
import {MatTableDataSource} from "@angular/material/table";
import {MatPaginator} from "@angular/material/paginator";
import {SimpleQuestion} from "../../../../shared/models/question";

@Component({
  selector: 'app-search-questions',
  templateUrl: './search-questions.component.html',
  styleUrl: './search-questions.component.scss',
    encapsulation: ViewEncapsulation.None
})
export class SearchQuestionsComponent implements OnInit, AfterViewInit{
    private questionService = inject(QuestionService)
    private activatedRoute = inject(ActivatedRoute)
    label = signal(this.activatedRoute.snapshot.params['label'])
    username = signal(this.activatedRoute.snapshot.params['username'])
    datasource = new MatTableDataSource<SimpleQuestion>([]);
    displayedColumns = ['title', 'description', 'Nom utilisateur', 'nbAnswers', 'actions']
    @ViewChild(MatPaginator) paginator!: MatPaginator;
    ngOnInit(): void {
        this.activatedRoute.params.subscribe(params => {
            this.label.set(params['label']);
            this.username.set(params['username'])
            const label = params['label']
            this.questionService.searchQuestion(label === 'undefined' ? '' : params['label'],
                params['username'] === 'undefined' ? undefined : params['username'])
                .subscribe(questions => this.datasource.data = questions);
        });
    }
    ngAfterViewInit() {
        this.datasource.paginator = this.paginator;
    }
}
