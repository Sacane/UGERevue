import { NgModule } from "@angular/core";
import { SharedModule } from "../../shared/shared.module";
import { QuestionsMenuComponent } from "./questions-menu.component";
import { QuestionsMenuRoutingModule } from "./questions-menu-routing.module";

@NgModule({
    declarations: [
        QuestionsMenuComponent
    ],
    imports: [
        QuestionsMenuRoutingModule,
        SharedModule
    ],
    exports: [QuestionsMenuComponent],
    bootstrap: [QuestionsMenuComponent]
})
export class QuestionsMenuModule { }
