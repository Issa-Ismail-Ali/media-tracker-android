# Week {{07}} Reflection

**Name:** Issa Ali
**Date:** 7/02/2026

---

## Commits This Week

https://github.com/Issa-Ismail-Ali/media-tracker-android/pull/7

**Link:**

---

## Code Review

<!-- Every week you leave a review on a pod mate's pull request. Fill in both parts below.
     Part 1 is the link — I will verify the review exists on GitHub.
     Part 2 is your written assessment — what you actually looked at and what you found. -->

**Reviewed:** *(pod mate's name)* Kenan Port
**Link to my review:** https://github.com/Zabzar22/media-tracker-android/pull/8#issuecomment-4872034871

### What I Looked At

I looked at my podmate’s Media Detail screen code for Week 7. I focused on how they built the screen
layout, especially the top bar, cover art section, title, creator credit, rating row, buttons, About
section, stat grid, and review cards. I also looked at how they used FakeMediaRepository to get the 
media item by mediaId and how they used fake reviews for now before the real API is connected.

### What I Noticed

<One thing I noticed was that they were using FakeMediaRepository.mediaList.find 
{ it.id == mediaId }, which makes sense for this week because we are not using network calls yet. 
I also noticed that they made a StarRow helper function instead of repeating the star code in 
multiple places. That stood out to me because the same star layout is used for both the main rating 
and the review cards, so making it reusable keeps the screen cleaner.
### Comments I Left

I left a comment saying that I liked how they used helper Composables like CoverArt, StarRow, 
StatBox, and ReviewCard. I said that this made the Media Detail screen easier to read because the 
main screen was not trying to do everything in one big block of code.

---

## One Thing I Understood More Deeply

One thing I understood more deeply is why it helps to split a large screen into smaller Composables.
Before, I would usually try to put most of the layout inside one screen, but now I can see how that 
gets messy fast. Breaking the screen into smaller parts like the cover, stat box, star row, and 
review card makes the code easier to follow and easier to fix later.

---

## One Thing I'm Still Confused About

One thing I am still confused about is how much logic should stay inside the screen and how much 
should go into the ViewModel. I understand that fake data is okay for this week, but I am still not 
fully sure how the Media Detail screen should change when we start using GET /media/{mediaId} and 
GET /reviews. I think I understand the layout now, but the API version is still the part I need more
practice with.

---

## Anything Else *(optional)*

<!-- Did you help a pod mate work through something? Did you discover something cool or frustrating?
     Did something from a previous week finally click? This is a good place to put it. -->

---

## Rubric

*You don't need to self-assess — this is here so you know what I'm looking at.*

| Section | Points | Full Credit | Half Credit | No Credit |
|:---|:---:|:---|:---|:---|
| **Reflection** | 10 | Specific, honest responses to "More Deeply" and "Still Confused" sections. Shows genuine thinking — not just "I learned X." | Responses are present but vague or generic ("I got better at Compose"). | Missing or one-word answers. |
| **Code Review** | 10 | Specific observation about the code with explanation of why it matters (or a substantive positive comment). Link to review present and verified. | A question or comment that shows you read the code, but lacks explanation. | "Looks good!" or equivalent. Missing link. Review not found on GitHub. |
| **Total** | **20** | | | |

**A note on the code review score:** I check that the review actually exists on GitHub before grading. The written summary here and the GitHub comment should match. If the review isn't there, the written summary can't earn credit.
