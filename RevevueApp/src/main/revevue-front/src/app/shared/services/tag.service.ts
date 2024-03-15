import {inject, Injectable} from "@angular/core";
import {environment} from "../../environment";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {TagWrapperDTO} from "../models/tag.model";

@Injectable({
    providedIn: 'root',
})
export class TagService{
    private url: string = environment.apiUrl + 'tags';

    private client = inject(HttpClient)

    public getTags(): Observable<Array<TagWrapperDTO>> {
        return this.client.get<Array<TagWrapperDTO>>(this.url)
    }
}
