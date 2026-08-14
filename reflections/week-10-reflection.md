# Week {{10}} Reflection

**Name:** Issa Ali
**Date:** 7/23/2026

---

## Commits This Week

https://github.com/Issa-Ismail-Ali/media-tracker-android/pull/9

**Link:**

---

## Code Review

<!-- Every week you leave a review on a pod mate's pull request. Fill in both parts below.
     Part 1 is the link — I will verify the review exists on GitHub.
     Part 2 is your written assessment — what you actually looked at and what you found. -->

**Reviewed:** *(pod mate's name)* Kenan Port 
**Link to my review:** https://github.com/Zabzar22/media-tracker-android/pull/11#issuecomment-5209481588

### What I Looked At

This week I spent most of my time looking at how to connect the Media Detail screen and the Library 
screen to the API instead of using fake data. I looked through the MediaDetailViewModel, 
LibraryViewModel, DefaultMediaRepository, and MediaApiService to understand how everything works 
together. I also looked at how the GET, POST, PUT, and DELETE requests are used to load media 
details, add items to the library, save favorites, update the library status, and remove items 
from the library. I also spent time comparing my code to my professor's code so I could understand 
the overall structure while still keeping my project organized in my own way. Looking through each 
file helped me understand how the app communicates with the API.

### What I Noticed

One thing I noticed this week is that most of the logic is handled inside the repository instead of 
directly inside the ViewModel. Once I moved my API calls into DefaultMediaRepository, my ViewModels 
became much cleaner and easier to read. I also noticed that not every error should be treated the 
same. For example, if GET /favorites/{mediaId} or GET /library/{mediaId} returns a 404, it doesn't 
actually mean something went wrong. It just means that the media has not been added yet. I also 
noticed how optimistic updates improve the user experience because the interface updates immediately
while the network request finishes in the background.

### Comments I Left

While looking through the pull request, I left comments about how the Media Detail screen was now 
using real API calls instead of fake data. I pointed out that checking the Library and Favorites 
separately makes sense because an item can be in one without being in the other. I also commented 
on how returning 404 for GET /library/{mediaId} and GET /favorites/{mediaId} should not be treated 
as an error since it only means the item has not been added yet. Another thing I noticed was that 
moving the API requests into DefaultMediaRepository makes the ViewModels much cleaner and easier to
understand. I also mentioned that handling duplicate favorites correctly prevents the app from 
crashing and gives the user a smoother experience.

---

## One Thing I Understood More Deeply

his week I gained a much better understanding of how repositories work in Android development. 
Before this assignment, I was making most of my network requests directly inside the ViewModel, 
but now I understand why it is better to move that code into a repository. It keeps the project 
more organized and makes the ViewModel much easier to read. I also understand optimistic updates 
much better now. Instead of waiting for the server to respond before updating the screen, the app 
updates the interface first and only rolls the changes back if the network request fails. Seeing 
that pattern in my own project made it much easier to understand than just reading about it in class.

---

## One Thing I'm Still Confused About

One thing I am still a little confused about is writing unit tests with MockK. I understand the idea
of mocking the repository and simulating failed network calls, but I still need more practice with 
setting up the test environment and verifying that the rollback logic restores the UI correctly 
after an exception is thrown. I think writing a few more tests will help me become more comfortable 
with it.

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
