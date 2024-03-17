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
import {provideHttpClient, withFetch, withInterceptors, withXsrfConfiguration} from "@angular/common/http";
import {authInterceptor} from "./shared/authInterceptor";
import {CommonModule, HashLocationStrategy, LocationStrategy} from "@angular/common";
import {MarkdownModule} from "ngx-markdown";
import {provideToastr, ToastrModule} from "ngx-toastr";
import {MatChipsModule} from "@angular/material/chips";
import {MatAutocompleteModule} from "@angular/material/autocomplete";

@NgModule({
    declarations: [
        AppComponent
    ],
    providers: [
        provideHttpClient(withFetch(), withInterceptors([authInterceptor]), withXsrfConfiguration({
                cookieName: 'XSRF-TOKEN',
                headerName: 'X-XSRF-TOKEN'
            })
        ),
        provideToastr(),
        provideAnimations(),
        {provide: LocationStrategy, useClass: HashLocationStrategy}
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
        ToastrModule.forRoot(),
        MatChipsModule,
        MatAutocompleteModule
    ],
    bootstrap: [AppComponent]
})
export class AppModule {
}
