import { NgModule } from "@angular/core";
import { RouterModule, Routes } from "@angular/router";
import { QuestionsComponent } from "./components/questions/questions.component";
import { QuestionComponent } from "./components/question/question.component";
import { CreateQuestionComponent } from "./components/create-question/create-question.component";
import {SearchQuestionsComponent} from "./components/search-questions/search-questions.component";

const routes: Routes = [
    {
        path: 'search',
        component: SearchQuestionsComponent
    },
    {
        path: '',
        component: QuestionsComponent
    },
    {
        path: 'ask',
        component: CreateQuestionComponent
    },
    {
        path: ':id',
        component: QuestionComponent
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class QuestionsMenuRoutingModule { }
