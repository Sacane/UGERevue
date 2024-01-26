import { NgModule } from "@angular/core";
import { SharedModule } from "../../shared/shared.module";
import { QuestionsComponent } from "./questions.component";
import { QuestionsRoutingModule } from "./questions-routing.module";

@NgModule({
    declarations: [
        QuestionsComponent
    ],
    imports: [
        QuestionsRoutingModule,
        SharedModule
    ],
    exports: [QuestionsComponent],
    bootstrap: [QuestionsComponent]
})
export class QuestionsModule { }
