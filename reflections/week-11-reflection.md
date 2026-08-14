# Week 11 Reflection — Bonus Feature Sprint (Week 1 of 2)

*This week's reflection is different from the standard template. We're not doing Profile this week — instead, this is the first of two weeks building your assigned bonus feature (Write Review, Quotes, or Priorities). See `reflection-instructions.md` for naming/submission rules, which are unchanged; only the content below differs.*

**Name:** Issa Ali 
**Date:** 7/30/2026
**My assigned bonus feature:** *Quotes

---

## Commits This Week

https://github.com/Issa-Ismail-Ali/media-tracker-android/pull/10



---

## Code Review



**Reviewed:** *(pod mate's name)* Kenan Port
**Link to my review:** https://github.com/Zabzar22/media-tracker-android/pull/12#issuecomment-5210124772

### What I Looked At
I looked at my pod mate’s Write Review feature and focused mostly on the changes made to the 
WriteReviewScreen and WriteReviewViewModel. I looked at how they changed the screen from a stub into
an actual review form. They added a star rating, review text box, character limit, and a checkbox 
for sharing the review to the activity feed. I also looked at how the ViewModel keeps track of the 
rating, review text, share option, and submit state. Another thing I looked at was how they load the
media information so the title and cover can appear at the top of the review screen. I wanted to 
understand how all of these pieces connected when the user posts a review

### What I Noticed
One thing I noticed was how they handled submitting the review instead of immediately going back 
when the Post Review button is clicked. The ViewModel changes the state to Submitting, waits for 
the API request to finish, and only changes to Success after the review is actually posted. The 
screen watches that state and navigates back when it becomes successful. I thought this was 
important because if the app went back immediately, the user could think their review was posted 
even if the request failed. I also noticed that the review text is limited to 500 characters inside 
the ViewModel instead of only limiting it on the screen. This makes sure that the value being sent 
to the API cannot go over the limit.

### Comments I Left
I commented on the way the review submission waits for the API response before navigating back. 
I liked this because the user stays on the review screen while the request is submitting, and if 
something goes wrong they can see the error and try again instead of being sent back automatically. 
I also mentioned that disabling the Post Review button until a rating is selected was a good way to 
prevent an invalid review from being submitted. The ViewModel also checks the rating again before 
making the request, which gives it another layer of protection instead of depending only on the 
button. I thought this was a good approach because the screen and ViewModel both make sure the 
required rating is present before posting the review.

---

## Bonus Feature Progress



**What's working:**
This week I started building my assigned Quotes feature. I created the Quote data model and the 
request model needed to save a quote. I added GET /quotes and POST /quotes to the API service and 
connected them through DefaultMediaRepository. I also created a QuotesViewModel that can load a 
user’s quotes and handle pagination using the next cursor and has-more headers. On the Media Detail 
screen, I added an Add Quote button and a dialog where the user can enter quote text, an optional 
page number, and choose whether the quote is public or private. The quote text is limited to 500 
characters. I also added a My Quotes option to the Profile screen so the quotes list can be opened.

**What's still stubbed, fake, or not started:**
The main Week 1 target is mostly built, but I still need to fully test the quote flow from beginning
to end with the real API. I need to confirm that saving a quote from Media Detail actually makes it
appear on the My Quotes screen through GET /quotes. The later requirements are also not started yet.
I have not added editing or deleting quotes, liking or unliking public quotes, or the public quotes 
feed using GET /quotes?public=true. I also have not added the user interface for those actions yet. 
For now I am focused on making sure the basic Week 1 feature works correctly before moving on to the
Week 2 parts.

**What I'm blocked on, if anything:**
Right now I am not completely blocked, but I still need to test the API responses carefully to make
sure the request model matches what the backend expects. The part I am watching the closest is 
POST /quotes because it sends several fields, including the media ID, quote text, optional 
page number, and public/private value. If one of those fields does not match the API exactly, 
the quote will not save. I also need to make sure the quotes returned by GET /quotes include the 
media information needed to show which book, movie, or show the quote belongs to. Once I confirm 
those responses, I should be able to finish the Week 1 target without a major blocker.

---

## One Thing I Understood More Deeply

One thing I understood more deeply this week is how a feature has to move through several layers of 
the app before it actually works. For Quotes, it was not enough to just add an Add Quote button. 
I needed a data model, request model, Retrofit endpoint, repository function, ViewModel function, 
screen state, and user interface all connected together. I understand the repository pattern better 
now because I can see why it helps keep the network code separate from the screen logic. I also 
understand pagination better because the Quotes endpoint uses the same cursor system as Search. 
Instead of loading everything at once, the app keeps the next cursor and loads another page only 
when more results are available.

---

## One Thing I'm Still Confused About

One thing I am still a little confused about is how the public quotes view will work differently 
from the user’s normal quotes list. I understand that normal GET /quotes should show my own public 
and private quotes, while GET /quotes?public=true replaces that list with public quotes from all 
users. I still need more practice with keeping those two states separate so switching to the public
view does not mix my private quotes into the public feed. I am also still learning how liking and 
unliking should update the quote card while keeping the like count correct. I understand the 
endpoints, but I want to make sure the screen state stays accurate when the user likes or unlikes 
a quote.

---

## Anything Else *(optional)*

This week helped me see that building the bonus feature is similar to the work we did with Library 
and Favorites. A lot of the same patterns are being reused, especially the repository, ViewModel 
state, API calls, and pagination. That made the Quotes feature easier to start because I was not 
learning every part from the beginning.

## Rubric

*You don't need to self-assess — this is here so you know what I'm looking at.*

| Section | Points | Full Credit | Half Credit | No Credit |
|:---|:---:|:---|:---|:---|
| **Reflection** | 10 | Concrete progress report (what's wired, what's not) plus specific, honest "Understood More Deeply" and "Still Confused" sections. | Present but vague — "I worked on my feature" with no specifics on what's actually working. | Missing or one-word answers. |
| **Code Review** | 10 | Specific observation about the code with explanation of why it matters (or a substantive positive comment). Link to review present and verified. | A question or comment that shows you read the code, but lacks explanation. | "Looks good!" or equivalent. Missing link. Review not found on GitHub. |
| **Total** | **20** | | | |

**A note on the code review score:** I check that the review actually exists on GitHub before grading. The written summary here and the GitHub comment should match.
