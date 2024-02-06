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
import {ReviewComponent} from "./components/review/review.component";
import {ReviewsComponent} from "./components/reviews/reviews.component";
import {MatChipsModule} from "@angular/material/chips";
import {CreateQuestionComponent} from "./components/create-question/create-question.component";

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
        QuestionsComponent,
        ReviewComponent,
        ReviewsComponent,
        CreateQuestionComponent
    ],
    imports: [
        QuestionsMenuRoutingModule,
        SharedModule,
        DatePipe,
        HighlightModule,
        MatDividerModule,
        MatListModule,
        MatChipsModule,
    ],
    exports: [QuestionsMenuComponent],
    bootstrap: [QuestionsMenuComponent]
})
export class QuestionsMenuModule {
}
