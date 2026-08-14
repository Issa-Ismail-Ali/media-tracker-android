# Week 12 Reflection — Bonus Feature Sprint (Week 2 of 2, Final)

*Second and last week of bonus feature work. Week 13 has no build time — this is the last chance to get your feature demo-ready before Week 14. This template replaces the standard weekly reflection, same as last week.*

**Name:** Issa Ali
**Date:** 8/6/206
**My assigned bonus feature:** *Quotes

---

## Commits This Week

https://github.com/Issa-Ismail-Ali/media-tracker-android/pull/11

---

## Code Review

**Reviewed:** *(pod mate's name)* Ismail Bakkaoui
**Link to my review:**
https://github.com/ismabakk/media-tracker-android/pull/11#issuecomment-5211419693

### What I Looked At
I looked at how my pod mate added the My Quotes and Public Quotes sections and how they handled the 
different actions for each one. I looked at how they used the ViewModel to load the user's own 
quotes and then load public quotes separately. I also looked at the quote cards and how they only 
show Edit and Delete for the user's own quotes, while the public quotes have a Like or Unlike 
button. I paid attention to the edit and delete dialogs too because those were similar to things I 
had to add for my own Quotes feature. I also looked at how the Like button calls the ViewModel 
instead of handling the API request directly inside the screen.

### What I Noticed
One thing I noticed was how my pod mate separated what the user can do depending on which quotes 
list they are viewing. When the user is looking at My Quotes, the card gives them Edit and Delete 
buttons. When they are looking at Public Quotes, those buttons are replaced with a Like or Unlike 
button. I thought this was a good way to handle it because users should only be able to edit or 
delete their own quotes. I also noticed that the Like button is disabled when that quote is busy 
with a request. This is useful because it can prevent the user from pressing Like multiple times 
while the first request is still happening. The button also changes between Like and Unlike based 
on the current state.

### Comments I Left
I commented on how I liked that the quote card changes its available actions depending on whether 
the user is viewing My Quotes or Public Quotes. The user's own quotes have Edit and Delete, while 
public quotes have Like and Unlike instead. I think this is important because it prevents someone 
from being given options to edit or delete another user's quote. I also liked that the Like button 
becomes disabled while the quote is busy. That helps prevent the user from accidentally sending the 
same request multiple times by pressing the button repeatedly. I mentioned that changing the button 
between Like and Unlike also makes it easier for the user to understand the current state of the 
quote without having to guess whether their action worked.

---

## Bonus Feature — Final Status

<!-- Be concrete and honest. This is your last chance to flag something before demos.
     What does your feature actually do, end to end, right now? What's polished vs. rough?
     Is there anything you know is broken or half-done that you want on my radar before Week 14? -->

**What works end-to-end, right now:**
My Quotes feature is working from beginning to end now. I can go to a Media Detail screen and add a 
quote with the quote text, an optional page number, and choose if I want it to be public or private.
I can see my saved quotes from my profile and edit them later. When editing a quote, I can change 
the text, page number, and switch it between public and private. I can also delete a quote, and the 
app asks me to confirm before deleting it. There is also a Public Quotes section that shows public 
quotes from other users. I can like and unlike public quotes, and the like count changes. Loading, 
empty, error, and pagination states are also included.

**Tests written for this feature:**
I wrote a MockK test for the Like and Unlike part of my Quotes feature. The test uses a mocked 
DefaultMediaRepository, so it does not have to make a real request to the API. First, the test loads
a public quote that starts with zero likes. Then it calls the Like function and checks that the 
quote is added to the liked quotes and that the like count changes from zero to one. It also checks
that repository.likeQuote() was called exactly one time. After that, the test calls the function 
again to unlike the quote. It checks that the quote is no longer liked, the count goes back to zero,
and repository.unlikeQuote() was called. I ran the test and got BUILD SUCCESSFUL.

**Known gaps or rough edges going into demos:**
The main rough edge I still have is with remembering whether the current user already liked a public 
quote. The API gives me the likeCount, but the Quote model does not have something like isLiked that 
tells me if the logged-in user personally liked that quote already. Right now my ViewModel keeps track
of the quote IDs that I liked while I am using the Quotes screen. This works for liking and unliking 
during the current session, but that local state can be lost after restarting the app. The like 
itself is still sent to the API, but the screen does not have enough information to know that I 
previously liked it. Other than that, the main Week 1 and Week 2 requirements are working.

---

## One Thing I Understood More Deeply

One thing I understand more deeply after working on the Quotes feature is how the screen, ViewModel,
repository, and API all work together. Before this feature, I understood each part a little, but it 
was harder for me to understand the full process. With Quotes, I had to use all of them together for
adding, loading, editing, deleting, liking, and unliking quotes. I also understand optimistic 
updates better now. For example, when a user likes a quote, I can update the screen immediately 
instead of making them wait for the API. The request then happens in the background, and if it fails
I can put the old state back. Writing the MockK test also helped me understand how I can test this 
logic without using the real API.

---

## One Thing I'm Still Confused About
One thing I am still confused about is the best way to keep the Like and Unlike state completely 
synced with the server. I understand how to send POST /quotes/{id}/likes when someone likes a quote
and DELETE /quotes/{id}/likes when they unlike it. The part that still confuses me is what should 
happen when the app is closed and opened again. The API gives me the total number of likes for each
quote, but it does not give me a value that says whether the current user already liked that 
specific quote. Because of that, I can keep track of it in the ViewModel while the app is running, 
but I am still not completely sure how I would restore the correct liked state after restarting the 
app.

---

## Anything Else *(optional)*

<!-- Anything about the bonus feature sprint as a whole — the two-week format, being assigned a
     feature rather than choosing it, whatever's on your mind — is fair game here. -->

---

## Rubric

*You don't need to self-assess — this is here so you know what I'm looking at.*

| Section | Points | Full Credit | Half Credit | No Credit |
|:---|:---:|:---|:---|:---|
| **Reflection** | 10 | Honest final-status report — what works end-to-end, what's rough, what's tested — plus a specific, genuine "Understood More Deeply" that reflects on the sprint as a whole, not just this week. | Present but vague, or only reports on this week rather than the feature's overall state. | Missing or one-word answers. |
| **Code Review** | 10 | Specific observation about the code with explanation of why it matters (or a substantive positive comment). Link to review present and verified. | A question or comment that shows you read the code, but lacks explanation. | "Looks good!" or equivalent. Missing link. Review not found on GitHub. |
| **Total** | **20** | | | |

**A note on the code review score:** same as every other week — I check the link before grading.
