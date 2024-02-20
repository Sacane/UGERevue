import { NgModule } from "@angular/core";
import { SharedModule } from "../../shared/shared.module";
import { ReviewsComponent } from "./reviews.component";
import { ReviewsRoutingModule } from "./reviews-routing.module";

@NgModule({
    declarations: [
        ReviewsComponent
    ],
    imports: [
        ReviewsRoutingModule,
        SharedModule
    ],
    exports: [ReviewsComponent],
    bootstrap: [ReviewsComponent]
})
export class ReviewsModule { }
