package org.wordpress.android.ui.screenshots;

import androidx.test.espresso.Espresso;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wordpress.android.R;
import org.wordpress.android.e2e.pages.PostsListPage;
import org.wordpress.android.e2e.pages.SitePickerPage;
import org.wordpress.android.support.BaseTest;
import org.wordpress.android.support.DemoModeEnabler;
import org.wordpress.android.ui.WPLaunchActivity;
import org.wordpress.android.ui.posts.EditPostActivity;
import org.wordpress.android.ui.prefs.AppPrefs;
import org.wordpress.android.util.image.ImageType;

import tools.fastlane.screengrab.Screengrab;
import tools.fastlane.screengrab.UiAutomatorScreenshotStrategy;

import static org.wordpress.android.support.WPSupportUtils.clickOn;
import static org.wordpress.android.support.WPSupportUtils.getCurrentActivity;
import static org.wordpress.android.support.WPSupportUtils.getTranslatedString;
import static org.wordpress.android.support.WPSupportUtils.idleFor;
import static org.wordpress.android.support.WPSupportUtils.isElementDisplayed;
import static org.wordpress.android.support.WPSupportUtils.populateTextField;
import static org.wordpress.android.support.WPSupportUtils.populateTextFieldWithin;
import static org.wordpress.android.support.WPSupportUtils.pressBackUntilElementIsDisplayed;
import static org.wordpress.android.support.WPSupportUtils.scrollToThenClickOn;
import static org.wordpress.android.support.WPSupportUtils.selectItemWithTitleInTabLayout;
import static org.wordpress.android.support.WPSupportUtils.waitForAtLeastOneElementWithIdToBeDisplayed;
import static org.wordpress.android.support.WPSupportUtils.waitForElementToBeDisplayed;
import static org.wordpress.android.support.WPSupportUtils.waitForElementToBeDisplayedWithoutFailure;
import static org.wordpress.android.support.WPSupportUtils.waitForImagesOfTypeWithPlaceholder;
import static org.wordpress.android.test.BuildConfig.SCREENSHOT_LOGINUSERNAME;
import static org.wordpress.android.test.BuildConfig.SCREENSHOT_LOGINPASSWORD;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class WPScreenshotTest extends BaseTest {
    @ClassRule
    public static final WPLocaleTestRule LOCALE_TEST_RULE = new WPLocaleTestRule();


    @Rule
    public ActivityTestRule<WPLaunchActivity> mActivityTestRule = new ActivityTestRule<>(WPLaunchActivity.class,
            false, false);

    private DemoModeEnabler mDemoModeEnabler = new DemoModeEnabler();

    @Test
    public void wPScreenshotTest() {
        mActivityTestRule.launchActivity(null);
        Screengrab.setDefaultScreenshotStrategy(new UiAutomatorScreenshotStrategy());
        
        // Never show the Gutenberg dialog when opening a post
        AppPrefs.setGutenbergInformativeDialogDisabled(true);

        // Enable Demo Mode
        mDemoModeEnabler.enable();

        tmpWPLogin();

        idleFor(1000);
        Screengrab.screenshot("1-build-and-manage-your-website");

        editBlogPost();
        manageMedia();
        navigateStats();
        navigateNotifications();

        // Turn Demo Mode off on the emulator when we're done
        mDemoModeEnabler.disable();
        tmpWpLogout();
    }

    private void tmpWPLogin() {
        // If we're already logged in, log out before starting
        if (!isElementDisplayed(R.id.login_button)) {
            this.tmpWpLogout();
        }

        // Login Prologue – We want to log in, not sign up
        // See LoginPrologueFragment
        clickOn(R.id.login_button);
        clickOn(R.id.login_site_button);

        // Choose WordPress.com for reliability
        populateTextField(R.id.input, "wordpress.com");
        clickOn(R.id.primary_button);

        // Email Address Screen – Fill it in and click "Next"
        populateTextFieldWithin(R.id.login_username_row, SCREENSHOT_LOGINUSERNAME);
        populateTextFieldWithin(R.id.login_password_row, SCREENSHOT_LOGINPASSWORD);

        clickOn(R.id.primary_button);

        // Login Confirmation Screen – Click "Continue"
        // See LoginEpilogueFragment
        clickOn(R.id.primary_button);
    }

    private void tmpWpLogout() {
        // Click on the "Me" tab in the nav, then choose "Log Out"
        clickOn(R.id.nav_me);
        scrollToThenClickOn(R.id.row_logout);

        // Confirm that we want to log out
        clickOn(android.R.id.button1);
    }

    private void editBlogPost() {
        // Choose the "sites" tab in the nav
        clickOn(R.id.nav_sites);

        // Choose "Switch Site"
        clickOn(R.id.switch_site);

        (new SitePickerPage()).chooseSiteWithURL("infocusphotographers.com");

        // Choose "Blog Posts"
        scrollToThenClickOn(R.id.row_blog_posts);

        // Choose "Drafts"
        selectItemWithTitleInTabLayout(getTranslatedString(R.string.post_list_drafts), R.id.tabLayout);

        // Get a screenshot of the writing feature (without image)
        String name = "2-create-beautiful-posts-and-pages";
        screenshotPostWithName("Time to Book Summer Sessions", name, false);

        // Get a screenshot of the drafts feature
        screenshotPostWithName("Ideas", "6-capture-ideas-on-the-go", false);

        // Get a screenshot of the drafts feature
        screenshotPostWithName("Summer Band Jam", "7-create-beautiful-posts-and-pages", true);

        // Get a screenshot for "write without compromises"
        screenshotPostWithName("Now Booking Summer Sessions", "8-write-without-compromises", true);

        // Exit back to the main activity
        pressBackUntilElementIsDisplayed(R.id.nav_sites);
    }

    private void screenshotPostWithName(String name, String screenshotName, boolean hideKeyboard) {
        idleFor(2000);

        PostsListPage.scrollToTop();
        PostsListPage.tapPostWithName(name);

        waitForElementToBeDisplayed(R.id.editor_activity);

        // Wait for the editor to load all images
        idleFor(5000);

        if (hideKeyboard) {
            Espresso.closeSoftKeyboard();
        }

        takeScreenshot(screenshotName);
        pressBackUntilElementIsDisplayed(R.id.tabLayout);
    }

    private void manageMedia() {
        // Click on the "Sites" tab in the nav, then choose "Media"
        clickOn(R.id.nav_sites);
        clickOn(R.id.row_media);

        waitForElementToBeDisplayedWithoutFailure(R.id.media_grid_item_image);

        Screengrab.screenshot("5-share-from-anywhere");

        pressBackUntilElementIsDisplayed(R.id.row_media);
    }

    private void navigateNotifications() {
        // Click on the "Notifications" tab in the nav
        clickOn(R.id.nav_notifications);

        waitForAtLeastOneElementWithIdToBeDisplayed(R.id.note_content_container);
        waitForImagesOfTypeWithPlaceholder(R.id.note_avatar, ImageType.AVATAR);


        Screengrab.screenshot("4-check-whats-happening-in-real-time");

        // Exit the notifications activity
        pressBackUntilElementIsDisplayed(R.id.nav_sites);
    }

    private void navigateStats() {
        // Click on the "Sites" tab in the nav, then choose "Stats"
        clickOn(R.id.nav_sites);
        clickOn(R.id.row_stats);

        // Show the year view – it'll have the best layout
        selectItemWithTitleInTabLayout("Years", R.id.tabLayout);

        // Wait for the stats to load
        idleFor(5000);
        
        Screengrab.screenshot("3-track-what-your-visitors-love");

        // Exit the Stats Activity
        pressBackUntilElementIsDisplayed(R.id.nav_sites);
    }

    private void takeScreenshot(String screenshotName) {
        try {
            Screengrab.screenshot(screenshotName);
        } catch (RuntimeException r) {
            // Screenshots will fail when running outside of Fastlane, so this is safe to ignore.
        }
    }

    private boolean editPostActivityIsNoLongerLoadingImages() {
        EditPostActivity editPostActivity = (EditPostActivity) getCurrentActivity();
        return editPostActivity.getAztecImageLoader().getNumberOfImagesBeingDownloaded() == 0;
    }
}
