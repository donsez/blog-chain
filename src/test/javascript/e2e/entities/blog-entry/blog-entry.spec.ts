/* tslint:disable no-unused-expression */
import { browser, ExpectedConditions as ec, protractor, promise } from 'protractor';
import { NavBarPage, SignInPage } from '../../page-objects/jhi-page-objects';

import { BlogEntryComponentsPage, BlogEntryDeleteDialog, BlogEntryUpdatePage } from './blog-entry.page-object';

const expect = chai.expect;

describe('BlogEntry e2e test', () => {
    let navBarPage: NavBarPage;
    let signInPage: SignInPage;
    let blogEntryUpdatePage: BlogEntryUpdatePage;
    let blogEntryComponentsPage: BlogEntryComponentsPage;
    let blogEntryDeleteDialog: BlogEntryDeleteDialog;

    before(async () => {
        await browser.get('/');
        navBarPage = new NavBarPage();
        signInPage = await navBarPage.getSignInPage();
        await signInPage.autoSignInUsing('admin', 'admin');
        await browser.wait(ec.visibilityOf(navBarPage.entityMenu), 5000);
    });

    it('should load BlogEntries', async () => {
        await navBarPage.goToEntity('blog-entry');
        blogEntryComponentsPage = new BlogEntryComponentsPage();
        await browser.wait(ec.visibilityOf(blogEntryComponentsPage.title), 5000);
        expect(await blogEntryComponentsPage.getTitle()).to.eq('blogchainApp.blogEntry.home.title');
    });

    it('should load create BlogEntry page', async () => {
        await blogEntryComponentsPage.clickOnCreateButton();
        blogEntryUpdatePage = new BlogEntryUpdatePage();
        expect(await blogEntryUpdatePage.getPageTitle()).to.eq('blogchainApp.blogEntry.home.createOrEditLabel');
        await blogEntryUpdatePage.cancel();
    });

    it('should create and save BlogEntries', async () => {
        const nbButtonsBeforeCreate = await blogEntryComponentsPage.countDeleteButtons();

        await blogEntryComponentsPage.clickOnCreateButton();
        await promise.all([
            blogEntryUpdatePage.setTitleInput('title'),
            blogEntryUpdatePage.setContentInput('content'),
            blogEntryUpdatePage.setDateInput('01/01/2001' + protractor.Key.TAB + '02:30AM'),
            blogEntryUpdatePage.blogSelectLastOption()
            // blogEntryUpdatePage.tagSelectLastOption(),
        ]);
        expect(await blogEntryUpdatePage.getTitleInput()).to.eq('title');
        expect(await blogEntryUpdatePage.getContentInput()).to.eq('content');
        expect(await blogEntryUpdatePage.getDateInput()).to.contain('2001-01-01T02:30');
        await blogEntryUpdatePage.save();
        expect(await blogEntryUpdatePage.getSaveButton().isPresent()).to.be.false;

        expect(await blogEntryComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1);
    });

    it('should delete last BlogEntry', async () => {
        const nbButtonsBeforeDelete = await blogEntryComponentsPage.countDeleteButtons();
        await blogEntryComponentsPage.clickOnLastDeleteButton();

        blogEntryDeleteDialog = new BlogEntryDeleteDialog();
        expect(await blogEntryDeleteDialog.getDialogTitle()).to.eq('blogchainApp.blogEntry.delete.question');
        await blogEntryDeleteDialog.clickOnConfirmButton();

        expect(await blogEntryComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
    });

    after(async () => {
        await navBarPage.autoSignOut();
    });
});
