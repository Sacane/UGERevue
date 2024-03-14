import {NgModule} from "@angular/core";
import {AppComponent} from "./app.component";
import {SharedModule} from "./shared/shared.module";
import {AppRoutingModule} from "./app-routing.module";
import {RouterOutlet} from "@angular/router";
import {BrowserModule} from "@angular/platform-browser";
import {BrowserAnimationsModule, provideAnimations} from '@angular/platform-browser/animations';
import {
    HomeModule,
    LoginModule,
    QuestionsMenuModule,
    ReviewsModule,
    SignupModule,
    TagsModule,
    UsersModule
} from "./modules";
import {provideHttpClient, withFetch, withInterceptors} from "@angular/common/http";
import {authInterceptor} from "./shared/authInterceptor";
import {CommonModule} from "@angular/common";
import {MarkdownModule} from "ngx-markdown";
import {provideToastr, ToastrModule} from "ngx-toastr";

@NgModule({
    declarations: [
        AppComponent
    ],
    providers: [
        provideHttpClient(withFetch(), withInterceptors([authInterceptor])), provideToastr(), provideAnimations()
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
        ReviewsModule,
        MarkdownModule,
        MarkdownModule.forRoot(),
        BrowserAnimationsModule,
        ToastrModule.forRoot()
    ],
    bootstrap: [AppComponent]
})
export class AppModule {
}
