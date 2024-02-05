import {NgModule} from "@angular/core";
import {SharedModule} from "../../shared/shared.module";
import {QuestionsMenuComponent} from "./questions-menu.component";
import {QuestionsMenuRoutingModule} from "./questions-menu-routing.module";
import {QuestionComponent} from "./components/question/question.component";
import {QuestionsComponent} from "./components/questions/questions.component";
import {MatGridListModule} from "@angular/material/grid-list";

@NgModule({
    declarations: [
        QuestionsMenuComponent,
        QuestionComponent,
        QuestionsComponent
    ],
    imports: [
        QuestionsMenuRoutingModule,
        SharedModule,
        MatGridListModule,
    ],
    exports: [QuestionsMenuComponent],
    bootstrap: [QuestionsMenuComponent]
})
export class QuestionsMenuModule {
}
