# Week {{06}} Reflection

**Name:** Issa Ali
**Date:** 6/25/2026

---

## Commits This Week

https://github.com/Issa-Ismail-Ali/media-tracker-android/pull/6

**Link:**

---

## Code Review

<!-- Every week you leave a review on a pod mate's pull request. Fill in both parts below.
     Part 1 is the link — I will verify the review exists on GitHub.
     Part 2 is your written assessment — what you actually looked at and what you found. -->

**Reviewed:** *(pod mate's name)* Kenan Port
**Link to my review:** 
https://github.com/Zabzar22/media-tracker-android/pull/6#issuecomment-4805751534

### What I Looked At

I looked at my pod mate's pull request for the search feature. I focused on the SearchViewModel.kt 
file and looked at the onQueryChange(), clearQuery(), and onTypeSelect() functions. 
I wanted to see how the search query and selected media type were being stored and updated when the
user uses the search screen.

### What I Noticed

I noticed that SearchViewModel.kt has _query and _selectedType as private values, then exposes 
them as query and selectedType. I thought this was important because the screen can read the values,
but it has to use functions like onQueryChange() and onTypeSelect() to change them. This helps keep 
the search data controlled inside the ViewModel.

### Comments I Left

I left a comment on SearchViewModel.kt about how _query is private but query is public. I said this 
was a good design choice because it lets the screen observe the search text without directly 
changing the private state.

---

## One Thing I Understood More Deeply

One thing I understood better was what onQueryChange(value: String) does in SearchViewModel.kt. 
Before looking at it I knew the search box needed to update when the user typed, but I did not know
exactly where that update happened. This function takes the new text from the search box and saves 
it into _query.value so the rest of the software knows what the user typed.

---

## One Thing I'm Still Confused About

One thing I am still confused about is how the app knows when to load the next page of search 
results. I understand that when the user scrolls near the bottom of the list, the next page of 
results loads automatically and gets added to the current list. I also understand that the response 
tells the app if there are more pages and where the next page starts. The part I am still unsure 
about is whether I implemented the loading part correctly because I cannot really see the loading 
happening on the screen. Since the results load quickly, it is hard to tell if the app is actually 
showing the loading state or if it is just adding the next results right away.


---

## Anything Else *(optional)*

Me and one of my pod mates realized their was an issue where the search was returning 3,600 results 
instead of 60. We looked through the search code together and figured out that the problem was caused by 
flattening the list of 60 results, which created 60 copies of every item. Instead of showing 60 
unique results, the app ended up showing 3,600 results.

---

## Rubric

*You don't need to self-assess — this is here so you know what I'm looking at.*

| Section | Points | Full Credit | Half Credit | No Credit |
|:---|:---:|:---|:---|:---|
| **Reflection** | 10 | Specific, honest responses to "More Deeply" and "Still Confused" sections. Shows genuine thinking — not just "I learned X." | Responses are present but vague or generic ("I got better at Compose"). | Missing or one-word answers. |
| **Code Review** | 10 | Specific observation about the code with explanation of why it matters (or a substantive positive comment). Link to review present and verified. | A question or comment that shows you read the code, but lacks explanation. | "Looks good!" or equivalent. Missing link. Review not found on GitHub. |
| **Total** | **20** | | | |

**A note on the code review score:** I check that the review actually exists on GitHub before grading. The written summary here and the GitHub comment should match. If the review isn't there, the written summary can't earn credit.
