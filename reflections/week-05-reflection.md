# Week {{N}} Reflection

**Name:** Issa Ali
**Date:** 6/18/2026
`
---

## Commits This Week

https://github.com/Issa-Ismail-Ali/media-tracker-android/pull/5
**Link:**

---

## Code Review

<!-- Every week you leave a review on a pod mate's pull request. Fill in both parts below.
     Part 1 is the link — I will verify the review exists on GitHub.
     Part 2 is your written assessment — what you actually looked at and what you found. -->

**Reviewed:** *(pod mate's name)* Kenan Port
**Link to my review:**
https://github.com/Zabzar22/media-tracker-android/pull/5#issuecomment-4748101721
### What I Looked At

I looked at my pod mate's code for the create account API call and the login user API call. 
I focused on how the application sends user information to the API when creating a new account 
and how the login process verifies a user's credentials.

### What I Noticed

I noticed that the create account and login features use multiple files working together. 
The interface collects the user's information, the ViewModel handles the data, and the repository 
communicates with the API. I also noticed that the code looked similar to my own which made it 
easier for me to follow and understand what was happening.

### Comments I Left

I commented that the create account and login features look to be working correctly. The code 
appears to be set up properly for users to create an account and log in.

---

## One Thing I Understood More Deeply

One thing I understood better was how API calls work. Before looking at the code, I knew that the 
app had to send information somewhere but I wasn't sure how it happened. Looking at the code helped
me see how the user's information gets taken from the interface and sent to the API when creating an
account or logging in.

---

## One Thing I'm Still Confused About

One thing I was confused about while looking at the code was that the API was in its own repository 
instead of being directly inside the MediaTracker Android project. At first, I expected all of 
the code for creating an account and logging in a user to be located in the same project. 
It took me a little while to understand that the Android app and the API are separate parts 
that communicate with each other. I understand that the Android application sends requests to the 
API and the API handles the data and sends information back, but I am still learning why they are 
kept in separate repositories and how they work together behind the scenes. Looking at the code 
helped me understand the overall process better, but I still have questions about how the connection
between the app and the API is set up.

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
