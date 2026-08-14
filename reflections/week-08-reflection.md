# Week {{08}} Reflection

**Name:** Issa Ali
**Date:** 7/9/2026

---

## Commits This Week

https://github.com/Issa-Ismail-Ali/media-tracker-android/pull/8

---

## Code Review

<!-- Every week you leave a review on a pod mate's pull request. Fill in both parts below.
     Part 1 is the link — I will verify the review exists on GitHub.
     Part 2 is your written assessment — what you actually looked at and what you found. -->

**Reviewed:** Kenna Port
**Link to my review:**
https://github.com/Zabzar22/media-tracker-android/pull/10#issuecomment-5065135936
### What I Looked At
I looked at my pod mate's networking code for the Media Detail feature. I focused on how they 
implemented the getMedia(id: Int) function to retrieve a media item from the API. I also looked at 
how they handled different API responses, including successful requests, 404 errors, and other 
server errors. Finally, I looked at the errorMessage() helper function that reads the error message 
from the server's response body.


### What I Noticed

One thing I noticed was that they handled a 404 response differently from other errors by throwing 
a MediaNotFoundException. This allows the app to display a more specific message when a media item 
does not exist. I also noticed that they created an errorMessage() helper function instead of 
repeating the same error-handling code multiple times. That stood out to me because it makes the 
code cleaner and easier to maintain.

### Comments I Left

I left a comment saying that I liked how they separated the 404 "Media not found" case from other 
API errors because it makes it easier for the UI to show the correct message to the user. I also 
commented that using the errorMessage() helper function was a good idea because it avoids 
duplicating code and keeps the repository easier to read.

---

## One Thing I Understood More Deeply

This week I understood more deeply how a ViewModel manages the different states of a screen while
waiting for data from an API. Before this assignment, I knew that a ViewModel stored data, but I did
not fully understand how it switches between loading, success, and error states. While working on
the Media Detail screen, I saw how the ViewModel requests data from the API, updates the UI state,
and allows the Retry button to reload the request if something goes wrong. Seeing everything work
together helped me understand why keeping the UI state inside the ViewModel makes the screen easier
to manage.

---

## One Thing I'm Still Confused About

One thing I am still confused about is how exceptions like HttpException and custom exceptions such 
as MediaNotFoundException are passed through the application and eventually displayed on the screen.
I understand why they are thrown, but I would like more practice following the flow from the 
repository to the ViewModel and then to the UI so I can better understand how errors are handled 
from start to finish.

---

## Anything Else *(optional)*



---

## Rubric

*You don't need to self-assess — this is here so you know what I'm looking at.*

| Section | Points | Full Credit | Half Credit | No Credit |
|:---|:---:|:---|:---|:---|
| **Reflection** | 10 | Specific, honest responses to "More Deeply" and "Still Confused" sections. Shows genuine thinking — not just "I learned X." | Responses are present but vague or generic ("I got better at Compose"). | Missing or one-word answers. |
| **Code Review** | 10 | Specific observation about the code with explanation of why it matters (or a substantive positive comment). Link to review present and verified. | A question or comment that shows you read the code, but lacks explanation. | "Looks good!" or equivalent. Missing link. Review not found on GitHub. |
| **Total** | **20** | | | |

**A note on the code review score:** I check that the review actually exists on GitHub before grading. The written summary here and the GitHub comment should match. If the review isn't there, the written summary can't earn credit.
