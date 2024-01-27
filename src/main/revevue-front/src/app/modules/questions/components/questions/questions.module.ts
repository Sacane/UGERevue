import { NgModule } from "@angular/core";
import { QuestionsComponent } from "./questions.component";
import { QuestionsRoutingModule } from "./questions-routing.module";
import { SharedModule } from "../../../../shared/shared.module";

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
