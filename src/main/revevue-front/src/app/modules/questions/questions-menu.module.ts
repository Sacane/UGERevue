import {NgModule} from "@angular/core";
import {SharedModule} from "../../shared/shared.module";
import {QuestionsMenuComponent} from "./questions-menu.component";
import {QuestionsMenuRoutingModule} from "./questions-menu-routing.module";
import {QuestionComponent} from "./components/question/question.component";
import {QuestionsComponent} from "./components/questions/questions.component";
import {DatePipe} from "@angular/common";
import {HIGHLIGHT_OPTIONS, HighlightModule} from "ngx-highlightjs";
import {MatDividerModule} from "@angular/material/divider";
import {MatListModule} from "@angular/material/list";

@NgModule({
    providers: [
        {
            provide: HIGHLIGHT_OPTIONS,
            useValue: {
                coreLibraryLoader: () => import('highlight.js/lib/core'),
                lineNumbersLoader: () => import('ngx-highlightjs/line-numbers'),
                languages: {
                    java: () => import('highlight.js/lib/languages/java'),
                },
            }
        }
    ],
    declarations: [
        QuestionsMenuComponent,
        QuestionComponent,
        QuestionsComponent
    ],
    imports: [
        QuestionsMenuRoutingModule,
        SharedModule,
        DatePipe,
        HighlightModule,
        MatDividerModule,
        MatListModule,
    ],
    exports: [QuestionsMenuComponent],
    bootstrap: [QuestionsMenuComponent]
})
export class QuestionsMenuModule {
}
