import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { JhiLanguageService } from 'ng-jhipster';
import { JhiLanguageHelper } from 'app/core';

import { BlogchainSharedModule } from 'app/shared';
import {
    BlogEntryComponent,
    BlogEntryDetailComponent,
    BlogEntryUpdateComponent,
    BlogEntryDeletePopupComponent,
    BlogEntryDeleteDialogComponent,
    blogEntryRoute,
    blogEntryPopupRoute
} from './';

const ENTITY_STATES = [...blogEntryRoute, ...blogEntryPopupRoute];

@NgModule({
    imports: [BlogchainSharedModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [
        BlogEntryComponent,
        BlogEntryDetailComponent,
        BlogEntryUpdateComponent,
        BlogEntryDeleteDialogComponent,
        BlogEntryDeletePopupComponent
    ],
    entryComponents: [BlogEntryComponent, BlogEntryUpdateComponent, BlogEntryDeleteDialogComponent, BlogEntryDeletePopupComponent],
    providers: [{ provide: JhiLanguageService, useClass: JhiLanguageService }],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class BlogchainBlogEntryModule {
    constructor(private languageService: JhiLanguageService, private languageHelper: JhiLanguageHelper) {
        this.languageHelper.language.subscribe((languageKey: string) => {
            if (languageKey !== undefined) {
                this.languageService.changeLanguage(languageKey);
            }
        });
    }
}
