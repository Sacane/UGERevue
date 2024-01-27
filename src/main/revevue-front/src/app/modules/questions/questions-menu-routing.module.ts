import { NgModule } from "@angular/core";
import { RouterModule, Routes } from "@angular/router";
import { QuestionsComponent } from "./components/questions/questions.component";
import { QuestionComponent } from "./components/question/question.component";

const routes: Routes = [
    {
        path: '',
        component: QuestionsComponent
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
