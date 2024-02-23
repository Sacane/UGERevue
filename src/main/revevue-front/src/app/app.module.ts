import { NgModule } from "@angular/core";
import { AppComponent } from "./app.component";
import { SharedModule } from "./shared/shared.module";
import { AppRoutingModule } from "./app-routing.module";
import { RouterOutlet } from "@angular/router";
import { BrowserModule } from "@angular/platform-browser";
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HomeModule, LoginModule, QuestionsMenuModule, ReviewsModule, SignupModule, TagsModule, UsersModule } from "./modules";
import { provideHttpClient, withFetch, withInterceptors } from "@angular/common/http";
import { authInterceptor } from "./shared/authInterceptor";
import { CommonModule } from "@angular/common";

@NgModule({
    declarations: [
        AppComponent
    ],
    providers: [
        provideHttpClient(withFetch(), withInterceptors([authInterceptor])),
    ],
    imports: [
        CommonModule,
        BrowserModule,
        BrowserAnimationsModule,
        RouterOutlet,
        AppRoutingModule,
        SharedModule,
        LoginModule,
        SignupModule,
        HomeModule,
        QuestionsMenuModule,
        TagsModule,
        UsersModule,
        ReviewsModule
    ],
    bootstrap: [AppComponent]
})
export class AppModule {
}
