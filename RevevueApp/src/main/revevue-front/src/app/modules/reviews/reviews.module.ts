import {NgModule} from "@angular/core";
import {SharedModule} from "../../shared/shared.module";
import {ReviewsComponent} from "./reviews.component";
import {ReviewsRoutingModule} from "./reviews-routing.module";
import {HIGHLIGHT_OPTIONS, HighlightModule} from "ngx-highlightjs";
import {ChildReviewComponent} from "./child-review/child-review.component";
import {CommonModule} from "@angular/common";
import {MarkdownComponent} from "ngx-markdown";

@NgModule({
    providers: [
        {
            provide: HIGHLIGHT_OPTIONS,
            useValue: {
                coreLibraryLoader: () => import('highlight.js/lib/core'),
                lineNumbersLoader: () => import('ngx-highlightjs/line-numbers'),
                languages: {
                    java: () => import('highlight.js/lib/languages/java'),
                }
            }
        }
    ],
    declarations: [
        ReviewsComponent,
        ChildReviewComponent
    ],
    imports: [
        CommonModule,
        ReviewsRoutingModule,
        HighlightModule,
        SharedModule,
        MarkdownComponent
    ],
    exports: [ReviewsComponent, ChildReviewComponent],
    bootstrap: [ReviewsComponent, ChildReviewComponent]
})
export class ReviewsModule { }
