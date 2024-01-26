import { NgModule } from "@angular/core";
import { RouterModule, Routes } from "@angular/router";
import { QuestionsComponent } from "./questions.component";

const routes: Routes = [
    {
        path: '',
        component: QuestionsComponent,
        children: [
            { path: ':id', component: QuestionsComponent } //TODO FAIRE PAGE ID
        ]
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class QuestionsRoutingModule { }
