import { NgModule } from "@angular/core";
import { RouterModule, Routes } from "@angular/router";
import { QuestionsComponent } from "./components/questions/questions.component";
import { QuestionComponent } from "./components/question/question.component";
import { CreateQuestionComponent } from "./components/create-question/create-question.component";
import { TestComponent } from "./components/test/test.component";

const routes: Routes = [
    {
        path: '',
        component: QuestionsComponent
    },
    {
        path: 'test',
        component: TestComponent
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
