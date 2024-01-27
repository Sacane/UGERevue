import { NgModule } from "@angular/core";
import { QuestionRoutingModule } from "./question-routing.module";
import { QuestionComponent } from "./question.component";
import { SharedModule } from "../../../../shared/shared.module";

@NgModule({
    declarations: [
        QuestionComponent
    ],
    imports: [
        QuestionRoutingModule,
        SharedModule
    ],
    exports: [QuestionComponent],
    bootstrap: [QuestionComponent]
})
export class QuestionModule { }
