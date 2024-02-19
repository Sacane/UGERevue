import {NgModule} from "@angular/core";
import {AppComponent} from "./app.component";
import {SharedModule} from "./shared/shared.module";
import {AppRoutingModule} from "./app-routing.module";
import {RouterOutlet} from "@angular/router";
import {BrowserModule} from "@angular/platform-browser";
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {HomeModule, LoginModule, QuestionsMenuModule, SignupModule, TagsModule, UsersModule} from "./modules";
import {provideHttpClient, withFetch, withInterceptors} from "@angular/common/http";
import {authInterceptor} from "./shared/authInterceptor";

@NgModule({
    declarations: [
        AppComponent
    ],
    providers: [
        provideHttpClient(withFetch(), withInterceptors([authInterceptor])),
    ],
    imports: [
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
        UsersModule
    ],
    bootstrap: [AppComponent]
})
export class AppModule {
}
