import { NgModule } from "@angular/core";
import { RouterModule, Routes } from "@angular/router";
import { ReviewsComponent } from "./reviews.component";

const routes: Routes = [
    { path: '', pathMatch: 'full', redirectTo: '/home' },
    {
        path: ':id',
        component: ReviewsComponent
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class ReviewsRoutingModule { }
