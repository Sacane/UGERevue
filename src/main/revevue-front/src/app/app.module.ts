import {NgModule} from "@angular/core";
import {AppComponent} from "./app.component";
import {SharedModule} from "./shared/shared.module";
import {AppRoutingModule} from "./app-routing.module";
import {RouterOutlet} from "@angular/router";
import {BrowserModule} from "@angular/platform-browser";
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {HomeModule, LoginModule, QuestionsMenuModule, SignupModule, TagsModule, UsersModule} from "./modules";
import {provideHttpClient} from "@angular/common/http";

@NgModule({
  declarations: [
    AppComponent
  ],
  providers: [
    provideHttpClient(),
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
